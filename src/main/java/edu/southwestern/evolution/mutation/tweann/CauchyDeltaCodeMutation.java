/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.util.random.CauchyGenerator;

/**
 * Perturbs ALL weights (100%) according to Cauchy distribution
 * 
 * @author Jacob Schrum
 */
public class CauchyDeltaCodeMutation extends AllWeightMutation {
	/**
	 * Default constructor
	 */
	public CauchyDeltaCodeMutation() {
		super(new CauchyGenerator(), 1.0);
	}
}
