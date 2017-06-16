package edu.utexas.cs.nn.util.sound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Contains a variety of utility methods that can extract byte 
 * and double arrays (both of which are useful for manipulating, playing, and
 * reading audio files) from a variety of inputs (file, string, AudioInputStream).
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundToArray {
	/**
	 * DOESN'T WORK FOR AUDIO - used for generation of image representations
	 * of waves
	 * 
	 * TODO: Need to delete/replace this eventually
	 * 
	 * Reads audio samples from a file (in .wav or .au format) and returns
	 * them as a double array with values between -1.0 and +1.0.
	 *
	 * @param  filename the name of the audio file
	 * @return the array of samples
	 */
	public static double[] read(String filename) {
		byte[] data= readByte(filename);
		try {
			// audio input stream used to access sample size in bits of audio format
			AudioInputStream ais = WAVUtil.audioStream(filename);
			int bits = ais.getFormat().getSampleSizeInBits();
			// decides which maximum value to use based on number of bits
			double max = bits == 16 ? PlayDoubleArray.MAX_16_BIT : PlayDoubleArray.MAX_8_BIT;
			int N= data.length;
			double[] d= new double[N/2];
			for (int i= 0; i < N/2; i++) {
				d[i]= ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / max;
			}
			return d;
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		return null; // shouldn't happen
	}

	/**
	 * Reads in data from audio file and returns it as an array of bytes. 
	 * 
	 * @param filename string reference to audio file being used
	 * @return byte array containing data from audio file 
	 */
	public static byte[] readByte(String filename) {
		AudioInputStream ais;
		byte[] data= null;
		try {
			ais = WAVUtil.audioStream(filename);
			data= new byte[ais.available()];
			ais.read(data);
		} catch (UnsupportedAudioFileException e1) {
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return data;
	}

	//Methods from GT - used to extract amplitude from recorded wave

	/**
	 * Method that inputs an AudioInputStream, calls method that extracts amplitude byte array from
	 * the audio input stream, and returns a method call using the resulting byte array to 
	 * extractAmplitudeDataFromAmplitudeByteArray(). 
	 * 
	 * @param audioInputStream stream of audio being converted into amplitude data
	 * @return  method call that extracts amplitude data from byte array formed
	 */
	public static int[] extractAmplitudeDataFromAudioInputStream(AudioInputStream audioInputStream) {  
		AudioFormat format = audioInputStream.getFormat();  	
		byte[] audioBytes = extractAmplitudeByteArrayFromAudioInputStream(audioInputStream);
		//System.out.println(Arrays.toString(audioBytes));
		return extractAmplitudeDataFromAmplitudeByteArray(format, audioBytes);  //calls method that extracts amplitude data from byte array formed
	}  

	/**
	 * Method that inputs an AudioInputStrean, obtains the format of the stream and forms an array of bytes 
	 * based on the size of the stream to return. 
	 * 
	 * @param audioInputStream stream of audio being converted to byte array
	 * @return byte array representation of audio file
	 */
	public static byte[] extractAmplitudeByteArrayFromAudioInputStream(AudioInputStream audioInputStream) {
		AudioFormat format = audioInputStream.getFormat();  
		byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];  
		try {  
			audioInputStream.read(audioBytes);  
		} catch (IOException e) {  
			System.out.println("IOException during reading audioBytes");  
			e.printStackTrace();  
		} 

		return audioBytes;
	}

	/**
	 * Method that inputs the format of an AudioInputStream as well as the byte array formed from its contents
	 * and then creates an array of ints containing the amplitude data of the stream. 
	 * 
	 * @param format AudioFormat of AudioinputStream
	 * @param audioBytes byte array formed based on the size of the stream
	 * @return int array containing amplitude data from stream
	 */
	public static int[] extractAmplitudeDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {  
		// convert
		int[]  audioData = null;  
		if (format.getSampleSizeInBits() == 16) {  
			System.out.println("16 bit!");
			int nlengthInSamples = audioBytes.length / 2;  
			audioData = new int[nlengthInSamples];  
			if (format.isBigEndian()) {  
				System.out.println("isBigEndian");
				for (int i = 0; i < nlengthInSamples; i++) {  
					/* First byte is MSB (high order) */  
					int MSB = audioBytes[2 * i];  
					/* Second byte is LSB (low order) */  
					int LSB = audioBytes[2 * i + 1];  
					audioData[i] = (MSB << 8 | (255 & LSB));  
				}  
			} else {  
				System.out.println("isLittleEndian");
				for (int i = 0; i < nlengthInSamples; i++) {  
					/* First byte is LSB (low order) */  
					int LSB = audioBytes[2 * i];  
					/* Second byte is MSB (high order) */  
					int MSB = audioBytes[2 * i + 1];  
					audioData[i] = (MSB << 8 | (255 & LSB));  
				}  
			}  
		} else if (format.getSampleSizeInBits() == 8) {  
			System.out.println("8 bit!");

			int nlengthInSamples = audioBytes.length;  
			audioData = new int[nlengthInSamples];  
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {  
				System.out.println("signed: " + format.getEncoding().toString());
				// PCM_SIGNED  
				for (int i = 0; i < audioBytes.length; i++) {  
					audioData[i] = audioBytes[i];  
				}  
			} else {  
				System.out.println("unsigned: " + format.getEncoding().toString());
				// PCM_UNSIGNED  
				for (int i = 0; i < audioBytes.length; i++) {  
					audioData[i] = audioBytes[i] - 128;  
				}  
			}  
		}
		//System.out.println(Arrays.toString(audioData));
		if(format.getChannels() > 1) { //if AudioFormat is stereo and not mono
			int start = 0;
			int[] stereoToMonoSamples = new int[audioData.length/2];
			for(int i = 0; i < audioData.length; i += 4) {
				//adds two bytes to previous two
				stereoToMonoSamples[start] = audioData[i] + audioData[i+2];
				stereoToMonoSamples[start+1] = audioData[i+1] + audioData[i+3];
				//skips every two bytes
//				stereoToMonoSamples[start] = audioData[i];
//				stereoToMonoSamples[start+1] = audioData[start+1];
				start += 2;
			}
			return stereoToMonoSamples;
		} else {
			return audioData;  
		}
	}

	/**
	 * Converts an integer array of amplitudes to a double array using conversions
	 * that specify that all values will be between -1.0 and 1.0 using the 
	 * maximum value of a short.
	 * 
	 * @param intArray integer array of amplitudes
	 * @return converted double array of amplitudes
	 */
	public static double[] doubleArrayAmplitudesFromIntArrayAmplitudes(int[] intArray, int sampleSizeInBits) {
		// Assumes only 16 bit or 8 bit, no 32 bit
		// Currently only works for 16 bit
		double max = sampleSizeInBits == 16 ? PlayDoubleArray.MAX_16_BIT : PlayDoubleArray.MAX_8_BIT;
		double[] doubleArray = new double[intArray.length];
		for(int i = 0; i < intArray.length; i++) {
			doubleArray[i] = (intArray[i]*1.0) / max;
		}
		return doubleArray;
	}

	/**
	 * Method that takes in a string reference to an audio file and uses the file's
	 * Audio Input Stream to extract an array of bytes, which is then converted to 
	 * an array of ints and then an array of doubles within the range -1.0 to 1.0.
	 * This method only works for audio files with a mono format (not stereo), and
	 * has only been tested with 16 bit audio.
	 * 
	 * @param audio string reference to audio file being written
	 * @return double array representing audio file
	 */
	public static double[] readDoubleArrayFromStringAudio(String audio) {
		AudioInputStream AIS;
		try {
			AIS = WAVUtil.audioStream(audio);	
			int[] intArray = SoundToArray.extractAmplitudeDataFromAudioInputStream(AIS);
			int bitNum = AIS.getFormat().getSampleSizeInBits();
			double[] doubleArray = SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(intArray,bitNum);
			//System.out.println(Arrays.toString(doubleArray));
			return doubleArray;
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}

		return null; //this shouldn't happen
	}

	/**
	 * Doesn't work - makes 8 bit files playable, but they don't sound like original file.
	 * Converting AudioFormat of files directly isn't the best way to go
	 * 
	 * @param format AudioFormat of source wave
	 * @return adjusted format specified to 16 bits, 2 bytes/frame, and a signed PCM encoding
	 */
	public static AudioFormat getAudioFormatRestrictedTo16Bits(AudioFormat format) {
		//Encoding encoding = format.getEncoding();
		float sampleRate = format.getSampleRate();
		int channels = format.getChannels();
		boolean endian = format.isBigEndian();
		float frameRate = format.getFrameRate();
		// only thing not in original format is the bits per sample and bytes per sample (set to 16 and 2 respectively)
		AudioFormat adjustedFormat = new AudioFormat(Encoding.PCM_SIGNED, sampleRate, PlayDoubleArray.BITS_PER_SAMPLE, channels, PlayDoubleArray.BYTES_PER_SAMPLE, frameRate, endian);
		return adjustedFormat;
	}

	/**
	 * Doesn't work - meant to adjust sample size of AudioFormat so that it can be played back correctly, 
	 * but instead creates an error because the SourceDataLine does not support an adjusted AudioFormat.
	 * 
	 * @param nSampleSizeInBits target sample size 
	 * @param bBigEndian true if bigEndian, false otherwise
	 * @param sourceStream input AudioInputStream of source audio
	 * @return adjusted AudioInputStream
	 */
	public static AudioInputStream convertSampleSizeAndEndianess(int nSampleSizeInBits, boolean bBigEndian, AudioInputStream sourceStream) {
		AudioFormat sourceFormat = sourceStream.getFormat();
		AudioFormat targetFormat = new AudioFormat(sourceFormat.getEncoding(), sourceFormat.getSampleRate(), nSampleSizeInBits, sourceFormat.getChannels(), calculateFrameSize(sourceFormat.getChannels(),nSampleSizeInBits),sourceFormat.getFrameRate(), bBigEndian);
		return AudioSystem.getAudioInputStream(targetFormat, sourceStream);
	}

	/**
	 * Calculates size of frame. Necessary for method above
	 * 
	 * @param nChannels number of channels
	 * @param nSampleSizeInBits sample size in bits
	 * @return frame size
	 */
	private static int calculateFrameSize(int nChannels, int nSampleSizeInBits) {
		return ((nSampleSizeInBits + 7) / 8) * nChannels;
	}

	/**
	 * Doesn't work - another attempt at converting eight bit files to sixteen bit. Takes in 8-bit 
	 * audio and converts it to an int array. The values of the int array are then individually 
	 * manipulated to match 16 bit index specifications. Then the array is converted to a double 
	 * array using 16-bit audio format. Pretty sure it is throwing an UnsupportedAudioFileException
	 * because it just returns null
	 * 
	 * Schrum: I fixed this so it returns an array (you just forgot the return) but the result still does
	 *  not sound right. Annoying.
	 * 
	 * @param audio audio file
	 * @return adjusted 16 bit double array
	 */
	public static double[] eightBitToSixteenBit(String audio) {
		try {
			AudioInputStream eightBitAIS = WAVUtil.audioStream(audio);
			byte[] eightBitBytes = WAVUtil.WAVToByte(audio);
			int[] eightBitInts = SoundToArray.extractAmplitudeDataFromAmplitudeByteArray(eightBitAIS.getFormat(), eightBitBytes);
			//System.out.println(Arrays.toString(eightBitInts));
			//MiscUtil.waitForReadStringAndEnterKeyPress();
			int[] sixteenBitInts = new int[eightBitInts.length];
			for(int i = 0; i < eightBitInts.length; i++) {
				sixteenBitInts[i] = (eightBitInts[i]-128) *256;
			}
			return SoundToArray.doubleArrayAmplitudesFromIntArrayAmplitudes(sixteenBitInts, 16);
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null; //shouldn't happen
	}
}
