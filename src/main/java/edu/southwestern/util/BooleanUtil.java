
package edu.southwestern.util;

/**
 * Utility class used for the evaluation of boolean Arrays.
 * Able to check if any or all members of a boolean Array are true.
 * One function checks if any member of the Array is true,
 * Other function checks if all members of the Array are true.
 *
 * @author Jacob Schrum
 */
public class BooleanUtil {

	/**
	 * Return true if any members of array are true
	 * 
	 * @param bs array of booleans
	 * @return whether any is true
	 */
	public static boolean any(boolean[] bs) {
		for (int i = 0; i < bs.length; i++) {
			if (bs[i]) {
				return true;
			}
		}
		return false;
	}

        /**
         * Return true if all members of array are true
         * 
         * @param bs array of booleans
         * @return whether all are true
         */
	public static boolean all(boolean[] bs) {
		for (int i = 0; i < bs.length; i++) {
			if (!bs[i]) {
				return false;
			}
		}
		return true;
	}
}
