package edu.southwestern.tasks.gridTorus.objectives;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusPredPreyGame;
import edu.southwestern.parameters.Parameters;

public class PredatorCatchCloseObjectiveTest {

	private static final double doubleThreshold = .001;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"task:edu.southwestern.tasks.gridTorus.TorusEvolvedPreyVsStaticPredatorsTask",
				"allowDoNothingActionForPredators:true", "allowDoNothingActionForPreys:true", "torusPreys:2",
				"torusPredators:3",
				"staticPredatorController:edu.southwestern.gridTorus.controllers.AggressivePredatorController",
		"torusSenseTeammates:true" });
		MMNEAT.loadClasses();
	}

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void test() {

		TorusPredPreyGame game = new TorusPredPreyGame(100, 100, 3, 2);
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
		assertEquals(fitness, (1 / 2.0) * PredatorCatchCloseObjective.NO_PREY_SCORE, doubleThreshold);

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
		//distances from prey 1 (prey 1 hasnt been caught): 100, 100, 80
		// 280/300 = .933333333333333333333333333...
		// 1 - .93333333333333333333333333333333... = 0.066666666666666666666666666666666666666667...
		//1.0 + 0.066666666666666666666666666666666666666667.. = 1.0666666666666667..
		fitness = objective.score(game, null);
		assertEquals(fitness, 1.0666666666666667, doubleThreshold); 

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
		assertEquals(fitness, PredatorCatchCloseObjective.NO_PREY_SCORE, doubleThreshold);

	}

}
