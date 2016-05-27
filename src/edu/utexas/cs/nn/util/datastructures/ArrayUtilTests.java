package edu.utexas.cs.nn.util.datastructures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ArrayUtilTests {
	
	@Test
	public void doubleOnes_test() { // tests that the given number of 1's is returned in the double[]
		int temp = 14;
		double [] result = ArrayUtil.doubleOnes(temp);
		double [] expected = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		assertArrayEquals(result, expected, 0.0);
	}
	
	@Test
	public void intOnes_test() { // tests that the given number of 1's is returned in the int[]
		int temp = 14;
		int [] result = ArrayUtil.intOnes(temp);
		int [] expected = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		assertArrayEquals(result, expected);
	}
	
	@Test
	public void primitiveDoubleArrayToDoubleArray_test() { // tests that the given array is of object type Double and not primitive type double
		double [] test = {14.0, 11.0, 8.0, 1.0, 2.0, 1.0, 8.0, 3.0, 23.0};
		Double[] result = ArrayUtil.primitiveDoubleArrayToDoubleArray(test);
		assertTrue(test instanceof double[]);
		assertTrue(result instanceof Double[]);
		for(int i = 0; i < result.length ; i++){
			assertEquals(test[i], result[i], 0.0);
		}		
	}
	
	@Test
	public void intArrayFromArrayList_test(){ // tests that an int[] is returned from a given ArrayList<Integer>
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(12);
		test.add(6);
		test.add(34);
		test.add(22);
		test.add(14);
		int[] result = ArrayUtil.intArrayFromArrayList(test);
		assertTrue(result instanceof int[]);
		assertTrue(test instanceof ArrayList<?>);
		for(int i = 0; i < result.length ; i++){
			assertEquals(test.get(i), result[i], 0.0);
		}
	}
	
	@Test
	public void doubleArrayFromList_test(){ // tests that an double[] is returned from a given ArrayList<Double>
		ArrayList<Double> test = new ArrayList<Double>();
		test.add(12.0);
		test.add(6.0);
		test.add(34.0);
		test.add(22.0);
		test.add(14.0);
		double[] result = ArrayUtil.doubleArrayFromList(test);
		assertTrue(result instanceof double[]);
		assertTrue(test instanceof ArrayList<?>);
		for(int i = 0; i < result.length ; i++){
			assertEquals(test.get(i), result[i], 0.0);
		}
	}
	
	@Test
	public void containsAny_test(){ // tests that containsAny correctly identifies members of a set
		int[] members = {1, 2, 3, 4, 5};
		int[] set1 = {6, 7, 8, 9, 5};
		int[] set2 = {0, 9, 7};
		assertTrue(members instanceof int[]);
		assertTrue(set1 instanceof int[]);
		assertTrue(set2 instanceof int[]);
		assertTrue(ArrayUtil.containsAny(members, set1));
		assertFalse(ArrayUtil.containsAny(members, set2));
	}
	
	@Test
	public void member_int_test(){ // tests that member only returns true when int x is a member of the given set
		int member = 5;
		int[] set1 = {6, 7, 8, 9, 5};
		int[] set2 = {0, 9, 7};
		assertTrue(set1 instanceof int[]);
		assertTrue(set2 instanceof int[]);
		assertTrue(ArrayUtil.member(member, set1));
		assertFalse(ArrayUtil.member(member, set2));
	}
	
	@Test
	public void member_long_test(){ // tests that member only returns true when long x is a member of the given set
		long member = 5;
		long[] set1 = {6, 7, 8, 9, 5};
		long[] set2 = {0, 9, 7};
		assertTrue(set1 instanceof long[]);
		assertTrue(set2 instanceof long[]);
		assertTrue(ArrayUtil.member(member, set1));
		assertFalse(ArrayUtil.member(member, set2));
	}
	
	@Test
	public void member_double_test(){ // tests that member only returns true when double x is a member of the given set
		double member = 5.0;
		double[] set1 = {6, 7, 8, 9, 5};
		double[] set2 = {0, 9, 7};
		assertTrue(set1 instanceof double[]);
		assertTrue(set2 instanceof double[]);
		assertTrue(ArrayUtil.member(member, set1));
		assertFalse(ArrayUtil.member(member, set2));
	}
	
	@Test
	public void combineArrays_int_test(){ // tests that combine arrays makes an array with the first array elements followed by the second array elements
		int[] first = {1, 1, 1, 1, 1};
		int[] second = {2, 2, 2, 2, 2};
		int[] third = {3, 3, 3};
		
		assertTrue(ArrayUtil.combineArrays(first, second) instanceof int[]);
		int[] result1 = ArrayUtil.combineArrays(first, second);
		assertEquals(result1[0], 1);
		assertEquals(result1[result1.length-1], 2);
		assertEquals(result1.length, first.length + second.length);
		
		assertTrue(ArrayUtil.combineArrays(first, third) instanceof int[]);
		int[] result2 = ArrayUtil.combineArrays(first, third);
		assertEquals(result2[0], 1);
		assertEquals(result2[result2.length-1], 3);
		assertEquals(result2.length, first.length + third.length);
	}
	
	@Test
	public void combineArrays_long_test(){ // tests that combine arrays makes an array with the first array elements followed by the second array elements
		long[] first = {1, 1, 1, 1, 1};
		long[] second = {2, 2, 2, 2, 2};
		long[] third = {3, 3, 3};
		
		assertTrue(ArrayUtil.combineArrays(first, second) instanceof long[]);
		long[] result1 = ArrayUtil.combineArrays(first, second);
		assertEquals(result1[0], 1);
		assertEquals(result1[result1.length-1], 2);
		assertEquals(result1.length, first.length + second.length);
		
		assertTrue(ArrayUtil.combineArrays(first, third) instanceof long[]);
		long[] result2 = ArrayUtil.combineArrays(first, third);
		assertEquals(result2[0], 1);
		assertEquals(result2[result2.length-1], 3);
		assertEquals(result2.length, first.length + third.length);
	}
	
	@Test
	public void countOccurrences_int_array_test(){ // tests that the method correctly returns an int of the number of times the value (int) occurs
		int[] base = {1, 6, 1, 7, 4, 3, 4, 6, 3, 4}; //"1" is 2, "6" is 2, "7" is 1, "4" is 3, "3" is 2
		assertEquals(ArrayUtil.countOccurrences(1, base), 2);
		assertEquals(ArrayUtil.countOccurrences(3, base), 2);
		assertEquals(ArrayUtil.countOccurrences(4, base), 3);
		assertEquals(ArrayUtil.countOccurrences(6, base), 2);
		assertEquals(ArrayUtil.countOccurrences(7, base), 1);
	}
	
	@Test
	public void countOccurrences_arraylist_test(){ // tests that the method correctly returns an int of the number of times the value (T) occurs
		ArrayList<Integer> base = new ArrayList<Integer>(); //"1" is 2, "6" is 2, "7" is 1, "4" is 3, "3" is 2
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
	
	@Test
	public void allSame_test(){ // tests that the allSame method correctly identifies an array of all the same int
		int[] base = {1, 2, 1, 1, 1, 1, 1};
		assertFalse(ArrayUtil.allSame(base));
		base[1] = 1;
		assertTrue(ArrayUtil.allSame(base));
	}
	
	@Test
	public void countOccurrences_array_test(){ // tests that the method correctly returns an int of the number of times the value (T) occurs
		String[] base = {"yes", "yes", "no", "yes", "no", "yes"}; //"yes" is 4, "no" is 2, "maybe" is 0
		assertEquals(ArrayUtil.countOccurrences("yes", base), 4);
		assertEquals(ArrayUtil.countOccurrences("no", base), 2);
		assertEquals(ArrayUtil.countOccurrences("maybe", base), 0);
	}
	
	@Test
	public void filter_test(){ // tests that filter returns an array of the correct size and of the right int values
		int[] base = {1, 1, 4, 1, 1, 4, 1, 4, 1, 1, 1}; //8 "1"s and 3 "4"s
		assertEquals(ArrayUtil.filter(base, 4).length, 8);
		assertTrue(ArrayUtil.allSame(ArrayUtil.filter(base, 4)));
		assertEquals(ArrayUtil.filter(base, 1).length, 3);
		assertTrue(ArrayUtil.allSame(ArrayUtil.filter(base, 1)));
	}
	
	@Test
	public void position_test(){ // tests that the correct position is actually being returned
		int[] base = {9, 9, 0, 9, 9,};
		assertEquals(ArrayUtil.position(base, 9), 0);
		assertEquals(ArrayUtil.position(base, 0), 2);
	}
	
	@Test
	public void subset_int_test(){ // tests that the left hand side array is a subset of the right hand side array (including spread out)
		int[] base = {2, 3, 4, 5, 6, 7, 8};
		int[] sub1 = {2, 3, 4};
		int[] sub2 = {2, 5, 8};
		int[] notSub = {1, 9};
		assertTrue(ArrayUtil.subset(sub1, base));
		assertTrue(ArrayUtil.subset(sub2, base));
		assertFalse(ArrayUtil.subset(notSub, base));
	}
	
	@Test
	public void subset_long_test(){ // tests that the left hand side array is a subset of the right hand side array (including spread out)
		long[] base = {2, 3, 4, 5, 6, 7, 8};
		long[] sub1 = {2, 3, 4};
		long[] sub2 = {2, 5, 8};
		long[] notSub = {1, 9};
		assertTrue(ArrayUtil.subset(sub1, base));
		assertTrue(ArrayUtil.subset(sub2, base));
		assertFalse(ArrayUtil.subset(notSub, base));
	}
	
	@Test
	public void equalSequences_test(){ // tests that equal sequences are identified correctly
		double[] seq1 = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
		double[] seq2 = {4.0, 5.0, 6.0};
		double[] seq3 = {3.0, 4.0, 6.0, 7.0};
		assertTrue(ArrayUtil.equalSequences(seq1, 3, seq2, 0, 3));
		assertTrue(ArrayUtil.equalSequences(seq1, 2, seq3, 0, 2));
		assertFalse(ArrayUtil.equalSequences(seq1, 2, seq3, 0, 4));
		assertFalse(ArrayUtil.equalSequences(seq1, 0, seq2, 0, 3));	
	}
	
	@Test
	public void intListFromArray_test(){ // tests that the resulting Arraylist is of Integer and contains the given array elements
		int[] base = {2, 6, 3, 7, 3, 5, 1};
		assertTrue(ArrayUtil.intListFromArray(base) instanceof ArrayList<?>);
		assertTrue(ArrayUtil.intListFromArray(base).get(0) instanceof Integer);
		ArrayList<Integer> result = ArrayUtil.intListFromArray(base);
		assertEquals(result.get(0).intValue(), 2);
	}
	
	@Test
	public void doubleVectorFromArray_test(){ // tests that the resulting Arraylist is of Double and contains the given array elements
		double[] base = {2.0, 6.0, 3.0, 7.0, 3.0, 5.0, 1.0};
		assertTrue(ArrayUtil.doubleVectorFromArray(base) instanceof ArrayList<?>);
		assertTrue(ArrayUtil.doubleVectorFromArray(base).get(0) instanceof Double);
		ArrayList<Double> result = ArrayUtil.doubleVectorFromArray(base);
		assertEquals(result.get(0).doubleValue(), 2.0, 0.0);
	}
	
	@Test
	public void column_array_test(){ // tests that the returned array is the correct column
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

	@Test
	public void column_arraylist_test(){ // tests that the returned array is the correct column
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
	
	@Test
	public void keepTrueValues_int_test(){ // tests that the array returned contains only the values set to true
		int[] base = {0, 4, 6, 2, 9, 0, 2, 0};
		boolean[] keep = {false, true, true, true, true, false, true, false};
		int[] result = ArrayUtil.keepTrueValues(base, keep);
		assertEquals(result.length, 5);
		assertEquals(result[0], 4);
		assertEquals(result[3], 9);
		assertFalse(result[0] == 0);
	}
	
	@Test
	public void keepTrueValues_double_test(){ // tests that the array returned contains only the values set to true
		double[] base = {0.0, 4.2, 6.0, 2.3, 9.0, 0.0, 2.5, 0.0};
		boolean[] keep = {false, true, true, true, true, false, true, false};
		double[] result = ArrayUtil.keepTrueValues(base, keep);
		assertEquals(result.length, 5);
		assertEquals(result[0], 4.2, 0.0);
		assertEquals(result[3], 9.0, 0.0);
		assertFalse(result[0] == 0.0);
	}
	
	@Test
	public void zipAdd_double_test(){ // tests that the two arrays are correctly added together
		double[] a = {1.0, 1.0, 1.0, 1.0, 1.0};
		double[] b = {1.0, 2.0, 3.0, 4.0, 5.0};
		assertTrue(ArrayUtil.zipAdd(a, b) instanceof double[]);
		assertEquals(ArrayUtil.zipAdd(a, b).length, 5);
		assertEquals(ArrayUtil.zipAdd(a, b)[0], 2.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[1], 3.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[2], 4.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[3], 5.0, 0.0);
		assertEquals(ArrayUtil.zipAdd(a, b)[4], 6.0, 0.0);
	}
	
	@Test
	public void zipAdd_int_test(){ // tests that the two arrays are correctly added together
		int[] a = {1, 1, 1, 1, 1};
		int[] b = {1, 2, 3, 4, 5};
		assertTrue(ArrayUtil.zipAdd(a, b) instanceof int[]);
		assertEquals(ArrayUtil.zipAdd(a, b).length, 5);
		assertEquals(ArrayUtil.zipAdd(a, b)[0], 2);
		assertEquals(ArrayUtil.zipAdd(a, b)[1], 3);
		assertEquals(ArrayUtil.zipAdd(a, b)[2], 4);
		assertEquals(ArrayUtil.zipAdd(a, b)[3], 5);
		assertEquals(ArrayUtil.zipAdd(a, b)[4], 6);
	}
	
	@Test
	public void zipMax_test(){ // tests that the resulting array only holds the pair-wise maxes of the given arrays
		double[] a = {5.0, 1.0, 5.0, 1.0, 6.0};
		double[] b = {1.0, 2.0, 3.0, 4.0, 5.0};
		assertTrue(ArrayUtil.zipMax(a, b) instanceof double[]);
		assertEquals(ArrayUtil.zipMax(a, b).length, 5);
		assertEquals(ArrayUtil.zipMax(a, b)[0], 5.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[1], 2.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[2], 5.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[3], 4.0, 0.0);
		assertEquals(ArrayUtil.zipMax(a, b)[4], 6.0, 0.0);
	}
	
	@Test
	public void scale_test(){ // tests that the array values are correctly scaled
		double[] base = {1.0, 2.0, 3.0, 4.0};
		assertEquals(ArrayUtil.scale(base, 2)[0], 2.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 2)[1], 4.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 2)[2], 6.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 2)[3], 8.0, 0.0);
		assertEquals(ArrayUtil.scale(base, 70)[0], 70.0, 0.0);
	}
	
	@Test
	public void intersection_test(){ // tests that the array returned only contains the intersection of the given arrays
		int[] a = {1, 2, 3, 4, 5};
		int[] b = {4, 5, 6, 7, 8};
		assertTrue(ArrayUtil.intersection(a, b) instanceof int[]);
		assertEquals(ArrayUtil.intersection(a, b).length, 2);
		assertEquals(ArrayUtil.intersection(a, b)[0], 4);
		assertEquals(ArrayUtil.intersection(a, b)[1], 5);
	}
	
	@Test
	public void integerSetToArray_test(){ // tests that the resulting array
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
	
	@Test
	public void portion_test(){ // tests that the resulting portion is correct
		double[] base = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		assertTrue(ArrayUtil.portion(base, 2, 4) instanceof double[]);
		assertEquals(ArrayUtil.portion(base, 2, 4).length, 3);
		assertEquals(ArrayUtil.portion(base, 2, 4)[0], 3.0, 0.0);
		assertEquals(ArrayUtil.portion(base, 2, 4)[2], 5.0, 0.0);
	}
	
	@Test
	public void setEquality_long_test(){ // tests that the equality function correctly determines what arrays are equals
		long[] a = {1, 2, 3, 4};
		long[] b = {1, 2, 3, 4};
		long[] c = {1, 2, 3};
		assertTrue(ArrayUtil.setEquality(a, b));
		assertFalse(ArrayUtil.setEquality(a, c));
	}
	
	@Test
	public void setEquality_arraylist_test(){ // tests that the equality function correctly determines what arrays are equals
		ArrayList<Integer> a = new ArrayList<Integer>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		ArrayList<String> c = new ArrayList<String>();
		ArrayList<String> d = new ArrayList<String>();
		ArrayList<String> e = new ArrayList<String>();
		a.add(1);
		a.add(2);
		a.add(3);
		assertTrue(ArrayUtil.setEquality(a, b));
		assertTrue(ArrayUtil.setEquality(c, d));
		assertFalse(ArrayUtil.setEquality(c, e));
	}
	
}