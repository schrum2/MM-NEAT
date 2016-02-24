/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util;

/**
 *
 * @author Jacob Schrum
 */
public class BooleanUtil {

    /**
     * Return true of all members of array are true
     * @param bs
     * @return 
     */
    public static boolean any(boolean[] bs) {
        for (int i = 0; i < bs.length; i++) {
            if (bs[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean all(boolean[] bs) {
        for (int i = 0; i < bs.length; i++) {
            if (!bs[i]) {
                return false;
            }
        }
        return true;
    }
}
