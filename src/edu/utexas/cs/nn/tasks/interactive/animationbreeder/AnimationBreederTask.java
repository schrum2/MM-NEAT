package edu.utexas.cs.nn.tasks.interactive.animationbreeder;

import java.awt.image.BufferedImage;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;

public class AnimationBreederTask<T extends Network> extends InteractiveEvolutionTask<T>{
	
	public static final int CPPN_NUM_INPUTS	= 5;
	public static final int CPPN_NUM_OUTPUTS = 3;

	public AnimationBreederTask() throws IllegalAccessException {
		super();
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias", "time" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "hue-value", "saturation-value", "brightness-value" };
	}

	@Override
	protected String getWindowTitle() {
		return "AnimationBreeder";
	}

	@Override
	protected void save(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	@Override
	public int numCPPNOutputs() {
		return CPPN_NUM_OUTPUTS;
	}

}
