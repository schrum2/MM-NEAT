package edu.southwestern.evolution.nsga2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomNumbers;

public class CoevolutionNSGA2Test {

	//@Before
	public void setUp() throws Exception {
		Parameters.parameters = null;
		MMNEAT.clearClasses();
		String[] args = new String[] { "runNumber:0", "trials:1", "teams:3", "mu:5", "io:false", "netio:false", "mating:true",
				"task:edu.southwestern.tasks.mspacman.CooperativeSubtaskSelectorMsPacManTask",
				"experiment:edu.southwestern.experiment.evolution.LimitedMultiplePopulationGenerationalEAExperiment",
				"ea:edu.southwestern.evolution.nsga2.CoevolutionNSGA2",
				"pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.FullTaskMediator",
				"numCoevolutionSubpops:2",
				"pacmanFitnessModeMap:edu.southwestern.tasks.mspacman.objectives.fitnessassignment.GhostsPillsMap",
				"pacManMediatorClass1:edu.southwestern.tasks.mspacman.sensors.mediators.GhostTaskMediator",
				"pacManMediatorClass2:edu.southwestern.tasks.mspacman.sensors.mediators.PillTaskMediator" };
		Parameters.initializeParameterCollections(args);
		MMNEAT.loadClasses();
	}	
	
	//@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	//@Test
	public void test() {
		CoevolutionNSGA2 ea = new CoevolutionNSGA2();
		
		assertNotNull(ea);
		
		@SuppressWarnings("unused")
		ArrayList<Integer> s0, s1, s2, s3, s4, s5, s6, s7, s8, s9;
		
		ArrayList<Score> scores = new ArrayList<Score>();
		// layer 0
		scores.add(new Score(new BoundedIntegerValuedGenotype(s0 = ArrayUtil.intListFromArray(new int[] { 1, 5 })), new double[] { 1, 5 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(s1 = ArrayUtil.intListFromArray(new int[] { 4, 4 })), new double[] { 4, 4 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(s2 = ArrayUtil.intListFromArray(new int[] { 5, 1 })), new double[] { 5, 1 }, null));
		// layer 1
		scores.add(new Score(new BoundedIntegerValuedGenotype(s3 = ArrayUtil.intListFromArray(new int[] { 1, 4 })), new double[] { 1, 4 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(s4 = ArrayUtil.intListFromArray(new int[] { 3, 3 })), new double[] { 3, 3 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(s5 = ArrayUtil.intListFromArray(new int[] { 4, 1 })), new double[] { 4, 1 }, null));
		// layer 2
		scores.add(new Score(new BoundedIntegerValuedGenotype(s6 = ArrayUtil.intListFromArray(new int[] { 1, 3 })), new double[] { 1, 3 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(s7 = ArrayUtil.intListFromArray(new int[] { 2, 2 })), new double[] { 2, 2 }, null));
		scores.add(new Score(new BoundedIntegerValuedGenotype(s8 = ArrayUtil.intListFromArray(new int[] { 3, 1 })), new double[] { 3, 1 }, null));
		// layer 3
		scores.add(new Score(new BoundedIntegerValuedGenotype(s9 = ArrayUtil.intListFromArray(new int[] { 1, 1 })), new double[] { 1, 1 }, null));

		Collections.shuffle(scores, RandomNumbers.randomGenerator);

		//System.out.println("Best 3");
		ArrayList<Genotype> front0 = (ea.selection(0, 3, scores));
		ArrayList<ArrayList<Integer>> scores0 = justScores(front0);
		
		assertEquals(front0.size(), 3);
		assertTrue(scores0.contains(s0));
		assertTrue(scores0.contains(s1));
		assertTrue(scores0.contains(s2));
		
		//System.out.println("Best 6");
		ArrayList<Genotype> front0and1 = (ea.selection(0, 6, scores));
		ArrayList<ArrayList<Integer>> scores0and1 = justScores(front0and1);

		assertEquals(front0and1.size(), 6);
		assertTrue(scores0and1.contains(s0));
		assertTrue(scores0and1.contains(s1));
		assertTrue(scores0and1.contains(s2));
		assertTrue(scores0and1.contains(s3));
		assertTrue(scores0and1.contains(s4));
		assertTrue(scores0and1.contains(s5));
		
		//System.out.println("Best 9");
		ArrayList<Genotype> front0and1and2 = (ea.selection(0, 9, scores));
		ArrayList<ArrayList<Integer>> scores0and1and2 = justScores(front0and1and2);		

		assertEquals(front0and1and2.size(), 9);
		assertTrue(scores0and1and2.contains(s0));
		assertTrue(scores0and1and2.contains(s1));
		assertTrue(scores0and1and2.contains(s2));
		assertTrue(scores0and1and2.contains(s3));
		assertTrue(scores0and1and2.contains(s4));
		assertTrue(scores0and1and2.contains(s5));		
		assertTrue(scores0and1and2.contains(s6));
		assertTrue(scores0and1and2.contains(s7));
		assertTrue(scores0and1and2.contains(s8));		
	}
	
	@SuppressWarnings("rawtypes")
	public static ArrayList<ArrayList<Integer>> justScores(ArrayList<Genotype> scores) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		for(Genotype s : scores) {
			result.add(((BoundedIntegerValuedGenotype) s).getGenes());
		}
		return result;
	}
}
