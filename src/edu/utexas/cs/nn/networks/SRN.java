package edu.utexas.cs.nn.networks;

import edu.utexas.cs.nn.evolution.genotypes.MLPGenotype;
import edu.utexas.cs.nn.parameters.Parameters;
import java.util.Arrays;

/**
 * Simple Recurrent Network:
 *
 * Copy the activations of all hidden neurons on each propogation and treat
 * these activations as additional inputs to the network on the next time step
 * (extra recurrent inputs). There is a separate set of weights connecting these
 * recurrent inputs to the hidden layer.
 */
public class SRN extends MLP {

    protected int numActualInputs;

    public SRN(int numberOfInputs, int numberOfHidden, int numberOfOutputs) {
        super(numberOfInputs + numberOfHidden, numberOfHidden, numberOfOutputs);
        numActualInputs = numberOfInputs;
    }

    // An MLP Genotype that doesn't know where the actual inputs end and the recurrent inputs begin
    public SRN(MLPGenotype genotype, int inputs) {
        super(genotype);
        numActualInputs = inputs;
    }

    private SRN(MLP copy, int numActualInputs) {
        this(new MLPGenotype(copy), numActualInputs);
    }

    public SRN(double[][] firstConnectionLayer, double[][] secondConnectionLayer, int numInputs) {
        this(new MLP(firstConnectionLayer, secondConnectionLayer), numInputs);
    }

    @Override
    public int numInputs() {
        return numActualInputs;
    }

    @Override
    public double[] process(double[] inputs) {
        double[] combinedInputs = new double[numActualInputs + hiddenNeurons.length];
        //System.out.println("actual:" + numActualInputs + ",in:"+inputs.length + ",hidden:"+hiddenNeurons.length+ ",combined:" +combinedInputs.length);
        // Copy hidden activations as extra inputs
        System.arraycopy(inputs, 0, combinedInputs, 0, numActualInputs);
        System.arraycopy(hiddenNeurons, 0, combinedInputs, numActualInputs, hiddenNeurons.length);
        return super.process(combinedInputs);
    }

    @Override
    public SRN copy() {
        return new SRN(super.copy(), this.numActualInputs);
    }

    @Override
    public String toString() {
        return "SRN:" + super.toString();
    }

    public static void main(String[] args) {
        Parameters.initializeParameterCollections(args);

        MLP base = new MLP(2, 3, 1);
        SRN test = new SRN(2, 3, 1);

        double[] inputs = new double[]{5, -3};
        int iterations = 10;

        System.out.println("MLP stays the same");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Arrays.toString(base.process(inputs)));
        }

        System.out.println("SRN changes");
        for (int i = 0; i < iterations; i++) {
            System.out.println(Arrays.toString(test.process(inputs)));
        }

        System.out.println("Unless flushed every time");
        for (int i = 0; i < iterations; i++) {
            test.flush();
            System.out.println(Arrays.toString(test.process(inputs)));
        }
    }
}
