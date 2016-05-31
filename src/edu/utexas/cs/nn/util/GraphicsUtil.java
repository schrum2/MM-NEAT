package edu.utexas.cs.nn.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

public class GraphicsUtil {
	
	private static final int HUE_INDEX = 0;
	private static final int SATURATION_INDEX = 1;
	private static final int BRIGHTNESS_INDEX = 2;
	private static final double BIAS = 1.0;// a common input used in neural
	// networks
private static final double SQRT2 = Math.sqrt(2); // Used for scaling
				// distance from center
	/**
	 * Draws the image created by the CPPN to a BufferedImage
	 *
	 * @param n
	 *            the network used to process the imag
	 * @param imageWidth
	 *            width of image
	 * @param imageHeight
	 *            height of image
	 * @return buffered image containing image drawn by network
	 */
	public static BufferedImage imageFromCPPN(Network n, int imageWidth, int imageHeight) {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {// scans across whole image
			for (int y = 0; y < imageHeight; y++) {
				float[] hsb = getHSBFromCPPN(n, x, y, imageWidth, imageHeight);
				// network outputs computed on hsb, not rgb scale because
				// creates better images
				Color childColor = Color.getHSBColor(hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX]);
				image.setRGB(x, y, childColor.getRGB());// set back to RGB to
														// draw picture to
														// JFrame
			}
		}
		return image;
	}
	/**
	 * Gets HSB outputs from the CPPN in question
	 *
	 * @param n
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
	public static float[] getHSBFromCPPN(Network n, int x, int y, int imageWidth, int imageHeight) {
		double[] input = getCPPNInputs(x, y, imageWidth, imageHeight);
		((TWEANN) n).flush();
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
		return new float[] { (float) hsb[HUE_INDEX],
				// (float) hsb[SATURATION_INDEX],
				(float) ActivationFunctions.halfLinear(hsb[SATURATION_INDEX]),
				// (float) hsb[BRIGHTNESS_INDEX]};
				(float) Math.abs(hsb[BRIGHTNESS_INDEX]) };
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
	public static double[] getCPPNInputs(int x, int y, int imageWidth, int imageHeight) {
		ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), imageWidth, imageHeight);
		return new double[] { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * SQRT2, BIAS };
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

}
