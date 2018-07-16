package edu.southwestern.tasks.mspacman.multitask.po;

import java.util.ArrayList;

import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.util.datastructures.Quad;
import pacman.game.Constants.MOVE;

/**
 * Has three modes. 0) there are no visible or predicted ghosts. 1) there
 * are only edible visible or predicted ghosts. 2) there are only threat visible
 * or predicted ghosts.
 * @author pricew
 *
 */
public class POProbableGhostStateModeSelector3Mod extends MsPacManModeSelector {

	public static final int NO_GHOSTS_VISIBLE = 0;
	public static final int EDIBLE_GHOSTS_VISIBLE = 1;
	public static final int THREAT_GHOSTS_VISIBLE = 2;
	public static final int MIXED_GHOSTS_VISIBLE = 3;
	ArrayList<Quad<Integer, MOVE, Double, Double>> predictedGhostInfo;
	
	public POProbableGhostStateModeSelector3Mod() {
		super();
	}
	
	@Override
	public int mode() {
		predictedGhostInfo = gs.getPossibleGhostInfo();
		if(predictedGhostInfo.size() == 0) {
			return 0;	
		}
				
		//The count of visible, edible ghosts. We need a specific count because we
		//do not know if a ghost is edible when we cannot see it, even if we have a prediction
		//of its location.
		//int visibleEdibleGhostCount = 0;
		//For all predicted ghosts
//		for(Quad<Integer, MOVE, Double, Double> ghost : predictedGhostInfo) {
//			//if they are visible
//			A:if(gs.poG.isNodeObservable(ghost.t1)) {
//				//if we can eat them
//				try {
//					for(int i = 0; i < pacman.game.Constants.NUM_GHOSTS; i++) {
//						if(gs.isGhostEdible(i)) {					
//							visibleEdibleGhostCount++;
//						}
//					}
//				} catch (Exception e) {
//					break A;
//				}
//			}
//		}
		
		//there are no predicted ghosts
		if(predictedGhostInfo.size() == 0) {
			return 0;
		}
		
		//if the only ghosts we have predicted are those we can see are edible
		if(containsOnlyEdibleGhosts(predictedGhostInfo)) {
			return 1;
		}
		
		return 2;
		
	}
	
	/**
	 * Takes an ArrayList of predicted ghost information and determines whether all preidcted ghosts are edible or not.
	 * @param predictedGhostInfo
	 * @return
	 */
	private boolean containsOnlyEdibleGhosts(ArrayList<Quad<Integer, MOVE, Double, Double>> predictedGhostInfo) {
		//for each predicted ghost location
		for(Quad<Integer, MOVE, Double, Double> q : predictedGhostInfo) {
			//if the probability that it is edible is 0
			if(q.t4 == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int numModes() {
		//We have four modes
		return 4;
	}

	@Override
	public int[] associatedFitnessScores() {
		throw new UnsupportedOperationException("We don't do that in these here parts");
	}

}
