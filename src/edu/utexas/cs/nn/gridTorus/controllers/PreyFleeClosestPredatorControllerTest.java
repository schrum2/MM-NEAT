package edu.utexas.cs.nn.gridTorus.controllers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;

public class PreyFleeClosestPredatorControllerTest {
	TorusPredPreyGame game;
	
	@Before
	public void setUp() throws Exception {
		game = new TorusPredPreyGame(100,100,3,2);
	}

	@Test
	public void test() {
		
		fail("dang");
//		TorusAgent closestPredator = me.closestAgent(preds);
//		double[] moveDistances = new double[preyActions().length];
//		for(int i = 0; i < preyActions().length; i++) {
//			double distance = closestPredator.distance(me.getPosition().add(new Tuple2D(preyActions()[i][0], preyActions()[i][1])));
//			moveDistances[i] = distance;
//		}
//		return preyActions()[StatisticsUtilities.argmax(moveDistances)];
	}

}
