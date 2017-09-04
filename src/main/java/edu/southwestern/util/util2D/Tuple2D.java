package edu.southwestern.util.util2D;

import java.awt.geom.Point2D;

/**
 * (x,y) coordinates for object in 2D plane.
 * 
 * @author Jacob Schrum
 */
public class Tuple2D extends Point2D.Double implements ILocated2D {

	/**
	 * added to removed compiler complaints
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct using x,y coordinates
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	public Tuple2D(double x, double y) {
		super(x, y);
	}

	/**
	 * Is the location at the origin?
	 * 
	 * @return Whether both coordinates are 0
	 */
	public boolean isZero() {
		return (x == 0 && y == 0);
	}

	/**
	 * Euclidean distance of (x,y) coordinate from the origin, a.k.a. length of
	 * vector.
	 * 
	 * @return length of vector to (x,y)
	 */
	public double length() {
		return Math.sqrt((x * x) + (y * y));
	}

	/**
	 * Parallel vector with length/magnitude of 1.
	 * 
	 * @return Corresponding unit vector.
	 */
	public Tuple2D normalize() {
		double len = length();
		return new Tuple2D(x / len, y / len);
	}

	/**
	 * returns the angle in radians from the positive x-axis to the the tuple2D
	 * using the math conventions that counterclockwise is positive, clockwise
	 * is negative and 2PI is the max angle
	 * 
	 * @return angle in radians
	 */
	public double angle() {
		double angle = x == 0 ? Math.PI / 2 : Math.acos(Math.abs(this.normalize().x));
		if (x < 0) {
			if (y < 0) {
				angle = Math.PI + angle;
			} else if (y > 0) {
				angle = Math.PI - angle;
			} else if (y == 0) {
				angle = Math.PI;
			}
		} else if (x > 0) {
			if (y < 0) {
				angle = (2 * Math.PI) - angle;
			}
		} else {
			if (y < 0) {
				angle = (3 * Math.PI) / 2;
			} else if (y > 0) {
				angle = Math.PI / 2;
			} else {
				angle = 0;
			}
		}
		return angle;
	}

	/**
	 * midpoint from other tuple to tuple in quesiton
	 * 
	 * @param position
	 *            other tuple
	 * @return tuple at midpoint between the two tuples
	 */
	public Tuple2D midpoint(Tuple2D position) {
		return add(position).div(2);
	}

	/**
	 * adds two tuples position
	 * 
	 * @param position
	 *            other tuple
	 * @return tuple that is result of adding two tuples
	 */
	public Tuple2D add(Tuple2D position) {
		return new Tuple2D(x + position.x, y + position.y);
	}

	/**
	 * subtracts the location of two tuples
	 * 
	 * @param rhs
	 *            location of other ILocated2D
	 * @return tuple that is result of subtracting two tuples
	 */
	public Tuple2D sub(ILocated2D rhs) {
		return new Tuple2D(this.x - rhs.getX(), this.y - rhs.getY());
	}

	/**
	 * multiplies a tuple by a variable
	 * 
	 * @param i
	 *            number to multiply tuple by
	 * @return location of multiplied tuple
	 */
	public Tuple2D mult(double i) {
		return new Tuple2D(x * i, y * i);
	}

	/**
	 * divides a tuple by a variable
	 * 
	 * @param i
	 *            number to divide tuple by
	 * @return location of divided tuple
	 */
	public Tuple2D div(double i) {
		return new Tuple2D(x / i, y / i);
	}

	/**
	 * returns a tuple that is identical to tuple in question
	 */
	public Tuple2D getPosition() {
		return this;
	}

	/**
	 * rotates a given tuple around origin by given radians
	 * 
	 * @param radians
	 *            radians to rotate by
	 * @return rotated tuple
	 */
	public Tuple2D rotate(double radians) {
		return new Tuple2D((x * Math.cos(radians)) - (y * Math.sin(radians)),
				(x * Math.sin(radians)) + (y * Math.cos(radians)));
	}

	/**
	 * distance from one tuple to another
	 */
	public double distance(ILocated2D other) {
		return distance(new Point2D.Double(other.getX(), other.getY()));
	}

	/**
	 * overrides default toString method
	 */
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	/**
	 * angle between two tuples
	 * 
	 * @param a
	 *            first tuple
	 * @param b
	 *            second tuple
	 * @return angle (in radians) between tuples a and b
	 */
	public double angleBetweenTargets(ILocated2D a, ILocated2D b) {
		double angle1 = a.getPosition().sub(this).angle();
		double angle2 = b.getPosition().sub(this).angle();
		return Math.abs(angle1 - angle2);
	}
}
