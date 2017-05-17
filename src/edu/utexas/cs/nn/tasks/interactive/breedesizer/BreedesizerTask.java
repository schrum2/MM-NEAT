package edu.utexas.cs.nn.tasks.interactive.breedesizer;

import java.awt.image.BufferedImage;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;

public class BreedesizerTask extends InteractiveEvolutionTask {

	public BreedesizerTask() throws IllegalAccessException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] outputLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWindowTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		// TODO Auto-generated method stub
		return null;
	}

}
