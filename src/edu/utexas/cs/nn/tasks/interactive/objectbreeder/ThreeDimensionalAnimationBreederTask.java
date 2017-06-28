package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;

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
import edu.utexas.cs.nn.util.datastructures.Triangle;
import edu.utexas.cs.nn.util.graphics.AnimationUtil;

public class ThreeDimensionalAnimationBreederTask<T extends Network> extends AnimationBreederTask<T> {
	
	public static final int CPPN_NUM_INPUTS = 6;
	public static final int CPPN_NUM_OUTPUTS = 4;
	
	public static final Color[] COLORS = new Color[]{ Color.RED, Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.ORANGE, Color.PINK, Color.BLACK };
	
	public static final int EVOLVED_COLOR_INDEX = 8;
	
	public static final int MAX_ROTATION = 360;
	
	protected JSlider pitchValue;
	protected JSlider headingValue;
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

		if(!simplifiedInteractiveInterface && !alwaysAnimate) {
			top.add(pitch);
			top.add(heading);
		}

		String[] choices = { "Red", "Green", "Blue", "Grey","Yellow", "Orange", "Pink", "Black", "Evolved" };
		colorChoice = new JComboBox<String>(choices);
		colorChoice.setSelectedIndex(choices.length - 1); // default to Evolved colors
		colorChoice.setSize(40, 40);
		colorChoice.addItemListener(new ItemListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				int index = source.getSelectedIndex();
				if(index == EVOLVED_COLOR_INDEX) {
					color = null;
				} else {
					color = COLORS[index];
					//TODO: change this
//					// change colors of triangles
//					for(List<Triangle> tris: shapes.values()) {
//						for(Triangle t: tris) {
//							t.color = color;
//						}
//					}
				}
				resetButtons(true);
			}

		});
	}
	
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "Z-coordinate", "distance from center", "time", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "cube present", "hue", "saturation", "brightness" };
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
		return CPPN_NUM_OUTPUTS;
	}
	
	@Override
	public BufferedImage[] getAnimationImages(T cppn, int startFrame, int endFrame, boolean beingSaved) {
		return AnimationUtil.shapesFromCPPN(cppn, picSize, picSize, startFrame, endFrame, color, heading, pitch, inputMultipliers);
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
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","task:edu.utexas.cs.nn.tasks.interactive.objectbreeder.ThreeDimensionalAnimationBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:500","imageHeight:500","imageSize:200","defaultFramePause:50"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
