package edu.southwestern.util.datastructures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import edu.southwestern.MMNEAT.MMNEAT;

public class ArrayUtilTests {

	
	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void doubleArrayFromINDArray_test() {
		double[] test = new double[] {1,3,4.5,456.5,234,-23,4324,-34,0.00006,-0.4324};
		INDArray converted = Nd4j.create(test);
		assertArrayEquals(test,ArrayUtil.doubleArrayFromINDArray(converted),0.0001);
	}
	
	/**
	 * Tests that the given number of "1"s is returned in a double[]
	 */
	@Test
	public void doubleOnes_test() {
		int temp = 14;
		double[] result = ArrayUtil.doubleOnes(temp);
		double[] expected = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		assertArrayEquals(result, expected, 0.0);
	}

	@Test
	public void doubleArrayFrom2DdoubleArray_test()  {
		double[][] inputs = {{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}};
		double[] outputs = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		assertTrue(Arrays.equals(outputs, ArrayUtil.doubleArrayFrom2DdoubleArrayColMajor(inputs)));
		
	}
	
	@Test
	public void range_test()  {
		assertArrayEquals(new int[] {0,1,2,3,4,5}, ArrayUtil.range(0, 6, 1));
		assertArrayEquals(new int[] {3,4,5}, ArrayUtil.range(3, 6, 1));
		assertArrayEquals(new int[] {0,2,4}, ArrayUtil.range(0, 6, 2));
		assertArrayEquals(new int[] {0,3}, ArrayUtil.range(0, 6, 3));
		assertArrayEquals(new int[] {5,12,19,26,33,40}, ArrayUtil.range(5, 41, 7));
	}
	
	/**
	 * Tests that the given number of "1"s is returned in a int[]
	 */
	@Test
	public void intOnes_test() { 
		int temp = 14;
		int[] result = ArrayUtil.intOnes(temp);
		int[] expected = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		assertArrayEquals(result, expected);
	}

	/**
	 * Tests that an int[] is turned from a given ArrayList<Integer>
	 */
	@Test
	public void intArrayFromArrayList_test() { 
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(12);
		test.add(6);
		test.add(34);
		test.add(22);
		test.add(14);
		int[] result = ArrayUtil.intArrayFromArrayList(test);
		assertTrue(result instanceof int[]);
		assertTrue(test instanceof ArrayList<?>);
		for (int i = 0; i < result.length; i++) {
			assertEquals(test.get(i), result[i], 0.0);
		}
	}

	/**
	 * Tests that an double[] is turned from a given ArrayList<Double>
	 */
	@Test
	public void doubleArrayFromList_test() { 
		ArrayList<Double> test = new ArrayList<Double>();
		test.add(12.0);
		test.add(6.0);
		test.add(34.0);
		test.add(22.0);
		test.add(14.0);
		double[] result = ArrayUtil.doubleArrayFromList(test);
		assertTrue(result instanceof double[]);
		assertTrue(test instanceof ArrayList<?>);
		for (int i = 0; i < result.length; i++) {
			assertEquals(test.get(i), result[i], 0.0);
		}
	}

	/**
	 * Tests that the method correctly identifies members of a set
	 */
	@Test
	public void containsAny_test() { 
		int[] members = { 1, 2, 3, 4, 5 };
		int[] set1 = { 6, 7, 8, 9, 5 };
		int[] set2 = { 0, 9, 7 };
		assertTrue(members instanceof int[]);
		assertTrue(set1 instanceof int[]);
		assertTrue(set2 instanceof int[]);
		assertTrue(ArrayUtil.containsAny(members, set1));
		assertFalse(ArrayUtil.containsAny(members, set2));
	}

	/**
	 * Tests that the method returns an array with the first array's 
	 * elements followed by the second array's elements
	 */
	@Test
	public void combineArrays_int_test() { 
		int[] first = { 1, 1, 1, 1, 1 };
		int[] second = { 2, 2, 2, 2, 2 };
		int[] third = { 3, 3, 3 };

		assertTrue(ArrayUtil.combineArrays(first, second) instanceof int[]);
		int[] result1 = ArrayUtil.combineArrays(first, second);
		assertEquals(result1[0], 1);
		assertEquals(result1[result1.length - 1], 2);
		assertEquals(result1.length, first.length + second.length);

		assertTrue(ArrayUtil.combineArrays(first, third) instanceof int[]);
		int[] result2 = ArrayUtil.combineArrays(first, third);
		assertEquals(result2[0], 1);
		assertEquals(result2[result2.length - 1], 3);
		assertEquals(result2.length, first.length + third.length);
	}

	/**
	 * Tests that the method returns an array with the first array's 
	 * elements followed by the second array's elements
	 */
	@Test
	public void combineArrays_long_test() {
		long[] first = { 1, 1, 1, 1, 1 };
		long[] second = { 2, 2, 2, 2, 2 };
		long[] third = { 3, 3, 3 };

		assertTrue(ArrayUtil.combineArrays(first, second) instanceof long[]);
		long[] result1 = ArrayUtil.combineArrays(first, second);
		assertEquals(result1[0], 1);
		assertEquals(result1[result1.length - 1], 2);
		assertEquals(result1.length, first.length + second.length);

		assertTrue(ArrayUtil.combineArrays(first, third) instanceof long[]);
		long[] result2 = ArrayUtil.combineArrays(first, third);
		assertEquals(result2[0], 1);
		assertEquals(result2[result2.length - 1], 3);
		assertEquals(result2.length, first.length + third.length);
	}

	/**
	 * Tests that the method correctly returns an int of the number 
	 * of times the value (int) occures
	 */
	@Test
	public void countOccurrences_int_array_test() { 
		int[] base = { 1, 6, 1, 7, 4, 3, 4, 6, 3, 4 }; 
		assertEquals(ArrayUtil.countOccurrences(1, base), 2);
		assertEquals(ArrayUtil.countOccurrences(3, base), 2);
		assertEquals(ArrayUtil.countOccurrences(4, base), 3);
		assertEquals(ArrayUtil.countOccurrences(6, base), 2);
		assertEquals(ArrayUtil.countOccurrences(7, base), 1);
	}

	/**
	 * Tests that the method correctly returns an int of the number 
	 * of times the value (T) occures
	 */
	@Test
	public void countOccurrences_arraylist_test() { 
		ArrayList<Integer> base = new ArrayList<Integer>(); 
		base.add(1);
		base.add(1);
		base.add(4);
		base.add(3);
		base.add(4);
		base.add(3);
		base.add(4);
		assertEquals(ArrayUtil.countOccurrences(1, base), 2);
		assertEquals(ArrayUtil.countOccurrences(3, base), 2);
		assertEquals(ArrayUtil.countOccurrences(4, base), 3);
		ArrayList<String> strings = new ArrayList<String>();
		strings.add("woah");
		strings.add("yeah");
		strings.add("woah");
		strings.add("woah");
		assertEquals(ArrayUtil.countOccurrences("woah", strings), 3);
		assertEquals(ArrayUtil.countOccurrences("yeah", strings), 1);
	}

	/**
	 * Tests that the method correctly identifies an arary of all
	 * the same int
	 */
	@Test
	public void allSame_test() { 
		int[] base = { 1, 2, 1, 1, 1, 1, 1 };
		assertFalse(ArrayUtil.allSame(base));
		base[1] = 1;
		assertTrue(ArrayUtil.allSame(base));
	}

	/**
	 * Tests that the method correctly returns an int of the number 
	 * of times the value (T) occurs
	 */
	@Test
	public void countOccurrences_array_test() { 
		String[] base = { "yes", "yes", "no", "yes", "no", "yes" };
		assertEquals(ArrayUtil.countOccurrences("yes", base), 4);
		assertEquals(ArrayUtil.countOccurrences("no", base), 2);
		assertEquals(ArrayUtil.countOccurrences("maybe", base), 0);
	}

	/**
	 * Tests that the filter returns an array of the correct size 
	 * and of the right int values
	 */
	@Test
	public void filter_test() { 
		int[] base = { 1, 1, 4, 1, 1, 4, 1, 4, 1, 1, 1 }; 
		assertEquals(ArrayUtil.filter(base, 4).length, 8);
		assertTrue(ArrayUtil.allSame(ArrayUtil.filter(base, 4)));
		assertEquals(ArrayUtil.filter(base, 1).length, 3);
		assertTrue(ArrayUtil.allSame(ArrayUtil.filter(base, 1)));
	}

	/**
	 * Tests that the correct position is actually being returned
	 */
	@Test
	public void position_test() { 
		int[] base = { 9, 9, 0, 9, 9, };
		assertEquals(ArrayUtil.position(base, 9), 0);
		assertEquals(ArrayUtil.position(base, 0), 2);
	}

	/**
	 * Tests that the left hand side array is a subset of the
	 * right hand side array (including spread out)
	 */
	@Test
	public void subset_int_test() {
		int[] base = { 2, 3, 4, 5, 6, 7, 8 };
		int[] sub1 = { 2, 3, 4 };
		int[] sub2 = { 2, 5, 8 };
		int[] notSub = { 1, 9 };
		assertTrue(ArrayUtil.subset(sub1, base));
		assertTrue(ArrayUtil.subset(sub2, base));
		assertFalse(ArrayUtil.subset(notSub, base));
	}

	/**
	 * Tests that the left hand side array is a subset of the
	 * right hand side array (including spread out)
	 */
	@Test
	public void subset_long_test() { 
		long[] base = { 2, 3, 4, 5, 6, 7, 8 };
		long[] sub1 = { 2, 3, 4 };
		long[] sub2 = { 2, 5, 8 };
		long[] notSub = { 1, 9 };
		assertTrue(ArrayUtil.subset(sub1, base));
		assertTrue(ArrayUtil.subset(sub2, base));
		assertFalse(ArrayUtil.subset(notSub, base));
	}

	/**
	 * Tests that equal sequences are identified correctly
	 */
	@Test
	public void equalSequences_test() { 
		double[] seq1 = { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
		double[] seq2 = { 4.0, 5.0, 6.0 };
		double[] seq3 = { 3.0, 4.0, 6.0, 7.0 };
		assertTrue(ArrayUtil.equalSequences(seq1, 3, seq2, 0, 3));
		assertTrue(ArrayUtil.equalSequences(seq1, 2, seq3, 0, 2));
		assertFalse(ArrayUtil.equalSequences(seq1, 2, seq3, 0, 4));
		assertFalse(ArrayUtil.equalSequences(seq1, 0, seq2, 0, 3));
	}

	/**
	 * Tests that the resulting Arraylist is of Integer and contains 
	 * the given array elements
	 */
	@Test
	public void intListFromArray_test() { 
		int[] base = { 2, 6, 3, 7, 3, 5, 1 };
		assertTrue(ArrayUtil.intListFromArray(base) instanceof ArrayList<?>);
		assertTrue(ArrayUtil.intListFromArray(base).get(0) instanceof Integer);
		ArrayList<Integer> result = ArrayUtil.intListFromArray(base);
		assertEquals(result.get(0).intValue(), 2);
	}

	/**
	 * Tests that the resulting Arraylist is of Double and contains 
	 * the given array elements
	 */
	@Test
	public void doubleVectorFromArray_test() { 
		double[] base = { 2.0, 6.0, 3.0, 7.0, 3.0, 5.0, 1.0 };
		assertTrue(ArrayUtil.doubleVectorFromArray(base) instanceof ArrayList<?>);
		assertTrue(ArrayUtil.doubleVectorFromArray(base).get(0) instanceof Double);
		ArrayList<Double> result = ArrayUtil.doubleVectorFromArray(base);
		assertEquals(result.get(0).doubleValue(), 2.0, 0.0);
	}

	/**
	 * Tests that the returned array is the correct column
	 */
	@Test
	public void column_array_test() {
		double[][] matrix = new double[3][3];
		matrix[0][0] = 0.0;
		matrix[0][1] = 6.0;
		matrix[0][2] = 1.0;
		matrix[1][0] = 2.0;
		matrix[1][1] = 6.0;
		matrix[1][2] = 2.0;
		matrix[2][0] = 4.0;
		matrix[2][1] = 6.0;
		matrix[2][2] = 3.0;
		double[] col = ArrayUtil.column(matrix, 2);
		assertTrue(col instanceof double[]);
		assertEquals(col[0], 1.0, 0.0);
		assertEquals(col[1], 2.0, 0.0);
		assertEquals(col[2], 3.0, 0.0);
	}

	/**
	 * Tests that the returned array is the correct column
	 */
	@Test
	public void column_arraylist_test() { 
		ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> a = new ArrayList<Integer>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		ArrayList<Integer> c = new ArrayList<Integer>();
		a.add(5);
		a.add(9);
		a.add(8);
		b.add(1);
		b.add(3);
		b.add(2);
		c.add(7);
		c.add(7);
		c.add(7);
		matrix.add(a);
		matrix.add(b);
		matrix.add(c);
		Integer[] result = new Integer[3];
		ArrayUtil.column(matrix, 0, result);
		assertEquals(result[0].intValue(), 5);
		assertEquals(result[1].intValue(), 1);
		assertEquals(result[2].intValue(), 7);
	}

	/**
	 * Tests that the array returned contains only the values set to true
	 */
	@Test
	public void keepTrueValues_int_test() { 
		int[] base = { 0, 4, 6, 2, 9, 0, 2, 0 };
		boolean[] keep = { false, true, true, true, true, false, true, false };
		int[] result = ArrayUtil.keepTrueValues(base, keep);
		assertEquals(result.length, 5);
		assertEquals(result[0], 4);
		assertEquals(result[3], 9);
		assertFalse(result[0] == 0);
	}

	/**
	 * Tests that the array returned contains only the values set to true
	 */
	@Test
	public void keepTrueValues_double_test() { 
		double[] base = { 0.0, 4.2, 6.0, 2.3, 9.0, 0.0, 2.5, 0.0 };
		boolean[] keep = { false, true, true, true, true, false, true, false };
		double[] result = ArrayUtil.keepTrueValues(base, keep);
		assertEquals(result.length, 5);
		assertEquals(result[0], 4.2, 0.0);
		assertEquals(result[3], 9.0, 0.0);
		assertFalse(result[0] == 0.0);
	}

	/**
	 * Tests that the two arrays are correctly added together
	 */
	@Test
	public void zipAdd_double_test() { 
		double[] a = { 1.0, 1.0, 1.0, 1.0, 1.0 };
		double[] b = { 1.0, 2.0, 3.0, 4.0, 5.0 };
		assertTrue(ArrayUtil.zipAdd(a, b) instanceof double[]);
		assertEquals(ArrayUtil.zipAdd(a, b).length, 5);
		assertEquals(ArrayUtil.zipAdd(a, b)[0], 2.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[1], 3.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[2], 4.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[3], 5.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[4], 6.0, 0.0);
	}

	/**
	 * Tests that the two arrays are correctly added together
	 */
	@Test
	public void zipAdd_int_test() { 
		int[] a = { 1, 1, 1, 1, 1 };
		int[] b = { 1, 2, 3, 4, 5 };
		assertTrue(ArrayUtil.zipAdd(a, b) instanceof int[]);
		assertEquals(ArrayUtil.zipAdd(a, b).length, 5);
		assertEquals(ArrayUtil.zipAdd(a, b)[0], 2);
		assertEquals(ArrayUtil.zipAdd(a, b)[1], 3);
		assertEquals(ArrayUtil.zipAdd(a, b)[2], 4);
		assertEquals(ArrayUtil.zipAdd(a, b)[3], 5);
		assertEquals(ArrayUtil.zipAdd(a, b)[4], 6);
	}

	/**
	 * Tests that the resulting array only holds the pair-wise maxes 
	 * of the given arrays
	 */
	@Test
	public void zipMax_test() {
		double[] a = { 5.0, 1.0, 5.0, 1.0, 6.0 };
		double[] b = { 1.0, 2.0, 3.0, 4.0, 5.0 };
		assertTrue(ArrayUtil.zipMax(a, b) instanceof double[]);
		assertEquals(ArrayUtil.zipMax(a, b).length, 5);
		assertEquals(ArrayUtil.zipMax(a, b)[0], 5.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[1], 2.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[2], 5.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[3], 4.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[4], 6.0, 0.0);
	}

	/**
	 * Tests that the given array values are correctly scaled
	 */
	@Test
	public void scale_test() { 
		double[] base = { 1.0, 2.0, 3.0, 4.0 };
		assertEquals(ArrayUtil.scale(base, 2)[0], 2.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 2)[1], 4.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 2)[2], 6.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 2)[3], 8.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 70)[0], 70.0, 0.0);
	}

	/**
	 * Tests that the array returned only contained the 
	 * intersection of the given arrays
	 */
	@Test
	public void intersection_test() { 
		int[] a = { 1, 2, 3, 4, 5 };
		int[] b = { 4, 5, 6, 7, 8 };
		assertTrue(ArrayUtil.intersection(a, b) instanceof int[]);
		assertEquals(ArrayUtil.intersection(a, b).length, 2);
		assertEquals(ArrayUtil.intersection(a, b)[0], 4);
		assertEquals(ArrayUtil.intersection(a, b)[1], 5);
	}

	/**
	 * Tests that the resulting array is correct (and in order of set)
	 */
	@Test
	public void integerSetToArray_test() { 
		Set<Integer> base = new HashSet<Integer>();
		base.add(5);
		base.add(2);
		base.add(1);
		base.add(4);
		assertTrue(ArrayUtil.integerSetToArray(base) instanceof int[]);
		assertEquals(ArrayUtil.integerSetToArray(base).length, 4);
		assertEquals(ArrayUtil.integerSetToArray(base)[0], 1);
		assertEquals(ArrayUtil.integerSetToArray(base)[3], 5);
	}

	/**
	 * Tests that the resulting portion is correct
	 */
	@Test
	public void portion_test() { 
		double[] base = { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 };
		assertTrue(ArrayUtil.portion(base, 2, 4) instanceof double[]);
		assertEquals(ArrayUtil.portion(base, 2, 4).length, 3);
		assertEquals(ArrayUtil.portion(base, 2, 4)[0], 3.0, 0.0);
		assertEquals(ArrayUtil.portion(base, 2, 4)[2], 5.0, 0.0);
	}

	/**
	 * Tests that the method correctly determines what arrays are equal
	 */
	@Test
	public void setEquality_long_test() { 
		long[] a = { 1, 2, 3, 4 };
		long[] b = { 1, 2, 3, 4 };
		long[] c = { 1, 2, 3 };
		assertTrue(ArrayUtil.setEquality(a, b));
		assertFalse(ArrayUtil.setEquality(a, c));
	}

	/**
	 * Tests that the method correctly determines what arrays are equal
	 */
	@Test
	public void setEquality_arraylist_test() { 
		ArrayList<Integer> a = new ArrayList<Integer>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		ArrayList<String> c = new ArrayList<String>();
		ArrayList<String> d = new ArrayList<String>();
		ArrayList<String> e = new ArrayList<String>();
		a.add(1);
		a.add(2);
		a.add(3);
		b.add(1);
		b.add(2);
		b.add(3);
		c.add("yes");
		c.add("no");
		d.add("yes");
		d.add("no");
		e.add("yes");
		e.add("maybe");
		assertTrue(ArrayUtil.setEquality(a, b));
		assertTrue(ArrayUtil.setEquality(c, d));
		assertFalse(ArrayUtil.setEquality(c, e));
	}
	
	/**
	 * Tests that the returned ArrayList contains the correct members from 
	 * the left hand side array list
	 */
	@Test
	public void setDifference_arraylist_test() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		a.add(4);
		b.add(3);
		b.add(2);
		assertTrue(ArrayUtil.setDifference(a, b) instanceof ArrayList<?>);
		assertEquals(ArrayUtil.setDifference(a, b).size(), 2);
		assertEquals(ArrayUtil.setDifference(a, b).get(0).intValue(), 1);
		assertEquals(ArrayUtil.setDifference(a, b).get(1).intValue(), 4);
	}
	
	/**
	 * Tests that the returned array contains the correct members from 
	 * the left hand side array 
	 */
	@Test
	public void setDifference_array_test() {
		int[] a = {1, 2, 3, 4};
		int[] b = {3, 2};
		assertTrue(ArrayUtil.setDifference(a, b) instanceof int[]);
		assertEquals(ArrayUtil.setDifference(a, b).length, 2);
		assertEquals(ArrayUtil.setDifference(a, b)[0], 1);
		assertEquals(ArrayUtil.setDifference(a, b)[1], 4);
	}
	
	/**
	 * Tests that the returned array contains the correct members from 
	 * the left hand side array compared with an arraylist
	 */
	@Test
	public void setDifference_array_and_arraylist_test() {
		int[] a = {1, 2, 3, 4};
		ArrayList<Integer> b = new ArrayList<Integer>();
		b.add(3);
		b.add(2);
		assertTrue(ArrayUtil.setDifference(a, b) instanceof int[]);
		assertEquals(ArrayUtil.setDifference(a, b).length, 2);
		assertEquals(ArrayUtil.setDifference(a, b)[0], 1);
		assertEquals(ArrayUtil.setDifference(a, b)[1], 4);
	}
	
	/**
	 * Tests that the given response is either the given item or null
	 */
	@Test
	public void firstInstance_test() {
		String[] base = {"no","yes"};
		assertEquals(ArrayUtil.firstInstance(base, "yes"), "yes");
		assertNull(ArrayUtil.firstInstance(base, "maybe"));
		assertNotNull(ArrayUtil.firstInstance(base, "no"));
	}
	
	/**
	 * Tests that the correct number of unique entries is returned
	 */
	@Test
	public void setCardinality_test() {
		int[] base = {1, 2, 3, 4, 3, 6, 2, 4, 5, 4, 3, 4, 4, 4};
		assertEquals(ArrayUtil.setCardinality(base), 6);
	}

//	@Test
//	public void pairwiseMinimum_test() {
//		INDArray a1 = Nd4j.create(new double[] {1, 2,3, 4,5, 6, 7, 8,-34});
//		INDArray a2 = Nd4j.create(new double[] {4,-2,5,-4,5,20,-7,-8,-2});
//		
//		INDArray originalA1 = a1.dup();
//		INDArray originalA2 = a2.dup();
//		
//		ArrayUtil.pairwiseMinimum(a1, a2);
//		
//		assertFalse(a1.equals(originalA1)); // a1 is changed, so they are not equal
//		
//		INDArray expected = Nd4j.create(new double[] {1,-2,3,-4,5,6,-7,-8,-34});
//		assertEquals(expected,a1); // New result for a1
//		
//		assertEquals(a2, originalA2); // a2 does not change
//	}

}