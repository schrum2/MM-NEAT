package edu.southwestern.tasks.megaman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator.SEGMENT_TYPE;

public class MegaManTrackSegmentType {
	private int numRight;
	private int numLeft;
	private int numUp;
	private int numDown;
	private int numCorner;
	private int numDistinctSegments;
	
	public MegaManTrackSegmentType() {
		numRight = 0;
		numLeft = 0;
		numUp = 0;
		numDown = 0;
		numCorner = 0;
		numDistinctSegments = 0;
	}
	
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
		assert countIntegrityCheck();
	}
	
	private boolean countIntegrityCheck() {
		
		int[] segmentTypes = new int[] {numUp, numDown, numRight, numLeft, numCorner};
		String[] segmentNames = new String[] {"numUp", "numDown", "numRight", "numLeft", "numCorner"};
		
		for (int i = 0; i < segmentTypes.length; i++) {
			assert segmentTypes[i] <= Parameters.parameters.integerParameter("megaManGANLevelChunks") : (segmentNames[i] + " (" + segmentTypes[i] + ") exceeded max level chunks of " + Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		}
		return true;
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
