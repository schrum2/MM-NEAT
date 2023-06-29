package edu.southwestern.tasks.evocraft.mutation;

import java.util.HashMap;
import java.util.HashSet;

import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;

public abstract class MinecraftShapeMutation extends Mutation<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> {
	// Every mutation has its own rate of occurrence
	protected double rate;

	/**
	 * Constructor retrieves the appropriate rate from the parameters.
	 *
	 * @param rateName
	 *            Parameter label for this mutation rate.
	 */
	public MinecraftShapeMutation(String rateName) {
		this(Parameters.parameters.doubleParameter(rateName));
	}


	/**
	 * Constructor is provided with actual mutation rate.
	 * 
	 * @param rate
	 *            Rate of mutation: between 0 and 1
	 */
	public MinecraftShapeMutation(double rate) {
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}
	
	/**
	 * Only perform the mutation if a random double is less than the mutation
	 * rate.
	 * 
	 * @return Whether to perform mutation
	 */
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}
}
