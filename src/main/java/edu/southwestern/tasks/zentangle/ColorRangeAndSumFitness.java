package edu.southwestern.tasks.zentangle;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.southwestern.util.random.RandomNumbers;

/**
 * Three fitness functions, each if max - min for each
 * color: RGB
 */
public class ColorRangeAndSumFitness implements ImageFitness {
	
	@Override
	public double[] fitness(BufferedImage image) {
		int redMin = 255;
		int greenMin = 255;
		int blueMin = 255;
		int redMax = 0;
		int greenMax = 0;
		int blueMax = 0;
		
		int redSum = 0;
		int greenSum = 0;
		int blueSum = 0;
		
		int width = image.getWidth();
		int height = image.getHeight();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				//System.out.print("("+x+","+y+":"+image.getRGB(x, y)+")");
				int color = image.getRGB(x, y);
				Color c = new Color(color);
				// Finds the max for red, green and blue
				redMax = Math.max(c.getRed(),redMax);
				greenMax = Math.max(c.getGreen(), greenMax);
				blueMax = Math.max(c.getBlue(),blueMax);
				// Finds the min for red, green, and blue
				redMin = Math.min(c.getRed(),redMin);
				greenMin = Math.min(c.getGreen(), greenMin);
				blueMin = Math.min(c.getBlue(),blueMin);
				
				redSum += c.getRed();
				blueSum += c.getBlue();
				greenSum += c.getGreen();
			}
			//System.out.println();
		}
		
		double redRange = redMax - redMin;
		double greenRange = greenMax - greenMin;
		double blueRange = blueMax - blueMin;
		
		int redWithoutOthers = redSum - blueSum - greenSum;
		int blueWithoutOthers = blueSum - redSum - greenSum;
		int greenWithoutOthers = greenSum - redSum - blueSum;
		
		// returns an array of doubles containing the range for red, green and blue
		//return new double[] {redRange, greenRange, blueRange, redWithoutOthers, blueWithoutOthers, greenWithoutOthers};
		return new double[] {redRange, greenRange, blueRange, redWithoutOthers,blueWithoutOthers,greenWithoutOthers, RandomNumbers.randomGenerator.nextDouble()};
	}

	@Override
	public int numberObjectives() {
		//return 6; 
		return 7;
	}

}
