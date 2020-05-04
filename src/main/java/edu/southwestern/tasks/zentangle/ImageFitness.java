package edu.southwestern.tasks.zentangle;

import java.awt.image.BufferedImage;

/**
 * Fitness function for BufferedImages. Can return multiple objectives.
 * @author Jacob Schrum
 */
public interface ImageFitness {
	/**
	 * Return an array of fitness scores for the image
	 * @param image Image to evaluate
	 * @return Array of fitness scores
	 */
	public double[] fitness(BufferedImage image);
	
	/**
	 * Number of fitness objectives, which equals length of array returned by fitness.
	 * @return Number of fitness objectives.
	 */
	public int numberObjectives();
}
