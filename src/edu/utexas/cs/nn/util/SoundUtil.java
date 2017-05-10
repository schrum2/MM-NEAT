package edu.utexas.cs.nn.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class SoundUtil {
	
	private static final String BEARGROWL_WAV = "data/sounds/bear_growl_y.wav";
	
	public static void main(String[] args) {
		playWAVFile(BEARGROWL_WAV);
	}
	
	
	public static void playClip(Clip audioClip) {
        audioClip.start();     
        do {
            // wait for the playback completes
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } while(audioClip.isRunning());      
	}
	
	/**
	 * 
	 * @param audio
	 */
	public static void playWAVFile(String audio) {
		File audioFile = new File(audio);
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);      
            playClip(audioClip);
            audioClip.close();
             
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
         
    }
}
