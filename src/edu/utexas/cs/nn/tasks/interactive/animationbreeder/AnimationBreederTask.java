package edu.utexas.cs.nn.tasks.interactive.animationbreeder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;
import edu.utexas.cs.nn.util.graphics.AnimationUtil;

public class AnimationBreederTask<T extends Network> extends InteractiveEvolutionTask<T>{
	
	protected JSlider animationLength;
	protected JSlider pauseLength;
	
	
	private class AnimationThread extends Thread {
		private boolean playing;
		private int imageID;
		private int end;
		
		public AnimationThread(int imageID) {
			this.imageID = imageID;
			this.end = Parameters.parameters.integerParameter("defaultAnimationLength");
			if(animations[imageID].size() < Parameters.parameters.integerParameter("defaultAnimationLength")) {
				int start = animations[imageID].size();
				BufferedImage[] newFrames = AnimationUtil.imagesFromCPPN(scores.get(imageID).individual.getPhenotype(), picSize, picSize, start, end, getInputMultipliers());
				for(BufferedImage bi : newFrames) {
					animations[imageID].add(bi); 	
				}
			}
		}
		
		public void run() {
			playing = true;
			while(playing) {
				// One animation loop
				for(int frame = 0; playing && frame < end; frame++) {
					// set button over and over
					setButtonImage(animations[imageID].get(frame), imageID);
					try {
						Thread.sleep(Parameters.parameters.integerParameter("defaultFramePause"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}		
				}
				try {
					Thread.sleep(Parameters.parameters.integerParameter("defaultPause"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		
			}
		}
		
		public void stopAnimation() {
			playing = false;
		}		
	}
	
	public static final int CPPN_NUM_INPUTS	= 5;
	public static final int CPPN_NUM_OUTPUTS = 3;
	
	public ArrayList<BufferedImage>[] animations;
	private JSlider pauseLengthBetweenFrames;
	
	public AnimationBreederTask() throws IllegalAccessException {
		super();
		
		//Construction of JSlider for desired animation length
		
		animationLength = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minAnimationLength"), Parameters.parameters.integerParameter("maxAnimationLength"), Parameters.parameters.integerParameter("defaultAnimationLength"));
		
		Hashtable<Integer,JLabel> animationLabels = new Hashtable<>();
		animationLength.setMinorTickSpacing(20);
		animationLength.setPaintTicks(true);
		animationLabels.put(Parameters.parameters.integerParameter("minAnimationLength"), new JLabel("Short"));
		animationLabels.put(Parameters.parameters.integerParameter("maxAnimationLength"), new JLabel("Long"));
		animationLength.setLabelTable(animationLabels);
		animationLength.setPaintLabels(true);
		animationLength.setPreferredSize(new Dimension(150, 40));
		
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
					resetButtons();
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
		pauseLength.setPreferredSize(new Dimension(100, 40));
		
		/**
		 * Implements ChangeListener to adjust animation length. When animation length is specified, 
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
					// reset buttons
					resetButtons();
				}
			}
		});
		
		pauseLengthBetweenFrames = new JSlider(JSlider.HORIZONTAL, Parameters.parameters.integerParameter("minPause"), Parameters.parameters.integerParameter("maxPause"), Parameters.parameters.integerParameter("defaultPause"));
		
		Hashtable<Integer,JLabel> framePauseLabels = new Hashtable<>();
		pauseLengthBetweenFrames.setMinorTickSpacing(75);
		pauseLengthBetweenFrames.setPaintTicks(true);
		framePauseLabels.put(Parameters.parameters.integerParameter("minPause"), new JLabel("No pause"));
		framePauseLabels.put(Parameters.parameters.integerParameter("maxPause"), new JLabel("Long pause"));
		pauseLengthBetweenFrames.setLabelTable(pauseLabels);
		pauseLengthBetweenFrames.setPaintLabels(true);
		pauseLengthBetweenFrames.setPreferredSize(new Dimension(100, 40));
		
		/**
		 * Implements ChangeListener to adjust animation length. When animation length is specified, 
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
					// reset buttons
					resetButtons();
				}
			}
		});
		
		JPanel animation = new JPanel();
		animation.setLayout(new BoxLayout(animation, BoxLayout.Y_AXIS));
		JLabel animationLabel = new JLabel();
		animationLabel.setText("Animation length");
		animation.add(animationLabel);
		animation.add(animationLength);
		
		JPanel pause = new JPanel();
		pause.setLayout(new BoxLayout(pause, BoxLayout.Y_AXIS));
		JLabel pauseLabel = new JLabel();
		pauseLabel.setText("Pause between animations");
		pause.add(pauseLabel);
		pause.add(pauseLength);
		
		JPanel framePause = new JPanel();
		framePause.setLayout(new BoxLayout(framePause, BoxLayout.Y_AXIS));
		JLabel framePauseLabel = new JLabel();
		framePauseLabel.setText("Pause between frames");
		framePause.add(framePauseLabel);
		framePause.add(pauseLengthBetweenFrames);
		
		top.add(animation);
		top.add(pause);
		top.add(framePause);
		
		
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
					id.next(); // throiw away tokens in string name
					id.next(); // the third will be the id number
					animation = new AnimationThread(id.nextInt());
					id.close();
					animation.start();
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					animation.stopAnimation();
				}
			});
		}
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias", "time" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "hue-value", "saturation-value", "brightness-value" };
	}

	@Override
	protected String getWindowTitle() {
		return "AnimationBreeder";
	}

	@Override
	protected void save(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		// Just get first frame for button. Slightly inefficent though, since all animation frames were pre-computed
		return AnimationUtil.imagesFromCPPN(phenotype, picSize, picSize, 0, 1, getInputMultipliers())[0];
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		// Load all Image arrays with animations
		animations = new ArrayList[population.size()];
		for(int i = 0; i < animations.length; i++) {
			animations[i] = new ArrayList<BufferedImage>();
 		}
		return super.evaluateAll(population); // wait for user choices
	}

	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	@Override
	public int numCPPNOutputs() {
		return CPPN_NUM_OUTPUTS;
	}

	/**
	 * Allows for quick and easy launching without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","task:edu.utexas.cs.nn.tasks.interactive.animationbreeder.AnimationBreederTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
