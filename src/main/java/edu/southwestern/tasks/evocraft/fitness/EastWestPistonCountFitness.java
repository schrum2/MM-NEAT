package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class EastWestPistonCountFitness extends PistonCountFitness {

	public EastWestPistonCountFitness() {
		super(new Orientation[] {Orientation.EAST, Orientation.WEST});
	}

}
