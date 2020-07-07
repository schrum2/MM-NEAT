package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.util.datastructures.Pair;

public class MegaManCPPNtoGANUtil {
	public static final int XPREF  = 0;
	public static final int YPREF = 1;
	public static final int BIASPREF = 2;
	public static int numHorizontal = 0;
	public static int numUp = 0;
	public static int numDown = 0;
	public static int numCorner = 0;
	public static int numDistinctSegments = 0;
	public static HashSet<List<List<Integer>>> distinct;
	public static int x = 0;
	public static int y = 0;
	public static Point previousMove;

	public static List<List<Integer>> cppnToMegaManLevel(MegaManGANGenerator megaManGenerator, Network cppn, int chunks, double[] inputMultipliers){

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
			segment = segmentAndPoint.t1;
			previousPoint = currentPoint; // backup previous
			currentPoint = segmentAndPoint.t2;
			if(i==chunks-1) MegaManGANUtil.placeOrb(segment);
			placementPoint = MegaManGANUtil.placeMegaManSegment(level, segment,  currentPoint, previousPoint, placementPoint);
		}
		
		
		return level;
	}

	public static HashMap<String, Integer> findMiscSegments(List<List<Integer>> level){
		HashMap<String, Integer> j = new HashMap<>();
		j.put("numUp", numUp);
		j.put("numDown", numDown);
		j.put("numHorizontal", numHorizontal);
		j.put("numCorner", numCorner);
		j.put("numDistinctSegments", numDistinctSegments);
		return j;
	}
	
}
