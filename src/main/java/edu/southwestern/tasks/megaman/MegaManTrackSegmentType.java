package edu.southwestern.tasks.megaman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator.SEGMENT_TYPE;

public class MegaManTrackSegmentType {
	public static int numRight = 0;
	public static int numLeft = 0;
	public static int numUp = 0;
	public static int numDown = 0;
	public static int numCorner = 0;
	public static int numDistinctSegments = 0;
	
	/**
	 * takes in a single segment type and adds to the total of that type
	 * @param segmentType the type of segment used in the placement of one segment
	 * @param distinct 
	 * @param segment 
	 */
	public void findSegmentData(SEGMENT_TYPE segmentType, List<List<Integer>> segment, HashSet<List<List<Integer>>> distinct) {
		distinct.add(segment);
		numDistinctSegments = distinct.size();

		switch(segmentType) {
		case UP: 
			numUp++;
			break;
		case DOWN: 
			numDown++;
			break;
		case RIGHT:
			numRight++;
			break;
		case LEFT: 
			numLeft++;
			break;
		case TOP_LEFT: 
			numCorner++;
			break;
		case TOP_RIGHT:	
			numCorner++;
			break;
		case BOTTOM_RIGHT: 
			numCorner++;
			break;
		case BOTTOM_LEFT: 
			numCorner++;
			break;
		default: throw new IllegalArgumentException("Valid SEGMENT_TYPE not specified");
		}
	}
	
	public HashMap<String, Integer> findMiscSegments(){
		
		HashMap<String, Integer> j = new HashMap<>();
		j.put("numUp", numUp);
		j.put("numDown", numDown);
		j.put("numRight", numRight);
		j.put("numLeft", numLeft);
		j.put("numCorner", numCorner);
		j.put("numDistinctSegments", numDistinctSegments);
		
		
		
		numRight = 0;
		numLeft = 0;
		numUp = 0;
		numDown = 0;
		numCorner = 0;
		numDistinctSegments = 0;
		return j;
		
	}
}
