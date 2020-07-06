package edu.southwestern.tasks.megaman.levelgenerators;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * Given variables associated with a single segment (both latent and aux),
 * generate the segment.
 * @author Jacob Schrum
 *
 */
public abstract class MegaManGANGenerator {
	
	/**
	 * Number of auxiliary variables at the start of each set of segmentVariables
	 * @return Num variables
	 */
	public static int numberOfAuxiliaryVariables() {
		// TODO: Add optional support for 4 directions
		return 3; // Currently only supporting Right, Up, Down, but will add Left (return 4) soon
	}
	
	public enum SEGMENT_TYPE {UP, DOWN, RIGHT, LEFT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT};
	
	/**
	 * Take all variables (latent and non-latent) associated with a single level segment and produce the
	 * segment. The type of the previous segment is also provided because this can affect the current
	 * segment. If previous is null, then this is the first segment in the level.
	 * 
	 * @param segmentVariables Array of auxiliary variables followed by latent variables
	 * @param previous Previous segment type
	 * @param previousPoints Set of Points in the level that are occupied by segments
	 * @param currentPoint Where the current segment will be placed
	 * @return List of Lists representation of the generated segment.
	 */
	public List<List<Integer>> generateSegmentFromVariables(double[] segmentVariables, SEGMENT_TYPE previous, HashSet<Point> previousPoints, Point currentPoint){
		// Save latent vector
		double[] latentVector = new double[Parameters.parameters.integerParameter("GANInputSize")];
		System.arraycopy(segmentVariables, numberOfAuxiliaryVariables(), latentVector, 0, latentVector.length);
		// Save aux variables
		double[] auxiliaryVariables = new double[numberOfAuxiliaryVariables()];
		System.arraycopy(segmentVariables, 0, auxiliaryVariables, 0, auxiliaryVariables.length);
		
		SEGMENT_TYPE type = determineType(previous, auxiliaryVariables, previousPoints, currentPoint);
		
		return generateSegmentFromLatentVariables(latentVector, type);
	}
	
	/**
	 * Given the previous segment type and the auxiliary variables for the current segment,
	 * determine the type of the current segment.
	 * 
	 * @param previous Previous segment type (null for first segment)
	 * @param auxiliaryVariables Variables for up, down, right, and maybe left
	 * @param previousPoints Set of Points in the level that are occupied by segments
	 * @param currentPoint Where the current segment will be placed
	 * @return Segment type of new segment
	 */
	protected static SEGMENT_TYPE determineType(SEGMENT_TYPE previous, double[] auxiliaryVariables, HashSet<Point> previousPoints, Point currentPoint) {
				
		int maxIndex = StatisticsUtilities.argmax(auxiliaryVariables);
		
		if(previous == null) {
			// This is the first segment in the level
			return SEGMENT_TYPE.values()[maxIndex];
		} else {	
			// TODO: Requires more work
			boolean done = false;
			SEGMENT_TYPE result = null;
			while(!done) {
				// This can only be UP, DOWN, RIGHT, LEFT
				SEGMENT_TYPE proposed = SEGMENT_TYPE.values()[maxIndex];
				Point next = nextPoint(previous, currentPoint, proposed); // Where would new segment go?
				if(previousPoints.contains(next)) { // This placement is illegal. Location occupied
					auxiliaryVariables[maxIndex] = Double.NEGATIVE_INFINITY; // Disable illegal option
					maxIndex = StatisticsUtilities.argmax(auxiliaryVariables); // Reset
					if(Double.isInfinite(auxiliaryVariables[maxIndex])) {
						result = null; // There is NO legal placement possible!
						done = true;
					}
				} else {
					previousPoints.add(next); // This point will be occupied now
					// Figure out if proposed should be changed to corner
					if(!previous.equals(proposed)) {
						// Change to appropriate corner
						// TODO: assign result and set done to true
						if((previous.equals(SEGMENT_TYPE.UP)&&proposed.equals(SEGMENT_TYPE.RIGHT))||(previous.equals(SEGMENT_TYPE.LEFT)&&proposed.equals(SEGMENT_TYPE.DOWN))) {//place upper left
							proposed = SEGMENT_TYPE.TOP_LEFT;
							done = true;
						}
						else if((previous.equals(SEGMENT_TYPE.DOWN)&&proposed.equals(SEGMENT_TYPE.RIGHT))||(previous.equals(SEGMENT_TYPE.LEFT)&&proposed.equals(SEGMENT_TYPE.UP))) { //place lower left
							proposed = SEGMENT_TYPE.BOTTOM_LEFT;
							done = true;
						}
						else if((previous.equals(SEGMENT_TYPE.RIGHT)&&proposed.equals(SEGMENT_TYPE.UP))||(previous.equals(SEGMENT_TYPE.DOWN)&&proposed.equals(SEGMENT_TYPE.LEFT))) { //place lower right
							proposed = SEGMENT_TYPE.BOTTOM_RIGHT;
							done = true;
						}
						else if((previous.equals(SEGMENT_TYPE.RIGHT)&&proposed.equals(SEGMENT_TYPE.DOWN))||(previous.equals(SEGMENT_TYPE.UP)&&proposed.equals(SEGMENT_TYPE.LEFT))) { //place upper right
							proposed = SEGMENT_TYPE.TOP_RIGHT;
							done = true;
						}
						
					} else {
						// Proposed result is fine ... done!
						done = true;
						result = proposed;
					}
				}
			}
			return result;
		}
	}
	
	/**
	 * Return where the next point would be if a segment of the given type is placed
	 * @param previousType Type of the previous segment (could be null) 
	 * @param current Current segment location
	 * @param currentType Type of the current segment 
	 * @return Where next Point would be
	 */
	private static Point nextPoint(SEGMENT_TYPE previousType, Point current, SEGMENT_TYPE currentType) {
		switch(currentType) {
			case UP: return new Point(current.x, current.y - 1);
			case DOWN: return new Point(current.x, current.y + 1);
			case RIGHT: return new Point(current.x+1, current.y);
			case LEFT: return new Point(current.x-1, current.y);
			case TOP_LEFT:
				if(previousType.equals(SEGMENT_TYPE.UP)) return new Point(current.x+1, current.y); // Move right
				else {
					assert previousType.equals(SEGMENT_TYPE.LEFT);
					return new Point(current.x, current.y+1); // Move down
				}
			case TOP_RIGHT:
				if(previousType.equals(SEGMENT_TYPE.UP)) return new Point(current.x-1, current.y); // Move left
				else {
					assert previousType.equals(SEGMENT_TYPE.RIGHT);
					return new Point(current.x, current.y+1); // Move down
				}
			case BOTTOM_RIGHT:
				if(previousType.equals(SEGMENT_TYPE.DOWN)) return new Point(current.x-1, current.y); // Move left
				else {
					assert previousType.equals(SEGMENT_TYPE.RIGHT);
					return new Point(current.x, current.y-1); // Move up
				}
			case BOTTOM_LEFT:
				if(previousType.equals(SEGMENT_TYPE.DOWN)) return new Point(current.x+1, current.y); // Move right
				else {
					assert previousType.equals(SEGMENT_TYPE.LEFT);
					return new Point(current.x, current.y-1); // Move up
				}
			default: throw new IllegalArgumentException("Valid SEGMENT_TYPE not specified");
		}
	}

	protected abstract List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type);
}
