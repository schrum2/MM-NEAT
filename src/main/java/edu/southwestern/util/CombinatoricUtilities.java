package edu.southwestern.util;

import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Several helper methods concerned with counting or
 * creating different possible structures. Lots of
 * discrete math.
 * 
 * @author Jacob Schrum
 */
public class CombinatoricUtilities {

	/**
	 * Return all permutations where the possible picks are 0 through 
     * (choices - 1). Each sub-ArrayList in the returned ArrayList is a single permutation
	 *
	 * @param choices
	 *            number of things to choose from
	 * @return all permutations
	 */
	public static ArrayList<ArrayList<Integer>> getAllPermutations(int choices) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		int[] a = new int[choices], p = new int[choices];
		// Upper Index i; Lower Index j initialize arrays; a[N] can be any type
		int i, j, tmp; 
		for (i = 0; i < choices; i++) { 
			a[i] = i; // a[i] value is not revealed and can be arbitrary
		}
		result.add(ArrayUtil.intListFromArray(a));
		i = 1; // setup first swap points to be 1 and 0 respectively (i & j)
		while (i < choices) {
			if (p[i] < i) {
				j = i % 2 * p[i]; // IF i is odd then j = p[i] otherwise j = 0
				tmp = a[j]; // swap(a[j], a[i])
				a[j] = a[i];
				a[i] = tmp;
				result.add(ArrayUtil.intListFromArray(a));
				p[i]++; // increase index "weight" for i by one
				i = 1; // reset index i to 1 (assumed)
			} else { // otherwise p[i] == i
				p[i] = 0; // reset p[i] to zero
				i++; // set new index value for i (increase by one)
			} // if (p[i] < i)
		} // while(i < N)
		return result;
	}

	/**
	 * Given a vector of sizes, every possible combination consisting of one
	 * member from each groups of a given size is returned. The results are
	 * returned in a vector, where each member is a vector that in turn contains
	 * the indices in the group of each member in that combination.
	 *
	 * @param lengths
	 *            size of each group
	 * @return all combinations
	 */
	public static ArrayList<ArrayList<Integer>> getAllCombinations(ArrayList<Integer> lengths) {
		long start = System.currentTimeMillis();
		ArrayList<ArrayList<Integer>> combos = new ArrayList<ArrayList<Integer>>();
		getAllCombinations(lengths, 0, new ArrayList<Integer>(lengths.size()), combos);
		System.out.println(combos.size() + " combinations of length " + combos.get(0).size());
		long end = System.currentTimeMillis();
		System.out.println("Combination computation time: " + TimeUnit.MILLISECONDS.toMinutes(end - start)
				+ " minutes (" + (end - start) + " milliseconds)");
		return combos;
	}

	/**
	 * Recursive helper method for getAllCombinations above
	 *
	 * @param lengths
	 *            sizes of all groups
	 * @param idx
	 *            index in lengths
	 * @param soFar
	 *            collection of members currently being built
	 * @param combos
	 *            accumulates complete combinations
	 */
	@SuppressWarnings("unchecked")
	public static void getAllCombinations(final ArrayList<Integer> lengths, int idx, ArrayList<Integer> soFar, ArrayList<ArrayList<Integer>> combos) {
		if (idx == lengths.size()) {
			// System.out.println(soFar);
			combos.add((ArrayList<Integer>) soFar.clone());
		} else {
			int numOptions = lengths.get(idx);
			for (int i = 0; i < numOptions; i++) {
				soFar.add(i);
				getAllCombinations(lengths, idx + 1, soFar, combos);
				soFar.remove(soFar.size() - 1);
			}
		}
	}

	/**
	 * Takes an integer from 0 to 6 inclusive and returns an array of float
	 * whose elements are 0 or 1. The contents are basically the bit
	 * representation of (x + 1).
	 *
	 * @param x
	 *            int in [0,6]
	 * @return bit representation of (x+1) in array of float
	 */
	public static float[] mapTuple(int x) {
		switch (x) {
		case 0:
			return new float[] { 0, 0, 1 };
		case 1:
			return new float[] { 0, 1, 0 };
		case 2:
			return new float[] { 0, 1, 1 };
		case 3:
			return new float[] { 1, 0, 0 };
		case 4:
			return new float[] { 1, 0, 1 };
		case 5:
			return new float[] { 1, 1, 0 };
		case 6:
			return new float[] { 1, 1, 1 };
		default:
			throw new IllegalArgumentException("mapTuple only takes values from 0 to 6 inclusive:" + x);
		}
	}

	/**
	 * Gets a unique color for each positive int
	 *
	 * @param m
	 *            positive int
	 * @return color corresponding to int
	 */
	public static Color colorFromInt(int m) {
		float[] baseColor = mapTuple(m % 7);
		for (int i = 0; i < baseColor.length; i++) {
			baseColor[i] *= Math.pow(0.75, (m / 7));
		}
		Color result = new Color(baseColor[0], baseColor[1], baseColor[2]);
		return result;
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> lens = new ArrayList<Integer>();
		lens.add(4);
		lens.add(5);
		lens.add(6);
		System.out.println(getAllCombinations(lens));
		DrawingPanel panel1 = new DrawingPanel(100, 100, "function 1");
		DrawingPanel panel2 = new DrawingPanel(100, 100, "function 2");
		DrawingPanel panel3 = new DrawingPanel(100, 100, "function 3");
		DrawingPanel panel4 = new DrawingPanel(100, 100, "function 4");
		DrawingPanel panel5 = new DrawingPanel(100, 100, "function 5");
		DrawingPanel panel6 = new DrawingPanel(100, 100, "function 6");
		DrawingPanel panel7 = new DrawingPanel(100, 100, "function 7");
		DrawingPanel panel8 = new DrawingPanel(100, 100, "function 8");
		DrawingPanel panel9 = new DrawingPanel(100, 100, "function 9");
		DrawingPanel panel10 = new DrawingPanel(100, 100, "function 10");

		panel1.setBackground(colorFromInt(1));
		panel2.setBackground(colorFromInt(2));
		panel3.setBackground(colorFromInt(3));
		panel4.setBackground(colorFromInt(4));
		panel5.setBackground(colorFromInt(5));
		panel6.setBackground(colorFromInt(15));
		panel7.setBackground(colorFromInt(16));
		panel8.setBackground(colorFromInt(12));
		panel9.setBackground(colorFromInt(13));
		panel10.setBackground(colorFromInt(0));
		
		panel1.setLocation(0, 0);
		panel2.setLocation(100, 0);
		panel3.setLocation(200, 0);
		panel4.setLocation(300, 0);
		panel5.setLocation(400, 0);
		panel6.setLocation(500, 0);
		panel7.setLocation(0, 100);
		panel8.setLocation(100, 100);
		panel9.setLocation(200, 100);
		panel10.setLocation(300, 100);
	}
}
