package edu.utexas.cs.nn.util.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jacob Schrum
 */
public class ArrayUtil {

	/**
	 * Return primitive double array of given size containing all ones
	 *
	 * @param size
	 * @return
	 */
	public static double[] doubleOnes(int size) {
		double[] ones = new double[size];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = 1;
		}
		return ones;
	}

	/**
	 * Return primitive int array of given size containing all ones
	 *
	 * @param size
	 * @return
	 */
	public static int[] intOnes(int size) {
		int[] ones = new int[size];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = 1;
		}
		return ones;
	}

	public static Double[] primitiveDoubleArrayToDoubleArray(double[] array) {
		Double[] result = new Double[array.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	public static int[] intArrayFromArrayList(ArrayList<Integer> path) {
		int[] arrayPath = new int[path.size()];
		for (int i = 0; i < arrayPath.length; i++) {
			arrayPath[i] = path.get(i);
		}
		return arrayPath;
	}

	/**
	 * returns a 1D double array from a 2D double array
	 * using column-major order.
	 * Only works for non-jagged 2D arrays
	 * @param inputs 2D array
	 * @return 1D array
	 */
	public static double[] doubleArrayFrom2DdoubleArrayColMajor(double[][] inputs) {
		double[] outputs = new double[inputs.length * inputs[0].length];
		int x = 0;
		for(double[] a : inputs) {
			for(double b : a) {
				outputs[x++] = b;
			}
		}
		return outputs;
	}
	
	/**
	 * returns a 1D double array from a 2D double array
	 * using row-major order.
	 * Only works for non-jagged 2D arrays
	 * @param inputs 2D array
	 * @return 1D array
	 */
	public static double[] doubleArrayFrom2DdoubleArrayRowMajor(double[][] inputs) {
		double[] outputs = new double[inputs.length * inputs[0].length];
		int x = 0;
		for(int i = 0; i < inputs[0].length; i++) {
			for(int j = 0; j < inputs.length; j++) {
				outputs[x++] = inputs[j][i];
			}
		}
		return outputs;
	}
	
	public static double[] doubleArrayFromList(List<? extends Number> values) {
		double[] array = new double[values.size()];
		int i = 0;
		for (Number n : values) {
			array[i++] = n.doubleValue();
		}
		return array;
	}

	/**
	 * Return true if any element of members is also an element of set
	 *
	 * @param members
	 * @param set
	 * @return
	 */
	public static boolean containsAny(int[] members, int[] set) {
		for (int i = 0; i < members.length; i++) {
			if (member(members[i], set)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if int x is in the array set
	 *
	 * @param x
	 *            element to look for
	 * @param set
	 *            set to look in
	 * @return true if x in set
	 */
	public static boolean member(int x, int[] set) {
		for (int j = 0; j < set.length; j++) {
			if (x == set[j]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if long x is in the array set
	 *
	 * @param x
	 *            element to look for
	 * @param set
	 *            set to look in
	 * @return true if x in set
	 */
	public static boolean member(long x, long[] set) {
		for (int j = 0; j < set.length; j++) {
			if (x == set[j]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if double x is in the array set
	 * 
	 * @param x
	 *            element to look for
	 * @param set
	 *            set to look in
	 * @return true if x in set
	 */
	public static boolean member(double x, double[] set) {
		for (int j = 0; j < set.length; j++) {
			if (x == set[j])
				return true;
		}
		return false;
	}

	/**
	 * Combine two int arrays into one array starting with the elements of the
	 * first array and ending with the elements of the second array
	 *
	 * @param a
	 *            starting elements
	 * @param b
	 *            ending elements
	 * @return combined array
	 */
	public static int[] combineArrays(int[] a, int[] b) {
		int[] result = new int[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	/**
	 * Combine two double arrays into one array starting with the elements of the
	 * first array and ending with the elements of the second array
	 *
	 * @param a
	 *            starting elements
	 * @param b
	 *            ending elements
	 * @return combined array
	 */
	public static double[] combineArrays(double[] a, double[] b) {
		double[] result = new double[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	
	/**
	 * Combine two long arrays into one array starting with the elements of the
	 * first array and ending with the elements of the second array
	 *
	 * @param a
	 *            starting elements
	 * @param b
	 *            ending elements
	 * @return combined array
	 */
	public static long[] combineArrays(long[] a, long[] b) {
		long[] result = new long[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	/**
	 * Count number of occurrences of the given value in the array
	 *
	 * @param value
	 *            value to look for
	 * @param array
	 *            set of values
	 * @return count of occurrences of value
	 */
	public static int countOccurrences(int value, int[] array) {
		int total = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				total++;
			}
		}
		return total;
	}

	/**
	 * Count number of occurrences of the given value in the ArrayList
	 *
	 * @param value
	 *            value to look for
	 * @param array
	 *            ArrayList of values
	 * @return count of occurrences of value
	 */
	public static <T> int countOccurrences(T value, List<T> array) {
		int total = 0;
		for (T e : array) {
			if (e.equals(value)) {
				total++;
			}
		}
		return total;
	}

	/**
	 * return true if all values in vales are the same.
	 *
	 * @param vals
	 *            array of ints
	 * @return true if all elements in vals are the same
	 */
	public static boolean allSame(int[] vals) {
		return countOccurrences(vals[0], vals) == vals.length;
	}

	/**
	 * Count number of occurrences of the given value in the array
	 *
	 * @param value
	 *            value to look for
	 * @param array
	 *            set of values
	 * @return count of occurrences of value
	 */
	public static int countOccurrences(boolean value, boolean[] array) {
		int total = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				total++;
			}
		}
		return total;
	}

	public static <T> int countOccurrences(T value, T[] array) {
		int total = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value || (value != null && value.equals(array[i]))) {
				total++;
			}
		}
		return total;
	}

	/**
	 * Takes array and returns an array with elements in same order, but all
	 * occurrences of "out" have been removed. Result array can therefore be
	 * smaller.
	 *
	 * @param array
	 *            = array to filter
	 * @param out
	 *            = element to remove from array
	 * @return array with no occurrences of out
	 */
	public static int[] filter(int[] array, int out) {
		int[] result = new int[array.length - countOccurrences(out, array)];
		int resultIndex = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != out) {
				result[resultIndex++] = array[i];
			}
		}
		return result;
	}
	
	/**
	 * Takes an array and filters out all of the null occurrences in the array,
	 * returning a new object array with all of the objects except nulls
	 * The objects shift down indices in the array to replace any nulls,
	 * so the returned array will not be the same size if there were any nulls
	 * 
	 * @param array, array to filter
	 * @return result, array without nulls
	 */
	public static <T> T[] filterNull(T[] array) {
		int nonNullCounter = 0;
		for(int i = 0; i < array.length; i++){
			if(array[i] != null){
				nonNullCounter++;
			}
		}
		// Don't want copy, just want array of correct length and type
		T[] result = Arrays.copyOf(array, nonNullCounter);
		int countFilled = 0;
		// overwrites contents of copy
		for(int i = 0; i < array.length; i++){
			if(array[i] != null){
				result[countFilled] = array[i];
				countFilled++;
			}
		}
		return result;
	}

	/**
	 * Returns index of first occurrence of seek in array
	 *
	 * @param array
	 *            = array to search
	 * @param seek
	 *            = element to search for
	 * @return array index of element, -1 on failure
	 */
	public static int position(int[] array, int seek) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == seek) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns true if every member of int lhs is also in rhs, i.e. lhs is a
	 * subset of rhs
	 *
	 * @param lhs
	 *            potential subset
	 * @param rhs
	 *            potential superset
	 * @return whether lhs is subset of rhs
	 */
	public static boolean subset(int[] lhs, int[] rhs) {
		for (int i = 0; i < lhs.length; i++) {
			if (!member(lhs[i], rhs)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if every member of long lhs is also in rhs, i.e. lhs is a
	 * subset of rhs
	 *
	 * @param lhs
	 *            potential subset
	 * @param rhs
	 *            potential superset
	 * @return whether lhs is subset of rhs
	 */
	public static boolean subset(long[] lhs, long[] rhs) {
		for (int i = 0; i < lhs.length; i++) {
			if (!member(lhs[i], rhs)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the n values in xs starting at position i equal the n
	 * values in ys starting at position j.
	 *
	 * @param xs
	 *            = array 1
	 * @param i
	 *            start position in array xs
	 * @param ys
	 *            = array 2
	 * @param j
	 *            start position in array ys
	 * @param n
	 *            length of sequence
	 * @return true if both sequences are equal
	 */
	public static boolean equalSequences(double[] xs, int i, double[] ys, int j, int n) {
		for (int k = 0; k < n; k++) {
			if (xs[i + k] != ys[j + k]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Takes a primitive array of int and returns a corresponding ArrayList of
	 * Integers
	 *
	 * @param ints
	 *            primitive array of int
	 * @return ArrayList of Integers
	 */
	public static ArrayList<Integer> intListFromArray(int[] ints) {
		ArrayList<Integer> result = new ArrayList<Integer>(ints.length);
		for (int i = 0; i < ints.length; i++) {
			result.add(ints[i]);
		}
		return result;
	}

	/**
	 * Takes a primitive array of double and returns a corresponding ArrayList
	 * of Doubles
	 *
	 * @param array
	 *            primitive array of double
	 * @return ArrayList of Doubles
	 */
	public static ArrayList<Double> doubleVectorFromArray(double[] array) {
		ArrayList<Double> result = new ArrayList<Double>(array.length);
		for (int i = 0; i < array.length; i++) {
			result.add(array[i]);
		}
		return result;
	}

	/**
	 * Given a 2D array of doubles, and the index of a column in the array,
	 * return the contents of the column within its own one-dimensional array.
	 *
	 * @param matrix
	 *            primitive 2D array of double (arrays of equal length)
	 * @param col
	 *            index of column in matrix
	 * @return 1D array of the column
	 */
	public static double[] column(double[][] matrix, int col) {
		double[] result = new double[matrix.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = matrix[i][col];
		}
		return result;
	}

	/**
	 * Treat an ArrayList of ArrayLists like a matrix of type T, and pick out
	 * the elements of a designated column, which are placed in the passed
	 * parameter result. The T array result is passed in because generic arrays
	 * cannot be created.
	 *
	 * @param <T>
	 *            Type contained in matrix
	 * @param matrix
	 *            ArrayList of ArrayLists treated like 2D matrix
	 * @param col
	 *            column in matrix to retrieve
	 * @param result
	 *            1D native array of T that will hold column result
	 */
	public static <T> void column(ArrayList<ArrayList<T>> matrix, int col, T[] result) {
		for (int i = 0; i < result.length; i++) {
			result[i] = matrix.get(i).get(col);
		}
	}

	/**
	 * Given parallel arrays of ints and booleans, return an array of int that
	 * only containsAny the values from the array of ints whose corresponding
	 * value in the array of booleans is true.
	 *
	 * @param values
	 *            array of int
	 * @param keep
	 *            array of boolean
	 * @return array subset of values whose keep indices are true
	 */
	public static int[] keepTrueValues(int[] values, boolean[] keep) {
		int count = countOccurrences(true, keep);
		int[] result = new int[count];
		int index = 0;
		for (int i = 0; i < values.length; i++) {
			if (keep[i]) {
				result[index++] = values[i];
			}
		}
		return result;
	}

	public static double[] keepTrueValues(double[] values, boolean[] keep) {
		int count = countOccurrences(true, keep);
		double[] result = new double[count];
		int index = 0;
		for (int i = 0; i < values.length; i++) {
			if (keep[i]) {
				result[index++] = values[i];
			}
		}
		return result;
	}

	/**
	 * Given two equal length double arrays, create a new array whose elements
	 * are the pair-wise sums of of the corresponding elements in a and b.
	 *
	 * @param a
	 *            array of double
	 * @param b
	 *            array of double
	 * @return array a[i]+b[i] for all i
	 */
	public static double[] zipAdd(double[] a, double[] b) {
		assert(a.length == b.length);
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] + b[i];
		}
		return result;
	}

	/**
	 * Given two equal length int arrays, create a new array whose elements are
	 * the pair-wise sums of of the corresponding elements in a and b.
	 *
	 * @param a
	 *            array of int
	 * @param b
	 *            array of int
	 * @return array a[i]+b[i] for all i
	 */
	public static int[] zipAdd(int[] a, int[] b) {
		assert(a.length == b.length);
		int[] result = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] + b[i];
		}
		return result;
	}

	/**
	 * Given two equal length double arrays, create a new array whose elements
	 * are the pair-wise maxes of the corresponding elements in a and b.
	 *
	 * @param a
	 *            array of double
	 * @param b
	 *            array of double
	 * @return array max(a[i],b[i]) for all i
	 */
	public static double[] zipMax(double[] a, double[] b) {
		assert(a.length == b.length);
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = Math.max(a[i], b[i]);
		}
		return result;
	}

	/**
	 * Return new array of all elements in a multiplied by the given scale
	 * factor
	 *
	 * @param a
	 *            array of double
	 * @param scale
	 *            scale factor
	 * @return array of a[i]*scale for all i
	 */
	public static double[] scale(double[] a, double scale) {
		double[] result = Arrays.copyOf(a, a.length);
		for (int i = 0; i < result.length; i++) {
			result[i] *= scale;
		}
		return result;
	}

	/**
	 * Gets set intersection of arrays, assuming each is a set (meaning no
	 * repeated values).
	 *
	 * @param s1
	 *            set 1
	 * @param s2
	 *            set 2
	 * @return set intersection (as array)
	 */
	public static int[] intersection(int[] s1, int[] s2) {
		ArrayList<Integer> result = new ArrayList<Integer>(Math.max(s1.length, s2.length));
		for (int i = 0; i < s1.length; i++) {
			for (int j = 0; j < s2.length; j++) {
				if (s1[i] == s2[j]) {
					result.add(s1[i]);
				}
			}
		}
		return intArrayFromArrayList(result);
	}

	/**
	 * Takes a set and returns a primitive int array containing all elements of
	 * the set in some arbitrary order.
	 *
	 * @param s
	 *            set of Integers
	 * @return int array with same elements
	 */
	public static int[] integerSetToArray(Set<Integer> s) {
		int[] result = new int[s.size()];
		int i = 0;
		for (Integer x : s) {
			result[i++] = x;
		}
		return result;
	}

	/**
	 * Returns sub-array of <code>array</code> from <code>startIndex</code> to
	 * <code>endIndex</code> inclusive
	 *
	 * @param array
	 *            non-null array
	 * @param startIndex
	 *            index in array
	 * @param endIndex
	 *            index in array not before startIndex
	 * @return sub-array
	 */
	public static double[] portion(double[] array, int startIndex, int endIndex) {
		assert startIndex >= 0 && endIndex >= startIndex && endIndex < array.length : "Indeces not in bounds!";
		double[] result = new double[endIndex - startIndex + 1];
		System.arraycopy(array, startIndex, result, 0, endIndex - startIndex + 1);
		return result;
	}

	/**
	 * Determines if two arrays of longs representing sets are equal by seeing
	 * if each is a subset of the other.
	 * 
	 * @param lhs
	 *            set 1
	 * @param rhs
	 *            set 2
	 * @return Whether the sets are equal in contents (not necessarily order)
	 */
	public static boolean setEquality(long[] lhs, long[] rhs) {
		return subset(lhs, rhs) && subset(rhs, lhs);
	}

	public static <T> boolean setEquality(ArrayList<T> lhs, ArrayList<T> rhs) {
		for (T l : lhs) {
			if (!rhs.contains(l)) {
				return false;
			}
		}
		for (T r : rhs) {
			if (!lhs.contains(r)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return an ArrayList containing all of the elements of lhs that are not
	 * members of rhs
	 * 
	 * @param lhs
	 *            ArrayList of type T
	 * @param rhs
	 *            Another ArrayList of type T
	 * @return Set difference of two arrays
	 */
	public static <T> ArrayList<T> setDifference(ArrayList<T> lhs, ArrayList<T> rhs) {
		ArrayList<T> result = new ArrayList<T>();
		for (T x : lhs) {
			if (!rhs.contains(x)) {
				result.add(x);
			}
		}
		return result;
	}

	public static int[] setDifference(int[] lhs, int[] rhs) {
		return intArrayFromArrayList(setDifference(intListFromArray(lhs), intListFromArray(rhs)));
	}

	public static int[] setDifference(int[] lhs, ArrayList<Integer> rhs) {
		return intArrayFromArrayList(setDifference(intListFromArray(lhs), rhs));
	}

	/**
	 * Find and return first instance of given item
	 * 
	 * @param <T>
	 *            should have equals implemented
	 * @param array
	 *            to search
	 * @param item
	 *            to look for
	 * @return first instance of item in array
	 */
	public static <T> T firstInstance(T[] array, T item) {
		assert item != null : "Don't search for null item";
		for (T x : array) {
			if (item.equals(x)) {
				return x;
			}
		}
		return null;
	}

	/**
	 * Return number of unique elements in an array
	 * 
	 * @param array
	 *            Array of primitive ints
	 * @return Cardinality of a set corresponding to the input array.
	 */
	public static int setCardinality(int[] array) {
		HashSet<Integer> counted = new HashSet<Integer>();
		for (int i = 0; i < array.length; i++) {
			if (!counted.contains(array[i])) { // Is this even necessary?
				counted.add(array[i]);
			}
		}
		return counted.size();
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(portion(new double[] { 0, 1, 2, 3, 4 }, 1, 3)));
	}
}
