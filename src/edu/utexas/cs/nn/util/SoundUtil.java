package edu.utexas.cs.nn.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class SoundUtil {

	private static final String BEARGROWL_WAV = "data/sounds/bear_growl_y.wav";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		playWAVFile(BEARGROWL_WAV);
	}

	/**
	 * Plays an input audio clip; exits loop when clip has finished playing.
	 * 
	 * @param audioClip clip being played
	 * @throws InterruptedException if a thread is interrupted while waiting/idling
	 */
	public static void playClip(Clip audioClip) throws InterruptedException {
		audioClip.start();     
		do { //use do while to allow for audio clip to start before audioClip.isRunning() is checked
			// wait for the playback to complete
			Thread.sleep(1000);
		} while(audioClip.isRunning());      
	}

	/**
	 * Converts a file into an audio clip. 
	 * 
	 * @param audioFile file to be converted
	 * @return newly converted audio clip
	 * @throws UnsupportedAudioFileException if type of audio file cannot be read/recognized as valid
	 * @throws IOException if an IO operation has failed or been interrupted
	 * @throws LineUnavailableException if line cannot be found
	 */
	public static Clip makeClip(File audioFile) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); //converts file into audio stream
		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		Clip audioClip = (Clip) AudioSystem.getLine(info); //uses necessary formatting to create clip from audio stream
		audioClip.open(audioStream);  
		return audioClip; //returns finalized clip
	}

	/**
	 * Plays an audio file by converting the input file into a clip and subsequently playing through the contents of the clip
	 * 
	 * @param audio input file being played
	 * @throws UnsupportedAudioFileException if type of audio file cannot be read/recognized as valid
	 * @throws IOException if an IO operation has failed or been interrupted
	 * @throws LineUnavailableException if line cannot be found
	 * @throws InterruptedException if a thread is interrupted while waiting/idling
	 */
	public static void playWAVFile(String audio) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		File audioFile = new File(audio);
		playWAVFile(audioFile);
	}

	/**
	 * Reads in audio file instance from previous method, converts it to a clip and plays that clip
	 * 
	 * @param audioFile File instance of input string from locally saved file
	 * @throws UnsupportedAudioFileException if type of audio file cannot be read/recognized as valid
	 * @throws LineUnavailableException if line cannot be found
	 * @throws InterruptedException if a thread is interrupted while waiting/idling
	 * @throws IOException if an IO operation has failed or been interrupted
	 */
	public static void playWAVFile(File audioFile) throws UnsupportedAudioFileException, LineUnavailableException, InterruptedException, IOException {
		Clip audioClip = makeClip(audioFile); //saves converted clip file as a variable
		playClip(audioClip); //calls playClip() to play file

	}

}
