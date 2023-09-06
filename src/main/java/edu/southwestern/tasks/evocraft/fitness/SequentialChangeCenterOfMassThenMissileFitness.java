package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;

/**
 * Shapes have to be flying before being able to earn any fitness from destroying targets.
 * 
 * @author Jacob Schrum
 *
 */
public class SequentialChangeCenterOfMassThenMissileFitness extends SequentialStackedFitness {

	// This wiggle room in the fitness threshold allows for some penalty from leftover blocks,
	// but shapes should still be flying before getting any missile fitness rewarded.
	private static final double WIGGLE_ROOM = 20; 

	public SequentialChangeCenterOfMassThenMissileFitness() {
		this(new ChangeCenterOfMassFitness());
	}
	
	private SequentialChangeCenterOfMassThenMissileFitness(ChangeCenterOfMassFitness ccomFitnessInstance) {
		super(Arrays.asList(new MinecraftFitnessFunction[] {ccomFitnessInstance, new MissileFitness()}),Arrays.asList(new Double[] {ccomFitnessInstance.maxFitness() - WIGGLE_ROOM}));
	}

	@Override
	public boolean needsSimulation() {
		return true; // for both
	}

}
