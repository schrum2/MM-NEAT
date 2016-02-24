/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.testmatch;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.LonerTask;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MatchDataTask<T extends Network> extends LonerTask<T> implements NetworkTask {

    public MatchDataTask(){
        MMNEAT.registerFitnessFunction("Error", null, true);
    }
    
    @Override
    public Score<T> evaluate(Genotype<T> individual) {
        //RandomNumbers.randomGenerator = new Random(0);
        ArrayList<Pair<double[],double[]>> trainingSet = getTrainingPairs();
        ArrayList<ArrayList<Pair<Double,Double>>> samples = new ArrayList<ArrayList<Pair<Double,Double>>>(trainingSet.size());
        Network n = individual.getPhenotype();
        for(Pair<double[],double[]> pattern : trainingSet){
            double[] inputs = pattern.t1;
            double[] desiredOutputs = pattern.t2;
            double[] actualOutputs = n.process(inputs);
            if(CommonConstants.watch) {
                System.out.println("Desired: "+ Arrays.toString(desiredOutputs) + ", Actual: " + Arrays.toString(actualOutputs));
            }
            ArrayList<Pair<Double,Double>> neuronResults = new ArrayList<Pair<Double,Double>>(n.numOutputs());
            for(int i = 0; i < desiredOutputs.length; i++){
                neuronResults.add(new Pair<Double,Double>(desiredOutputs[i], actualOutputs[i]));
            }
            samples.add(neuronResults);
            if(CommonConstants.watch) {
                MiscUtil.waitForReadStringAndEnterKeyPress();
            }
        }
        double averageError = StatisticsUtilities.averageSquaredErrorEnergy(samples);
        return new Score<T>(individual, new double[]{-averageError}, null); // minimize error, so score is negative error
    }

    public abstract int numInputs();
    
    public abstract int numOutputs();
    
    /**
     * Just the approximation error
     * @return 
     */
    public int numObjectives() {
        return 1;
    }

    public double getTimeStamp() {
        return 0;
    }

    /**
     * Get or generate a collection of desired input/output pairs to train on.
     * @return collection of Pairs
     */
    public abstract ArrayList<Pair<double[],double[]>> getTrainingPairs();


}
