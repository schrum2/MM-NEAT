package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
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
import edu.utexas.cs.nn.util.graphics.AnimationUtil;

/**
 * Interface that interactively evolves three-dimensional
 * objects that are created originally with a CPPN. To complete this,
 * the program uses the interactive evolution interface
 * Original endless forms paper: http://yosinski.com/media/papers/Clune__2012__EndlessFormscomCollaborativelyEvolvingObjectsAnd3DPrinting.pdf
 * 
 * @author Isabel Tweraser
 *
 */
public class ThreeDimensionalObjectBreederTask extends AnimationBreederTask<TWEANN> {
	public static final int CUBE_SIDE_LENGTH = 10;
	public static final int SHAPE_WIDTH = 10;
	public static final int SHAPE_HEIGHT = 15; //20;
	public static final int SHAPE_DEPTH = 10;
	public static final Color COLOR = Color.RED;

	public static final int CPPN_NUM_INPUTS = 4;
	public static final int CPPN_NUM_OUTPUTS = 1;

	public static final int MAX_ROTATION = 360;

	protected JSlider pitchValue;
	protected JSlider headingValue;
	protected JSlider pauseLengthBetweenFrames;

	public HashMap<Long,List<Triangle>> shapes;
	
	double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_ROTATION) * 2 * Math.PI; 
	double heading = (Parameters.parameters.integerParameter("defaultHeading")/(double) MAX_ROTATION) * 2 * Math.PI;

	public ThreeDimensionalObjectBreederTask() throws IllegalAccessException {
		super(false);
		Parameters.parameters.setInteger("defaultPause", 0);

		pitchValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_ROTATION, Parameters.parameters.integerParameter("defaultPitch"));

		Hashtable<Integer,JLabel> pitchLabels = new Hashtable<>();
		pitchValue.setMinorTickSpacing(20);
		pitchValue.setPaintTicks(true);
		pitchLabels.put(0, new JLabel("0"));
		pitchLabels.put(MAX_ROTATION, new JLabel("360"));
		pitchValue.setLabelTable(pitchLabels);
		pitchValue.setPaintLabels(true);
		pitchValue.setPreferredSize(new Dimension(150, 40));

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
					resetButtons();
				}
			}
		});

		//Pitch slider
		JPanel pitch = new JPanel();
		pitch.setLayout(new BoxLayout(pitch, BoxLayout.Y_AXIS));
		JLabel pitchLabel = new JLabel();
		pitchLabel.setText("Vertical Rotation of Object");
		pitch.add(pitchLabel);
		pitch.add(pitchValue);

		top.add(pitch);
		
		headingValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_ROTATION, Parameters.parameters.integerParameter("defaultHeading"));

		Hashtable<Integer,JLabel> headingLabels = new Hashtable<>();
		headingValue.setMinorTickSpacing(20);
		headingValue.setPaintTicks(true);
		headingLabels.put(0, new JLabel("0"));
		headingLabels.put(MAX_ROTATION, new JLabel("360"));
		headingValue.setLabelTable(headingLabels);
		headingValue.setPaintLabels(true);
		headingValue.setPreferredSize(new Dimension(150, 40));

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
					resetButtons();
				}
			}
		});

		//Heading slider
		JPanel heading = new JPanel();
		heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
		JLabel headingLabel = new JLabel();
		headingLabel.setText("Horizontal Rotation of Object");
		heading.add(headingLabel);
		heading.add(headingValue);

		top.add(heading);
//
//		//Construction of JSlider for desired length of pause between each frame within an animation
//
//		pauseLengthBetweenFrames = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minPause"), Parameters.parameters.integerParameter("maxPause"), Parameters.parameters.integerParameter("defaultFramePause"));
//
//		Hashtable<Integer,JLabel> framePauseLabels = new Hashtable<>();
//		pauseLengthBetweenFrames.setMinorTickSpacing(75);
//		pauseLengthBetweenFrames.setPaintTicks(true);
//		framePauseLabels.put(Parameters.parameters.integerParameter("minPause"), new JLabel("No pause"));
//		framePauseLabels.put(Parameters.parameters.integerParameter("maxPause"), new JLabel("Long pause"));
//		pauseLengthBetweenFrames.setLabelTable(framePauseLabels);
//		pauseLengthBetweenFrames.setPaintLabels(true);
//		pauseLengthBetweenFrames.setPreferredSize(new Dimension(100, 40));
//
//		/**
//		 * Implements ChangeListener to adjust frame pause length. When frame pause length is specified, 
//		 * input length is used to reset and redraw buttons. 
//		 */
//		pauseLengthBetweenFrames.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				// get value
//				JSlider source = (JSlider)e.getSource();
//				if(!source.getValueIsAdjusting()) {
//					int newLength = (int) source.getValue();
//					Parameters.parameters.setInteger("defaultFramePause", newLength);
//					// reset buttons
//					resetButtons();
//				}
//			}
//		});		
//
//		//Pause (between frames) slider
//		JPanel framePause = new JPanel();
//		framePause.setLayout(new BoxLayout(framePause, BoxLayout.Y_AXIS));
//		JLabel framePauseLabel = new JLabel();
//		framePauseLabel.setText("Pause between frames");
//		framePause.add(framePauseLabel);
//		framePause.add(pauseLengthBetweenFrames);
//
//
//		top.add(framePause);
	}

	public ArrayList<Score<TWEANN>> evaluateAll(ArrayList<Genotype<TWEANN>> population) {
		// Load all shapes in advance
		shapes = new HashMap<Long,List<Triangle>>();
		for(Genotype<TWEANN> g : population) {
			shapes.put(g.getId(), Construct3DObject.trianglesFromCPPN(g.getPhenotype(), picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR, getInputMultipliers()));
		}
		return super.evaluateAll(population); // wait for user choices
	}
	
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "Z-coordinate", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "cube present" };
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
		return CPPN_NUM_OUTPUTS;
	}
	
	@Override
	protected BufferedImage getButtonImage(TWEANN phenotype, int width, int height, double[] inputMultipliers) {
		//return Construct3DObject.rotationSequenceFromCPPN(phenotype, picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR,  0, 1, heading, pitch, getInputMultipliers())[0];
		//return Construct3DObject.currentImageFromCPPN(phenotype, picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR, heading, pitch, getInputMultipliers());
		return Construct3DObject.getImage(shapes.get(phenotype.getId()), picSize, picSize, heading, pitch);
	}

	@Override
	protected BufferedImage[] getAnimationImages(TWEANN cppn, int startFrame, int endFrame) {
		// For rotating the 3D object, we ignore the endFrame from the animation breeder
		endFrame = (int) (AnimationUtil.FRAMES_PER_SEC * 4); // 4 seconds worth of animation
		//return Construct3DObject.rotationSequenceFromCPPN(cppn, picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR,  startFrame, endFrame, heading, pitch, getInputMultipliers());
		return Construct3DObject.imagesFromTriangles(shapes.get(cppn.getId()), picSize, picSize, startFrame, endFrame, heading, pitch);
	}

	/**
	 * Allows for quick and easy launching without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","task:edu.utexas.cs.nn.tasks.interactive.objectbreeder.ThreeDimensionalObjectBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:500","imageHeight:500","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void save(int i) {
//		// Use of imageHeight and imageWidth allows saving a higher quality image than is on the button
//		double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_ROTATION) * 2 * Math.PI;
//		BufferedImage[] toSave = Construct3DObject.generate3DObjectFromCPPN((Network)scores.get(i).individual.getPhenotype(), picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR,  startFrame, endFrame, pitch, getInputMultipliers());
//		JFileChooser chooser = new JFileChooser();//used to get save name 
//		chooser.setApproveButtonText("Save");
//		FileNameExtensionFilter filter = new FileNameExtensionFilter("GIF", "gif");
//		chooser.setFileFilter(filter);
//		int returnVal = chooser.showOpenDialog(frame);
//		if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
//			System.out.println("You chose to call the image: " + chooser.getSelectedFile().getName());
//			try {
//				//saves gif to chosen file name
//				AnimationUtil.createGif(toSave, Parameters.parameters.integerParameter("defaultFramePause"), chooser.getSelectedFile().getName() + ".gif");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			System.out.println("image " + chooser.getSelectedFile().getName() + " was saved successfully");
//		} else { //else image dumped
//			System.out.println("image not saved");
//		}

	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<TWEANN> individual) {
		// Do nothing
	}

}
