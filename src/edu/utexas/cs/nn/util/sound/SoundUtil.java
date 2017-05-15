package edu.utexas.cs.nn.util.sound;


import java.io.*;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Scanner;

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

/**
 * Class containing utility methods that manipulate various types of sound files. Can play audio files,
 * convert them, and reconstruct them. 
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundUtil {

	private static final String BEARGROWL_WAV = "data/sounds/bear_growl_y.wav";
	private static final String APPLAUSE_WAV = "data/sounds/applause_y.wav";
	private static final String HARP_WAV = "data/sounds/harp.wav";
	private static final String HAPPY_MP3 = "data/sounds/25733.mp3";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException, JavaLayerException {
		//playWAVFile(BEARGROWL_WAV);
		//mp3Conversion(HAPPY_MP3).playMP3File();
		byte[] bearNumbers = WAVToByte(BEARGROWL_WAV);
		//System.out.println(Arrays.toString(bearNumbers));
		AudioInputStream bearAIS = SoundUtil.byteToAIS(bearNumbers);
		int[] bearData = SoundUtil.extractAmplitudeDataFromAudioInputStream(bearAIS);
		//System.out.println(Arrays.toString(bearData));

		byte[] applauseNumbers = WAVToByte(APPLAUSE_WAV);	
		//System.out.println(Arrays.toString(applauseNumbers));

		byte[] harpNumbers = WAVToByte(HARP_WAV);
		//System.out.println(Arrays.toString(harpNumbers));
		AudioInputStream harpAIS = SoundUtil.byteToAIS(harpNumbers);
		int[] harpData = SoundUtil.extractAmplitudeDataFromAudioInputStream(harpAIS);
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
		
		AudioInputStream applauseAIS = byteToAIS(applauseNumbers);
		int[] applauseNumbers2 = extractAmplitudeDataFromAudioInputStream(applauseAIS);
		//System.out.println(Arrays.toString(applauseNumbers2));
		

//		for(int i = bearNumbers.length-11; i <= bearNumbers.length-1; i++) {
//			System.out.print(bearNumbers[i] + " ");
//		}
//		System.out.println();

//		byte[] splice = new byte[applauseNumbers.length];
//		for(int i = 0; i < splice.length; i++) {
//			if(i < 46)
//				splice[i] = bearNumbers[i];
//			else 
//				splice[i] = applauseNumbers[i];
//		}
//		playByteAIS(splice);

		//		byte[] original = new byte[bearNumbers.length];
		//		for(int i = 0; i < original.length; i++) {
		//			if(i < 46 || i >= bearNumbers.length-11) 
		//				original[i] = bearNumbers[i];
		//			else 
		//				original[i] = 20;
		//		}
		//		original[original.length-1] = 0;
		//		
		//		playByteAIS(original);
	}

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

	//Methods from GT - used to extract amplitude from recorded wave

	
	//same thing as WAVToByte: 
	
//	private int[] extractAmplitudeFromFile(File wavFile) throws IOException, UnsupportedAudioFileException {  
//		byte[] arrFile; 
//		// create file input stream  
//		FileInputStream fis = new FileInputStream(wavFile);  
//		// create bytearray from file  
//		arrFile = new byte[(int) wavFile.length()];  
//		fis.read(arrFile);  
//		return extractAmplitudeFromFileByteArray(arrFile);  
//	} 
	
	
	//same thing as byteToAIS: 
	
//	private int[] extractAmplitudeFromFileByteArray(byte[] arrFile) throws UnsupportedAudioFileException, IOException {  
//		// System.out.println("File : "+wavFile+""+arrFile.length);  
//		ByteArrayInputStream bis = new ByteArrayInputStream(arrFile);  
//		return extractAmplitudeFromFileByteArrayInputStream(bis);  
//	} 
//	
//	/**  
//	 * for extracting amplitude array the format we are using :16bit, 22khz, 1  
//	 * channel, littleEndian,  
//	 *   
//	 * @return PCM audioData  
//	 * @throws IOException 
//	 * @throws UnsupportedAudioFileException 
//	 * @throws Exception  
//	 */  
//	private int[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis) throws UnsupportedAudioFileException, IOException {  
//		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);  
//		return extractAmplitudeDataFromAudioInputStream(audioInputStream);  
//	}  

	public static int[] extractAmplitudeDataFromAudioInputStream(AudioInputStream audioInputStream) {  
		AudioFormat format = audioInputStream.getFormat();  
		byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];  
		// calculate durations  
		long durationMSec = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());  
		//double durationSec = durationMSec / 1000.0;  
		// System.out.println("The current signal has duration "+durationSec+" Sec");  
		try {  
			audioInputStream.read(audioBytes);  
		} catch (IOException e) {  
			System.out.println("IOException during reading audioBytes");  
			e.printStackTrace();  
		}  
		return extractAmplitudeDataFromAmplitudeByteArray(format, audioBytes);  
	}  
	
	private static int[] extractAmplitudeDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {  
		// convert
		int[]  audioData = null;  
		if (format.getSampleSizeInBits() == 16) {  
			int nlengthInSamples = audioBytes.length / 2;  
			audioData = new int[nlengthInSamples];  
			if (format.isBigEndian()) {  
				for (int i = 0; i < nlengthInSamples; i++) {  
					/* First byte is MSB (high order) */  
					int MSB = audioBytes[2 * i];  
					/* Second byte is LSB (low order) */  
					int LSB = audioBytes[2 * i + 1];  
					audioData[i] = (MSB << 8 | (255 & LSB));  
				}  
			} else {  
				for (int i = 0; i < nlengthInSamples; i++) {  
					/* First byte is LSB (low order) */  
					int LSB = audioBytes[2 * i];  
					/* Second byte is MSB (high order) */  
					int MSB = audioBytes[2 * i + 1];  
					audioData[i] = (MSB << 8 | (255 & LSB));  
				}  
			}  
		} else if (format.getSampleSizeInBits() == 8) {  
			int nlengthInSamples = audioBytes.length;  
			audioData = new int[nlengthInSamples];  
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {  
				// PCM_SIGNED  
				for (int i = 0; i < audioBytes.length; i++) {  
					audioData[i] = audioBytes[i];  
				}  
			} else {  
				// PCM_UNSIGNED  
				for (int i = 0; i < audioBytes.length; i++) {  
					audioData[i] = audioBytes[i] - 128;  
				}  
			}  
		}
		return audioData;  
	}

//	public byte[] getAudioBytes() {  
//		return audioBytes;  
//	}  
//
//	public double getDurationSec() {  
//		return durationSec;  
//	} 
//
//	public double getDurationMiliSec() {  
//		return durationMSec;  
//	}  
//
//	public int[] getAudioData() {  
//		return audioData;  
//	}  
//	public AudioFormat getFormat() {  
//		return format;  
//	}  


	// Methods associated with playing MP3 file

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

	private static class SoundJLayer extends PlaybackListener implements Runnable {
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





	//TODO: figure out how to convert an audio file into an array of numbers and vice versa

}
