package edu.southwestern.util.util2D;

/**
 *
 * @author Jacob Schrum
 */
public class Box2D {

	private double top;
	private double bottom;
	private double right;
	private double left;

	/**
	 * 
	 * 
	 * @param points ILocated2D Array used to create an abstract box
	 */
	public Box2D(ILocated2D[] points) {
		top = -Double.MAX_VALUE;
		bottom = Double.MAX_VALUE;
		left = Double.MAX_VALUE;
		right = -Double.MAX_VALUE;
		for (ILocated2D p : points) { // Creates an abstract box using the points in the given Array
			top = Math.max(top, p.getY()); // Stores the maximum Y-Value as the top of the box
			bottom = Math.min(bottom, p.getY()); // Stores the minimum Y-Value as the bottom of the box
			right = Math.max(right, p.getX()); // Stores the maximum X-Value as the right edge of the box
			left = Math.min(left, p.getX()); // Stores the minimum X-Value as the left edge of the box
		}
	}

	/**
	 * Returns True if a given ILocated2D Point is within the Box2D, else returns False
	 * 
	 * @param p Point to be checked if it's within the Box2D
	 * @return True if p is within the Box2D, else returns False
	 */
	public boolean insideBox(ILocated2D p) {
		return insideBox(p, 0);
	}

	/**
	 * Returns True if a given ILocated2D Point is within the Box2D with a given buffer, else returns False
	 * 
	 * @param p Point to be checked if it's within the Box2D
	 * @param buffer Buffer that the Point is allowed to be within; extends the Box2D by a given distance in all directions
	 * 
	 * @return True if p is within the Box2D with the buffer, else returns False
	 */
	
	public boolean insideBox(ILocated2D p, double buffer) {
		return p.getX() > (left - buffer) && p.getX() < (right + buffer) && p.getY() > (bottom - buffer)
				&& p.getY() < (top + buffer);
	}
	
	public String toString(){
		return "(" + top + ", " + bottom + ") (" + left + ", " + right + ")";
	}
}
