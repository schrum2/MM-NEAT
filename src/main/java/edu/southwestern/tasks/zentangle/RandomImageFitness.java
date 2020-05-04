package edu.southwestern.tasks.zentangle;

import java.awt.image.BufferedImage;

import edu.southwestern.util.random.RandomNumbers;

public class RandomImageFitness implements ImageFitness {

	@Override
	public double[] fitness(BufferedImage image) {
		return new double[] {RandomNumbers.randomGenerator.nextDouble()};
	}

	@Override
	public int numberObjectives() {
		return 1;
	}

}
