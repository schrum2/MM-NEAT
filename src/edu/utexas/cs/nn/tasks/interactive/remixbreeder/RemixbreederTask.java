package edu.utexas.cs.nn.tasks.interactive.remixbreeder;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.breedesizer.BreedesizerTask;
import edu.utexas.cs.nn.tasks.interactive.breedesizer.Keyboard;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;
import edu.utexas.cs.nn.util.sound.PlayDoubleArray;
import edu.utexas.cs.nn.util.sound.SoundFromCPPNUtil;
import edu.utexas.cs.nn.util.sound.SoundToArray;
import edu.utexas.cs.nn.util.sound.WAVUtil;

public class RemixbreederTask<T extends Network> extends BreedesizerTask<T> {

	public static final int CPPN_NUM_INPUTS	= 4;
	
	public double[] WAVDoubleArray;
	public int playBackFrequency;
	

	public RemixbreederTask() throws IllegalAccessException {
		super();		
		initializationComplete = false;
		try {
			AudioInputStream AIS = WAVUtil.audioStream(Parameters.parameters.stringOptions.get("remixWAVFile"));
			playBackFrequency = AIS.getFormat().getSampleSizeInBits();
			PlayDoubleArray.changeAudioFormat(AIS.getFormat());
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		WAVDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(Parameters.parameters.stringOptions.get("remixWAVFile"));
		Parameters.parameters.setInteger("clipLength", Math.min(Parameters.parameters.integerParameter("clipLength"), WAVDoubleArray.length));
		Parameters.parameters.setInteger("maxClipLength", WAVDoubleArray.length);
		
		
		
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		clipLength.setMinorTickSpacing(10000);
		clipLength.setPaintTicks(true);
		labels.put(Keyboard.NOTE_LENGTH_DEFAULT, new JLabel("Shorter clip"));
		labels.put(Parameters.parameters.integerParameter("maxClipLength"), new JLabel("Longer clip"));
		clipLength.setLabelTable(labels);
		clipLength.setPaintLabels(true);
		clipLength.setPreferredSize(new Dimension(350, 40));
		clipLength.setMaximum(Parameters.parameters.integerParameter("maxClipLength"));
		
		PlayDoubleArray.playDoubleArray(WAVDoubleArray, false);
		
		//WAV file converted to double array in constructor, and double array
		//saved to be manipulated further
		
		initializationComplete = true;
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "Time", "Sine of time", "Wav file input", "bias" };
	}

	@Override
	protected String getWindowTitle() {
		return "Breederemix";
	}

	protected void respondToClick(int itemID) {
		super.respondToClick(itemID);
	}

	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		double[] amplitude = SoundFromCPPNUtil.amplitudeRemixer(phenotype, WAVDoubleArray, Parameters.parameters.integerParameter("clipLength"), playBackFrequency, PlayDoubleArray.SAMPLE_RATE, inputMultipliers);
		BufferedImage wavePlotImage = GraphicsUtil.wavePlotFromDoubleArray(amplitude, height, width);
		return wavePlotImage;
	}

	@Override
	protected void additionalButtonClickAction(Genotype<T> individual) {
		Network phenotype = individual.getPhenotype();
		double[] amplitude = SoundFromCPPNUtil.amplitudeRemixer(phenotype, WAVDoubleArray, Parameters.parameters.integerParameter("clipLength"), playBackFrequency, PlayDoubleArray.SAMPLE_RATE, inputMultipliers);
		PlayDoubleArray.playDoubleArray(amplitude);	

	}

	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}


}
