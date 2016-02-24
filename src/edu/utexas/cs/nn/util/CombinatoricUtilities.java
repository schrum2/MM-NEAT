package edu.utexas.cs.nn.util;

import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jacob Schrum
 */
public class CombinatoricUtilities {

    /**
     * Return all permutations where the possible picks are 0 through (choices -
     * 1). Each sub-ArrayList in the returned ArrayList is a single permutation
     *
     * @param choices number of things to choose from
     * @return all permutations
     */
    public static ArrayList<ArrayList<Integer>> getAllPermutations(int choices) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        int[] a = new int[choices], p = new int[choices];
        int i, j, tmp; // Upper Index i; Lower Index j

        for (i = 0; i < choices; i++) {  // initialize arrays; a[N] can be any type
            a[i] = i;   // a[i] value is not revealed and can be arbitrary
        }
        result.add(ArrayUtil.intListFromArray(a));
        i = 1;   // setup first swap points to be 1 and 0 respectively (i & j)
        while (i < choices) {
            if (p[i] < i) {
                j = i % 2 * p[i];   // IF i is odd then j = p[i] otherwise j = 0
                tmp = a[j];         // swap(a[j], a[i])
                a[j] = a[i];
                a[i] = tmp;
                result.add(ArrayUtil.intListFromArray(a));
                p[i]++;             // increase index "weight" for i by one
                i = 1;              // reset index i to 1 (assumed)
            } else {               // otherwise p[i] == i
                p[i] = 0;           // reset p[i] to zero
                i++;                // set new index value for i (increase by one)
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
     * @param lengths size of each group
     * @return all combinations
     */
    public static ArrayList<ArrayList<Integer>> getAllCombinations(ArrayList<Integer> lengths) {
        long start = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> combos = new ArrayList<ArrayList<Integer>>();
        getAllCombinations(lengths, 0, new ArrayList<Integer>(lengths.size()), combos);
        System.out.println(combos.size() + " combinations of length " + combos.get(0).size());
        long end = System.currentTimeMillis();
        System.out.println("Combination computation time: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes (" + (end - start) + " milliseconds)");
        return combos;
    }

    /**
     * Recursive helper method for getAllCombinations above
     *
     * @param lengths sizes of all groups
     * @param idx index in lengths
     * @param soFar collection of members currently being built
     * @param combos accumulates complete combinations
     */
    public static void getAllCombinations(final ArrayList<Integer> lengths, int idx, ArrayList<Integer> soFar, ArrayList<ArrayList<Integer>> combos) {
        if (idx == lengths.size()) {
            //System.out.println(soFar);
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
     * @param x int in [0,6]
     * @return bit representation of (x+1) in array of float
     */
    public static float[] mapTuple(int x) {
        switch (x) {
            case 0:
                return new float[]{0, 0, 1};
            case 1:
                return new float[]{0, 1, 0};
            case 2:
                return new float[]{0, 1, 1};
            case 3:
                return new float[]{1, 0, 0};
            case 4:
                return new float[]{1, 0, 1};
            case 5:
                return new float[]{1, 1, 0};
            case 6:
                return new float[]{1, 1, 1};
            default:
                throw new IllegalArgumentException("mapTuple only takes values from 0 to 6 inclusive");
        }
    }

    /**
     * Gets a unique color for each positive int
     *
     * @param m positive int
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
//        ArrayList<Integer> l = new ArrayList<Integer>(3);
//        l.add(100);
//        l.add(100);
//        l.add(100);
//        System.out.println(getAllCombinations(l).size());
        ArrayList<ArrayList<Integer>> result = getAllPermutations(3);
        System.out.println(result);
        System.out.println(result.size());
    }
}
