package edu.southwestern.tasks.functionoptimization;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;

public class FunctionOptimizationRangeBinLabelsBatchTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] params = "foBinDimension:500 solutionVectorSlices:2 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12 mapElitesQDBaseOffset:525 io:false netio:false ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels".split(" ");
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
		allMax = ArrayUtil.doubleSpecified(20, 5.12);
		allMin = ArrayUtil.doubleSpecified(20, -5.12);
		doubleNums1 = new double[] {1.3, 2.6, 5.12, -1.0, 3.21, -5.0, -2.32, 1.12, -1.64, -5.12, -2.34, 1.12, -0.01, 0.0, 4.88, 3.33, 1.01, 2.21, -5.0, -3.21};
		doubleNums2 = new double[] {1.01, 5.12, 3.43, -5.12, 0.0, -0.1, 2.1, -1.1, -3.18, -4.17, -4.31, 1.0, 3.03, -5.12, 1.11, 2.14, 0.51, -5.01, 4.33, 2.99};
	}

	@Test
	public void testBehaviorCharacterization() {
		assertArrayEquals(binLabels.behaviorCharacterization(allMax), new double[] {51.2, 51.2}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(allMin), new double[] {-51.2, -51.2}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(doubleNums1), new double[] {-1.73, 1.99}, 0.00000001);
		assertArrayEquals(binLabels.behaviorCharacterization(doubleNums2), new double[] {-2.01, 0.67}, 0.00000001);
	}

	@Test
	public void testBinLabels() {
		List<String> stringLabels = binLabels.binLabels();
		assertEquals(stringLabels.get(0), "-51_2to-50_9952--51_2to-50_9952");
	}

	@Test
	public void testOneDimensionalIndex() {
		assertEquals(binLabels.oneDimensionalIndex(new int[] {499, 499}), 249999);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {0, 0}), 0);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {499, 0}), 249500);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {0, 499}), 499);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {56, 11}), 28011);
		assertEquals(binLabels.oneDimensionalIndex(new int[] {73, 308}), 36808);
	}

	@Test
	public void testDiscretize() {
		assertArrayEquals(binLabels.discretize(new double[] {51.2, 51.2}), new int[] {499, 499});
		assertArrayEquals(binLabels.discretize(new double[] {-51.2, -51.2}), new int[] {0, 0}); 
		assertArrayEquals(binLabels.discretize(new double[] {11.23, -12.96}), new int[] {304, 186});
		assertArrayEquals(binLabels.discretize(new double[] {4.44, -6.45}), new int[] {271, 218});
	}	
	
	@Test
	public void testMultiDimensionalIndexToBinLabel() {
		assertEquals("-19_2512to-19_0464-34_816to35_0208", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {34.9, -19.1}))));
		assertEquals("0_0to0_2048-0_0to0_2048", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {0, 0.2}))));
		assertEquals("-51_2to-50_9952--51_2to-50_9952", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {-51.2, -51.2}))));
		assertEquals("50_9952to51_2-50_9952to51_2", binLabels.binLabels().get(binLabels.oneDimensionalIndex(binLabels.discretize(new double[] {51.2, 51.2}))));
	}
}
