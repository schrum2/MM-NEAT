package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import java.util.HashMap;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class NNHyperNEATPacManController extends NNPacManController {


	public static final int UP = 1;
	public static final int DOWN = 7;
	public static final int RIGHT = 5;
	public static final int LEFT = 3;
	private boolean pacManFullScreenOutput;

	HashMap<Integer, Network> networkForMaze;
	int currentMaze;
	public NNHyperNEATPacManController(HyperNEATCPPNGenotype genotype) {
		super(null);
		pacManFullScreenOutput = Parameters.parameters.booleanParameter("pacManFullScreenOutput");
		networkForMaze = new HashMap<>();
		@SuppressWarnings("rawtypes") // Type not needed here
		MsPacManTask task = (MsPacManTask) MMNEAT.task;
		for(int i = 0; i < Constants.NUM_MAZES; i++) {
			// Load facade for maze i
			GameFacade temp = new GameFacade(new Game(0, i));
			// Kills unreachable neurons
			task.customizeSubstratesForMaze(temp);
			// Don't create links for unusable nodes
			networkForMaze.put(i, genotype.getPhenotype()); // store network for maze
		}
		nn = networkForMaze.get(0); // start on first maze (TODO change for experiments that don't start on first maze)
		currentMaze = -1; // haven't seen any maze yet
	}
	//BTW, node list corresponds to all places pacman can legally travel to, not every coordinate in  maze
	@SuppressWarnings("rawtypes")
	@Override
	public int getDirection(GameFacade gf) {
		if(gf.getMazeIndex() != currentMaze) {
			currentMaze = gf.getMazeIndex();
			nn = networkForMaze.get(currentMaze); // recreate network
			// Need to update for substrate visualizer
			if(CommonConstants.monitorSubstrates) {
				((MsPacManTask) MMNEAT.task).substratesForMaze.get(currentMaze);
			}
		}
		double[] inputs = inputMediator.getInputs(gf, gf.getPacmanLastMoveMade());
		double[] outputs = nn.process(inputs);
		if(pacManFullScreenOutput) {
			int chosenNode = -1;
			double nodePreference = Double.NEGATIVE_INFINITY;		
			// get number of maze nodes
			for(int i = 0; i < gf.lengthMaze(); i++) {
				int x = gf.getNodeXCoord(i);
				int y = gf.getNodeYCoord(i);
				int j = getOutputIndexFromNodeCoord(x, y);
				if(outputs[j] > nodePreference) {//TODO can possibly speed up some by not checking every node since checking a lot of duplicates
					nodePreference = outputs[j];
					chosenNode = i;
				}
			}
			return gf.getNextPacManDirTowardsTarget(chosenNode);

		}
		else { 
			double[] realOutputs = new double[4];
			realOutputs[GameFacade.moveToIndex(MOVE.UP)] = outputs[UP];
			realOutputs[GameFacade.moveToIndex(MOVE.LEFT)] = outputs[LEFT];
			realOutputs[GameFacade.moveToIndex(MOVE.RIGHT)] = outputs[RIGHT];
			realOutputs[GameFacade.moveToIndex(MOVE.DOWN)] = outputs[DOWN];
			return StatisticsUtilities.argmax(realOutputs);
		}
	}

	public static int getOutputIndexFromNodeCoord(int x, int y) {
		int scaledX = x / MsPacManTask.MS_PAC_MAN_NODE_DIM;
		int scaledY = y / MsPacManTask.MS_PAC_MAN_NODE_DIM;
		int index = (scaledY * MsPacManTask.MS_PAC_MAN_SUBSTRATE_WIDTH) + scaledX;
		return index;
	}
}
