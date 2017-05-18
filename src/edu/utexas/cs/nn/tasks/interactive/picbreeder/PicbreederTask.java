package edu.utexas.cs.nn.tasks.interactive.picbreeder;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;

public class PicbreederTask<T extends Network> extends InteractiveEvolutionTask<T> {
	
	public static final int CPPN_NUM_INPUTS	= 4;
	public static final int CPPN_NUM_OUTPUTS = 3;

	//Indices of inputMultiplier effects
	private static final int XEFFECT_CHECKBOX_INDEX = -25;
	private static final int YEFFECT_CHECKBOX_INDEX = -26;
	private static final int CENTERDISTANCE_CHECKBOX_INDEX = -27;
	private static final int BIAS_CHECKBOX_INDEX = -28;

	private static final int XEFFECT_INPUT_INDEX = 0;
	private static final int YEFFECT_INPUT_INDEX = 1;
	private static final int CENTERDISTANCE_INPUT_INDEX = 2;
	private static final int BIAS_INPUT_INDEX = 3;

	public PicbreederTask() throws IllegalAccessException {
		super();
		//Checkboxes to control if x, y, distance from center, or bias effects appear on the console
		JCheckBox xEffect = new JCheckBox("X-Effect", true);
		inputMultipliers[XEFFECT_INPUT_INDEX] = 1.0;
		JCheckBox yEffect = new JCheckBox("Y-Effect", true);
		inputMultipliers[YEFFECT_INPUT_INDEX] = 1.0;
		JCheckBox centerDistanceEffect = new JCheckBox("Center-Distance_Effect", true);
		inputMultipliers[CENTERDISTANCE_INPUT_INDEX] = 1.0;
		JCheckBox biasEffect = new JCheckBox("Bias-Effect", true);
		inputMultipliers[BIAS_INPUT_INDEX] = 1.0;

		xEffect.setName("" + XEFFECT_CHECKBOX_INDEX);
		yEffect.setName("" + YEFFECT_CHECKBOX_INDEX);
		centerDistanceEffect.setName("" + CENTERDISTANCE_CHECKBOX_INDEX);
		biasEffect.setName("" + BIAS_CHECKBOX_INDEX);
		
		xEffect.addActionListener(this);
		yEffect.addActionListener(this);
		centerDistanceEffect.addActionListener(this);
		biasEffect.addActionListener(this);

		xEffect.setForeground(new Color(0,0,0));
		yEffect.setForeground(new Color(0,0,0));
		centerDistanceEffect.setForeground(new Color(0,0,0));
		biasEffect.setForeground(new Color(0,0,0));
		
		top.add(xEffect);
		top.add(yEffect);
		top.add(centerDistanceEffect);
		top.add(biasEffect);
		
		

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
	
	protected void respondToClick(int itemID) {
		super.respondToClick(itemID);
		// Extra checkboxes specific to Picbreeder
		if(itemID == XEFFECT_CHECKBOX_INDEX){ // If X-Effect checkbox is clicked
			setEffectCheckBox(XEFFECT_INPUT_INDEX);
		}else if(itemID == YEFFECT_CHECKBOX_INDEX){ // If Y-Effect checkbox is clicked
			setEffectCheckBox(YEFFECT_INPUT_INDEX);
		}else if(itemID == CENTERDISTANCE_CHECKBOX_INDEX){ // If Bias-Effect checkbox is clicked
			setEffectCheckBox(CENTERDISTANCE_INPUT_INDEX);
		}else if(itemID == BIAS_CHECKBOX_INDEX){ // If Center-Distance Effect checkbox is clicked
			setEffectCheckBox(BIAS_INPUT_INDEX);
		} 
	}

	@Override
	protected void additionalButtonClickAction(Genotype<T> individual) {
	}

}
