package machinelearning.networks;

import machinelearning.Tools;
import machinelearning.evolution.evolvables.Evolvable;
import utopia.Utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: daan
 * Date: Oct 30, 2008
 * Time: 6:38:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class LSTM implements Network, Serializable {

    private int time;

    private double[] outputs;
    private double[] inputs;

    private double[] forgetx;
    private double[] forgety;
    private double[] ingatex;
    private double[] ingatey;
    private double[] outgatex;
    private double[] outgatey;
    private double[] cellx;
    private double[] celly;

    private double[] state;
    private double[] prevstate;
    private double[] prevcelly;

    private int nrCells;
    private int nrInputs;
    private int nrOutputs;
    private int nrOfWeights;
    final private double biasValue;
    private int numberOfBias;

    private double[][] win, derivsin;
    private double[][] wrecin, derivsrecin;
    private double[] wpeepin, derivspeepin;
    private double[][] wforget, derivsforget;
    private double[][] wrecforget, derivsrecforget;
    private double[] wpeepforget, derivspeepforget;
    private double[][] wcell, derivscell;
    private double[][] wreccell, derivsreccell;
    private double[][] woutgate, derivsoutgate;
    private double[][] wrecoutgate, derivsrecoutgate;
    private double[] wpeepout, derivspeepout;

    private double[][] woutputs, derivsoutputs;

    /*private double[][] dsin;
  private double[][] dsrecin;
  private double[] dspeepin;
  private double[][] dsforget;
  private double[][] dsrecforget;
  private double[] dspeepforget;
  private double[][] dscell;
  private double[][] dsreccell;*/

    protected double initialWeightRange = 0.1;
    protected double mutationMagnitude = initialWeightRange / 10;
    private final Random random = new Random();

    public static double f(double x) {
        return Tools.sigmoid(x);
    }

    public static double fprime(double x) {
        return Tools.sigmoidprime(x);
    }

    public static double g(double x) {
        return Tools.tanh(x);
    }

    public static double gprime(double x) {
        return Tools.tanhprime(x);
    }

    public LSTM(int numberOfExternalInputs, int numberOfHidden, int numberOfOutputs, double weightRange, double mutation) {
        this(numberOfExternalInputs, numberOfHidden, numberOfOutputs);
        this.initialWeightRange = weightRange;
        mutationMagnitude = mutation;
    }

    public LSTM(int numberOfExternalInputs, int numberOfHidden, int numberOfOutputs) {
        numberOfBias = 1;
        nrCells = numberOfHidden;
        nrInputs = numberOfExternalInputs + numberOfBias;
        nrOutputs = numberOfOutputs;
        biasValue = 1;
        nrOfWeights = (nrInputs * nrCells * 4) + (nrCells * nrCells * 4) +
                (nrCells * nrOutputs) + (nrCells * 3);

        this.reset();

        win = new double[nrCells][nrInputs];
        wrecin = new double[nrCells][nrCells];
        wpeepin = new double[nrCells];
        wforget = new double[nrCells][nrInputs];
        wrecforget = new double[nrCells][nrCells];
        wpeepforget = new double[nrCells];
        wcell = new double[nrCells][nrInputs];
        wreccell = new double[nrCells][nrCells];
        woutgate = new double[nrCells][nrInputs];
        wrecoutgate = new double[nrCells][nrCells];
        wpeepout = new double[nrCells];
        woutputs = new double[nrCells][nrOutputs];
        /*derivsin = new double[nrCells][nrInputs];
     derivsrecin = new double[nrCells][nrCells];
     derivspeepin = new double[nrCells];
     derivsforget = new double[nrCells][nrInputs];
     derivsrecforget = new double[nrCells][nrCells];
     derivspeepforget = new double[nrCells];
     derivscell = new double[nrCells][nrInputs];
     derivsreccell = new double[nrCells][nrCells];
     derivsoutgate = new double[nrCells][nrInputs];
     derivsrecoutgate = new double[nrCells][nrCells];
     derivspeepout = new double[nrCells];
     derivsoutputs = new double[nrCells][nrOutputs];



     dsin = new double[nrCells][nrInputs];
     dsrecin = new double[nrCells][nrCells];
     dspeepin = new double[nrCells];
     dsforget = new double[nrCells][nrInputs];
     dsrecforget = new double[nrCells][nrCells];
     dspeepforget = new double[nrCells];
     dscell = new double[nrCells][nrInputs];
     dsreccell = new double[nrCells][nrCells];*/


    }

    public void reset() {
        time = 0;
        inputs = new double[nrInputs];
        outputs = new double[nrOutputs];

        ingatex = new double[nrCells];
        outgatex = new double[nrCells];
        forgetx = new double[nrCells];
        cellx = new double[nrCells];
        ingatey = new double[nrCells];
        outgatey = new double[nrCells];
        forgety = new double[nrCells];
        celly = new double[nrCells];
        state = new double[nrCells];
        prevstate = new double[nrCells];
        prevcelly = new double[nrCells];
    }

    /*public double[] calculateDerivatives(double[] outputError) {

        for (int i = 0; i < nrCells; i++) {
            prevstate[i] = state[i];
            prevcelly[i] = celly[i];
        }


        for (int i = 0; i < nrCells; i++) {
            for (int j = 0; j < nrOutputs; j++) {
                derivsoutputs[i][j] += outputError[j] * celly[i];
            }

        }

        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }*/

    public void changeWeights(double[] weightChanges) {
        if (weightChanges.length != nrOfWeights) {
            System.out.println("argument of changeWeights has not the same length (" + weightChanges.length + ") as the number of weights (" + nrOfWeights + ")");
            throw new IllegalArgumentException();
        }
        double[] newWeights = getWeightsArray();
        for (int i = 0; i < newWeights.length; i++) {
            newWeights[i] += weightChanges[i];
        }
        setWeightsArray(newWeights);
    }

    public String toString(){
        return Arrays.toString(getWeightsArray());
    }
    
    public double[] getWeightsArray() {
        double[] weightsArray = new double[nrOfWeights];
        int index = 0;
        index = copyFlat(win, index, weightsArray);
        index = copyFlat(wrecin, index, weightsArray);
        index = copyFlat(wpeepin, index, weightsArray);
        index = copyFlat(wforget, index, weightsArray);
        index = copyFlat(wrecforget, index, weightsArray);
        index = copyFlat(wpeepforget, index, weightsArray);
        index = copyFlat(wcell, index, weightsArray);
        index = copyFlat(wreccell, index, weightsArray);
        index = copyFlat(woutgate, index, weightsArray);
        index = copyFlat(wrecoutgate, index, weightsArray);
        index = copyFlat(wpeepout, index, weightsArray);
        index = copyFlat(woutputs, index, weightsArray);
        return weightsArray;
    }

    public void setWeightsArray(double[] weightsArray) {
        if (weightsArray.length != nrOfWeights) {
            System.out.println("argument of setWeightsArray has not the same length (" + weightsArray.length + ") as the number of weights (" + nrOfWeights + ")");
            throw new IllegalArgumentException();
        }
        int index = 0;
        index = copyExpand(weightsArray, index, win);
        index = copyExpand(weightsArray, index, wrecin);
        index = copyExpand(weightsArray, index, wpeepin);
        index = copyExpand(weightsArray, index, wforget);
        index = copyExpand(weightsArray, index, wrecforget);
        index = copyExpand(weightsArray, index, wpeepforget);
        index = copyExpand(weightsArray, index, wcell);
        index = copyExpand(weightsArray, index, wreccell);
        index = copyExpand(weightsArray, index, woutgate);
        index = copyExpand(weightsArray, index, wrecoutgate);
        index = copyExpand(weightsArray, index, wpeepout);
        index = copyExpand(weightsArray, index, woutputs);
    }

    public void randomise() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumberOfInputs() {
        return nrInputs;
    }

    public int getNumberOfCells() {
        return nrCells;
    }

    public int getNumberOfWeights() {
        return nrOfWeights;
    }

    public int getNumberOfOutputs() {
        return nrOutputs;
    }

    public double getMutationMagnitude() {
        return mutationMagnitude;
    }

    public void setMutationMagnitude(double mutationMagnitude) {
        this.mutationMagnitude = mutationMagnitude;
    }

    public Evolvable getNewInstance() {
        LSTM newLSTM = new LSTM(this.getNumberOfInputs() - this.numberOfBias, this.getNumberOfCells(), this.getNumberOfOutputs());
        double[] weights = newLSTM.getWeightsArray();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble() * (initialWeightRange * 2) - initialWeightRange;
        }
        newLSTM.setWeightsArray(weights);
        return newLSTM;
    }

    public Evolvable copy() {
        double[] weights = this.getWeightsArray();
        LSTM copy = new LSTM(this.getNumberOfInputs() - this.numberOfBias, this.getNumberOfCells(), this.getNumberOfOutputs());
        copy.setWeightsArray(weights);
        return copy;
    }

    public void mutate() {
        double[] mutated = getWeightsArray();
        mutate(mutated);
        setWeightsArray(mutated);
        //System.out.println("LSTM mutated");
    }

    protected void mutate(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] += Utils.randomCauchy(mutationMagnitude);//random.nextGaussian() * mutationMagnitude;
        }
    }

    protected void mutate(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            mutate(array[i]);

        }
    }

    public double[] propagate(double[] doubles) {
        //int[] indices = new int[nrCells];
        for (int i = 0; i < inputs.length - 1; i++) {
            inputs[i] = doubles[i];
        }
        inputs[inputs.length - 1] = biasValue;
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = 0.0;
        }

        for (int cell = 0; cell < nrCells; cell++) {
            ingatex[cell] = 0.0;
            cellx[cell] = 0.0;
            outgatex[cell] = 0.0;
            forgetx[cell] = 0.0;
            for (int input = 0; input < nrInputs; input++) {
                ingatex[cell] += inputs[input] * win[cell][input];
                outgatex[cell] += inputs[input] * woutgate[cell][input];
                forgetx[cell] += inputs[input] * wforget[cell][input];
                cellx[cell] += inputs[input] * wcell[cell][input];
            }
            if (time > 0) {
                for (int i = 0; i < nrCells; i++) {
                    ingatex[cell] += prevcelly[i] * wrecin[cell][i];
                    cellx[cell] += prevcelly[i] * wreccell[cell][i];
                    outgatex[cell] += prevcelly[i] * wrecoutgate[cell][i];
                    forgetx[cell] += prevcelly[i] * wrecforget[cell][i];
                }
            }

            if (time > 0)
                ingatex[cell] += wpeepin[cell] * prevstate[cell];
            ingatey[cell] = f(ingatex[cell]);
            if (time > 0)
                forgetx[cell] += wpeepforget[cell] * prevstate[cell];
            forgety[cell] = f(forgetx[cell]);
            state[cell] = ingatey[cell] * g(cellx[cell]);
            if (time > 0)
                state[cell] += forgety[cell] * prevstate[cell];
            outgatex[cell] += wpeepout[cell] * state[cell];
            outgatey[cell] = f(outgatex[cell]);
            celly[cell] = outgatey[cell] * state[cell];

        }

        for (int i = 0; i < nrOutputs; i++) {
            for (int cell = 0; cell < nrCells; cell++) {
                outputs[i] += woutputs[cell][i] * celly[cell];
            }
        }

        // compute annoying partially-partial DS derivatives

        /*for (int cell = 0; cell < nrCells; cell++) {
            if(time == 0.0)
                dspeepin[cell] = 0.0;
            dspeepin[cell] *= forgety[cell];
            dspeepin[cell] += g(cellx[cell]) * fprime(ingatex[cell]) * prevstate[cell];
            if(time == 0.0)
                dspeepforget[cell] = 0.0;
            dspeepforget[cell] *= forgety[cell];
            dspeepforget[cell] += prevstate[cell] * fprime(forgetx[cell]) * prevstate[cell] * prevstate[cell];
            for (int i = 0; i < nrInputs; i++) {
                if(time == 0)
                    dscell[cell][i] = 0.0;
                dscell[cell][i] *= forgety[cell];
                dscell[cell][i] += gprime(cellx[cell]) * ingatey[cell] * inputs[i];
                if(time == 0)
                    dsin[cell][i] = 0.0;
                dsin[cell][i] *= forgety[cell];
                dsin[cell][i] += g(cellx[cell]) * fprime(ingatex[cell]) * inputs[i];
                if(time == 0)
                    dsforget[cell][i] = 0.0;
                dsforget[cell][i] *= forgety[cell];
                dsforget[cell][i] += fprime(forgetx[cell]) * prevstate[cell] * inputs[i];
            }
            // recurrent DS derv stuff
            for (int i = 0; i < nrCells; i++) {
                if(time == 0)
                    dsreccell[cell][i] = 0.0;
                dsreccell[cell][i] *= forgety[cell];
                dsreccell[cell][i] += gprime(cellx[cell]) * ingatey[cell] * prevcelly[i];
                if(time == 0)
                    dsrecin[cell][i] = 0.0;
                dsrecin[cell][i] *= forgety[cell];
                dsrecin[cell][i] += g(cellx[cell]) * fprime(ingatex[cell]) * prevcelly[i];
                if(time == 0)
                    dsrecforget[cell][i] = 0.0;
                dsrecforget[cell][i] *= forgety[cell];
                dsrecforget[cell][i] += fprime(forgetx[cell]) * prevstate[cell] * prevcelly[i];
            }
        }*/
        time++;
        return outputs;
    }

    private int copyFlat(double[][] source, int position, double[] target) {
        for (int i = 0; i < source.length; i++) {
            position = copyFlat(source[i], position, target);
        }
        return position;
    }

    private int copyFlat(double[] source, int position, double[] target) {
        System.arraycopy(source, 0, target, position, source.length);
        return position + source.length;
    }

    private int copyExpand(double[] source, int position, double[][] target) {
        for (int i = 0; i < target.length; i++) {
            position = copyExpand(source, position, target[i]);
        }
        return position;
    }


    private int copyExpand(double[] source, int position, double[] target) {
        System.arraycopy(source, position, target, 0, target.length);
        return position + target.length;
    }

    /*public String toString() {
        String result = "";
        result += arrayToString(win) + "\n";
        result += arrayToString(wrecin) + "\n";
        result += Arrays.toString(wpeepin) + "\n";
        result += arrayToString(wforget) + "\n";
        result += arrayToString(wrecforget) + "\n";
        result += Arrays.toString(wpeepforget) + "\n";
        result += arrayToString(wcell) + "\n";
        result += arrayToString(wreccell) + "\n";
        result += arrayToString(woutgate) + "\n";
        result += arrayToString(wrecoutgate) + "\n";
        result += Arrays.toString(wpeepout) + "\n";
        result += arrayToString(woutputs) + "\n";
        return result;
    }

    public String arrayToString(double[][] array) {
        String result = "{";
        for(double[] arr : array) {
           result += Arrays.toString(arr) + ", ";
        }
        result += "}";
        return result;
    }*/


}