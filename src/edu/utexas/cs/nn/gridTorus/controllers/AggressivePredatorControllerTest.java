package edu.utexas.cs.nn.gridTorus.controllers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

public class AggressivePredatorControllerTest {

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPreyVsStaticPredatorsTask",
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:3",
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

		AggressivePredatorController controller = new AggressivePredatorController();

		// move all the agents to location 0,0
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[1].move((int) -prey[1].getX(), (int) -prey[1].getY());
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());

		// move a prey to location 10,10 and a predator to location 8,10 and
		// second prey to 50,50 to get it out of the way
		prey[0].move(10, 10);
		prey[1].move(50, 50);
		preds[0].move(8, 10);

		int[] predAct0 = controller.getAction(preds[0], world, preds, prey);
		int[] predAct1 = controller.getAction(preds[1], world, preds, prey);
		// pred[0] should move to the right (1,0) to approach prey[0]
		assertEquals(predAct0[0], 1);
		assertEquals(predAct0[1], 0);
		// pred[1] should do either 1,0 or 0,1 to move right or down to approach
		// the prey
		assertTrue((predAct1[0] == 1 && predAct1[1] == 0) || (predAct1[0] == 0 && predAct1[1] == 1));

		// do the movement
		preds[0].move(predAct0[0], predAct0[1]);
		preds[1].move(predAct1[0], predAct1[1]);
		// pred[0] should be at 9,10
		assertTrue(preds[0].getX() == 9 && preds[0].getY() == 10);
		// pred[1] should be at either 1,0 or 0,1
		assertTrue((preds[1].getX() == 1 && preds[1].getY() == 0) || (preds[1].getX() == 0 && preds[1].getY() == 1));

		// next action should be to move right (1,0) again from point 9,10 to
		// 10,10 where the prey is
		predAct0 = controller.getAction(preds[0], world, preds, prey);
		assertEquals(predAct0[0], 1);
		assertEquals(predAct0[1], 0);

		// do the movement
		preds[0].move(predAct0[0], predAct0[1]);
		// pred[0] should be at 10,10
		assertTrue(preds[0].getX() == 10 && preds[0].getY() == 10);
		// pred[0] and prey[0] should be at the same location of 10,10
		assertTrue(preds[0].isCoLocated(prey[0]));

		// next actions should be to do nothing
		predAct0 = controller.getAction(preds[0], world, preds, prey);
		assertEquals(predAct0[0], 0);
		assertEquals(predAct0[1], 0);

		// move preds[2] to location 51,51
		preds[2].move(51, 51);
		int[] predAct2 = controller.getAction(preds[2], world, preds, prey);
		// pred[2] should do either -1,0 or 0,-1 to move left or up to approach
		// prey[1]
		assertTrue((predAct2[0] == -1 && predAct2[1] == 0) || (predAct2[0] == 0 && predAct2[1] == -1));

		// do the movement
		preds[2].move(predAct2[0], predAct2[1]);
		// preds[2] should be at location 50,51 or 51,50, which was randomized
		assertTrue(
				(preds[2].getX() == 50 && preds[2].getY() == 51) || (preds[2].getX() == 51 && preds[2].getY() == 50));

		// do one more movement based on the next action
		predAct2 = controller.getAction(preds[2], world, preds, prey);
		preds[2].move(predAct2[0], predAct2[1]);
		// whether at 50,51 or 51,50 before, the predator should now be at 50,50
		// with the prey
		assertTrue(preds[2].isCoLocated(prey[1]));

	}

}
