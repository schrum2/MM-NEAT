package edu.utexas.cs.nn.util;

//import javax.media.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

public class SoundUtil {


	private static final String BEARGROWL_WAV = "data/sounds/bear_growl_y.wav";
	private static final String HAPPY_MP3 = "data/sounds/25733.mp3";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
		//playWAVFile(BEARGROWL_WAV);
		SoundJLayer soundToPlay = new SoundJLayer(HAPPY_MP3);
		soundToPlay.play();
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

	private static class SoundJLayer extends PlaybackListener implements Runnable {
		private String filePath;
		private AdvancedPlayer player;
		private Thread playerThread;    

		public SoundJLayer(String filePath) {
			this.filePath = filePath;
		}

		public void play(){
			try {
				String urlAsString = 
						"file:///" 
						+ new java.io.File(".").getCanonicalPath() + "/" 
						+ this.filePath;

				this.player = new AdvancedPlayer
						(new java.net.URL(urlAsString).openStream(),
						 javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
			} catch (JavaLayerException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1); // TODO: Give a specific error message?
			}

			this.player.setPlayBackListener(this);
			this.playerThread = new Thread(this, "AudioPlayerThread");
			this.playerThread.start();

		}

		// PlaybackListener members

//		public void playbackStarted(PlaybackEvent playbackEvent){
//			System.out.println("playbackStarted");
//		}
//
//		public void playbackFinished(PlaybackEvent playbackEvent)
//		{
//			System.out.println("playbackEnded");
//		}    

		// Runnable members

		public void run()
		{
			try{
				this.player.play();
			}catch (javazoom.jl.decoder.JavaLayerException ex) {
				ex.printStackTrace();
			}
		}
	}


	//TODO: implement methods associated with playing an mp3 file
	//TODO: figure out how to convert an audio file into an array of numbers and vice versa

}
