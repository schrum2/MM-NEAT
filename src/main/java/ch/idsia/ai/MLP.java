package ch.idsia.ai;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:15:10 PM
 */
public class MLP implements FA<double[], double[]>, Evolvable {

    private double[][] firstConnectionLayer;
    private double[][] secondConnectionLayer;
    private double[] hiddenNeurons;
    private double[] outputs;
    private double[] inputs;
    //private double[] targetOutputs;
    public double mutationMagnitude = 0.1;


    public static double mean = 0.0f;        // initialization mean
    public static double deviation = 0.1f;   // initialization deviation

    public static final Random random = new Random();
    public double learningRate = 0.01;

    /**
     * This public constructor method uses three parameters to initialize 
     * a MLP type neural network.  
     * 
     * @param numberOfInputs Integer representing the number of of inputs neurons.
     * @param numberOfHidden Integer representing the number of hidden neurons.
     * @param numberOfOutputs Integer representing the number of output neurons.
     */
    public MLP(int numberOfInputs, int numberOfHidden, int numberOfOutputs) {

        firstConnectionLayer = new double[numberOfInputs][numberOfHidden];
        secondConnectionLayer = new double[numberOfHidden][numberOfOutputs];
        hiddenNeurons = new double[numberOfHidden];
        outputs = new double[numberOfOutputs];
        //targetOutputs = new double[numberOfOutputs];
        inputs = new double[numberOfInputs];
        initializeLayer(firstConnectionLayer);
        initializeLayer(secondConnectionLayer);
    }

    /**
     * This public constructor method uses two 2-D arrays and two integer parameters
     * to initialize a MLP type neural network.
     * 
     * @param firstConnectionLayer Double 2-D array used 
     * @param secondConnectionLayer Double 2-D array used
     * @param numberOfHidden Integer representing the number of hidden neurons
     * @param numberOfOutputs Integer representing the number of output neurons
     */
    public MLP(double[][] firstConnectionLayer, double[][] secondConnectionLayer, int numberOfHidden,
               int numberOfOutputs) {
        this.firstConnectionLayer = firstConnectionLayer;
        this.secondConnectionLayer = secondConnectionLayer;
        inputs = new double[firstConnectionLayer.length];
        hiddenNeurons = new double[numberOfHidden];
        outputs = new double[numberOfOutputs];
    }

    /**
     * This method takes a 2-D double array as a parameter that represents 
     * a MLP layer. Each layer's neurons are filled with the value of a 
     * pseudo-random, Gaussian distributed double value * the deviation + the mean.   
     * 
     * @param layer Double 2-D Array representing neural network layer.
     */
    protected void initializeLayer(double[][] layer) {
        for (int i = 0; i < layer.length; i++) {
            for (int j = 0; j < layer[i].length; j++) {
                layer[i][j] = (random.nextGaussian() * deviation + mean);
            }
        }
    }

    /**
     * This method returns a new instance of a MLP.
     * 
     * @return A new instance of a MLP. 
     */
    public MLP getNewInstance() {
        return new MLP(firstConnectionLayer.length, secondConnectionLayer.length, outputs.length);
    }

    /**
     * This method is a kick-off method for the copy method.
     * A copy of a MLP is returned with an updated mutationMagnitude.
     * 
     * @return MLP that is a new MLP copy.
     */
    public MLP copy() {
        MLP copy = new MLP(copy(firstConnectionLayer), copy(secondConnectionLayer),
                hiddenNeurons.length, outputs.length);
        copy.setMutationMagnitude(mutationMagnitude);
        return copy;
    }

    /**
     * This method returns copies of a MLP's layers.
     * 
     * @param original Double 2-D array representing the original MLP's layers
     * @return Double 2-D array copy of the original layers sent.
     */
    private double[][] copy(double[][] original) {
        double[][] copy = new double[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    /**
     * This kick-off method mutates a MLP by mutating the 
     * first connection layer and second connection layer.
     */
    public void mutate() {
        mutate(firstConnectionLayer);
        mutate(secondConnectionLayer);
    }

    /**
     * This method takes an array of neurons from a layer and
     * randomly mutates each neuron.
     * 
     * @param array Double array with neurons to be mutated. 
     */
    private void mutate(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] += random.nextGaussian() * mutationMagnitude;
        }
    }

    /**
     * This kick-off mutate method takes a 2D Double array as a parameter that represents
     * a MLP layer. Every neuron in every layer is then mutated.
     * 
     * @param array 2D Double array that represents a collection of MLP layers.
     */
    private void mutate(double[][] array) {
        for (double[] anArray : array) {
            mutate(anArray);
        }
    }

    /**
     * This method uses the form of searching using particle swarm optimization to find
     * an optimal neuron value from three MLPs.
     * 
     * @param last MLP representing the last search.
     * @param pBest MLP representing the most recent search.
     * @param gBest MLP representing the global best search. 
     */
    public void psoRecombine(MLP last, MLP pBest, MLP gBest) {
        // Those numbers are supposed to be constants. Ask Maurice Clerc.
        final double ki = 0.729844;
        final double phi = 2.05;

        double phi1 = phi * random.nextDouble();
        double phi2 = phi * random.nextDouble();
        //System.out.println("phi1: "+phi1+" phi2: "+phi2);
        //System.out.println(" LAST:" + last);
        //System.out.println(" PBEST:" + pBest);
        //System.out.println(" GBEST:" + gBest);
        //System.out.println(" THIS:" + toString());
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < hiddenNeurons.length; j++) {
                firstConnectionLayer[i][j] = (double) (firstConnectionLayer[i][j] + ki * (firstConnectionLayer[i][j] - ((double[][]) (last.firstConnectionLayer))[i][j]
                        + phi1 * (((double[][]) (pBest.firstConnectionLayer))[i][j] - firstConnectionLayer[i][j])
                        + phi2 * (((double[][]) (gBest.firstConnectionLayer))[i][j] - firstConnectionLayer[i][j])));
            }
        }

        for (int i = 0; i < hiddenNeurons.length; i++) {
            for (int j = 0; j < outputs.length; j++) {
                secondConnectionLayer[i][j] = (double) (secondConnectionLayer[i][j] + ki * (secondConnectionLayer[i][j] - ((double[][]) (last.secondConnectionLayer))[i][j]
                        + phi1 * (((double[][]) (pBest.secondConnectionLayer))[i][j] - secondConnectionLayer[i][j])
                        + phi2 * (((double[][]) (gBest.secondConnectionLayer))[i][j] - secondConnectionLayer[i][j])));
            }
        }

    }

    /**
     * This method clears all of the neuron values in a layer.
     * 
     * @param array Double array representing neuron values.
     */
    private void clear(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    public void reset() {
    }

    /**
     * This method calls the propagate method on an array of doubles (neurons).
     * 
     * @return Double array of neurons filled from the propagate method.
     */
    public double[] approximate(double[] doubles) {
        return propagate(doubles);
    }

    /**
     * This method sends the input neuron values across layers to the output layer,
     * where the output neurons are then stored in a Double array that is returned. 
     * 
     * @param inputIn Double array with input neuron values.
     * @return Double array with the output neuron values.
     */
    public double[] propagate(double[] inputIn) {
        if (inputs != inputIn) {
            System.arraycopy(inputIn, 0, this.inputs, 0, inputIn.length);
        }
        if (inputIn.length < inputs.length)
            System.out.println("NOTE: only " + inputIn.length + " inputs out of " + inputs.length + " are used in the network");
        propagateOneStep(inputs, hiddenNeurons, firstConnectionLayer);
        tanh(hiddenNeurons);
        propagateOneStep(hiddenNeurons, outputs, secondConnectionLayer);
        tanh(outputs);

        return outputs;

    }

    /**
     * This helper method clears the layer where the neurons are being sent, and then fills the toLayer
     * with the value from the fromLayer at the position of respective neuron * the connection value.
     * 
     * @param fromLayer Double array representing initial beginning neurons.
     * @param toLayer Double array representing the layer where the initial neurons will be sent.
     * @param connections 2D Double array representing the resulting connections.
     */
    private void propagateOneStep(double[] fromLayer, double[] toLayer, double[][] connections) {
        clear(toLayer);
        for (int from = 0; from < fromLayer.length; from++) {
            for (int to = 0; to < toLayer.length; to++) {
                toLayer[to] += fromLayer[from] * connections[from][to];
                //System.out.println("From : " + from + " to: " + to + " :: " +toLayer[to] + "+=" +  fromLayer[from] + "*"+  connections[from][to]);
            }
        }
    }

    /**
     * This method calculates the output error and hidden layer error, then 
     * updates the weights of the first two layers. The combined sum of the first two layers 
     * is returned.
     * 
     * @param targetOutputs Double array representing the target outputs of the layer.
     * @return Double value representing the sum of errors in the first two layers.
     */
    public double backPropagate(double[] targetOutputs) {
        // Calculate output error
        double[] outputError = new double[outputs.length];

        for (int i = 0; i < outputs.length; i++) {
            //System.out.println("Node : " + i);
            outputError[i] = dtanh(outputs[i]) * (targetOutputs[i] - outputs[i]);
            //System.out.println("Err: " + (targetOutputs[i] - outputs[i]) +  "=" + targetOutputs[i] +  "-" + outputs[i]);
            //System.out.println("dnet: " +  outputError[i] +  "=" + (dtanh(outputs[i])) +  "*" + (targetOutputs[i] - outputs[i]));

            if (Double.isNaN(outputError[i])) {
                System.out.println("Problem at output " + i);
                System.out.println(outputs[i] + " " + targetOutputs[i]);
                System.exit(0);
            }
        }

        // Calculate hidden layer error
        double[] hiddenError = new double[hiddenNeurons.length];

        for (int hidden = 0; hidden < hiddenNeurons.length; hidden++) {
            double contributionToOutputError = 0;
            // System.out.println("Hidden: " + hidden);
            for (int toOutput = 0; toOutput < outputs.length; toOutput++) {
                // System.out.println("Hidden " + hidden + ", toOutput" + toOutput);
                contributionToOutputError += secondConnectionLayer[hidden][toOutput] * outputError[toOutput];
                // System.out.println("Err tempSum: " + contributionToOutputError +  "=" +secondConnectionLayer[hidden][toOutput]  +  "*" +outputError[toOutput] );
            }
            hiddenError[hidden] = dtanh(hiddenNeurons[hidden]) * contributionToOutputError;
            //System.out.println("dnet: " + hiddenError[hidden] +  "=" +  dtanh(hiddenNeurons[hidden])+  "*" + contributionToOutputError);
        }

        ////////////////////////////////////////////////////////////////////////////
        //WEIGHT UPDATE
        ///////////////////////////////////////////////////////////////////////////
        // Update first weight layer
        for (int input = 0; input < inputs.length; input++) {
            for (int hidden = 0; hidden < hiddenNeurons.length; hidden++) {

                double saveAway = firstConnectionLayer[input][hidden];
                firstConnectionLayer[input][hidden] += learningRate * hiddenError[hidden] * inputs[input];

                if (Double.isNaN(firstConnectionLayer[input][hidden])) {
                    System.out.println("Late weight error! hiddenError " + hiddenError[hidden]
                            + " input " + inputs[input] + " was " + saveAway);
                }
            }
        }

        // Update second weight layer
        for (int hidden = 0; hidden < hiddenNeurons.length; hidden++) {

            for (int output = 0; output < outputs.length; output++) {

                double saveAway = secondConnectionLayer[hidden][output];
                secondConnectionLayer[hidden][output] += learningRate * outputError[output] * hiddenNeurons[hidden];

                if (Double.isNaN(secondConnectionLayer[hidden][output])) {
                    System.out.println("target: " + targetOutputs[output] + " outputs: " + outputs[output] + " error:" + outputError[output] + "\n" +
                            "hidden: " + hiddenNeurons[hidden] + "\nnew conn weight: " + secondConnectionLayer[hidden][output] + " was: " + saveAway + "\n");
                }
            }
        }

        double summedOutputError = 0.0;
        for (int k = 0; k < outputs.length; k++) {
            summedOutputError += Math.abs(targetOutputs[k] - outputs[k]);
        }
        summedOutputError /= outputs.length;

        // Return something sensible
        return summedOutputError;
    }

    /**
     * This method returns the value of the sigmoid function when using the negative
     * value of a neuron.
     * 
     * @param val Double value representing a neuron value.
     * @return The value of the sigmoid function with the value of a neuron as the variable.
     */
    @SuppressWarnings("unused")
	private double sig(double val) {
        return 1.0d / (1.0d + Math.exp(-val));
    }

    /**
     * This method fills a layer with the hyperbolic tangent value of the 
     * current neuron in the layer.
     * 
     * @param array Double array representing a layer of neurons.
     */
    private void tanh(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.tanh(array[i]);
            // for the sigmoid
            // array[i] = array[i];
            //array[i] = sig(array[i]);//array[i];//
        }
    }

    /**
     * This method takes a double parameter that is the error of 
     * a neuron value * the error contribution. This value is squared
     * then subtracted by 1 and then returned.
     * 
     * @param num Double value representing the output errors.
     * @return double representing 1 - the output error^2.
     */
    private double dtanh(double num) {
        //return 1;
        return (1 - (num * num));
        // for the sigmoid
        //final double val = sig(num);
        //return (val*(1-val));
    }

    /**
     * This method goes through every neuron in the MLP and
     * returns the sum of all neuron values. 
     * 
     * @return Double value of the sum of all neuron values.
     */
    private double sum() {
        double sum = 0;
        for (double[] aFirstConnectionLayer : firstConnectionLayer) {
            for (double anAFirstConnectionLayer : aFirstConnectionLayer) {
                sum += anAFirstConnectionLayer;
            }
        }
        for (double[] aSecondConnectionLayer : secondConnectionLayer) {
            for (double anASecondConnectionLayer : aSecondConnectionLayer) {
                sum += anASecondConnectionLayer;
            }
        }
        return sum;
    }

    /**
     * This helper method gets the mutationMagnitude. 
     * 
     * @return Double value mutationMagnitude.
     */
    public double getMutationMagnitude() {
        return mutationMagnitude;
    }

    /**
     * This helper method sets the mutationMagnitude.
     * 
     * @param mutationMagnitude The mutation magnitude to be changed.
     */
    public void setMutationMagnitude(double mutationMagnitude) {
        this.mutationMagnitude = mutationMagnitude;
    }

    /**
     * This helper method sets the initial mean and deviation values of an MLP.
     * 
     * @param mean Double value representing the mean value of an MLP.
     * @param deviation Double value representing the deviation value of an MLP.
     */
    public static void setInitParameters(double mean, double deviation) {
        System.out.println("PARAMETERS SET: " + mean + "  deviation: " + deviation);

        MLP.mean = mean;
        MLP.deviation = deviation;
    }

    /**
     * This method prints out the contents of a layer in a readable manner.
     */
    public void println() {
        System.out.print("\n\n----------------------------------------------------" +
                "-----------------------------------\n");
        for (double[] aFirstConnectionLayer : firstConnectionLayer) {
            System.out.print("|");
            for (double anAFirstConnectionLayer : aFirstConnectionLayer) {
                System.out.print(" " + anAFirstConnectionLayer);
            }
            System.out.print(" |\n");
        }
        System.out.print("----------------------------------------------------" +
                "-----------------------------------\n");
        for (double[] aSecondConnectionLayer : secondConnectionLayer) {
            System.out.print("|");
            for (double anASecondConnectionLayer : aSecondConnectionLayer) {
                System.out.print(" " + anASecondConnectionLayer);
            }
            System.out.print(" |\n");
        }
        System.out.print("----------------------------------------------------" +
                "-----------------------------------\n");
    }

    /**
     * This method prints out the mean connection weight.
     */
    public String toString() {
        int numberOfConnections = (firstConnectionLayer.length * firstConnectionLayer[0].length) +
                (secondConnectionLayer.length * secondConnectionLayer[0].length);
        return "Straight mlp, mean connection weight " + (sum() / numberOfConnections);
    }

    /**
     * This helper method sets the learning rate. 
     * 
     * @param learningRate Double value representing the learning rate.
     */
    public void ssetLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    /**
     * This method returns a copy of the output neurons.
     * 
     * @return Double array copy of output neurons. 
     */
    public double[] getOutputs() {
        double[] outputsCopy = new double[outputs.length];
        System.arraycopy(outputs, 0, outputsCopy, 0, outputs.length);
        return outputsCopy;
    }

    /**
     * This method gets the weights of each neuron of an MLP and stores
     * each weight value in a Double array that is then returned.
     * 
     * @return Double array with values of all the weights in the MLP.
     */
    public double[] getWeightsArray() {
        double[] weights = new double[inputs.length * hiddenNeurons.length + hiddenNeurons.length * outputs.length];

        int k = 0;
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < hiddenNeurons.length; j++) {
                weights[k] = firstConnectionLayer[i][j];
                k++;
            }
        }
        for (int i = 0; i < hiddenNeurons.length; i++) {
            for (int j = 0; j < outputs.length; j++) {
                weights[k] = secondConnectionLayer[i][j];
                k++;
            }
        }
        return weights;
    }

    /**
     * This method sets the weights from a double array of a MLP's weights. 
     * 
     * @param weights Double array with values of all the weights in the MLP.
     */
    public void setWeightsArray(double[] weights) {
        int k = 0;

        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < hiddenNeurons.length; j++) {
                firstConnectionLayer[i][j] = weights[k];
                k++;
            }
        }
        for (int i = 0; i < hiddenNeurons.length; i++) {
            for (int j = 0; j < outputs.length; j++) {
                secondConnectionLayer[i][j] = weights[k];
                k++;
            }
        }
    }
	/**
	 * This helper method returns the number of input neurons from the input layer.
	 * 
	 * @return Integer value of the number of input neurons.
	 */
    public int getNumberOfInputs() {
        return inputs.length;
    }

    /**
     * This helper method is a kick-off method for the randomise method.
     */
    public void randomise() {
        randomise(firstConnectionLayer);
        randomise(secondConnectionLayer);
    }

    /**
     * This method goes through every neuron in both layers of an MLP
     * and randomly changes the values of the neurons.
     * 
     * @param layer 2D Double array representing a layer of an MLP.
     */
    protected void randomise(double[][] layer) {
        for (int i = 0; i < layer.length; i++) {
            for (int j = 0; j < layer[i].length; j++) {
                layer[i][j] = (Math.random() * 4.0) - 2.0;
            }
        }
    }

    /**
     * This method returns a weights array by calling the getWeightsArray() method.
     * 
     * @return Double array representing all the weight values in an MLP.
     */
    public double[] getArray() {
        return getWeightsArray();
    }

    /**
     * This method calls the setWeightsArray on the array parameter
     * passed. 
     * 
     * @param array Double array representing all the weight values in an MLP. 
     */
    public void setArray(double[] array) {
        setWeightsArray(array);
    }

}
