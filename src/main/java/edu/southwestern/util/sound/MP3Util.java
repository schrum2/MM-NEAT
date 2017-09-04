package edu.southwestern.util.sound;

import java.io.IOException;
import java.net.MalformedURLException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * Series of utility methods associated with playing an MP3 file.
 * 
 * @author Isabel Tweraser
 *
 */
public class MP3Util {
	/**
	 * Reads in file reference to an mp3 and converts into SoundJLayer so that it can access methods of the 
	 * SoundJLayer class and be played. 
	 * @param mp3 input file being played
	 * @return file as a SoundJLayer
	 */
	public static SoundJLayer mp3Conversion(String mp3) {
		SoundJLayer soundToPlay = new SoundJLayer(mp3);
		return soundToPlay;
	}
	
	/**
	 * Inner SoundJLayer class that contains methods associated with playing an MP3 file.  
	 */
	public static class SoundJLayer extends PlaybackListener implements Runnable {
		private String filePath;
		private AdvancedPlayer player;
		private Thread playerThread;    

		public SoundJLayer(String filePath) {
			this.filePath = filePath;
		}

		/**
		 * Writes filePath reference as a string url
		 * @param filePath input file being written as string url
		 * @return url reference to filePath
		 * @throws IOException if an IO operation has failed of been interrupted
		 */
		public static String urlAsString(String filePath) throws IOException {
			String url = "file:///" 
					+ new java.io.File(".").getCanonicalPath() + "/" 
					+ filePath;
			return url;
		}

		/**
		 * reads input file into an AdvancedPlayer
		 * @param url input string url that references audio file
		 * @return AdvancedPlayer that reads string file
		 * @throws MalformedURLException if a malformed URL has occurred
		 * @throws JavaLayerException base class for all exceptions thrown by JavaLayer
		 * @throws IOException if an IO operation has failed of been interrupted
		 */
		public AdvancedPlayer player(String url) throws MalformedURLException, JavaLayerException, IOException {
			this.player = new AdvancedPlayer
					(new java.net.URL(url).openStream(),
							javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
			return player;
		}

		/**
		 * Reads AdvancedPlayer to a thread so that it can be played by the SoundJLayer class
		 * @param player AdvancedPlayer reading in url file
		 * @return thread that can be played by SoundJLayer class
		 */
		public Thread playerThread(AdvancedPlayer player) {
			this.player.setPlayBackListener(this);
			this.playerThread = new Thread(this, "AudioPlayerThread");
			return playerThread;
		}

		/**
		 * Converts file into a player and then a thread, and then runs that thread (playing the MP3 file).
		 * @throws IOException if an IO operation has failed of been interrupted
		 * @throws JavaLayerException base class for all exceptions thrown by JavaLayer
		 */
		public void playMP3File() throws IOException, JavaLayerException{
			String url = urlAsString(filePath);
			AdvancedPlayer mp3Player = player(url);
			playerThread(mp3Player).start();

		}

		/**
		 * Runs player
		 */
		public void run()
		{
			try{
				this.player.play();
			}catch (javazoom.jl.decoder.JavaLayerException ex) {
				ex.printStackTrace();
			}
		}

	}
}
