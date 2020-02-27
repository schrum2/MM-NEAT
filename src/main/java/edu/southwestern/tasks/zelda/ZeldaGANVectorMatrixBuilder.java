package edu.southwestern.tasks.zelda;

/**
 * Defines a way of generating a vector that can create a Zelda room with a GAN based approach.
 * The vector will start with some miscellaneous information to aid in dungeon construction,
 * but is followed by latent vector inputs to a GAN
 * 
 * @author Jacob
 *
 */
public interface ZeldaGANVectorMatrixBuilder {
	/**
	 * If the width and the height of the dungeon (in rooms) matches the given parameters, and x and y
	 * are the coordinates for the specific room being made, what vector of doubles will be used to
	 * generate the room? The first few numbers are miscellaneous data, and the remainder are latent
	 * GAN inputs.
	 * 
	 * @param width Width of dungeon in rooms
	 * @param height Height of dungeon in rooms
	 * @param x Coordinate (along width dimension) from left to right of room
	 * @param y Coordinate (along height dimension) from top to bottom of room
	 * @return Vector that GAN can use to define room
	 */
	public double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y);
}
