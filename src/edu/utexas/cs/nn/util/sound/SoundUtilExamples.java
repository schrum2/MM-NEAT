package edu.utexas.cs.nn.util.sound;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;
import javazoom.jl.decoder.JavaLayerException;

/**
 * Class containing various testing examples for sound utility methods in sound package.
 * Pretty disorganized, but just storing the various examples here so we don't lose them.
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundUtilExamples {

	private static final String BEARGROWL_WAV = "data/sounds/bear_growl_y.wav";
	private static final String APPLAUSE_WAV = "data/sounds/applause_y.wav";
	public static final String HARP_WAV = "data/sounds/harp.wav";
	private static final String HAPPY_MP3 = "data/sounds/25733.mp3";
	private static final String PIRATES_MID = "data/sounds/pirates.mid";
	private static final String CLASSICAL_MID = "data/sounds/CLASSICA.MID";
	private static final String SOLO_PIANO_MID	= "data/sounds/Chon01.MID";
	public static final String FUR_ELISE_MID = "data/sounds/for_elise_by_beethoven.mid";
	private static final String BASS_16BIT_WAV 	= "data/sounds/acousticbass16bit.wav";
	public static final String SEASHORE_WAV = "data/sounds/Digital-Seashore.wav";
	public static final String ALARM_WAV = "data/sounds/tone06.wav";
	public static final String CHIPTUNE_WAV = "data/sounds/8-Bit-Noise-1.wav";
	public static final String CHRISTMAS_MID = "data/sounds/christmas.mid";
	
	//used to obtain MIDI file data
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException, JavaLayerException, InvalidMidiDataException {
		//CPPN initialization
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false","randomSeed:12"});
		MMNEAT.loadClasses();
		EvolutionaryHistory.initArchetype(0);

		HyperNEATCPPNGenotype test = new HyperNEATCPPNGenotype(3, 1, 0);
		for(int i = 0; i < 30; i++) {
			test.mutate();
		}
		Network cppn = test.getCPPN();

		// method call
		multipleCPPNMIDIPlayback();
	}

	public static void randomCPPNExamples(Network cppn) throws IOException {
		// saves CPPN with variety of AudioFormat initializations to see which one works the best
		double[] testArray = SoundFromCPPNUtil.amplitudeGenerator(cppn, 60000, 440);
		AudioFormat af1 = new AudioFormat(PlayDoubleArray.SAMPLE_RATE, PlayDoubleArray.BITS_PER_SAMPLE,1, true, true);
		SoundFromCPPNUtil.saveFileFromCPPN(cppn, 60000, 440, "cppn1.wav");
		AudioFormat af2 = new AudioFormat(PlayDoubleArray.SAMPLE_RATE/2, PlayDoubleArray.BITS_PER_SAMPLE/2,1, true, false); // Correct?
		SoundFromCPPNUtil.saveFileFromCPPN(cppn, 60000, 440, "cppn2.wav");
		AudioFormat af3 = new AudioFormat(PlayDoubleArray.SAMPLE_RATE, PlayDoubleArray.BYTES_PER_SAMPLE,1, true, true);
		SoundFromCPPNUtil.saveFileFromCPPN(cppn, 60000, 440, "cppn3.wav");
		AudioFormat af4 = new AudioFormat(PlayDoubleArray.SAMPLE_RATE, PlayDoubleArray.BYTES_PER_SAMPLE,1, true, false);
		SoundFromCPPNUtil.saveFileFromCPPN(cppn, 60000, 440, "cppn4.wav");
		PlayDoubleArray.playDoubleArray(testArray);
		GraphicsUtil.wavePlotFromDoubleArray(testArray, 500, 500);

		double[] testArray2 = SoundFromCPPNUtil.amplitudeGenerator(cppn, 60000, 440);
		PlayDoubleArray.playDoubleArray(testArray2);
		SaveFromArray.saveFileFromDoubleArray("data/sounds/testingSourceDataLine.wav", testArray2);
	}


	public static void playCPPNAtDifferentFrequencies(Network cppn) {
		// double array containing frequencies of a C Major scale
		double[] frequencies = new double[]{261.626, 293.665, 329.628, 349.228, 391.995, 440.0, 493.883, 523.251};
		for(int i = 0; i < frequencies.length; i++) {
			double[] scaleCPPN = SoundFromCPPNUtil.amplitudeGenerator(cppn, 30000, frequencies[i]);
			PlayDoubleArray.playDoubleArray(scaleCPPN);
		}
	}

	public static void byteToIntToDouble() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		byte[] harpByteArray = WAVUtil.WAVToByte(HARP_WAV);
		AudioInputStream harpAIS = WAVUtil.byteToAIS(harpByteArray);
		AudioFormat harpFormat = harpAIS.getFormat();
		// Change to int[]
		int[] harpIntArray = SoundToArray.extractAmplitudeDataFromAmplitudeByteArray(harpFormat, harpByteArray);
		double[] harpDoubleArray = SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(harpIntArray, 8);
		PlayDoubleArray.playDoubleArray(harpDoubleArray);
	}

	// PlayDoubleArray works for WAV files with this format:
	// PCM_SIGNED, 16 bit, mono, 2 bytes/frame, little-endian
	public static void sixteenBit44100HzTests() throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
		WAVUtil.playWAVFile(SEASHORE_WAV);
		AudioInputStream seashoreAIS = WAVUtil.audioStream(SEASHORE_WAV);

		// testing converting WAV file using step-by-step method calls
		int[] seashoreIntArray = SoundToArray.extractAmplitudeDataFromAudioInputStream(seashoreAIS);
		double[] seashoreDoubleArray = SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(seashoreIntArray, 16);
		PlayDoubleArray.playDoubleArray(seashoreDoubleArray, false);

		// testing converting WAV files using shortcut method call
		seashoreDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(SEASHORE_WAV);
		PlayDoubleArray.playDoubleArray(seashoreDoubleArray, false);

		MiscUtil.waitForReadStringAndEnterKeyPress();

		WAVUtil.playWAVFile(CHIPTUNE_WAV);	

		double[] chiptuneDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(CHIPTUNE_WAV);
		PlayDoubleArray.playDoubleArray(chiptuneDoubleArray, false);

	}

	// sixteen bit files can now be played at multiple sample rates!
	public static void sixteenBit11025HzTests() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		WAVUtil.playWAVFile(ALARM_WAV);
		AudioInputStream alarmAIS = WAVUtil.audioStream(ALARM_WAV);
		double[] alarmDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(ALARM_WAV);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		PlayDoubleArray.playDoubleArray(alarmAIS.getFormat(), alarmDoubleArray);
	}

	// cannot currently play WAV files with stereo channels
	public static void sixteenBitStereoTests() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		WAVUtil.playWAVFile(BASS_16BIT_WAV);
		byte[] bassByteArray = WAVUtil.WAVToByte(BASS_16BIT_WAV);
		WAVUtil.playByteAIS(bassByteArray);
		//can this byte array be saved back to a wav?
		AudioInputStream bassAIS = WAVUtil.audioStream(BASS_16BIT_WAV);
		AudioFormat bassFormat = bassAIS.getFormat();
		int[] bassIntArray = SoundToArray.extractAmplitudeDataFromAmplitudeByteArray(bassFormat, bassByteArray);
		// directly form file instead?
		bassIntArray = SoundToArray.extractAmplitudeDataFromAudioInputStream(bassAIS);
		double[] bassDoubleArray = SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(bassIntArray, 16);
		PlayDoubleArray.playDoubleArray(bassDoubleArray);
	}

	// cannot currently play eight bit WAV files
	public static void eightBitTests() throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
		double[] harpDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(HARP_WAV);
		AudioInputStream harpAIS = WAVUtil.audioStream(HARP_WAV);
		//System.out.println(Arrays.toString(harpDoubleArray));
		WAVUtil.playWAVFile(HARP_WAV);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		PlayDoubleArray.playDoubleArray(harpAIS.getFormat(), harpDoubleArray, false);	
	}

	// Testing to see if using a different byte conversion method (the one from WAVUtil) would work better than the 
	// extractAmplitudeByteArrayFromAudioInputStream method in SoundToArray. this doesn't work - it sounds the same 
	// if not worse than the original method.
	public static void useDifferentByteConversion() throws UnsupportedAudioFileException, IOException, InterruptedException, LineUnavailableException {
		AudioInputStream harpAIS = WAVUtil.audioStream(HARP_WAV);
		byte[] harpNumbers = SoundToArray.extractAmplitudeByteArrayFromAudioInputStream(harpAIS);
		//WAVUtil.playByteAIS(harpNumbers); // DOES NOT WORK?!?
		int[] harpIntArray = SoundToArray.extractAmplitudeDataFromAmplitudeByteArray(harpAIS.getFormat(), harpNumbers);
		double[] harpDoubleArray = SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(harpIntArray, harpAIS.getFormat().getSampleSizeInBits());
		//PlayDoubleArray.playDoubleArray(harpAIS.getFormat(), harpDoubleArray, false);
	}

	public static void getAudioFormat() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		// testing writeSingleChannel to see if the problem saving files has to do with initialization of AudioFormat
		// harp AudioFormat
		File harp = new File(HARP_WAV);
		AudioInputStream harpAIS = WAVUtil.audioStream(harp);
		System.out.println("harp: " + harpAIS.getFormat());
		System.out.println("Sample rate: " + harpAIS.getFormat().getSampleRate());
		System.out.println("Frame rate: " + harpAIS.getFormat().getFrameRate());
		System.out.println("getSampleSizeInBits: " + harpAIS.getFormat().getSampleSizeInBits());
		System.out.println("bytes/frame: " + harpAIS.getFormat().getFrameSize());
		System.out.println(harpAIS.getFormat().isBigEndian());
		System.out.println();
		// bear AudioFormat
		File bear = new File(BEARGROWL_WAV);
		AudioInputStream bearAIS = WAVUtil.audioStream(bear);
		System.out.println("bear: " + bearAIS.getFormat());
		System.out.println(bearAIS.getFormat().getSampleRate());
		System.out.println(bearAIS.getFormat().getFrameRate());
		System.out.println(bearAIS.getFormat().isBigEndian());
		System.out.println();
		// applause AudioFormat
		File applause = new File(APPLAUSE_WAV);
		AudioInputStream applauseAIS = WAVUtil.audioStream(applause);
		System.out.println("applause: " + applauseAIS.getFormat());
		System.out.println(applauseAIS.getFormat().getSampleRate());
		System.out.println(applauseAIS.getFormat().getFrameRate());
		System.out.println(applauseAIS.getFormat().isBigEndian());
		System.out.println();
		// seashore AudioFormat
		AudioInputStream bassAIS = WAVUtil.audioStream(BASS_16BIT_WAV);
		AudioFormat bassFormat = bassAIS.getFormat();
		System.out.println("Bass format: " + bassFormat);
		System.out.println("Sample rate: " + bassFormat.getSampleRate());
		System.out.println("Frame rate: " + bassFormat.getFrameRate());
		// seashore AudioFormat
		AudioInputStream seashoreAIS = WAVUtil.audioStream(SEASHORE_WAV);
		AudioFormat seashoreFormat = seashoreAIS.getFormat();
		System.out.println("Seashore format: " + seashoreFormat);
		System.out.println("Sample rate: " + seashoreAIS.getFormat().getSampleRate());
		System.out.println("Frame rate: " + seashoreAIS.getFormat().getFrameRate());
		// alarm AudioFormat
		AudioInputStream alarmAIS = WAVUtil.audioStream(ALARM_WAV);
		AudioFormat alarmFormat = alarmAIS.getFormat();
		System.out.println("Alarm format: " + alarmFormat);
		System.out.println("Sample rate: " + alarmAIS.getFormat().getSampleRate());
		System.out.println("Frame rate: " + alarmAIS.getFormat().getFrameRate());
		// chip tune AudioFormat
		AudioInputStream chiptuneAIS = WAVUtil.audioStream(CHIPTUNE_WAV);
		AudioFormat chiptuneFormat = chiptuneAIS.getFormat();
		System.out.println("Chiptune format: " + chiptuneFormat);
		System.out.println("Sample rate: " + chiptuneAIS.getFormat().getSampleRate());
		System.out.println("Frame rate: " + chiptuneAIS.getFormat().getFrameRate());

	}

	public static void plotExamples(Network cppn) {
		double[] testArray2 = SoundFromCPPNUtil.amplitudeGenerator(cppn, 60000, 440);
		ArrayList<Double> fileArrayList2 = ArrayUtil.doubleVectorFromArray(testArray2); //convert array into array list
		DrawingPanel panel2 = new DrawingPanel(500,500, "2"); //create panel where line will be plotted 
		GraphicsUtil.linePlot(panel2, -1.0, 1.0, fileArrayList2, Color.black); //call linePlot with ArrayList to draw graph
		MiscUtil.waitForReadStringAndEnterKeyPress();

	}

	public static void playFileExamples() throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
		WAVUtil.playWAVFile(BEARGROWL_WAV);
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		System.out.println(Arrays.toString(bearNumbers));
		AudioInputStream bearAIS = WAVUtil.byteToAIS(bearNumbers);
		int[] bearData = SoundToArray.extractAmplitudeDataFromAudioInputStream(bearAIS);
		System.out.println(Arrays.toString(bearData));

		byte[] applauseNumbers =WAVUtil.WAVToByte(APPLAUSE_WAV);	
		System.out.println(Arrays.toString(applauseNumbers));

		byte[] harpNumbers = WAVUtil.WAVToByte(HARP_WAV);
		System.out.println(Arrays.toString(harpNumbers));
		AudioInputStream harpAIS = WAVUtil.byteToAIS(harpNumbers);
		int[] harpData = SoundToArray.extractAmplitudeDataFromAudioInputStream(harpAIS);
		System.out.println(Arrays.toString(harpData));

		WAVUtil.playByteAIS(applauseNumbers);
	}

	public static void attemptsToManipulateWAVFiles() throws IOException, UnsupportedAudioFileException {
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		byte[] applauseNumbers =WAVUtil.WAVToByte(APPLAUSE_WAV);	
		AudioInputStream applauseAIS = WAVUtil.byteToAIS(applauseNumbers);
		double[] bear = new double[bearNumbers.length];
		// Trying to take a preexisting WAV file and rewrite certain numbers in it to see how the sound output
		// would be affected. These tests proved to be unsuccessful
		for(int i = 0; i < bear.length; i++) {
			if(i < 50120 || i >= bearNumbers.length-11) 
				bear[i] = bearNumbers[i];
			else 
				bear[i] = 20;
		}
		bear[bear.length-1] = 0;
		PlayDoubleArray.playDoubleArray(bear);

		double[] original = new double[bearNumbers.length];
		for(int i = 0; i < original.length; i++) {
			if(i < 50120 || i >= bearNumbers.length-11) 
				original[i] = bearNumbers[i];
			else 
				original[i] = 20;
		}
		original[original.length-1] = 0;
		PlayDoubleArray.playDoubleArray(original);

		for(int i = 0; i < original.length; i++) {
			if(i < 50120 || i >= bearNumbers.length-11) 
				original[i] = bearNumbers[i];
			else 
				original[i] = 20;
		}
		original[original.length-1] = 0;
		PlayDoubleArray.playDoubleArray(original);
	}

	public static void spliceWAVFiles() throws IOException {
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		byte[] applauseNumbers =WAVUtil.WAVToByte(APPLAUSE_WAV);
		double[] splice = new double[applauseNumbers.length];
		for(int i = 0; i < splice.length; i++) {
			if(i < 46)
				splice[i] = bearNumbers[i];
			else 
				splice[i] = applauseNumbers[i];
		}
		PlayDoubleArray.playDoubleArray(splice);
	}

	public static void overlapWAVFiles() throws IOException, UnsupportedAudioFileException {
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		byte[] applauseNumbers =WAVUtil.WAVToByte(APPLAUSE_WAV);	
		byte[] harpNumbers = WAVUtil.WAVToByte(HARP_WAV);
		System.out.println("bear growl: " + bearNumbers.length);
		System.out.println("applause: " + applauseNumbers.length);
		System.out.println("harp: " + harpNumbers.length);
		double[] applauseAndHarp = ArrayUtil.overlap(APPLAUSE_WAV, HARP_WAV);
		for(int i = 0; i < applauseAndHarp.length; i++) {
			applauseAndHarp[i] = applauseAndHarp[i]/2.0; //to avoid overflow
		}
		System.out.println("applauseAndHarp length: " + applauseAndHarp.length);
		double[] bearGrowlAndHarp = ArrayUtil.overlap(BEARGROWL_WAV, HARP_WAV);
		System.out.println("bearGrowlAndHarp length: " + bearGrowlAndHarp.length);
		double[] applauseAndBearGrowl = ArrayUtil.overlap(APPLAUSE_WAV, BEARGROWL_WAV);
		System.out.println("applauseAndBearGrowl length: " + applauseAndBearGrowl.length);
		
		AudioInputStream harpAIS = WAVUtil.audioStream(HARP_WAV);

		PlayDoubleArray.playDoubleArray(harpAIS.getFormat(), applauseAndHarp);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		PlayDoubleArray.playDoubleArray(harpAIS.getFormat(), bearGrowlAndHarp);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		PlayDoubleArray.playDoubleArray(harpAIS.getFormat(), applauseAndBearGrowl);
	}

	// Won't work with the new AmplitudeArrayPlayer that contains the playDouble method (no longer static).
	// Maybe find a way to add back these examples later
	public static void toneGeneratorExamples() {
		// 440 Hz for 1 sec
		double freq1 = 440.0;
		//uses sine function to generate sound wave
		//		for (int i = 0; i <= PlayDoubleArray.SAMPLE_RATE; i++) {
		//			PlayDoubleArray.playDouble(0.5 * Math.sin(2*Math.PI * freq1 * i / PlayDoubleArray.SAMPLE_RATE));
		//		}
		//		//uses square wave function to generate sound wave
		//		for (int i = 0; i <= PlayDoubleArray.SAMPLE_RATE; i++) {
		//			PlayDoubleArray.playDouble(0.5 * ActivationFunctions.squareWave(2*Math.PI * freq1 * i / PlayDoubleArray.SAMPLE_RATE));
		//		}
		//		//uses triangle wave function to generate sound wave
		//		for (int i = 0; i <= PlayDoubleArray.SAMPLE_RATE; i++) {
		//			PlayDoubleArray.playDouble(0.5 * ActivationFunctions.triangleWave(2*Math.PI * freq1 * i / PlayDoubleArray.SAMPLE_RATE));
		//		}
		//Fills up double array with sounds generated by sine function
		double[] exampleSound = new double[PlayDoubleArray.SAMPLE_RATE+1];
		double freq2 = 440.0;
		for (int i = 0; i <= PlayDoubleArray.SAMPLE_RATE; i++) {
			exampleSound[i] = (0.5 * Math.sin(2*Math.PI * freq2 * i / PlayDoubleArray.SAMPLE_RATE));
		}
		PlayDoubleArray.playDoubleArray(exampleSound);
		//Fills double array with sounds generated by full sawtooth function
		for (int i = 0; i <= PlayDoubleArray.SAMPLE_RATE; i++) {
			exampleSound[i] = (0.5 * ActivationFunctions.fullSawtooth(2*Math.PI * freq2 * i / PlayDoubleArray.SAMPLE_RATE));
		}
		PlayDoubleArray.playDoubleArray(exampleSound);
		//Fills double array with sounds generated by tanh function
		for (double i = 0; i <= PlayDoubleArray.SAMPLE_RATE; i++) {
			exampleSound[(int)i] = (ActivationFunctions.tanh( 2*(i/PlayDoubleArray.SAMPLE_RATE) - 1 ));
		}
		PlayDoubleArray.playDoubleArray(exampleSound);
	}

	public static void saveFileFromArrayExamples() throws IOException, UnsupportedAudioFileException {	
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		SaveFromArray.saveFileFromByteArray(bearNumbers, "data/sounds/bearGrowlCopy.wav");

		File happyFile = new File(HAPPY_MP3);
		AudioInputStream happyStream = WAVUtil.audioStream(happyFile);
		byte[] happyNumbers = SoundToArray.extractAmplitudeByteArrayFromAudioInputStream(happyStream);

		SaveFromArray.saveFileFromByteArray(happyNumbers, "data/sounds/happyCopy.mp3");
		String classical = "data/sounds/CLASSICA.MID";
		byte[] classicalNumbers = SoundToArray.readByte(classical);
		SaveFromArray.saveFileFromByteArray(classicalNumbers, "data/sounds/classicalCopy.mid");

		// Not necessary any more?
		//PlayDoubleArray.close(); 
	}

	
	public static void saveWAVFileForRemix() throws IOException {
		byte[] alarmByteArray = WAVUtil.WAVToByte(ALARM_WAV);
		SaveFromArray.saveFileFromByteArray(alarmByteArray, "data/sounds/alarmCopy.wav");
	}

	public static void twoSoundsSimultaneously() {
		AudioInputStream aisAlarm;
		AudioInputStream aisChiptune;
		try {
			aisAlarm = WAVUtil.audioStream(ALARM_WAV);
			double[] alarm = SoundToArray.readDoubleArrayFromStringAudio(ALARM_WAV);

			aisChiptune = WAVUtil.audioStream(CHIPTUNE_WAV);
			double[] chiptune = SoundToArray.readDoubleArrayFromStringAudio(CHIPTUNE_WAV);

			PlayDoubleArray.playDoubleArray(aisAlarm.getFormat(), alarm, true);
			PlayDoubleArray.playDoubleArray(aisChiptune.getFormat(), chiptune, true);		

		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}	
	}

	// Tests meant to adjust specific aspects of a file's AudioFormat. Conversion does not work because SourceDataLine does not
	// support the format. I tried this with two 16 bit audio files that have different formats, and neither of their formats could
	// be integrated with the 8-bit format to produce a new AudioFormat. this means that the SourceDataLine won't run unless the
	// AudioFormat is extremely specific and the components match up in a certain way.
	public static void adjustAudioFormat() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		AudioInputStream harpAIS = WAVUtil.audioStream(HARP_WAV);

		//test with 11025 Hz 16-bit file

		//		AudioInputStream alarmAIS = WAVUtil.audioStream(ALARM_WAV);
		//		AudioInputStream adjustedAIS = SoundToArray.convertSampleSizeAndEndianess(alarmAIS.getFormat().getSampleSizeInBits(), alarmAIS.getFormat().isBigEndian(), harpAIS);

		//test with 44100Hz 16-bit file

		AudioInputStream seashoreAIS = WAVUtil.audioStream(SEASHORE_WAV);
		AudioInputStream adjustedAIS = SoundToArray.convertSampleSizeAndEndianess(seashoreAIS.getFormat().getSampleSizeInBits(), seashoreAIS.getFormat().isBigEndian(), harpAIS);
		int[] harpIntArray = SoundToArray.extractAmplitudeDataFromAudioInputStream(adjustedAIS);
		double[] harpDoubleArray = SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(harpIntArray, adjustedAIS.getFormat().getSampleSizeInBits());
		PlayDoubleArray.playDoubleArray(adjustedAIS.getFormat(), harpDoubleArray);
		Clip clip = WAVUtil.makeClip(adjustedAIS);
		WAVUtil.playClip(clip);
	}

	// Test that converts entire audio format of audio into a 16-bit format that is known to be supported.
	// The sound will play, but it sounds just as bad as before. Just changing the AudioFormat to a format
	// that works won't make the file play properly if its original AudioFormat doesn't match.
	public static void changeEntireAudioFormat() throws UnsupportedAudioFileException, IOException {
		AudioInputStream harpAIS = WAVUtil.audioStream(HARP_WAV);
		AudioFormat adjustedFormat = SoundToArray.getAudioFormatRestrictedTo16Bits(harpAIS.getFormat());
		double[] harpDoubleArray = SoundToArray.readDoubleArrayFromStringAudio(HARP_WAV);
		PlayDoubleArray.playDoubleArray(adjustedFormat, harpDoubleArray, true);
	}

	public static void viewMIDIChannel() {
		File file = new File(PIRATES_MID);
		MIDIData(file);
	}

	
	public static void eightBitToSixteenBit() {
		double[] harpAsSixteenBit = SoundToArray.eightBitToSixteenBit(HARP_WAV);
		System.out.println(Arrays.toString(harpAsSixteenBit));
		PlayDoubleArray.playDoubleArray(harpAsSixteenBit);
	}
	
	public static void printMIDIData() {
		File classicalFile = new File(CLASSICAL_MID);
		MIDIData(classicalFile);
		File piratesFile = new File(PIRATES_MID);
		MIDIData(piratesFile);
		File soloPiano = new File(SOLO_PIANO_MID);
		MIDIData(soloPiano);
		File furElise = new File(FUR_ELISE_MID);
		MIDIData(furElise);
	}
	
	/**
	 * Method that takes in a MIDI file and prints out useful information about the note, whether the 
	 * note is on or off, the key, and the velocity. This is printed for each individual track in the 
	 * MIDI file.
	 * 
	 * Not necessary for functioning of other methods, but contains useful information about 
	 * functioning of MIDI files (channels, tracks, notes, velocity, etc.)
	 * 
	 * @param audioFile input MIDI file
	 */
	public static void MIDIData(File audioFile) {
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
			System.out.println("tick length: " + sequence.getTickLength());
			System.out.println("microsecond length: " + sequence.getMicrosecondLength());
			System.out.println("resolution: " + sequence.getResolution());
			System.out.println("division type: " + sequence.getDivisionType());
			int trackNumber = 0;
			for (Track track :  sequence.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				System.out.println();
				for (int i=0; i < track.size(); i++) { 
					MidiEvent event = track.get(i);
					System.out.print("@" + event.getTick() + " ");
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						System.out.print("Channel: " + sm.getChannel() + " ");
						if (sm.getCommand() == NOTE_ON) {
							int key = sm.getData1();
							int octave = (key / 12)-1;
							int note = key % 12;
							String noteName = NOTE_NAMES[note];
							int velocity = sm.getData2();
							System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
						} else if (sm.getCommand() == NOTE_OFF) {
							int key = sm.getData1();
							int octave = (key / 12)-1;
							int note = key % 12;
							String noteName = NOTE_NAMES[note];
							int velocity = sm.getData2();
							System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
						} else {
							System.out.println("Command:" + sm.getCommand());
						}
					} else {
						//System.out.println("Other message: " + message.getClass());
					}
				}

				System.out.println();
			}
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}		
	}
	
	//Test printing a MIDI file's representative data after it is broken into lines 
	//of representative frequencies, lengths, and start times
	public static void newMIDIUtilPrint() throws InvalidMidiDataException, IOException {
		File midiFile = new File(PIRATES_MID);
		Sequence sequence = MidiSystem.getSequence(midiFile);
		Track[] tracks = sequence.getTracks();
		ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> midiLists = MIDIUtil.soundLines(tracks);
		for(int i = 0; i < midiLists.size(); i++) {
			System.out.println("frequencies: " + midiLists.get(i).t1);
			System.out.println("lengths: " + midiLists.get(i).t2);
			System.out.println("start times: " + midiLists.get(i).t3);
			System.out.println();
		}
	}
	
	//Test playing MIDI file after it is manipulated by a CPPN
	public static void newMIDIUtilPlay(Network cppn) throws InvalidMidiDataException, IOException {
		//MIDIUtil.playApplet(SOLO_PIANO_MID);
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		File midiFile = new File(CHRISTMAS_MID);
		Sequence sequence = MidiSystem.getSequence(midiFile);
		Track[] tracks = sequence.getTracks();
		ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> midiLists = MIDIUtil.soundLines(tracks);		
		double[] amplitudes = MIDIUtil.lineToAmplitudeArray(CHRISTMAS_MID, midiLists, cppn, 1);
		PlayDoubleArray.playDoubleArray(amplitudes);
	}
	
	//Tests printing the calculated amplitude length multiplier of multiple MIDI files so that I could compare 
	//with the ideal value and see how to manipulate the multiplier calculation to obtain that value
	public static void printALM() {
		double classicalALM = MIDIUtil.getAmplitudeLengthMultiplier(CLASSICAL_MID);
		double furEliseALM = MIDIUtil.getAmplitudeLengthMultiplier(FUR_ELISE_MID);
		double piratesALM = MIDIUtil.getAmplitudeLengthMultiplier(PIRATES_MID);
		System.out.println("classicalALM: " + classicalALM);
		System.out.println("furEliseALM: " + furEliseALM);
		System.out.println("piratesALM: " + piratesALM);
	}
	
	public static void multipleCPPNMIDIPlayback() {
		HyperNEATCPPNGenotype test = new HyperNEATCPPNGenotype(3, 1, 0);
		Network[] cppns = new Network[30];
		for(int i = 0; i < 30; i++) {
			test.mutate();
			cppns[i] = test.getCPPN();
		}
		MIDIUtil.playMIDIWithCPPNsFromString(FUR_ELISE_MID, cppns, 1);
	}
}
