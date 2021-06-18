package edu.southwestern.evolution.mapelites.generalmappings;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;

public class LatentVariablePartitionSumBinLabelsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception { // latent vector size is 80
		Parameters.parameters = null;
		MMNEAT.clearClasses();
		String[] params = "solutionVectorSlices:2 latentPartitionBinDimension:10 randomSeed:1 mu:1 experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 megaManGANLevelChunks:10 maxGens:1 io:false netio:false GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:true useMultipleGANsMegaMan:true cleanFrequency:-1 recurrency:false saveAllChampions:true ea:edu.southwestern.evolution.mapelites.MAPElites mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype".split(" ");
		Parameters.initializeParameterCollections(params);
		MMNEAT.loadClasses();
	}

	static final double INTERVAL = 80/10;
	
	LatentVariablePartitionSumBinLabels binLabels;
	double[] allMax = ArrayUtil.doubleOnes(80);
	double[] allMin = ArrayUtil.doubleNegativeOnes(80);
	double[] doubleNums1 = new double[] {0.46, 0.83, 0.73, -0.76, 0.13, 0.65, 0.32, 0.39, -0.11, -0.71, 0.09, 0.39, 0.11, 0.09, 0.52, 0.53, 0.75, 0.31, -0.32, -0.04, 0.05, 0.75, 0.28, 0.14, 0.28, 0.14, 0.60, 0.83, 0.64, 0.34, 0.61, 0.36, 0.91, 0.66, 0.26, 0.83, 0.33, 0.52, 0.62, 0.46, 0.66, 0.49, 0.84, 0.39, 0.90, 0.90, 0.95, 0.69, 1.00, 0.18, 0.76, 0.37, 0.20, 0.49, 0.94, 0.41, 0.22, 0.83, 0.07, 0.66, -0.73, -0.68, -0.40, 0.01, 0.64, 0.11, 0.84, 0.21, 0.47, 0.71, 0.09, -0.99, -0.82, -0.56, 0.92, 0.08, 0.61, 0.98, -0.90, 0.95};
	double[] doubleNums2 = new double[] {-0.04, 0.58, -0.69, 0.63, -0.99, -0.55, 0.76, -0.53, 0.65, 0.29, 0.11, -0.99, -0.90, -1.00, 0.83, 0.08, 0.33, 0.50, 0.15, 0.86, 0.57, 0.69, 0.88, 0.87, 0.83, 0.20, -0.68, 0.12, -0.76, 0.82, 0.88, 0.10, 0.66, -0.99, -0.15, 0.71, 0.60, 0.66, 0.22, 0.81, 0.79, 0.06, 0.59, 0.98, 0.40, 0.79, 0.31, 0.30, 0.38, 0.60, 0.29, 0.30, 0.65, 0.14, 0.10, 0.07, 0.37, 0.75, 0.38, 0.83, 0.44, 0.84, 0.51, 0.95, -0.72, 0.00, 0.86, 0.42, 0.75, -0.71, 0.06, 0.83, 0.87, 1.00, 0.62, -0.18, -0.73, 0.80, -0.59, 0.56};
	
	@Before
	public void setUp() throws Exception {
		binLabels = new LatentVariablePartitionSumBinLabels();
	}
	
	@Test
	public void testBehaviorCharacterization() {
		assertArrayEquals(binLabels.behaviorCharacterization(allMax), new double[] {40, 40}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(allMin), new double[] {-40, -40}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(doubleNums1), new double[] {13.97, 13.49}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(doubleNums2), new double[] {7.12, 15.66}, 0.00000001);
		
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
		
		assertEquals(stringLabels.get(0), "([-40.0 to -32.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(1), "([-32.0 to -24.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(2), "([-24.0 to -16.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(3), "([-16.0 to -8.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(4), "([-8.0 to 0.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(5), "([0.0 to 8.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(6), "([8.0 to 16.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(7), "([16.0 to 24.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(8), "([24.0 to 32.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(9), "([32.0 to 40.0], [-40.0 to -32.0])");
		assertEquals(stringLabels.get(10), "([-40.0 to -32.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(11), "([-32.0 to -24.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(12), "([-24.0 to -16.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(13), "([-16.0 to -8.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(14), "([-8.0 to 0.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(15), "([0.0 to 8.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(16), "([8.0 to 16.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(17), "([16.0 to 24.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(18), "([24.0 to 32.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(19), "([32.0 to 40.0], [-32.0 to -24.0])");
		assertEquals(stringLabels.get(20), "([-40.0 to -32.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(21), "([-32.0 to -24.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(22), "([-24.0 to -16.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(23), "([-16.0 to -8.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(24), "([-8.0 to 0.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(25), "([0.0 to 8.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(26), "([8.0 to 16.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(27), "([16.0 to 24.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(28), "([24.0 to 32.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(29), "([32.0 to 40.0], [-24.0 to -16.0])");
		assertEquals(stringLabels.get(30), "([-40.0 to -32.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(31), "([-32.0 to -24.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(32), "([-24.0 to -16.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(33), "([-16.0 to -8.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(34), "([-8.0 to 0.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(35), "([0.0 to 8.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(36), "([8.0 to 16.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(37), "([16.0 to 24.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(38), "([24.0 to 32.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(39), "([32.0 to 40.0], [-16.0 to -8.0])");
		assertEquals(stringLabels.get(40), "([-40.0 to -32.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(41), "([-32.0 to -24.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(42), "([-24.0 to -16.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(43), "([-16.0 to -8.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(44), "([-8.0 to 0.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(45), "([0.0 to 8.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(46), "([8.0 to 16.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(47), "([16.0 to 24.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(48), "([24.0 to 32.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(49), "([32.0 to 40.0], [-8.0 to 0.0])");
		assertEquals(stringLabels.get(50), "([-40.0 to -32.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(51), "([-32.0 to -24.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(52), "([-24.0 to -16.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(53), "([-16.0 to -8.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(54), "([-8.0 to 0.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(55), "([0.0 to 8.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(56), "([8.0 to 16.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(57), "([16.0 to 24.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(58), "([24.0 to 32.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(59), "([32.0 to 40.0], [0.0 to 8.0])");
		assertEquals(stringLabels.get(60), "([-40.0 to -32.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(61), "([-32.0 to -24.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(62), "([-24.0 to -16.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(63), "([-16.0 to -8.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(64), "([-8.0 to 0.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(65), "([0.0 to 8.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(66), "([8.0 to 16.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(67), "([16.0 to 24.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(68), "([24.0 to 32.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(69), "([32.0 to 40.0], [8.0 to 16.0])");
		assertEquals(stringLabels.get(70), "([-40.0 to -32.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(71), "([-32.0 to -24.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(72), "([-24.0 to -16.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(73), "([-16.0 to -8.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(74), "([-8.0 to 0.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(75), "([0.0 to 8.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(76), "([8.0 to 16.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(77), "([16.0 to 24.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(78), "([24.0 to 32.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(79), "([32.0 to 40.0], [16.0 to 24.0])");
		assertEquals(stringLabels.get(80), "([-40.0 to -32.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(81), "([-32.0 to -24.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(82), "([-24.0 to -16.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(83), "([-16.0 to -8.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(84), "([-8.0 to 0.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(85), "([0.0 to 8.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(86), "([8.0 to 16.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(87), "([16.0 to 24.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(88), "([24.0 to 32.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(89), "([32.0 to 40.0], [24.0 to 32.0])");
		assertEquals(stringLabels.get(90), "([-40.0 to -32.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(91), "([-32.0 to -24.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(92), "([-24.0 to -16.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(93), "([-16.0 to -8.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(94), "([-8.0 to 0.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(95), "([0.0 to 8.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(96), "([8.0 to 16.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(97), "([16.0 to 24.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(98), "([24.0 to 32.0], [32.0 to 40.0])");
		assertEquals(stringLabels.get(99), "([32.0 to 40.0], [32.0 to 40.0])");
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
		assertArrayEquals(binLabels.discretize(new double[] {40, 40}), new int[] {9, 9});
		assertArrayEquals(binLabels.discretize(new double[] {-40, -40}), new int[] {0, 0}); 
		assertArrayEquals(binLabels.discretize(new double[] {13.97, 13.49}), new int[] {6, 6}); // floor ( ( ( <value> + 40 ) / 80 ) * 10 )
		assertArrayEquals(binLabels.discretize(new double[] {7.12, -15.66}), new int[] {5, 3}); // above operation shows how calculation should work
	}
	
	@Test
	public void testMultiDimensionalIndexToBinLabel() {
		assertEquals("([-16.0 to -8.0], [-40.0 to -32.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-9.33, -36.0}))));
		assertEquals("([-40.0 to -32.0], [32.0 to 40.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-40.0, 32.0}))));
		assertEquals("([-16.0 to -8.0], [16.0 to 24.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-15.5, 17.23}))));
		assertEquals("([0.0 to 8.0], [0.0 to 8.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {0.0, 7.99}))));
		assertEquals("([-40.0 to -32.0], [-40.0 to -32.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-40.0, -40.0}))));
		assertEquals("([32.0 to 40.0], [32.0 to 40.0])", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {40.0, 40.0}))));

	}
}
