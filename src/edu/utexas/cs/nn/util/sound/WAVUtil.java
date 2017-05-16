package edu.utexas.cs.nn.util.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WAVUtil {
	

	// Methods associated with playing WAV file

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
	 * Converts audio file into audio input stream.
	 * 
	 * @param audioFile file to be converted
	 * @return AudioInputStream that can be converted into a clip
	 * @throws UnsupportedAudioFileException if file does not contain valid/recognizable data 
	 * @throws IOException if an IO operation has failed or been interrupted
	 */
	public static AudioInputStream audioStream(File audioFile) throws UnsupportedAudioFileException, IOException {
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); //converts file into audio stream
		return audioStream;
	}

	/**
	 * Converts a file into an audio clip. 
	 * 
	 * @param audioStream stream to be converted
	 * @return newly converted audio clip
	 * @throws UnsupportedAudioFileException if type of audio file cannot be read/recognized as valid
	 * @throws IOException if an IO operation has failed or been interrupted
	 * @throws LineUnavailableException if line cannot be found
	 */
	public static Clip makeClip(AudioInputStream audioStream) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
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
	 * Reads in audio file instance from previous method, converts it to a clip and plays that clip.
	 * 
	 * @param audioFile File instance of input string from locally saved file
	 * @throws UnsupportedAudioFileException if type of audio file cannot be read/recognized as valid
	 * @throws LineUnavailableException if line cannot be found
	 * @throws InterruptedException if a thread is interrupted while waiting/idling
	 * @throws IOException if an IO operation has failed or been interrupted
	 */
	public static void playWAVFile(File audioFile) throws UnsupportedAudioFileException, LineUnavailableException, InterruptedException, IOException {
		AudioInputStream stream = audioStream(audioFile);
		Clip audioClip = makeClip(stream); //saves converted clip file as a variable
		playClip(audioClip); //calls playClip() to play file

	}

	//Methods associated with WAV file conversion

	/**
	 * Reads in a WAV file and converts it into an array of byte data type
	 * 
	 * @param inputAudio String audio file being converted
	 * @return array of bytes - data representation of audio
	 * @throws IOException if an IO operation has failed or been interrupted
	 */
	public static byte[] WAVToByte(String inputAudio) throws IOException {
		File file = new File(inputAudio);
		byte[] data = new byte[(int) file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(data);
		in.close();
		return data;
	}

	/**
	 * converts an array of bytes into an Audio Input Stream so that it can be converted into a clip and played as 
	 * an audio file
	 * 
	 * @param inputData array of bytes
	 * @return AudioInputStream of data
	 * @throws UnsupportedAudioFileException if file does not contain valid/recognizable data
	 * @throws IOException if an IO operation has failed or been interrupted 
	 */
	public static AudioInputStream byteToAIS(byte[] inputData) throws UnsupportedAudioFileException, IOException {
		ByteArrayInputStream IS = new ByteArrayInputStream(inputData);
		AudioInputStream AIS = AudioSystem.getAudioInputStream(IS);
		return AIS;
	}

	/**
	 * Converts AIS to a clip and then calls the playClip() method to play it.
	 * 
	 * @param data input data used for Audio Input Stream
	 * @throws UnsupportedAudioFileException if file does not contain valid/recognizable data
	 * @throws IOException if an IO operation has failed or been interrupted 
	 * @throws InterruptedException if a thread is waiting, sleeping, or otherwise occupied
	 * @throws LineUnavailableException if a line cannot be opened because it is unavailable
	 */
	public static void playByteAIS(byte[] data) throws UnsupportedAudioFileException, IOException, InterruptedException, LineUnavailableException {
		AudioInputStream byteAudio = byteToAIS(data);
		Clip byteAudioClip = makeClip(byteAudio);
		playClip(byteAudioClip);
	}



}
