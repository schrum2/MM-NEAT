package edu.southwestern.util;

import edu.southwestern.util.datastructures.Vertex;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;
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
		double newX = centerAndScale(toScale.x, width); //scaled x coordinate
		double newY = centerAndScale(toScale.y, height); //scaled y coordinate
		assert !Double.isNaN(newX) : "newX is NaN! width="+width+", height="+height+", toScale="+toScale;
		assert !Double.isNaN(newY) : "newY is NaN! width="+width+", height="+height+", toScale="+toScale;
		return new Tuple2D(newX, newY);
	}
	
	/**
	 * Method that centers and scales a vertex based on the width, height, and depth of the shape
	 * 
	 * @param toScale Vertex to be scaled
	 * @param width width of shape
	 * @param height height of shape
	 * @param depth depth of shape
	 * @return scaled vertex
	 */
	public static Vertex centerAndScale(Vertex toScale, int width, int height, int depth) {
		double newX = CartesianGeometricUtilities.centerAndScale(toScale.x, width); //scaled x coordinate
		double newY = CartesianGeometricUtilities.centerAndScale(toScale.y, height); //scaled y coordinate
		double newZ = CartesianGeometricUtilities.centerAndScale(toScale.z, depth); //scaled z coordinate
		assert !Double.isNaN(newX) : "newX is NaN! width="+width+", height="+height+", toScale="+toScale;
		assert !Double.isNaN(newY) : "newY is NaN! width="+width+", height="+height+", toScale="+toScale;
		assert !Double.isNaN(newZ) : "newZ is NaN! width="+width+", height="+height+", toScale="+toScale;
		return new Vertex(newX, newY, newZ);
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
		if(maxDimension == 1) {
			assert toScale == 0 : "If the dimension is 1, then you can only scale 0. maxDimension = " + maxDimension + ", toScale = " + toScale;
			return 0;
		}
		return ((toScale / (maxDimension - 1)) * 2) - 1;
	}

	/**
	 * Scales coordinates to 1D in Y-coordinate space
	 * @param toScale coordinates to scale
	 * @param width width of domain
	 * @param height height of domain
	 * @return scaled coordinates
	 */
	public static Tuple2D Bottom1DCenterAndScale(Tuple2D toScale, int width, int height) {
		double newX = 0.0;
		double newY = bottomScale(toScale.y, height);
		assert !Double.isNaN(newY) : "newY is NaN! width="+width+", height="+height+", toScale="+toScale;
		return new Tuple2D(newX, newY);
	}
	
	/**
	 * Scales the coordinates to -1 - 1 in X-direction and 0 - 1 in Y-direction
	 * @param toScale coordinates to scale
	 * @param width width of domain
	 * @param height height of domain
	 * @return scaled coordinates
	 */
	public static Tuple2D bottomCenterAndScale(Tuple2D toScale, int width, int height) { 
		double newX = centerAndScale(toScale.x, width); //scaled x coordinate
		double newY = bottomScale(toScale.y, height); //scaled y coordinate
		assert !Double.isNaN(newX) : "newX is NaN! width="+width+", height="+height+", toScale="+toScale;
		assert !Double.isNaN(newY) : "newY is NaN! width="+width+", height="+height+", toScale="+toScale;
		return new Tuple2D(newX, newY);
	}
	
	/**
	 * Scales coordinates to where origin is at bottom of the plane based on the maximum dimension by mapping 
	 * the scale to its corresponding dimension
	 * @param toScale  coordinate to be scaled
	 * @param maxDimension  either height or width, depending on whether toScale is x or y
	 *            coordinate
	 * @return 0 if maxDimension is 1 (special case), otherwise scaled value
	 */
	public static double bottomScale(double toScale, int maxDimension) {
		if(maxDimension == 1) {
			assert toScale == 0 : "If the dimension is 1, then you can only scale 0";
			return 0;
		}
		return (toScale / (maxDimension - 1));
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
	 * and goes through the point. However, if the normal line does not pass 
	 * through the segment, then the distance to the nearest segment endpoint
	 * is returned instead.
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

		if (param < 0) { //normal line does not pass through segment, so focus on first segment endpoint
			xx = x1;
			yy = y1;
		} else if (param > 1) { //normal line does not pass through segment, so focus on second segment endpoint
			xx = x2;
			yy = y2;
		} else { // working out correctly; param between 0 and 1: point where normal line intersects segment
			xx = x1 + param * C;
			yy = y1 + param * D;
		}

		ILocated2D other = new Tuple2D(xx, yy);
		return (new Tuple2D(x, y).distance(other)); //distance between input point and point created based on param
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
		for (int i = 0; i < x1.size(); i++) { //sum of squared distances between x1 and x2
			sum += Math.pow(x1.get(i) - x2.get(i), 2);
		}
		return Math.sqrt(sum); // taking square root of squared sums calculates euclidian distance
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
		if (sourceToTarget.isZero()) { //if the difference between the vectors is 0
			return 0;
		}
		sourceToTarget = sourceToTarget.normalize(); // returns the norm of the difference between vectors
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
		if (angleDifference > Math.PI) { // has exceeded maximum to avoid cycling
			angleDifference -= 2 * Math.PI; 
		} else if (angleDifference < -Math.PI) {  // has goen below minimum to avoid cycling
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
		while (rads >= 2 * Math.PI) { // if radian exceeds unit circle
			rads -= 2 * Math.PI; // rotate until w/in unit circle parameters
		}
		while (rads < 0) { // if radian is less than unit circle
			rads += 2 * Math.PI; // rotate until w/in unit circle parameters
		}
		return rads;
	}
}
