package edu.utexas.cs.nn.util.sound;


import java.awt.Color;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.breedesizer.BreedesizerTask;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

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
	private static final String HARP_WAV = "data/sounds/harp.wav";
	private static final String HAPPY_MP3 = "data/sounds/25733.mp3";
	private static final String PIRATES = "data/sounds/pirates.mid";
	private static final String CLASSICAL_MID = "data/sounds/CLASSICA.MID";
	private static final String SOLO_PIANO_MID	= "data/sounds/Chon01.MID";
	private static final String FUR_ELISE_MID = "data/sounds/for_elise_by_beethoven.mid";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException, JavaLayerException {
		CPPNExamples();
	}

	public static void CPPNExamples() throws IOException {
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false"});
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype test = new HyperNEATCPPNGenotype(3, 1, 0);
		for(int i = 0; i < 30; i++) {
			test.mutate();
		}
		Network cppn = test.getCPPN();
//		double[] testArray = SoundAmplitudeArrayManipulator.amplitudeGenerator(cppn, 60000, 440);
//		AudioFormat af1 = new AudioFormat(MiscSoundUtil.SAMPLE_RATE, MiscSoundUtil.BITS_PER_SAMPLE,1, true, true);
//		SoundAmplitudeArrayManipulator.saveFileFromCPPN(cppn, 60000, 440, "cppn1.wav", af1);
//		AudioFormat af2 = new AudioFormat(MiscSoundUtil.SAMPLE_RATE/2, MiscSoundUtil.BITS_PER_SAMPLE/2,1, true, false); // Correct?
//		SoundAmplitudeArrayManipulator.saveFileFromCPPN(cppn, 60000, 440, "cppn2.wav", af2);
//		AudioFormat af3 = new AudioFormat(MiscSoundUtil.SAMPLE_RATE, MiscSoundUtil.BYTES_PER_SAMPLE,1, true, true);
//		SoundAmplitudeArrayManipulator.saveFileFromCPPN(cppn, 60000, 440, "cppn3.wav", af3);
//		AudioFormat af4 = new AudioFormat(MiscSoundUtil.SAMPLE_RATE, MiscSoundUtil.BYTES_PER_SAMPLE,1, true, false);
//		SoundAmplitudeArrayManipulator.saveFileFromCPPN(cppn, 60000, 440, "cppn4.wav", af4);
//		MiscSoundUtil.playDoubleArray(testArray);
//		GraphicsUtil.wavePlotFromDoubleArray(testArray, 500, 500);
		
		//AudioFormat af5 = new AudioFormat(BreedesizerTask.DEFAULT_SAMPLE_RATE, BreedesizerTask.DEFAULT_BIT_RATE, )
		
		double[] testArray = SoundAmplitudeArrayManipulator.amplitudeGenerator(cppn, 60000, 440);
		MiscSoundUtil.playDoubleArray(testArray, false);
		//File testingSourceDataLine = new File("data/sounds/testingSourceDataLine.wav");
		SoundAmplitudeArrayManipulator.save("data/sounds/testingSourceDataLine.wav", testArray);
		
		//double array containing frequencies of a C Major scale
		double[] frequencies = new double[]{261.626, 293.665, 329.628, 349.228, 391.995, 440.0, 493.883, 523.251};
		for(int i = 0; i < frequencies.length; i++) {
			double[] scaleCPPN = SoundAmplitudeArrayManipulator.amplitudeGenerator(cppn, 30000, frequencies[i]);
			//MiscSoundUtil.playDoubleArray(scaleCPPN);
		}
	}

	public static void saveFileTests() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		//testing writeSingleChannel to see if the problem saving files has to do with initialization of AudioFormat
		File harp = new File(HARP_WAV);
		AudioInputStream harpAIS = WAVUtil.audioStream(harp);
		System.out.println("harp: " + harpAIS.getFormat());
		System.out.println(harpAIS.getFormat().getSampleRate());
		System.out.println(harpAIS.getFormat().getFrameRate());
		System.out.println(harpAIS.getFormat().isBigEndian());
		System.out.println();
		
		File bear = new File(BEARGROWL_WAV);
		AudioInputStream bearAIS = WAVUtil.audioStream(bear);
		System.out.println("bear: " + bearAIS.getFormat());
		System.out.println(bearAIS.getFormat().getSampleRate());
		System.out.println(bearAIS.getFormat().getFrameRate());
		System.out.println(bearAIS.getFormat().isBigEndian());
		System.out.println();
		
		
		File applause = new File(APPLAUSE_WAV);
		AudioInputStream applauseAIS = WAVUtil.audioStream(applause);
		System.out.println("applause: " + applauseAIS.getFormat());
		System.out.println(applauseAIS.getFormat().getSampleRate());
		System.out.println(applauseAIS.getFormat().getFrameRate());
		System.out.println(applauseAIS.getFormat().isBigEndian());
		System.out.println();
		
		
		//double[] harpDoubleArray = MiscSoundUtil.read(HARP_WAV);
		//MiscSoundUtil.playDoubleArray(harpDoubleArray);
		//System.out.println(Arrays.toString(harpDoubleArray));
		//WAVUtil.playWAVFile(harp);
		//SoundAmplitudeArrayManipulator.writeSingleChannel(harpAIS.getFormat(), harpDoubleArray, "harpDoubleArray.wav");
	}

	public static void plotExamples() {

		//ArrayList<Double> fileArrayList2 = ArrayUtil.doubleVectorFromArray(testArray2); //convert array into array list
		DrawingPanel panel2 = new DrawingPanel(500,500, "2"); //create panel where line will be plotted 
		//GraphicsUtil.linePlot(panel2, -1.0, 1.0, fileArrayList2, Color.black); //call linePlot with ArrayList to draw graph
		MiscUtil.waitForReadStringAndEnterKeyPress();

	}

	public static void playFileExamples() throws IOException, UnsupportedAudioFileException {

		//playWAVFile(BEARGROWL_WAV);
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		//System.out.println(Arrays.toString(bearNumbers));
		AudioInputStream bearAIS = WAVUtil.byteToAIS(bearNumbers);
		int[] bearData = SoundAmplitudeArrayManipulator.extractAmplitudeDataFromAudioInputStream(bearAIS);
		//System.out.println(Arrays.toString(bearData));

		byte[] applauseNumbers =WAVUtil.WAVToByte(APPLAUSE_WAV);	
		//System.out.println(Arrays.toString(applauseNumbers));

		byte[] harpNumbers = WAVUtil.WAVToByte(HARP_WAV);
		//System.out.println(Arrays.toString(harpNumbers));
		AudioInputStream harpAIS = WAVUtil.byteToAIS(harpNumbers);
		int[] harpData = SoundAmplitudeArrayManipulator.extractAmplitudeDataFromAudioInputStream(harpAIS);
		System.out.println(Arrays.toString(harpData));

		//System.out.println(stream);

		//		for(int i = 0; i < Math.max(bearNumbers.length, applauseNumbers.length); i++) {
		//			if(i < bearNumbers.length) System.out.print(bearNumbers[i]);
		//			System.out.print("\t");
		//			if(i < applauseNumbers.length) System.out.print(applauseNumbers[i]);
		//			System.out.println();
		//			
		//			new Scanner(System.in).nextLine();
		//		}	

		//FileOutputStream bearReturns = byteToWAV(BEARGROWL_WAV, bearNumbers);
		//playWAVFile(bearReturns);

		//playByteAIS(applauseNumbers);
	}

	public static void manipulationExamples() throws IOException, UnsupportedAudioFileException {
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		byte[] applauseNumbers =WAVUtil.WAVToByte(APPLAUSE_WAV);	
		byte[] harpNumbers = WAVUtil.WAVToByte(HARP_WAV);
		AudioInputStream applauseAIS = WAVUtil.byteToAIS(applauseNumbers);
		int[] applauseNumbers2 = SoundAmplitudeArrayManipulator.extractAmplitudeDataFromAudioInputStream(applauseAIS);
		//System.out.println(Arrays.toString(applauseNumbers2));



		//		for(int i = bearNumbers.length-11; i <= bearNumbers.length-1; i++) {
		//			System.out.print(bearNumbers[i] + " ");
		//		}
		//		System.out.println();

		double[] splice = new double[applauseNumbers.length];
		//		for(int i = 0; i < splice.length; i++) {
		//			if(i < 46)
		//				splice[i] = bearNumbers[i];
		//			else 
		//				splice[i] = applauseNumbers[i];
		//		}
		//		StdAudio.play(splice);

		double[] bear = new double[bearNumbers.length];
		for(int i = 0; i < bear.length; i++) {
			if(i < 50120 || i >= bearNumbers.length-11) 
				bear[i] = bearNumbers[i];
			else 
				bear[i] = 20;
		}
		bear[bear.length-1] = 0;
		//MiscSoundUtil.play(bear);

		//		double[] applause = new double[bearNumbers.length];
		//		for(int i = 0; i < original.length; i++) {
		//			if(i < 50120 || i >= bearNumbers.length-11) 
		//				original[i] = bearNumbers[i];
		//			else 
		//				original[i] = 20;
		//		}
		//		original[original.length-1] = 0;
		//		StdAudio.play(original);
		//		
		//		double[] original = new double[bearNumbers.length];
		//		for(int i = 0; i < original.length; i++) {
		//			if(i < 50120 || i >= bearNumbers.length-11) 
		//				original[i] = bearNumbers[i];
		//			else 
		//				original[i] = 20;
		//		}
		//		original[original.length-1] = 0;
		//		StdAudio.play(original);

		//		StdAudio.wavePlot(BEARGROWL_WAV);
		//		StdAudio.wavePlot(APPLAUSE_WAV);
		//		StdAudio.wavePlot(HARP_WAV);

		System.out.println("bear growl: " + bearNumbers.length);
		System.out.println("applause: " + applauseNumbers.length);
		System.out.println("harp: " + harpNumbers.length);
		double[] applauseAndHarp = ArrayUtil.overlap(APPLAUSE_WAV, HARP_WAV);
		System.out.println("applauseAndHarp length: " + applauseAndHarp.length);
		double[] bearGrowlAndHarp = ArrayUtil.overlap(BEARGROWL_WAV, HARP_WAV);
		System.out.println("bearGrowlAndHarp length: " + bearGrowlAndHarp.length);
		double[] applauseAndBearGrowl = ArrayUtil.overlap(APPLAUSE_WAV, BEARGROWL_WAV);
		System.out.println("applauseAndBearGrowl length: " + applauseAndBearGrowl.length);

		//		StdAudio.play(applauseAndHarp);
		//		StdAudio.play(bearGrowlAndHarp);
		//		StdAudio.play(applauseAndBearGrowl);
	}

	//MiscSoundUtil.play(PIRATES);
	public static void toneGeneratorExamples() {
		// 440 Hz for 1 sec
		double freq1 = 440.0;
		for (int i = 0; i <= MiscSoundUtil.SAMPLE_RATE; i++) {
			//StdAudio.play(0.5 * Math.sin(2*Math.PI * freq1 * i / StdAudio.SAMPLE_RATE));
		}

		for (int i = 0; i <= MiscSoundUtil.SAMPLE_RATE; i++) {
			//StdAudio.play(0.5 * ActivationFunctions.squareWave(2*Math.PI * freq1 * i / StdAudio.SAMPLE_RATE));
		}

		for (int i = 0; i <= MiscSoundUtil.SAMPLE_RATE; i++) {
			//StdAudio.play(0.5 * ActivationFunctions.triangleWave(2*Math.PI * freq1 * i / StdAudio.SAMPLE_RATE));
		}


		//			// scale increments
		//			int[] steps = { 0, 2, 4, 5, 7, 9, 11, 12 };
		//			for (int i = 0; i < steps.length; i++) {
		//				double hz = 440.0 * Math.pow(2, steps[i] / 12.0);
		//				StdAudio.play(note(hz, 1.0, 0.5));
		//			}

		double[] exampleSound = new double[MiscSoundUtil.SAMPLE_RATE+1];
		double freq2 = 440.0;
		for (int i = 0; i <= MiscSoundUtil.SAMPLE_RATE; i++) {
			exampleSound[i] = (0.5 * Math.sin(2*Math.PI * freq2 * i / MiscSoundUtil.SAMPLE_RATE));
		}
		//StdAudio.play(exampleSound);

		for (int i = 0; i <= MiscSoundUtil.SAMPLE_RATE; i++) {
			exampleSound[i] = (0.5 * ActivationFunctions.fullSawtooth(2*Math.PI * freq2 * i / MiscSoundUtil.SAMPLE_RATE));
		}
		//StdAudio.play(exampleSound);

		double[] thing = new double[500];
		for(int i = 0; i < thing.length; i++) {
			thing[i] = i;
		}
		//StdAudio.play(thing);


		for (double i = 0; i <= MiscSoundUtil.SAMPLE_RATE; i++) {
			exampleSound[(int)i] = (ActivationFunctions.tanh( 2*(i/MiscSoundUtil.SAMPLE_RATE) - 1 ));
		}
		//StdAudio.play(exampleSound);

		//String pirates= "data/sounds/pirates.mid";
		//play(pirates);
		//playApplet(pirates);
		String classical = "data/sounds/CLASSICA.MID";
		//play(classical);
		//StdAudio.playApplet(classical); //TODO: failure here?

	}
	// need to call this in non-interactive stuff so the program doesn't terminate
	// until all the sound leaves the speaker.


	public static void saveFileFromArrayExamples() throws IOException {	
		byte[] bearNumbers = WAVUtil.WAVToByte(BEARGROWL_WAV);
		SoundAmplitudeArrayManipulator.saveFileFromArray(bearNumbers, "data/sounds/bearGrowlCopy.wav");

		File happyFile = new File(HAPPY_MP3);
		//		AudioInputStream happyStream = WAVUtil.audioStream(happyFile);
		//		byte[] happyNumbers = SoundAmplitudeArrayManipulator.extractAmplitudeByteArrayFromAudioInputStream(happyStream);

		//		SoundAmplitudeArrayManipulator.saveFileFromArray(happyNumbers, "data/sounds/happyCopy.mp3");
		String classical = "data/sounds/CLASSICA.MID";
		byte[] classicalNumbers = MiscSoundUtil.readByte(classical);
		SoundAmplitudeArrayManipulator.saveFileFromArray(classicalNumbers, "data/sounds/classicalCopy.mid");
		MiscSoundUtil.close(); 
	}
	
	public static void MIDITests() {
//		File classicalFile = new File(CLASSICAL_MID);
//		MiscSoundUtil.MIDIData(classicalFile);
		
//		File piratesFile = new File(PIRATES);
//		MiscSoundUtil.MIDIData(piratesFile);
		
//		File soloPiano = new File(SOLO_PIANO_MID);
//		MIDIUtil.MIDIData(soloPiano);
		
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false"});
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype test = new HyperNEATCPPNGenotype(3, 1, 0);
		for(int i = 0; i < 30; i++) {
			test.mutate();
		}
		Network cppn = test.getCPPN();
//		double[] furEliseFreq = MIDIUtil.freqFromMIDI(FUR_ELISE_MID);
//		MIDIUtil.playMIDIWithCPPN(cppn, furEliseFreq);
		
		
		double[] classicalFreq = MIDIUtil.freqFromMIDI(CLASSICAL_MID);
		MIDIUtil.playMIDIWithCPPN(cppn, classicalFreq);
	}
}
