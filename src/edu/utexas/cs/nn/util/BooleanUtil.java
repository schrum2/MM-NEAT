
package edu.utexas.cs.nn.util;

/**
 *
 * @author Jacob Schrum
 */
public class BooleanUtil {

	/**
	 * Return true of all members of array are true
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
