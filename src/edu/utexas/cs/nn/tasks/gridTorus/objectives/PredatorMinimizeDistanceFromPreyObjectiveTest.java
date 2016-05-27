package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

public class PredatorMinimizeDistanceFromPreyObjectiveTest {

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
		TorusPredPreyGame game = new TorusPredPreyGame(100, 100, 3, 2);
		TorusWorld world = new TorusWorld(100, 100);
		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();

		PredatorMinimizeDistanceFromPreyObjective objective = new PredatorMinimizeDistanceFromPreyObjective();
		double fitness;

		// move all prey and predators to 0,0
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[1].move((int) -prey[1].getX(), (int) -prey[1].getY());
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());

		// each predator is 0 distance away from each prey, meaning the distance
		// double array should be the following:
		// [ {0,0} , {0,0} , {0,0} ]
		// the sum array should hold [0,0,0], so the final value should be 0,
		// which is the best possible score
		fitness = objective.score(game, null);
		assertEquals(fitness, 0, doubleThreshold);

		// move all predators to 50,50
		preds[0].move(50, 50);
		preds[1].move(50, 50);
		preds[2].move(50, 50);
		// each predator is 100 distance away from each prey, meaning the
		// distance double array should be the following:
		// [ {100,100} , {100,100} , {100,100} ]
		// the sum array should hold [200,200,200], so the final value should be
		// -600, which is the worst possible score
		fitness = objective.score(game, null);
		assertEquals(fitness, -600, doubleThreshold);
		assertEquals(fitness, objective.minScore(), doubleThreshold);

		// make preds[0] location 0,0
		preds[0].move(-50, -50);
		// [ {0,0} , {100,100} , {100,100} ]
		// the sum array should hold [0,200,200], so the final value should be
		// -400
		fitness = objective.score(game, null);
		assertEquals(fitness, -400, doubleThreshold);

		// make preds[1] location 32,61
		preds[1].move(-18, 11);
		// [ {0,0} , {71,71} , {100,100} ]
		// the sum array should hold [0,142,200], so the final value should be
		// -342
		fitness = objective.score(game, null);
		assertEquals(fitness, -342, doubleThreshold);

		// make preds[2] location 91, 6
		preds[2].move(41, -44);
		// [ {0,0} , {71,71} , {15,15} ]
		// the sum array should hold [0,142,30], so the final value should be
		// -172
		fitness = objective.score(game, null);
		assertEquals(fitness, -172, doubleThreshold);

		// make prey[1] location 20, 20
		prey[1].move(20, 20);
		// [ {0,40} , {71,53} , {15,43} ]
		// the sum array should hold [40,124,58], so the final value should be
		// -222
		fitness = objective.score(game, null);
		assertEquals(fitness, -222, doubleThreshold);

		// make one prey null and test distance objective:
		int[][] predMoves = new int[][] { { 0, 1 }, { 0, 0 }, { 0, 0 } };
		int[][] preyMoves = new int[][] { { 0, 0 }, { 0, 0 } };
		for (int i = 0; i < 20; i++)
			game.advance(predMoves, preyMoves);
		predMoves = new int[][] { { 1, 0 }, { 0, 0 }, { 0, 0 } };
		preyMoves = new int[][] { { 0, 0 }, { 0, 0 } };
		for (int i = 0; i < 20; i++)
			game.advance(predMoves, preyMoves);
		assertNull(prey[1]);

		// [ {40,0} , {71,0} , {15,0} ]
		// the sum array should hold [40,71,15], so the final value should be
		// -126
		fitness = objective.score(game, null);
		assertEquals(fitness, -126, doubleThreshold);

	}

}
