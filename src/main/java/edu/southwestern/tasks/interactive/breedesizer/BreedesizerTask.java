package edu.southwestern.tasks.interactive.breedesizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.sound.MIDIUtil;
import edu.southwestern.util.sound.PlayDoubleArray;
import edu.southwestern.util.sound.PlayDoubleArray.AmplitudeArrayPlayer;
import edu.southwestern.util.sound.SoundFromCPPNUtil;

/**
 * Class that builds an interface with a variety of sound waves that have been generated with
 * an input CPPN. These sound waves are represented with an image and with audio, and can be evolved,
 * saved, and mutated with various activation functions. This class extends InteractiveEvolutionTask,
 * which is also used to design the Picbreeder and Remixbreeder interfaces. 
 * 
 * Original Breedesizer paper: 
 * Bj\"{o}rn p\'{o}r J\'{o}nsson, Amy K. Hoover, and Sebastian Risi. Interactively Evolving Compositional 
 * Sound Synthesis Networks. Proceedings of the 2015 Genetic and Evolutionary Computation 
 * Conference (GECCO '15). Pages 321-328. 2015. 
 * DOI: http://dx.doi.org/10.1145/2739480.2754796
 * 
 * @author Isabel Tweraser
 *
 * @param <T>
 */
public class BreedesizerTask<T extends Network> extends InteractiveEvolutionTask<T> {

	//private static final int LENGTH_DEFAULT = 60000; //default length of generated amplitude
	public static final int FREQUENCY_DEFAULT = 440; //default frequency of generated amplitude: A440

	//ideal numbers to initialize AudioFormat; based on obtaining formats of a series of WAV files
	public static final float DEFAULT_SAMPLE_RATE = 11025; //default frame rate is same value
	public static final int DEFAULT_BIT_RATE = 8; 
	public static final int DEFAULT_CHANNEL = 1; 
	public static final int BYTES_PER_FRAME = 1; 

	public static final int CPPN_NUM_INPUTS	= 3;
	public static final int CPPN_NUM_OUTPUTS = 1;

	private static final int MIDI_PLAY_BUTTON_INDEX = CHECKBOX_IDENTIFIER_START - CPPN_NUM_INPUTS; //index of button for MIDI playback
	private static final int FILE_LOADER_BUTTON_INDEX = CHECKBOX_IDENTIFIER_START - CPPN_NUM_INPUTS - 1; //index for button to load new MIDI file
	private static final int MIDI_PLAYBACK_TYPE_CHECKBOX_INDEX = CHECKBOX_IDENTIFIER_START - CPPN_NUM_INPUTS - 2; //index for type of MIDI playback

	Keyboard keyboard;
	protected JSlider clipLength;
	protected boolean initializationComplete = false;
	protected AmplitudeArrayPlayer arrayPlayer = null;
	public double noteLengthScale;
	private JCheckBox MIDIPlaybackType;

	// Controls MIDI playback, and allows for interruption
	private AmplitudeArrayPlayer midiPlay = null;

	private JSlider speedOfMIDI;

	public BreedesizerTask() throws IllegalAccessException {
		this(true);
	}

	public BreedesizerTask(boolean justBreedesizer) throws IllegalAccessException {
		super();
		midiPlay = new AmplitudeArrayPlayer(); // no sequence to play

		//Construction of JSlider to determine length of generated CPPN amplitude
		clipLength = new JSlider(JSlider.HORIZONTAL, Keyboard.NOTE_LENGTH_DEFAULT, Parameters.parameters.integerParameter("maxClipLength"), Parameters.parameters.integerParameter("clipLength"));
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		clipLength.setMinorTickSpacing(10000);
		clipLength.setPaintTicks(true);
		labels.put(Keyboard.NOTE_LENGTH_DEFAULT, new JLabel("Shorter clip"));
		labels.put(Parameters.parameters.integerParameter("maxClipLength"), new JLabel("Longer clip"));
		clipLength.setLabelTable(labels);
		clipLength.setPaintLabels(true);
		clipLength.setPreferredSize(new Dimension(200, 40));

		/**
		 * Implements ChangeListener to adjust clip length of generated sounds. When clip length is specified, 
		 * input length is used to reset and redraw buttons. 
		 */
		clipLength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!initializationComplete) return;
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {

					int newLength = (int) source.getValue();

					Parameters.parameters.setInteger("clipLength", newLength);
					// reset buttons
					resetButtons(true);
				}
			}
		});
		
		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(clipLength);	
		}

		if(justBreedesizer) {
			//keyboard constructor
			keyboard = new Keyboard();

			int minMultiplier = 10; //non-scaled minimum value (correlates with fastest speed)
			int maxMultiplier = 400; //non-scaled maximum value (correlates with slowest speed)
			int defaultMultiplier = 100; //non-scaled default value - simply amplitude length multiplier
			final double scale = 100.0;
			noteLengthScale = defaultMultiplier/scale; //no scaling by default

			//Construction of JSlider used to determine playback speed of MIDI file
			speedOfMIDI = new JSlider(JSlider.HORIZONTAL, minMultiplier, maxMultiplier, defaultMultiplier);
			Hashtable<Integer,JLabel> speedLabels = new Hashtable<>();
			speedOfMIDI.setMinorTickSpacing(40);
			speedOfMIDI.setPaintTicks(true);
			speedLabels.put(minMultiplier, new JLabel("Fast"));
			speedLabels.put(maxMultiplier, new JLabel("Slow"));
			speedOfMIDI.setLabelTable(speedLabels);
			speedOfMIDI.setPaintLabels(true);
			speedOfMIDI.setPreferredSize(new Dimension(150, 40));		
			speedOfMIDI.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if(!initializationComplete) return;
					// get value
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting()) {

						double newSpeed = source.getValue()/scale;

						noteLengthScale = newSpeed;
						// reset buttons: necessary?
						resetButtons(true);
					}
				}

			});
			top.add(speedOfMIDI);

			//Construction of button that plays MIDI file with the selected CPPN(s)
			JButton playWithMIDI = new JButton("PlayWithMIDI");
			// Name is first available numeric label after the input disablers
			playWithMIDI.setName("" + (MIDI_PLAY_BUTTON_INDEX));
			playWithMIDI.addActionListener(this);
			top.add(playWithMIDI);
			JButton fileLoadButton = new JButton();
			fileLoadButton.setText("ChooseNewSound");
			fileLoadButton.setName("" + FILE_LOADER_BUTTON_INDEX);
			fileLoadButton.addActionListener(this);

			//JCheckbox to specify whether MIDI playback occurs with one CPPN or multiple CPPNs
			MIDIPlaybackType = new JCheckBox("advancedMIDIPlayback", false);
			MIDIPlaybackType.setName("" + MIDI_PLAYBACK_TYPE_CHECKBOX_INDEX);
			MIDIPlaybackType.addActionListener(this);
			MIDIPlaybackType.setForeground(new Color(0,0,0));

			if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {	
				top.add(fileLoadButton);
				top.add(MIDIPlaybackType);
			}

		}		
		initializationComplete = true;
	}

	/**
	 * Calls action associated with clicking a certain button - in this case, the button plays a MIDI
	 * file with the most recently clicked CPPN as the "instrument", or uses a series of most recently
	 * clicked CPPNs as instruments for each track of the file
	 */
	protected boolean respondToClick(int itemID) {
		boolean justStopped = false;
		if(midiPlay.isPlaying()) {
			midiPlay.stopPlayback();
			justStopped = true;
		}
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		// Play original sound if they click the button
		if(itemID == (MIDI_PLAY_BUTTON_INDEX)) {
			Network[] cppns = new Network[selectedItems.size()];
			if(!justStopped) { // Pressing original button can stop playback too
				if(!MIDIPlaybackType.isSelected()) { // action for simple MIDI playback
					midiPlay = MIDIUtil.playMIDIWithCPPNFromString(Parameters.parameters.stringParameter("remixMIDIFile"), currentCPPN, noteLengthScale);
				} else { // action for advanced MIDI playback
					//read in all CPPNs from selectedCPPNS list to an array of networks
					// Read backwards so most recent selection is present
					for(int i = selectedItems.size() - 1; i >= 0; i--) { 
						cppns[i] = scores.get(selectedItems.get(i)).individual.getPhenotype();
					}
					midiPlay = MIDIUtil.playMIDIWithCPPNsFromString(Parameters.parameters.stringParameter("remixMIDIFile"), cppns, noteLengthScale);
				}
			}
		}
		if(itemID == FILE_LOADER_BUTTON_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("MIDI Files", "mid");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				Parameters.parameters.setString("remixMIDIFile", chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName());
			}
			// reset necessary?
			resetButtons(true);
		}
		return false; // no undo: every thing is fine
	}

	@Override
	public String[] sensorLabels() {
		return new String[] { "Time", "Sine of time", "bias" };
	}

	@Override
	public String[] outputLabels() {
		return new String[] { "amplitude" };
	}

	@Override
	protected String getWindowTitle() {
		return "Breedesizer";
	}

	/**
	 * Creates BufferedImage from amplitude generated by network (saved in double array) and plays amplitude generated. 
	 */
	@Override
	protected BufferedImage getButtonImage(T phenotype, int width, int height, double[] inputMultipliers) {
		double[] amplitude = SoundFromCPPNUtil.amplitudeGenerator(phenotype, Parameters.parameters.integerParameter("clipLength"), FREQUENCY_DEFAULT, inputMultipliers);
		BufferedImage wavePlotImage = GraphicsUtil.wavePlotFromDoubleArray(amplitude, height, width);
		return wavePlotImage;
	}

	/**
	 * Plays sound associated with an image when the image is clicked
	 */
	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<T> individual) {
		if(arrayPlayer != null) { // Always stop any currently playing sound
			arrayPlayer.stopPlayback();
		}

		if(chosen[scoreIndex]) { // Play sound if item was just selected
			Network phenotype = individual.getPhenotype();
			double[] amplitude = SoundFromCPPNUtil.amplitudeGenerator(phenotype, Parameters.parameters.integerParameter("clipLength"), FREQUENCY_DEFAULT, inputMultipliers);
			arrayPlayer = PlayDoubleArray.playDoubleArray(amplitude);	
			keyboard.setCPPN(phenotype);
		} 
	}

	@Override
	protected void save(String file, int i) {	
		// Use of imageHeight and imageWidth allows saving a higher quality image than is on the button
		BufferedImage toSave = getButtonImage(scores.get(i).individual.getPhenotype(), Parameters.parameters.integerParameter("imageWidth"), Parameters.parameters.integerParameter("imageHeight"), inputMultipliers);
		//SAVING IMAGE
		GraphicsUtil.saveImage(toSave, file + ".bmp");
		System.out.println("image " + file + " was saved successfully");
		//SAVING AUDIO
		file += ".wav";
		saveSound(i, file);
		System.out.println("Audio " + file + " was saved successfully");
	}

	/**
	 * The way sound is saved has to be a different method call for Breedesizer and Remixbreeder, so this code is
	 * extracted from the original save method and made into a protected method.
	 * 
	 * @param i location of current phenotype
	 * @param chooser user input of desired file name
	 */
	protected void saveSound(int i, String filename) {
		try {
			SoundFromCPPNUtil.saveFileFromCPPN(scores.get(i).individual.getPhenotype(), Parameters.parameters.integerParameter("clipLength"), FREQUENCY_DEFAULT, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * Allows for quick and easy launching of breedesizer without saving any files
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:5","randomSeed:5","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true", "fs:false", "task:edu.southwestern.tasks.interactive.breedesizer.BreedesizerTask","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String getFileType() {
		return "WAV audio file";
	}

	@Override
	protected String getFileExtension() {
		return "wav";
	}
}
