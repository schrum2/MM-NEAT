package edu.southwestern.util.util2D;

/**
 * Interface for any object that has a location in 2D space.
 * 
 * @author Jacob Schrum
 */
public interface ILocated2D {

	/**
	 * Position of object
	 * 
	 * @return (x,y) coordinates of object
	 */
	public Tuple2D getPosition();

	/**
	 * Distance from object to other object
	 * 
	 * @param other
	 *            Other object with a location
	 * @return distance between this object and the other
	 */
	public double distance(ILocated2D other);

	/**
	 * x-coordinate of object
	 * 
	 * @return x-coordinate
	 */
	public double getX();

	/**
	 * y-coordinate of object
	 * 
	 * @return y-coordinate
	 */
	public double getY();
}
