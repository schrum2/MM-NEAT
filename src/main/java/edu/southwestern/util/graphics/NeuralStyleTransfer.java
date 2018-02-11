package edu.southwestern.util.graphics;

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomNumbers;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
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
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.indexing.functions.Value;
import org.nd4j.linalg.ops.transforms.Transforms;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * My attempt to implement the neural style transfer algorithm in DL4J:
 * https://arxiv.org/pdf/1508.06576.pdf
 * https://arxiv.org/pdf/1603.08155.pdf
 * https://harishnarayanan.org/writing/artistic-style-transfer/
 *
 * @author Jacob Schrum
 */
public class NeuralStyleTransfer {

    /**
     * Image conversion/size properties
     */
    public static final int HEIGHT = 224;
    public static final int WIDTH = 224;
    public static final int CHANNELS = 3;
    public static final int IMAGE_SIZE = HEIGHT * WIDTH;//                "input_1",
    private static final String[] LOWER_LAYERS = new String[]{
//                "input_1",
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
//            "block1_conv1,0.1",
//            "block2_conv1,0.3",
//            "block3_conv1,0.2",
            "block4_conv2,1.0",
//            "block5_conv1,1"
    };


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
    public static double alpha = 0.025;
    public static double beta = 5.0;
    public static double total_variation_weight = 1.0;
    private static final double NOISE_RATION = 0.1;

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

        int iterations = 1000;
        double learningRate = 0.00001;

        vgg16FineTune.output(content);
        Map<String, INDArray> activationsContent = vgg16FineTune.feedForward();

        vgg16FineTune.output(style);
        Map<String, INDArray> activationsStyle = vgg16FineTune.feedForward();

        String contentLayerName = LOWER_LAYERS[LOWER_LAYERS.length - 1];
        for (int itr = 0; itr < iterations; itr++) {
            System.out.println("itr = " + itr);
            vgg16FineTune.output(combination);
            Map<String, INDArray> activations = vgg16FineTune.feedForward();


            INDArray layerFeatures = activations.get(contentLayerName);
            INDArray layerFeaturesContent = activationsContent.get(contentLayerName);
            INDArray styleBackProb = Nd4j.zeros(combination.shape());
            Map<String, INDArray> styleMap = new HashMap<>();
            Map<String, INDArray> combMap = new HashMap<>();
            for (String styleLayer : STYLE_LAYERS) {
                String[] split = styleLayer.split(",");
                String styleLayerName = split[0];
                INDArray styleValues = activationsStyle.get(styleLayerName);
                INDArray combValues = activations.get(styleLayerName);
                double weight = Double.parseDouble(split[1]);
                int index = findLayerIndex(styleLayerName);
                INDArray dStyleValues = derivativeLossStyleInLayer(styleValues, combValues).transpose();
                styleBackProb = styleBackProb.add(backPropagate(vgg16FineTune, dStyleValues.mul(beta).mul(weight).reshape(styleValues.shape()), index));
                styleMap.put(styleLayerName, styleValues.dup());
                combMap.put(styleLayerName, combValues.dup());
            }

            int[] newShape = layerFeatures.shape();
            INDArray dLcontent_currLayer = flatten(derivativeLossContentInLayer(layerFeaturesContent, layerFeatures));
            INDArray backPropContent = backPropagate(vgg16FineTune, dLcontent_currLayer.reshape(newShape).mul(alpha), findLayerIndex(contentLayerName));

            combination = combination.sub(backPropContent.add(styleBackProb).mul(learningRate));
            System.out.println("Total Loss >> " + totalLoss(styleMap, combMap, layerFeatures.dup(), layerFeaturesContent.dup()));

            if (itr % 5 == 0 && itr != 0) {
                saveImage(scaler, combination.dup(), itr);
            }
        }
        BufferedImage output = saveImage(scaler, combination, iterations);
        DrawingPanel panel = GraphicsUtil.drawImage(output, "Combined Image", WIDTH, HEIGHT);
        MiscUtil.waitForReadStringAndEnterKeyPress();
    }

    private static int findLayerIndex(String styleLayerName) {
        int index = 0;
        for (int i = 0; i < ALL_LAYERS.length; i++) {
            if (styleLayerName.equalsIgnoreCase(ALL_LAYERS[i])) {
                index = i;
                break;
            }
        }
        return index;
    }


    public static double contentLoss(INDArray combFeatures, INDArray contentFeatures) {
        return sumOfSquaredErrors(contentFeatures, combFeatures) / (4.0 * (CHANNELS) * (WIDTH) * (HEIGHT));
    }

    public static double styleLoss(INDArray styleFeatures, INDArray comboFeatures) {

        return style_loss_for_one_layer(styleFeatures, comboFeatures);
    }

    public static double totalLoss(Map<String, INDArray> styleMap, Map<String, INDArray> comboMap, INDArray comboFeatures, INDArray contentFeatures) {
        Double styles = 0.0;
        for (String styleLayers : STYLE_LAYERS) {
            String[] split = styleLayers.split(",");
            String styleLayerName = split[0];
            double weight = Double.parseDouble(split[1]);
            styles += styleLoss(styleMap.get(styleLayerName).mul(weight), comboMap.get(styleLayerName));
        }
        return alpha * contentLoss(comboFeatures, contentFeatures) + beta * styles;
    }

    private static INDArray backPropagate(ComputationGraph vgg16FineTune, INDArray dLdANext, int startFrom) {
        ;
        for (int i = startFrom; i > 0; i--) {
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
     * Equation (2) from the Gatys et all paper: https://arxiv.org/pdf/1508.06576.pdf
     * This is the derivative of the content loss w.r.t. the combo image features
     * within a specific layer of the CNN.
     *
     * @param originalFeatures Features at particular layer from the original content image
     * @param comboFeatures    Features at same layer from current combo image
     * @return Derivatives of content loss w.r.t. combo features
     */
    public static INDArray derivativeLossContentInLayer(INDArray originalFeatures, INDArray comboFeatures) {

        comboFeatures = comboFeatures.dup();
        originalFeatures = originalFeatures.dup();

        double channels = comboFeatures.shape()[0];
        assert comboFeatures.shape()[1] == comboFeatures.shape()[2] : "Images and features must have square shapes";
        double w = comboFeatures.shape()[1];
        double h = comboFeatures.shape()[2];

        double contentWeight = 1.0 / (2 * (channels) * (w) * (h));
        // Compute the F^l - P^l portion of equation (2), where F^l = comboFeatures and P^l = originalFeatures
        INDArray diff = comboFeatures.sub(originalFeatures);
        // This multiplication assures that the result is 0 when the value from F^l < 0, but is still F^l - P^l otherwise
        return diff.mul(contentWeight).mul(ensurePositive(comboFeatures));
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
        double channels = comboFeatures.shape()[0];
        assert comboFeatures.shape()[1] == comboFeatures.shape()[2] : "Images and features must have square shapes";
        double size = comboFeatures.shape()[1];
        double size2 = comboFeatures.shape()[2];

        double styleWeight = 1.0 / ((channels * channels) * (size * size) * (size2 * size2));
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

    private static INDArray ensurePositive(INDArray comboFeatures) {
        BooleanIndexing.applyWhere(comboFeatures, Conditions.lessThan(0.0f), new Value(0.0f));
        BooleanIndexing.applyWhere(comboFeatures, Conditions.greaterThan(0.0f), new Value(1.0f));
        return comboFeatures;
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

    private static BufferedImage saveImage(DataNormalization scaler, INDArray combination, int iterations) throws IOException {
        scaler.revertFeatures(combination);
        // Show final image afterward
        BufferedImage output = GraphicsUtil.imageFromINDArray(combination);
        ImageIO.write(output, "jpg", new File("data/iteration" + iterations + ".jpg"));
        return output;
    }
}
