package edu.utexas.cs.nn.util.graphics;

import java.awt.image.BufferedImage;

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
}
