package edu.utexas.cs.nn.gridTorus;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

public class TorusPredPreyGameTest {

	TorusPredPreyGame game1;
	TorusPredPreyGame game2;
	TorusPredPreyGame game3;

	public static final double doubleThreshold = .001;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController",
				"predatorCatchClose:false", "preyRRM:false" });
		MMNEAT.loadClasses();
	}

	@After
	public void tearDown() throws Exception {
		game1 = null;
		game2 = null;
		game3 = null;
		MMNEAT.clearClasses();
	}
	@Test
	public void testConstructor() {
		game1 = new TorusPredPreyGame(100, 100, 3, 2);
		game2 = new TorusPredPreyGame(75, 75, 3, 2);
		game3 = new TorusPredPreyGame(100, 100, 5, 8);

		assertEquals(game1.getWorld().height(), 100);
		assertEquals(game1.getWorld().width(), 100);
		assertEquals(game2.getWorld().height(), 75);
		assertEquals(game2.getWorld().width(), 75);
		assertEquals(game3.getWorld().height(), 100);
		assertEquals(game3.getWorld().width(), 100);

		assertEquals(game1.getPrey().length, 2);
		assertEquals(game2.getPrey().length, 2);
		assertEquals(game3.getPrey().length, 8);
		assertEquals(game1.getPredators().length, 3);
		assertEquals(game2.getPredators().length, 3);
		assertEquals(game3.getPredators().length, 5);

		assertEquals(game1.getTime(), 0);
		assertEquals(game2.getTime(), 0);
		assertEquals(game3.getTime(), 0);

		assertEquals(game1.getTimeLimit(), 1000);
		assertEquals(game2.getTimeLimit(), 1000);
		assertEquals(game3.getTimeLimit(), 1000);

	}

	@Test
	public void testAdvance() {
		game1 = new TorusPredPreyGame(100, 100, 3, 2);

		TorusWorld world = new TorusWorld(100, 100);
		TorusAgent pred1 = new TorusAgent(world, 0, 0, 0);
		TorusAgent pred2 = new TorusAgent(world, 0, 0, 0);
		TorusAgent pred3 = new TorusAgent(world, 0, 0, 0);
		TorusAgent prey1 = new TorusAgent(world, 0, 0, 1);
		TorusAgent prey2 = new TorusAgent(world, 0, 0, 1);

		game1.preds[0] = pred1;
		game1.preds[1] = pred2;
		game1.preds[2] = pred3;
		game1.preys[0] = prey1;
		game1.preys[1] = prey2;

		int[] pred1Move = new int[] { 0, 0 };
		int[] pred2Move = new int[] { 1, 0 };
		int[] pred3Move = new int[] { 1, 1 };
		int[] prey1Move = new int[] { 1, 1 };
		int[] prey2Move = new int[] { 0, 1 };

		int[][] predMoves = new int[][] { pred1Move, pred2Move, pred3Move };
		int[][] preyMoves = new int[][] { prey1Move, prey2Move };

		assertNotNull(prey1);
		assertEquals(pred1.getPosition(), new Tuple2D(0, 0));
		assertEquals(pred2.getPosition(), new Tuple2D(0, 0));
		assertEquals(pred3.getPosition(), new Tuple2D(0, 0));
		assertEquals(prey1.getPosition(), new Tuple2D(0, 0));
		assertEquals(prey2.getPosition(), new Tuple2D(0, 0));
		Tuple2D pred1Pos = pred1.getPosition();
		Tuple2D pred2Pos = pred2.getPosition();
		Tuple2D pred3Pos = pred3.getPosition();
		Tuple2D prey2Pos = prey2.getPosition();
		assertEquals(game1.getTime(), 0);

		game1.advance(predMoves, preyMoves);

		assertEquals(game1.getTime(), 1);
		assertEquals(game1.preds[0].getPosition(), pred1Pos.add(new Tuple2D(pred1Move[0], pred1Move[1])));
		assertEquals(game1.preds[1].getPosition(), pred2Pos.add(new Tuple2D(pred2Move[0], pred2Move[1])));
		assertEquals(game1.preds[2].getPosition(), pred3Pos.add(new Tuple2D(pred3Move[0], pred3Move[1])));
		assertNull(game1.preys[0]);
		assertEquals(game1.preys[1].getPosition(), prey2Pos.add(new Tuple2D(prey2Move[0], prey2Move[1])));

	}

}
