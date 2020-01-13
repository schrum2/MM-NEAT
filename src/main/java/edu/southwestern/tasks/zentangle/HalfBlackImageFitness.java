package edu.southwestern.tasks.zentangle;

import java.awt.image.BufferedImage;

/**
 * Encourages images to be as close to half black as possible, since
 * the template images differentiate between black and not black.
 * 
 * @author Jacob Schrum
 */
public class HalfBlackImageFitness implements ImageFitness{

	// Not sure why this is true, but it is
	private static final int BLACK_VALUE = -16777216;
	
	@Override
	public double[] fitness(BufferedImage image) {
		int blackCount = 0;
		int width = image.getWidth();
		int height = image.getHeight();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				//System.out.print("("+x+","+y+":"+image.getRGB(x, y)+")");
				if(image.getRGB(x, y) == BLACK_VALUE) {
					blackCount++;
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
		return new double[] {-distFrom50};
	}

	@Override
	public int numberObjectives() {
		return 1;
	}

}
