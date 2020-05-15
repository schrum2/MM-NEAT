package edu.southwestern.util.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.southwestern.parameters.Parameters;

/**
 * A central point for all random number generation to go through. 
 * Makes sure all randomness is linked to a single random seed.
 *
 * @author Jacob Schrum
 */
public class RandomNumbers {

	public static Random randomGenerator = new Random();

	/**
	 * Reset random generator based on seed from parameter file
	 */
	public static void reset() {
		int seed = Parameters.parameters.integerParameter("randomSeed");
		if (seed != -1) { // Control algorithmic randomness
			reset(seed);
		} else {
			randomGenerator = new Random();
		}
	}

	public static final boolean RANDOM_DEBUG = false;
	
	/**
	 * Give random generator a specific new seed
	 *
	 * @param seed
	 *            seed to use
	 */
	@SuppressWarnings("serial")
	public static void reset(int seed) {
		System.out.println("Reset random seed to: " + seed);
		// If RANDOM_DEBUG is true, then extensive information will be printed to the console whenever
		// a random number is generated.
		randomGenerator = !RANDOM_DEBUG ? new Random(seed) : new Random(seed) {
			public int nextInt(int x) {
				int result = super.nextInt(x);
				System.out.println("int: " + result + " (out of "+x+")");
				new IllegalArgumentException().printStackTrace(System.out);
				return result;
			}
			
			public double nextDouble() {
				double result = super.nextDouble();
				System.out.println("double: " + result);
				new IllegalArgumentException().printStackTrace(System.out);
				return result;
			}

			public boolean nextBoolean() {
				boolean result = super.nextBoolean();
				System.out.println("boolean: " + result);
				new IllegalArgumentException().printStackTrace(System.out);
				return result;
			}
			
			public void nextBytes(byte[] bs) {
				super.nextBytes(bs);
				System.out.println("bytes: " + Arrays.toString(bs));
				new IllegalArgumentException().printStackTrace(System.out);
			}
		};
	}

	/**
	 * Returns random number in range [-1,1]
	 */
	public static double fullSmallRand() {
		return (randomGenerator.nextDouble() * 2.0) - 1.0;
	}

	/**
	 * Random number in range [lower,upper)
	 *
	 * @param lower
	 *            lower bound
	 * @param upper
	 *            upper bound
	 * @return random number in range
	 */
	public static double boundedRandom(double lower, double upper) {
		double rand = randomGenerator.nextDouble();
		rand *= (upper - lower);
		rand += lower;
		return rand;
	}

	/**
	 * Array with random elements in range [-1,1]
	 *
	 * @param size
	 *            size of array
	 * @return array with "size" random elements
	 */
	public static double[] randomArray(int size) {
		double[] array = new double[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = fullSmallRand();
		}
		return array;
	}

	/**
	 * Array with random elements in range [lower,upper). Length of lower and
	 * upper must be equal.
	 *
	 * @param lower
	 *            lower bounds for each index
	 * @param upper
	 *            upper bounds for each index
	 * @return array of random elements, each in the corresponding range
	 */
	public static double[] randomBoundedArray(double[] lower, double[] upper) {
		double[] array = new double[lower.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = boundedRandom(lower[i], upper[i]);
		}
		return array;
	}

	/**
	 * Array with random integers, with each slot restricted in range by the
	 * corresponding slot in ceilings.
	 *
	 * @param ceilings
	 *            max (exclusive) value for each slot (min is 0 inclusive).
	 * @return random array of integers
	 */
	public static Integer[] randomIntArray(int[] ceilings) {
		Integer[] result = new Integer[ceilings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = randomGenerator.nextInt(ceilings[i]);
		}
		return result;
	}

	/**
	 * The elements of probabilities must sum to 1.0
	 *
	 * @param probs
	 *            array represents a probability distribution and must sum to
	 *            1.0
	 * @return probabilistically selected index
	 */
	public static int probabilisticSelection(double[] probs) {
		double randomValue = randomGenerator.nextDouble();
		double sum = 0;
		int selection = -1;
		// Extra case needed because floating point arithmetic may add to
		// slightly more than 1
		while (sum < randomValue) {
			selection++;
			sum += probs[selection];
		}
		return Math.min(selection, probs.length - 1);
	}

	/**
	 * Of the integers in [0,ceiling) pick num of them and return in an array
	 *
	 * @param num
	 *            number of distinct random values to get 
	 * @param ceiling
	 *            possible values are integers less than ceiling
	 * @return array of num chosen values
	 */
	public static int[] randomDistinct(int num, int ceiling) {
		assert ceiling > 0 : "There must be some numbers to choose from";
		assert num <= ceiling : "Can't choose more numbers than are available";
		
		ArrayList<Integer> all = new ArrayList<Integer>(ceiling);
		for (int i = 0; i < ceiling; i++) {
			all.add(i);
		}
		Collections.shuffle(all, randomGenerator);
		int[] result = new int[num];
		for (int i = 0; i < num; i++) {
			result[i] = all.get(i);
		}
		return result;
	}

	/**
	 * Randomly return 1 or -1
	 * @return 1 or -1, 50% chance
	 */
	public static double randomSign() {
		return randomGenerator.nextBoolean() ? 1 : -1;
	}
	
	/**
	 * Random boolean
	 * @return true or false, 50% chance of each
	 */
	public static boolean coinFlip() {
		return randomGenerator.nextBoolean();
	}

	/**
	 * Randomly pick x of the integers in the range [0,y) where no value can be
	 * picked more than once.
	 *
	 * @param x
	 *            number of random values to pick
	 * @param y
	 *            values to choose from
	 * @return array of chosen values
	 */
	public static int[] randomXofY(int x, int y) {
		assert y > 0 : "There must be some numbers to choose from";
		assert x <= y : "Can't choose more numbers than are available";

		ArrayList<Integer> source = new ArrayList<Integer>(y);
		for (int i = 0; i < y; i++) {
			source.add(i);
		}
		Collections.shuffle(source, randomGenerator);

		int[] result = new int[x];
		for (int i = 0; i < x; i++) {
			result[i] = source.get(i);
		}
		return result;
	}

	/**
	 * The original Java ESP code uses this 0.3 magic number for the weight
	 * range, and testing indicates that it generally produces values of
	 * magnitude less than 3, but larger outliers are possible. I can only
	 * assume that Gomez (or whoever came up with this value) used some sort of
	 * heuristics to choose it. Presumably it works well for delta coding, which
	 * is how I intend to use it.
	 * 
	 * @return random Cauchy value
	 */
	public static double randomCauchyValue() {
		return randomCauchyValue(0.3);
	}

	/**
	 * Code clipped from the Java implementation of Enforced Sub-Populations by
	 * Alan Oursland, Original code by Faustino Gomez
	 * 
	 * Computes random value from a Cauchy distribution within restricted range.
	 * 
	 * @param wtrange
	 *            influences the potential magnitude of values
	 * @return random result
	 */
	public static double randomCauchyValue(double wtrange) {
		double u = 0.5, Cauchy_cut = 10.0;

		while (u == 0.5) {
			u = randomGenerator.nextDouble();
		}
		u = wtrange * Math.tan(u * Math.PI);
		if (Math.abs(u) > Cauchy_cut) { // disallows extreme ends of tails
			return randomCauchyValue(wtrange);
		} else {
			return u;
		}
	}

	/**
	 * Takes in an array list and selects a random index. Originally implemented
	 * for function list.
	 * 
	 * @param list
	 *            of <T>
	 * @return random T of <T>
	 */
	public static <T> T randomElement(List<T> list) {
		int index = randomGenerator.nextInt(list.size());
		return list.get(index);
	}
	
	/**
	 * Return new list of num randomly selected, distinct items from the list,
	 * without changing the original order of elements in list.
	 * 
	 * @param list List of items to select
	 * @param num Number of items to select
	 * @return List of num random items from list
	 */
	public static <T> ArrayList<T> randomChoose(List<T> list, int num) {
		return randomChoose(list,num,randomGenerator);
	}
	
	/**
	 * Same as above, but allows a Random generator other than the static one 
	 * in this class to be used.
	 * @param list List of values
	 * @param num Number of items to select
	 * @param rand Random generator
	 * @return list of num distinct elements from list 
	 */
	public static <T> ArrayList<T> randomChoose(List<T> list, int num, Random rand) {
		if(num > list.size()) throw new IllegalArgumentException("Number of items "+num+" greater than size "+list.size());
		ArrayList<T> result = new ArrayList<T>(num);
		ArrayList<Integer> indices = new ArrayList<>(num);
		for(int i = 0; i < num; i++) {
			indices.add(i); // All indices in the list
		}
		// Shuffle the list of indices, not the original list
		Collections.shuffle(indices, rand);
		for(int i = 0; i < num; i++) {
			// Randomly shuffled indices leads to a random selection from the list
			result.add(list.get(indices.get(i)));
		}
		return result;
	}
	
	/**
	 * Same as above, but allows a Set as input.
	 * @param set Set of elements
	 * @param num Number of elements to select
	 * @param rand Random generator
	 * @return List of num elements from set
	 */
	public static <T> ArrayList<T> randomChoose(Set<T> set, int num, Random rand) {
		ArrayList<T> temp = new ArrayList<>(set.size());
		temp.addAll(set);
		return randomChoose(temp, num, rand);
	}

	/**
	 * Takes in an array and selects a random index. 
	 * 
	 * @param array of <T>
	 * @return random T of <T>
	 */
	public static <T> T randomElement(T[] list) {
		int index = randomGenerator.nextInt(list.length);
		return list[index];
	}

	/**
	 * Return true if random double in [0,1] is less than input, and false otherwise
	 * @param chance Chance of true result
	 * @return true with chance probability, false otherwise
	 */
	public static boolean randomCoin(double chance) {
		return randomGenerator.nextDouble() < chance;
	}
	
	/**
	 * Generate and return an array of random bytes of a given length
	 * @param len length of byte array
	 * @return random byte array
	 */
	public static byte[] randomByteArray(int len) {
		byte[] b = new byte[len];
		randomGenerator.nextBytes(b);
		return b;
	}
	
	/**
	 * For testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RandomNumbers.reset(0);
		for(int i = 0; i < 100; i++) {
			RandomNumbers.randomGenerator.nextInt(100);
		}
	}
}
