package edu.southwestern.util.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.util.sound.SoundToArray;

/**
 * Various useful methods with arrays
 *
 * @author Jacob Schrum
 */
public class ArrayUtil {

	/**
	 * Remove all instances of toRemove from the String array.
	 * There is probably a better way to do this with streams in the latest version of Java.
	 * @param source String array
	 * @param toRemove String to remove from the array
	 * @return new array that has all elements of the original, except occurrences of toRemove
	 */
	public static String[] filterString(String[] source, String toRemove) {
		LinkedList<String> result = new LinkedList<>();
		for(String s : source) {
			if(!s.equals(toRemove)) {
				result.add(s);
			}
		}
		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * Move all contents to the right a certain number of spaces
	 * @param array Array to rotate
	 * @param spaces Spaces to move the indexes
	 */
	public static void rotateRight(Object[] array, int spaces) {
		Object[] temp = new Object[array.length];
		for(int i = 0; i < array.length; i++) {
			temp[(i + spaces) % array.length] = array[i];
		}
		for(int i = 0; i < array.length; i++) {
			array[i] = temp[i];
		}
	}
	
	/**
	 * Print values in the array from the start index to the end index (inclusive)
	 * @param array Array to print values from
	 * @param start First index to print
	 * @param end Last index to print
	 */
	public static void printArrayRange(double[] array, int start, int end) {
		assert start >= 0 : "Start not in bounds: " + start;
		assert start <= end : "Start not less than end : " + start + ", " + end;
		assert end < array.length : "End not in bounds: " + end;
		for(int i = start; i < end; i++) {
			System.out.print(array[i] + ", ");
		}
		System.out.println(array[end]);
	}
	
	/**
	 * Get max length across all sub-arrays.
	 * @param array 2D array to check
	 * @return Maximum across all array[i] for all valid i
	 */
	public static int maxArrayLengthFrom2DArray(double[][] array) {
		int max = 0;
		for(int i = 0; i < array.length; i++) {
			max = Math.max(max, array[i].length);
		}
		return max;
	}
	
	public static double[] arrayOfMaxLengthsFrom2DArray(double[][] array, int maxArrayLength) {
		double max = 0;
		double[] result = new double[maxArrayLength];
		for(int i = 0; i < array.length; i++) {
			for(int j = 0; j < array[i].length; j++) {
				max = Math.max(max, Math.abs(array[i][j]));
				result[j] += array[i][j];
			}
		}
		return result;
	}
	
	/**
	 * Return primitive double array of given size containing all ones
	 *
	 * @param size 
	 * 			desired size of array
	 * @return array of doubles of size 'size', all of which are 1
	 */
	public static double[] doubleOnes(int size) {
		double[] ones = new double[size];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = 1;
		}
		return ones;
	}
	
	/**
	 * Return primitive double array of given size containing all negative ones
	 *
	 * @param size 
	 * 			desired size of array
	 * @return array of doubles of size 'size', all of which are -1
	 */
	public static double[] doubleNegativeOnes(int size) {
		double[] ones = new double[size];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = -1;
		}
		return ones;
	}
	
	/**
	 * Return primitve double array of given size containing only
	 * copies of the specified value.
	 * 
	 * @param size Desired size of array
	 * @param num Specified number to fill array
	 * @return Array of doubles of size 'size', all of which are 'num'
	 */
	public static double[] doubleSpecified(int size, double num) {
		double[] spec = new double[size];
		for (int i = 0; i < spec.length; i++) {
			spec[i] = num;
		}
		return spec;
	}

	/**
	 * Return primitive int array of given size containing all ones
	 *
	 * @param size : desired size of array
	 * @return array of integers of size 'size', all of which are 1
	 */
	public static int[] intOnes(int size) {
		int[] ones = new int[size];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = 1;
		}
		return ones;
	}
	
	/**
	 * Returns primitive int array of a given size containing only
	 * copies of the specified number
	 * @param size Desired size of array
	 * @param num Specified integer to fill array
	 * @return Array of ints of size 'size' where every value equals num
	 */
	public static int[] intSpecified(int size, int num) {
		int[] ones = new int[size];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = num;
		}
		return ones;
	}

	/**
	 * Return an array of numbers from start (inclusive) to 
	 * end (exclusive) with the given step size between elements.
	 * Modeled after the Python range function.
	 * 
	 * @param start first value in result array
	 * @param end no value in result array is greater than or equal to this
	 * @param step difference between adjacent array elements
	 * @return array of start, start+step, start+2*step, ... for values less than end
	 */
	public static int[] range(int start, int end, int step) {
		int[] result = new int[(int) Math.ceil((end - start)/(1.0 * step))];
		for(int i = 0; i < result.length; i++) {
			result[i] = start + i*step;
		}
		return result;
	}
	
	/**
	 * creates a new array of primitive ints with the same contents as List<Integer> input
	 * 
	 * @param path List of type Integer to be copied
	 * @return an array with the same size and contents as input List
	 */
	public static int[] intArrayFromArrayList(List<Integer> path) {
		int[] arrayPath = new int[path.size()];
		for (int i = 0; i < arrayPath.length; i++) {
			arrayPath[i] = path.get(i);
		}
		return arrayPath;
	}
	
	/**
	 * Creates a new 2D array of primitive ints with the same 
	 * contents as a List<List<Integer>> input
	 * 
	 * @param path List of lists of type Integer to be transformed
	 * @return A 2D array with the same size and contents as input list of lists
	 */
	public static int[][] int2DArrayFromListOfLists(List<List<Integer>> path) {
		int[][] arrayPath = new int[path.size()][path.get(0).size()];
		for (int i = 0; i < arrayPath.length; i++) {
			arrayPath[i] = intArrayFromArrayList(path.get(i));
		}
		return arrayPath;
	}	

	/**
	 * returns a 1D double array from a 2D double array
	 * using column-major order.
	 * Only works for non-jagged 2D arrays
	 * 
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
	
	/**
	 * Returns an array of doubles from an ArrayList of numbers
	 * 
	 * @param values 
	 * 			ArrayList of numbers (any numeric type) 
	 * @return an array of doubles with the same length and contents as the list
	 */
	public static double[] doubleArrayFromList(List<? extends Number> values) {
		double[] array = new double[values.size()];
		int i = 0;
		for (Number n : values) {
			array[i++] = n.doubleValue();
		}
		return array;
	}
	
	public static double[] doubleArrayFromIntegerArray(Integer[] values) {
		double[] array = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * Convert linear INDArray to a 1D double array
	 * @param row INDArray with just one row
	 * @return corresponding double array
	 */
	public static double[] doubleArrayFromINDArray(INDArray row) {
		double[] array = new double[(int) row.length()];
		for(int i = 0; i < array.length; i++) {
			array[i] = row.getDouble(i);
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
			if(ArrayUtils.contains(set, members[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Combine two String arrays into one array starting with the elements of the
	 * first array and ending with the elements of the second array
	 *
	 * @param a
	 *            starting elements
	 * @param b
	 *            ending elements
	 * @return combined array
	 */
	public static String[] combineArrays(String[] a, String[] b) {
		String[] result = new String[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
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

	/**
	 * returns the number of times that 'value' occurs within 'array' 
	 * 
	 * @param value 
	 * 			data to be searched for
	 * @param array
	 * 			array that may contain instances of that data 
	 * @return
	 */
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
			if (!ArrayUtils.contains(rhs, lhs[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check if the array contains any null entries
	 * @param array Array of Objects
	 * @return true if any entries are null, false otherwise
	 */
	public static boolean anyNull(Object[] array) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == null) {
				return true;
			}
		}
		return false;
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
			if (!ArrayUtils.contains(rhs, lhs[i])) {
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
		return zipAdd(a,b,a.length);
	}
		
	/**
	 * Creates an array adding two arrays together at their respective indexes that
	 * may not necessarily be the same length. Length of result array is specified
	 * 
	 * @param a array of doubles
	 * @param b array of doubles
	 * @param length desired size of result array
	 * @return array a[i] + b[i] for all i
	 */
	public static double[] zipAdd(double[] a, double[] b, int length) {
		double[] result = new double[length];
		for (int i = 0; i < a.length; i++) {
			result[i] += a[i];
		}
		for (int i = 0; i < b.length; i++) {
			result[i] += b[i];
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
	 * Adds the array values at each index of two audio files represented as double arrays, combining them into a single double array.
	 * Value is decided based on length of shortest array.
	 * 
	 * @param fileA First audio file being overlapped
	 * @param fileB Second audio file being overlapped
	 * @return array of doubles with values that are the sum of the two input audio files at each index
	 */
	public static double[] overlap(String fileA, String fileB) {
		double[] a = SoundToArray.read(fileA); //reads string files in as double arrays
		double[] b = SoundToArray.read(fileB);
		double[] adjustedA = Arrays.copyOf(a, Math.min(a.length, b.length)); //copy of itself but adjusted based on length of shortest array
		double[] adjustedB = Arrays.copyOf(b, Math.min(a.length, b.length)); //copy of itself but adjusted based on length of shortest array
		double[] overlapped = ArrayUtil.zipAdd(adjustedA, adjustedB); //zipAdd() is a method in ArrayUtil that adds values at each index of two arrays into a new array
		return overlapped;
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
	 * Pair-wise multiplication of elements in the two arrays to get one result array of products
	 * @param a input array 
	 * @param b input array
	 * @return array of products
	 */
	public static double[] zipMultiply(double[]a, double[] b) {
		assert(a.length == b.length);
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * b[i];
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
		assert startIndex >= 0 && endIndex >= startIndex && endIndex < array.length : "Indices not in bounds!";
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
	public static <T> ArrayList<T> setDifference(List<T> lhs, List<T> rhs) {
		ArrayList<T> result = new ArrayList<T>();
		for (T x : lhs) {
			if (!rhs.contains(x)) {
				result.add(x);
			}
		}
		return result;
	}

	/**
	 * Return an ArrayList containing all of the elements of lhs that are not
	 * members of rhs
	 * 
	 * @param lhs
	 *            Array of primitive int
	 * @param rhs
	 *            Another Array of primitive int
	 * @return Set difference of two arrays
	 */
	public static int[] setDifference(int[] lhs, int[] rhs) {
		return intArrayFromArrayList(setDifference(intListFromArray(lhs), intListFromArray(rhs)));
	}

	/**
	 * Return an ArrayList containing all of the elements of lhs that are not
	 * members of rhs
	 * 
	 * @param lhs
	 *            Array of primitive int
	 * @param rhs
	 *            ArrayList of type T
	 * @return Set difference of two arrays
	 */
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
	 * True if the item is contained in the array
	 * 
	 * @param <T> type of item
	 * @param array Array to search
	 * @param item item to look for
	 * @return true if present, false otherwise
	 */
	public static <T> boolean contains(T[] array, T item) {
		for(T x : array) {
			if(x.equals(item)) return true;
		}
		return false;
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

	/**
	 * Converts a 1D INDArray from DL4J into an ArrayList of Doubles
	 * @param values An INDArray that must be 1D
	 * @return ArrayList with same values in same order
	 */
	public static ArrayList<Double> doubleVectorFromINDArray(INDArray values) {
		ArrayList<Double> result = new ArrayList<Double>((int) values.length());
		for (int i = 0; i < values.length(); i++) {
			result.add(values.getDouble(0,i));
		}
		return result;
	}
	
	/**
	 * For each pair of elements in the two separate 1D INDArrays
	 * at the same index, take the min and save the result in the
	 * toModify array.
	 * @param toModify Array that is modified to contain minimum values
	 * @param other Other array checked for smaller values
	 */
	public static void pairwiseMinimum(INDArray toModify, INDArray other) {
		assert toModify.length() == other.length() : "INDArrays not the same size";
		int size = (int) toModify.length();
		for(int i = 0; i < size; i++) {
			toModify.put(0, i, Math.min(toModify.getDouble(0,i), other.getDouble(0,i)));
		}		
	}
	
	/**
	 * Return a rotated version of a List of Lists representing a 2D grid.
	 * 
	 * @param <T> Any type contained in the nested lists
	 * @param original 2D List of Lists
	 * @return Rotated List of Lists
	 */
	public static <T> List<List<T>> rotateCounterClockwise(List<List<T>> original) {
		List<List<T>> result = new ArrayList<List<T>>();
		int width = original.get(0).size();
		for(int j = 0; j < width; j++) {
			result.add(new ArrayList<T>(original.size()));
		}
		// transfer
		for(int i = 0; i < original.size(); i++) {
			for(int j = 0; j < width; j++) {
				List<T> row = original.get(i);
				T previous = row.get(j);
				result.get(j).add(previous);
			}
		}
		return result;
	}

	/**
	 * Turn 3D array into 1D array in row major order
	 * @param array 3D array, NOT jagged in any dimension
	 * @return corresponding 1D array in row major order
	 */
	public static double[] flatten3DDoubleArray(double[][][] array) {
		double[] result = new double[array.length*array[0].length*array[0][0].length];
		int index = 0;
		for(double[][] slice: array) {
			for(double[] row: slice) {
				System.arraycopy(row, 0, result, index, row.length);
				index += row.length;
			}
		}
		return result;
	}
}
