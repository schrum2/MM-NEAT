package edu.utexas.cs.nn.networks;

import edu.utexas.cs.nn.evolution.genotypes.MLPGenotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class MLP implements Network {

    public int numInputs() {
        return firstConnectionLayer.length;
    }

    public int numOutputs() {
        return secondConnectionLayer[0].length;
    }

    public double[] process(double[] inputs) {
        return propagate(inputs);
    }

    public void flush() {
        // Matters for SRN
        clear(inputs);
        clear(hiddenNeurons);
        clear(outputs);
    }

    public boolean isMultitask() {
        // Leave like this until I bother developing multitask MLPs
        return false;
    }

    public void chooseMode(int mode) {
        // Does nothing until I bother developing multitask MLPs
    }

    /*
     * Everything below here comes from Togelius' implementation
     * and is only slightly modified by me
     */
    public double[][] firstConnectionLayer;
    public double[][] secondConnectionLayer;
    protected double[] hiddenNeurons;
    protected double[] inputs;
    protected double[] outputs;
    public double learningRate = Parameters.parameters.doubleParameter("backpropLearningRate");

    public MLP(MLPGenotype genotype) {
        this(genotype.firstConnectionLayer, genotype.secondConnectionLayer);
    }

    public MLP(int numberOfInputs, int numberOfHidden, int numberOfOutputs) {
        this.inputs = new double[numberOfInputs];
        this.firstConnectionLayer = new double[numberOfInputs][numberOfHidden];
        this.secondConnectionLayer = new double[numberOfHidden][numberOfOutputs];
        this.hiddenNeurons = new double[numberOfHidden];
        this.outputs = new double[numberOfOutputs];

        initializeAllLayersRandom();
    }

    public MLP(double[][] firstConnectionLayer, double[][] secondConnectionLayer) {
        this(firstConnectionLayer, secondConnectionLayer, secondConnectionLayer.length, secondConnectionLayer[0].length);
    }

    public MLP(double[][] firstConnectionLayer, double[][] secondConnectionLayer, int numberOfHidden,
            int numberOfOutputs) {
        this.inputs = new double[firstConnectionLayer.length];
        this.firstConnectionLayer = firstConnectionLayer;
        this.secondConnectionLayer = secondConnectionLayer;
        this.hiddenNeurons = new double[numberOfHidden];
        this.outputs = new double[numberOfOutputs];
    }

    public double[] propagate(double[] inputIn) {
        if (inputs == null) {
            inputs = new double[inputIn.length];
        }
        if (inputs != inputIn) {
            if (inputIn.length > inputs.length) {
                System.out.println("MLP given " + inputIn.length + " inputs, but only intialized for "
                        + inputs.length);
            }
            System.arraycopy(inputIn, 0, this.inputs, 0, inputIn.length);
        }
        if (inputIn.length < inputs.length) {
            System.out.println("NOTE: only " + inputIn.length + " inputs out of " + inputs.length + " are used in the network");
            System.out.println("inputIn:" + inputIn.length + ",inputs" + inputs.length);
            System.out.println("inputIn:" + Arrays.toString(inputIn));
            System.out.println("inputs:" + Arrays.toString(inputs));
            System.exit(1);
        }
        clear(hiddenNeurons);
        clear(outputs);
        propagateOneStep(inputs, hiddenNeurons, firstConnectionLayer);
        tanh(hiddenNeurons);
        propagateOneStep(hiddenNeurons, outputs, secondConnectionLayer);
        tanh(outputs);
        return outputs;
    }

    public final void initializeAllLayersRandom() {
        initializeRandom(this.firstConnectionLayer);
        initializeRandom(this.secondConnectionLayer);
    }

    private void initializeRandom(double[][] layer) {
        for (int i = 0; i < layer.length; i++) {
            for (int j = 0; j < layer[i].length; j++) {
                layer[i][j] = RandomNumbers.fullSmallRand();
            }
        }
    }

    public MLP copy() {
        return new MLP(copy(firstConnectionLayer), copy(secondConnectionLayer),
                hiddenNeurons.length, outputs.length);
    }

    protected double[][] copy(double[][] original) {
        double[][] copy = new double[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    protected void propagateOneStep(double[] fromLayer, double[] toLayer, double[][] connections) {
        for (int from = 0; from < fromLayer.length; from++) {
            for (int to = 0; to < toLayer.length; to++) {
                toLayer[to] += fromLayer[from] * connections[from][to];
            }
        }
    }

    protected void clear(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    protected void tanh(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.tanh(array[i]);
        }
    }

    public double sum() {
        double sum = 0;
        for (int i = 0; i < firstConnectionLayer.length; i++) {
            for (int j = 0; j < firstConnectionLayer[i].length; j++) {
                sum += firstConnectionLayer[i][j];
            }
        }
        for (int i = 0; i < secondConnectionLayer.length; i++) {
            for (int j = 0; j < secondConnectionLayer[i].length; j++) {
                sum += secondConnectionLayer[i][j];
            }
        }
        return sum;
    }

    public void println() {

        System.out.print("\n\n----------------------------------------------------"
                + "-----------------------------------\n");
        for (int i = 0; i < firstConnectionLayer.length; i++) {

            System.out.print("|");

            for (int j = 0; j < firstConnectionLayer[i].length; j++) {
                System.out.print(" " + firstConnectionLayer[i][j]);
            }

            System.out.print(" |\n");
        }

        System.out.print("----------------------------------------------------"
                + "-----------------------------------\n");


        for (int i = 0; i < secondConnectionLayer.length; i++) {

            System.out.print("|");

            for (int j = 0; j < secondConnectionLayer[i].length; j++) {

                System.out.print(" " + secondConnectionLayer[i][j]);

            }

            System.out.print(" |\n");

        }

        System.out.print("----------------------------------------------------"
                + "-----------------------------------\n");
    }

    private double dtanh(double num) {
        //return 1;
        return (1 - (num * num));

        // for the sigmoid
        //final double val = sig(num);
        //return (val*(1-val));
    }

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
                    System.out.println("target: " + targetOutputs[output] + " outputs: " + outputs[output] + " error:" + outputError[output] + "\n"
                            + "hidden: " + hiddenNeurons[hidden] + "\nnew conn weight: " + secondConnectionLayer[hidden][output] + " was: " + saveAway + "\n");
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

    public String info() {
        int numberOfConnections = (firstConnectionLayer.length * firstConnectionLayer[0].length)
                + (secondConnectionLayer.length * secondConnectionLayer[0].length);
        return "Straight mlp, mean connection weight " + (sum() / numberOfConnections);
    }

    @Override
    public String toString() {
        return "MLP:" + firstConnectionLayer.length + "/" + secondConnectionLayer.length + "/" + outputs.length;
    }

    public int effectiveNumOutputs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double[] modeOutput(int mode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int lastMode() {
        // Does nothing until I develope multitask for MLPs
        return -1;
    }

    public int numModes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
