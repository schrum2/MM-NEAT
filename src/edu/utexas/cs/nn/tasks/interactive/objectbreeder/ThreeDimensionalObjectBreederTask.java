package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
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
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.interactive.animationbreeder.AnimationBreederTask;
import edu.utexas.cs.nn.util.datastructures.Triangle;
import edu.utexas.cs.nn.util.graphics.AnimationUtil;
import edu.utexas.cs.nn.util.graphics.ThreeDimensionalUtil;

/**
 * Interface that interactively evolves three-dimensional
 * objects that are created originally with a CPPN. To complete this,
 * the program uses the interactive evolution interface
 * 
 * Original Endless Forms paper: 
 * Jeff Clune and Hod Lipson. 2011. Evolving 3D objects with a generative encoding inspired by developmental biology. 
 * Proceedings of the European Conference on Artificial Life. 144-148. 
 * URL: http://www.evolvingai.org/files/2011-CluneLipson-Evolving3DObjectsWithCPPNs-ECAL.pdf
 * 
 * @author Isabel Tweraser
 *
 */
public class ThreeDimensionalObjectBreederTask extends AnimationBreederTask<TWEANN> {
	public static final int CUBE_SIDE_LENGTH = 10;
	public static final int SHAPE_WIDTH = 10;
	public static final int SHAPE_HEIGHT = 15; //20;
	public static final int SHAPE_DEPTH = 10;
	public Color color = null;

	public static final int CPPN_NUM_INPUTS = 5;
	public static final int CPPN_NUM_OUTPUTS = 4; //minimum number of outputs

	public static final Color[] COLORS = new Color[]{ Color.RED, Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.ORANGE, Color.PINK, Color.BLACK };
	
	public static final int EVOLVED_COLOR_INDEX = 8;

	public static final int MAX_ROTATION = 360;

	protected JSlider pitchValue;
	protected JSlider headingValue;
	protected JSlider pauseLengthBetweenFrames;
	protected JComboBox<String> colorChoice;
	protected JComboBox<String> directionChoice;

	protected boolean vertical;

	// For undo button
	public HashMap<Long,List<Triangle>> previousShapes;
	// Pre-load shapes for current generation
	public HashMap<Long,List<Triangle>> shapes;

	double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_ROTATION) * 2 * Math.PI; 
	double heading = (Parameters.parameters.integerParameter("defaultHeading")/(double) MAX_ROTATION) * 2 * Math.PI;

	public ThreeDimensionalObjectBreederTask() throws IllegalAccessException {
		super(false);
		Parameters.parameters.setInteger("defaultPause", 0);
		Parameters.parameters.setInteger("defaultAnimationLength", (int) (AnimationUtil.FRAMES_PER_SEC * 3));	
		vertical = false;
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

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface") && !alwaysAnimate) {
			top.add(pitch);
			top.add(heading);
		}
		
		// Drop down box to select color for generated object
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
					// change colors of triangles
					for(List<Triangle> tris: shapes.values()) {
						for(Triangle t: tris) {
							t.color = color;
						}
					}
				}
				resetButtons(true);
			}

		});
		JPanel colorAndMovement = new JPanel();
		colorAndMovement.setLayout(new BoxLayout(colorAndMovement, BoxLayout.Y_AXIS));

		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
		JLabel colorLabel = new JLabel();
		colorLabel.setText("Color of Objects: ");

		
		// Drop down box to select direction of animated rotation
		String[] directionChoices = { "Horizontal", "Vertical" };
		directionChoice = new JComboBox<String>(directionChoices);
		directionChoice.setSize(40, 40);
		directionChoice.addItemListener(new ItemListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				if(source.getSelectedItem().toString() == "Horizontal") {
					vertical = false;
				} else if(source.getSelectedItem().toString() == "Vertical"){
					vertical = true;
				}
				resetButtons(true);
			}

		});

		JPanel directionPanel = new JPanel();
		directionPanel.setLayout(new BoxLayout(directionPanel, BoxLayout.X_AXIS));
		JLabel directionLabel = new JLabel();
		directionLabel.setText("Direction: ");
		colorPanel.add(colorLabel);
		colorPanel.add(colorChoice);
		directionPanel.add(directionLabel);		
		directionPanel.add(directionChoice);
		
		colorAndMovement.add(colorPanel);
		colorAndMovement.add(directionPanel);
		
		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(colorAndMovement);
		}

	}

	public ArrayList<Score<TWEANN>> evaluateAll(ArrayList<Genotype<TWEANN>> population) {
		// Load all shapes in advance
		previousShapes = shapes;
		shapes = new HashMap<Long,List<Triangle>>();
		for(Genotype<TWEANN> g : population) {
			shapes.put(g.getId(), ThreeDimensionalUtil.trianglesFromCPPN(g.getPhenotype(), picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, color, getInputMultipliers()));
		}
		return super.evaluateAll(population); // wait for user choices
	}
	
	@Override
	public void resetButtons(boolean hardReset) {
		if(hardReset){
			if(alwaysAnimate) {
				for(int x = 0; x < animationThreads.length; x++) {
					if(animationThreads[x] != null) {
						animationThreads[x].stopAnimation();
					}
				}
				// Make sure all threads actually stopped
				for(int x = 0; x < animationThreads.length; x++) {
					if(animationThreads[x] != null) {
						try {
							// Wait for thread to actually stop
							animationThreads[x].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			shapes = new HashMap<Long,List<Triangle>>();
			assert inputMultipliers.length == numCPPNInputs() : "Number of inputs should always match CPPN inputs! " + inputMultipliers.length + " vs " + numCPPNInputs();
			for(Score<TWEANN> s : scores) {
				shapes.put(s.individual.getId(), ThreeDimensionalUtil.trianglesFromCPPN(s.individual.getPhenotype(), picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, color, inputMultipliers));
			}		
		}
		super.resetButtons(hardReset);
	}
	
	@Override
	protected void reset() { 
		shapes = new HashMap<Long,List<Triangle>>();
		super.reset();
	}

	protected void setUndo() {
		// Get the old shapes back
		shapes = previousShapes;
		super.setUndo();
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "Z-coordinate", "distance from center", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "cube present", "hue", "saturation", "brightness" };
	}

	@Override
	protected String getWindowTitle() {
		return "3DObjectBreeder";
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
	protected BufferedImage getButtonImage(TWEANN phenotype, int width, int height, double[] inputMultipliers) {
		// If reset button cleared out triangles, then load again right before displaying
		if(!shapes.containsKey(phenotype.getId())) {
			shapes.put(phenotype.getId(), ThreeDimensionalUtil.trianglesFromCPPN(phenotype, picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, color, getInputMultipliers()));
		}		
		return ThreeDimensionalUtil.imageFromTriangles(shapes.get(phenotype.getId()), picSize, picSize, heading, pitch, null);
	}

	@Override
	protected BufferedImage[] getAnimationImages(TWEANN cppn, int startFrame, int endFrame, boolean beingSaved) {
		//if animation images are being saved as a gif, set background to grey (similar to button background) to avoid frame overlap
		return ThreeDimensionalUtil.imagesFromTriangles(shapes.get(cppn.getId()), picSize, picSize, startFrame, endFrame, heading, pitch, beingSaved ? new Color(223,233,244) : null, vertical);
	}

	/**
	 * Allows for quick and easy launching without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true", "allowCubeDisplacement:true", "fs:false", "task:edu.utexas.cs.nn.tasks.interactive.objectbreeder.ThreeDimensionalObjectBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:500","imageHeight:500","imageSize:200","defaultFramePause:50"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
