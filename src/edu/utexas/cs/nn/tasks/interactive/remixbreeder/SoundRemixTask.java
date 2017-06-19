package edu.utexas.cs.nn.tasks.interactive.remixbreeder;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.breedesizer.BreedesizerTask;
import edu.utexas.cs.nn.tasks.interactive.breedesizer.Keyboard;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;
import edu.utexas.cs.nn.util.sound.PlayDoubleArray;
import edu.utexas.cs.nn.util.sound.SoundFromCPPNUtil;
import edu.utexas.cs.nn.util.sound.SoundToArray;
import edu.utexas.cs.nn.util.sound.WAVUtil;

/**
 * Interface that can play input sounds with a variety of CPPNs, like in Breedesizer, "remixing"
 * the sounds based on various activation functions. 
 * 
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class SoundRemixTask<T extends Network> extends BreedesizerTask<T> {

	public static final int CPPN_NUM_INPUTS	= 4;
	
	private static final int FILE_LOADER_BUTTON_INDEX = CHECKBOX_IDENTIFIER_START-CPPN_NUM_INPUTS-3;
	private static final int PLAY_ORIGINAL_BUTTON_INDEX = CHECKBOX_IDENTIFIER_START-CPPN_NUM_INPUTS-4;

	public double[] WAVDoubleArray;
	public int playBackRate;
	public AudioFormat format;

	public SoundRemixTask() throws IllegalAccessException {
		super(false); // do not use keyboard		
		initializationComplete = false;
		try {
			AudioInputStream AIS = WAVUtil.audioStream(Parameters.parameters.stringOptions.get("remixWAVFile"));
			format = AIS.getFormat();
			playBackRate = format.getSampleSizeInBits(); //sample size - should be changed?
			//format = SoundToArray.getAudioFormatRestrictedTo16Bits(format);
			
			// Doesn't work any more ... remove eventually
			//PlayDoubleArray.changeAudioFormat(format);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			System.exit(1);
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
		clipLength.setPreferredSize(new Dimension(200, 40));
		clipLength.setMaximum(Parameters.parameters.integerParameter("maxClipLength"));

		JButton playOriginal = new JButton("PlayOriginal");
		// Name is first available numeric label after the input disablers.
		// Extra -1 avoids conflict with play MIDI button of breedesizer.
		playOriginal.setName("" + (PLAY_ORIGINAL_BUTTON_INDEX));
		playOriginal.addActionListener(this);
		top.add(playOriginal);
		
		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("ChooseNewSound");
		fileLoadButton.setName("" + FILE_LOADER_BUTTON_INDEX);
		fileLoadButton.addActionListener(this);
		top.add(fileLoadButton);

		//WAV file converted to double array in constructor, and double array
		//saved to be manipulated further

		initializationComplete = true;
	}

	protected void respondToClick(int itemID) {
		super.respondToClick(itemID);
		
		if(arrayPlayer != null && arrayPlayer.isPlaying()) { // Always stop any currently playing sound
			arrayPlayer.stopPlayback();
		} else if(itemID == (PLAY_ORIGINAL_BUTTON_INDEX)) { // only play if wasn't playing
			// Play original sound if they click the button
			//arrayPlayer = PlayDoubleArray.playDoubleArray(format, WAVDoubleArray);
			try {
				// TODO: If this method could launch in a Thread or return a Thread reference, we could interrupt playback when
				//       other buttons are clicked
				WAVUtil.playWAVFile(Parameters.parameters.stringParameter("remixWAVFile"));
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(itemID == FILE_LOADER_BUTTON_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Files", "wav");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				Parameters.parameters.setString("remixWAVFile", chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName());
				try {
					AudioInputStream AIS = WAVUtil.audioStream(Parameters.parameters.stringOptions.get("remixWAVFile"));
					format = AIS.getFormat();
					playBackRate = format.getSampleSizeInBits(); //sample size - should be changed?
				} catch (UnsupportedAudioFileException | IOException e) {
					e.printStackTrace();
					System.exit(1);
				}

				WAVDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(Parameters.parameters.stringOptions.get("remixWAVFile"));
				Parameters.parameters.setInteger("clipLength", Math.min(Parameters.parameters.integerParameter("clipLength"), WAVDoubleArray.length));
				Parameters.parameters.setInteger("maxClipLength", WAVDoubleArray.length);
			}
			resetButtons();
		}
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "Time", "Sine of time", "Wav file input", "bias" };
	}

	@Override
	protected String getWindowTitle() {
		return "SoundRemix";
	}

	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		double[] amplitude = SoundFromCPPNUtil.amplitudeRemixer(phenotype, WAVDoubleArray, Parameters.parameters.integerParameter("clipLength"),FREQUENCY_DEFAULT, inputMultipliers);
		BufferedImage wavePlotImage = GraphicsUtil.wavePlotFromDoubleArray(amplitude, height, width);
		return wavePlotImage;
	}
	
	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		if(arrayPlayer != null) { // Always stop any currently playing sound
			arrayPlayer.stopPlayback();
		}

		if(chosen[scoreIndex]) {
			Network phenotype = individual.getPhenotype();
			double[] amplitude = SoundFromCPPNUtil.amplitudeRemixer(phenotype, WAVDoubleArray, Parameters.parameters.integerParameter("clipLength"), FREQUENCY_DEFAULT, inputMultipliers);
			arrayPlayer = PlayDoubleArray.playDoubleArray(amplitude);	
			// Should we use original audio format? Using it breaks stereo playback
			// arrayPlayer = PlayDoubleArray.playDoubleArray(format, amplitude);	
		} 
	}
	
	@Override
	protected void saveSound(int i, JFileChooser chooser) {
		SoundFromCPPNUtil.saveRemixedFileFromCPPN(scores.get(i).individual.getPhenotype(), WAVDoubleArray, Parameters.parameters.integerParameter("clipLength"), FREQUENCY_DEFAULT, inputMultipliers, chooser.getSelectedFile().getName() + ".wav", format);
	}
	
	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	/**
	 * For quick testing
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.utexas.cs.nn.tasks.interactive.remixbreeder.SoundRemixTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.utexas.cs.nn.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
