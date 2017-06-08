package edu.utexas.cs.nn.tasks.interactive.animationbreeder;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;
import edu.utexas.cs.nn.util.graphics.AnimationUtil;

public class AnimationBreederTask<T extends Network> extends InteractiveEvolutionTask<T>{
	
	public static final int TIME = 100; // TODO: change eventually? // probably have a slide bar
	
	private class AnimationThread extends Thread {
		private boolean playing;
		private int imageID;
		
		public AnimationThread(int imageID) {
			this.imageID = imageID;
		}
		
		public void run() {
			playing = true;
			int frame = 0;
			while(playing) {
				
				// set button over and over
				setButtonImage(animations[imageID][frame], imageID);
				
				frame++;
				frame = frame % animations[imageID].length;
			}
		}
		
		public void stopAnimation() {
			playing = false;
		}		
	}
	
	public static final int CPPN_NUM_INPUTS	= 5;
	public static final int CPPN_NUM_OUTPUTS = 3;
	
	public BufferedImage[][] animations;
	
	public AnimationBreederTask() throws IllegalAccessException {
		super();
		
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
		return AnimationUtil.imagesFromCPPN(phenotype, picSize, picSize, 1, getInputMultipliers())[0];
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		// Load all Image arrays with animations
		animations = new BufferedImage[population.size()][];
		for(int i = 0; i < population.size(); i++) {
			animations[i] = AnimationUtil.imagesFromCPPN(population.get(i).getPhenotype(), picSize, picSize, TIME, getInputMultipliers());
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
