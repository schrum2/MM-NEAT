package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

public class NNHyperNEATPacManController extends NNPacManController {

	public NNHyperNEATPacManController(Network n) {
		super(n);
	}
//BTW, node list corresponds to all places pacman can legally travel to, not every coordinate in  maze
	@Override
	public int getDirection(GameFacade gf) {
		// TODO make HyperNEAT input mediator
		double[] inputs = inputMediator.getInputs(gf, gf.getPacmanLastMoveMade());
		double[] outputs = nn.process(inputs);

		int chosenNode = -1;
		double nodePreference = Double.NEGATIVE_INFINITY;		
		// get number of maze nodes
		for(int i = 0; i < gf.lengthMaze(); i++) {
			int x = gf.getNodeXCoord(i);
			int y = gf.getNodeYCoord(i);
			int j = getOutputIndexFromNodeCoord(x, y);
			if(outputs[j] > nodePreference) {//TODO find a way to account for the fact that there are 5 different substrates
				nodePreference = outputs[j];
				chosenNode = i;
			}
		}
		return gf.getNextPacManDirTowardsTarget(chosenNode);
	}
	
	public static int getOutputIndexFromNodeCoord(int x, int y) { 
		int index = (y * MsPacManTask.MS_PAC_MAN_SUBSTRATE_WIDTH) + x;
		return index;
	}
}
