package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class UpDownPistonCountFitness extends PistonCountFitness {

	public UpDownPistonCountFitness() {
		super(new Orientation[] {Orientation.UP, Orientation.DOWN});
	}

}
