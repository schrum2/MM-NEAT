package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

public class TorusPredatorsByProximitySensorBlockTest {

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "torusTimeLimit:1000", 
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask", 
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:3",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController", "torusSenseTeammates:true",
				"torusSenseByProximity:true"
		});
		MMNEAT.loadClasses();
	}

	@Test
	public void testNumSensors() {
		TorusPredatorsByProximitySensorBlock block = new TorusPredatorsByProximitySensorBlock();
		//should be 6 sensors because there are two sensors (X and Y) for each pred, and there are three preds
		assertEquals(block.numSensors(),6);
		
		//try again with a different number of predators
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "torusTimeLimit:1000", 
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask", 
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:5",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController", "torusSenseTeammates:true",
				"torusSenseByProximity:true"
		});
		MMNEAT.loadClasses();
		block = new TorusPredatorsByProximitySensorBlock();
		//should be 10 sensors because there are two sensors (X and Y) for each pred, and there are 5 preds
		assertEquals(block.numSensors(),10);
	}
	
	@Test
	public void testSensorLabels() {
		TorusPredatorsByProximitySensorBlock block = new TorusPredatorsByProximitySensorBlock();
		assertEquals(Arrays.toString(block.sensorLabels()), 
				"[X Offset to Closest Pred 0, Y Offset to Closest Pred 0, X Offset to Closest Pred 1, Y Offset to Closest Pred 1, "
				+ "X Offset to Closest Pred 2, Y Offset to Closest Pred 2]");
		
		//try again with a different number of predators
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "torusTimeLimit:1000", 
				"task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask", 
				"allowDoNothingActionForPredators:true", "torusPreys:2", "torusPredators:5",
				"staticPreyController:edu.utexas.cs.nn.gridTorus.controllers.PreyFleeClosestPredatorController", "torusSenseTeammates:true",
				"torusSenseByProximity:true"
		});
		MMNEAT.loadClasses();
		block = new TorusPredatorsByProximitySensorBlock();
		assertEquals(Arrays.toString(block.sensorLabels()), 
				"[X Offset to Closest Pred 0, Y Offset to Closest Pred 0, X Offset to Closest Pred 1, Y Offset to Closest Pred 1, "
				+ "X Offset to Closest Pred 2, Y Offset to Closest Pred 2, X Offset to Closest Pred 3, Y Offset to Closest Pred 3, "
				+ "X Offset to Closest Pred 4, Y Offset to Closest Pred 4]");
	}
	
	@Test
	public void testSensorValues(){
		TorusPredatorsByProximitySensorBlock block = new TorusPredatorsByProximitySensorBlock();
		TorusPredPreyGame game = new TorusPredPreyGame(100,100,3,2);
		TorusWorld world = new TorusWorld(100,100);
		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();
		
		//move all predators and prey to 0,0
		prey[0].move((int) -prey[0].getX(), (int) -prey[0].getY());
		prey[1].move((int) -prey[1].getX(), (int) -prey[1].getY());
		preds[0].move((int) -preds[0].getX(), (int) -preds[0].getY());
		preds[1].move((int) -preds[1].getX(), (int) -preds[1].getY());
		preds[2].move((int) -preds[2].getX(), (int) -preds[2].getY());
		
		//move the predators to locations 11,22 , 48,85 and 99, 36 respectively and prey[1] to location 50,57
		preds[0].move(11, 22);
		preds[1].move(48, 85);
		preds[2].move(99, 36);
		prey[1].move(50,57);

		//from prey[0] at point 0,0 , the closest predators are pred[0] then pred[2], then pred[1]
		assertEquals(Arrays.toString(block.sensorValues(prey[0], world, preds, prey)), "[0.11, 0.22, -0.01, 0.36, 0.48, -0.15]");
		//from prey[1] at point 50,57 , the closest predators are pred[1] then pred[2], then pred[0]
		assertEquals(Arrays.toString(block.sensorValues(prey[1], world, preds, prey)), "[-0.02, 0.28, 0.49, -0.21, -0.39, -0.35]");
		//from pred[0] at point 11,22 , the closest predators are pred[0] then pred[2] then pred[1]
		assertEquals(Arrays.toString(block.sensorValues(preds[0], world, preds, prey)), "[0.0, 0.0, -0.12, 0.14, 0.37, -0.37]");
		//from pred[1] at point 48,85 , the closest predators are pred[1] then pred[0] then pred[2]
		assertEquals(Arrays.toString(block.sensorValues(preds[1], world, preds, prey)), "[0.0, 0.0, -0.37, 0.37, -0.49, -0.49]");
		//from pred[2] at point 99,36 , the closest predators are pred[2] then pred[0] then pred[1]
		assertEquals(Arrays.toString(block.sensorValues(preds[2], world, preds, prey)), "[0.0, 0.0, 0.12, -0.14, 0.49, 0.49]");
		
		
	}

}
