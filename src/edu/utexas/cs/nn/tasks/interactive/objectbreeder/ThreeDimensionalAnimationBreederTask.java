package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.animationbreeder.AnimationBreederTask;
import edu.utexas.cs.nn.util.graphics.AnimationUtil;

/**
 * Interface that interactively evolves originally generated three-dimensional animations
 * from a CPPN. Uses the interactive evolution interface to complete this.
 * 
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class ThreeDimensionalAnimationBreederTask<T extends Network> extends AnimationBreederTask<T> {
	
	public static final int CPPN_NUM_INPUTS = 6;
	
	// list of color options for constructed object
	public static final Color[] COLORS = new Color[]{ Color.RED, Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.ORANGE, Color.PINK, Color.BLACK };
	
	public static final int EVOLVED_COLOR_INDEX = 8;
	
	public static final int MAX_ROTATION = 360;
	
	protected JSlider pitchValue; // vertical tilt of animated object
	protected JSlider headingValue; // horizontal tilt of animated object
	protected JSlider pauseLengthBetweenFrames;
	protected JComboBox<String> colorChoice;
	
	double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_ROTATION) * 2 * Math.PI; 
	double heading = (Parameters.parameters.integerParameter("defaultHeading")/(double) MAX_ROTATION) * 2 * Math.PI;
	
	public Color color = null;

	public ThreeDimensionalAnimationBreederTask() throws IllegalAccessException {
		super();
		Parameters.parameters.setInteger("defaultPause", 0);
		Parameters.parameters.setInteger("defaultAnimationLength", (int) (AnimationUtil.FRAMES_PER_SEC * 3));	
		pitchValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_ROTATION, Parameters.parameters.integerParameter("defaultPitch"));

		Hashtable<Integer,JLabel> pitchLabels = new Hashtable<>();
		pitchValue.setMinorTickSpacing(72);
		pitchValue.setPaintTicks(true);
		pitchLabels.put(0, new JLabel("0"));
		pitchLabels.put(MAX_ROTATION, new JLabel("360"));
		pitchValue.setLabelTable(pitchLabels);
		pitchValue.setPaintLabels(true);
		pitchValue.setPreferredSize(new Dimension(75, 40));

		/**
		 * Implements ChangeListener to adjust animation length. When animation length is specified, 
		 * input length is used to reset and redraw buttons. 
		 */
		pitchValue.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int newLength = (int) source.getValue();
					pitch = (newLength /(double) MAX_ROTATION) * 2 * Math.PI; 
					// reset buttons
					resetButtons(true);
				}
			}
		});
		
		
		//Pitch slider
		JPanel pitch = new JPanel();
		pitch.setLayout(new BoxLayout(pitch, BoxLayout.Y_AXIS));
		JLabel pitchLabel = new JLabel();
		pitchLabel.setText("Vertical Tilt");
		pitch.add(pitchLabel);
		pitch.add(pitchValue);

		headingValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_ROTATION, Parameters.parameters.integerParameter("defaultHeading"));

		Hashtable<Integer,JLabel> headingLabels = new Hashtable<>();
		headingValue.setMinorTickSpacing(72);
		headingValue.setPaintTicks(true);
		headingLabels.put(0, new JLabel("0"));
		headingLabels.put(MAX_ROTATION, new JLabel("360"));
		headingValue.setLabelTable(headingLabels);
		headingValue.setPaintLabels(true);
		headingValue.setPreferredSize(new Dimension(75, 40));

		/**
		 * Implements ChangeListener to adjust animation length. When animation length is specified, 
		 * input length is used to reset and redraw buttons. 
		 */
		headingValue.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int newLength = (int) source.getValue();

					heading = (newLength /(double) MAX_ROTATION) * 2 * Math.PI; 
					// reset buttons
					resetButtons(true);
				}
			}
		});

		//Heading slider
		JPanel heading = new JPanel();
		heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
		JLabel headingLabel = new JLabel();
		headingLabel.setText("Horizontal Tilt");
		heading.add(headingLabel);
		heading.add(headingValue);
		
		JPanel pitchAndHeading = new JPanel();
		pitchAndHeading.setLayout(new BoxLayout(pitchAndHeading, BoxLayout.Y_AXIS));
		pitchAndHeading.add(pitch);
		pitchAndHeading.add(heading);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(pitchAndHeading);
		}
	}
	
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "Z-coordinate", "distance from center", "time", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return (Parameters.parameters.booleanParameter("allowCubeDisplacement") ? 
				new String[] { "cube present", "hue", "saturation", "brightness", "X-displacement", "Y-displacement", "Z-Coordinate" } :
				new String[] { "cube present", "hue", "saturation", "brightness"});
	}

	@Override
	protected String getWindowTitle() {
		return "3DAnimationBreeder";
	}

	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	@Override
	public int numCPPNOutputs() {
		return (Parameters.parameters.booleanParameter("allowCubeDisplacement") ? 7 : 4);
	}
	
	@Override
	public BufferedImage[] getAnimationImages(T cppn, int startFrame, int endFrame, boolean beingSaved) {
		// Grey color new Color(223,233,244) used for background
		return AnimationUtil.shapesFromCPPN(cppn, picSize, picSize, startFrame, endFrame, beingSaved ? new Color(223,233,244) : null, heading, pitch, inputMultipliers);
	}
	
	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		// Just get first frame for button. Slightly inefficent though, since all animation frames were pre-computed
		return AnimationUtil.shapesFromCPPN(phenotype, picSize, picSize, 0, 1, color, heading, pitch, getInputMultipliers())[0];
	}
	
	/**
	 * Allows for quick and easy launching without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","allowCubeDisplacement:true","maxGens:500","io:false","netio:false","mating:true", "fs:false", "task:edu.utexas.cs.nn.tasks.interactive.objectbreeder.ThreeDimensionalAnimationBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:500","imageHeight:500","imageSize:200","simplifiedInteractiveInterface:true","defaultFramePause:50"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
