package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * 
 * 
 * @author Anna Wicker
 *
 */

public class PictureFourQuadrantBrightnessBinLabels implements BinLabels {
	List<String> binLabels = null;
	private final int BINS_PER_DIMENSION;

	
//	private static final Pair<MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT> MAX_PIXEL;
	
	public PictureFourQuadrantBrightnessBinLabels() {
		this(10); // replace with command line parameter
	}

	public PictureFourQuadrantBrightnessBinLabels(int binsPerDimension) {
		BINS_PER_DIMENSION = binsPerDimension;
	}
	
	/**
	 * Creates the bin labels (coordinates corresponding
	 * to the correct bin).
	 * 
	 * @return List of bin labels as strings
	 */
	public List<String> binLabels() {
		if(binLabels == null) {
			int size = BINS_PER_DIMENSION * BINS_PER_DIMENSION;
			binLabels = new ArrayList<String>(size);
			for(int i = 0; i < BINS_PER_DIMENSION; i++) {
				for(int j = 0; j < BINS_PER_DIMENSION; j++) {
					for(int k = 0; k < BINS_PER_DIMENSION; k++) {
						for(int m = 0; m < BINS_PER_DIMENSION; m++) {
							// (i,j,k,m) <-- the coordinate of the current bin
							// (0,0,0,0) <-- should be the first bin
							// (BINS_PER_DIMENSION - 1,BINS_PER_DIMENSION - 1,BINS_PER_DIMENSION - 1,BINS_PER_DIMENSION - 1) <-- should be the last bin
							binLabels.add("(" + i + "," + j + "," + k + "," + m + ")");
						}
					}
				}
			}
		}
		
		return binLabels;
	}
	
	/**
	 * Creates an array of doubles corresponding to the brightness
	 * of each pixel in one of four quadrants in the image.
	 * 
	 * @param image Image being analyzed
	 * @return An array of doubles containing the sum of the brightness
	 * 		   of each pixel.
	 */
	public static double[] getQuadrantBehaviorCharacterization(BufferedImage image) {
		int quadWidth = image.getWidth() / 2;
		int quadHeight = image.getHeight() / 2;
		
		double q1 = sumQuadrantBrightnessValues(image, 0, 0, quadWidth, quadHeight);
		double q2 = sumQuadrantBrightnessValues(image, quadWidth, 0, quadWidth * 2, quadHeight);
		double q3 = sumQuadrantBrightnessValues(image, 0, quadHeight, quadWidth, quadHeight * 2);
		double q4 = sumQuadrantBrightnessValues(image, quadWidth, quadHeight, quadWidth * 2, quadHeight * 2);
		
		return new double[] {q1, q2, q3, q4};
	}

	/**
	 * Sums the brightness of each pixel in a quadrant starting
	 * at (quadXStart, quadYStart) and ending at (quadXEnd, quadYEnd).
	 * 
	 * @param image Image being analyzed
	 * @param quadXStart Starting X coordinate of the quadrant
	 * @param quadYStart Starting Y coordinate of the quadrant
	 * @param quadXEnd Ending X coordinate of the quadrant
	 * @param quadYEnd Ending Y coordinate of the quadrant
	 * @return The sum of the brightness of each pixel in the quadrant
	 */
	public static float sumQuadrantBrightnessValues(BufferedImage image, int quadXStart, int quadYStart, int quadXEnd, int quadYEnd) {
		float sumBrightness = 0;
		
		for(int x = quadXStart; x < quadXEnd; x++) {
			for(int y = quadYStart; y < quadYEnd; y++) {
				sumBrightness += GraphicsUtil.getBrightnessFromImage(image, x, y);
			}
		}
		
		return sumBrightness;
	}
	
	/**
	 * Takes the double array produced by getQuadrantBehaviorCharacterization
	 * and converts it into coordinates for a corresponding bin.
	 * 
	 * @param image The image being analyzed
	 * @return An int array containing 4 values corresponding to the current bin
	 */
	public int[] binCoordinates(BufferedImage image) {
		double[] behaviorCharacterization = getQuadrantBehaviorCharacterization(image);
		
		int[] binCoordinates = new int[behaviorCharacterization.length];
		
		double quadPixelCount = (image.getWidth() / 2) * (image.getHeight() / 2);
		
		for(int i = 0; i < binCoordinates.length; i++) {
			double original = behaviorCharacterization[i];
			binCoordinates[i] = (int) ((original / quadPixelCount) * BINS_PER_DIMENSION);
		}
		return binCoordinates;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		// TODO Auto-generated method stub
		return 0;
	}
}
