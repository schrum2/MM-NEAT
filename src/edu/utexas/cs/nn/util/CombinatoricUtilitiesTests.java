package edu.utexas.cs.nn.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

public class CombinatoricUtilitiesTests {

	@Test
	public void combinations_test() {
		ArrayList<Integer> lens = new ArrayList<Integer>();
		lens.add(3);
		lens.add(3);
		ArrayList<ArrayList<Integer>> result = CombinatoricUtilities.getAllCombinations(lens);
		assertEquals(result.size(), 9);
		for(int i = 0; i < result.size(); i++){
			assertEquals(result.get(i).size(), 2);
			ArrayList<Integer> result_i = result.get(i);
			assertTrue(result_i.get(0) < 3 && result_i.get(0) >= 0);
			assertTrue(result_i.get(1) < 3 && result_i.get(1) >= 0);
		}
		lens.add(3);
		result = CombinatoricUtilities.getAllCombinations(lens);
		assertEquals(result.size(), 27);
	}
	
	@Test
	public void permutations_test() {
		ArrayList<ArrayList<Integer>> result = CombinatoricUtilities.getAllPermutations(3);
		assertEquals(result.size(), 6);
		for(int i = 0; i < result.size(); i++){
			assertEquals(result.get(i).size(), 3);
			ArrayList<Integer> result_i = result.get(i);
			assertTrue(result_i.get(0) < 3 && result_i.get(0) >= 0);
			assertTrue(result_i.get(1) < 3 && result_i.get(1) >= 0);
			assertTrue(result_i.get(2) < 3 && result_i.get(1) >= 0);
			assertEquals(ArrayUtil.countOccurrences(0, result_i), 1);
			assertEquals(ArrayUtil.countOccurrences(1, result_i), 1);
			assertEquals(ArrayUtil.countOccurrences(2, result_i), 1);
		}
		ArrayList<ArrayList<Integer>> result2 = CombinatoricUtilities.getAllPermutations(5);
		assertEquals(result2.size(), 120);
		for(int i = 0; i < result2.size(); i++){
			assertEquals(result2.get(i).size(), 5);
			ArrayList<Integer> result_i = result2.get(i);
			assertTrue(result_i.get(0) < 5 && result_i.get(0) >= 0);
			assertTrue(result_i.get(1) < 5 && result_i.get(1) >= 0);
			assertTrue(result_i.get(2) < 5 && result_i.get(2) >= 0);
			assertTrue(result_i.get(3) < 5 && result_i.get(3) >= 0);
			assertTrue(result_i.get(4) < 5 && result_i.get(4) >= 0);
			assertEquals(ArrayUtil.countOccurrences(0, result_i), 1);
			assertEquals(ArrayUtil.countOccurrences(1, result_i), 1);
			assertEquals(ArrayUtil.countOccurrences(2, result_i), 1);
			assertEquals(ArrayUtil.countOccurrences(3, result_i), 1);
			assertEquals(ArrayUtil.countOccurrences(4, result_i), 1);
		}
	}
}
