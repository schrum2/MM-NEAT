package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.astar.MegaManState.MegaManAction;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Quad;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
/**
 * This is a utility class that is primarily used in MegaManLevelTask
 * @author Benjamin Capps
 *
 */
public class MegaManLevelAnalysisUtil {
	/**
	 * finds the total number of tiles in a level
	 * @param level the level as a List<List<Integer>>
	 * @return totalTiles the total number of tiles in a level excluding null 
	 */
	public static int findTotalTiles(List<List<Integer>> level) {
		int totalTiles = 0;
		for(int y=0;y<level.size();y++) {
			for(int x=0;x<level.get(0).size();x++) {
				if(level.get(y).get(x)!=MegaManVGLCUtil.UNIQUE_NULL) {
					totalTiles++;
				}
			}
		}
		return totalTiles;
	}
	/**
	 * gets total passable tiles from the level to determine connectivity.
	 * @param level list of integer representation of a level
	 * @return total number of passable tiles
	 */
	public static double findTotalPassableTiles(List<List<Integer>> level) {
		double totalAirTiles = 0.0;
		for(int y=0;y<level.size();y++) {
			for(int x=0;x<level.get(0).size();x++) {
				if(level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_AIR||
						//level.get(y).get(x)>MegaManVGLCUtil.UNIQUE_ENEMY_THRESH_HOLD||
						level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_WATER||
						level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_BREAKABLE||
						level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_PLAYER||
//						level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_ORB||
						level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_LADDER||
						level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_MOVING_PLATFORM) {
					totalAirTiles++;
				}
			}
		}
		return totalAirTiles;
	}
	/**
	 * finds miscellaneous information about enemies in the level
	 * (what type, total, etc)
	 * @param level
	 * @return miscData - the HashMap that contains info about what type of enemy
	 */
	public static HashMap<String, Integer> findMiscEnemies(List<List<Integer>> level) {
		HashMap<String, Integer> miscData = new HashMap<>();
		
		
		int totalEnemies = 0;
		int totalWallEnemies = 0;
		int totalGroundEnemies = 0;
		int totalFlyingEnemies = 0;
		for(int y=0;y<level.size();y++) {
			for(int x=0;x<level.get(0).size();x++) {
				if(level.get(y).get(x)>MegaManVGLCUtil.UNIQUE_ENEMY_THRESH_HOLD) {
					totalEnemies++;
				}
				if(level.get(y).get(x)>MegaManVGLCUtil.UNIQUE_ENEMY_THRESH_HOLD) {
					if(level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_OCTOPUS_BATTERY_LEFTRIGHT_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_OCTUPUS_BATTERY_UPDOWN_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_BEAK_ENEMY) {
						totalWallEnemies++;
					}else if(level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_MET_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_PICKET_MAN_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_BIG_EYE_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_SPINE_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_CRAZY_RAZY_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_JUMPER_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_GUNNER_ENEMY||
							level.get(y).get(x)==MegaManVGLCUtil.UNIQUE_SCREW_BOMBER_ENEMY) {
						totalGroundEnemies++;
					}else{
						totalFlyingEnemies++;
					}
				}
				
					
			}
		}
		miscData.put("numEnemies", totalEnemies);
		miscData.put("numWallEnemies", totalWallEnemies);
		miscData.put("numGroundEnemies", totalGroundEnemies);
		miscData.put("numFlyingEnemies", totalFlyingEnemies);
		return miscData;
	}
	/**
	 * 
	 * 
	 * **NOT USED**
	 * Find information regarding what type of segment
	 * Irrelevant!!! Information is gathered inside classes that extend MegaManLevelTask
	 * @param level list of list of integer level
	 * @return
	 */
	public static HashMap<String, Integer> findMiscSegments(List<List<Integer>> level) {
		HashMap<String, Integer> miscData = new HashMap<>();
		HashSet<Point> segmentPoints = new HashSet<>();
		int numCorners = 0;
		int numHorizontal = 0;
		int numUp = 0;
		int numDown = 0;
		
		for(int y=0;y<level.size();y++) {
			for(int x=0;x<level.get(0).size();x++) {
				int chunksx = x/16;
				int chunksy = y/14;
				boolean ex = x%16==0;
				boolean wy = y%14==0;
				if(ex&&wy&&!segmentPoints.contains(new Point(chunksx, chunksy))) {
					int rightScreenSide = chunksx+16;
					int y1 = chunksy;
					int x2 = rightScreenSide-16;
					boolean left = MegaManVGLCUtil.canGoLeft(level,rightScreenSide,y1);
					boolean right =  MegaManVGLCUtil.canGoRight(level,rightScreenSide,y1);
					boolean down =  MegaManVGLCUtil.canGoDown(level,rightScreenSide,y1);
					boolean up =  MegaManVGLCUtil.canGoUp(level,rightScreenSide,y1);
					//System.out.println(new Point(rightScreenSide-x2,y1));
					Point point = new Point(rightScreenSide-x2,y1);
					if((up&&left&&!right&&!down&&!segmentPoints.contains(point))||
							(up&&right&&!left&&!down&&!segmentPoints.contains(point))||
							(down&&right&&!up&&!left&&!segmentPoints.contains(point))||
							(down&&left&&!right&&!up&&!segmentPoints.contains(point))) { //lower right

						segmentPoints.add(point);
						
						numCorners++;
					}
					if(!up&&!down&&(right||left)&&!segmentPoints.contains(point)) {
						numHorizontal++;
						segmentPoints.add(point);

					}
					if(down&&!up&&!segmentPoints.contains(point)) {
						segmentPoints.add(point);
						numDown++;
					}
					if(!down&&up&&!segmentPoints.contains(point)) {
						segmentPoints.add(point);
						numUp++;
					}
				}
				
					
			}
		}
		
		miscData.put("numCorners", numCorners);
		miscData.put("numHorizontal", numHorizontal);
		miscData.put("numUp", numUp);
		miscData.put("numDown", numDown);
		return miscData;
	}
	
	/**
	 * Takes in a level and returns all information regarding the A* search
	 * @param level List<List<Integer>> representing the level
	 * @return Quad  containing misc data about A*
	 */
	public static Quad<HashSet<MegaManState>, ArrayList<MegaManAction>, MegaManState, Double> 
	performAStarSearchAndCalculateAStarDistance(List<List<Integer>> level) {
		//declares variable to be initizalized in the if statements below 
		MegaManState start;
		List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level);
		start = new MegaManState(levelCopy);
		Search<MegaManAction,MegaManState> search = new AStarSearch<>(MegaManState.manhattanToOrb); //initializes a search based on the heuristic 
		HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence = null;
		double simpleAStarDistance = -1; //intialized to hold distance of solution path, or -1 if search fails
		//calculates the Distance to the farthest gold as a fitness fucntion 
		try { 
			actionSequence = ((AStarSearch<MegaManAction, MegaManState>) search).search(start, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
			if(actionSequence == null) {
				simpleAStarDistance = -1.0;
			} else {
				simpleAStarDistance = 1.0*actionSequence.size();

			}
		} catch(IllegalStateException e) {
			simpleAStarDistance = -1.0;
			System.out.println("failed search");
			//e.printStackTrace();
		}
		mostRecentVisited = ((AStarSearch<MegaManAction, MegaManState>) search).getVisited();
		return new Quad<HashSet<MegaManState>, ArrayList<MegaManAction>, MegaManState, Double>(mostRecentVisited,actionSequence,start,simpleAStarDistance);
	}

	/**
	 * Calculates the connectivity in the level
	 * @param mostRecentVisited The set of states traversed by A*
	 * @return connectivityOfLevel - the connectivity of the level
	 */
	public static double caluclateConnectivity(HashSet<MegaManState> mostRecentVisited) {
		//calculates the amount of the level that was covered in the search, connectivity.
		HashSet<Point> visitedPoints = new HashSet<>();
		double connectivityOfLevel = -1;
		for(MegaManState s : mostRecentVisited) {
			visitedPoints.add(new Point(s.currentX,s.currentY));
		}
		connectivityOfLevel = 1.0*visitedPoints.size();
		return connectivityOfLevel;
	}
}
