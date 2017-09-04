package edu.southwestern.gridTorus;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.util2D.Tuple2D;

public class TorusAgentTest {

	TorusAgent pred;
	TorusAgent prey;

	public static final double doubleThreshold = .001;

	@Before
	public void setUp() throws Exception {
		pred = new TorusAgent(new TorusWorld(100, 100), 0, 0, 0);
		prey = new TorusAgent(new TorusWorld(100, 100), 0, 0, 1);
	}
	@After
	public void tearDown() throws Exception {
		pred = null;
		prey = null;
		MMNEAT.clearClasses();
	}
	
	@Test
	public void testGetPosition() {
		// returns Tuple2D getPosition()
		// start agents at upper left point, 0,0
		Tuple2D coordinates = pred.getPosition();
		assertEquals(coordinates.getX(), 0.0, doubleThreshold);
		assertEquals(coordinates.getY(), 0.0, doubleThreshold);
		coordinates = prey.getPosition();
		assertEquals(coordinates.getX(), 0.0, doubleThreshold);
		assertEquals(coordinates.getY(), 0.0, doubleThreshold);
		// start at another point
		pred = new TorusAgent(new TorusWorld(100, 100), 51, 78, 0);
		prey = new TorusAgent(new TorusWorld(100, 100), 69, 42, 1);
		coordinates = pred.getPosition();
		assertEquals(coordinates.getX(), 51.0, doubleThreshold);
		assertEquals(coordinates.getY(), 78.0, doubleThreshold);
		coordinates = prey.getPosition();
		assertEquals(coordinates.getX(), 69.0, doubleThreshold);
		assertEquals(coordinates.getY(), 42.0, doubleThreshold);
	}

	@Test
	public void testDistance() {
		// returns Manhattan distance
		// param object ILocated2D (some object in the grid with an x,y
		// coordinate on the grid)
		TorusAgent otherPred = new TorusAgent(new TorusWorld(100, 100), 48, 24, 0);
		TorusAgent otherPrey = new TorusAgent(new TorusWorld(100, 100), 79, 33, 1);

		assertEquals(prey.distance(prey), 0, doubleThreshold);
		assertEquals(prey.distance(pred), 0, doubleThreshold);
		assertEquals(prey.distance(otherPred), 72.0, doubleThreshold);
		assertEquals(prey.distance(otherPrey), 54.0, doubleThreshold);

		assertEquals(pred.distance(pred), 0, doubleThreshold);
		assertEquals(pred.distance(prey), 0, doubleThreshold);
		assertEquals(pred.distance(otherPred), 72.0, doubleThreshold);
		assertEquals(pred.distance(otherPrey), 54.0, doubleThreshold);

		assertEquals(otherPrey.distance(otherPrey), 0, doubleThreshold);
		assertEquals(otherPrey.distance(otherPred), 40, doubleThreshold);
		assertEquals(otherPrey.distance(prey), 54.0, doubleThreshold);
		assertEquals(otherPrey.distance(pred), 54, doubleThreshold);

		assertEquals(otherPred.distance(otherPrey), 40, doubleThreshold);
		assertEquals(otherPred.distance(otherPred), 0, doubleThreshold);
		assertEquals(otherPred.distance(prey), 72.0, doubleThreshold);
		assertEquals(otherPred.distance(pred), 72.0, doubleThreshold);

		TorusAgent eatenPrey = new TorusAgent(new TorusWorld(100, 100), 12, 99, 1);
		eatenPrey = null;
		assertEquals(prey.distance(eatenPrey), Double.POSITIVE_INFINITY, doubleThreshold);
		assertEquals(pred.distance(eatenPrey), Double.POSITIVE_INFINITY, doubleThreshold);
		assertEquals(otherPrey.distance(eatenPrey), Double.POSITIVE_INFINITY, doubleThreshold);
		assertEquals(otherPred.distance(eatenPrey), Double.POSITIVE_INFINITY, doubleThreshold);
	}

	@Test
	public void testGetX() {
		TorusAgent otherPred = new TorusAgent(new TorusWorld(100, 100), 48, 24, 0);
		TorusAgent otherPrey = new TorusAgent(new TorusWorld(100, 100), 79, 33, 1);
		assertEquals(prey.getX(), 0, doubleThreshold);
		assertEquals(pred.getX(), 0, doubleThreshold);
		assertEquals(otherPrey.getX(), 79, doubleThreshold);
		assertEquals(otherPred.getX(), 48, doubleThreshold);
	}

	@Test
	public void testGetY() {
		TorusAgent otherPred = new TorusAgent(new TorusWorld(100, 100), 48, 24, 0);
		TorusAgent otherPrey = new TorusAgent(new TorusWorld(100, 100), 79, 33, 1);
		assertEquals(prey.getY(), 0, doubleThreshold);
		assertEquals(pred.getY(), 0, doubleThreshold);
		assertEquals(otherPrey.getY(), 33, doubleThreshold);
		assertEquals(otherPred.getY(), 24, doubleThreshold);
	}

	@Test
	public void testMove() {
		assertEquals(prey.getPosition(), new Tuple2D(0, 0));
		prey.move(5, 17);
		assertEquals(prey.getPosition(), new Tuple2D(5, 17));
		prey.move(3, 1);
		assertEquals(prey.getPosition(), new Tuple2D(8, 18));
		prey.move(-13, -8);
		assertEquals(prey.getPosition(), new Tuple2D(95, 10));
		prey.move(5, 110);
		assertEquals(prey.getPosition(), new Tuple2D(0, 20));

		assertEquals(pred.getPosition(), new Tuple2D(0, 0));
		pred.move(5, 17);
		assertEquals(pred.getPosition(), new Tuple2D(5, 17));
		pred.move(3, 1);
		assertEquals(pred.getPosition(), new Tuple2D(8, 18));
		pred.move(-13, -8);
		assertEquals(pred.getPosition(), new Tuple2D(95, 10));
		pred.move(5, 110);
		assertEquals(pred.getPosition(), new Tuple2D(0, 20));

	}

	@Test
	public void testIsCoLocated() {
		// param other torus agent
		// return whether cell is shared by other agent
		TorusAgent nullAgent = null;
		TorusAgent otherPred = new TorusAgent(new TorusWorld(100, 100), 48, 24, 0);
		TorusAgent otherPrey = new TorusAgent(new TorusWorld(100, 100), 79, 33, 1);

		assertTrue(prey.isCoLocated(prey));
		assertTrue(prey.isCoLocated(pred));
		prey.move(1, 0);
		assertFalse(prey.isCoLocated(pred));
		prey.move(-1, 0);
		assertTrue(prey.isCoLocated(pred));
		prey.move(0, 1);
		assertFalse(prey.isCoLocated(pred));
		prey.move(0, -1);
		assertTrue(prey.isCoLocated(pred));
		assertFalse(prey.isCoLocated(nullAgent));

		assertFalse(prey.isCoLocated(otherPred));
		assertFalse(prey.isCoLocated(otherPrey));
		assertFalse(pred.isCoLocated(otherPred));
		assertFalse(pred.isCoLocated(otherPrey));

	}

	@Test
	public void testClosestAgent() {
		TorusAgent[] preys = new TorusAgent[4];
		TorusAgent[] preds = new TorusAgent[4];
		preds[0] = new TorusAgent(new TorusWorld(100, 100), 10, 10, 0);
		preds[1] = new TorusAgent(new TorusWorld(100, 100), 20, 20, 0);
		preds[2] = new TorusAgent(new TorusWorld(100, 100), 30, 30, 0);
		preys[0] = new TorusAgent(new TorusWorld(100, 100), 30, 10, 1);
		preys[1] = new TorusAgent(new TorusWorld(100, 100), 40, 40, 1);
		preys[2] = new TorusAgent(new TorusWorld(100, 100), 10, 25, 1);

		assertSame(pred.closestAgent(preds), preds[0]);
		assertSame(pred.closestAgent(preys), preys[2]);

		assertSame(prey.closestAgent(preds), preds[0]);
		assertSame(prey.closestAgent(preys), preys[2]);

		assertSame(preds[1].closestAgent(preds), preds[1]);
		assertSame(preys[2].closestAgent(preys), preys[2]);

		assertSame(preds[0].closestAgent(preys), preys[2]);
		assertSame(preds[1].closestAgent(preys), preys[2]);
		assertSame(preds[2].closestAgent(preys), preys[0]);

		assertSame(preys[0].closestAgent(preds), preds[0]);
		assertSame(preys[1].closestAgent(preds), preds[2]);
		assertSame(preys[2].closestAgent(preds), preds[0]);

		TorusAgent negPred = new TorusAgent(new TorusWorld(100, 100), 96, 95, 0);
		TorusAgent negPrey = new TorusAgent(new TorusWorld(100, 100), 93, 94, 1);

		preds[3] = negPred;
		preys[3] = negPrey;

		assertSame(pred.closestAgent(preds), preds[3]);
		assertSame(pred.closestAgent(preys), preys[3]);

		assertSame(prey.closestAgent(preds), preds[3]);
		assertSame(prey.closestAgent(preys), preys[3]);

	}

	@Test
	public void testDistances() {
		TorusAgent[] preys = new TorusAgent[3];
		TorusAgent[] preds = new TorusAgent[3];
		preds[0] = new TorusAgent(new TorusWorld(100, 100), 10, 10, 0);
		preds[1] = new TorusAgent(new TorusWorld(100, 100), 20, 20, 0);
		preds[2] = new TorusAgent(new TorusWorld(100, 100), 30, 30, 0);
		preys[0] = new TorusAgent(new TorusWorld(100, 100), 40, 50, 1);
		preys[1] = new TorusAgent(new TorusWorld(100, 100), 95, 5, 1);
		preys[2] = new TorusAgent(new TorusWorld(100, 100), 70, 60, 1);

		double[] dists = pred.distances(preys);
		assertEquals(dists[0], 90.0, doubleThreshold);
		assertEquals(dists[1], 10.0, doubleThreshold);
		assertEquals(dists[2], 70.0, doubleThreshold);

		dists = pred.distances(preds);
		assertEquals(dists[0], 20.0, doubleThreshold);
		assertEquals(dists[1], 40.0, doubleThreshold);
		assertEquals(dists[2], 60.0, doubleThreshold);

		dists = prey.distances(preds);
		assertEquals(dists[0], 20.0, doubleThreshold);
		assertEquals(dists[1], 40.0, doubleThreshold);
		assertEquals(dists[2], 60.0, doubleThreshold);

		dists = prey.distances(preds);
		assertEquals(dists[0], 20.0, doubleThreshold);
		assertEquals(dists[1], 40.0, doubleThreshold);
		assertEquals(dists[2], 60.0, doubleThreshold);

		dists = preds[0].distances(preys);
		assertEquals(dists[0], 70.0, doubleThreshold);
		assertEquals(dists[1], 20.0, doubleThreshold);
		assertEquals(dists[2], 90.0, doubleThreshold);

		dists = preds[1].distances(preds);
		assertEquals(dists[0], 20.0, doubleThreshold);
		assertEquals(dists[1], 0.0, doubleThreshold);
		assertEquals(dists[2], 20.0, doubleThreshold);

		dists = preys[0].distances(preds);
		assertEquals(dists[0], 70.0, doubleThreshold);
		assertEquals(dists[1], 50.0, doubleThreshold);
		assertEquals(dists[2], 30.0, doubleThreshold);

		dists = preys[1].distances(preys);
		assertEquals(dists[0], 90.0, doubleThreshold);
		assertEquals(dists[1], 00.0, doubleThreshold);
		assertEquals(dists[2], 70.0, doubleThreshold);

	}

	@Test
	public void testShortestXOffset() {
		TorusAgent[] preys = new TorusAgent[3];
		TorusAgent[] preds = new TorusAgent[3];
		preds[0] = new TorusAgent(new TorusWorld(100, 100), 10, 10, 0);
		preds[1] = new TorusAgent(new TorusWorld(100, 100), 20, 20, 0);
		preds[2] = new TorusAgent(new TorusWorld(100, 100), 30, 30, 0);
		preys[0] = new TorusAgent(new TorusWorld(100, 100), 40, 50, 1);
		preys[1] = new TorusAgent(new TorusWorld(100, 100), 95, 5, 1);
		preys[2] = new TorusAgent(new TorusWorld(100, 100), 70, 60, 1);

		assertEquals(prey.shortestXOffset(prey), 0.0, doubleThreshold);
		assertEquals(prey.shortestXOffset(pred), 0.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(pred), 0.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(prey), 0.0, doubleThreshold);

		assertEquals(pred.shortestXOffset(preds[0]), 10.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(preds[1]), 20.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(preds[2]), 30.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(preys[0]), 40.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(preys[1]), -5.0, doubleThreshold);
		assertEquals(pred.shortestXOffset(preys[2]), -30.0, doubleThreshold);

		assertEquals(prey.shortestXOffset(preds[0]), 10.0, doubleThreshold);
		assertEquals(prey.shortestXOffset(preds[1]), 20.0, doubleThreshold);
		assertEquals(prey.shortestXOffset(preds[2]), 30.0, doubleThreshold);
		assertEquals(prey.shortestXOffset(preys[0]), 40.0, doubleThreshold);
		assertEquals(prey.shortestXOffset(preys[1]), -5.0, doubleThreshold);
		assertEquals(prey.shortestXOffset(preys[2]), -30.0, doubleThreshold);

	}

	@Test
	public void testShortestYOffset() {
		TorusAgent[] preys = new TorusAgent[3];
		TorusAgent[] preds = new TorusAgent[3];
		preds[0] = new TorusAgent(new TorusWorld(100, 100), 10, 10, 0);
		preds[1] = new TorusAgent(new TorusWorld(100, 100), 20, 20, 0);
		preds[2] = new TorusAgent(new TorusWorld(100, 100), 30, 30, 0);
		preys[0] = new TorusAgent(new TorusWorld(100, 100), 40, 50, 1);
		preys[1] = new TorusAgent(new TorusWorld(100, 100), 95, 5, 1);
		preys[2] = new TorusAgent(new TorusWorld(100, 100), 70, 60, 1);

		assertEquals(prey.shortestYOffset(prey), 0.0, doubleThreshold);
		assertEquals(prey.shortestYOffset(pred), 0.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(pred), 0.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(prey), 0.0, doubleThreshold);

		assertEquals(pred.shortestYOffset(preds[0]), 10.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(preds[1]), 20.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(preds[2]), 30.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(preys[0]), -50.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(preys[1]), 5.0, doubleThreshold);
		assertEquals(pred.shortestYOffset(preys[2]), -40.0, doubleThreshold);

		assertEquals(prey.shortestYOffset(preds[0]), 10.0, doubleThreshold);
		assertEquals(prey.shortestYOffset(preds[1]), 20.0, doubleThreshold);
		assertEquals(prey.shortestYOffset(preds[2]), 30.0, doubleThreshold);
		assertEquals(prey.shortestYOffset(preys[0]), -50.0, doubleThreshold);
		assertEquals(prey.shortestYOffset(preys[1]), 5.0, doubleThreshold);
		assertEquals(prey.shortestYOffset(preys[2]), -40.0, doubleThreshold);

	}

}
