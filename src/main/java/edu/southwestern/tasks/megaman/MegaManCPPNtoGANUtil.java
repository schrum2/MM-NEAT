package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator.SEGMENT_TYPE;
import edu.southwestern.util.datastructures.Pair;

public class MegaManCPPNtoGANUtil {
	public static final int XPREF  = 0;
	public static final int YPREF = 1;
	public static final int BIASPREF = 2;
	public static int numRight = 0;
	public static int numLeft = 0;
	public static int numUp = 0;
	public static int numDown = 0;
	public static int numCorner = 0;
	public static int numDistinctSegments = 0;
	public static HashSet<List<List<Integer>>> distinct;
	public static int x = 0;
	public static int y = 0;
	public static Point previousMove;

	public static List<List<Integer>> cppnToMegaManLevel(MegaManGANGenerator megaManGenerator, Network cppn, int chunks, double[] inputMultipliers){
		numDistinctSegments = 0;
		numUp = 0;
		numDown = 0;
		numRight = 0;
		numLeft = 0;
		numCorner = 0;
		HashSet<List<List<Integer>>> distinct = new HashSet<>();

		// TODO: This method unnecessarily repeats code from MegaManGANUtil.longVectorToMegaManLevel
		//       We should refactor to avoid the repeated code
		
		HashSet<Point> previousPoints = new HashSet<>();
		Point currentPoint  = new Point(0,0);
		Point previousPoint = null;
		Point placementPoint = currentPoint;
		List<List<Integer>> level = new ArrayList<>();
		List<List<Integer>> segment = new ArrayList<>();
		for(int i = 0;i<chunks;i++) {
			
			double[] oneSegmentData = cppn.process(new double[] {
					inputMultipliers[XPREF] * currentPoint.x/(1.0*chunks),
					inputMultipliers[YPREF] * currentPoint.y/(1.0*chunks),
					inputMultipliers[BIASPREF] * 1.0});
						
			Pair<List<List<Integer>>, Point> segmentAndPoint = megaManGenerator.generateSegmentFromVariables(oneSegmentData, previousPoint, previousPoints, currentPoint);
			if(segmentAndPoint==null) {
				break; //NEEDS TO BE FIXED!! ORB WILL NOT BE PLACED
			}
			findSegmentData(megaManGenerator.getSegmentType());
			segment = segmentAndPoint.t1;
			distinct.add(segment);
			previousPoint = currentPoint; // backup previous
			currentPoint = segmentAndPoint.t2;
			if(i==chunks-1) MegaManGANUtil.placeOrb(segment);
			placementPoint = MegaManGANUtil.placeMegaManSegment(level, segment,  currentPoint, previousPoint, placementPoint);
		}
		
		MegaManGANUtil.postProcessingPlaceProperEnemies(level);
		numDistinctSegments = distinct.size();
		return level;
	}
	/**
	 * takes in a single segment type and adds to the total of that type
	 * @param segmentType the type of segment used in the placement of one segment
	 */
	private static void findSegmentData(SEGMENT_TYPE segmentType) {
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

	public static HashMap<String, Integer> findMiscSegments(List<List<Integer>> level){
		HashMap<String, Integer> j = new HashMap<>();
		j.put("numUp", numUp);
		j.put("numDown", numDown);
		j.put("numRight", numRight);
		j.put("numLeft", numLeft);
		j.put("numCorner", numCorner);
		j.put("numDistinctSegments", numDistinctSegments);
		return j;
	}
	
}
