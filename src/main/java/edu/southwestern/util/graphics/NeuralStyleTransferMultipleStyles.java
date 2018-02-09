package edu.southwestern.util.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.indexing.functions.Value;
import org.nd4j.linalg.ops.transforms.Transforms;

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomNumbers;

import javax.imageio.ImageIO;

/**
 * My attempt to implement the neural style transfer algorithm in DL4J:
 * https://arxiv.org/pdf/1508.06576.pdf
 * https://arxiv.org/pdf/1603.08155.pdf
 * https://harishnarayanan.org/writing/artistic-style-transfer/
 *
 * @author Jacob Schrum
 */
public class NeuralStyleTransferMultipleStyles {

    /**
     * Image conversion/size properties
     */
    public static final int HEIGHT = 224;
    public static final int WIDTH = 224;
    public static final int CHANNELS = 3;
    public static final int IMAGE_SIZE = HEIGHT * WIDTH;//                "input_1",
    private static final String[] CONTENT_LAYERS = new String[]{
            "block1_conv1",
            "block1_conv2",
            "block1_pool",
            "block2_conv1",
            "block2_conv2",
            "block2_pool",
            "block3_conv1",
            "block3_conv2",
            "block3_conv3",
            "block3_pool",
            "block4_conv1",
            "block4_conv2"
    };

    private static final String[] STYLE_LAYERS = new String[]{
            "block1_conv1,0.5",
            "block2_conv1,1",
            "block3_conv1,1.5",
            "block4_conv1,3",
            "block5_conv1,4"
    };

//        private static final String[] STYLE_LAYERS = new String[]{
//            "block4_conv2,1"
//    };

    private static final String[] ALL_LAYERS = new String[]{
            "input_1",
            "block1_conv1",
            "block1_conv2",
            "block1_pool",
            "block2_conv1",
            "block2_conv2",
            "block2_pool",
            "block3_conv1",
            "block3_conv2",
            "block3_conv3",
            "block3_pool",
            "block4_conv1",
            "block4_conv2",
            "block4_conv3",
            "block4_pool",
            "block5_conv1",
            "block5_conv2",
            "block5_conv3",
            "block5_pool",
            "flatten",
            "fc1",
            "fc2"
    };

    /**
     * Values suggested by
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     * <p>
     * Will likely change this, or make them parameters.
     */
    public static double content_weight = 0.025;
    public static double style_weight = 5.0;
    public static double total_variation_weight = 1.0;
    private static final double NOISE_RATION = 0.4;

    public static void main(String[] args) throws IOException {
        ComputationGraph vgg16FineTune = loadModel();
        NativeImageLoader loader = new NativeImageLoader(HEIGHT, WIDTH, CHANNELS);
        DataNormalization scaler = new VGG16ImagePreProcessor();

        String contentFile = "data/imagematch/content2.jpg";
        INDArray content = loader.asMatrix(new File(contentFile));
        INDArray dupContent = content.dup();
        scaler.transform(content);

        String styleFile = "data/imagematch/style2.jpg";
        INDArray style = loader.asMatrix(new File(styleFile));
        scaler.transform(style);

        // Starting combination image is pure white noise
        int totalEntries = CHANNELS * HEIGHT * WIDTH;
        int[] upper = new int[totalEntries];
        Arrays.fill(upper, 256);
        INDArray combination = Nd4j.create(ArrayUtil.doubleArrayFromIntegerArray(RandomNumbers.randomIntArray(upper)), new int[]{1, CHANNELS, HEIGHT, WIDTH});
        combination = combination.mul(NOISE_RATION).add(dupContent.mul(1 - NOISE_RATION));
        scaler.transform(combination);
        int iterations = 500;
        double learningRate = 0.000001;

        vgg16FineTune.output(content);
        Map<String, INDArray> activationsContent = vgg16FineTune.feedForward();


        vgg16FineTune.output(style);
        Map<String, INDArray> activationsStyle = vgg16FineTune.feedForward();
        HashMap<String, INDArray> styleMap = createWeightMap(activationsStyle);


        String layer = CONTENT_LAYERS[CONTENT_LAYERS.length - 1];
        for (int itr = 0; itr < iterations; itr++) {
            System.out.println("Iteration: " + itr);
            vgg16FineTune.output(combination);
            Map<String, INDArray> activations = vgg16FineTune.feedForward();

            INDArray layerFeatures = activations.get(layer);
            INDArray layerFeaturesContent = activationsContent.get(layer);
            INDArray layerFeaturesStyle = activationsStyle.get(layer);
            HashMap<String, INDArray> comboMap = createWeightMap(activations);
            INDArray dContent = derivativeLossContentInLayer(layerFeaturesContent, layerFeatures, vgg16FineTune);
            INDArray dStyle = derivativeLossStyle(styleMap, comboMap, vgg16FineTune);


            INDArray dLdANext = Nd4j.zeros(new int[]{1, CHANNELS, WIDTH, HEIGHT});
            dLdANext = dLdANext.add(dContent).add(dStyle);
            combination = combination.sub(dLdANext.mul(learningRate));

            log(dLdANext, layerFeaturesContent, layerFeaturesStyle, layerFeatures, dContent, dStyle);
            System.out.println("Result pixels.... = " + combination.sumNumber());

            if (itr % 5 == 0) {
                saveImage(scaler, combination.dup(), itr);
            }
        }
        BufferedImage output = saveImage(scaler, combination, iterations);
        DrawingPanel panel = GraphicsUtil.drawImage(output, "Combined Image", WIDTH, HEIGHT);
        MiscUtil.waitForReadStringAndEnterKeyPress();
    }

    private static HashMap<String, INDArray> createWeightMap(Map<String, INDArray> activationsStyle) {
        HashMap<String, INDArray> styleMap = new HashMap<>();
        for (String styleLayer : STYLE_LAYERS) {
            String[] split = styleLayer.split(",");
            String styleLayerName = split[0];
            styleMap.put(styleLayerName, activationsStyle.get(styleLayerName));
        }
        return styleMap;
    }

    private static INDArray backPropagate(ComputationGraph vgg16FineTune, INDArray dLdANext) {
        for (int i = CONTENT_LAYERS.length - 1; i >= 0; i--) {

            System.out.println("lowerLayers = " + CONTENT_LAYERS[i]);

            Layer layer = vgg16FineTune.getLayer(CONTENT_LAYERS[i]);
            dLdANext = layer.backpropGradient(dLdANext).getSecond();
            System.out.println("dLdANext.shapeInfoToString()  - " + CONTENT_LAYERS[i] + " >>  " + dLdANext.shapeInfoToString());
        }

        return dLdANext;
    }

    private static INDArray backPropagate(ComputationGraph vgg16FineTune, String layerName, INDArray dLdANext) {
        int startFrom = vgg16FineTune.getLayer(layerName).getIndex();
        System.out.println("Style Back prop from layer name " + layerName);
        for (int i = startFrom; i > 0; i--) {
            System.out.println("Style layer back " + i);
            Layer layer = vgg16FineTune.getLayer(ALL_LAYERS[i]);
            dLdANext = layer.backpropGradient(dLdANext).getSecond();
        }
        return dLdANext;
    }

    /**
     * Element-wise differences are squared, and then summed.
     * This is modelled after the content_loss method defined in
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     *
     * @param a One tensor
     * @param b Another tensor
     * @return Sum of squared errors: scalar
     */
    public static double sumOfSquaredErrors(INDArray a, INDArray b) {
        INDArray diff = a.sub(b); // difference
        INDArray squares = Transforms.pow(diff, 2); // element-wise squaring
        return squares.sumNumber().doubleValue();
    }

    /**
     * After passing in the content, style, and combination images,
     * compute the loss with respect to the content. Based off of:
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     *
     * @param activations Intermediate layer activations from the three inputs
     * @return Weighted content loss component
     */
    public static double content_loss(Map<String, INDArray> activations) {
        INDArray block2_conv2_features = activations.get("block2_conv2");
        INDArray content_features = block2_conv2_features.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
        INDArray combination_features = block2_conv2_features.get(NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
        return content_weight * sumOfSquaredErrors(content_features, combination_features);
    }

    /**
     * Equation (2) from the Gatys et all paper: https://arxiv.org/pdf/1508.06576.pdf
     * This is the derivative of the content loss w.r.t. the combo image features
     * within a specific layer of the CNN.
     *
     * @param originalFeatures Features at particular layer from the original content image
     * @param comboFeatures    Features at same layer from current combo image
     * @param vgg16
     * @return Derivatives of content loss w.r.t. combo features
     */
    public static INDArray derivativeLossContentInLayer(INDArray originalFeatures, INDArray comboFeatures, ComputationGraph vgg16) {
        comboFeatures = comboFeatures.dup();
        originalFeatures = originalFeatures.dup();

        double N = comboFeatures.shape()[0];
        double M = comboFeatures.shape()[1] * comboFeatures.shape()[2];

        double contentWeight = 1.0 / (2 * (N * M));

        // Compute the F^l - P^l portion of equation (2), where F^l = comboFeatures and P^l = originalFeatures
        INDArray diff = comboFeatures.sub(originalFeatures).mul(contentWeight);
        // This multiplication assures that the result is 0 when the value from F^l < 0, but is still F^l - P^l otherwise
        return backPropagate(vgg16, diff.mul(ensurePositive(comboFeatures)));
    }

    /**
     * Computing the Gram matrix as described here:
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     * Permuting dimensions is not needed because DL4J stores
     * the channel at the front rather than the end of the tensor.
     * Basically, each tensor is flattened into a vector so that
     * the dot product can be calculated.
     *
     * @param x Tensor to get Gram matrix of
     * @return Resulting Gram matrix
     */
    public static INDArray gram_matrix(INDArray x) {
        INDArray flattened = flatten(x);
        // mmul is dot product/outer product
        INDArray gram = flattened.mmul(flattened.transpose()); // Is the dup necessary?
        return gram;
    }

    private static INDArray flatten(INDArray x) {
        int[] shape = x.shape();
        return x.reshape(shape[0] * shape[1], shape[2] * shape[3]);
    }

    /**
     * This method is simply called style_loss in
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     * but it takes inputs for intermediate activations from a particular
     * layer, hence my re-name. These values contribute to the total
     * style loss.
     *
     * @param style       Activations from intermediate layer of CNN for style image input
     * @param combination Activations from intermediate layer of CNN for combination image input
     * @return Loss contribution from this comparison
     */
    public static double style_loss_for_one_layer(INDArray style, INDArray combination) {
        INDArray s = gram_matrix(style);
        INDArray c = gram_matrix(combination);
        return sumOfSquaredErrors(s, c) / (4.0 * (CHANNELS * CHANNELS) * (IMAGE_SIZE * IMAGE_SIZE));
    }

    /**
     * The overall style loss calculation shown in
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     * for every relevant intermediate layer of the CNN.
     *
     * @param activations Intermediate activations of all CNN layers
     * @return weighted style loss component
     */
    public static double style_loss(Map<String, INDArray> activations) {
        String[] feature_layers = new String[]{
                "block1_conv2", "block2_conv2",
                "block3_conv3", "block4_conv3",
                "block5_conv3"};
        double loss = 0.0;
        for (String layer_name : feature_layers) {
            INDArray layer_features = activations.get(layer_name);
            INDArray style_features = layer_features.get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
            INDArray combination_features = layer_features.get(NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
            double sl = style_loss_for_one_layer(style_features, combination_features);
            loss += (style_weight / feature_layers.length) * sl;
        }
        return loss;
    }

    /**
     * Equation (6) from the Gatys et all paper: https://arxiv.org/pdf/1508.06576.pdf
     * This is the derivative of the style error for a single layer w.r.t. the
     * combo image features at that layer.
     *
     * @param styleFeatures Intermediate activations of one layer for style input
     * @param comboFeatures Intermediate activations of one layer for combo image input
     * @return Derivative of style error matrix for the layer w.r.t. combo image
     */
    public static INDArray derivativeLossStyleInLayer(INDArray styleFeatures, INDArray comboFeatures) {
        // Create tensor of 0 and 1 indicating whether values in comboFeatures are positive or negative
        comboFeatures = comboFeatures.dup();
        styleFeatures = styleFeatures.dup();


        double N = comboFeatures.shape()[0];
        double M = comboFeatures.shape()[1] * comboFeatures.shape()[2];


        double styleWeight = 1.0 / ((N * N) * (M * M));
        // Corresponds to A^l in equation (6)
        INDArray a = gram_matrix(styleFeatures); // Should this actually be the content image?
        // Corresponds to G^l in equation (6)
        INDArray g = gram_matrix(comboFeatures);
        // G^l - A^l
        INDArray diff = g.sub(a);
        // (F^l)^T * (G^l - A^l)
        INDArray trans = flatten(comboFeatures).transpose();
        INDArray product = trans.mmul(diff);
        // (1/(N^2 * M^2)) * ((F^l)^T * (G^l - A^l))
        INDArray posResult = product.mul(styleWeight);
        // This multiplication assures that the result is 0 when the value from F^l < 0, but is still (1/(N^2 * M^2)) * ((F^l)^T * (G^l - A^l)) otherwise
        return posResult.mul(ensurePositive(trans));
    }


    public static INDArray derivativeLossStyle(HashMap<String, INDArray> styleMap,
                                               HashMap<String, INDArray> comboMap,
                                               ComputationGraph vgg16) {

        INDArray dlNext = Nd4j.zeros(new int[]{1, CHANNELS, WIDTH, HEIGHT});
        // Create tensor of 0 and 1 indicating whether values in comboFeatures are positive or negative
        for (String styleLayer : STYLE_LAYERS) {
            String[] split = styleLayer.split(",");
            double styleWight = Double.parseDouble(split[1]);
            String styleLayerName = split[0];
            INDArray combo = comboMap.get(styleLayerName);
            INDArray style = styleMap.get(styleLayerName);
            INDArray dStyle = derivativeLossStyleInLayer(style, combo).mul(styleWight);
            dStyle = dStyle.reshape(combo.shape());
            dlNext = dlNext.add(backPropagate(vgg16, styleLayerName, dStyle));
        }
        return dlNext;
    }


    private static INDArray ensurePositive(INDArray comboFeatures) {
        BooleanIndexing.applyWhere(comboFeatures, Conditions.lessThan(0.0f), new Value(0.0f));
        BooleanIndexing.applyWhere(comboFeatures, Conditions.greaterThan(0.0f), new Value(1.0f));
        return comboFeatures;
    }

    /**
     * Returns weighted total variation loss that smooths the error across the image.
     * Based on https://harishnarayanan.org/writing/artistic-style-transfer/ again.
     * I'm not sure what the point of leaving out certain edges is in the tensor bounds,
     * but I expect the point is to reduce the variance between directly adjacent pixels.
     *
     * @param combination Combination image
     * @return Weighted total variation loss across combo image
     */
    public static double total_variation_loss(INDArray combination) {
        INDArray sliceA1 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, HEIGHT - 1), NDArrayIndex.interval(0, WIDTH - 1));
        INDArray sliceA2 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(1, HEIGHT), NDArrayIndex.interval(0, WIDTH - 1));
        INDArray diffA = sliceA1.sub(sliceA2);
        INDArray a = Transforms.pow(diffA, 2); // element-wise squaring

        INDArray sliceB1 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, HEIGHT - 1), NDArrayIndex.interval(0, WIDTH - 1));
        INDArray sliceB2 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, HEIGHT - 1), NDArrayIndex.interval(1, WIDTH));
        INDArray diffB = sliceB1.sub(sliceB2);
        INDArray b = Transforms.pow(diffB, 2); // element-wise squaring

        INDArray add = a.add(b);
        INDArray pow = Transforms.pow(add, 1.25);
        return total_variation_weight * pow.sumNumber().doubleValue();
    }

    /**
     * Computes complete loss function for the neural style transfer
     * optimization problem by adding up all of the components.
     *
     * @param activations Activations from the content, style, and combination images (TODO: cache those that don't change)
     * @param combination Combined image (so far)
     * @return Loss value
     */
    public static double neuralStyleLoss(Map<String, INDArray> activations, INDArray combination) {
        double loss = 0;
        loss += content_loss(activations);
        loss += style_loss(activations);
        loss += total_variation_loss(combination);
        return loss;
    }

    private static ComputationGraph loadModel() throws IOException {
        ZooModel zooModel = new VGG16();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);

        FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
                .learningRate(5e-7)
                .optimizationAlgo(OptimizationAlgorithm.LBFGS)
                .updater(Updater.ADAM)
                .seed(1234)
                .build();

        ComputationGraph vgg16FineTune = new TransferLearning.GraphBuilder(vgg16)
                .fineTuneConfiguration(fineTuneConf)
                .build();

        vgg16FineTune.initGradientsView();
        System.out.println(vgg16FineTune.summary());
        return vgg16FineTune;
    }

    private static void log(INDArray dLdANext, INDArray content_features, INDArray style_features, INDArray combination_features, INDArray dLcontent_currLayer, INDArray dLstyle_currLayer) {
        System.out.println("content " + content_features.shapeInfoToString());
        System.out.println("style " + style_features.shapeInfoToString());
        System.out.println("combination " + combination_features.shapeInfoToString());

        System.out.println("dlContent " + dLcontent_currLayer.shapeInfoToString());
        System.out.println("dlStyle " + dLstyle_currLayer.shapeInfoToString());
        System.out.println("dLdANext " + dLdANext.shapeInfoToString());
    }

    private static BufferedImage saveImage(DataNormalization scaler, INDArray combination, int iterations) throws IOException {
        scaler.revertFeatures(combination);
        // Show final image afterward
        BufferedImage output = GraphicsUtil.imageFromINDArray(combination);
        ImageIO.write(output, "jpg", new File("data/iteration" + iterations + ".jpg"));
        return output;
    }
}
