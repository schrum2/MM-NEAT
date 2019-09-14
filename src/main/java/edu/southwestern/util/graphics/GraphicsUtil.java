package edu.southwestern.util.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

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
	 * @param n
	 *            the network used to process the image
	 * @param imageWidth
	 *            width of image
	 * @param imageHeight
	 *            height of image
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight, double[] inputMultiples, double time) {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		// Min and max brightness levels used for stark coloring
		float maxB = 0;
		float minB = 1;
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {				
				// network outputs computed on hsb, not rgb scale because
				float[] hsb = getHSBFromCPPN(n, x, y, imageWidth, imageHeight, inputMultiples, time);
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
			float midB = (maxB-minB)/2;
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
	
	public static BufferedImage zentangleImages(BufferedImage backgroundImage, BufferedImage pattern1, BufferedImage pattern2, BufferedImage pattern3) {
		int imageWidth = backgroundImage.getWidth();
		int imageHeight = backgroundImage.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				if((backgroundImage.getRGB(x, y) == Color.BLACK.getRGB() && pattern1.getRGB(x, y) == Color.BLACK.getRGB()) || (backgroundImage.getRGB(x, y) == Color.WHITE.getRGB() && pattern1.getRGB(x, y) == Color.WHITE.getRGB())) {
					image.setRGB(x, y, pattern2.getRGB(x, y));
				} else /*if((backgroundImage.getRGB(x,  y) == Color.BLACK.getRGB() && pattern1.getRGB(x,  y) == Color.WHITE.getRGB()) || (backgroundImage.getRGB(x,  y) == Color.WHITE.getRGB() && pattern1.getRGB(x,  y) == Color.BLACK.getRGB()))*/{
					image.setRGB(x, y, pattern3.getRGB(x, y));
				}
			}
		}
		return image;
	}
	
	public static BufferedImage zentangleImages(BufferedImage backgroundImage, BufferedImage pattern1, BufferedImage pattern2, BufferedImage pattern3, BufferedImage pattern4) {
		int imageWidth = backgroundImage.getWidth();
		int imageHeight = backgroundImage.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				if((backgroundImage.getRGB(x, y) == Color.BLACK.getRGB() && pattern1.getRGB(x, y) == Color.BLACK.getRGB())) {
					image.setRGB(x, y, pattern2.getRGB(x, y));
				} else if((backgroundImage.getRGB(x,  y) == Color.BLACK.getRGB() && pattern1.getRGB(x,  y) != Color.BLACK.getRGB()) || (backgroundImage.getRGB(x,  y) != Color.BLACK.getRGB() && pattern1.getRGB(x,  y) == Color.BLACK.getRGB())){
					image.setRGB(x, y, pattern3.getRGB(x, y));
				} else {
					image.setRGB(x,  y, pattern4.getRGB(x, y));
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

	/**
	 * Gets HSB outputs from the CPPN in question
	 *
	 *            the CPPN
	 * @param x
	 *            x-coordinate of pixel
	 * @param y
	 *            y-coordinate of pixel
	 * @param imageWidth
	 *            width of image
	 * @param imageHeight
	 *            height of image
	 *
	 * @return double containing the HSB values
	 */
	public static float[] getHSBFromCPPN(Network n, int x, int y, int imageWidth, int imageHeight, double[] inputMultiples, double time) {

		double[] input = get2DObjectCPPNInputs(x, y, imageWidth, imageHeight, time);

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
	 * @param hsb
	 *            array of HSB color information from CPPN
	 * @return scaled HSB information in float array
	 */
	public static float[] rangeRestrictHSB(double[] hsb) {
		return new float[] { (float) FullLinearPiecewiseFunction.fullLinear(hsb[HUE_INDEX]),
				(float) HalfLinearPiecewiseFunction.halfLinear(hsb[SATURATION_INDEX]),
				(float) Math.abs(FullLinearPiecewiseFunction.fullLinear(hsb[BRIGHTNESS_INDEX])) };
	}

	/**
	 * Gets scaled inputs to send to CPPN
	 *
	 * @param x
	 *            x-coordinate of pixel
	 * @param y
	 *            y-coordinate of pixel
	 * @param imageWidth
	 *            width of image
	 * @param imageHeight
	 *            height of image
	 *
	 * @return array containing inputs for CPPN
	 */
	public static double[] get2DObjectCPPNInputs(int x, int y, int imageWidth, int imageHeight, double time) {
		ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), imageWidth, imageHeight);
		if(time == -1) { // default, single image. Do not care about time
			return new double[] { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * SQRT2, BIAS };
		} else { // TODO: May need to divide time by frame rate later
			return new double[] { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * SQRT2, time, BIAS };
		}
	}

	/**
	 * method for drawing an image onto a drawing panel
	 *
	 * @param image
	 *            image to draw
	 * @param label
	 *            name of image
	 * @param imageWidth
	 *            width of image
	 * @param imageHeight
	 *            height of image
	 *
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
	 * Takes an INDArray containing an image loaded using the native image loader
	 * libraries associated with DL4J, and converts it into a BufferedImage.
	 * The INDArray contains the color values split up across three channels (RGB)
	 * and in the integer range 0-255.
	 * @param array INDArray containing an image
	 * @return BufferedImage 
	 */
//	public static BufferedImage imageFromINDArray(INDArray array) {
//		int[] shape = array.shape();
//		// Should the order of these be switched?
//		int width = (int) shape[2];
//		int height = (int) shape[3];
//		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		// Copy from INDArray to BufferedImage
//		for (int x = 0; x < width; x++) {// scans across whole image
//			for (int y = 0; y < height; y++) {
//				int red = array.getInt(0,2,y,x);
//				int green = array.getInt(0,1,y,x);
//				int blue = array.getInt(0,0,y,x);
//				
//				// There is a risk of colors going out of acceptable bounds when doing the neural style transfer.
//				// The clipping here prevents that.
//				red = Math.min(red, 255);
//				green = Math.min(green, 255);
//				blue = Math.min(blue, 255);
//
//				red = Math.max(red, 0);
//				green = Math.max(green, 0);
//				blue = Math.max(blue, 0);
//				
//				image.setRGB(x, y, new Color(red,green,blue).getRGB());
//			}
//		}
//		return image;
//	}

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
	 *  Creates a graphed visualization of an audio file by taking in the list of doubles that represents the file and 
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
		return invert(y,max,min);
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
}
