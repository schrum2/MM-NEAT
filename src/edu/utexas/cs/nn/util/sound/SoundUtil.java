package edu.utexas.cs.nn.util.sound;


import java.awt.Color;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;
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
	private static final String PIRATES = "data/sounds/pirates.mid";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException, JavaLayerException {
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false"});
		MMNEAT.loadClasses();
		HyperNEATCPPNGenotype test = new HyperNEATCPPNGenotype(3, 1, 0);
		for(int i = 0; i < 30; i++) {
			test.mutate();
		}
		Network cppn = test.getCPPN();
		double[] testArray = amplitudeGenerator(cppn, 60000, 440);
		double[] testArray2 = amplitudeGenerator(cppn, 60000, 1000);
		StdAudio.play(testArray);
		StdAudio.play(testArray2);
		
		ArrayList<Double> fileArrayList = ArrayUtil.doubleVectorFromArray(testArray); //convert array into array list
		DrawingPanel panel = new DrawingPanel(500,500, "1"); //create panel where line will be plotted 
		GraphicsUtil.linePlot(panel, -1.0, 1.0, fileArrayList, Color.black); //call linePlot with ArrayList to draw graph
		
		
		ArrayList<Double> fileArrayList2 = ArrayUtil.doubleVectorFromArray(testArray2); //convert array into array list
		DrawingPanel panel2 = new DrawingPanel(500,500, "2"); //create panel where line will be plotted 
		GraphicsUtil.linePlot(panel2, -1.0, 1.0, fileArrayList2, Color.black); //call linePlot with ArrayList to draw graph
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		//playWAVFile(BEARGROWL_WAV);
		mp3Conversion(HAPPY_MP3).playMP3File();
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

		double[] splice = new double[applauseNumbers.length];
//		for(int i = 0; i < splice.length; i++) {
//			if(i < 46)
//				splice[i] = bearNumbers[i];
//			else 
//				splice[i] = applauseNumbers[i];
//		}
//		StdAudio.play(splice);

		double[] bear = new double[bearNumbers.length];
		for(int i = 0; i < bear.length; i++) {
			if(i < 50120 || i >= bearNumbers.length-11) 
				bear[i] = bearNumbers[i];
			else 
				bear[i] = 20;
		}
		bear[bear.length-1] = 0;
		StdAudio.play(bear);
		
//		double[] applause = new double[bearNumbers.length];
//		for(int i = 0; i < original.length; i++) {
//			if(i < 50120 || i >= bearNumbers.length-11) 
//				original[i] = bearNumbers[i];
//			else 
//				original[i] = 20;
//		}
//		original[original.length-1] = 0;
//		StdAudio.play(original);
//		
//		double[] original = new double[bearNumbers.length];
//		for(int i = 0; i < original.length; i++) {
//			if(i < 50120 || i >= bearNumbers.length-11) 
//				original[i] = bearNumbers[i];
//			else 
//				original[i] = 20;
//		}
//		original[original.length-1] = 0;
//		StdAudio.play(original);
		
		//		StdAudio.wavePlot(BEARGROWL_WAV);
		//		StdAudio.wavePlot(APPLAUSE_WAV);
		//		StdAudio.wavePlot(HARP_WAV);

		System.out.println("bear growl: " + bearNumbers.length);
		System.out.println("applause: " + applauseNumbers.length);
		System.out.println("harp: " + harpNumbers.length);
		double[] applauseAndHarp = StdAudio.overlap(APPLAUSE_WAV, HARP_WAV);
		System.out.println("applauseAndHarp length: " + applauseAndHarp.length);
		double[] bearGrowlAndHarp = StdAudio.overlap(BEARGROWL_WAV, HARP_WAV);
		System.out.println("bearGrowlAndHarp length: " + bearGrowlAndHarp.length);
		double[] applauseAndBearGrowl = StdAudio.overlap(APPLAUSE_WAV, BEARGROWL_WAV);
		System.out.println("applauseAndBearGrowl length: " + applauseAndBearGrowl.length);

		//		StdAudio.play(applauseAndHarp);
		//		StdAudio.play(bearGrowlAndHarp);
		//		StdAudio.play(applauseAndBearGrowl);
		
		StdAudio.play(PIRATES);
		
		

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
	
	/**
	 * Method that inputs an AudioInputStream, obtains the format of the stream and forms an array of bytes 
	 * based on the size of the stream, and returns a method call to extractAmplitudeDataFromAmplitudeByteArray(). 
	 * 
	 * @param audioInputStream stream of audio being converted into amplitude data
	 * @return  method call that extracts amplitude data from byte array formed
	 */
	public static int[] extractAmplitudeDataFromAudioInputStream(AudioInputStream audioInputStream) {  
		AudioFormat format = audioInputStream.getFormat();  
		byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];  
		try {  
			audioInputStream.read(audioBytes);  
		} catch (IOException e) {  
			System.out.println("IOException during reading audioBytes");  
			e.printStackTrace();  
		}  
		return extractAmplitudeDataFromAmplitudeByteArray(format, audioBytes);  //calls method that extracts amplitude data from byte array formed
	}  
	
	/**
	 * Method that inputs the format of an AudioInputStream as well as the byte array formed from its contents
	 * and then creates an array of ints containing the amplitude data of the stream. 
	 * 
	 * @param format AudioFormat of AudioinputStream
	 * @param audioBytes byte array formed based on the size of the stream
	 * @return int array containing amplitude data from stream
	 */
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

	//CPPN 
	
	/**
	 * Creates a double array of amplitudes using a CPPN. CPPN has three inputs - time, frequency,
	 * and a bias. It only has one output, which is the amplitude. The array of inputs is looped through 
	 * so that each index is manipulated by the CPPN, and the output indexes are saved into a result array 
	 * of doubles. 
	 * 
	 * @param CPPN network used to generate amplitude 
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @return array of doubles representing all CPPN-manipulated output amplitudes
	 */
	public static double[] amplitudeGenerator(Network CPPN, int length, int frequency) {
		double[] result = new double[length];
		for(double time = 0; time < length; time++) {
			//double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, Math.sin(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};
			//double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, ActivationFunctions.triangleWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};
			double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, 
					Math.sin(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
//					ActivationFunctions.triangleWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
//					ActivationFunctions.squareWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
					HyperNEATCPPNGenotype.BIAS};
			double[] outputs = CPPN.process(inputs);
			result[(int) time] = outputs[0]; // amplitude
		}
		return result;
	}




}
