package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.swing.BoxLayout;
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
 * Interface that interactively evolves three-dimensional
 * objects that are created originally with a CPPN. To complete this,
 * the program uses the interactive evolution interface
 * Original endless forms paper: http://yosinski.com/media/papers/Clune__2012__EndlessFormscomCollaborativelyEvolvingObjectsAnd3DPrinting.pdf
 * 
 * @author Isabel Tweraser
 *
 */
public class ThreeDimensionalObjectBreederTask extends AnimationBreederTask {
	public static final int CUBE_SIDE_LENGTH = 10;
	public static final int SHAPE_WIDTH = 10;
	public static final int SHAPE_HEIGHT = 15; //20;
	public static final int SHAPE_DEPTH = 10;
	public static final Color COLOR = Color.RED;
	
	public static final int CPPN_NUM_INPUTS = 4;
	public static final int CPPN_NUM_OUTPUTS = 1;
	
	public static final int MAX_PITCH = 260;
	
	protected JSlider pitchValue;
					

	public ThreeDimensionalObjectBreederTask() throws IllegalAccessException {
		super(false);
		Parameters.parameters.setInteger("defaultPause", 0);
		
		pitchValue = new JSlider(JSlider.HORIZONTAL, 0, MAX_PITCH, Parameters.parameters.integerParameter("defaultPitch"));

		Hashtable<Integer,JLabel> pitchLabels = new Hashtable<>();
		pitchValue.setMinorTickSpacing(20);
		pitchValue.setPaintTicks(true);
		pitchLabels.put(0, new JLabel("0"));
		pitchLabels.put(MAX_PITCH, new JLabel("360"));
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
				//if(!source.getValueIsAdjusting()) {
					int newLength = (int) source.getValue();
					Parameters.parameters.setInteger("defaultPitch", newLength);
					// reset buttons
					resetButtons();
				//}
			}
		});
		
		//Animation slider
		JPanel pitch = new JPanel();
		pitch.setLayout(new BoxLayout(pitch, BoxLayout.Y_AXIS));
		JLabel pitchLabel = new JLabel();
		pitchLabel.setText("Vertical Rotation of Object");
		pitch.add(pitchLabel);
		pitch.add(pitchValue);
		
		top.add(pitch);
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
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		// Just get first frame for button. Slightly inefficent though, since all animation frames were pre-computed
		double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_PITCH) * 2 * Math.PI; 
		return Construct3DObject.generate3DObjectFromCPPN(phenotype, picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR,  0, 1, pitch, getInputMultipliers())[0];
	}
	
	@Override
	protected BufferedImage[] getAnimationImages(Network cppn, int startFrame, int endFrame) {
		// For rotating the 3D object, we ignore the endFrame from the animation breeder
		endFrame = (int) (AnimationUtil.FRAMES_PER_SEC * 4); // 4 seconds worth of animation
		double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) MAX_PITCH) * 2 * Math.PI;
		return Construct3DObject.generate3DObjectFromCPPN(cppn, picSize, picSize, CUBE_SIDE_LENGTH, SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_DEPTH, COLOR,  startFrame, endFrame, pitch, getInputMultipliers());
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

}
