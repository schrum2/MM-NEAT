package edu.utexas.cs.nn.util.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MIDIUtil {
	
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	
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
	public static final int NOTES_IN_OCTAVE = 12;
	
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
	 * Takes an input note value from a MIDI file and converts it to its corresponding frequency.
	 * 
	 * @param key Input integer taken from MIDI file that encodes the note and octave
	 * @return Frequency of input MIDI note
	 */
	public static double noteToFreq(int key) {
		int note = key%12;
		int octave = (key/12) -1;
		//String noteName = NOTE_NAMES[note];
		return NOTES[note] * Math.pow(2.0, (double) octave - 1.0);
		
//		if(octave == 1) {
//			switch(noteName) {
//			case "C": return NOTES[0];
//			case "C#": return NOTES[1];
//			case "D": return NOTES[2];
//			case "D#": return NOTES[3];
//			case "E": return NOTES[4];
//			case "F": return NOTES[5];
//			case "F#": return NOTES[6];
//			case "G": return NOTES[7];
//			case "G#": return NOTES[8];
//			case "A": return NOTES[9];
//			case "A#": return NOTES[10];
//			case "B": return NOTES[11];
//			}
//		} else if(octave == 2) {
//			switch(noteName) {
//			case "C": return NOTES[0] * 2.0;
//			case "C#": return NOTES[1] * 2.0;
//			case "D": return NOTES[2] * 2.0;
//			case "D#": return NOTES[3] * 2.0;
//			case "E": return NOTES[4] * 2.0;
//			case "F": return NOTES[5] * 2.0;
//			case "F#": return NOTES[6] * 2.0;
//			case "G": return NOTES[7] * 2.0;
//			case "G#": return NOTES[8] * 2.0;
//			case "A": return NOTES[9] * 2.0;
//			case "A#": return NOTES[10] * 2.0;
//			case "B": return NOTES[11] * 2.0;
//			}
//		} else if(octave == 3) {
//			switch(noteName) {
//			case "C": return NOTES[0] * 4.0;
//			case "C#": return NOTES[1] * 4.0;
//			case "D": return NOTES[2] * 4.0;
//			case "D#": return NOTES[3] * 4.0;
//			case "E": return NOTES[4] * 4.0;
//			case "F": return NOTES[5] * 4.0;
//			case "F#": return NOTES[6] * 4.0;
//			case "G": return NOTES[7] * 4.0;
//			case "G#": return NOTES[8] * 4.0;
//			case "A": return NOTES[9] * 4.0;
//			case "A#": return NOTES[10] * 4.0;
//			case "B": return NOTES[11] * 4.0;
//			}
//		} else if(octave == 4) {
//			switch(noteName) {
//			case "C": return NOTES[0] * 8.0;
//			case "C#": return NOTES[1] * 8.0;
//			case "D": return NOTES[2] * 8.0;
//			case "D#": return NOTES[3] * 8.0;
//			case "E": return NOTES[4] * 8.0;
//			case "F": return NOTES[5] * 8.0;
//			case "F#": return NOTES[6] * 8.0;
//			case "G": return NOTES[7] * 8.0;
//			case "G#": return NOTES[8] * 8.0;
//			case "A": return NOTES[9] * 8.0;
//			case "A#": return NOTES[10] * 8.0;
//			case "B": return NOTES[11] * 8.0;
//			}
//		} else if(octave == 5) {
//			switch(noteName) {
//			case "C": return NOTES[0] * 16.0;
//			case "C#": return NOTES[1] * 16.0;
//			case "D": return NOTES[2] * 16.0;
//			case "D#": return NOTES[3] * 16.0;
//			case "E": return NOTES[4] * 16.0;
//			case "F": return NOTES[5] * 16.0;
//			case "F#": return NOTES[6] * 16.0;
//			case "G": return NOTES[7] * 16.0;
//			case "G#": return NOTES[8] * 16.0;
//			case "A": return NOTES[9] * 16.0;
//			case "A#": return NOTES[10] * 16.0;
//			case "B": return NOTES[11] * 16.0;
//			}
//		} else if(octave == 6) {
//			switch(noteName) {
//			case "C": return NOTES[0] * 32.0;
//			case "C#": return NOTES[1] * 32.0;
//			case "D": return NOTES[2] * 32.0;
//			case "D#": return NOTES[3] * 32.0;
//			case "E": return NOTES[4] * 32.0;
//			case "F": return NOTES[5] * 32.0;
//			case "F#": return NOTES[6] * 32.0;
//			case "G": return NOTES[7] * 32.0;
//			case "G#": return NOTES[8] * 32.0;
//			case "A": return NOTES[9] * 32.0;
//			case "A#": return NOTES[10] * 32.0;
//			case "B": return NOTES[11] * 32.0;
//			}
//		} else if(octave == 7) {
//			switch(noteName) {
//			case "C": return NOTES[0] * 64.0;
//			case "C#": return NOTES[1] * 64.0;
//			case "D": return NOTES[2] * 64.0;
//			case "D#": return NOTES[3] * 64.0;
//			case "E": return NOTES[4] * 64.0;
//			case "F": return NOTES[5] * 64.0;
//			case "F#": return NOTES[6] * 64.0;
//			case "G": return NOTES[7] * 64.0;
//			case "G#": return NOTES[8] * 64.0;
//			case "A": return NOTES[9] * 64.0;
//			case "A#": return NOTES[10] * 64.0;
//			case "B": return NOTES[11] * 64.0;
//			}
//		}
//		return -1; //fail
	}
}
