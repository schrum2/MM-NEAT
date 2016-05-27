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
		/*
		 * NO_PREY_SCORE = 10 maxDistance = height/2 + width/2 d =
		 * sumOfDistances / (numPrey * numPreds * maxDistance) d = 1 - d WEIGHT
		 * = (NO_PREY_SCORE/(numPrey+1.0)) d*WEIGHT + numCaught*WEIGHT
		 */

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
		assertEquals(fitness, (1 / 3.0) * 10.0, doubleThreshold);

	}

}
