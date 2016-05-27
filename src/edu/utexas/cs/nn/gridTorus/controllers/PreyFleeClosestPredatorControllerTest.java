package edu.utexas.cs.nn.gridTorus.controllers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

public class PreyFleeClosestPredatorControllerTest {
	TorusPredPreyGame game;
	TorusWorld world;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "torusTimeLimit:1000",
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask",
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController",
				"torusSenseTeammates:true" });
		MMNEAT.loadClasses();
	}

	@Test
	public void testGetAction() {
		game = new TorusPredPreyGame(100, 100, 3, 2);
		world = new TorusWorld(100, 100);
		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();

		PreyFleeClosestPredatorController preyController = new PreyFleeClosestPredatorController();

		// set the other predator locations to 0,0
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());
		// make the prey be at location 1,1
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[0].move(1, 1);
		// set main the predator location to 1,0
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[0].move(1, 0);
		// the movement away from the predator can be anything except -1 on the
		// Y-axis
		assertFalse(preyController.getAction(prey[0], world, preds, prey)[1] == -1);

		// make the prey be at location 5,5
		prey[0].move(4, 4);
		// set predator one location to 5,0
		preds[0].move(4, 0);
		// set predator two location to 0,5
		preds[1].move(0, 5);
		// set predator three location to 5,10
		preds[2].move(5, 10);
		// the movement away from the predator can be anything except -1 on the
		// Y-axis
		// pred[0] is seen as the closest predator in the tie break, at 5,0
		int[] preyAction = preyController.getAction(prey[0], world, preds, prey);
		assertFalse(preyAction[1] == -1);
		// make the actual movement from the getAction based on the controller
		prey[0].move(preyAction[0], preyAction[1]);
		// now prey is at either 4,5 , 5,6 or 6,5
		assertTrue((prey[0].getX() == 4 && prey[0].getY() == 5) || (prey[0].getX() == 5 && prey[0].getY() == 6)
				|| (prey[0].getX() == 6 && prey[0].getY() == 5));

		// reset the prey to be at location 0,0 then move to location 6,11
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[0].move(6, 11);
		// pred[2] now closest at 5,10, so two possible (and random) movements
		// should be 1,0 or 0,1 and never the other two
		preyAction = preyController.getAction(prey[0], world, preds, prey);
		assertFalse(preyAction[0] == -1);
		assertFalse(preyAction[1] == -1);
		assertTrue((preyAction[0] == 1 && preyAction[1] == 0) || (preyAction[0] == 0 && preyAction[1] == 1));
		// so prey should be at either 6,12 or 7,11 after movement
		prey[0].move(preyAction[0], preyAction[1]);
		assertTrue((prey[0].getX() == 6 && prey[0].getY() == 12) || (prey[0].getX() == 7 && prey[0].getY() == 11));

	}

}
