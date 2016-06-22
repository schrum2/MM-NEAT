package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

public class PreyRawalRajagopalanMiikkulainenObjectiveTest {

	TorusPredPreyGame game;
	TorusWorld world;

	public static final double doubleThreshold = .001;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask",
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController",
				"torusSenseTeammates:true" });
		MMNEAT.loadClasses();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFitnessWhenAllPreySurvive() {
		PreyRawalRajagopalanMiikkulainenObjective objective = new PreyRawalRajagopalanMiikkulainenObjective();
		double fitness;
		game = new TorusPredPreyGame(100, 100, 3, 2);
		world = new TorusWorld(100, 100);

		fitness = objective.score(game, null);
		assertEquals(fitness, 10.0, doubleThreshold);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFitnessWhenSomePreySurvive() {
		PreyRawalRajagopalanMiikkulainenObjective objective = new PreyRawalRajagopalanMiikkulainenObjective();
		double fitness;
		game = new TorusPredPreyGame(100, 100, 3, 2);
		world = new TorusWorld(100, 100);

		assertNotNull(game.getPrey()[0]);

		// make the prey get eaten to set it to null
		TorusAgent pred = game.getPredators()[0];
		TorusAgent prey = game.getPrey()[0];
		pred.move((int) -pred.getX(), (int) -pred.getY());
		prey.move((int) -prey.getX(), (int) -prey.getY());

		fitness = objective.score(game, null);
		assertEquals(fitness, 10.0, doubleThreshold);

		// call advance with bunch of (0,0) moves to update the game
		game.advance(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 } }, new int[][] { { 0, 0 }, { 0, 0 } });

		assertNull(game.getPrey()[0]);

		fitness = objective.score(game, null);
		assertEquals(fitness, 5.0, doubleThreshold);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFitnessWhenNoPreySurvive() {
		PreyRawalRajagopalanMiikkulainenObjective objective = new PreyRawalRajagopalanMiikkulainenObjective();
		double fitness;
		game = new TorusPredPreyGame(100, 100, 3, 2);
		world = new TorusWorld(100, 100);

		assertNotNull(game.getPrey()[0]);

		// make the prey get eaten to set it to null
		TorusAgent pred = game.getPredators()[0];
		TorusAgent prey = game.getPrey()[0];
		pred.move((int) -pred.getX(), (int) -pred.getY());
		prey.move((int) -prey.getX(), (int) -prey.getY());

		fitness = objective.score(game, null);
		assertEquals(fitness, 10.0, doubleThreshold);

		// call advance with bunch of (0,0) moves to update the game
		game.advance(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 } }, new int[][] { { 0, 0 }, { 0, 0 } });

		assertNull(game.getPrey()[0]);

		fitness = objective.score(game, null);
		assertEquals(fitness, 5.0, doubleThreshold);

		// set game time to 350
		while (game.getTime() < 350) {
			game.advance(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 } }, new int[][] { { 0, 0 }, { 0, 0 } });
		}
		// simulate that the last prey got eaten at this time
		TorusAgent prey2 = game.getPrey()[1];
		prey2.move((int) -prey2.getX(), (int) -prey2.getY());
		pred.move((int) -pred.getX(), (int) -pred.getY());

		// advance one more time
		game.advance(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 } }, new int[][] { { 0, 0 }, { 0, 0 } });

		fitness = objective.score(game, null);
		assertEquals(fitness, 1.755, doubleThreshold);

	}

}
