/**
 * 
 */
package edu.southwestern.util.random;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;

/**
 * This is a JUnit test for the core methods of the Resumable Random class,
 * getSeed which gets the current seed of the Random, and hardSetSeed which sets
 * the seed to a new value.
 * 
 * @author gillespl
 *
 */
public class ResumableRandomTest {

	static final int SIZE = 100;
	static final long seed = 10;
	ResumableRandom randomGenerator = new ResumableRandom();

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests whether or not the method can get and reset the seed for a
	 * resumable random number generator
	 */
	@Test
	public void test_getSeed() {

		long seed = randomGenerator.getSeed();
		int[] test1 = new int[SIZE];
		for (int i = 0; i < test1.length; i++) {
			test1[i] = randomGenerator.nextInt();
		}
		randomGenerator.hardSetSeed(seed);
		int[] test2 = new int[SIZE];
		for (int i = 0; i < test2.length; i++) {
			test2[i] = randomGenerator.nextInt();
		}
		for (int i = 0; i < test1.length && i < test2.length; i++) {
			assertEquals(test1[i], test2[i]);
		}
	}

	@Test
	public void test_hardSetSeed() {
		ResumableRandom r2 = new ResumableRandom();
		randomGenerator.hardSetSeed(seed);
		long l1 = randomGenerator.getSeed();
		r2.hardSetSeed(seed);
		long l2 = r2.getSeed();
		assertEquals(l1, l2);
	}

}
