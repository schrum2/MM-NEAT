package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

public class PredatorCatchCloseObjectiveTest {

	private static final double doubleThreshold = .001;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPreyVsStaticPredatorsTask",
				"allowDoNothingActionForPredators:true", "allowDoNothingActionForPreys:true", "torusPreys:2",
				"torusPredators:3",
				"staticPredatorController:edu.utexas.cs.nn.gridTorus.controllers.AggressivePredatorController",
		"torusSenseTeammates:true" });
		MMNEAT.loadClasses();
	}

	@Test
	public void test() {

		// In this case:
		// WEIGHT = 3.3333333333333333333 , NO_PREY_SCORE = 10
		// d = sumOfDistances / (numPrey * numPreds * maxDistance)
		// d = 1 - d
		// d*WEIGHT + numCaught*WEIGHT

		TorusPredPreyGame game = new TorusPredPreyGame(100, 100, 3, 2);
		TorusWorld world = new TorusWorld(100, 100);
		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();

		PredatorCatchCloseObjective objective = new PredatorCatchCloseObjective();
		double fitness;

		// move all prey and predators to 0,0
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[1].move((int) -prey[1].getX(), (int) -prey[1].getY());
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());
		// d = 1 , numPrey = 2, numCaught = 0
		fitness = objective.score(game, null);
		assertEquals(fitness, (1 / 3.0) * objective.NO_PREY_SCORE, doubleThreshold);

		// worst score: max distance and no prey caught
		preds[0].move(50, 50);
		preds[1].move(50, 50);
		preds[2].move(50, 50);
		// d = 0 , numPrey = 2, numCaught = 0
		fitness = objective.score(game, null);
		assertEquals(fitness, 0, doubleThreshold); 
		assertEquals(fitness, objective.minScore(), doubleThreshold);

		// check that the score is correct when some but not all 
		// prey are caught
		prey[0].move(60,60);
		int[][] predMoves = new int[][] { { 1, 0 }, { 0, 0 }, { 0, 0 } };
		int[][] preyMoves = new int[][] { { 0, 0 }, { 0, 0 } };
		for(int i = 0; i < 10; i++)
			game.advance(predMoves, preyMoves);
		predMoves = new int[][] { { 0, 1 }, { 0, 0 }, { 0, 0 } };
		preyMoves = new int[][] { { 0, 0 }, { 0, 0 } };
		for(int i = 0; i < 10; i++)
			game.advance(predMoves, preyMoves);
		assertNull(prey[0]);
		// d = .5333333333333333 , numPrey = 2, numCaught = 1
		//1.77777776 + 3.33333333 = 5.11111109
		fitness = objective.score(game, null);
		assertEquals(fitness, 5.11111109, doubleThreshold); 

		// make prey null and see if max fitness score is given
		// because all of the prey were caught
		// move all alive prey and predators to 0,0
		prey[1].move((int) -prey[1].getX(), (int) -prey[1].getY());
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());
		predMoves = new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 } };
		preyMoves = new int[][] { { 0, 0 }, { 0, 0 } };
		game.advance(predMoves, preyMoves);
		assertNull(prey[0]);
		assertNull(prey[1]);
		fitness = objective.score(game, null);
		assertEquals(fitness, objective.NO_PREY_SCORE, doubleThreshold);

	}

}
