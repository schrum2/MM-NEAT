package edu.southwestern.util.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.activationfunctions.FullLinearPiecewiseFunction;
import edu.southwestern.networks.activationfunctions.HalfLinearPiecewiseFunction;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.sound.SoundToArray;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;

/**
 * Several useful methods for creating and manipulating images.
 * Mostly used by Picbreeder and PictureRemix.
 * 
 * @author Lauren Gillespie, edits by Isabel Tweraser
 *
 */
public class GraphicsUtil {

	public static final int HUE_INDEX = 0;
	public static final int SATURATION_INDEX = 1;
	public static final int BRIGHTNESS_INDEX = 2;
	public static final int NUM_HSB = 3;
	public static final double BIAS = 1.0;// a common input used in neural networks
	public static final double SQRT2 = Math.sqrt(2); // Used for scaling distance from center
	
	/**
	 * Determine the default graphics configuration for the current system.
	 * Used to preview Mario levels.
	 * @return
	 */
	public static GraphicsConfiguration getConfiguration() { 
		return GraphicsEnvironment.getLocalGraphicsEnvironment(). 
				getDefaultScreenDevice().getDefaultConfiguration(); 
	} 

	/**
	 * Save an image to the specified filename (which includes path and file extension)
	 * 
	 * @param image Buffered image
	 * @param filename Path and file name plus extension
	 */
	public static void saveImage(BufferedImage image, String filename) {
		String extension = filename.substring(filename.lastIndexOf(".") + 1);
		// write file
		try {
			System.out.println("Save image: "+filename);
			ImageIO.write(image, extension, new java.io.File(filename));
		} catch (java.io.IOException e) {
			System.err.println("Unable to save image:\n" + e);
		}
	}
	
	/**
	 * Used by imagematch because we assume all inputs are on and time is irrelevant.
	 * 
	 * @param n CPPN
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight) {
		//-1 indicates that we don't care about time
		return imageFromCPPN(n,imageWidth,imageHeight, ArrayUtil.doubleOnes(PicbreederTask.CPPN_NUM_INPUTS), -1);
	}

	/**
	 * Default version of Buffered Image creation used for Picbreeder. Takes input multipliers into account,
	 * but time is irrelevant so it is defaulted to -1.
	 * 
	 * @param n CPPN
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param inputMultiples array of multiples indicating whether to turn activation functions on or off
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight, double[] inputMultiples) {
		return imageFromCPPN(n, imageWidth, imageHeight, inputMultiples, -1);
	}

	/**
	 * Draws the image created by the CPPN to a BufferedImage
	 *
	 * @param n the network used to process the image
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param inputMultiples array of multiples indicating whether to turn activation functions on or off
	 * @param time For animated images, the frame number (just use 0 for still images)
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight, double[] inputMultiples, double time) {
		return imageFromCPPN(n, imageWidth, imageHeight, inputMultiples, time, 1.0, 0.0, 0.0, 0.0);
	}
	
	/**
	 * Draws the image created by the CPPN to a BufferedImage.
	 * Same as the BufferedImage method containing the time parameter
	 * above, but has two new parameters, scale and rotation to
	 * be used for scaling and rotating the image.
	 * 
	 * @param n the network used to process teh image
	 * @param imageWidth width of the image
	 * @param imageHeight height of the image
	 * @param inputMultiples array of multiples indicating whether to turn activation functions on or off
	 * @param time For animated images, the frame number (just use 0 for still images)
	 * @param scale scale factor by which to scale the image
	 * @param rotation the degree in radians by which to rotate the image
	 * @param deltaX X coordinate of the center of the box
	 * @param deltaY Y coordinate of the center of the box
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight, double[] inputMultiples, double time, double scale, double rotation, double deltaX, double deltaY) {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		// Min and max brightness levels used for stark coloring
		float maxB = 0;
		float minB = 1;
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {				
				// network outputs computed on hsb, not rgb scale because
				float[] hsb = getHSBFromCPPN(n, x, y, imageWidth, imageHeight, inputMultiples, time, scale, rotation, deltaX, deltaY);
				//System.out.println(Arrays.toString(hsb));
				maxB = Math.max(maxB, hsb[BRIGHTNESS_INDEX]);
				minB = Math.min(minB, hsb[BRIGHTNESS_INDEX]);
				if(Parameters.parameters.booleanParameter("blackAndWhitePicbreeder")) { // black and white
					Color childColor = Color.getHSBColor(0, 0, hsb[BRIGHTNESS_INDEX]);
					// set back to RGB to draw picture to JFrame
					image.setRGB(x, y, childColor.getRGB());
				} else { // Original Picbreeder color encoding
					Color childColor = Color.getHSBColor(hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX]);
					// set back to RGB to draw picture to JFrame
					image.setRGB(x, y, childColor.getRGB());
				}
			}
		}
		
		// From Sarah F. Anna K., and Alice Q.
		if(Parameters.parameters.booleanParameter("starkPicbreeder")) {
			// Restricts image to two brightness levels
			float midB = (maxB+minB)/2; //  Midpoint
			for (int x = 0; x < imageWidth; x++) {// scans across whole image
				for (int y = 0; y < imageHeight; y++) {
					// Rather the use the CPPN, grab colors from the image and change the brightness
					int originalColor = image.getRGB(x, y);
					Color original = new Color(originalColor);
					// Get HSB values (null means a new array is created)
					float[] hsb = Color.RGBtoHSB(original.getRed(), original.getGreen(), original.getBlue(), null);
					// Change brightness to 0 or 1 in comparison with mid value
					Color childColor = Color.getHSBColor(hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX] > midB ? 1 : 0);
					image.setRGB(x, y, childColor.getRGB());
				}
			}		
		}
		
		return image;
	}

	/**
	 * Code from Sarah Friday, Anna Krolikowski, and Alice Quintanilla from their final Spring 2019 AI project.
	 * 
	 * Creates a zentangled image by overlaying two patterns into the black and white areas of the
	 * background image.
	 * 
	 * @param backgroundImage Image into which zentangle will be applied
	 * @param pattern1 First zentangle pattern
	 * @param pattern2 Second zentangle pattern
	 * @return The resulting image
	 */
	public static BufferedImage zentangleImages(BufferedImage backgroundImage, BufferedImage pattern1, BufferedImage pattern2) {
		int imageWidth = backgroundImage.getWidth();
		int imageHeight = backgroundImage.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				if(backgroundImage.getRGB(x, y) == Color.BLACK.getRGB()) {
					image.setRGB(x, y, pattern1.getRGB(x, y));
				} else {
					image.setRGB(x, y, pattern2.getRGB(x, y));
				}
			}
		}
		return image;
	}
	
	/**
	 * Creates a zentangled image by overlaying two patterns into the black and white areas of the
	 * background image.  Contains an extra zentangle pattern as a parameter. 
	 * 
	 * @param backgroundImage Image into which zentangle will be applied
	 * @param pattern1 First zentangle pattern
	 * @param pattern2 Second zentangle pattern
	 * @param pattern3 Third zentangle pattern
	 * @return The resulting image
	 */
	// Apparently this is never used!?
//	public static BufferedImage zentangleImages(BufferedImage backgroundImage, BufferedImage pattern1, BufferedImage pattern2, BufferedImage pattern3) {
//		int imageWidth = backgroundImage.getWidth();
//		int imageHeight = backgroundImage.getHeight();
//		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
//		for (int x = 0; x < imageWidth; x++) {// scans across whole image
//			for (int y = 0; y < imageHeight; y++) {
//				if((backgroundImage.getRGB(x, y) == Color.BLACK.getRGB() && pattern1.getRGB(x, y) == Color.BLACK.getRGB()) || (backgroundImage.getRGB(x, y) == Color.WHITE.getRGB() && pattern1.getRGB(x, y) == Color.WHITE.getRGB())) {
//					image.setRGB(x, y, pattern2.getRGB(x, y));
//				} else /*if((backgroundImage.getRGB(x,  y) == Color.BLACK.getRGB() && pattern1.getRGB(x,  y) == Color.WHITE.getRGB()) || (backgroundImage.getRGB(x,  y) == Color.WHITE.getRGB() && pattern1.getRGB(x,  y) == Color.BLACK.getRGB()))*/{
//					image.setRGB(x, y, pattern3.getRGB(x, y));
//				}
//			}
//		}
//		return image;
//	}
	
	/**
	 * Creates a zentangled image. Overlays two images to create three pattern regions:
	 * black in both, black in one and non-black in the other, and non-black in both.
	 * Different pattern images are applied to each region.
	 * 
	 * @param backgroundImage1 First background template
	 * @param backgroundImage2 Second background template
	 * @param pattern1 First zentangle pattern (for black+black regions)
	 * @param pattern2 Second zentangle pattern (for black+non-black regions)
	 * @param pattern3 Third zentangle pattern (for non-black+non-black regions)
	 * @return The resulting image
	 */
	public static BufferedImage zentangleImages(BufferedImage backgroundImage1, BufferedImage backgroundImage2, BufferedImage pattern1, BufferedImage pattern2, BufferedImage pattern3) {
		int imageWidth = backgroundImage1.getWidth();
		int imageHeight = backgroundImage1.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				if((backgroundImage1.getRGB(x, y) == Color.BLACK.getRGB() && backgroundImage2.getRGB(x, y) == Color.BLACK.getRGB())) {
					image.setRGB(x, y, pattern1.getRGB(x, y));
				} else if((backgroundImage1.getRGB(x,  y) == Color.BLACK.getRGB() && backgroundImage2.getRGB(x,  y) != Color.BLACK.getRGB()) || (backgroundImage1.getRGB(x,  y) != Color.BLACK.getRGB() && backgroundImage2.getRGB(x,  y) == Color.BLACK.getRGB())){
					image.setRGB(x, y, pattern2.getRGB(x, y));
				} else {
					image.setRGB(x,  y, pattern3.getRGB(x, y));
				}
			}
		}
		return image;
	}
	
	/**
	 * Creates a zentangled image. Overlays two images to create four pattern regions:
	 * black in both, black in one and non-black in the other, and non-black in both.
	 * Different pattern images are applied to each region.
	 * 
	 * @param backgroundImage1 First background template
	 * @param backgroundImage2 Second background template
	 * @param pattern1 First zentangle pattern (for black+black regions)
	 * @param pattern2 Second zentangle pattern (for black+non-black regions)
	 * @param pattern3 Third zentangle pattern (for non-black+non-black regions)
	 * @param pattern4 Fourth zentangle pattern (
	 * @return The resulting image
	 */
	public static BufferedImage zentangleImages(BufferedImage backgroundImage1, BufferedImage backgroundImage2, BufferedImage pattern1, BufferedImage pattern2, BufferedImage pattern3, BufferedImage pattern4) {
		int imageWidth = backgroundImage1.getWidth();
		int imageHeight = backgroundImage1.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				if((backgroundImage1.getRGB(x, y) == Color.BLACK.getRGB() && backgroundImage2.getRGB(x, y) == Color.BLACK.getRGB())) {
					image.setRGB(x, y, pattern1.getRGB(x, y));
				} else if((backgroundImage1.getRGB(x,  y) == Color.BLACK.getRGB() && backgroundImage2.getRGB(x,  y) != Color.BLACK.getRGB())){
					image.setRGB(x, y, pattern2.getRGB(x, y));
				} else if((backgroundImage1.getRGB(x,  y) != Color.BLACK.getRGB() && backgroundImage2.getRGB(x,  y) == Color.BLACK.getRGB())) {
					image.setRGB(x, y, pattern3.getRGB(x, y));
				} else {
					image.setRGB(x, y, pattern4.getRGB(x, y));
				}
			}
		}
		return image;
	}
	
	/**
	 * Returns adjusted image based on manipulation of an input image with a CPPN. To add
	 * more variation, each pixel is manipulated based on the average HSB of its surrounding pixels.
	 * 
	 * @param n CPPN
	 * @param img input image being "remixed"
	 * @param inputMultiples array of multiples indicating whether to turn activation functions on or off
	 * @param remixWindow size of window being adjusted
	 * @return BufferedImage representation of adjusted image
	 */
	public static BufferedImage remixedImageFromCPPN(Network n, BufferedImage img, double[] inputMultiples, int remixWindow) {
		//initialize new image
		BufferedImage remixedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		int loopWindow = remixWindow/2; //ensures that pixel is in center

		float[][][] sourceHSB = new float[img.getWidth()][img.getHeight()][];

		for(int x = 0; x < img.getWidth(); x++) {
			for(int y = 0; y < img.getHeight(); y++) {
				//get HSB from input image
				float totalH = 0;
				float totalS = 0;
				float totalB = 0;
				int count = 0;
				// loop through all pixels in surrounding window of current pixel to add up the 
				// average hue, saturation, and brightness. The average HSB is taken and applied
				// to the remixed image
				for(int windowX = x-loopWindow; windowX < x + loopWindow; windowX++) {
					if(windowX >= 0 && windowX < img.getWidth()) { //if current x-coordinate is within image bounds
						for(int windowY = y-loopWindow; windowY < y + loopWindow; windowY++) {
							if(windowY >= 0 && windowY < img.getHeight()) { //if current y-coordinate is within image bounds
								if(windowX >= 0 && windowX < img.getWidth()) {
									if(sourceHSB[windowX][windowY] == null) sourceHSB[windowX][windowY] = getHSBFromImage(img, windowX, windowY);
									totalH += sourceHSB[windowX][windowY][HUE_INDEX];
									totalS += sourceHSB[windowX][windowY][SATURATION_INDEX];
									totalB += sourceHSB[windowX][windowY][BRIGHTNESS_INDEX];

									count++;
								}
							}
						}
					}
				}
				//calculate average HSB after querying surrounding pixels
				float avgH = totalH/count;
				float avgS = totalS/count;
				float avgB = totalB/count;
				float[] queriedHSB = new float[]{avgH, avgS, avgB};
				//scale point for CPPN input
				ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), img.getWidth(), img.getHeight());
				double[] remixedInputs = { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * SQRT2, queriedHSB[HUE_INDEX], queriedHSB[SATURATION_INDEX], queriedHSB[BRIGHTNESS_INDEX], BIAS };
				// Multiplies the inputs of the pictures by the inputMultiples; used to turn on or off the effects in each picture
				for(int i = 0; i < inputMultiples.length; i++) {
					remixedInputs[i] = remixedInputs[i] * inputMultiples[i];
				}			
				n.flush(); // erase recurrent activation
				float[] hsb = rangeRestrictHSB(n.process(remixedInputs));
				Color childColor = Color.getHSBColor(hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX]);
				// set back to RGB to draw picture to JFrame
				remixedImage.setRGB(x, y, childColor.getRGB());
			}
		}
		return remixedImage;
	}
	
	/**
	 * Alternative approach to remixing an image from a CPPN. This version, instead of averaging all HSBs across a window, 
	 * reads in the individual HSBs of evenly spaced out points across a window and uses them. This method produced some
	 * interesting results (it created lots of static that looked similar to brush strokes, could translate the image, and 
	 * distorted it more than the original method). However, it wasn't as aesthetically pleasing as the original approach and 
	 * slowed the interface down a lot. Keeping it here for future reference.
	 * 
	 * @param n CPPN
	 * @param img input image being "remixed"
	 * @param inputMultiples array of multiples indicating whether to turn activation functions on or off
	 * @param remixWindow size of window being adjusted
	 * @param remixSamplesPerDimension number of samples being taken in the window
	 * @return BufferedImage representation of adjusted image
	 */
//	public static BufferedImage remixedImageFromCPPN(Network n, BufferedImage img, double[] inputMultiples, int remixWindow, int remixSamplesPerDimension) {
//		int spaceBetweenPixels = remixWindow/(remixSamplesPerDimension-1);
//		int loopWindow = remixWindow/2; //ensures that pixel is in center
//		BufferedImage remixedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
//		float[][][] sourceHSB = new float[img.getWidth()][img.getHeight()][]; //use 3d array to keep track of calculated inputs at each coordinate
//		
//		
//		for(int x = 0; x < img.getWidth(); x++) {
//			for(int y = 0; y < img.getHeight(); y++) { //loop through all pixels of image
//				
//				double[] inputs = new double[4+remixSamplesPerDimension*remixSamplesPerDimension*NUM_HSB];
//				ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), img.getWidth(), img.getHeight());
//				inputs[0] = scaled.getX();
//				inputs[1] = scaled.getY();
//				inputs[2] = scaled.distance(new Tuple2D(0, 0)) * SQRT2;
//				
//				int windowX = x - loopWindow;
//				int windowY = y - loopWindow;
//				for(int i = 0; i < remixSamplesPerDimension; i++) {
//					int currentX = windowX + i*spaceBetweenPixels;
//					if(currentX >= 0 && currentX < img.getWidth()) { //if current location is within bounds of image
//						for(int j = 0; j < remixSamplesPerDimension; j++) {
//							int currentY = windowY + j*spaceBetweenPixels;
//							if(currentY >= 0 && currentY < img.getHeight()) { //if current location is within bounds of image
//								if(sourceHSB[currentX][currentY] == null) sourceHSB[currentX][currentY] = getHSBFromImage(img, currentX, currentY);
//								int inputIndex = 3 + (i*remixSamplesPerDimension) + (j*NUM_HSB);
//								//save HSB values from current point to respective indexes in inputs array
//								inputs[inputIndex+HUE_INDEX] = sourceHSB[currentX][currentY][HUE_INDEX];
//								inputs[inputIndex+SATURATION_INDEX] = sourceHSB[currentX][currentY][SATURATION_INDEX];
//								inputs[inputIndex+BRIGHTNESS_INDEX] = sourceHSB[currentX][currentY][BRIGHTNESS_INDEX];
//							}
//						}
//					}
//				}
//				
//				inputs[inputs.length-1] = BIAS;
//								
//				
//				// set image pixel
//				// Multiplies the inputs of the pictures by the inputMultiples; used to turn on or off the effects in each picture
//				for(int i = 0; i < inputMultiples.length; i++) {
//					inputs[i] = inputs[i] * inputMultiples[i];
//				}			
//				n.flush(); // erase recurrent activation
//				float[] hsb = rangeRestrictHSB(n.process(inputs));
//				Color childColor = Color.getHSBColor(hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX]);
//				// set back to RGB to draw picture to JFrame
//				remixedImage.setRGB(x, y, childColor.getRGB());
//			}
//		}
//		return remixedImage;
//	}

	/**
	 * Accesses HSB at a specific pixel in a BufferedImage. Does so by accessing the 
	 * RGB first and then creating a Color class instance to convert the components of 
	 * the RGB to HSB. Creates an array of floats that numerically represent the hue,
	 * saturation, and brightness of the pixel. 
	 * 
	 * @param img Image containing pixel
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return array of floats representing hue, saturation, and brightness of pixel
	 */
	private static float[] getHSBFromImage(BufferedImage img, int x, int y) {
		int RGB = img.getRGB(x, y);
		Color c = new Color(RGB, true);
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		float[] HSB = Color.RGBtoHSB(r, g, b, null);
		return HSB;
	}
	
	public static float getBrightnessFromImage(BufferedImage image, int x, int y) {
		float[] tempResult = getHSBFromImage(image, x, y);
		return tempResult[BRIGHTNESS_INDEX];
	}

	/**
	 * Gets HSB outputs from the CPPN in question from the CPPN
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param deltaX X coordinate of the center of the box
	 * @param deltaY Y coordinate of the center of the box
	 * @return double containing the HSB values
	 */
	public static float[] getHSBFromCPPN(Network n, int x, int y, int imageWidth, int imageHeight, double[] inputMultiples, double time, double scale, double rotation, double deltaX, double deltaY) {

		double[] input = get2DObjectCPPNInputs(x, y, imageWidth, imageHeight, time, scale, rotation, deltaX, deltaY);

		// Multiplies the inputs of the pictures by the inputMultiples; used to turn on or off the effects in each picture
		for(int i = 0; i < inputMultiples.length; i++) {
			input[i] = input[i] * inputMultiples[i];
		}

		// Eliminate recurrent activation for consistent images at all resolutions
		n.flush();
		return rangeRestrictHSB(n.process(input));
	}

	/**
	 * Given the direct HSB values from the CPPN (a double array), convert to a
	 * float array (required by Color methods) and do range restriction on
	 * certain values.
	 * 
	 * These range restrictions were stolen from Picbreeder code on GitHub
	 * (though not the original code), but 2 in 13 randomly mutated networks
	 * still produce boring black screens. Is there a way to fix this?
	 * 
	 * @param hsb array of HSB color information from CPPN
	 * @return scaled HSB information in float array
	 */
	public static float[] rangeRestrictHSB(double[] hsb) {
		if(Parameters.parameters.booleanParameter("standardPicBreederHSBRestriction")) {
			// This is the original Picbreeder code that I have used for a while, but even the comment
			// above (from long ago) indicates problems with saturation. These are exacerbated with the
			// enhanced genotypes (especially when scale change is allowed), hence the alternative below.
			return new float[] { 
					(float) FullLinearPiecewiseFunction.fullLinear(hsb[HUE_INDEX]),
					(float) HalfLinearPiecewiseFunction.halfLinear(hsb[SATURATION_INDEX]),
					(float) Math.abs(FullLinearPiecewiseFunction.fullLinear(hsb[BRIGHTNESS_INDEX])) 
			};
		} else {
			// This new code is a possible alternative for enhanced genotypes
			return new float[] { 
					(float) ActivationFunctions.fullSawtooth(hsb[HUE_INDEX],2),	// Wraps around the Hue cylinder
					(float) Math.abs(FullLinearPiecewiseFunction.fullLinear(hsb[SATURATION_INDEX])), // Smooth out transition from positive to negative
					(float) Math.abs(FullLinearPiecewiseFunction.fullLinear(hsb[BRIGHTNESS_INDEX])) // No change
			};
		}
	}

	/**
	 * Gets scaled inputs to send to CPPN, using default scale of 1.0 
	 * and default rotation of 0.0 (no rotation and no scaling).
	 *
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param time For animated images, the frame number (just use 0 for still images)
	 * @return array containing inputs for CPPN
	 */
	public static double[] get2DObjectCPPNInputs(int x, int y, int imageWidth, int imageHeight, double time) {
		return get2DObjectCPPNInputs(x,y,imageWidth,imageHeight,time,1.0, 0.0, 0.0, 0.0);
	}
	
	/**
	 * Gets scaled inputs to send to CPPN, using default scale of 1.0 
	 * and default rotation of 0.0 (no rotation and no scaling).  Same 
	 * as the get2DObjectCPPNInputs method above but contains two new 
	 * parameters, scale and rotation, used for rotating and scaling
	 * the image.
	 * 
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param time For animated images, the frame number (just use 0 for still images)
	 * @param scale scale factor by which to scale the image
	 * @param rotation the degree in radians by which to rotate the image
	 * @param deltaX X coordinate of the center of the box
	 * @param deltaY Y coordinate of the center of the box
	 * @return array containing inputs for CPPN
	 */
	public static double[] get2DObjectCPPNInputs(int x, int y, int imageWidth, int imageHeight, double time, double scale, double rotation, double deltaX, double deltaY) {
		Tuple2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), imageWidth, imageHeight);
		scaled = scaled.mult(scale);
		scaled = scaled.rotate(rotation);
		ILocated2D finalPoint = scaled.add(new Tuple2D (deltaX, deltaY));
		if(time == -1) { // default, single image. Do not care about time
			return new double[] { finalPoint.getX(), finalPoint.getY(), finalPoint.distance(new Tuple2D(0, 0)) * SQRT2, BIAS };
		} else { // TODO: May need to divide time by frame rate later
			return new double[] { finalPoint.getX(), finalPoint.getY(), finalPoint.distance(new Tuple2D(0, 0)) * SQRT2, time, BIAS };
		}
	}

	/**
	 * method for drawing an image onto a drawing panel
	 *
	 * @param image image to draw
	 * @param label name of image
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @return the drawing panel with the image
	 */
	public static DrawingPanel drawImage(BufferedImage image, String label, int imageWidth, int imageHeight) {
		DrawingPanel parentPanel = new DrawingPanel(imageWidth, imageHeight, label);
		Graphics2D parentGraphics = parentPanel.getGraphics();
		parentGraphics.drawRenderedImage(image, null);
		return parentPanel;
	}

	/**
	 * Creates an image of the specified size and height consisting entirely
	 * of a designated solid color.
	 * 
	 * @param c Color throughout image
	 * @param width width of image in pixels
	 * @param height height of image in pixels
	 * @return BufferedImage in solid color
	 */
	public static BufferedImage solidColorImage(Color c, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {// scans across whole image
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, c.getRGB());
			}
		}
		return image;
	}
	
	/**
	 * Converts an image into a flat double array of features, where the features
	 * are the HSB values of each pixel.  Uses RGB for colored images.
	 * 
	 * @param image A BufferedImage
	 * @return double array of all HSB values of each pixel.
	 */
	public static double[] flatFeatureArrayFromBufferedImage(BufferedImage image) {
		int numberOfChannels = Parameters.parameters.booleanParameter("blackAndWhitePicbreeder")? 1: NUM_HSB;
		double[] result = new double[image.getHeight() * image.getWidth() * numberOfChannels];
		int resultIndex = 0;
		// If the image is black and white use brightness
		if(Parameters.parameters.booleanParameter("blackAndWhitePicbreeder")) {
			for(int x = 0; x < image.getWidth(); x++) {
				for(int y = 0; y < image.getHeight(); y++) {
					float[] hsb = getHSB(image, x, y);
					result[resultIndex++] = hsb[BRIGHTNESS_INDEX];
				}
			}
		} else {
			for(int y = 0; y < image.getHeight(); y++) {
				for(int x = 0; x < image.getWidth(); x++) {
					// Copy RGB values over as features
					Color c = new Color(image.getRGB(x, y));
					float[] rgb = c.getRGBColorComponents(null);
					result[resultIndex] = rgb[0]; // Magic number for red
					result[resultIndex+(image.getWidth()*image.getHeight())] = rgb[1]; // Magic number for green
					result[resultIndex+(2*image.getWidth()*image.getHeight())] = rgb[2]; // Magic number for blue
					resultIndex++;
				}
			}
		}
		return result;
	}

	/**
	 * GEt HSB values at specific x/y coordinates in an image
	 * 
	 * @param image Image to get pixel from
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return three-element HSB array corresponding to pixel
	 */
	public static float[] getHSB(BufferedImage image, int x, int y) {
		int pixelRGB = image.getRGB(x, y);
		Color original = new Color(pixelRGB);
		// Get HSB values (null means a new array is created)
		float[] hsb = Color.RGBtoHSB(original.getRed(), original.getGreen(), original.getBlue(), null);
		return hsb;
	}
	
	/**
	 * Takes an INDArray containing an image loaded using the native image loader
	 * libraries associated with DL4J, and converts it into a BufferedImage.
	 * The INDArray contains the color values split up across three channels (RGB)
	 * and in the integer range 0-255.
	 * @param array INDArray containing an image
	 * @return BufferedImage 
	 */
	public static BufferedImage imageFromINDArray(INDArray array) {
		long[] shape = array.shape();
		// Should the order of these be switched?
		int width = (int) shape[2];
		int height = (int) shape[3];
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Copy from INDArray to BufferedImage
		for (int x = 0; x < width; x++) {// scans across whole image
			for (int y = 0; y < height; y++) {
				int red = array.getInt(0,2,y,x);
				int green = array.getInt(0,1,y,x);
				int blue = array.getInt(0,0,y,x);
				
				// There is a risk of colors going out of acceptable bounds when doing the neural style transfer.
				// The clipping here prevents that.
				red = Math.min(red, 255);
				green = Math.min(green, 255);
				blue = Math.min(blue, 255);

				red = Math.max(red, 0);
				green = Math.max(green, 0);
				blue = Math.max(blue, 0);
				
				image.setRGB(x, y, new Color(red,green,blue).getRGB());
			}
		}
		return image;
	}

	/**
	 * Plots line of a designated color drawn by an input array list of doubles on a drawing panel.
	 * 
	 * @param panel DrawingPanel
	 * @param min minimum value of score
	 * @param max maximum value of score
	 * @param scores list of doubles to be plotted on graph
	 * @param color Color of line
	 */
	public static void linePlot(DrawingPanel panel, double min, double max, ArrayList<Double> scores, Color color) {
		Graphics g = panel.getGraphics();
		int height = panel.getFrame().getHeight() - 50; // -50 is to avoid gray panel at bottom of DrawingPanel
		int width = panel.getFrame().getWidth();		
		linePlot(g, min, max, height, width, scores, color); // calls secondary linePlot method after necessary info is defined
	}

	/**
	 * Creates an image, sets the background to be white, and plots a line on the image.
	 * 
	 * @param height of image
	 * @param width of image
	 * @param min score for scaling
	 * @param max score for scaling
	 * @param scores list of doubles being plotted
	 * @param color of line being plotted
	 * @return BufferedImage formed from plotted line
	 */
	public static BufferedImage linePlotImage(int height, int width, double min, double max, ArrayList<Double> scores, Color color) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		linePlot(g, min, max, height, width, scores, color);
		return bi;
	}

	/**
	 * Plots a line of a designated color on a graphics image using an input array list of doubles
	 * 
	 * @param g input graphics to be manipulated
	 * @param min minimum value of score
	 * @param max maximum value of score
	 * @param height height of image being created
	 * @param width width of image being created
	 * @param scores list of doubles to be plotted on graph
	 * @param color Color of line
	 */
	public static void linePlot(Graphics g, double min, double max, int height, int width, ArrayList<Double> scores, Color color) {
		g.setColor(Color.black);
		// y-axis
		g.drawLine(Plot.OFFSET, Plot.OFFSET, Plot.OFFSET, height - Plot.OFFSET);	
		// x-axis
		g.drawLine(Plot.OFFSET, height - Plot.OFFSET, width - Plot.OFFSET, height - Plot.OFFSET);
		double last = scores.get(0);
		double maxRange = Math.max(max, max - min);
		double lowerMin = Math.min(0, min);
		for (int i = 1; i < scores.size(); i++) {
			g.setColor(color);
			// g.fillRect(OFFSET + scale((double) i, (double) scores.size()),
			// OFFSET + invert(scores.get(i), max), 1, 1);
			int x1 = Plot.OFFSET + scale((double) (i - 1), (double) scores.size(), 0, width);
			int y1 = Plot.OFFSET + invert(last, maxRange, lowerMin, height);
			int x2 = Plot.OFFSET + scale((double) i, (double) scores.size(), 0, width);
			int y2 = Plot.OFFSET + invert(scores.get(i), maxRange, lowerMin, height);

			//System.out.println(x1+","+ y1+","+ x2+","+ y2);
			g.drawLine(x1, y1, x2, y2);
			g.setColor(Color.black);
			last = scores.get(i);
		}
		g.drawString("" + max, Plot.OFFSET / 2, Plot.OFFSET / 2);
		g.drawString("" + lowerMin, Plot.OFFSET / 2, height - (Plot.OFFSET / 2));
	}

	/**
	 * Creates a graphed visualization of an audio file by taking in the file represented as a list of doubles and 
	 * plotting it using a DrawingPanel.
	 * 
	 * @param fileName String reference to file being plotted
	 */
	public static void wavePlotFromFile(String fileName, int height, int width) {
		double[] fileArray = SoundToArray.read(fileName); //create array of doubles representing audio
		wavePlotFromDoubleArray(fileArray, height, width);
	}

	/**
	 * Creates a graphed visualization of an audio file by taking in the list of doubles that represents the file and 
	 * plotting it using a DrawingPanel.
	 * 
	 * @param inputArray
	 */
	public static BufferedImage wavePlotFromDoubleArray(double[] inputArray, int height, int width) {
		ArrayList<Double> fileArrayList = ArrayUtil.doubleVectorFromArray(inputArray); //convert array into array list
		BufferedImage wavePlot = linePlotImage(height, width, -1.0, 1.0, fileArrayList, Color.black);
		return wavePlot;
	}

	/**
	 * Scales x value based on maximum and minimum value. This scale method is based on original browser 
	 * dimension, which is meant for evolution lineage/Ms. Pacman. 
	 * 
	 * @param x Input value
	 * @param max maximum x value
	 * @param min minimum x value
	 * @return scaled x value
	 */
	public static int scale(double x, double max, double min) {
		return scale(x, max, min, Plot.BROWSE_DIM);
	}

	/**
	 * Scales x value based on maximum and minimum value. This scale method is more generalized and 
	 * was created specifically for plotting sound waves in StdAudio. 
	 * 
	 * @param x Input value
	 * @param max maximum value
	 * @param min minimum value
	 * @return scaled x value
	 */
	public static int scale(double x, double max, double min, int totalWidth) {
		return (int) (((x - min) / max) * (totalWidth - (2 * Plot.OFFSET)));
	}

	/**
	 * Inverts y value based on maximum and minimum value to fit graphical x/y proportions. 
	 * This method is the original invert method intended for evolution lineage/Ms. Pacman. 
	 * 
	 * @param y Input value
	 * @param max maximum value
	 * @param min minimum value
	 * @return scaled x value
	 */
	public static int invert(double y, double max, double min) {
		throw new UnsupportedOperationException("This method lacks an appropriate definition");
		//return invert(y,max,min);
	}

	/**
	 * Inverts y value based on maximum and minimum value to fit graphical x/y proportions. 
	 * This method is a secondary invert method created for plotting sound waves in StdAudio
	 *  
	 * @param y Input value
	 * @param max maximum value
	 * @param min minimum value
	 * @return scaled x value
	 */
	public static int invert(double y, double max, double min, int totalHeight) {
		return (totalHeight - (2 * Plot.OFFSET)) - scale(y, max, min, totalHeight);
	}
	
	/**
	 * Converts a given Image into a BufferedImage
	 * Thanks to https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage/13605411
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

	/**
	 * Converts an image to be a BufferedImage.
	 * 
	 * @param img The image to be converted
	 * @return img as a BufferedImage
	 */
	public static BufferedImage convertToBufferedImage(Image img) {
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
		return bimage;
	}
	
	/**
	 * Rotates the input image.
	 * Source code taken from the following link:
	 * https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
	 * 
	 * @param img Image to be rotated
	 * @param angle	Angle to be rotated by
	 * @return Return the rotated image
	 */
	public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
	    double rads = Math.toRadians(angle);
	    double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
	    int w = img.getWidth();
	    int h = img.getHeight();
	    int newWidth = (int) Math.floor(w * cos + h * sin);
	    int newHeight = (int) Math.floor(h * cos + w * sin);

	    BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = rotated.createGraphics();
	    AffineTransform at = new AffineTransform();
	    at.translate((newWidth - w) / 2, (newHeight - h) / 2);

	    int x = w / 2;
	    int y = h / 2;

	    at.rotate(rads, x, y);
	    g2d.setTransform(at);
	    // Changed the ImageObserver to null
	    g2d.drawImage(img, 0, 0, null);
	    g2d.dispose();

	    return rotated;
	}
	
	/**
	 * Creates a new image double the size of the original,
	 * and built out of four of the original image.
	 * 
	 * @param original The original image
	 * @return the new tile image
	 */
	public static BufferedImage getTwoByTwoTiledImage(BufferedImage original) {
		BufferedImage result = new BufferedImage(original.getWidth() * 2, original.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < original.getWidth(); x++) {
			for(int y = 0; y < original.getHeight(); y++) {
				int originalColor = original.getRGB(x, y);
				result.setRGB(x, y, originalColor);
				result.setRGB(x + original.getWidth(), y, originalColor);
				result.setRGB(x, y + original.getWidth(), originalColor);
				result.setRGB(x + original.getWidth(), y + original.getWidth(), originalColor);
			}
		}
		return result;
	}
	
	/**
	 * Rotates the background Zentangle image. Makes an image
	 * double the size of the original containing a two by 
	 * two of the original image.  Then takes the center of
	 * the double size image to use as the background image.
	 * 
	 * @param image The original image
	 * @param angle The angle by which to rotate the background image
	 * @return the background image
	 */
	public static BufferedImage extractCenterOfDoubledRotatedImage(BufferedImage image, double angle) {
		BufferedImage doubleSize = GraphicsUtil.getTwoByTwoTiledImage(image);
		BufferedImage rotated = GraphicsUtil.rotateImageByDegrees(doubleSize, angle);
		BufferedImage middleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int doubleMidWidth = doubleSize.getWidth() / 2;
		int doubleMidHeight = doubleSize.getHeight() / 2;
		int halfFinalWidth = middleImage.getWidth() / 2;
		int haldFinalHeight = middleImage.getHeight() / 2;
		int startX = doubleMidWidth - halfFinalWidth;
		int startY = doubleMidHeight - haldFinalHeight;
		for(int x = 0; x < middleImage.getWidth(); x++) {
			for(int y = 0; y < middleImage.getHeight(); y++) {
				middleImage.setRGB(x, y, rotated.getRGB(x + startX, y + startY));
			}
		}
		return middleImage;
	}
	
	/**
	 * Calculates the percent of matching pixels between 
	 * two Buffered images.  Pixels are considered matching
	 * if they contain the same RGB values.
	 * 
	 * @param firstImage first BufferedImage
	 * @param secondImage second BufferedImage
	 * @return the percent of matching pixels
	 */
	public static double percentMatchingPixels(BufferedImage firstImage, BufferedImage secondImage) {
		double numMatching = 0;
		assert firstImage.getWidth() == secondImage.getWidth() && firstImage.getHeight() == secondImage.getHeight(): "Image widths and heights need to match";
		for(int x = 0; x < firstImage.getWidth(); x++) {
			for(int y = 0; y < firstImage.getHeight(); y++) {
				if(firstImage.getRGB(x, y) == secondImage.getRGB(x, y)) {
					numMatching++;
				}
			}
		}
		double percentMatching = numMatching / (firstImage.getWidth() * firstImage.getHeight());
		
		return percentMatching;
	}

}
