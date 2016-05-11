/**
 * 
 */
package edu.utexas.cs.nn.util.random;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author gillespl
 *
 */
public class ResumableRandomTest {

	ResumableRandom randomGenerator = new ResumableRandom();
	
	/**
	 * Tests whether or not the method can get and reset the seed for a resumable random number generator
	 */
	@Test
	public void test() {
		long seed = randomGenerator.getSeed();
		int[] test1 = new int[100];
		for(int i = 0; i < test1.length; i++) {
			test1[i] = randomGenerator.nextInt();
		}
		randomGenerator.hardSetSeed(seed);
		int[] test2 = new int[100];
		for(int i = 0; i < test2.length; i++) {
			test2[i] = randomGenerator.nextInt();
		}
		for(int i = 0; i < test1.length && i < test2.length; i++) {
			assertEquals(test1[i], test2[i]);
		}
	}

}
