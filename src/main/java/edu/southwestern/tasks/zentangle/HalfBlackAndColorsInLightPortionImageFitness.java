package edu.southwestern.tasks.zentangle;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Like HalfBlackAndColorsImageFitness, but with a scale so that color only
 * matters in the non-black regions.
 * 
 * @author Jacob Schrum
 */
public class HalfBlackAndColorsInLightPortionImageFitness implements ImageFitness {

	// Not sure why this is true, but it is
	private static final int BLACK_VALUE = -16777216;
	
	@Override
	public double[] fitness(BufferedImage image) {
		int blackCount = 0;
		int redSum = 0;
		int greenSum = 0;
		int blueSum = 0;
		
		int width = image.getWidth();
		int height = image.getHeight();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				//System.out.print("("+x+","+y+":"+image.getRGB(x, y)+")");
				int color = image.getRGB(x, y);
				if(color == BLACK_VALUE) {
					blackCount++;
				} else {
					Color c = new Color(color);
					redSum += c.getRed();
					blueSum += c.getBlue();
					greenSum += c.getGreen();
				}
			}
			//System.out.println();
		}
		
		double total = width * height;
		// Percentage of black pixels
		double percent = blackCount/total;
		// How far from perfect 50/50 split
		double distFrom50 = Math.abs(percent - 0.5);
		// Negate because closer to 50 is better. Best possible fitness is 0
		
		// Only get color percentage within the non-black region
		double notBlackCount = total - blackCount;
		
		double redWithoutOthers = (redSum - blueSum - greenSum)/(notBlackCount+0.0001);
		double blueWithoutOthers = (blueSum - redSum - greenSum)/(notBlackCount+0.0001);
		double greenWithoutOthers = (greenSum - redSum - blueSum)/(notBlackCount+0.0001);
		
		return new double[] {-distFrom50,redWithoutOthers,blueWithoutOthers,greenWithoutOthers};
	}

	@Override
	public int numberObjectives() {
		return 1;
	}

}
