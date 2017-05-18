package edu.utexas.cs.nn.tasks.interactive.picbreeder;

import java.awt.image.BufferedImage;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;

public class PicBreederTaskGeneralizedCopy extends InteractiveEvolutionTask {

	public PicBreederTaskGeneralizedCopy() throws IllegalAccessException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* After save and setEffectCheckbox are generalized so that they can be applied to both 
	 * Breedesizer and Picbreeder, specified method calls will have to be included here.
	 */

	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "hue-value", "saturation-value", "brightness-value" };
	}

	@Override
	protected String getWindowTitle() {
		return "Picbreeder";
	}

	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		return GraphicsUtil.imageFromCPPN(phenotype, width, height, inputMultipliers);
	}

}
