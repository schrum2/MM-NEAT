package edu.southwestern.tasks.megaman.levelgenerators;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
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
		if(Parameters.parameters.booleanParameter("megaManAllowsLeftSegments")) return 4;
		else return 3; // Currently only supporting Right, Up, Down, but will add Left (return 4) soon
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
	public Pair<List<List<Integer>>, Point> generateSegmentFromVariables(double[] segmentVariables, Point previousPoint, HashSet<Point> previousPoints, Point currentPoint){
		// Save latent vector
		double[] latentVector = new double[Parameters.parameters.integerParameter("GANInputSize")];
		System.arraycopy(segmentVariables, numberOfAuxiliaryVariables(), latentVector, 0, latentVector.length);
		// Save aux variables
		double[] auxiliaryVariables = new double[numberOfAuxiliaryVariables()];
		System.arraycopy(segmentVariables, 0, auxiliaryVariables, 0, auxiliaryVariables.length);
		
		Pair<SEGMENT_TYPE, Point> type = determineType(previousPoint, auxiliaryVariables, previousPoints, currentPoint);
		assert type.t1 != null;
		System.out.println(type.t1);
		Pair<List<List<Integer>>, Point> segmentAndCurrentPoint = new Pair<>(generateSegmentFromLatentVariables(latentVector, type.t1), type.t2);
		return segmentAndCurrentPoint;
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
	protected static Pair<SEGMENT_TYPE, Point> determineType(Point previousPoint, double[] auxiliaryVariables, HashSet<Point> previousPoints, Point currentPoint) {
				
		int maxIndex = StatisticsUtilities.argmax(auxiliaryVariables);
		
		if(previousPoint == null) {
			// This is the first segment in the level
			SEGMENT_TYPE proposed = SEGMENT_TYPE.values()[maxIndex];
			assert proposed != null;
			Point next = nextPoint(previousPoint, currentPoint, proposed);
//			System.out.println(next);
			previousPoints.add(next);			
			previousPoints.add(currentPoint);

			return new Pair<SEGMENT_TYPE, Point>(proposed, next);
		} else {	
			// TODO: Requires more work
			boolean done = false;
			SEGMENT_TYPE result = null;
			Point next = null;
			while(!done) {
				//System.out.println(maxIndex + ":" + Arrays.toString(auxiliaryVariables));
				// This can only be UP, DOWN, RIGHT, LEFT
				SEGMENT_TYPE proposed = SEGMENT_TYPE.values()[maxIndex];
				next = nextPoint(previousPoint, currentPoint, proposed); // Where would new segment go?
//				System.out.println(next);

				if(previousPoints.contains(next)) { // This placement is illegal. Location occupied
					//System.out.println(previousPoints + " contains " + next);
					auxiliaryVariables[maxIndex] = Double.NEGATIVE_INFINITY; // Disable illegal option
					maxIndex = StatisticsUtilities.argmax(auxiliaryVariables); // Reset
					if(Double.isInfinite(auxiliaryVariables[maxIndex])) {
						result = null; // There is NO legal placement possible!
						//System.out.println("NO LEGAL PLACEMENT!");
						//assert false : ""+previousPoints + ":" + next; // TEMP
						done = true;
					}
				} else {
					System.out.println(proposed);
					switch(proposed) {
					case RIGHT:
						if(previousPoint.y == currentPoint.y) // Keep moving right. Do nothing
							result = proposed;
						else if(previousPoint.y + 1 == currentPoint.y) // Moved down
							result = SEGMENT_TYPE.BOTTOM_LEFT;
						else if(previousPoint.y - 1 == currentPoint.y) // Moved up
							result = SEGMENT_TYPE.TOP_LEFT;
						else
							throw new IllegalStateException();
						break;
					case LEFT:
						if(previousPoint.y == currentPoint.y) // Keep moving left. Do nothing
							result = proposed;
						else if(previousPoint.y + 1 == currentPoint.y) // Moved down
							result = SEGMENT_TYPE.BOTTOM_RIGHT;
						else if(previousPoint.y - 1 == currentPoint.y) // Moved up
							result = SEGMENT_TYPE.TOP_RIGHT;
						else
							throw new IllegalStateException();
						break;
					case UP:
						if(previousPoint.x == currentPoint.x) // Keep moving up. Do nothing
							result = proposed;
						else if(previousPoint.x + 1 == currentPoint.x) // Moved right
							result = SEGMENT_TYPE.BOTTOM_RIGHT;
						else if(previousPoint.x - 1 == currentPoint.x) // Moved left
							result = SEGMENT_TYPE.BOTTOM_LEFT;
						else
							throw new IllegalStateException();
						break;
					case DOWN:
						if(previousPoint.x == currentPoint.x) // Keep moving down. Do nothing
							result = proposed;
						else if(previousPoint.x + 1 == currentPoint.x) // Moved right
							result = SEGMENT_TYPE.TOP_RIGHT;
						else if(previousPoint.x - 1 == currentPoint.x) // Moved left
							result = SEGMENT_TYPE.TOP_LEFT;
						else
							throw new IllegalStateException();
						break;
					default:
						throw new IllegalStateException();
					}
					done = true;					
				}
			}
//			System.out.println();
			previousPoints.add(next); // This point will be occupied now
			return new Pair<SEGMENT_TYPE, Point>(result, next);
		}
	}
	
	/**
	 * Return where the next point would be if a segment of the given type is placed
	 * @param previousType Type of the previous segment (could be null) 
	 * @param current Current segment location
	 * @param currentType Type of the current segment 
	 * @return Where next Point would be
	 */
	private static Point nextPoint(Point previousPoint, Point current, SEGMENT_TYPE currentType) {
		switch(currentType) {
			case UP: return new Point(current.x, current.y - 1);
			case DOWN: return new Point(current.x, current.y + 1);
			case RIGHT: return new Point(current.x+1, current.y);
			case LEFT: return new Point(current.x-1, current.y);
			case TOP_LEFT:
				if(previousPoint.y - 1 == current.y) return new Point(current.x+1, current.y); // Move right
				else {
					assert previousPoint.x - 1 == current.x;
					return new Point(current.x, current.y+1); // Move down
				}
			case TOP_RIGHT:
				if(previousPoint.y - 1 == current.y) return new Point(current.x-1, current.y); // Move left
				else {
					assert previousPoint.x + 1 == current.x;
					return new Point(current.x, current.y+1); // Move down
				}
			case BOTTOM_RIGHT:
				if(previousPoint.y + 1 == current.y) return new Point(current.x-1, current.y); // Move left
				else {
					assert previousPoint.x + 1 == current.x;
					return new Point(current.x, current.y-1); // Move up
				}
			case BOTTOM_LEFT:
				if(previousPoint.y + 1 == current.y) return new Point(current.x+1, current.y); // Move right
				else {
					assert previousPoint.x - 1 == current.x;
					return new Point(current.x, current.y-1); // Move up
				}
			default: throw new IllegalArgumentException("Valid SEGMENT_TYPE not specified");
		}
	}

	protected abstract List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type);
}
