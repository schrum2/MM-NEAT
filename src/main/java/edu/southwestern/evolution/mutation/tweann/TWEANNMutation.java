package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.MultiObjectiveScore;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;

import java.util.ArrayList;

/**
 * General template for all mutations affecting a TWEANN.
 *
 * @author Jacob Schrum
 */
public abstract class TWEANNMutation extends Mutation<TWEANN> {

	// Every mutation has its own rate of occurrence
	protected double rate;

	/**
	 * Constructor retrieves the appropriate rate from the parameters.
	 *
	 * @param rateName
	 *            Parameter label for this mutation rate.
	 */
	public TWEANNMutation(String rateName) {
		this(Parameters.parameters.doubleParameter(rateName));
	}

	/**
	 * Constructor is provided with actual mutation rate.
	 * 
	 * @param rate
	 *            Rate of mutation: between 0 and 1
	 */
	public TWEANNMutation(double rate) {
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}

	/**
	 * Only perform the mutation if a random double is less than the mutation
	 * rate.
	 * 
	 * @return Whether to perform mutation
	 */
	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}

	/**
	 * This method generally isn't used. It is based on some ideas from Paul
	 * McQuesten (http://nn.cs.utexas.edu/?paulmcquesten), but isn't fully
	 * explored here. The idea is that new structural mutations add new links.
	 * It may be good to have a new link, but this will only be clear if the
	 * weights are set correctly. Therefore, after the new structure is
	 * introduced, the algorithm immediately evaluates the organism with several
	 * different weight options for the new links. The best resulting weights
	 * for the brand new links are kept.
	 *
	 * @param genotype
	 *            Genotype that was just mutated
	 * @param subs
	 *            array of index offsets in the link weight list. New link genes
	 *            are always added to the end of the link list, so by going
	 *            backward from the end of the list, the new links can be found.
	 *            The subs array has an entry for each newly added link. The
	 *            value is the number of steps backward from the end of the link
	 *            list to go in order to find the index of the link which was
	 *            newly added.
	 */
	public void cullForBestWeight(TWEANNGenotype genotype, int[] subs) {
		if ((CommonConstants.exploreWeightsOfNewStructure && subs.length == 1)
				|| (CommonConstants.cullModeMutations && subs.length > 1)) {
			// System.out.println("Exploring "+ subs.length +" weights after " +
			// this.getClass().getSimpleName());
			ArrayList<LinkGene> links = genotype.links;
			@SuppressWarnings("unchecked") // Not sure that this should only
											// apply to noisy tasks
			NoisyLonerTask<TWEANN> task = ((NoisyLonerTask<TWEANN>) MMNEAT.task);
			// Get the links added by mutation (sub depends on the mutation
			// used)
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
				// System.out.println(i+ ":" + weight + ":" + s);
				// Update bestWeight based on evaluation
				// TODO: This can be generalized later using the 'Better'
				// interface
				if (s.isBetter(bestScore) || (!s.isWorse(bestScore) && RandomNumbers.randomGenerator.nextBoolean())) {
					// Keep new weight if it is better, or by chance if neither
					// is better
					// System.out.println("Swap:" + (s.isBetter(bestScore) ? "Is
					// Better" : "Not Worse"));
					bestScore = s;
					bestWeights = weights;
				}
			}
			// Set actual weights based on various test evals
			for (int j = 0; j < subs.length; j++) {
				int sub = subs[j];
				links.get(links.size() - sub).weight = bestWeights[j];
			}
			// System.out.println("Chosen weight: " + bestWeight);
		}
	}
}
