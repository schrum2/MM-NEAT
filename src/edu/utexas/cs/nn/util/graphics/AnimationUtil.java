package edu.utexas.cs.nn.util.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import edu.utexas.cs.nn.networks.Network;

/**
 * Series of utility methods used to create and manipulate
 * animations. Mainly used by AnimationBreederTask
 * 
 * @author Isabel Tweraser
 *
 */
public class AnimationUtil {

	//default frame rate to smooth out animation
	public static final double FRAMES_PER_SEC = 24.0;

	/**
	 * Utility method that generates an array of images based on an input CPPN.
	 * 
	 * @param n CPPN used to create image
	 * @param imageWidth width of created image
	 * @param imageHeight height of created image
	 * @param time How long the animation should be. This input determines size of array of images
	 * @param inputMultiples array with inputs determining whether checkboxes in interface should be turned on or off
	 * @return Array of images that can be animated in a JApplet
	 */
	public static BufferedImage[] imagesFromCPPN(Network n, int imageWidth, int imageHeight, int startTime, int endTime, double[] inputMultiples) {
		BufferedImage[] images = new BufferedImage[endTime-startTime];
		for(int i = startTime; i < endTime; i++) {
			images[i-startTime] = GraphicsUtil.imageFromCPPN(n, imageWidth, imageHeight, inputMultiples, i/FRAMES_PER_SEC);
		}
		return images;
	}		
	
	/**
	 * Method used to save an array of buffered images to a file. Uses external class GifSequenceWriter
	 * to write an output stream to a file. Used by save() method for AnimationBreederTask
	 * 
	 * @param slides Array of BufferedImages to be saved
	 * @param pauseBetweenFrames designated pause between frames for gif (taken from current state on AnimationBreeder interface)
	 * @param filename Desired name of file being saved
	 * @throws IOException if an I/O operation has failed or been interrupted
	 */
	public static void createGif(BufferedImage[] slides, int pauseBetweenFrames, String filename) throws IOException {
		ImageOutputStream output = new FileImageOutputStream(new File(filename)); 
		GifSequenceWriter writer = new GifSequenceWriter(output, slides[0].getType(), pauseBetweenFrames, true);
		for (BufferedImage slide : slides) {
			writer.writeToSequence(slide);
		}
		writer.close();
		output.close();
	}
}
