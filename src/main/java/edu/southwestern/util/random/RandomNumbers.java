package edu.southwestern.util.random;

import edu.southwestern.parameters.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A central point for all random number generation to go through. 
 * Makes sure all randomness is linked to a single random seed.
 *
 * @author Jacob Schrum
 */
public class RandomNumbers {

	public static Random randomGenerator = new Random();

	/*
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

	/**
	 * Give random generator a specific new seed
	 *
	 * @param seed
	 *            seed to use
	 */
	public static void reset(int seed) {
		System.out.println("Reset random seed to: " + seed);
		randomGenerator = new Random(seed);
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

	public static double randomSign() {
		return randomGenerator.nextBoolean() ? 1 : -1;
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
	 * For testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 1000;
		double[] cauchies = new double[size];
		double[] gaussians = new double[size];
		for (int i = 0; i < size; i++) {
			cauchies[i] = randomCauchyValue();
			gaussians[i] = randomGenerator.nextGaussian();
		}
		Arrays.sort(cauchies);
		Arrays.sort(gaussians);
		System.out.println("Cauchy\tGaussian");
		for (int i = 0; i < size; i++) {
			System.out.println(cauchies[i] + "\t" + gaussians[i]);
		}
	}

	/**
	 * Return true if random double in [0,1] is less than input, and false otherwise
	 * @param chance Chance of true result
	 * @return true with chance probability, false otherwise
	 */
	public static boolean randomCoin(double chance) {
		return randomGenerator.nextDouble() < chance;
	}
}
