package edu.utexas.cs.nn.tasks.interactive.remixesizer;

import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;

public class RemixesizerTask<T extends Network> extends InteractiveEvolutionTask<T> {

	private static final int TIME_CHECKBOX_INDEX = -25;
	private static final int SINE_OF_TIME_CHECKBOX_INDEX = -26;
	private static final int CPPN_CHECKBOX_INDEX = -27;
	private static final int BIAS_CHECKBOX_INDEX = -28;

	private static final int TIME_INPUT_INDEX = 0;
	private static final int SINE_OF_TIME_INPUT_INDEX = 1;
	private static final int CPPN_INPUT_INDEX = 2;
	private static final int BIAS_INPUT_INDEX = 3;
	
	public static final int CPPN_NUM_INPUTS	= 3;
	public static final int CPPN_NUM_OUTPUTS = 1;

	public RemixesizerTask() throws IllegalAccessException {
		super();
		//Checkboxes to control if x, y, distance from center, or bias effects appear on the console
		JCheckBox timeEffect = new JCheckBox("Time", true);
		inputMultipliers[TIME_INPUT_INDEX] = 1.0;
		JCheckBox sineOfTimeEffect = new JCheckBox("Sine(time)", true); //no spaces because of scanner in actionPerformed
		inputMultipliers[SINE_OF_TIME_INPUT_INDEX] = 1.0;
		JCheckBox CPPNEffect = new JCheckBox("CPPN", true);
		inputMultipliers[CPPN_INPUT_INDEX] = 1.0;
		JCheckBox biasEffect = new JCheckBox("Bias", true);
		inputMultipliers[BIAS_INPUT_INDEX] = 1.0;
		
		timeEffect.setName("" + TIME_CHECKBOX_INDEX);
		sineOfTimeEffect.setName("" + SINE_OF_TIME_CHECKBOX_INDEX);
		CPPNEffect.setName("" + CPPN_CHECKBOX_INDEX);
		biasEffect.setName("" + BIAS_CHECKBOX_INDEX);
		
		timeEffect.addActionListener(this);
		sineOfTimeEffect.addActionListener(this);
		CPPNEffect.addActionListener(this);
		biasEffect.addActionListener(this);
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "Time", "Sine of time", "CPPN", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "amplitude" };
	}

	@Override
	protected String getWindowTitle() {
		return "Breederemix";
	}
	
	protected void respondToClick(int itemID) {
		super.respondToClick(itemID);
		// Extra checkboxes specific to Remixesizer
		if(itemID == TIME_CHECKBOX_INDEX){ // If time checkbox is clicked
			setEffectCheckBox(TIME_INPUT_INDEX);
		}else if(itemID == SINE_OF_TIME_CHECKBOX_INDEX){ // If sine of time checkbox is clicked
			setEffectCheckBox(SINE_OF_TIME_INPUT_INDEX);
		}else if(itemID == CPPN_CHECKBOX_INDEX){ // If CPPN checkbox is clicked
			setEffectCheckBox(CPPN_INPUT_INDEX);
		}else if(itemID == BIAS_CHECKBOX_INDEX){ // If bias checkbox is clicked
			setEffectCheckBox(BIAS_INPUT_INDEX);
		} 
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
	protected void additionalButtonClickAction(Genotype<T> individual) {
		// TODO Auto-generated method stub

	}

}
