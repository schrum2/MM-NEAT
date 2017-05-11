package edu.utexas.cs.nn.util;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.*;

//import javax.media.*;
import java.io.*;

public class SoundUtil {


	private static final String BEARGROWL_WAV = "data/sounds/bear_growl_y.wav";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		//playWAVFile(BEARGROWL_WAV);

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
	 * Reads in audio file instance from previous method, converts it to a clip and plays that clip.
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

//	private URL url;
//	private MediaLocator mediaLocator;
//	private Player playMP3;
//
//	public mp3(String mp3)
//	{
//		try{
//			this.url = new URL(mp3);
//		}catch(java.net.MalformedURLException e)
//		{System.out.println(e.getMessage());}
//	}
//
//	public void run()
//	{
//
//		try{
//			mediaLocator = new MediaLocator(url);     
//			playMP3 = Manager.createPlayer(mediaLocator);
//		}catch(java.io.IOException e)
//		{System.out.println(e.getMessage());
//		}catch(javax.media.NoPlayerException e)
//		{System.out.println(e.getMessage());}
//
//		playMP3.addControllerListener(new ControllerListener()
//		{
//			public void controllerUpdate(ControllerEvent e)
//			{
//				if (e instanceof EndOfMediaEvent)
//				{
//					playMP3.stop();
//					playMP3.close();
//				}
//			}
//		}
//				);
//		SoundUtil.realize();
//		SoundUtil.start();
//	} 
//}
//TODO: implement methods associated with playing an mp3 file
//TODO: figure out how to convert an audio file into an array of numbers and vice versa

}
