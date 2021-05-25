

/*******************************************************************************
 * Copyright (c) 2020 Konduit K.K.
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package autoencoder;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.MultiImageTransform;
import org.datavec.image.transform.ShowImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import edu.southwestern.util.MiscUtil;

/**Example: Anomaly Detection on MNIST using simple autoencoder without pretraining
 * The goal is to identify outliers digits, i.e., those digits that are unusual or
 * not like the typical digits.
 * This is accomplished in this example by using reconstruction error: stereotypical
 * examples should have low reconstruction error, whereas outliers should have high
 * reconstruction error. The number of epochs here is set to 3. Set to 30 for even better
 * results.
 *
 * @author Alex Black
 */
public class PictureTargetTaskAutoEncoder {

    public static boolean visualize = true;
    
    //Images are of format given by allowedExtension
    private static final String [] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;
    
    private static final long seed = 12345;
    
    private static final Random randNumGen = new Random(seed);
    
    private static final int height = 28; //50;		// height of image
    private static final int width = 28; //50;		// width of image
    private static final int channels = 1; //3;		// 3 channels because using RGB

    public static void main(String[] args) throws Exception {

        //Set up network. 784 in/out (as MNIST images are 28x28).
        //784 -> 250 -> 10 -> 250 -> 784
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .updater(new AdaGrad(0.05))
                .activation(Activation.RELU)
                .l2(0.0001)
                .list()
                .layer(new DenseLayer.Builder().nIn(784).nOut(250)
                        .build())
                .layer(new DenseLayer.Builder().nIn(250).nOut(10)
                        .build())
                .layer(new DenseLayer.Builder().nIn(10).nOut(250)
                        .build())
                .layer(new OutputLayer.Builder().nIn(250).nOut(784)
                        .activation(Activation.LEAKYRELU)
                        .lossFunction(LossFunctions.LossFunction.MSE)
                        .build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(Collections.singletonList(new ScoreIterationListener(10)));

        
//        // Replace the whole portion in this comment surrounded section with
//        // something similar to lines 58 through 115 of ImagePipelineExample
//        ////////////////////////////////////// ADD
//        ImageRecordReader rr = new ImageRecordReader(28,28,3); //28x28 RGB images
//        rr.initialize(new FileSplit(new File("parentDir/")));
//
//        DataSetIterator iter = new RecordReaderDataSetIterator.Builder(rr, 32)
//             //Label index (first arg): Always value 1 when using ImageRecordReader. For CSV etc: use index of the column
//             //  that contains the label (should contain an integer value, 0 to nClasses-1 inclusive). Column indexes start
//             // at 0. Number of classes (second arg): number of label classes (i.e., 10 for MNIST - 10 digits)
//             // ??? .classification(1, nClasses)
//             .preProcessor(new ImagePreProcessingScaler())      //For normalization of image values 0-255 to 0-1
//             .build();
//        //////////////////////////////////////////// END ADD
//        
//        //Load data and split into training and testing sets. 40000 train, 10000 test
//        //DataSetIterator iter = new MnistDataSetIterator(100,50000,false);
        
        //String parentDirExamples = DownloaderUtility.DATAEXAMPLES.Download();
  
        File parentDir=new File("parentDir/");
        //Files in directories under the parent dir that have "allowed extensions" split needs a random number generator for reproducibility when splitting the files into train and test
        FileSplit filesInDir = new FileSplit(parentDir, allowedExtensions, randNumGen);

        //You do not have to manually specify labels. This class (instantiated as below) will
        //parse the parent dir and use the name of the subdirectories as label/class names
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        //The balanced path filter gives you fine tune control of the min/max cases to load for each class
        //Below is a bare bones version. Refer to javadoc for details
        BalancedPathFilter pathFilter = new BalancedPathFilter(randNumGen, allowedExtensions, labelMaker);

        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 20, 0); // Get ALL 20 images
        InputSplit trainData = filesInDirSplit[0];
        //InputSplit testData = filesInDirSplit[1];  //The testData is never used in the example, commenting out.

        //Specifying a new record reader with the height and width you want the images to be resized to.
        //Note that the images in this example are all of different size
        //They will all be resized to the height and width specified below
        ImageRecordReader recordReader = new ImageRecordReader(height,width,channels,labelMaker);
        
        ImageTransform transform = new MultiImageTransform(randNumGen,new ShowImageTransform("Display - before "));

        //Initialize the record reader with the train data and the transform chain
        recordReader.initialize(trainData,transform);
        int outputNum = recordReader.numLabels();
        //convert the record reader to an iterator for training - Refer to other examples for how to use an iterator
        int batchSize = 10; // Minibatch size. Here: The number of images to fetch for each call to dataIter.next().
        int labelIndex = 1; // Index of the label Writable (usually an IntWritable), as obtained by recordReader.next()
        // List<Writable> lw = recordReader.next();
        // then lw[0] =  NDArray shaped [1,3,50,50] (1, channels, height, width)
        //      lw[0] =  label as integer.

        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, outputNum);

        List<INDArray> featuresTrain = new ArrayList<>();	// Images to train the autoencoder with
        List<INDArray> featuresTest = new ArrayList<>();	// Images the autoencoder has never seen (test images to make sure the autoencoder properly runs)
        
        Random r = new Random(12345);
        while(dataIter.hasNext()){
            DataSet ds = dataIter.next();
            
            featuresTrain.add(ds.getFeatures());
            
            // For splitting up test and train sets
//            SplitTestAndTrain split = ds.splitTestAndTrain(8, r);  //8/2 split (from miniBatch = 10)
//            featuresTrain.add(split.getTrain().getFeatures());	// Adding the training images to featuresTrain
//            DataSet dsTest = split.getTest();
//            featuresTest.add(dsTest.getFeatures());				// Adding images to featuresTest
        }

        //Train model:
        int nEpochs = 100;
        for( int epoch=0; epoch<nEpochs; epoch++ ){
            for(INDArray data : featuresTrain){
            	long[] originalShape = data.shape();
            	INDArray reshapedArray = data.reshape(new int[] {(int) originalShape[0], 28*28});
                net.fit(reshapedArray,reshapedArray);
            }
            System.out.println("Epoch " + epoch + " complete");
        }
        
        
        List<Pair<Double,INDArray>> testResults = new ArrayList<>();
        List<Pair<INDArray,INDArray>> inputOutput = new ArrayList<>();
        
        // Images not in training set
        //for(INDArray data : featuresTest){
        for(INDArray data : featuresTrain){
           	long[] originalShape = data.shape();
        	INDArray reshapedArray = data.reshape(new int[] {(int) originalShape[0], 28*28});
            int nRows = reshapedArray.rows();
            for( int j=0; j<nRows; j++){
                INDArray example = reshapedArray.getRow(j, true);
                double score = net.score(new DataSet(example,example));
                INDArray output = net.output(example);
                // Add (score, example) pair to the appropriate list
                testResults.add(new ImmutablePair<>(score, example));
                inputOutput.add(new ImmutablePair<>(example, output));
                System.out.println("Score " + j + ": " + score + " example: " + example + "output: " + output);
                ArrayList<INDArray> last = new ArrayList<>();
                last.add(example);
                last.add(output);
                MNISTVisualizer inputVisualizer = new MNISTVisualizer(2.0, last, "Last Pair");
                inputVisualizer.visualize();
                MiscUtil.waitForReadStringAndEnterKeyPress();
            }
            
        }

        //After sorting, select N best and N worst scores (by reconstruction error) for each digit, where N=5
        List<INDArray> input = new ArrayList<>(50);
        List<INDArray> output = new ArrayList<>(50);
        for(Pair<INDArray, INDArray> p : inputOutput) {
        	input.add(p.getLeft());
        	output.add(p.getRight());
        }
        
        //Visualize by default
        if (visualize) {
            MNISTVisualizer inputVisualizer = new MNISTVisualizer(2.0, input, "Input");
            inputVisualizer.visualize();
            //Visualize the best and worst digits
            MNISTVisualizer outputVisualizer = new MNISTVisualizer(2.0, output, "Output");
            outputVisualizer.visualize();
        }
    }

    public static class MNISTVisualizer {
        private double imageScale;
        private List<INDArray> digits;  //Digits (as row vectors), one per INDArray
        private String title;
        private int gridWidth;

        public MNISTVisualizer(double imageScale, List<INDArray> digits, String title ) {
            this(imageScale, digits, title, 5);
        }

        public MNISTVisualizer(double imageScale, List<INDArray> digits, String title, int gridWidth ) {
            this.imageScale = imageScale;
            this.digits = digits;
            this.title = title;
            this.gridWidth = gridWidth;
        }

        public void visualize(){
            JFrame frame = new JFrame();
            frame.setTitle(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0,gridWidth));

            List<JLabel> list = getComponents();
            for(JLabel image : list){
                panel.add(image);
            }

            frame.add(panel);
            frame.setVisible(true);
            frame.pack();
        }

        private List<JLabel> getComponents(){
            List<JLabel> images = new ArrayList<>();
            for( INDArray arr : digits ){
                BufferedImage bi = new BufferedImage(28,28,BufferedImage.TYPE_BYTE_GRAY);
                for( int i=0; i<784; i++ ){
                    bi.getRaster().setSample(i % 28, i / 28, 0, (int)(255*arr.getDouble(i)));
                }
                ImageIcon orig = new ImageIcon(bi);
                Image imageScaled = orig.getImage().getScaledInstance((int)(imageScale*28),(int)(imageScale*28),Image.SCALE_REPLICATE);
                ImageIcon scaled = new ImageIcon(imageScaled);
                images.add(new JLabel(scaled));
            }
            return images;
        }
    }
}
