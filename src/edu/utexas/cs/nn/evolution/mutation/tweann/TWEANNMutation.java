package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.mutation.Mutation;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class TWEANNMutation extends Mutation<TWEANN> {

    protected double rate;

    public TWEANNMutation(String rateName) {
        this(Parameters.parameters.doubleParameter(rateName));
    }

    public TWEANNMutation(double rate) {
        this.rate = rate;
    }

    public boolean perform() {
        return (RandomNumbers.randomGenerator.nextDouble() < rate);
    }

    public void cullForBestWeight(TWEANNGenotype genotype, int[] subs) {
        if ((CommonConstants.exploreWeightsOfNewStructure && subs.length == 1)
                || (CommonConstants.cullModeMutations && subs.length > 1)) {
            //System.out.println("Exploring "+ subs.length +" weights after " + this.getClass().getSimpleName());
            ArrayList<LinkGene> links = genotype.links;
            NoisyLonerTask<TWEANN> task = ((NoisyLonerTask<TWEANN>) MMNEAT.task);
            // Get the links added by mutation (sub depends on the mutation used)
            double[] bestWeights = new double[subs.length];
            for (int i = 0; i < subs.length; i++) {
                int sub = subs[i];
                LinkGene lg = links.get(links.size() - sub);
                bestWeights[i] = lg.weight;
            }
            Pair<double[], double[]> pair = task.oneEval(genotype, 0);
            MultiObjectiveScore<TWEANN> bestScore = new MultiObjectiveScore<TWEANN>(null, pair.t1, null, pair.t2);

            for (int i = 0; i < CommonConstants.litterSize; i++) {
                // Plug in new weights
                double[] weights = RandomNumbers.randomArray(subs.length);
                for (int j = 0; j < subs.length; j++) {
                    int sub = subs[j];
                    links.get(links.size() - sub).weight = weights[j];
                }
                // Evaluate with new weights
                Pair<double[], double[]> score = task.oneEval(genotype, 0);
                MultiObjectiveScore<TWEANN> s = new MultiObjectiveScore<TWEANN>(null, score.t1, null, score.t2);
                //System.out.println(i+ ":" + weight + ":" + s);
                // Update bestWeight based on evaluation
                // TODO: This can be generalized later using the 'Better' interface
                if (s.isBetter(bestScore)
                        || (!s.isWorse(bestScore)
                        && RandomNumbers.randomGenerator.nextBoolean())) {
                    // Keep new weight if it is better, or by chance if neither is better
                    //System.out.println("Swap:" + (s.isBetter(bestScore) ? "Is Better" : "Not Worse"));
                    bestScore = s;
                    bestWeights = weights;
                }
            }
            // Set actual weights based on various test evals
            for (int j = 0; j < subs.length; j++) {
                int sub = subs[j];
                links.get(links.size() - sub).weight = bestWeights[j];
            }
            //System.out.println("Chosen weight: " + bestWeight);
        }
    }
}
