package edu.southwestern.tasks.interactive.animationbreeder;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.util.graphics.AnimationUtil;

/**
 * Interface that interactively evolves originally generated animations
 * from a CPPN. Uses the interactive evolution interface to complete this.
 * 
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class AnimationBreederTask<T extends Network> extends InteractiveEvolutionTask<T>{
	// Plays animation in reverse
	private boolean reverse = Parameters.parameters.booleanParameter("loopAnimationInReverse");
	// Animation specific interface options
	protected JSlider animationLength;
	protected JSlider pauseLength;
	protected JSlider pauseLengthBetweenFrames;
	// Animates continuously
	protected boolean alwaysAnimate = Parameters.parameters.booleanParameter("alwaysAnimate");

	/**
	 * Returns array of all images in an animation sequence created by a CPPN.
	 * 
	 * @param cppn A neural network that is queried to create images
	 * @param startFrame First time input to CPPN
	 * @param endFrame Last time input to CPPN
	 * @param beingSaved Ignored
	 * @return Array of the images created by the CPPN in sequence
	 */
	protected BufferedImage[] getAnimationImages(T cppn, int startFrame, int endFrame, boolean beingSaved) {
		return imagesFromCPPN(cppn, buttonWidth, buttonHeight, startFrame, endFrame, getInputMultipliers());
	}

	private BufferedImage[] imagesFromCPPN(T cppn, int width, int height, int startFrame, int endFrame, double[] inputMultipliers) {
		double scale = Parameters.parameters.doubleParameter("picbreederImageScale");
		double rotation = Parameters.parameters.doubleParameter("picbreederImageRotation");
		return AnimationUtil.imagesFromCPPN(cppn, width, height, startFrame, endFrame, inputMultipliers, scale, rotation, Parameters.parameters.doubleParameter("picbreederImageTranslationX"), Parameters.parameters.doubleParameter("picbreederImageTranslationY"));
	}



	/**
	 * Private inner class to run animations in a loop
	 */
	protected class AnimationThread extends Thread {
		private int imageID;
		private boolean abort;

		/**
		 * Default constructor
		 * 
		 * @param imageID ID of image to use in animation
		 */
		public AnimationThread(int imageID) {
			this.imageID = imageID;
			this.abort = false;
		}

		/**
		 * Begin animation loop
		 */
		public void run() {
			if(showNetwork) {
				stopAnimation();
			}
			int end = Parameters.parameters.integerParameter("defaultAnimationLength");
			// Only one thread can add frames at a time
			buttons.get(imageID).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); //turn on busy cursor while animations are loading
			synchronized(animations[imageID]) {
				//adds images to array at index of specified button (imageID)
				if(animations[imageID].size() < Parameters.parameters.integerParameter("defaultAnimationLength")) {
					int start = animations[imageID].size();
					try {
						BufferedImage[] newFrames = getAnimationImages(scores.get(imageID).individual.getPhenotype(), start, end, false);
						for(BufferedImage bi : newFrames) {
							if(abort) break; // stop loading if animation is aborted
							animations[imageID].add(bi);
						}
						if(!abort && reverse) {
							for (int i = newFrames.length-1; i >= 0; i--) {
								if(abort) break; // stop loading if animation is aborted
								animations[imageID].add(newFrames[i]);
							}
						}
					} catch(IndexOutOfBoundsException e) {
						// Suppressing this exception seems like a bad idea.
						System.out.println("Scores not ready for animation " + imageID);
						abort = true;
					}
				}
			}
			buttons.get(imageID).setCursor(Cursor.getDefaultCursor()); //turn off busy cursor after animations have finished loading

			while(!abort) {
				// One animation loop
				int actualAnimationLength = reverse ? end*2 : end;
				for(int frame = 0; !abort && frame < actualAnimationLength; frame++) {
					// set button over and over
					setButtonImage(animations[imageID].get(frame), imageID);
					try {
						// pause between frames (each image in animation)
						Thread.sleep(Parameters.parameters.integerParameter("defaultFramePause"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}		
				}
				try {
					// pause between animations
					Thread.sleep(Parameters.parameters.integerParameter("defaultPause"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		
			}
		}

		/**
		 * Halts animation loop
		 */
		public void stopAnimation() {
			abort = true;
		}		
	}

	public static final int CPPN_NUM_INPUTS	= 5;
	public static final int BASE_CPPN_NUM_OUTPUTS = 3;

	// stores all animations in an array with a different button's animation at each index
	public ArrayList<BufferedImage>[] animations;
	protected AnimationThread[] animationThreads;

	/**
	 * Default constructor
	 * 
	 * @throws IllegalAccessException
	 */
	public AnimationBreederTask() throws IllegalAccessException {
		this(true);
	}

	/**
	 * Constructor - all sliders are added here and mouse listening is enabled for hovering over the buttons
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public AnimationBreederTask(boolean justAnimationBreeder) throws IllegalAccessException {
		super();
		animationThreads = new AnimationBreederTask.AnimationThread[Parameters.parameters.integerParameter("mu")];
		if(justAnimationBreeder) {
			//Construction of JSlider for desired animation length

			animationLength = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minAnimationLength"), Parameters.parameters.integerParameter("maxAnimationLength"), Parameters.parameters.integerParameter("defaultAnimationLength"));

			Hashtable<Integer,JLabel> animationLabels = new Hashtable<>();
			animationLength.setMinorTickSpacing(20);
			animationLength.setPaintTicks(true);
			animationLabels.put(Parameters.parameters.integerParameter("minAnimationLength"), new JLabel("Short"));
			animationLabels.put(Parameters.parameters.integerParameter("maxAnimationLength"), new JLabel("Long"));
			animationLength.setLabelTable(animationLabels);
			animationLength.setPaintLabels(true);
			animationLength.setPreferredSize(new Dimension(75, 40));

			/**
			 * Implements ChangeListener to adjust animation length. When animation length is specified, 
			 * input length is used to reset and redraw buttons. 
			 */
			animationLength.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// get value
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting()) {
						int newLength = (int) source.getValue();
						Parameters.parameters.setInteger("defaultAnimationLength", newLength);
						// reset buttons
						resetButtons(false); // do not clear out cached animation frames
					}
				}
			});

			//Construction of JSlider for desired length of pause between each iteration of animation

			pauseLength = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minPause"), Parameters.parameters.integerParameter("maxPause"), Parameters.parameters.integerParameter("defaultPause"));

			Hashtable<Integer,JLabel> pauseLabels = new Hashtable<>();
			pauseLength.setMinorTickSpacing(75);
			pauseLength.setPaintTicks(true);
			pauseLabels.put(Parameters.parameters.integerParameter("minPause"), new JLabel("No pause"));
			pauseLabels.put(Parameters.parameters.integerParameter("maxPause"), new JLabel("Long pause"));
			pauseLength.setLabelTable(pauseLabels);
			pauseLength.setPaintLabels(true);
			pauseLength.setPreferredSize(new Dimension(75, 40));

			/**
			 * Implements ChangeListener to adjust pause length. When pause length is specified, 
			 * input length is used to reset and redraw buttons. 
			 */
			pauseLength.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// get value
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting()) {
						int newLength = (int) source.getValue();
						Parameters.parameters.setInteger("defaultPause", newLength);
						// reset buttons: necessary?
						//resetButtons();
					}
				}
			});

			//Add all JSliders to individual panels so that they can be titled with JLabels

			//Animation slider
			JPanel animation = new JPanel();
			animation.setLayout(new BoxLayout(animation, BoxLayout.Y_AXIS));
			JLabel animationLabel = new JLabel();
			animationLabel.setText("Animation length");
			if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
				animation.add(animationLabel);
				animation.add(animationLength);
			}

			//Pause (between animations) slider
			JPanel pause = new JPanel();
			pause.setLayout(new BoxLayout(pause, BoxLayout.Y_AXIS));
			JLabel pauseLabel = new JLabel();
			pauseLabel.setText("Pause between animations");
			pause.add(pauseLabel);
			pause.add(pauseLength);			
			
			JCheckBox stark = new JCheckBox("stark", Parameters.parameters.booleanParameter("starkPicbreeder"));
			stark.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Flip Stark Brightness");
					// Switch to opposite of current setting
					Parameters.parameters.changeBoolean("starkPicbreeder");
					// Need to change all images and re-load
					resetButtons(true);
				}
			});
			
			//AnimateRotation, AnimateScale, AnimateDeltaX, and AnimateDeltaY.
			JCheckBox animateRotation = new JCheckBox("rotation", Parameters.parameters.booleanParameter("AnimateRotation"));
			animateRotation.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Flip rotation");
					Parameters.parameters.changeBoolean("AnimateRotation");
					resetButtons(true);
				}
			});
			
			JCheckBox animateScale = new JCheckBox("Scale", Parameters.parameters.booleanParameter("AnimateScale"));
			animateScale.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Flip scale");
					Parameters.parameters.changeBoolean("AnimateScale");
					resetButtons(true);
				}
			});
			
			JCheckBox animateDeltaX = new JCheckBox("deltaX", Parameters.parameters.booleanParameter("AnimateDeltaX"));
			animateDeltaX.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Flip delta X");
					Parameters.parameters.changeBoolean("AnimateDeltaX");
					resetButtons(true);
				}
			});
			
			JCheckBox animateDeltaY = new JCheckBox("deltaY", Parameters.parameters.booleanParameter("AnimateDeltaY"));
			animateDeltaY.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Flip delta Y");
					Parameters.parameters.changeBoolean("AnimateDeltaY");
					resetButtons(true);
				}
			});
			
			JPanel imageTweaks = new JPanel();
			imageTweaks.setLayout(new GridLayout(5, 1, 2, 2));
			imageTweaks.add(stark);
			if(Parameters.parameters.booleanParameter("animateWithScaleRotationTranslation")) {
				imageTweaks.add(animateRotation);
				imageTweaks.add(animateScale);
				imageTweaks.add(animateDeltaX);
				imageTweaks.add(animateDeltaY);
			}
			top.add(imageTweaks);
			
			//Add all panels to interface
			top.add(animation);
			if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
				top.add(pause);
			}	
		}

		//Construction of JSlider for desired length of pause between each frame within an animation

		pauseLengthBetweenFrames = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minPause"), Parameters.parameters.integerParameter("maxPause"), Parameters.parameters.integerParameter("defaultFramePause"));

		Hashtable<Integer,JLabel> framePauseLabels = new Hashtable<>();
		pauseLengthBetweenFrames.setMinorTickSpacing(75);
		pauseLengthBetweenFrames.setPaintTicks(true);
		framePauseLabels.put(Parameters.parameters.integerParameter("minPause"), new JLabel("No pause"));
		framePauseLabels.put(Parameters.parameters.integerParameter("maxPause"), new JLabel("Long pause"));
		pauseLengthBetweenFrames.setLabelTable(framePauseLabels);
		pauseLengthBetweenFrames.setPaintLabels(true);
		pauseLengthBetweenFrames.setPreferredSize(new Dimension(100, 40));

		/**
		 * Implements ChangeListener to adjust frame pause length. When frame pause length is specified, 
		 * input length is used to reset and redraw buttons. 
		 */
		pauseLengthBetweenFrames.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int newLength = (int) source.getValue();
					Parameters.parameters.setInteger("defaultFramePause", newLength);
					// reset buttons: necessary?
					//resetButtons();
				}
			}
		});		

		//Pause (between frames) slider
		JPanel framePause = new JPanel();
		framePause.setLayout(new BoxLayout(framePause, BoxLayout.Y_AXIS));
		JLabel framePauseLabel = new JLabel();
		framePauseLabel.setText("Pause between frames");
		framePause.add(framePauseLabel);
		framePause.add(pauseLengthBetweenFrames);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(framePause);
		}

		if(!alwaysAnimate) {
			//Enables MouseListener so that animation on a button will play when mouse is hovering over it
			for(JButton button: buttons) {
				button.addMouseListener(new MouseListener() {

					AnimationThread animation;

					@Override
					public void mouseClicked(MouseEvent e) { } // Do not use
					@Override
					public void mousePressed(MouseEvent e) { } // Do not use
					@Override
					public void mouseReleased(MouseEvent e) { } // Do not use

					@Override
					public void mouseEntered(MouseEvent e) {
						// ugly handling of button id extraction.
						Scanner id = new Scanner(e.toString());
						id.next(); // throw away tokens in string name
						id.next(); // the third will be the id number
						animation = new AnimationThread(id.nextInt());
						id.close();
						animation.start();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						animation.stopAnimation(); //exits loop in which animation is played
					}
				});
			}
		}
	}

	/**
	 * X and Y input labels, distance from center is useful for radial distance,
	 * time, bias is required for all neural networks.
	 */
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "time", "bias"};
	}

	/**
	 * Hue, saturation, and brightness values being output by CPPN
	 */
	@Override
	public String[] outputLabels() {
		// if animate scale and rotation, then return array with "scale" and "rotation" at the end
		if(Parameters.parameters.booleanParameter("animateWithScaleAndRotation")) {
			return new String[] { "hue-value", "saturation-value", "brightness-value"};
		}else {
			return new String[] { "hue-value", "saturation-value", "brightness-value" };
		}
	}

	/** 
	 * Window Title
	 */
	@Override
	protected String getWindowTitle() {
		return "AnimationBreeder";
	}

	/**
	 * Save generated animated images from CPPN
	 * 
	 * @param file Desired file name
	 * @param i Index of item being saved
	 */
	@Override
	protected void save(String filename, int i) {
		// Use of imageHeight and imageWidth allows saving a higher quality image than is on the button
		BufferedImage[] toSave = getAnimationImages(scores.get(i).individual.getPhenotype(), 0, Parameters.parameters.integerParameter("defaultAnimationLength"), true);
		filename += ".gif";
		try {
			//saves gif to chosen file name
			AnimationUtil.createGif(toSave, Parameters.parameters.integerParameter("defaultFramePause"), filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("image " + filename + " was saved successfully");
	}


	/**
	 * Create BufferedImage from CPPN
	 */
	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		// Just get first frame for button. Slightly inefficent though, since all animation frames were pre-computed
		return imagesFromCPPN(phenotype, buttonWidth, buttonHeight, 0, 1, getInputMultipliers())[0];
	}

	/**
	 * Change image on button to given image
	 * 
	 * @param gmi replacing image
	 * @param buttonIndex index of button 
	 */
	@Override
	protected void setButtonImage(BufferedImage gmi, int buttonIndex) {
		if(animationThreads[buttonIndex] != null && showNetwork) animationThreads[buttonIndex].stopAnimation();
		super.setButtonImage(gmi, buttonIndex);
		if(animationThreads[buttonIndex] != null && !animationThreads[buttonIndex].isAlive() && alwaysAnimate && !showNetwork) {
			animationThreads[buttonIndex] =  new AnimationThread(buttonIndex);
			animationThreads[buttonIndex].start();
		}
	}

	/**
	 * This method does nothing since no additional behavior of 
	 * click other than initial response is used
	 */
	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		// do nothing
	}


	/**
	 * Evaluates all genotypes in a population. 
	 * 
	 * @param population ArrayList<Genotype<T>> list of genotypes
	 * @return ArrayList<Score<T>> list of scores
	 */
	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		clearAnimations(population.size());
		return super.evaluateAll(population); // wait for user choices
	}

	/**
	 * Clears all animations in each image array by replacing each with a
	 * new ArrayList  
	 * 
	 * @param num int value of number image arrays
	 */
	@SuppressWarnings("unchecked")
	private void clearAnimations(int num) {
		// Load all Image arrays with animations
		animations = new ArrayList[num]; // Suppression for warning on type for ArrayLists
		for(int i = 0; i < animations.length; i++) {
			animations[i] = new ArrayList<BufferedImage>();
		}		
	}

	/**
	 * Undoes previous evolution call
	 */
	@Override
	protected void setUndo() {
		clearAnimations(scores.size());
		if(alwaysAnimate) {
			for(int x = 0; x < animationThreads.length; x++) {
				if(animationThreads[x] != null) animationThreads[x].stopAnimation();
			}
		}
		super.setUndo();
	}

	/**
	 * Resets all buttons. If hardReset is true, cache is cleared
	 */
	@Override
	public void resetButtons(boolean hardReset) {
		super.resetButtons(hardReset);
		if(alwaysAnimate) {
			for(int x = 0; x < animationThreads.length; x++) {
				if(animationThreads[x] != null) animationThreads[x].stopAnimation();
			}
		}
		if(hardReset) {
			//Clears out all pre-computed animations so that checking/unchecking boxes actually creates new animations
			for(int i = 0; i < animations.length; i++) {
				// Cannot clear animation if being loaded
				synchronized(animations[i]) {
					animations[i].clear();
				}
			}
		}
		if(alwaysAnimate) {
			for(int x = 0; x < animationThreads.length; x++) {
				animationThreads[x] = new AnimationThread(x);
				animationThreads[x].start();
			}
		}
	}

	/**
	 * Resets image on a button using specified genotype
	 * 
	 * @param individual Genotype used  to replace the button image
	 * @param x index of the button to be modified
	 */
	@Override
	protected void resetButton(Genotype<T> individual, int x, boolean selected) {
		super.resetButton(individual, x, selected);
		if(alwaysAnimate) {
			if(animationThreads[x] != null) animationThreads[x].stopAnimation();
			animationThreads[x] = new AnimationThread(x);
			animationThreads[x].start();
		}
	}

	/**
	 * Resets to a new random population
	 */
	@Override
	protected void reset() {
		if(alwaysAnimate) {
			for(int x = 0; x < animationThreads.length; x++) {
				if(animationThreads[x] != null) animationThreads[x].stopAnimation();
			}
		}
		//Clears out all pre-computed animations so that checking/unchecking boxes actually creates new animations
		for(int i = 0; i < animations.length; i++) {
			// Cannot clear animation if being loaded
			synchronized(animations[i]) {
				animations[i].clear();
			}
		}
		super.reset();
	}

	/**
	 * Stops all animations and saves current score evaluations 
	 */
	@Override
	protected void evolve() {
		super.evolve();
		if(alwaysAnimate) {
			for(int x = 0; x < animationThreads.length; x++) {
				if(animationThreads[x] != null) animationThreads[x].stopAnimation();
			}
		}
	}

	/**
	 * Returns the number of inputs used in the interactive evolution task
	 */
	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	/**
	 * Returns the number of outputs used in the interactive evolution task
	 */
	@Override
	public int numCPPNOutputs() {
		// if animate rotation and scale, then add 2, otherwise just return BASE_CPPN_NUM_OUTPUTS
		if(Parameters.parameters.booleanParameter("animateWithScaleRotationTranslation")) {
			return BASE_CPPN_NUM_OUTPUTS + 4;
		} else {
			return BASE_CPPN_NUM_OUTPUTS;
		}
	}

	/**
	 * Allows for quick and easy launching without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","allowInteractiveSave:true","io:false","netio:false","mating:true", "simplifiedInteractiveInterface:false", "fs:false", "task:edu.southwestern.tasks.interactive.animationbreeder.AnimationBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:500","imageHeight:500","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:false","includeSquareWaveFunction:false","includeFullSawtoothFunction:false","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false","loopAnimationInReverse:true", "picbreederImageScale:1.0", "picbreederImageRotation:0.0", "starkPicbreeder:true", "animateWithScaleRotationTranslation:true"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns type of file being saved (GIF)
	 */
	@Override
	protected String getFileType() {
		return "GIF";
	}

	/**
	 * Returns extension of saved images (.gif)
	 */
	@Override
	protected String getFileExtension() {
		return "gif";
	}
}
