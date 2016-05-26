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
	public void _test(){
		
	}
}