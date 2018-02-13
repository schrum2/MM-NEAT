package edu.southwestern.util.graphics;

import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomNumbers;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
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
import org.nd4j.linalg.learning.AdamUpdater;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.ops.transforms.Transforms;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Neural Style Transfer Algorithm in DL4J:
 * https://arxiv.org/pdf/1508.06576.pdf
 * https://arxiv.org/pdf/1603.08155.pdf
 * https://harishnarayanan.org/writing/artistic-style-transfer/
 *
 * @author Jacob Schrum & Klevis Ramo
 */
public class NeuralStyleTransfer {


    public static final int HEIGHT = 224;
    public static final int WIDTH = 224;
    public static final int CHANNELS = 3;
    public static final int IMAGE_SIZE = HEIGHT * WIDTH;
    /**
     * Values suggested by
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     */
    public static final double ALPHA = 0.025;
    public static final double BETA = 5.0;
    private static final String[] STYLE_LAYERS = new String[]{
            "block1_conv1,0.2",
            "block2_conv1,0.2",
            "block3_conv1,0.2",
            "block4_conv2,0.2",
            "block5_conv1,0.2"
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
    private static final String CONTENT_LAYER_NAME = "block4_conv2";
    private static final double BETA_MOMENTUM = 0.8;
    private static final double BETA2_MOMENTUM = 0.999;
    private static final double EPSILON = 0.00000008;
    private static final double LEARNING_RATE = 2;

    private static final double NOISE_RATION = 0.1;
    private static final int ITERATIONS = 1000;
    private static final String CONTENT_FILE = "data/imagematch/content2.jpg";
    private static final String STYLE_FILE = "data/imagematch/style2.jpg";

    public static void main(String[] args) throws IOException {
        ComputationGraph vgg16FineTune = loadModel();
        NativeImageLoader loader = new NativeImageLoader(HEIGHT, WIDTH, CHANNELS);
        DataNormalization imagePreProcessor = new VGG16ImagePreProcessor();

        INDArray content = loadImage(loader, imagePreProcessor, CONTENT_FILE);

        INDArray style = loadImage(loader, imagePreProcessor, STYLE_FILE);

        INDArray combination = createCombinationImage(imagePreProcessor, content);

        Map<String, INDArray> activationsContentMap = vgg16FineTune.feedForward(content, true);

        Map<String, INDArray> activationsStyleMap = vgg16FineTune.feedForward(style, true);
        HashMap<String, INDArray> activationsStyleGramMap = buildStyleGramValues(activationsStyleMap);

        AdamUpdater adamUpdater = createADAMUpdater();
        for (int itr = 0; itr < ITERATIONS; itr++) {
            System.out.println("itr = " + itr);
            Map<String, INDArray> activationsCombMap = vgg16FineTune.feedForward(combination, true);

            INDArray styleBackProb = backPropagateStyles(vgg16FineTune, activationsStyleGramMap, activationsCombMap);

            INDArray backPropContent = backPropagateContent(vgg16FineTune, activationsContentMap, activationsCombMap);

            INDArray backPropAllValues = backPropContent.muli(ALPHA).addi(styleBackProb.muli(BETA));

            adamUpdater.applyUpdater(backPropAllValues, itr);
            combination.subi(backPropAllValues);

            System.out.println("Total Loss >> " + totalLoss(activationsStyleMap, activationsCombMap, activationsContentMap));
            if (itr % 5 == 0 && itr != 0) {
                saveImage(imagePreProcessor, combination.dup(), itr);
            }
        }

    }

    private static INDArray backPropagateStyles(ComputationGraph vgg16FineTune, HashMap<String, INDArray> activationsStyleGramMap, Map<String, INDArray> activationsCombMap) {
        INDArray styleBackProb = Nd4j.zeros(new int[]{1, CHANNELS, WIDTH, HEIGHT});
        for (String styleLayer : STYLE_LAYERS) {
            String[] split = styleLayer.split(",");
            String styleLayerName = split[0];
            INDArray styleGramValues = activationsStyleGramMap.get(styleLayerName);
            INDArray combValues = activationsCombMap.get(styleLayerName);
            double weight = Double.parseDouble(split[1]);
            int index = findLayerIndex(styleLayerName);
            INDArray dStyleValues = derivativeLossStyleInLayer(styleGramValues, combValues).transpose();
            styleBackProb.addi(backPropagate(vgg16FineTune, dStyleValues.reshape(combValues.shape()), index).muli(weight));
        }
        return styleBackProb;
    }

    private static INDArray backPropagateContent(ComputationGraph vgg16FineTune, Map<String, INDArray> activationsContentMap, Map<String, INDArray> activationsCombMap) {
        INDArray activationsContent = activationsContentMap.get(CONTENT_LAYER_NAME);
        INDArray activationsComb = activationsCombMap.get(CONTENT_LAYER_NAME);
        INDArray dContentLayer = derivativeLossContentInLayer(activationsContent, activationsComb);
        return backPropagate(vgg16FineTune, dContentLayer.reshape(activationsComb.shape()), findLayerIndex(CONTENT_LAYER_NAME));
    }

    private static AdamUpdater createADAMUpdater() {
        AdamUpdater adamUpdater = new AdamUpdater(new Adam(LEARNING_RATE, BETA_MOMENTUM, BETA2_MOMENTUM, EPSILON));
        adamUpdater.setStateViewArray(Nd4j.zeros(1, 2 * CHANNELS * WIDTH * HEIGHT),
                new int[]{1, CHANNELS, WIDTH, HEIGHT}, 'c',
                true);
        return adamUpdater;
    }

    private static INDArray createCombinationImage(DataNormalization scaler, INDArray content) {
        int totalEntries = CHANNELS * HEIGHT * WIDTH;
        int[] upper = new int[totalEntries];
        Arrays.fill(upper, 256);
        INDArray combination = Nd4j.create(ArrayUtil.doubleArrayFromIntegerArray(RandomNumbers.randomIntArray(upper)), new int[]{1, CHANNELS, HEIGHT, WIDTH});
        combination.muli(NOISE_RATION).addi(content.mul(1 - NOISE_RATION));
        scaler.transform(combination);
        return combination;
    }

    private static INDArray loadImage(NativeImageLoader loader, DataNormalization scaler, String contentFile) throws IOException {
        INDArray content = loader.asMatrix(new File(contentFile));
        scaler.transform(content);
        return content;
    }

    /**
     * Since style activation are not changing we are saving some computation by calculating style grams only once
     *
     * @param activationsStyle
     * @return
     */
    private static HashMap<String, INDArray> buildStyleGramValues(Map<String, INDArray> activationsStyle) {
        HashMap<String, INDArray> styleGramValuesMap = new HashMap<>();
        for (String styleLayer : STYLE_LAYERS) {
            String[] split = styleLayer.split(",");
            String styleLayerName = split[0];
            INDArray styleValues = activationsStyle.get(styleLayerName);
            styleGramValuesMap.put(styleLayerName, gramMatrix(styleValues));
        }
        return styleGramValuesMap;
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

    public static double totalLoss(Map<String, INDArray> activationsStyleMap, Map<String, INDArray> activationsCombMap, Map<String, INDArray> activationsContentMap) {
        Double stylesLoss = allStyleLayersLoss(activationsStyleMap, activationsCombMap);
        return ALPHA * contentLoss(activationsCombMap.get(CONTENT_LAYER_NAME).dup(), activationsContentMap.get(CONTENT_LAYER_NAME).dup()) + BETA * stylesLoss;
    }

    private static Double allStyleLayersLoss(Map<String, INDArray> activationsStyleMap, Map<String, INDArray> activationsCombMap) {
        Double styles = 0.0;
        for (String styleLayers : STYLE_LAYERS) {
            String[] split = styleLayers.split(",");
            String styleLayerName = split[0];
            double weight = Double.parseDouble(split[1]);
            styles += styleLoss(activationsStyleMap.get(styleLayerName).dup(), activationsCombMap.get(styleLayerName).dup()) * weight;
        }
        return styles;
    }

    /**
     * After passing in the content, style, and combination images,
     * compute the loss with respect to the content. Based off of:
     * https://harishnarayanan.org/writing/artistic-style-transfer/
     *
     * @param combActivations Intermediate layer activations from the three inputs
     * @param contentActivations Intermediate layer activations from the three inputs
     * @return Weighted content loss component
     */

    public static double contentLoss(INDArray combActivations, INDArray contentActivations) {
        return sumOfSquaredErrors(contentActivations, combActivations) / (4.0 * (CHANNELS) * (WIDTH) * (HEIGHT));
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
    public static double styleLoss(INDArray style, INDArray combination) {
        INDArray s = gramMatrix(style);
        INDArray c = gramMatrix(combination);
        return sumOfSquaredErrors(s, c) / (4.0 * (CHANNELS * CHANNELS) * (IMAGE_SIZE * IMAGE_SIZE));
    }

    private static INDArray backPropagate(ComputationGraph vgg16FineTune, INDArray dLdANext, int startFrom) {

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
     * @param contentActivations Features at particular layer from the original content image
     * @param combActivations    Features at same layer from current combo image
     * @return Derivatives of content loss w.r.t. combo features
     */
    public static INDArray derivativeLossContentInLayer(INDArray contentActivations, INDArray combActivations) {

        combActivations = combActivations.dup();
        contentActivations = contentActivations.dup();

        double channels = combActivations.shape()[0];
        double w = combActivations.shape()[1];
        double h = combActivations.shape()[2];

        double contentWeight = 1.0 / (2 * (channels) * (w) * (h));
        // Compute the F^l - P^l portion of equation (2), where F^l = comboFeatures and P^l = originalFeatures
        INDArray diff = combActivations.sub(contentActivations);
        // This multiplication assures that the result is 0 when the value from F^l < 0, but is still F^l - P^l otherwise
        return flatten(diff.muli(contentWeight).muli(ensurePositive(combActivations)));
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
    public static INDArray gramMatrix(INDArray x) {
        INDArray flattened = flatten(x);
        INDArray gram = flattened.mmul(flattened.transpose()); // Is the dup necessary?
        return gram;
    }

    private static INDArray flatten(INDArray x) {
        int[] shape = x.shape();
        return x.reshape(shape[0] * shape[1], shape[2] * shape[3]);
    }


    /**
     * Equation (6) from the Gatys et all paper: https://arxiv.org/pdf/1508.06576.pdf
     * This is the derivative of the style error for a single layer w.r.t. the
     * combo image features at that layer.
     *
     * @param styleGramFeatures Intermediate activations of one layer for style input
     * @param comboFeatures     Intermediate activations of one layer for combo image input
     * @return Derivative of style error matrix for the layer w.r.t. combo image
     */
    public static INDArray derivativeLossStyleInLayer(INDArray styleGramFeatures, INDArray comboFeatures) {

        comboFeatures = comboFeatures.dup();
        double N = comboFeatures.shape()[0];
        double M = comboFeatures.shape()[1] * comboFeatures.shape()[2];

        double styleWeight = 1.0 / ((N * N) * (M * M));
        // Corresponds to G^l in equation (6)
        INDArray contentGram = gramMatrix(comboFeatures);
        // G^l - A^l
        INDArray diff = contentGram.sub(styleGramFeatures);
        // (F^l)^T * (G^l - A^l)
        INDArray trans = flatten(comboFeatures).transpose();
        INDArray product = trans.mmul(diff);
        // (1/(N^2 * M^2)) * ((F^l)^T * (G^l - A^l))
        INDArray posResult = product.muli(styleWeight);
        // This multiplication assures that the result is 0 when the value from F^l < 0, but is still (1/(N^2 * M^2)) * ((F^l)^T * (G^l - A^l)) otherwise
        return posResult.muli(ensurePositive(trans));
    }

    private static INDArray ensurePositive(INDArray comboFeatures) {
        BooleanIndexing.applyWhere(comboFeatures, Conditions.lessThan(0.0f), new Value(0.0f));
        BooleanIndexing.applyWhere(comboFeatures, Conditions.greaterThan(0.0f), new Value(1.0f));
        return comboFeatures;
    }

    private static ComputationGraph loadModel() throws IOException {
        ZooModel zooModel = new VGG16();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        vgg16.initGradientsView();
        System.out.println(vgg16.summary());
        return vgg16;
    }

    private static BufferedImage saveImage(DataNormalization scaler, INDArray combination, int iterations) throws IOException {
        scaler.revertFeatures(combination);
        // Show final image afterward
        BufferedImage output = GraphicsUtil.imageFromINDArray(combination);
        ImageIO.write(output, "jpg", new File("data/iteration" + iterations + ".jpg"));
        return output;
    }
}
