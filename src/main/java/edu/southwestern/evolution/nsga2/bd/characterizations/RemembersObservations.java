package edu.southwestern.evolution.nsga2.bd.characterizations;

import java.util.List;

public interface RemembersObservations {
	/**
	 * Adds observation/input to the set for Behavioral Diversity with intelligent vectors from past experiences 
	 * @param an observation/set of inputs
	 */
	public void addObservation(double[] inputs);
	
	/**
	 * @return set of observations/inputs for Behavioral Diversity with intelligent vectors from past experiences
	 */
	public List<double[]> recallObservations();
	
	/**
	 * Clears set of observations/inputs. For use at the end of a generation. 
	 * For Behavioral Diversity with intelligent vectors from past experiences
	 */
	public void clearObservations();
}
