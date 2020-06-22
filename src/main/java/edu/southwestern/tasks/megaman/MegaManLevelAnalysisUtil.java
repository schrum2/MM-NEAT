package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
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
				if(level.get(y).get(x)!=9) {
					totalTiles++;
				}
			}
		}
		return totalTiles;
	}
	
	
	/**
	 * Takes in a level and returns all information regarding the A* search
	 * @param level List<List<Integer>> representing the level
	 * @return
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
	 * @param mostRecentVisited
	 * @return
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
