package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import java.util.ArrayList;

/**
 * Figure out sensor value indicating distance to element of a candidate group
 * in a particular direction.
 * 
 * @author Jacob Schrum
 */
public abstract class VariableDirectionDistanceBlock extends VariableDirectionBlock {

	public static ArrayList<Integer> excludedNodes = new ArrayList<Integer>();

	public final int numberToExclude;

	public VariableDirectionDistanceBlock(int dir) {
		this(dir, 0);
	}

	public VariableDirectionDistanceBlock(int dir, int exclude) {
		super(dir);
		this.numberToExclude = exclude;
	}

	public double getValue(GameFacade gf) {
		
		if (numberToExclude == 0) {
			excludedNodes.clear();
		}
		
		assert numberToExclude == excludedNodes.size() : "Not excluding the right number of node results: "
				+ numberToExclude + ":" + excludedNodes;
		
		final int current = gf.getPacmanCurrentNodeIndex();
		
		final int[] targets = ArrayUtil.setDifference(getTargets(gf), excludedNodes);
		
		if (targets.length == 0) {
			// excludedNodes.add(-1); // non-existant node
			return 1.0; // Distance is "infinity"
		} else {
			Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, dir);
			
			//Used for PO conditions
			if(pair.t2 == null) {
				return 1.0; //distance is "infinity"
			}
			assert pair != null : "stack trace";
			excludedNodes.add(pair.t1); // Exclude this result from the next
										// call
			int[] path = pair.t2;
			assert path != null : "The above if should have caught the null path case";
			double distance = path.length;
			double result = (Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE);
			// System.out.println("Distance:"+distance+":result:"+result);
			return result;
		}
	}

	public double wallValue() {
		return 1;
	}

	@Override
	public String getLabel() {
		return "Distance to " + numberToExclude + " Nearest " + getType();
	}

	public abstract String getType();

	public abstract int[] getTargets(GameFacade gf);
}
