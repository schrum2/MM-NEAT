package edu.utexas.cs.nn.gridTorus.controllers;

import static org.junit.Assert.*;

import java.util.Arrays;

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
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "torusTimeLimit:1000", 
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask", 
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController", "torusSenseTeammates:true"});
		MMNEAT.loadClasses();
	}

	@Test
	public void testGetAction() {
		game = new TorusPredPreyGame(100,100,3,2);
		world = new TorusWorld(100,100);
		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();

		PreyFleeClosestPredatorController preyController = new PreyFleeClosestPredatorController();

		//set the other predator locations to 0,0
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());

		//make the prey be at location 1,1
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[0].move(1, 1);

		//set main the predator location to 1,0
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[0].move(1,0);

		System.out.println(prey[0].getX());
		System.out.println(prey[0].getY());
		System.out.println(preds[0].getX());
		System.out.println(preds[0].getY());
		System.out.println(Arrays.toString(preyController.getAction(prey[0], world, preds, prey)));
		
		//the movement away from the predator can be anything except -1 on the Y-axis
		assertFalse(preyController.getAction(prey[0], world, preds, prey)[1] == -1);


		//		/** 
		//		 * The getAction method takes in the controlled agent, the world and predators and prey as arrays.
		//		 * The prey moves away from the closest predator in a sequence depending on what sequence from the
		//		 * prey's possible move sequences leaves them the farthest away from the closest predator.
		//		 */
		//		@Override
		//		public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		//			TorusAgent closestPredator = me.closestAgent(preds);
		//			double[] moveDistances = new double[preyActions().length];
		//			for(int i = 0; i < preyActions().length; i++) {
		//				double distance = closestPredator.distance(me.getPosition().add(new Tuple2D(preyActions()[i][0], preyActions()[i][1])));
		//				moveDistances[i] = distance;
		//			}
		//			return preyActions()[StatisticsUtilities.argmax(moveDistances)];
		//		}
	}

}
