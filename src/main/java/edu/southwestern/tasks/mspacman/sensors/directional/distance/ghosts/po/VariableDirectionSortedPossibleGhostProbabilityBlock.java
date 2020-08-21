package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.util.datastructures.Quad;
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
	private final boolean sortThreats;
	private final boolean sortEdibles;
	
	public VariableDirectionSortedPossibleGhostProbabilityBlock(int order, boolean sortThreats, boolean sortEdibles) {
		this(-1, order, sortThreats, sortEdibles);
	}
		
	public VariableDirectionSortedPossibleGhostProbabilityBlock(int order) {
		this(-1, order, true, true);
	}
	
	public VariableDirectionSortedPossibleGhostProbabilityBlock(int dir, int order, boolean sortThreats, boolean sortEdibles) {
		super(dir);
		this.order = order;
		this.sortEdibles = sortEdibles;
		this.sortThreats = sortThreats;
	}

	@Override
	public double wallValue() {
		//if there is a wall, the probability of a ghost being there is surely zero
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		
		ArrayList<Quad<Integer, MOVE, Double, Double>> ghosts = gf.getPossibleGhostInfo();
			
		if(sortThreats && !sortEdibles) {
			ListIterator<Quad<Integer, MOVE, Double, Double>> itr = ghosts.listIterator();
			//remove edible ghosts from the list
			while(itr.hasNext()) {
				Quad<Integer, MOVE, Double, Double> current = itr.next();
				if(current.t4 > 0) {
					itr.remove();
				}
			}
		} else if(!sortThreats && sortEdibles) {
			ListIterator<Quad<Integer, MOVE, Double, Double>> itr = ghosts.listIterator();
			//remove threat ghosts from the list
			while(itr.hasNext()) {
				Quad<Integer, MOVE, Double, Double> current = itr.next();
				if(current.t4 <= 0) {
					itr.remove();
				}
			}
		}
		
//		System.out.println("----------------------------------------");
//		System.out.println(this.getLabel() + " in " + dir + ": ");
//		System.out.println(ghosts);
		
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
		return ghosts.get(order).t3;
	}

	@Override
	public String getLabel() {
		if(sortThreats && !sortEdibles) {
			return "Probability of " + order + " Closest Possible Threat Ghost in " + dir + "direction";
		} else if(!sortThreats && sortEdibles) {
			return "Probability of " + order + " Closest Possible Edible Ghost in " + dir + "direction";
		} else {
			return "Probability of " + order + " Closest Possible Ghost in " + dir + "direction";	
		}
	}
	

}
