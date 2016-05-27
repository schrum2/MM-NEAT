package edu.utexas.cs.nn.evolution.nsga2;

import edu.utexas.cs.nn.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.mulambda.CooperativeCoevolutionMuLambda;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Jacob Schrum
 */
public class CooperativeCoevolutionNSGA2 extends CooperativeCoevolutionMuLambda {

	@Override
	public ArrayList<Genotype> selection(int popIndex, int toKeep, ArrayList<Score> sourcePopulation) {
		return PopulationUtil.removeListGenotypeType(NSGA2.staticSelection(toKeep,
				NSGA2.staticNSGA2Scores(PopulationUtil.addListScoreType(sourcePopulation))));
	}

	/**
	 * Testing
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		args = new String[] { "runNumber:0", "trials:1", "teams:3", "mu:5", "io:false", "netio:false", "mating:true",
				"task:edu.utexas.cs.nn.tasks.mspacman.CooperativeSubtaskSelectorMsPacManTask",
				"experiment:edu.utexas.cs.nn.experiment.LimitedMultiplePopulationGenerationalEAExperiment",
				"ea:edu.utexas.cs.nn.evolution.nsga2.CooperativeCoevolutionNSGA2",
				"pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.FullTaskMediator",
				"numCoevolutionSubpops:2",
				"pacmanFitnessModeMap:edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment.GhostsPillsMap",
				"pacManMediatorClass1:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.GhostTaskMediator",
				"pacManMediatorClass2:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.PillTaskMediator" };
		Parameters.initializeParameterCollections(args);
		MMNEAT.loadClasses();

		CooperativeCoevolutionNSGA2 ea = (CooperativeCoevolutionNSGA2) MMNEAT.ea;
		ArrayList<Score> scores = new ArrayList<Score>();
		// layer 0
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 1, 5 })),
				new double[] { 1, 5 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 4, 4 })),
				new double[] { 4, 4 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 5, 1 })),
				new double[] { 5, 1 }, null));
		// layer 1
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 1, 4 })),
				new double[] { 1, 4 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 3, 3 })),
				new double[] { 3, 3 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 4, 1 })),
				new double[] { 4, 1 }, null));
		// layer 2
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 1, 3 })),
				new double[] { 1, 3 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 2, 2 })),
				new double[] { 2, 2 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 3, 1 })),
				new double[] { 3, 1 }, null));
		// layer 3
		scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[] { 1, 1 })),
				new double[] { 1, 1 }, null));

		Collections.shuffle(scores, RandomNumbers.randomGenerator);

		System.out.println("Best 3");
		System.out.println(ea.selection(0, 3, scores));
		System.out.println("Best 6");
		System.out.println(ea.selection(0, 6, scores));
		System.out.println("Best 9");
		System.out.println(ea.selection(0, 9, scores));
	}
}
