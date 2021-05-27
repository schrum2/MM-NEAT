package edu.southwestern.evolution.mapelites.generalmappings;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class MultiDimensionalRealValuedBinLabelsTest {

	MultiDimensionalRealValuedBinLabels lab1;
	MultiDimensionalRealValuedBinLabels lab2;
	MultiDimensionalRealValuedBinLabels lab3;
	
	@Before
	public void setUp() throws Exception {
		lab1 = new MultiDimensionalRealValuedBinLabels(4, -1, 3, 1, 10);
		lab2 = new MultiDimensionalRealValuedBinLabels(4, -1, 3, 2, 10);
		lab3 = new MultiDimensionalRealValuedBinLabels(4, -1, 3, 3, 10);
	}

	@Test
	public void testBinLabels() {
		// 1D labels
		ArrayList<String> solutionLabels1 = new ArrayList<>();
		solutionLabels1.add("([-10.0 to 0.0])");
		solutionLabels1.add("([0.0 to 10.0])");
		solutionLabels1.add("([10.0 to 20.0])");
		solutionLabels1.add("([20.0 to 30.0])");
		
		// 2D labels
		ArrayList<String> solutionLabels2 = new ArrayList<>();
		solutionLabels2.add("([-10.0 to 0.0], [-10.0 to 0.0])");
		solutionLabels2.add("([0.0 to 10.0], [-10.0 to 0.0])");
		solutionLabels2.add("([10.0 to 20.0], [-10.0 to 0.0])");
		solutionLabels2.add("([20.0 to 30.0], [-10.0 to 0.0])");
		solutionLabels2.add("([-10.0 to 0.0], [0.0 to 10.0])");
		solutionLabels2.add("([0.0 to 10.0], [0.0 to 10.0])");
		solutionLabels2.add("([10.0 to 20.0], [0.0 to 10.0])");
		solutionLabels2.add("([20.0 to 30.0], [0.0 to 10.0])");
		solutionLabels2.add("([-10.0 to 0.0], [10.0 to 20.0])");
		solutionLabels2.add("([0.0 to 10.0], [10.0 to 20.0])");
		solutionLabels2.add("([10.0 to 20.0], [10.0 to 20.0])");
		solutionLabels2.add("([20.0 to 30.0], [10.0 to 20.0])");
		solutionLabels2.add("([-10.0 to 0.0], [20.0 to 30.0])");
		solutionLabels2.add("([0.0 to 10.0], [20.0 to 30.0])");
		solutionLabels2.add("([10.0 to 20.0], [20.0 to 30.0])");
		solutionLabels2.add("([20.0 to 30.0], [20.0 to 30.0])");
		
		// 3D labels
		ArrayList<String> solutionLabels3 = new ArrayList<>();
		solutionLabels3.add("([-10.0 to 0.0], [-10.0 to 0.0], [-10.0 to 0.0])");
		solutionLabels3.add("([0.0 to 10.0], [-10.0 to 0.0], [-10.0 to 0.0])");
		solutionLabels3.add("([10.0 to 20.0], [-10.0 to 0.0], [-10.0 to 0.0])");
		solutionLabels3.add("([20.0 to 30.0], [-10.0 to 0.0], [-10.0 to 0.0])");
		solutionLabels3.add("([-10.0 to 0.0], [0.0 to 10.0], [-10.0 to 0.0])");
		solutionLabels3.add("([0.0 to 10.0], [0.0 to 10.0], [-10.0 to 0.0])");
		solutionLabels3.add("([10.0 to 20.0], [0.0 to 10.0], [-10.0 to 0.0])");
		solutionLabels3.add("([20.0 to 30.0], [0.0 to 10.0], [-10.0 to 0.0])");
		solutionLabels3.add("([-10.0 to 0.0], [10.0 to 20.0], [-10.0 to 0.0])");
		solutionLabels3.add("([0.0 to 10.0], [10.0 to 20.0], [-10.0 to 0.0])");
		solutionLabels3.add("([10.0 to 20.0], [10.0 to 20.0], [-10.0 to 0.0])");
		solutionLabels3.add("([20.0 to 30.0], [10.0 to 20.0], [-10.0 to 0.0])");
		solutionLabels3.add("([-10.0 to 0.0], [20.0 to 30.0], [-10.0 to 0.0])");
		solutionLabels3.add("([0.0 to 10.0], [20.0 to 30.0], [-10.0 to 0.0])");
		solutionLabels3.add("([10.0 to 20.0], [20.0 to 30.0], [-10.0 to 0.0])");
		solutionLabels3.add("([20.0 to 30.0], [20.0 to 30.0], [-10.0 to 0.0])");
		solutionLabels3.add("([-10.0 to 0.0], [-10.0 to 0.0], [0.0 to 10.0])");
		solutionLabels3.add("([0.0 to 10.0], [-10.0 to 0.0], [0.0 to 10.0])");
		solutionLabels3.add("([10.0 to 20.0], [-10.0 to 0.0], [0.0 to 10.0])");
		solutionLabels3.add("([20.0 to 30.0], [-10.0 to 0.0], [0.0 to 10.0])");
		solutionLabels3.add("([-10.0 to 0.0], [0.0 to 10.0], [0.0 to 10.0])");
		solutionLabels3.add("([0.0 to 10.0], [0.0 to 10.0], [0.0 to 10.0])");
		solutionLabels3.add("([10.0 to 20.0], [0.0 to 10.0], [0.0 to 10.0])");
		solutionLabels3.add("([20.0 to 30.0], [0.0 to 10.0], [0.0 to 10.0])");
		solutionLabels3.add("([-10.0 to 0.0], [10.0 to 20.0], [0.0 to 10.0])");
		solutionLabels3.add("([0.0 to 10.0], [10.0 to 20.0], [0.0 to 10.0])");
		solutionLabels3.add("([10.0 to 20.0], [10.0 to 20.0], [0.0 to 10.0])");
		solutionLabels3.add("([20.0 to 30.0], [10.0 to 20.0], [0.0 to 10.0])");
		solutionLabels3.add("([-10.0 to 0.0], [20.0 to 30.0], [0.0 to 10.0])");
		solutionLabels3.add("([0.0 to 10.0], [20.0 to 30.0], [0.0 to 10.0])");
		solutionLabels3.add("([10.0 to 20.0], [20.0 to 30.0], [0.0 to 10.0])");
		solutionLabels3.add("([20.0 to 30.0], [20.0 to 30.0], [0.0 to 10.0])");
		solutionLabels3.add("([-10.0 to 0.0], [-10.0 to 0.0], [10.0 to 20.0])");
		solutionLabels3.add("([0.0 to 10.0], [-10.0 to 0.0], [10.0 to 20.0])");
		solutionLabels3.add("([10.0 to 20.0], [-10.0 to 0.0], [10.0 to 20.0])");
		solutionLabels3.add("([20.0 to 30.0], [-10.0 to 0.0], [10.0 to 20.0])");
		solutionLabels3.add("([-10.0 to 0.0], [0.0 to 10.0], [10.0 to 20.0])");
		solutionLabels3.add("([0.0 to 10.0], [0.0 to 10.0], [10.0 to 20.0])");
		solutionLabels3.add("([10.0 to 20.0], [0.0 to 10.0], [10.0 to 20.0])");
		solutionLabels3.add("([20.0 to 30.0], [0.0 to 10.0], [10.0 to 20.0])");
		solutionLabels3.add("([-10.0 to 0.0], [10.0 to 20.0], [10.0 to 20.0])");
		solutionLabels3.add("([0.0 to 10.0], [10.0 to 20.0], [10.0 to 20.0])");
		solutionLabels3.add("([10.0 to 20.0], [10.0 to 20.0], [10.0 to 20.0])");
		solutionLabels3.add("([20.0 to 30.0], [10.0 to 20.0], [10.0 to 20.0])");
		solutionLabels3.add("([-10.0 to 0.0], [20.0 to 30.0], [10.0 to 20.0])");
		solutionLabels3.add("([0.0 to 10.0], [20.0 to 30.0], [10.0 to 20.0])");
		solutionLabels3.add("([10.0 to 20.0], [20.0 to 30.0], [10.0 to 20.0])");
		solutionLabels3.add("([20.0 to 30.0], [20.0 to 30.0], [10.0 to 20.0])");
		solutionLabels3.add("([-10.0 to 0.0], [-10.0 to 0.0], [20.0 to 30.0])");
		solutionLabels3.add("([0.0 to 10.0], [-10.0 to 0.0], [20.0 to 30.0])");
		solutionLabels3.add("([10.0 to 20.0], [-10.0 to 0.0], [20.0 to 30.0])");
		solutionLabels3.add("([20.0 to 30.0], [-10.0 to 0.0], [20.0 to 30.0])");
		solutionLabels3.add("([-10.0 to 0.0], [0.0 to 10.0], [20.0 to 30.0])");
		solutionLabels3.add("([0.0 to 10.0], [0.0 to 10.0], [20.0 to 30.0])");
		solutionLabels3.add("([10.0 to 20.0], [0.0 to 10.0], [20.0 to 30.0])");
		solutionLabels3.add("([20.0 to 30.0], [0.0 to 10.0], [20.0 to 30.0])");
		solutionLabels3.add("([-10.0 to 0.0], [10.0 to 20.0], [20.0 to 30.0])");
		solutionLabels3.add("([0.0 to 10.0], [10.0 to 20.0], [20.0 to 30.0])");
		solutionLabels3.add("([10.0 to 20.0], [10.0 to 20.0], [20.0 to 30.0])");
		solutionLabels3.add("([20.0 to 30.0], [10.0 to 20.0], [20.0 to 30.0])");
		solutionLabels3.add("([-10.0 to 0.0], [20.0 to 30.0], [20.0 to 30.0])");
		solutionLabels3.add("([0.0 to 10.0], [20.0 to 30.0], [20.0 to 30.0])");
		solutionLabels3.add("([10.0 to 20.0], [20.0 to 30.0], [20.0 to 30.0])");
		solutionLabels3.add("([20.0 to 30.0], [20.0 to 30.0], [20.0 to 30.0])");

		assertEquals(solutionLabels1, lab1.binLabels());
		assertEquals(solutionLabels2, lab2.binLabels());
		assertEquals(solutionLabels3, lab3.binLabels());
	}

	
	@Test
	public void testDiscretize() {
		// 1D labels
		assertArrayEquals(new int[] {0}, lab1.discretize(new double[] {-10.0})); // lowest values
		assertArrayEquals(new int[] {0}, lab1.discretize(new double[] {-5.0}));
		assertArrayEquals(new int[] {1}, lab1.discretize(new double[] {0.0}));
		assertArrayEquals(new int[] {1}, lab1.discretize(new double[] {5.0}));
		assertArrayEquals(new int[] {2}, lab1.discretize(new double[] {10.0}));
		assertArrayEquals(new int[] {2}, lab1.discretize(new double[] {15.0}));
		assertArrayEquals(new int[] {3}, lab1.discretize(new double[] {20.0}));
		assertArrayEquals(new int[] {3}, lab1.discretize(new double[] {25.0}));
		assertArrayEquals(new int[] {3}, lab1.discretize(new double[] {30.0})); // highest values
		
		// 2D labels
		assertArrayEquals(new int[] {0, 0}, lab2.discretize(new double[] {-10.0, -10.0})); // lowest values
		assertArrayEquals(new int[] {0, 0}, lab2.discretize(new double[] {-5.0, -5.0}));
		assertArrayEquals(new int[] {1, 0}, lab2.discretize(new double[] {5.0, -5.0}));
		assertArrayEquals(new int[] {2, 0}, lab2.discretize(new double[] {15.0, -5.0}));
		assertArrayEquals(new int[] {3, 0}, lab2.discretize(new double[] {25.0, -5.0}));
		assertArrayEquals(new int[] {0, 1}, lab2.discretize(new double[] {-5.0, 5.0}));
		assertArrayEquals(new int[] {1, 1}, lab2.discretize(new double[] {5.0, 5.0}));
		assertArrayEquals(new int[] {2, 1}, lab2.discretize(new double[] {15.0, 5.0}));
		assertArrayEquals(new int[] {3, 1}, lab2.discretize(new double[] {25.0, 5.0}));
		assertArrayEquals(new int[] {0, 2}, lab2.discretize(new double[] {-5.0, 15.0}));
		assertArrayEquals(new int[] {1, 2}, lab2.discretize(new double[] {5.0, 15.0}));
		assertArrayEquals(new int[] {2, 2}, lab2.discretize(new double[] {15.0, 15.0}));
		assertArrayEquals(new int[] {3, 2}, lab2.discretize(new double[] {25.0, 15.0}));
		assertArrayEquals(new int[] {0, 3}, lab2.discretize(new double[] {-5.0, 25.0}));
		assertArrayEquals(new int[] {1, 3}, lab2.discretize(new double[] {5.0, 25.0}));
		assertArrayEquals(new int[] {2, 3}, lab2.discretize(new double[] {15.0, 25.0}));
		assertArrayEquals(new int[] {3, 3}, lab2.discretize(new double[] {25.0, 25.0}));
		assertArrayEquals(new int[] {3, 3}, lab2.discretize(new double[] {30.0, 30.0})); // highest values
		
		// 3D labels
		assertArrayEquals(new int[] {0, 0, 0}, lab3.discretize(new double[] {-10.0, -10.0, -10.0})); // lowest values
		assertArrayEquals(new int[] {0, 0, 0}, lab3.discretize(new double[] {-5.0, -5.0, -5.0}));
		assertArrayEquals(new int[] {1, 0, 0}, lab3.discretize(new double[] {5.0, -5.0, -5.0}));
		assertArrayEquals(new int[] {2, 0, 0}, lab3.discretize(new double[] {15.0, -5.0, -5.0}));
		assertArrayEquals(new int[] {3, 0, 0}, lab3.discretize(new double[] {25.0, -5.0, -5.0}));
		assertArrayEquals(new int[] {0, 1, 0}, lab3.discretize(new double[] {-5.0, 5.0, -5.0}));
		assertArrayEquals(new int[] {1, 1, 0}, lab3.discretize(new double[] {5.0, 5.0, -5.0}));
		assertArrayEquals(new int[] {2, 1, 0}, lab3.discretize(new double[] {15.0, 5.0, -5.0}));
		assertArrayEquals(new int[] {3, 1, 0}, lab3.discretize(new double[] {25.0, 5.0, -5.0}));
		assertArrayEquals(new int[] {0, 2, 0}, lab3.discretize(new double[] {-5.0, 15.0, -5.0}));
		assertArrayEquals(new int[] {1, 2, 0}, lab3.discretize(new double[] {5.0, 15.0, -5.0}));
		assertArrayEquals(new int[] {2, 2, 0}, lab3.discretize(new double[] {15.0, 15.0, -5.0}));
		assertArrayEquals(new int[] {3, 2, 0}, lab3.discretize(new double[] {25.0, 15.0, -5.0}));
		assertArrayEquals(new int[] {0, 3, 0}, lab3.discretize(new double[] {-5.0, 25.0, -5.0}));
		assertArrayEquals(new int[] {1, 3, 0}, lab3.discretize(new double[] {5.0, 25.0, -5.0}));
		assertArrayEquals(new int[] {2, 3, 0}, lab3.discretize(new double[] {15.0, 25.0, -5.0}));
		assertArrayEquals(new int[] {3, 3, 0}, lab3.discretize(new double[] {25.0, 25.0, -5.0}));
		assertArrayEquals(new int[] {0, 0, 1}, lab3.discretize(new double[] {-5.0, -5.0, 5.0}));
		assertArrayEquals(new int[] {1, 0, 1}, lab3.discretize(new double[] {5.0, -5.0, 5.0}));
		assertArrayEquals(new int[] {2, 0, 1}, lab3.discretize(new double[] {15.0, -5.0, 5.0}));
		assertArrayEquals(new int[] {3, 0, 1}, lab3.discretize(new double[] {25.0, -5.0, 5.0}));
		assertArrayEquals(new int[] {0, 1, 1}, lab3.discretize(new double[] {-5.0, 5.0, 5.0}));
		assertArrayEquals(new int[] {1, 1, 1}, lab3.discretize(new double[] {5.0, 5.0, 5.0}));
		assertArrayEquals(new int[] {2, 1, 1}, lab3.discretize(new double[] {15.0, 5.0, 5.0}));
		assertArrayEquals(new int[] {3, 1, 1}, lab3.discretize(new double[] {25.0, 5.0, 5.0}));
		assertArrayEquals(new int[] {0, 2, 1}, lab3.discretize(new double[] {-5.0, 15.0, 5.0}));
		assertArrayEquals(new int[] {1, 2, 1}, lab3.discretize(new double[] {5.0, 15.0, 5.0}));
		assertArrayEquals(new int[] {2, 2, 1}, lab3.discretize(new double[] {15.0, 15.0, 5.0}));
		assertArrayEquals(new int[] {3, 2, 1}, lab3.discretize(new double[] {25.0, 15.0, 5.0}));
		assertArrayEquals(new int[] {0, 3, 1}, lab3.discretize(new double[] {-5.0, 25.0, 5.0}));
		assertArrayEquals(new int[] {1, 3, 1}, lab3.discretize(new double[] {5.0, 25.0, 5.0}));
		assertArrayEquals(new int[] {2, 3, 1}, lab3.discretize(new double[] {15.0, 25.0, 5.0}));
		assertArrayEquals(new int[] {3, 3, 1}, lab3.discretize(new double[] {25.0, 25.0, 5.0}));
		assertArrayEquals(new int[] {0, 0, 2}, lab3.discretize(new double[] {-5.0, -5.0, 15.0}));
		assertArrayEquals(new int[] {1, 0, 2}, lab3.discretize(new double[] {5.0, -5.0, 15.0}));
		assertArrayEquals(new int[] {2, 0, 2}, lab3.discretize(new double[] {15.0, -5.0, 15.0}));
		assertArrayEquals(new int[] {3, 0, 2}, lab3.discretize(new double[] {25.0, -5.0, 15.0}));
		assertArrayEquals(new int[] {0, 1, 2}, lab3.discretize(new double[] {-5.0, 5.0, 15.0}));
		assertArrayEquals(new int[] {1, 1, 2}, lab3.discretize(new double[] {5.0, 5.0, 15.0}));
		assertArrayEquals(new int[] {2, 1, 2}, lab3.discretize(new double[] {15.0, 5.0, 15.0}));
		assertArrayEquals(new int[] {3, 1, 2}, lab3.discretize(new double[] {25.0, 5.0, 15.0}));
		assertArrayEquals(new int[] {0, 2, 2}, lab3.discretize(new double[] {-5.0, 15.0, 15.0}));
		assertArrayEquals(new int[] {1, 2, 2}, lab3.discretize(new double[] {5.0, 15.0, 15.0}));
		assertArrayEquals(new int[] {2, 2, 2}, lab3.discretize(new double[] {15.0, 15.0, 15.0}));
		assertArrayEquals(new int[] {3, 2, 2}, lab3.discretize(new double[] {25.0, 15.0, 15.0}));
		assertArrayEquals(new int[] {0, 3, 2}, lab3.discretize(new double[] {-5.0, 25.0, 15.0}));
		assertArrayEquals(new int[] {1, 3, 2}, lab3.discretize(new double[] {5.0, 25.0, 15.0}));
		assertArrayEquals(new int[] {2, 3, 2}, lab3.discretize(new double[] {15.0, 25.0, 15.0}));
		assertArrayEquals(new int[] {3, 3, 2}, lab3.discretize(new double[] {25.0, 25.0, 15.0}));
		assertArrayEquals(new int[] {0, 0, 3}, lab3.discretize(new double[] {-5.0, -5.0, 25.0}));
		assertArrayEquals(new int[] {1, 0, 3}, lab3.discretize(new double[] {5.0, -5.0, 25.0}));
		assertArrayEquals(new int[] {2, 0, 3}, lab3.discretize(new double[] {15.0, -5.0, 25.0}));
		assertArrayEquals(new int[] {3, 0, 3}, lab3.discretize(new double[] {25.0, -5.0, 25.0}));
		assertArrayEquals(new int[] {0, 1, 3}, lab3.discretize(new double[] {-5.0, 5.0, 25.0}));
		assertArrayEquals(new int[] {1, 1, 3}, lab3.discretize(new double[] {5.0, 5.0, 25.0}));
		assertArrayEquals(new int[] {2, 1, 3}, lab3.discretize(new double[] {15.0, 5.0, 25.0}));
		assertArrayEquals(new int[] {3, 1, 3}, lab3.discretize(new double[] {25.0, 5.0, 25.0}));
		assertArrayEquals(new int[] {0, 2, 3}, lab3.discretize(new double[] {-5.0, 15.0, 25.0}));
		assertArrayEquals(new int[] {1, 2, 3}, lab3.discretize(new double[] {5.0, 15.0, 25.0}));
		assertArrayEquals(new int[] {2, 2, 3}, lab3.discretize(new double[] {15.0, 15.0, 25.0}));
		assertArrayEquals(new int[] {3, 2, 3}, lab3.discretize(new double[] {25.0, 15.0, 25.0}));
		assertArrayEquals(new int[] {0, 3, 3}, lab3.discretize(new double[] {-5.0, 25.0, 25.0}));
		assertArrayEquals(new int[] {1, 3, 3}, lab3.discretize(new double[] {5.0, 25.0, 25.0}));
		assertArrayEquals(new int[] {2, 3, 3}, lab3.discretize(new double[] {15.0, 25.0, 25.0}));
		assertArrayEquals(new int[] {3, 3, 3}, lab3.discretize(new double[] {25.0, 25.0, 25.0}));
		assertArrayEquals(new int[] {3, 3, 3}, lab3.discretize(new double[] {30.0, 30.0, 30.0})); // highest values
	}
	
	
	@Test
	public void testOneDimensionalIndex() {
		// 1D labels
		assertEquals(0, lab1.oneDimensionalIndex(new int[] {0}));
		assertEquals(1, lab1.oneDimensionalIndex(new int[] {1}));
		assertEquals(2, lab1.oneDimensionalIndex(new int[] {2}));
		assertEquals(3, lab1.oneDimensionalIndex(new int[] {3}));
		
		// 2D labels
		assertEquals(0, lab2.oneDimensionalIndex(new int[] {0, 0}));
		assertEquals(1, lab2.oneDimensionalIndex(new int[] {1, 0}));
		assertEquals(2, lab2.oneDimensionalIndex(new int[] {2, 0}));
		assertEquals(3, lab2.oneDimensionalIndex(new int[] {3, 0}));
		assertEquals(4, lab2.oneDimensionalIndex(new int[] {0, 1}));
		assertEquals(5, lab2.oneDimensionalIndex(new int[] {1, 1}));
		assertEquals(6, lab2.oneDimensionalIndex(new int[] {2, 1}));
		assertEquals(7, lab2.oneDimensionalIndex(new int[] {3, 1}));
		assertEquals(8, lab2.oneDimensionalIndex(new int[] {0, 2}));
		assertEquals(9, lab2.oneDimensionalIndex(new int[] {1, 2}));
		assertEquals(10, lab2.oneDimensionalIndex(new int[] {2, 2}));
		assertEquals(11, lab2.oneDimensionalIndex(new int[] {3, 2}));
		assertEquals(12, lab2.oneDimensionalIndex(new int[] {0, 3}));
		assertEquals(13, lab2.oneDimensionalIndex(new int[] {1, 3}));
		assertEquals(14, lab2.oneDimensionalIndex(new int[] {2, 3}));
		assertEquals(15, lab2.oneDimensionalIndex(new int[] {3, 3}));
		
		// 3D labels
		assertEquals(0, lab3.oneDimensionalIndex(new int[] {0, 0, 0}));
		assertEquals(1, lab3.oneDimensionalIndex(new int[] {1, 0, 0}));
		assertEquals(2, lab3.oneDimensionalIndex(new int[] {2, 0, 0}));
		assertEquals(3, lab3.oneDimensionalIndex(new int[] {3, 0, 0}));
		assertEquals(4, lab3.oneDimensionalIndex(new int[] {0, 1, 0}));
		assertEquals(5, lab3.oneDimensionalIndex(new int[] {1, 1, 0}));
		assertEquals(6, lab3.oneDimensionalIndex(new int[] {2, 1, 0}));
		assertEquals(7, lab3.oneDimensionalIndex(new int[] {3, 1, 0}));
		assertEquals(8, lab3.oneDimensionalIndex(new int[] {0, 2, 0}));
		assertEquals(9, lab3.oneDimensionalIndex(new int[] {1, 2, 0}));
		assertEquals(10, lab3.oneDimensionalIndex(new int[] {2, 2, 0}));
		assertEquals(11, lab3.oneDimensionalIndex(new int[] {3, 2, 0}));
		assertEquals(12, lab3.oneDimensionalIndex(new int[] {0, 3, 0}));
		assertEquals(13, lab3.oneDimensionalIndex(new int[] {1, 3, 0}));
		assertEquals(14, lab3.oneDimensionalIndex(new int[] {2, 3, 0}));
		assertEquals(15, lab3.oneDimensionalIndex(new int[] {3, 3, 0}));
		assertEquals(16, lab3.oneDimensionalIndex(new int[] {0, 0, 1}));
		assertEquals(17, lab3.oneDimensionalIndex(new int[] {1, 0, 1}));
		assertEquals(18, lab3.oneDimensionalIndex(new int[] {2, 0, 1}));
		assertEquals(19, lab3.oneDimensionalIndex(new int[] {3, 0, 1}));
		assertEquals(20, lab3.oneDimensionalIndex(new int[] {0, 1, 1}));
		assertEquals(21, lab3.oneDimensionalIndex(new int[] {1, 1, 1}));
		assertEquals(22, lab3.oneDimensionalIndex(new int[] {2, 1, 1}));
		assertEquals(23, lab3.oneDimensionalIndex(new int[] {3, 1, 1}));
		assertEquals(24, lab3.oneDimensionalIndex(new int[] {0, 2, 1}));
		assertEquals(25, lab3.oneDimensionalIndex(new int[] {1, 2, 1}));
		assertEquals(26, lab3.oneDimensionalIndex(new int[] {2, 2, 1}));
		assertEquals(27, lab3.oneDimensionalIndex(new int[] {3, 2, 1}));
		assertEquals(28, lab3.oneDimensionalIndex(new int[] {0, 3, 1}));
		assertEquals(29, lab3.oneDimensionalIndex(new int[] {1, 3, 1}));
		assertEquals(30, lab3.oneDimensionalIndex(new int[] {2, 3, 1}));
		assertEquals(31, lab3.oneDimensionalIndex(new int[] {3, 3, 1}));
		assertEquals(32, lab3.oneDimensionalIndex(new int[] {0, 0, 2}));
		assertEquals(33, lab3.oneDimensionalIndex(new int[] {1, 0, 2}));
		assertEquals(34, lab3.oneDimensionalIndex(new int[] {2, 0, 2}));
		assertEquals(35, lab3.oneDimensionalIndex(new int[] {3, 0, 2}));
		assertEquals(36, lab3.oneDimensionalIndex(new int[] {0, 1, 2}));
		assertEquals(37, lab3.oneDimensionalIndex(new int[] {1, 1, 2}));
		assertEquals(38, lab3.oneDimensionalIndex(new int[] {2, 1, 2}));
		assertEquals(39, lab3.oneDimensionalIndex(new int[] {3, 1, 2}));
		assertEquals(40, lab3.oneDimensionalIndex(new int[] {0, 2, 2}));
		assertEquals(41, lab3.oneDimensionalIndex(new int[] {1, 2, 2}));
		assertEquals(42, lab3.oneDimensionalIndex(new int[] {2, 2, 2}));
		assertEquals(43, lab3.oneDimensionalIndex(new int[] {3, 2, 2}));
		assertEquals(44, lab3.oneDimensionalIndex(new int[] {0, 3, 2}));
		assertEquals(45, lab3.oneDimensionalIndex(new int[] {1, 3, 2}));
		assertEquals(46, lab3.oneDimensionalIndex(new int[] {2, 3, 2}));
		assertEquals(47, lab3.oneDimensionalIndex(new int[] {3, 3, 2}));
		assertEquals(48, lab3.oneDimensionalIndex(new int[] {0, 0, 3}));
		assertEquals(49, lab3.oneDimensionalIndex(new int[] {1, 0, 3}));
		assertEquals(50, lab3.oneDimensionalIndex(new int[] {2, 0, 3}));
		assertEquals(51, lab3.oneDimensionalIndex(new int[] {3, 0, 3}));
		assertEquals(52, lab3.oneDimensionalIndex(new int[] {0, 1, 3}));
		assertEquals(53, lab3.oneDimensionalIndex(new int[] {1, 1, 3}));
		assertEquals(54, lab3.oneDimensionalIndex(new int[] {2, 1, 3}));
		assertEquals(55, lab3.oneDimensionalIndex(new int[] {3, 1, 3}));
		assertEquals(56, lab3.oneDimensionalIndex(new int[] {0, 2, 3}));
		assertEquals(57, lab3.oneDimensionalIndex(new int[] {1, 2, 3}));
		assertEquals(58, lab3.oneDimensionalIndex(new int[] {2, 2, 3}));
		assertEquals(59, lab3.oneDimensionalIndex(new int[] {3, 2, 3}));
		assertEquals(60, lab3.oneDimensionalIndex(new int[] {0, 3, 3}));
		assertEquals(61, lab3.oneDimensionalIndex(new int[] {1, 3, 3}));
		assertEquals(62, lab3.oneDimensionalIndex(new int[] {2, 3, 3}));
		assertEquals(63, lab3.oneDimensionalIndex(new int[] {3, 3, 3}));
	}


}
