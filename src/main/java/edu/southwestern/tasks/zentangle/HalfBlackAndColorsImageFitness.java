package edu.southwestern.tasks.zentangle;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Same as HalfBlackImageFitness, but also has additional fitness functions for each color,
 * to encourage variety in the population.
 * 
 * @author Jacob Schrum
 */
public class HalfBlackAndColorsImageFitness implements ImageFitness {

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
				}
				Color c = new Color(color);
				redSum += c.getRed();
				blueSum += c.getBlue();
				greenSum += c.getGreen();
			}
			//System.out.println();
		}
		
		double total = width * height;
		// Percentage of black pixels
		double percent = blackCount/total;
		// How far from perfect 50/50 split
		double distFrom50 = Math.abs(percent - 0.5);
		// Negate because closer to 50 is better. Best possible fitness is 0
		
		int redWithoutOthers = redSum - blueSum - greenSum;
		int blueWithoutOthers = blueSum - redSum - greenSum;
		int greenWithoutOthers = greenSum - redSum - blueSum;
		
		return new double[] {-distFrom50,redWithoutOthers,blueWithoutOthers,greenWithoutOthers};
	}

	@Override
	public int numberObjectives() {
		return 1;
	}

}
