package edu.utexas.cs.nn.util.sound;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.interactive.InteractiveEvolutionTask;
import edu.utexas.cs.nn.tasks.interactive.breedesizer.Keyboard;

/**
 * Series of utility methods that read data from MIDI files and convert it into frequencies
 * that can be used by a CPPN. 
 * 
 * @author Isabel Tweraser
 *
 */
public class MIDIUtil {
	
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	//Representative frequencies for octave 1
	public static final double C1 = 32.70;
	public static final double CSHARP1 = 34.65;
	public static final double D1 = 36.71;
	public static final double DSHARP1 = 38.89;
	public static final double E1 = 41.20;
	public static final double F1 = 43.65;
	public static final double FSHARP1 = 46.25;
	public static final double G1 = 49.00;
	public static final double GSHARP1 = 51.91;
	public static final double A1 = 55.00;
	public static final double ASHARP1 = 58.27;
	public static final double B1 = 61.74;
	
	public static final double[] NOTES = new double[]{C1, CSHARP1, D1, DSHARP1, E1, F1, FSHARP1, G1, GSHARP1, A1, ASHARP1, B1};
	
	public static final int NOTES_IN_OCTAVE = 12; //number of chromatic notes in a single octave
	
	/**
	 * Method that takes in a MIDI file and prints out useful information about the note, whether the 
	 * note is on or off, the key, and the velocity. This is printed for each individual track in the 
	 * MIDI file.
	 * 
	 * @param audioFile input MIDI file
	 */
	public static void MIDIData(File audioFile) {
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
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
						//System.out.print("Channel: " + sm.getChannel() + " ");
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
						System.out.println("Other message: " + message.getClass());
					}
				}

				System.out.println();
			}
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}		

	}
	
	/**
	 * Method that takes an input string representation of a MIDI file and loops through
	 * the individual tracks of the file, converting each one to its equivalent 
	 * frequency by calling noteToFreq(). 
	 * 
	 * @param audio string representation of MIDI file
	 * @return double array of all frequencies of tracks in order
	 */
	public static double[] freqFromMIDI(String audio){
		double[] frequencies = new double[10000];
		File audioFile = new File(audio);
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
			int trackNumber = 0;
			for (Track track :  sequence.getTracks()) {
				trackNumber++;
				for (int i=0; i < track.size(); i++) { 
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						if (sm.getCommand() == NOTE_ON) {
						int key = sm.getData1();
						frequencies[i] = noteToFreq(key);
						}
					}
				}
			}
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}	
		return frequencies;
	}
	
	/**
	 * Takes an input note value from a MIDI file and converts it to its corresponding frequency.
	 * 
	 * @param key Input integer taken from MIDI file that encodes the note and octave
	 * @return Frequency of input MIDI note
	 */
	public static double noteToFreq(int key) {
		int note = key % NOTES_IN_OCTAVE;
		int octave = (key / NOTES_IN_OCTAVE) -1;
		return NOTES[note] * Math.pow(2.0, (double) octave - 1.0); //this is because frequencies of notes are always double the frequencies of the lower adjacent octave
	}
	
	/**
	 * Loops through array of frequencies generated from a MIDI file and plays it using a CPPN,
	 * essentially making the CPPN the "instrument". 
	 * 
	 * @param cppn input network used to generate sound
	 * @param frequencies frequencies corresponding to data taken from MIDI file
	 */
	public static void playMIDIWithCPPN(Network cppn, double[] frequencies) {
		System.out.println(Arrays.toString(frequencies)); // Something strange about these frequencies
		for(int i = 0; i < frequencies.length; i++) {
			double[] amplitude = SoundAmplitudeArrayManipulator.amplitudeGenerator(cppn, Keyboard.NOTE_LENGTH_DEFAULT, frequencies[i]);
			System.out.println("note "+ i + " :" + Arrays.toString(amplitude));
			MiscSoundUtil.playDoubleArray(amplitude, false);
		}
	}
}
