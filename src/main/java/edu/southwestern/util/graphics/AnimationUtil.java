package edu.southwestern.util.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.objectbreeder.ThreeDimensionalObjectBreederTask;

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
	
	public static final int CPPN_OUTPUT_INDEX_SCALE = 3;
	public static final int CPPN_OUTPUT_INDEX_ROTATION = 4;

	/**
	 * Utility method that generates an array of images based on an input CPPN.
	 * 
	 * @param n CPPN used to create image
	 * @param imageWidth width of created image
	 * @param imageHeight height of created image
	 * @param startTime input time when animation begins
	 * @param endTime input time when animation ends
	 * @param inputMultiples array with inputs determining whether CPPN inputs are turned on or off
	 * @return Array of images that can be animated in a JApplet
	 */
	public static BufferedImage[] imagesFromCPPN(Network n, int imageWidth, int imageHeight, int startTime, int endTime, double[] inputMultiples) {
		return imagesFromCPPN(n, imageWidth, imageHeight, startTime, endTime, inputMultiples, 1.0, 0.0);
	}

	public static BufferedImage[] imagesFromCPPN(Network n, int imageWidth, int imageHeight, int startTime, int endTime, double[] inputMultiples, double scale, double rotation) {
		BufferedImage[] images = new BufferedImage[endTime-startTime];
		for(int i = startTime; i < endTime; i++) {
			// if animate scale and rotation (command line parameter)
			// then query CPPN here with time i (other inputs are 0)
			// check the scale and rotation outputs and change the scale and rotation values
			if(Parameters.parameters.booleanParameter("animateWithScaleAndRotation")) {
				double[] input = GraphicsUtil.get2DObjectCPPNInputs(0, 0, imageWidth, imageHeight, i/FRAMES_PER_SEC, 1, 0);
				// Multiplies the inputs of the pictures by the inputMultiples; used to turn on or off the effects in each picture
				for(int j = 0; j < inputMultiples.length; j++) {
					input[j] = input[j] * inputMultiples[j];
				}
				// Eliminate recurrent activation for consistent images at all resolutions
				n.flush();
				double[] scaleAndRotationOutputs = n.process(input);
				scale = scaleAndRotationOutputs[CPPN_OUTPUT_INDEX_SCALE];
				rotation = scaleAndRotationOutputs[CPPN_OUTPUT_INDEX_ROTATION];
			}			
			images[i-startTime] = GraphicsUtil.imageFromCPPN(n, imageWidth, imageHeight, inputMultiples, i/FRAMES_PER_SEC, scale, rotation);
		}
		return images;
	}		
	
	/**
	 * Utility method that generates an array of shapes based on an input CPPN.
	 * These BufferedImages are used to animate a three-dimensional object.
	 * 
	 * @param n CPPN used to create shape
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param startTime input time when animation begins
	 * @param endTime input time when animation ends
	 * @param color desired color of shapes (if null, colors are evolved)
	 * @param heading horizontal tilt of object
	 * @param pitch vertical tilt of object
	 * @param inputMultiples array with inputs determining whether CPPN inputs are turned on or off
	 * @return
	 */
	public static BufferedImage[] shapesFromCPPN(Network n, int imageWidth, int imageHeight, int startTime, int endTime, Color color, double heading, double pitch, double[] inputMultiples) {
		BufferedImage[] images = new BufferedImage[endTime-startTime];
		for(int i = startTime; i < endTime; i++) {
			images[i-startTime] = ThreeDimensionalUtil.currentImageFromCPPN(n, imageWidth, imageHeight, ThreeDimensionalObjectBreederTask.CUBE_SIDE_LENGTH, ThreeDimensionalObjectBreederTask.SHAPE_HEIGHT,ThreeDimensionalObjectBreederTask.SHAPE_WIDTH, ThreeDimensionalObjectBreederTask.SHAPE_DEPTH, color, heading, pitch, inputMultiples, i/FRAMES_PER_SEC);
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
