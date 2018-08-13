package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.specific.po;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import org.apache.commons.lang3.ArrayUtils;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.util.datastructures.Quad;
import pacman.game.Constants.MOVE;

/**
 * @author Will Price, Jacob Schrum
 */
public class VariableDirectionSortedPossibleGhostIncomingBlock extends VariableDirectionBlock {

	private final int order;
	private final boolean sortThreats;
	private final boolean sortEdibles;

	/**
	 * @param order
	 */
	public VariableDirectionSortedPossibleGhostIncomingBlock(int order) {
		this(order, true, true);
	}
	
	public VariableDirectionSortedPossibleGhostIncomingBlock(int order, boolean sortThreats, boolean sortEdibles) {
		super(-1);
		this.order = order;
		this.sortThreats = sortThreats;
		this.sortEdibles = sortEdibles;
	}

	@Override
	public double wallValue() {
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

		//calculate if the ghost is incoming
		int current = gf.getPacmanCurrentNodeIndex();
		assert current != -1 : "we can't see pacman";
		int[] neighbors = gf.neighbors(current);
		//assert neighbors[pacmanDir] != -1 : "Pacman dir is a wall: " + pacmanDir + "; " + Arrays.toString(neighbors);
		try {
		int[] ghostPath = gf.poG.getShortestPath(ghosts.get(order).t1.intValue(), current, ghosts.get(order).t2);
		return ArrayUtils.contains(ghostPath, neighbors[dir]) ? 1 : 0;
		} catch(java.lang.NullPointerException e) {
			System.out.println(ghosts);
			throw e;
		}
	}

	@Override
	public String getLabel() {
		if(sortThreats && !sortEdibles) {
			return order + " Closest Incoming Possible Threat Ghost in " + dir + "direction";
		} else if(!sortThreats && sortEdibles) {
			return order + " Closest Incoming Possible Edible Ghost in " + dir + "direction";
		} else {
			return order + " Closest IncomingPossible Ghost in " + dir + "direction";	
		}
	}
}

