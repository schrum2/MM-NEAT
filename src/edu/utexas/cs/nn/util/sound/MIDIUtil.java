package edu.utexas.cs.nn.util.sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

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

	// representative frequencies for octave 1 read into double array so that they 
	// can be manipulated based on their index in noteToFreq()
	public static final double[] NOTES = new double[]{C1, CSHARP1, D1, DSHARP1, E1, F1, FSHARP1, G1, GSHARP1, A1, ASHARP1, B1};

	public static final int NOTES_IN_OCTAVE = 12; //number of chromatic notes in a single octave

	public static final int BPM = 120; //beats per minute - should be generalized
	public static final int PPQ = 96; //parts per quarter note - should be generalized
	
	public static final int CLIP_VOLUME_LENGTH = 4000;

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
			int trackNumber = 0;
			for (Track track :  sequence.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				System.out.println();
				//MiscUtil.waitForReadStringAndEnterKeyPress();
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
	 * Method that takes in a string reference to a MIDI file and the index of a specified track 
	 * and saves the values of its frequencies and note lengths sequentially  in a pair of 
	 * double arrays. Method does so by calling creating a File out of the string reference and
	 * then calling other freqFromMidi() method.
	 * 
	 * @param audio string reference to MIDI file being analyzed
	 * @param trackNum index of specific track being analyzed 
	 * @return Pair of double arrays with the frequencies and note lengths saved
	 */
	public static Pair<double[],double[]> freqFromMIDI(String audio, int trackNum){
		File audioFile = new File(audio);
		return freqFromMIDI(audioFile, trackNum);
	}	

	/**
	 * Method that keeps track of the total number of notes in a track.
	 * This allows freqFromMIDI() to avoid events in the track that are not
	 * related to notes.
	 * 
	 * @param t Track being analyzed
	 * @return total number of notes in track
	 */
	public static int countNotes(Track t) {
		int total = 0;
		for(int i = 0; i < t.size(); i++) {
			MidiEvent event = t.get(i);
			MidiMessage message = event.getMessage();
			//System.out.println(message);
			if (message instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) message;
				if (sm.getCommand() == NOTE_ON && sm.getData2() > 0) {
					total++;
				}
			}
		}
		return total;
	}

	/**
	 * Returns number of ticks in track
	 * @param track Track from MIDI being analyzed
	 * @return maximum ticks in track
	 */
	public static long ticksInTrack(Track track) {
		assert track.ticks() <= Integer.MAX_VALUE : "Cannot handle this many ticks in a track: " +track.ticks();
		return track.ticks();
	}

	// TODO: Since we'll be keeping this method after all, comment it.
	public static ArrayList<double[]> soundLines(Track[] tracks) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		for(Track t: tracks) {
			result.addAll(soundLines(t));
		}
		return result;
	}
	
	/**
	 * Divides each piano voice up into a single list, so that all indexes when voice is not playing
	 * are filled with a 0 and indexes when the voice is playing are filled with the frequency. This
	 * is done so that each sound can be fed into a separate SourceDataLine and the double arrays can 
	 * potentially be played simultaneously.
	 * 
	 * @param track input track of MIDI file being analyzed (track represents a single instrument, usually)
	 * @return List of representative double arrays for each voice (number of arrays in list should be 
	 * equal to the max number of notes played at once on the given instrument)
	 */
	public static ArrayList<double[]> soundLines(Track track) {
		Map<Double, Long> map = new HashMap<Double, Long>();
		// TODO: Don't even create this ArrayList in the first place because it takes up
		// tons of memory. Rather, create one double array that has one index for each tick,
		// and add each sound result you encounter to the appropriate tick/index.
		// Accomplishing this will require a method that first determines which track across
		// the whole file has the most ticks, so that you can initailize the array to the right size.
		// ALSO: after looking at the code some more, I realized that the values returned here are
		// frequencies, not amplitudes. The trick of adding only works with amplitudes, but we can
		// only derive amplitudes after using the CPPN. That means we need to extract the sound
		// info and encode with the CPPN at the same time in order to increase efficiency and reduce
		// the memory footprint.
		ArrayList<double[]> soundLines = new ArrayList<double[]>();
		HashMap<Double, Integer> lines = new HashMap<Double, Integer>();
		for(int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);
			MidiMessage message = event.getMessage();
			// TODO: I wonder if we should start investigating other types of messages.
			// Some nuance about the sounds produces could depend on interpreting the
			// other messages correctly.
			if (message instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) message;
				int key = sm.getData1();
				double freq = noteToFreq(key);
				long tick = event.getTick(); // actually starting tick time
				if (sm.getCommand() == NOTE_ON && sm.getData2() > 0) { //turn on
					int index = map.size();
					// TODO: Remove once the ArrayList is gone
					if(index >= soundLines.size()) {
						soundLines.add(new double[(int) ticksInTrack(track)]);
					}
					map.put(freq, tick);
					lines.put(freq, index);
				} else if((sm.getCommand() == NOTE_OFF || (sm.getCommand() == NOTE_ON && sm.getData2() == 0))) { // Check: is negative velocity possible?
					int index = lines.get(freq);
					long tickStart = map.get(freq);
					long tickEnd = tick;
					
					// TODO: Rather than what happens here, you would simply add to the one universal array
					double[] lineArray = soundLines.get(index);
					for(long j = tickStart; j <= tickEnd; j++) {
						lineArray[(int) j] = freq;
					}
					lines.remove(freq);
					map.remove(freq);
				}
			}
		}
		// TODO: Some normalization (division) might be needed here before the final return.
		
		return soundLines;
	}
	
	/**
	 * Takes in a double array representing a single voice in a track and extracts vital information
	 * out of it so that the individual notes are retained sequentially in an ArrayList and the lengths
	 * of those notes are also retained in a separate ArrayList. 
	 * 
	 * @param soundLine double array representing single voice in track
	 * @return pair of Array Lists containing the frequencies and lengths of the notes in a single sound line
	 */
	public static Pair<ArrayList<Double>, ArrayList<Double>> notesAndLengthsOfLine(double[] soundLine) {
		ArrayList<Double> frequencies = new ArrayList<Double>();
		ArrayList<Double> lengths = new ArrayList<Double>(); // Needs to be double?

		// TODO: The length calculations here look suspiscious to me.
		// I wonder if they have problems for the first freq value and last
		// freq values in the array. Could use more analysis.
		int prevIndex = 0;
		double prevVal = soundLine[0];
		for(int i = 0; i < soundLine.length; i++) {
			if(soundLine[i] != prevVal) {
				frequencies.add(prevVal);
				lengths.add((double) (i - prevIndex) + 1); 
				prevVal = soundLine[i];
				prevIndex = i;
			}
		}
		frequencies.add(prevVal);
		lengths.add((double) soundLine.length-prevIndex);
		return new Pair<>(frequencies, lengths);
	}

	/**
	 * Method that takes in an audio file and the index of a specified track
	 * and saves the values of its frequencies and note lengths sequentially
	 * in a pair of double arrays. 
	 * 
	 * @param audioFile MIDI file being analyzed
	 * @param trackNum index of specific track being analyzed 
	 * @return Pair of double arrays with the frequencies and note lengths saved
	 */
	public static Pair<double[], double[]> freqFromMIDI(File audioFile, int trackNum) {
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
			Track[] tracks = sequence.getTracks();
			Track track = tracks[trackNum];
			int numNotes = countNotes(track);

			double[] frequencies = new double[numNotes];
			long[] starts = new long[numNotes]; 
			double[] lengths = new double[numNotes]; // Needs to be double?

			int j = 0;
			for(int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				//System.out.println(message);
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;

					int key = sm.getData1();
					double freq = noteToFreq(key);
					long tick = event.getTick(); // actually starting tick time

					if (sm.getCommand() == NOTE_ON && sm.getData2() > 0) {
						frequencies[j] = freq;
						starts[j] = tick; // actually starting tick time
						j++;
					} 

					if(j > 0) { // Not first note
						// May not be turning off the right note
						//lengths[j - 1] = convertTicksToMilliseconds(tick - starts[j-1]);
						lengths[j-1] = convertTicksToMilliseconds(tick - starts[j-1]);
					}

					// This was looking for the stop time of the particular note, but did not account for note overlap
					//					} else if(sm.getCommand() == NOTE_OFF || (sm.getCommand() == NOTE_ON && sm.getData2() == 0)) {
					//						int key = sm.getData1();
					//						int indexInLengths = noteIndex.get(key);
					//						lengths[indexInLengths] = convertTicksToMilliseconds(event.getTick() - starts[indexInLengths]); // actually starting time
					//						//lengths[indexInLengths] = (event.getTick() - starts[indexInLengths]); // actually starting time
					//					}
				}
			}
			return new Pair<>(frequencies,lengths);
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}	
		return null; // never reached
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
		return NOTES[note] * Math.pow(2.0, (double) octave - 1.0); // this is because frequencies of notes are always double the frequencies of the lower adjacent octave
	}

	/**
	 * NEEDS TO BE REWORKED
	 * Takes in the time representation for note duration in MIDI files, which is referred to as
	 * ticks, and manipulates it according to the BPM (beats per minute) and PPQ (parts per 
	 * quarter note) to return the equivalent time in milliseconds. 
	 * 
	 * @param ticks Length of events in MIDI files
	 * @return Equivalent length in milliseconds
	 */
	
	// TODO: Make this method correct, and then actually use it (or something like it) in place
	// of that weird magic number 50.
	public static float convertTicksToMilliseconds(long ticks) {
		float millisecondsPerTick = 60000 / (PPQ * BPM);
		float result = ticks * millisecondsPerTick;
		//System.out.println(result);
		return result * 10; // We have no idea why the number 10 goes here, but it seems to scale things nicely
	}

	/**
	 * Plays sound using Applet.newAudioClip() - works for MIDI files
	 * 
	 * @param filename string reference to audio file being played
	 */
	public static void playApplet(String filename) {
		URL url = null;
		try {
			File file = new File(filename);
			if(file.canRead()) url = file.toURI().toURL();
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException("could not play '" + filename + "'", e);
		}
		// URL url = StdAudio.class.getResource(filename);
		if (url == null) {
			throw new IllegalArgumentException("could not play '" + filename + "'");
		}
		AudioClip clip = Applet.newAudioClip(url);
		clip.play();
	}
	
	// TODO: Needs comments
	public static double[] lineToAmplitudeArray(double[] soundLine, Network cppn) {
		Pair<ArrayList<Double>, ArrayList<Double>> lineData = notesAndLengthsOfLine(soundLine);
		double[] frequencies = ArrayUtil.doubleArrayFromList(lineData.t1);
		double[] lengths = ArrayUtil.doubleArrayFromList(lineData.t2);
		return lineToAmplitudeArray(frequencies, lengths, cppn);
		
	}

	// TODO: Needs comments
	// TODO: I wonder if some of this code needs to be moved into soundLines as well.
	// I forgot about the Pair<freq,lengths> step, which makes me reconsider some of my
	// previous TODO statements ... do as much computation up front as possible.
	public static double[] lineToAmplitudeArray(double[] frequencies, double[] lengths, Network cppn) {
	
		// TODO: We really need to find out what the deal with this number is.
		// In particular, I suspect that part of the reason that other MIDI files failed
		// is this number ... maybe it only works for fur Elise. We should try to properly
		// calculate this value, or at least try different values with different MIDI files
		// to get a feel for the result.
		// VERY MAGIC NUMBER! NO IDEA WHY THIS NUMBER WORKS!
		int amplitudeLengthMultiplier = 50;

		double[] amplitudeArray = new double[(int) StatisticsUtilities.sum(lengths)*amplitudeLengthMultiplier];
		int noteLength = 0;
		for(int i = 0; i < lengths.length; i++) {
			int amplitudeLength = (int)(lengths[i] * amplitudeLengthMultiplier);
			double[] amplitude = frequencies[i] == 0 ? new double[amplitudeLength] : SoundFromCPPNUtil.amplitudeGenerator(cppn, amplitudeLength, frequencies[i]);
			System.arraycopy(amplitude, 0, amplitudeArray, noteLength, amplitude.length);
			noteLength += amplitudeLength;
		}
		return amplitudeArray;
	}

	/**
	 * Loops through array of frequencies generated from a MIDI file and plays it using a CPPN,
	 * essentially making the CPPN the "instrument". 
	 * 
	 * CURRENT ISSUES: Cannot play more than one note at a time (chords are impossible), can't 
	 * handle MIDI files that have multiple tracks containing melodic content. Also, the conversion 
	 * from ticks to milliseconds is a little bit off because I don't know exactly what to initialize
	 * BPM and PPQ to and how to extract that from individual MIDI files in a general way. 
	 * 
	 * @param cppn input network used to generate sound
	 * @param frequencies frequencies corresponding to data taken from MIDI file
	 * @param lengths double array containing lengths of individual notes
	 */
	public static CPPNNoteSequencePlayer playMIDIWithCPPNFromDoubleArray(Network cppn, ArrayList<double[]> soundLines) {
		CPPNNoteSequencePlayer result = new CPPNNoteSequencePlayer(cppn, soundLines);
		result.start();
		return result; // To allow for interrupting of playback
	}

	/**
	 * Class starts playback of a note sequence in its own Thread, but can be interrupted
	 * @author Jacob Schrum
	 *
	 */
	public static class CPPNNoteSequencePlayer extends Thread {
		boolean play;
		// TODO: Replace this variable with a single double array
		private double[][] amplitudeArrays;

		public CPPNNoteSequencePlayer() {
			// Without any content to play, playing cannot occur
			play = false; 
		}

		//TODO: Rather than pass in the ArrayList of double arrays, a single double array would be passed in.
		// This single double array would already contain the values in the toPlay array that is computed in run().
		public CPPNNoteSequencePlayer(Network cppn, ArrayList<double[]> soundLines) {
			play = true;
			amplitudeArrays = new double[soundLines.size()][];
			for(int i = 0; i < soundLines.size(); i++) {
				amplitudeArrays[i] = lineToAmplitudeArray(soundLines.get(i), cppn);
			}
		}
		
		public void run() {
			// TODO: Allow for interruption. Now that only a single array is used,
			// this should be easy. The playDoubleArray method returns an AmplitudeArrayPlayer
			// that has a method capable of stopping playback. Save the AmplitudeArrayPlayer
			// in a global variable (of CPPNNoteSequencePlayer) and shut down the AmplitudeArrayPlayer
			// when the stopPlayback method of the CPPNNoteSequencePlayer is called.
			
			// TODO: The code below would essentially be moved into the soundLines method,
			// or other helper methods that are used in the soundLines method. However, some of
			// these operations are so general that they may be (or should be) util methods in 
			// ArrayUtil and/or StatisticsUtilities
			int max = 0;
			for(int i = 0; i < amplitudeArrays.length; i++) {
				max = Math.max(max, amplitudeArrays[i].length);
			}
			double maxNote = 0;
			double[] toPlay = new double[max];
			for(int i = 0; i < amplitudeArrays.length; i++) {
				for(int j = 0; j < amplitudeArrays[i].length; j++) {
					maxNote = Math.max(maxNote, Math.abs(amplitudeArrays[i][j]));
					toPlay[j] += amplitudeArrays[i][j];
				}
			}
			//System.out.println(maxNote);
			//if(maxNote >= 1) { // normalize for excessive volume
				for(int i = 0; i < toPlay.length; i++) {
					//toPlay[i] /= maxNote;
					toPlay[i] /= 2.0;
					// Schrum: dividing by 2 seems to produce the least scratchy outcome when
					// playing fur Elise. I originally attempted this because it was recommended
					// for combining 2 sound sources. However, in retrospect, It is confusing that
					// this works for our sounds because there are more than 2 sources. However,
					// most of them are empty at any given time. It could be that there are usually
					// no more than two sources with actual volume at the same time. This parameter
					// is something we need to pay attention to.
				}
			//}
			PlayDoubleArray.playDoubleArray(toPlay);
			
		}

		public void stopPlayback() {
			play = false;
		}

		public boolean isPlaying() {
			return play;
		}
	}

	/**
	 * Loops through array of frequencies generated from a MIDI file and plays it using a CPPN,
	 * essentially making the CPPN the "instrument". Does so by calling freqFromMIDI to generate
	 * a pair of double arrays corresponding to the frequencies and durations of all notes in a 
	 * specific track, and then calls playMIDIWithCPPNFromDoubleArray() with the double arrays.
	 * 
	 * @param audio string representation of MIDI file being analyzed
	 * @param trackNumber specific track in file from which data is being extracted
	 * @param cppn Input network being used as the "instrument" to generate MIDI file playback
	 */
	public static CPPNNoteSequencePlayer playMIDIWithCPPNFromString(String audio, int trackNumber, Network cppn) {
		File audioFile = new File(audio);
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
			Track[] tracks = sequence.getTracks();
			// TODO: Note that I am already ignoring the track number here,
			// but we should actually have a version of this method that uses
			// a specific track and another version that uses all tracks.
			//Track track = tracks[trackNumber];
			
			// TODO: Instead of having a method that returns this massive ArrayList of separate sound lines,
			// simply return a single double array. Essentially, stop using the ArrayList as a middle format
			// between MIDI and the one array of double that gets played inside of the run method of the
			// note sequence player.
			ArrayList<double[]> data = soundLines(tracks);
			return playMIDIWithCPPNFromDoubleArray(cppn, data);
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
		return null; //shouldn't happen
	}
}
