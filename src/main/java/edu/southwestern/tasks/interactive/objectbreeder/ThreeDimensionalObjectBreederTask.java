package edu.southwestern.tasks.interactive.objectbreeder;

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

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.interactive.animationbreeder.AnimationBreederTask;
import edu.southwestern.util.datastructures.Triangle;
import edu.southwestern.util.graphics.AnimationUtil;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;

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
	public static final int CUBE_SIDE_LENGTH = 10; //The length of the side of a voxel (all are same size)
	public static final int SHAPE_WIDTH = 10; //the width of the overall shape (number of voxels)
	public static final int SHAPE_HEIGHT = 15; //the height of the overall shape (number of voxels)
	public static final int SHAPE_DEPTH = 10; //the depth of the overall shape (number of voxels)
	public Color color = null; //sets the current color to null

	public static final int CPPN_NUM_INPUTS = 5; //the number of inputs that the CPPN takes in
	public static final int CPPN_NUM_OUTPUTS = 4; //minimum number of outputs

	//Brings in every color avaliable for creation
	public static final Color[] COLORS = new Color[]{ Color.RED, Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.ORANGE, Color.PINK, Color.BLACK };
	
	public static final int EVOLVED_COLOR_INDEX = 8; //the number of colors available. Selecting this means voxel colors are evolved (come from CPPN)

	public static final int MAX_ROTATION = 360; //the ability to rotate 360 degress

	protected JSlider pitchValue; //the value of the pitch
	protected JSlider headingValue; //the heading value
	protected JSlider pauseLengthBetweenFrames; //The length of the pause between frames
	protected JComboBox<String> colorChoice; //the color of choice
	protected JComboBox<String> directionChoice; //the direction of choice

	protected boolean vertical; //determines if rotation of objects is vertical (if not, then it is horizontal)

	// For undo button
	public HashMap<Long,List<Triangle>> previousShapes;
	// Pre-load shapes for current generation
	public HashMap<Long,List<Triangle>> shapes;

	double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_ROTATION) * 2 * Math.PI; //the pitch
	double heading = (Parameters.parameters.integerParameter("defaultHeading")/(double) MAX_ROTATION) * 2 * Math.PI; //the heading

	public ThreeDimensionalObjectBreederTask() throws IllegalAccessException {
		super(false);
		//setting the parameters
		Parameters.parameters.setInteger("defaultPause", 0);
		Parameters.parameters.setInteger("defaultAnimationLength", (int) (AnimationUtil.FRAMES_PER_SEC * 3));	
		vertical = false;
		pitchValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_ROTATION, Parameters.parameters.integerParameter("defaultPitch"));


		//establishing a new Hashtable called pitchLabels
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

		//sets headingValue to a new slider
		headingValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_ROTATION, Parameters.parameters.integerParameter("defaultHeading"));
		//creates a new Hashtable for headingValues and sets the parameters
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

		//if it's not a simplifiedInderactiveInterface and it's not 
		//always animating, then add pitch and heading to the top
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
				//if the index is the evoloved color index, the color becomes null
				if(index == EVOLVED_COLOR_INDEX) {
					color = null; // Means colors are set by CPPN
				} else {
					color = COLORS[index]; // All voxels have same color
					// change colors of triangles
					for(List<Triangle> tris: shapes.values()) {
						for(Triangle t: tris) {
							t.color = color;
						}
					}
				}
				//reset buttons
				resetButtons(true);
			}

		});

		//sets panel for color and movements
		JPanel colorAndMovement = new JPanel();
		colorAndMovement.setLayout(new BoxLayout(colorAndMovement, BoxLayout.Y_AXIS));

		//creates a panel for the color of objects
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
			/**
			 * determies if the item state has changed
			 */
			public void itemStateChanged(ItemEvent e) {
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				//it it's horizontal, it's not vertical
				if(source.getSelectedItem().toString() == "Horizontal") {
					vertical = false;
				} else if(source.getSelectedItem().toString() == "Vertical"){ //if it is vertical, then vertical is true.
					vertical = true;
				}
				//reset buttons
				resetButtons(true);
			}

		});

		//sets the panel for the direction of the object
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
		

		//if it's not a simplified interactive interface, then add colorAndMovement to top
		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(colorAndMovement);
		}

	}

	/**
	 * evaluates the population to create the shapes for the user to choose
	 * allows the user to pick the objects they preferred. 
	 * @param population genomes for all objects
	 * @return result of parent method finishing the evaluation: scores for each population member
	 */
	public ArrayList<Score<TWEANN>> evaluateAll(ArrayList<Genotype<TWEANN>> population) {
		// Load all shapes in advance
		previousShapes = shapes;
		shapes = new HashMap<Long,List<Triangle>>();
		//for every g in population, puts the shapes
		for(Genotype<TWEANN> g : population) {
			shapes.put(g.getId(), ThreeDimensionalUtil.trianglesFromCPPN(g.getPhenotype(), buttonWidth, buttonHeight, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, color, getInputMultipliers(), Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")));
		}
		return super.evaluateAll(population); // wait for user choices
	}

	@Override
	/**
	 * resets the buttons. if hardReset is true, the cache is cleared
	 * @param hardRest (boolean) if the buttons are being hard reset or not
	 */
	public void resetButtons(boolean hardReset) {
		//if it's being reset
		if(hardReset){
			//if it's always animating, then stop it
			if(alwaysAnimate) {
				//loop through to stop each animation
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
			//asserts the correct number of inputs
			assert inputMultipliers.length == numCPPNInputs() : "Number of inputs should always match CPPN inputs! " + inputMultipliers.length + " vs " + numCPPNInputs();
			//for each s in scores, set the shapes
			for(Score<TWEANN> s : scores) {
				shapes.put(s.individual.getId(), ThreeDimensionalUtil.trianglesFromCPPN(s.individual.getPhenotype(), buttonWidth, buttonHeight, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, color, inputMultipliers, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")));
			}		
		}
		super.resetButtons(hardReset); //calls the super resetButtons method with hardReset boolean
	}
	
	@Override
	/**
	 * resets the shapes
	 * 
	 */
	protected void reset() { 
		shapes = new HashMap<Long,List<Triangle>>();
		super.reset();
	}
	/**
	 * undoes the evolution to restore the shapes
	 */
	protected void setUndo() {
		// Get the old shapes back
		shapes = previousShapes;
		super.setUndo();
	}

	@Override
	/**
	 * returns the sensorLabels
	 * @return the sensorLabels
	 */
	public String[] sensorLabels() {
		if(Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")) {
			return new String[] { "X", "Y", "Z", "R", "R-XY", "R-YZ", "R-XZ", "bias" };
		} else {
			return new String[] { "X", "Y", "Z", "R", "bias" };
		}
	}

	@Override
	/**
	 * returns the outputLabels
	 * @return the outputLabels
	 */
	public String[] outputLabels() {
		return new String[] { "cube present", "hue", "saturation", "brightness" };
	}

	@Override
	/**
	 * gets the window title
	 * @return "3DObjectBreeder"  - a String for the title of the window
	 */
	protected String getWindowTitle() {
		return "3DObjectBreeder";
	}

	@Override
	/**
	 * gets the number of CPPN inputs
	 * @return CPPN_NUM_INPUTS - the number of CPPN inputs
	 */
	public int numCPPNInputs() {
		// Possibly add distances in three planes
		return CPPN_NUM_INPUTS + (Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane") ? 3 : 0);
	}

	@Override
	/**
	 * gets the number of CPPN outputs.
	 * Depends on whether cube displacement is allowed.
	 * @return the number of CPPN outputs
	 */
	public int numCPPNOutputs() {
		return (Parameters.parameters.booleanParameter("allowCubeDisplacement") ? 7 : 4);
	}

	@Override
	/**
	 * gets the button image for a single evolved object from is phenotype (a CPPN)
	 * @param phenotype - the CPPN
	 * @param width the width of the button
	 * @param height the height of the button
	 * @param inputMultipliers the input multipliers
	 * @return the button image
	 */
	protected BufferedImage getButtonImage(TWEANN phenotype, int width, int height, double[] inputMultipliers) {
		// If reset button cleared out triangles, then load again right before displaying
		if(!shapes.containsKey(phenotype.getId())) {
			shapes.put(phenotype.getId(), ThreeDimensionalUtil.trianglesFromCPPN(phenotype, buttonWidth, buttonHeight, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, color, getInputMultipliers(), Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")));
		}		
		return ThreeDimensionalUtil.imageFromTriangles(shapes.get(phenotype.getId()), buttonWidth, buttonHeight, heading, pitch, null);
	}

	@Override
	/**
	 * gets the animation images for a single CPPN's evolved shape.
	 * Animation is from constant rotation.
	 * @param cppn - the CPPN
	 * @param startFrame - the start frame of the animation image
	 * @param endframe - the end frame of the animation image
	 * @param beingSaved - whether or not it's being saved
	 * @return the animation image
	 */
	protected BufferedImage[] getAnimationImages(TWEANN cppn, int startFrame, int endFrame, boolean beingSaved) {
		//if animation images are being saved as a gif, set background to grey (similar to button background) to avoid frame overlap
		return ThreeDimensionalUtil.imagesFromTriangles(shapes.get(cppn.getId()), buttonWidth, buttonHeight, startFrame, endFrame, heading, pitch, beingSaved ? new Color(223,233,244) : null, vertical);
	}

	/**
	 * Allows for quick and easy launching without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","objectBreederDistanceInEachPlane:true","io:false","netio:false","mating:true", "allowCubeDisplacement:true", "simplifiedInteractiveInterface:false","fs:false", "task:edu.southwestern.tasks.interactive.objectbreeder.ThreeDimensionalObjectBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:500","imageHeight:500","imageSize:200","defaultFramePause:50","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:false","includeSquareWaveFunction:false","includeFullSawtoothFunction:false","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
