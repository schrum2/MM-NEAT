package edu.southwestern.tasks.functionoptimization;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;

public class FunctionOptimizationRangeBinLabelsTest {
	
	static final boolean BATCH_OPTIONS = true;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MMNEAT.clearClasses();
		String[] params = "foBinDimension:10 solutionVectorSlices:2 foVectorLength:10 foUpperBounds:5.12 foLowerBounds:-5.12 io:false netio:false ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels".split(" ");
		Parameters.initializeParameterCollections(params);
		MMNEAT.loadClasses();
	}
	
	static final double INTERVAL = 5.12;
	FunctionOptimizationRangeBinLabels binLabels;
	double[] allMax;
	double[] allMin;
	double[] doubleNums1;
	double[] doubleNums2;
	
	@Before
	public void setUp() throws Exception {
		binLabels = new FunctionOptimizationRangeBinLabels();
		allMax = new double[] {5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12};
		allMin = new double[] {-5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12};
		doubleNums1 = new double[] {1.3, 2.6, 5.12, -1.0, 3.21, -5.0, -2.32, 1.12, -1.64, -5.12};
		doubleNums2 = new double[] {1.01, 5.12, 3.43, -5.12, 0.0, -0.1, 2.1, -1.1, -3.18, -4.17};
	}

	@Test
	public void testBehaviorCharacterization() {
		assertArrayEquals(binLabels.behaviorCharacterization(allMax), new double[] {25.6, 25.6}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(allMin), new double[] {-25.6, -25.6}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(doubleNums1), new double[] {11.23, -12.96}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(doubleNums2), new double[] {4.44, -6.45}, 0.00000001);
	}

	@Test
	public void testBinLabels() {
		List<String> stringLabels = binLabels.binLabels();
		assertEquals(stringLabels.get(0), "-25_6to-20_48--25_6to-20_48");
		assertEquals(stringLabels.get(1), "-20_48to-15_36--25_6to-20_48");
		assertEquals(stringLabels.get(2), "-15_36to-10_24--25_6to-20_48");
		assertEquals(stringLabels.get(3), "-10_24to-5_12--25_6to-20_48");
		assertEquals(stringLabels.get(4), "-5_12to0_0--25_6to-20_48");
		assertEquals(stringLabels.get(5), "0_0to5_12--25_6to-20_48");
		assertEquals(stringLabels.get(6), "5_12to10_24--25_6to-20_48");
		assertEquals(stringLabels.get(7), "10_24to15_36--25_6to-20_48");
		assertEquals(stringLabels.get(8), "15_36to20_48--25_6to-20_48");
		assertEquals(stringLabels.get(9), "20_48to25_6--25_6to-20_48");
		assertEquals(stringLabels.get(10), "-25_6to-20_48--20_48to-15_36");
		assertEquals(stringLabels.get(11), "-20_48to-15_36--20_48to-15_36");
		assertEquals(stringLabels.get(12), "-15_36to-10_24--20_48to-15_36");
		assertEquals(stringLabels.get(13), "-10_24to-5_12--20_48to-15_36");
		assertEquals(stringLabels.get(14), "-5_12to0_0--20_48to-15_36");
		assertEquals(stringLabels.get(15), "0_0to5_12--20_48to-15_36");
		assertEquals(stringLabels.get(16), "5_12to10_24--20_48to-15_36");
		assertEquals(stringLabels.get(17), "10_24to15_36--20_48to-15_36");
		assertEquals(stringLabels.get(18), "15_36to20_48--20_48to-15_36");
		assertEquals(stringLabels.get(19), "20_48to25_6--20_48to-15_36");
		assertEquals(stringLabels.get(20), "-25_6to-20_48--15_36to-10_24");
		assertEquals(stringLabels.get(21), "-20_48to-15_36--15_36to-10_24");
		assertEquals(stringLabels.get(22), "-15_36to-10_24--15_36to-10_24");
		assertEquals(stringLabels.get(23), "-10_24to-5_12--15_36to-10_24");
		assertEquals(stringLabels.get(24), "-5_12to0_0--15_36to-10_24");
		assertEquals(stringLabels.get(25), "0_0to5_12--15_36to-10_24");
		assertEquals(stringLabels.get(26), "5_12to10_24--15_36to-10_24");
		assertEquals(stringLabels.get(27), "10_24to15_36--15_36to-10_24");
		assertEquals(stringLabels.get(28), "15_36to20_48--15_36to-10_24");
		assertEquals(stringLabels.get(29), "20_48to25_6--15_36to-10_24");
		assertEquals(stringLabels.get(30), "-25_6to-20_48--10_24to-5_12");
		assertEquals(stringLabels.get(31), "-20_48to-15_36--10_24to-5_12");
		assertEquals(stringLabels.get(32), "-15_36to-10_24--10_24to-5_12");
		assertEquals(stringLabels.get(33), "-10_24to-5_12--10_24to-5_12");
		assertEquals(stringLabels.get(34), "-5_12to0_0--10_24to-5_12");
		assertEquals(stringLabels.get(35), "0_0to5_12--10_24to-5_12");
		assertEquals(stringLabels.get(36), "5_12to10_24--10_24to-5_12");
		assertEquals(stringLabels.get(37), "10_24to15_36--10_24to-5_12");
		assertEquals(stringLabels.get(38), "15_36to20_48--10_24to-5_12");
		assertEquals(stringLabels.get(39), "20_48to25_6--10_24to-5_12");
		assertEquals(stringLabels.get(40), "-25_6to-20_48--5_12to0_0");
		assertEquals(stringLabels.get(41), "-20_48to-15_36--5_12to0_0");
		assertEquals(stringLabels.get(42), "-15_36to-10_24--5_12to0_0");
		assertEquals(stringLabels.get(43), "-10_24to-5_12--5_12to0_0");
		assertEquals(stringLabels.get(44), "-5_12to0_0--5_12to0_0");
		assertEquals(stringLabels.get(45), "0_0to5_12--5_12to0_0");
		assertEquals(stringLabels.get(46), "5_12to10_24--5_12to0_0");
		assertEquals(stringLabels.get(47), "10_24to15_36--5_12to0_0");
		assertEquals(stringLabels.get(48), "15_36to20_48--5_12to0_0");
		assertEquals(stringLabels.get(49), "20_48to25_6--5_12to0_0");
		assertEquals(stringLabels.get(50), "-25_6to-20_48-0_0to5_12");
		assertEquals(stringLabels.get(51), "-20_48to-15_36-0_0to5_12");
		assertEquals(stringLabels.get(52), "-15_36to-10_24-0_0to5_12");
		assertEquals(stringLabels.get(53), "-10_24to-5_12-0_0to5_12");
		assertEquals(stringLabels.get(54), "-5_12to0_0-0_0to5_12");
		assertEquals(stringLabels.get(55), "0_0to5_12-0_0to5_12");
		assertEquals(stringLabels.get(56), "5_12to10_24-0_0to5_12");
		assertEquals(stringLabels.get(57), "10_24to15_36-0_0to5_12");
		assertEquals(stringLabels.get(58), "15_36to20_48-0_0to5_12");
		assertEquals(stringLabels.get(59), "20_48to25_6-0_0to5_12");
		assertEquals(stringLabels.get(60), "-25_6to-20_48-5_12to10_24");
		assertEquals(stringLabels.get(61), "-20_48to-15_36-5_12to10_24");
		assertEquals(stringLabels.get(62), "-15_36to-10_24-5_12to10_24");
		assertEquals(stringLabels.get(63), "-10_24to-5_12-5_12to10_24");
		assertEquals(stringLabels.get(64), "-5_12to0_0-5_12to10_24");
		assertEquals(stringLabels.get(65), "0_0to5_12-5_12to10_24");
		assertEquals(stringLabels.get(66), "5_12to10_24-5_12to10_24");
		assertEquals(stringLabels.get(67), "10_24to15_36-5_12to10_24");
		assertEquals(stringLabels.get(68), "15_36to20_48-5_12to10_24");
		assertEquals(stringLabels.get(69), "20_48to25_6-5_12to10_24");
		assertEquals(stringLabels.get(70), "-25_6to-20_48-10_24to15_36");
		assertEquals(stringLabels.get(71), "-20_48to-15_36-10_24to15_36");
		assertEquals(stringLabels.get(72), "-15_36to-10_24-10_24to15_36");
		assertEquals(stringLabels.get(73), "-10_24to-5_12-10_24to15_36");
		assertEquals(stringLabels.get(74), "-5_12to0_0-10_24to15_36");
		assertEquals(stringLabels.get(75), "0_0to5_12-10_24to15_36");
		assertEquals(stringLabels.get(76), "5_12to10_24-10_24to15_36");
		assertEquals(stringLabels.get(77), "10_24to15_36-10_24to15_36");
		assertEquals(stringLabels.get(78), "15_36to20_48-10_24to15_36");
		assertEquals(stringLabels.get(79), "20_48to25_6-10_24to15_36");
		assertEquals(stringLabels.get(80), "-25_6to-20_48-15_36to20_48");
		assertEquals(stringLabels.get(81), "-20_48to-15_36-15_36to20_48");
		assertEquals(stringLabels.get(82), "-15_36to-10_24-15_36to20_48");
		assertEquals(stringLabels.get(83), "-10_24to-5_12-15_36to20_48");
		assertEquals(stringLabels.get(84), "-5_12to0_0-15_36to20_48");
		assertEquals(stringLabels.get(85), "0_0to5_12-15_36to20_48");
		assertEquals(stringLabels.get(86), "5_12to10_24-15_36to20_48");
		assertEquals(stringLabels.get(87), "10_24to15_36-15_36to20_48");
		assertEquals(stringLabels.get(88), "15_36to20_48-15_36to20_48");
		assertEquals(stringLabels.get(89), "20_48to25_6-15_36to20_48");
		assertEquals(stringLabels.get(90), "-25_6to-20_48-20_48to25_6");
		assertEquals(stringLabels.get(91), "-20_48to-15_36-20_48to25_6");
		assertEquals(stringLabels.get(92), "-15_36to-10_24-20_48to25_6");
		assertEquals(stringLabels.get(93), "-10_24to-5_12-20_48to25_6");
		assertEquals(stringLabels.get(94), "-5_12to0_0-20_48to25_6");
		assertEquals(stringLabels.get(95), "0_0to5_12-20_48to25_6");
		assertEquals(stringLabels.get(96), "5_12to10_24-20_48to25_6");
		assertEquals(stringLabels.get(97), "10_24to15_36-20_48to25_6");
		assertEquals(stringLabels.get(98), "15_36to20_48-20_48to25_6");
		assertEquals(stringLabels.get(99), "20_48to25_6-20_48to25_6");
	}

	@Test
	public void testOneDimensionalIndex() {
		assertEquals(binLabels.oneDimensionalIndex(new int[] {9, 9}), 99);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {0, 0}), 0);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {9, 0}), 9);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {0, 9}), 90);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {5, 1}), 15);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {7, 3}), 37);
	}

	@Test
	public void testDiscretize() {
		assertArrayEquals(binLabels.discretize(new double[] {25.6, 25.6}), new int[] {9, 9});
		assertArrayEquals(binLabels.discretize(new double[] {-25.6, -25.6}), new int[] {0, 0}); 
		assertArrayEquals(binLabels.discretize(new double[] {11.23, -12.96}), new int[] {7, 2}); // floor ( ( ( <value> + 25.6 ) / 51.2 ) * 10 )
		assertArrayEquals(binLabels.discretize(new double[] {4.44, -6.45}), new int[] {5, 3}); // above operation shows how calculation should work
	}	
	
	@Test
	public void testMultiDimensionalIndexToBinLabel() {
		assertEquals("10_24to15_36--10_24to-5_12", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {10.4, -6}))));
		assertEquals("-25_6to-20_48-20_48to25_6", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-23.1, 20.48}))));
		assertEquals("-15_36to-10_24--5_12to0_0", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-13.72, -2.24}))));
		assertEquals("0_0to5_12-0_0to5_12", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {0, 5.11}))));
		assertEquals("-25_6to-20_48--25_6to-20_48", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-25.6, -25.6}))));
		assertEquals("20_48to25_6-20_48to25_6", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {25.6, 25.6}))));
	}
}
