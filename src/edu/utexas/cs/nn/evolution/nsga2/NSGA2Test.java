package edu.utexas.cs.nn.evolution.nsga2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 * Test class for NSGA2 sorting algorithm. Only core methods tested
 * @author Lauren Gillespie
 *
 */
public class NSGA2Test {

	String[] args;
	@SuppressWarnings("rawtypes")
	NSGA2 ea;
	@SuppressWarnings("rawtypes")
	ArrayList<Score> scores;
    ArrayList<ArrayList<Long>> fronts;
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setUp() throws Exception {
		args = new String[]{"runNumber:0", "trials:1", "mu:5", "io:false", "netio:false", "mating:true", "task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask", "ea:edu.utexas.cs.nn.evolution.nsga2.NSGA2", "pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.FullTaskMediator"};
        ea = (NSGA2) MMNEAT.ea;
        scores = new ArrayList<Score>();Parameters.initializeParameterCollections(args);
        fronts = new ArrayList<ArrayList<Long>>();
        MMNEAT.loadClasses();
        ea = (NSGA2) MMNEAT.ea;
        scores = new ArrayList<Score>();	
        // layer 0
        ArrayList<Long> layer0 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 5})), new double[]{1, 5}, null));
        layer0.add(scores.get(0).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{4, 4})), new double[]{4, 4}, null));
        layer0.add(scores.get(1).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{5, 1})), new double[]{5, 1}, null));
        layer0.add(scores.get(2).individual.getId());
        fronts.add(layer0);
        // layer 1
        ArrayList<Long> layer1 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 4})), new double[]{1, 4}, null));
        layer1.add(scores.get(3).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{3, 3})), new double[]{3, 3}, null));
        layer1.add(scores.get(4).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{4, 1})), new double[]{4, 1}, null));
        layer1.add(scores.get(5).individual.getId());
        fronts.add(layer1);
        // layer 2
        ArrayList<Long> layer2 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 3})), new double[]{1, 3}, null));
        layer2.add(scores.get(6).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{2, 2})), new double[]{2, 2}, null));
        layer2.add(scores.get(7).individual.getId());
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{3, 1})), new double[]{3, 1}, null));
        layer2.add(scores.get(8).individual.getId());
        fronts.add(layer2);
        // layer 3
        ArrayList<Long> layer3 = new ArrayList<Long>();
        scores.add(new Score(new BoundedIntegerValuedGenotype(ArrayUtil.intListFromArray(new int[]{1, 1})), new double[]{1, 1}, null));
        layer3.add(scores.get(9).individual.getId());
        fronts.add(layer3);
        //randomizes layers
        Collections.shuffle(scores, RandomNumbers.randomGenerator);
	}

	/**
	 * tests NSGA2 algorithm and selection process
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSelection() {
		ArrayList<Genotype> result0 = ea.selection(3, scores);
		assertTrue(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(PopulationUtil.addListGenotypeType(result0)), fronts.get(0)));
        ArrayList<Genotype> result1 = ea.selection(6, scores);
        fronts.get(1).addAll(fronts.get(0));
        assertTrue(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(PopulationUtil.addListGenotypeType(result1)), fronts.get(1)));
        ArrayList<Genotype> result2 = ea.selection(9, scores);
        fronts.get(2).addAll(fronts.get(1));
        assertTrue(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(PopulationUtil.addListGenotypeType(result2)), fronts.get(2)));
	}
}
