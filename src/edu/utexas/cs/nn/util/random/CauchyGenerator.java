/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.util.random;

/**
 *
 * @author Jacob Schrum
 */
public class CauchyGenerator implements RandomGenerator {

    public double randomOutput() {
        return RandomNumbers.randomCauchyValue();
    }

}
