package edu.southwestern.util.sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import edu.southwestern.networks.Network;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.sound.PlayDoubleArray.AmplitudeArrayPlayer;

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

	public static final double ALM_CONSTANT = 23.0; //constant used to derive correct amplitude length multiplier - not sure why it works

	
	public static void main(String[] args) {
		for(int i = 0; i < NOTES.length; i++) {
			System.out.println(i + ":" +noteToFreq(i));
		}
	}
	
	
	/**
	 * Method that calculates array lists of frequency, length, and start time data for all tracks in an array
	 * of tracks in a MIDI file.
	 * 
	 * @param tracks Representative array of tracks derived from a MIDI file's Sequence
	 * @return array list of all frequency, length, and start time data of a MIDI file
	 */
	public static ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> soundLines(Track[] tracks) {
		ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> result = new ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>>();
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
	public static ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> soundLines(Track track) {
		Map<Double, Long> map = new HashMap<Double, Long>();
		ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> soundLines = new ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>>();
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
					if(index >= soundLines.size()) {
						soundLines.add(new Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>(new ArrayList<Double>(), new ArrayList<Long>(), new ArrayList<Long>()));
					}
					map.put(freq, tick);
					lines.put(freq, index);
				} else if((sm.getCommand() == NOTE_OFF || (sm.getCommand() == NOTE_ON && sm.getData2() == 0))) { // Check: is negative velocity possible?
					if(lines.containsKey(freq)) {
						int index = lines.get(freq);
						long tickStart = map.get(freq);
						long tickEnd = tick;

						soundLines.get(index).t1.add(freq);                // add frequency 
						soundLines.get(index).t2.add(tickEnd-tickStart+1); // add length
						soundLines.get(index).t3.add(tickStart);           // add start time

						lines.remove(freq);
						map.remove(freq);
					}
				}
			}
		}
		return soundLines;
	}

	/**
	 * Takes an input note value from a MIDI file and converts it to its corresponding frequency.
	 * 
	 * @param key Input integer taken from MIDI file that encodes the note and octave
	 * @return Frequency of input MIDI note
	 */
	private static double noteToFreq(int key) {
		int note = key % NOTES_IN_OCTAVE;
		int octave = (key / NOTES_IN_OCTAVE) -1;
		return NOTES[note] * Math.pow(2.0, (double) octave - 1.0); // this is because frequencies of notes are always double the frequencies of the lower adjacent octave
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

	/**
	 * Takes in data of frequencies, lengths, and starting times of an audio file and 
	 * reconstructs the file as a single playable amplitude array that uses a generated
	 * CPPN as the instrument.
	 * 
	 * @param audio Original MIDI file
	 * @param midiLists ArrayList containing ArrayLists with the file's frequencies, lengths, and start times of all notes
	 * @param cppn input CPPN used to play back reconstructed audio
	 * @return playable double array of amplitudes
	 */
	public static double[] lineToAmplitudeArray(String audio, ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> midiLists, Network cppn, double noteLengthScale) {
		double amplitudeLengthMultiplier = getAmplitudeLengthMultiplier(audio); // access value that amplitudes are multiplied by to ensure correct playback speed 
		amplitudeLengthMultiplier *= noteLengthScale; // for JSlider with user feedback regarding playback speed
		amplitudeLengthMultiplier = Math.ceil(amplitudeLengthMultiplier); // To prevent rounding issues with array indexes
		long totalTicks = 0;
		// Need as many ticks as are in the longest line
		for(int i = 0; i < midiLists.size(); i++) {
			long lineTicks = midiLists.get(i).t3.get(midiLists.get(i).t3.size()-1) // last start time, plus
					+ midiLists.get(i).t2.get(midiLists.get(i).t2.size()-1);// last duration
			totalTicks = Math.max(totalTicks, lineTicks);
		}
		//create representative double array of all soundlines
		double[] amplitudeArray = new double[(int) (amplitudeLengthMultiplier*totalTicks)];
		for(int k = 0; k < midiLists.size(); k++) { //loop through all lines
			for(int i = 0; i < midiLists.get(k).t1.size(); i++) { //loop through all frequencies in a line
				int amplitudeLength = (int)(amplitudeLengthMultiplier*midiLists.get(k).t2.get(i));
				double[] amplitude = SoundFromCPPNUtil.amplitudeGenerator(cppn, amplitudeLength, midiLists.get(k).t1.get(i));
				//incorporate note lengths into array after frequencies are added
				int start = (int)(amplitudeLengthMultiplier*midiLists.get(k).t3.get(i));
				for(int j = 0; j < amplitude.length; j++) {
					amplitudeArray[start+j] += amplitude[j];
				}
			}
		}
		//to control volume
		for(int i = 0; i < amplitudeArray.length; i++) {
			amplitudeArray[i] /= midiLists.size(); // divide by total number of voices played at once
		}
		return amplitudeArray;
	}	

	/**
	 * Obtains value all amplitudes are multiplied by so that 
	 * values play back at the correct speed after being manipulated
	 * by a CPPN
	 * 
	 * @param audio input audio file
	 * @return Double value that all indexes in amplitude array are multiplied by
	 */
	public static double getAmplitudeLengthMultiplier(String audio) {
		File audioFile = new File(audio);
		try {
			Sequence sequence = MidiSystem.getSequence(audioFile);
			double alm = ((1.0*sequence.getMicrosecondLength())/sequence.getTickLength())/ALM_CONSTANT;
			return alm;
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return 0; // shouldn't happen
	}

	/**
	 * Loops through array of frequencies generated from a MIDI file and plays it using a CPPN,
	 * essentially making the CPPN the "instrument". Does so by calling freqFromMIDI to generate
	 * a pair of double arrays corresponding to the frequencies and durations of all notes in  
	 * all tracks of a file, and then calls playDoubleArray() with the double arrays.
	 * 
	 * @param audio string representation of MIDI file being analyzed
	 * @param cppn Input network being used as the "instrument" to generate MIDI file playback
	 * @param noteLengthScale Specified playback speed from JSlider on Breedesizer interface
	 */
	public static AmplitudeArrayPlayer playMIDIWithCPPNFromString(String audio, Network cppn, double noteLengthScale) {
		File audioFile = new File(audio);
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
			Track[] tracks = sequence.getTracks();
			ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> sound = soundLines(tracks);
			double[] data = lineToAmplitudeArray(audio, sound, cppn, noteLengthScale);
			return PlayDoubleArray.playDoubleArray(data);
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
		return null; //shouldn't happen
	}

	/**
	 * Loops through array of frequencies generated from a MIDI file and plays it using an array of CPPNS,
	 * essentially making the CPPN the "instrument" for each track. Uses similar code as the original 
	 * playMIDIWithCPPNFromString() method, but calculates double arrays for individual tracks so that they can
	 * be manipulated by unique CPPNs, and then adds all arrays together using zipAdd().
	 * 
	 * @param audio string representation of MIDI file being analyzed
	 * @param cppn Input network being used as the "instrument" to generate MIDI file playback
	 * @param noteLengthScale Specified playback speed from JSlider on Breedesizer interface
	 */
	public static AmplitudeArrayPlayer playMIDIWithCPPNsFromString(String audio, Network[] cppns, double noteLengthScale) {
		File audioFile = new File(audio);
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(audioFile);
			Track[] tracks = sequence.getTracks();
			double[] data = new double[0];
			for(int i = 0; i < tracks.length; i++) {
				ArrayList<Triple<ArrayList<Double>, ArrayList<Long>, ArrayList<Long>>> sound = soundLines(tracks[i]);
				double[] lineData = lineToAmplitudeArray(audio, sound, cppns[i % cppns.length], noteLengthScale);
				data = ArrayUtil.zipAdd(data, lineData, Math.max(data.length, lineData.length)); //add new values to larger double array
			}
			//to control volume
			for(int i = 0; i < data.length; i++) {
				data[i] /= tracks.length; // divide by number of tracks
			}
			return PlayDoubleArray.playDoubleArray(data);
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
		return null; //shouldn't happen
	}
}
