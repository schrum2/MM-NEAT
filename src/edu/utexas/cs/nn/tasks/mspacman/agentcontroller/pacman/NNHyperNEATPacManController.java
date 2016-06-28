package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import java.util.Arrays;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pacman.game.Constants.MOVE;

public class NNHyperNEATPacManController extends NNPacManController {

	public NNHyperNEATPacManController(Network n) {
		super(n);
	}
//BTW, node list corresponds to all places pacman can legally travel to, not every coordinate in  maze
	@Override
	public int getDirection(GameFacade gf) {
		double[] inputs = inputMediator.getInputs(gf, gf.getPacmanLastMoveMade());
		double[] outputs = nn.process(inputs);
/** the size of the output substrate is incorrect for this below schematic
		int chosenNode = -1;
		double nodePreference = Double.NEGATIVE_INFINITY;		
		// get number of maze nodes
		for(int i = 0; i < gf.lengthMaze(); i++) {
			int x = gf.getNodeXCoord(i);
			int y = gf.getNodeYCoord(i);
			int j = getOutputIndexFromNodeCoord(x, y);
			if(outputs[j] > nodePreference) {//TODO something is way messed up here
				nodePreference = outputs[j];
				chosenNode = i;
			}
		}
		return gf.getNextPacManDirTowardsTarget(chosenNode);
	
 * 
 */
                // TODO Make these public static final constants
		int UP = 1;
		int DOWN = 7;
		int RIGHT = 5;
		int LEFT = 3;
		double[] realOutputs = new double[4];
		realOutputs[GameFacade.moveToIndex(MOVE.UP)] = outputs[UP];
		realOutputs[GameFacade.moveToIndex(MOVE.LEFT)] = outputs[LEFT];
		realOutputs[GameFacade.moveToIndex(MOVE.RIGHT)] = outputs[RIGHT];
		realOutputs[GameFacade.moveToIndex(MOVE.DOWN)] = outputs[DOWN];
		return StatisticsUtilities.argmax(realOutputs);
	}
	
	public static int getOutputIndexFromNodeCoord(int x, int y) {
		int scaledX = x / MsPacManTask.MS_PAC_MAN_NODE_DIM;
		int scaledY = y / MsPacManTask.MS_PAC_MAN_NODE_DIM;
		int index = (scaledY * MsPacManTask.MS_PAC_MAN_SUBSTRATE_WIDTH) + scaledX;
		//System.out.println("x:"+x+",y:"+y+",scaledX:"+scaledX+",scaledY:"+scaledY+",index:"+index);
		return index;
	}
}
