package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class NorthSouthPistonCountFitness extends PistonCountFitness{

	public NorthSouthPistonCountFitness() {
		super(new Orientation[] {Orientation.NORTH, Orientation.SOUTH});
	}

}
