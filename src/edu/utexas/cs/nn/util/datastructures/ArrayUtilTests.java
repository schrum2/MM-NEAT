package edu.utexas.cs.nn.util.datastructures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

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
	
	
}
