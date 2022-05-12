package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.util.random.RandomNumbers;

@SuppressWarnings("rawtypes")
public abstract class ConvertCPPN2GANtoDirect2GANMutation extends Mutation {

	protected double rate;

	public ConvertCPPN2GANtoDirect2GANMutation() {
		super();
	}

	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}

}