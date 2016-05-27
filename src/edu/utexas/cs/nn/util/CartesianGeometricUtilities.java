package edu.utexas.cs.nn.util;

import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.util.ArrayList;

/**
 * Utility functions for Cartesian geometry calculations
 *
 * @author Jacob Schrum
 */
public class CartesianGeometricUtilities {

	/**
	 * Scales X-Y coordinates to where origin is at center of plane, not top
	 * left corner. Also scales to range [-1,1] in each dimension. Remember that
	 * upper value should be n-1 if not yet 0-based
	 *
	 * @param toScale
	 *            (x,y) coordinates as a tuple
	 * @param width
	 *            width
	 * @param height
	 *            height
	 *
	 * @return new tuple with scaled coordinates
	 */
	public static Tuple2D centerAndScale(Tuple2D toScale, int width, int height) {
		double newX = centerAndScale(toScale.x, width);
		double newY = centerAndScale(toScale.y, height);
		return new Tuple2D(newX, newY);
	}

	/**
	 * Scales either x or y coordinate to where origin is at center of plane,
	 * not top left corner
	 *
	 * @param toScale
	 *            coordinate to be scaled
	 * @param maxDimension
	 *            either height or width, depending on whether toScale is x or y
	 *            coordinate
	 *
	 * @return scaled coordinate
	 */
	public static double centerAndScale(double toScale, int maxDimension) {
		return ((toScale / (maxDimension - 1)) * 2) - 1;
	}

	/**
	 * Calculates the shortest distance from a point to a segment
	 * 
	 * @param p1
	 *            point to calculate distance from
	 * @param l1
	 *            one end of segment
	 * @param l2
	 *            other end of segment
	 * 
	 * @return shortest distance from segment to point
	 */
	public static double shortestDistanceToLineSegment(ILocated2D p1, ILocated2D l1, ILocated2D l2) {
		return shortestDistanceToLineSegment(p1.getX(), p1.getY(), l1.getX(), l2.getX(), l1.getY(), l2.getY());
	}

	/**
	 * Calculates the shortest distance from a point to a line segment. In other
	 * words, the length of a segment that is perpendicular to the line segment,
	 * and goes through the point.
	 * 
	 * @param x
	 *            x-coordinate of point
	 * @param y
	 *            y-coordinate of point
	 * @param x1
	 *            First x-coordinate of line
	 * @param x2
	 *            First y-coordinate of line
	 * @param y1
	 *            Second x-coordinate of line
	 * @param y2
	 *            Second y-coordinate of line
	 * @return length of normal line to point
	 */
	public static double shortestDistanceToLineSegment(double x, double y, double x1, double x2, double y1, double y2) {
		double A = x - x1;
		double B = y - y1;
		double C = x2 - x1;
		double D = y2 - y1;

		double dot = A * C + B * D;
		double len_sq = C * C + D * D;
		double param = dot / len_sq;

		double xx, yy;

		if (param < 0) {
			xx = x1;
			yy = y1;
		} else if (param > 1) {
			xx = x2;
			yy = y2;
		} else {
			xx = x1 + param * C;
			yy = y1 + param * D;
		}

		ILocated2D other = new Tuple2D(xx, yy);
		return (new Tuple2D(x, y).distance(other));
	}

	/**
	 * calculates euclidian distance in a generalized vector space
	 * 
	 * @param x1
	 *            the first point in space
	 * @param x2
	 *            the second point in space
	 * 
	 * @return distance between x1 and x2
	 */
	public static double euclideanDistance(ArrayList<Double> x1, ArrayList<Double> x2) {
		double sum = 0;
		for (int i = 0; i < x1.size(); i++) {
			sum += Math.pow(x1.get(i) - x2.get(i), 2);
		}
		return Math.sqrt(sum);
	}

	/**
	 * calculates the Cartesian coordinates from polar inputs
	 * 
	 * @param r
	 *            radius
	 * @param theta
	 *            angle from X-axis to radius
	 * 
	 * @return double array containing x/y coordinates
	 */
	public static double[] polarToCartesian(double r, double theta) {
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		return new double[] { x, y };
	}

	/**
	 * Assuming that something at source is aimed towards the position at
	 * sourceRads radians on the unit circle, compare the resulting vector to a
	 * vector from the source to the target. The difference in radians between
	 * the angles of the two vectors is returned, with different signs
	 * indicating which side of each other the two vectors are.
	 *
	 * @param source
	 *            any 2d located object
	 * @param target
	 *            any 2d located object
	 * @param sourceRads
	 *            between 0 and 2pi
	 * @return difference in angles
	 */
	public static double signedAngleFromSourceHeadingToTarget(ILocated2D source, ILocated2D target, double sourceRads) {
		if (target == null || target.getPosition() == null || source == null || source.getPosition() == null) {
			return Math.PI;
		}
		Tuple2D sourceToTarget = target.getPosition().sub(source);
		if (sourceToTarget.isZero()) {
			return 0;
		}
		sourceToTarget = sourceToTarget.normalize();
		double angleToTarget = sourceToTarget.angle();
		return signedAngleDifference(sourceRads, angleToTarget);
	}

	/**
	 * returns the difference between the angle measurements of two 2D vectors
	 * with a sign corresponding to whether the second vector is clockwise or
	 * counter-clockwise from the first (closest distance)
	 * 
	 * @param v1
	 *            vector 1
	 * @param v2
	 *            vector 2
	 * 
	 * @return the difference in angles
	 */
	public static double signedAngleDifference(ILocated2D v1, ILocated2D v2) {
		return signedAngleDifference(v1.getPosition().angle(), v2.getPosition().angle());
	}

	/**
	 * returns the difference between two angles and the correct sign
	 * 
	 * @param rad1
	 *            first angle
	 * @param rad2
	 *            second angle
	 * @return difference between rad1 and rad2 w/ correct sign
	 */
	public static double signedAngleDifference(double rad1, double rad2) {
		double angleDifference = rad1 - rad2;
		if (angleDifference > Math.PI) {
			angleDifference -= 2 * Math.PI;
		} else if (angleDifference < -Math.PI) {
			angleDifference += 2 * Math.PI;
		}
		return -angleDifference;
	}

	/**
	 * returns whether or not the source is heading towards the target
	 * 
	 * @param sourceRadians
	 *            pie slice of source's sensors
	 * @param source
	 *            location of source
	 * @param target
	 *            location of target
	 * @param allowance
	 *            error allowance
	 * 
	 * @return whether or not source is heading towards target
	 */
	public static boolean sourceHeadingTowardsTarget(double sourceRadians, ILocated2D source, ILocated2D target,
			double allowance) {
		double angle = signedAngleFromSourceHeadingToTarget(source, target, sourceRadians);
		return Math.abs(angle) < allowance;
	}

	/**
	 * returns true if other agent is on the side of the source agent determined
	 * by the direction the source agent is looking and the value of the boolean
	 * right (true for right, false for left).
	 * 
	 * @param source
	 *            location of source agent
	 * @param sourceRadians
	 *            direction source is facing
	 * @param other
	 *            location of other agent
	 * @param right
	 *            if true, indicate whether the other agent is on the source's
	 *            right. else, indicate whether the other agent is on the
	 *            source's left.
	 * @return whether or not other agent is on designated side of source.
	 */
	public static boolean onSideOf(ILocated2D source, double sourceRadians, ILocated2D other, boolean right) {
		double angle = signedAngleFromSourceHeadingToTarget(source, other, sourceRadians);
		return (right && (angle > 0)) || (!right && (angle < 0));
	}

	/**
	 * restricts radians to the unit circle radian dimensions
	 * 
	 * @param rads
	 *            radians to check
	 * 
	 * @return radian within unit circle bounds
	 */
	public static double restrictRadians(double rads) {
		while (rads >= 2 * Math.PI) {
			rads -= 2 * Math.PI;
		}
		while (rads < 0) {
			rads += 2 * Math.PI;
		}
		return rads;
	}
}
