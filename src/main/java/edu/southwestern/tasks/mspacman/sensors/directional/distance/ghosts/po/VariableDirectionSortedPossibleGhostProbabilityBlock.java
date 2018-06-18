package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.util.datastructures.Quad;
import edu.southwestern.util.datastructures.Triple;
import pacman.game.Constants.MOVE;


/**
 * Possible ghosts are those that we can see and those that we have a probability for.
 * This block sorts possible ghosts by distance and returns the orderth ghost away's probability.
 * 
 * @author Will Price
 *
 */
public class VariableDirectionSortedPossibleGhostProbabilityBlock extends VariableDirectionBlock{

	private final int order;
	
	public VariableDirectionSortedPossibleGhostProbabilityBlock(int order) {
		this(-1, order);
	}
	
	public VariableDirectionSortedPossibleGhostProbabilityBlock(int dir, int order) {
		super(dir);
		this.order = order;
	}

	@Override
	public double wallValue() {
		//if there is a wall, the probability of a ghost being there is surely zero
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		ArrayList<Quad<Integer, MOVE, Double, Double>> ghosts = gf.getPossibleGhostInfo();
		
		if (order >= ghosts.size()) {
			return 0.0; // Target in lair will result in distance of
								// infinity
		}
		
		Collections.sort(ghosts, new Comparator<Quad<Integer, MOVE, Double, Double>>(){

			@Override
			public int compare(Quad<Integer, MOVE, Double, Double> arg0, Quad<Integer, MOVE, Double, Double> arg1) {
				
				//the length of the path from pacman to arg0 (ghost) in dir
				int disToArg0 = gf.getDirectionalPath(gf.getPacmanCurrentNodeIndex(), arg0.t1, dir).length;
				//the length of the path from pacman to arg1 (ghost) in dir
				int disToArg1 = gf.getDirectionalPath(gf.getPacmanCurrentNodeIndex(), arg1.t1, dir).length;
				
				if(disToArg0 > disToArg1) {
					return 1;
				} else if (disToArg0 == disToArg1) {
					return 0;
				} else {
					return -1;
				}
				
			}
		});
		
		//returns the shortest path to the order (1st, 2nd, 3rd, etc) possible ghost away
		return ghosts.get(order).t3 ;
	}

	@Override
	public String getLabel() {
		return "Probability of " + order + " Closest Possible Ghost in " + dir + "direction";
	}
	

}
