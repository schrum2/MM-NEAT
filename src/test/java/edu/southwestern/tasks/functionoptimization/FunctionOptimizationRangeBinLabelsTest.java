package edu.southwestern.tasks.functionoptimization;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class FunctionOptimizationRangeBinLabelsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] params = "foBinDimension:10 solutionVectorSlices:2 foVectorLength:10 foUpperBounds:5.12 foLowerBounds:-5.12 numImprovementEmitters:2 numOptimizingEmitters:0 solutionVectorSlices:2 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-2 saveTo:2 netio:false lambda:37 maxGens:5000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels".split(" ");
		Parameters.initializeParameterCollections(params);
		MMNEAT.loadClasses();
	}
	
	static final double INTERVAL = 5.12;
	FunctionOptimizationRangeBinLabels binLabels;
	double[] allMax = new double[] {5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12, 5.12};
	double[] allMin = new double[] {-5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12, -5.12};
	double[] doubleNums1 = new double[] {1.3, 2.6, 5.12, -1.0, 3.21, -5.0, -2.32, 1.12, -1.64, -5.12};
	double[] doubleNums2 = new double[] {1.01, 5.12, 3.43, -5.12, 0.0, -0.1, 2.1, -1.1, -3.18, -4.17};
	
	@Before
	public void setUp() throws Exception {
		binLabels = new FunctionOptimizationRangeBinLabels();
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
	
//		int counter = 0;
//		for (double i = -5; i < 5; i++) {
//			for (double j = -5; j < 5; j++) {
//				String newLabel = "(["+j*INTERVAL+" to "+(j+1)*INTERVAL+"], ["+i*INTERVAL+" to "+(i+1)*INTERVAL+"])";
//				System.out.println("assertEquals(stringLabels.get("+counter+"), \""+newLabel+"\");");
//				counter++;
//			}
//		}
		
		assertEquals(stringLabels.get(0), "([-25.6 to -20.48], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(1), "([-20.48 to -15.36], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(2), "([-15.36 to -10.24], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(3), "([-10.24 to -5.12], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(4), "([-5.12 to 0.0], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(5), "([0.0 to 5.12], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(6), "([5.12 to 10.24], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(7), "([10.24 to 15.36], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(8), "([15.36 to 20.48], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(9), "([20.48 to 25.6], [-25.6 to -20.48])");
		assertEquals(stringLabels.get(10), "([-25.6 to -20.48], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(11), "([-20.48 to -15.36], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(12), "([-15.36 to -10.24], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(13), "([-10.24 to -5.12], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(14), "([-5.12 to 0.0], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(15), "([0.0 to 5.12], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(16), "([5.12 to 10.24], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(17), "([10.24 to 15.36], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(18), "([15.36 to 20.48], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(19), "([20.48 to 25.6], [-20.48 to -15.36])");
		assertEquals(stringLabels.get(20), "([-25.6 to -20.48], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(21), "([-20.48 to -15.36], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(22), "([-15.36 to -10.24], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(23), "([-10.24 to -5.12], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(24), "([-5.12 to 0.0], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(25), "([0.0 to 5.12], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(26), "([5.12 to 10.24], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(27), "([10.24 to 15.36], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(28), "([15.36 to 20.48], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(29), "([20.48 to 25.6], [-15.36 to -10.24])");
		assertEquals(stringLabels.get(30), "([-25.6 to -20.48], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(31), "([-20.48 to -15.36], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(32), "([-15.36 to -10.24], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(33), "([-10.24 to -5.12], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(34), "([-5.12 to 0.0], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(35), "([0.0 to 5.12], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(36), "([5.12 to 10.24], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(37), "([10.24 to 15.36], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(38), "([15.36 to 20.48], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(39), "([20.48 to 25.6], [-10.24 to -5.12])");
		assertEquals(stringLabels.get(40), "([-25.6 to -20.48], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(41), "([-20.48 to -15.36], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(42), "([-15.36 to -10.24], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(43), "([-10.24 to -5.12], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(44), "([-5.12 to 0.0], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(45), "([0.0 to 5.12], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(46), "([5.12 to 10.24], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(47), "([10.24 to 15.36], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(48), "([15.36 to 20.48], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(49), "([20.48 to 25.6], [-5.12 to 0.0])");
		assertEquals(stringLabels.get(50), "([-25.6 to -20.48], [0.0 to 5.12])");
		assertEquals(stringLabels.get(51), "([-20.48 to -15.36], [0.0 to 5.12])");
		assertEquals(stringLabels.get(52), "([-15.36 to -10.24], [0.0 to 5.12])");
		assertEquals(stringLabels.get(53), "([-10.24 to -5.12], [0.0 to 5.12])");
		assertEquals(stringLabels.get(54), "([-5.12 to 0.0], [0.0 to 5.12])");
		assertEquals(stringLabels.get(55), "([0.0 to 5.12], [0.0 to 5.12])");
		assertEquals(stringLabels.get(56), "([5.12 to 10.24], [0.0 to 5.12])");
		assertEquals(stringLabels.get(57), "([10.24 to 15.36], [0.0 to 5.12])");
		assertEquals(stringLabels.get(58), "([15.36 to 20.48], [0.0 to 5.12])");
		assertEquals(stringLabels.get(59), "([20.48 to 25.6], [0.0 to 5.12])");
		assertEquals(stringLabels.get(60), "([-25.6 to -20.48], [5.12 to 10.24])");
		assertEquals(stringLabels.get(61), "([-20.48 to -15.36], [5.12 to 10.24])");
		assertEquals(stringLabels.get(62), "([-15.36 to -10.24], [5.12 to 10.24])");
		assertEquals(stringLabels.get(63), "([-10.24 to -5.12], [5.12 to 10.24])");
		assertEquals(stringLabels.get(64), "([-5.12 to 0.0], [5.12 to 10.24])");
		assertEquals(stringLabels.get(65), "([0.0 to 5.12], [5.12 to 10.24])");
		assertEquals(stringLabels.get(66), "([5.12 to 10.24], [5.12 to 10.24])");
		assertEquals(stringLabels.get(67), "([10.24 to 15.36], [5.12 to 10.24])");
		assertEquals(stringLabels.get(68), "([15.36 to 20.48], [5.12 to 10.24])");
		assertEquals(stringLabels.get(69), "([20.48 to 25.6], [5.12 to 10.24])");
		assertEquals(stringLabels.get(70), "([-25.6 to -20.48], [10.24 to 15.36])");
		assertEquals(stringLabels.get(71), "([-20.48 to -15.36], [10.24 to 15.36])");
		assertEquals(stringLabels.get(72), "([-15.36 to -10.24], [10.24 to 15.36])");
		assertEquals(stringLabels.get(73), "([-10.24 to -5.12], [10.24 to 15.36])");
		assertEquals(stringLabels.get(74), "([-5.12 to 0.0], [10.24 to 15.36])");
		assertEquals(stringLabels.get(75), "([0.0 to 5.12], [10.24 to 15.36])");
		assertEquals(stringLabels.get(76), "([5.12 to 10.24], [10.24 to 15.36])");
		assertEquals(stringLabels.get(77), "([10.24 to 15.36], [10.24 to 15.36])");
		assertEquals(stringLabels.get(78), "([15.36 to 20.48], [10.24 to 15.36])");
		assertEquals(stringLabels.get(79), "([20.48 to 25.6], [10.24 to 15.36])");
		assertEquals(stringLabels.get(80), "([-25.6 to -20.48], [15.36 to 20.48])");
		assertEquals(stringLabels.get(81), "([-20.48 to -15.36], [15.36 to 20.48])");
		assertEquals(stringLabels.get(82), "([-15.36 to -10.24], [15.36 to 20.48])");
		assertEquals(stringLabels.get(83), "([-10.24 to -5.12], [15.36 to 20.48])");
		assertEquals(stringLabels.get(84), "([-5.12 to 0.0], [15.36 to 20.48])");
		assertEquals(stringLabels.get(85), "([0.0 to 5.12], [15.36 to 20.48])");
		assertEquals(stringLabels.get(86), "([5.12 to 10.24], [15.36 to 20.48])");
		assertEquals(stringLabels.get(87), "([10.24 to 15.36], [15.36 to 20.48])");
		assertEquals(stringLabels.get(88), "([15.36 to 20.48], [15.36 to 20.48])");
		assertEquals(stringLabels.get(89), "([20.48 to 25.6], [15.36 to 20.48])");
		assertEquals(stringLabels.get(90), "([-25.6 to -20.48], [20.48 to 25.6])");
		assertEquals(stringLabels.get(91), "([-20.48 to -15.36], [20.48 to 25.6])");
		assertEquals(stringLabels.get(92), "([-15.36 to -10.24], [20.48 to 25.6])");
		assertEquals(stringLabels.get(93), "([-10.24 to -5.12], [20.48 to 25.6])");
		assertEquals(stringLabels.get(94), "([-5.12 to 0.0], [20.48 to 25.6])");
		assertEquals(stringLabels.get(95), "([0.0 to 5.12], [20.48 to 25.6])");
		assertEquals(stringLabels.get(96), "([5.12 to 10.24], [20.48 to 25.6])");
		assertEquals(stringLabels.get(97), "([10.24 to 15.36], [20.48 to 25.6])");
		assertEquals(stringLabels.get(98), "([15.36 to 20.48], [20.48 to 25.6])");
		assertEquals(stringLabels.get(99), "([20.48 to 25.6], [20.48 to 25.6])");
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
		assertEquals("([10.24 to 15.36], [-10.24 to -5.12])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {10.4, -6}))));
		assertEquals("([-25.6 to -20.48], [20.48 to 25.6])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-23.1, 20.48}))));
		assertEquals("([-15.36 to -10.24], [-5.12 to 0.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-13.72, -2.24}))));
		assertEquals("([0.0 to 5.12], [0.0 to 5.12])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {0, 5.11}))));
		assertEquals("([-25.6 to -20.48], [-25.6 to -20.48])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-25.6, -25.6}))));
		assertEquals("([20.48 to 25.6], [20.48 to 25.6])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {25.6, 25.6}))));
		
	}
}
