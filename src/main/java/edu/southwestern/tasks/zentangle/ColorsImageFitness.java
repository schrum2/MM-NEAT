package edu.southwestern.tasks.zentangle;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Same as HalfBlackImageFitness, but also has additional fitness functions for each color,
 * to encourage variety in the population.
 * 
 * @author Jacob Schrum
 */
public class ColorsImageFitness implements ImageFitness {
	
	@Override
	public double[] fitness(BufferedImage image) {
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
				redSum += c.getRed();
				blueSum += c.getBlue();
				greenSum += c.getGreen();
			}
			//System.out.println();
		}
		
		int redWithoutOthers = redSum - blueSum - greenSum;
		int blueWithoutOthers = blueSum - redSum - greenSum;
		int greenWithoutOthers = greenSum - redSum - blueSum;
		
		return new double[] {redWithoutOthers,blueWithoutOthers,greenWithoutOthers};
	}

	@Override
	public int numberObjectives() {
		return 3;
	}

}
