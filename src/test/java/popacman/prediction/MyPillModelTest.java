package popacman.prediction;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.ExecutorFacade;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.facades.GhostControllerFacade;
import edu.southwestern.tasks.mspacman.facades.PacManControllerFacade;
import edu.southwestern.tasks.popacman.controllers.OldToNewPacManIntermediaryController;
import edu.southwestern.util.MiscUtil;
import oldpacman.controllers.NewPacManController;
import pacman.game.Game;
import popacman.CustomExecutor.Builder;
import popacman.DummyBlinkyForTesting;
import popacman.DummyInkyForTesting;
import popacman.DummyPinkyForTesting;
import popacman.DummySueForTesting;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * 
 * @author Will Price
 *
 */
public class MyPillModelTest {
	
	static OldToNewPacManIntermediaryController infoManager;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.southwestern.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.po.POCheckEachDirectionMediator", 
								
				//General Parameters
				"partiallyObservablePacman:true",  "pacmanPO:true",
				"rawScorePacMan:true", "ghostPO:false", "observePacManPO:true", 
				
				//Ghost Model Parameters
				"useGhostModel:false", "drawGhostPredictions:false",
				
				//PillModel Parameters
				"usePillModel:true", "drawPillModel:true", 
				
		//REMEMBER TO REMOVE
				"watch:true"});
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}
	
	@Test
	public void testMode() {
		
		
		///////////////////////////////////HOW TO SET UP A GAME SO THAT YOU HAVE ALL OF THE PARTS YOU NEED////////////////////////////////////////
		//BUILD AN ExecutorFacade
		Builder b = new Builder();
		ExecutorFacade testExecutorFacade = new ExecutorFacade(b.build());
		
		//CREATE A PacManControllerFacade THAT IS MADE FROM AN OldToNewPacManIntermediaryController:
		//THIS HAS ALL OF THE MODEL TRACKING WITHIN IT
		NewPacManController controller = new NewPacManController() { // These methods are never actually used

			@Override
			public int getAction(GameFacade gs, long timeDue) {
				return 0; // Not actually used
			}

			@Override
			public void logEvaluationDetails() {
				// Not actually used
			}
			
		};
		OldToNewPacManIntermediaryController infoManager = new OldToNewPacManIntermediaryController(controller);
		PacManControllerFacade testPacManControllerFacade = new PacManControllerFacade(infoManager);
		
		//CREATE a GameFacade
		GameFacade testGameFacade = new GameFacade(new Game(0));	
		// View the game to create tests, but disable afterward		
		
		//CREATE TESTING GHOSTS
		DummyBlinkyForTesting blinky = new DummyBlinkyForTesting(GHOST.BLINKY);
		DummyInkyForTesting inky = new DummyInkyForTesting(GHOST.INKY);
		DummyPinkyForTesting pinky = new DummyPinkyForTesting(GHOST.PINKY); 
		DummySueForTesting sue = new DummySueForTesting(GHOST.SUE);
		//PUT TESTING GHOSTS IN A MASController
		MASController boo = MASController.masControllerFactory(false, blinky, inky, pinky, sue);
		//PUT MASController IS A GhostControllerFacade
		GhostControllerFacade testGhostControllerFacade = new GhostControllerFacade(boo);
		
		GameFacade informedGameFacade = null;
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//TO DRAW A FRAME (AND CLOSE IT)//
		////testExecutorFacade.forceGameView.showGame();
		//testExecutorFacade.forceGameView.closeGame();		
		//////////////////////////////////
		
		//TO ADVANCE THE GAME/////////////////////
		//testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.LEFT);
		//THE MOVE DECIDES WHAT PACMAN DOES
		//////////////////////////////////////////
		
		//SET THE MOVES OF ALL OF THE GHOSTS TO BE LEFT UNTIL SET AGAIN
		blinky.setMove(MOVE.LEFT);
		pinky.setMove(MOVE.LEFT);
		inky.setMove(MOVE.LEFT);
		sue.setMove(MOVE.LEFT);
		
		Assert.assertNull(testGameFacade.pillModel);
		
		//FOR 5 TIMESTEPS, THE TIMESTEP BEFORE THE FIRST PILL IS EATEN		
		for(int i = 0; i < 5; i++) {
			//UPDATE THE GAME FOR i STEPS, SENDING PACMAN LEFT
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.LEFT);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			
			//It is set above 
			Assert.assertNotNull(testGameFacade.pillModel);
			System.out.println("Pill Model reference at time step " + i + ": " + informedGameFacade.pillModel.toString());
			//We have not gotten to a pill yet
			Assert.assertFalse(testGameFacade.pillModel.getPills().get(testGameFacade.getPacmanCurrentNodeIndex()));
			
			//DRAW THE GAME STATE
			////testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		testExecutorFacade.forceGameView.showGame();
		System.out.println("");
		
		//WE HAVEN'T YET EATEN A PILL
		int pillsEatenAtTime5 = testGameFacade.pillModel.getPillsEaten();
		int pacManLocAtTime5 = testGameFacade.getPacmanCurrentNodeIndex();
		Assert.assertTrue(pillsEatenAtTime5 == 0);
		Assert.assertTrue(testGameFacade.getScore() == 0.0);
		
		ArrayList<Integer> activePillsAtTime5 = new ArrayList<Integer>();
		for(int i : testGameFacade.getActivePillsIndices()) {
			activePillsAtTime5.add(i);
		}

		System.out.println("TIMESTEP 5:\t");
		System.out.println("Active Pills before any consumption: " + activePillsAtTime5.toString());
		System.out.println("pacmans location at time 5: " + testGameFacade.getPacmanCurrentNodeIndex());
		System.out.println("Is there a pill at index " + testGameFacade.getPacmanCurrentNodeIndex() + "? " + testGameFacade.pillModel.getPills().get(testGameFacade.getPacmanCurrentNodeIndex()));
		System.out.println("");
		
		testExecutorFacade.forceGameView.showGame();
		Assert.assertTrue(testGameFacade.pillModel.getPills().get(972));
		
		//MOVE PACMAN LEFT, WE EAT A PILL ON THIS STEP
		testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.LEFT);
		//UPDATE THE MODELS
		testGameFacade.pillModel = informedGameFacade.pillModel;
		testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
		
		ArrayList<Integer> activePillsAtTime6 = new ArrayList<Integer>();
		for(int i : testGameFacade.getActivePillsIndices()) {
			activePillsAtTime6.add(i);
		}
				
		//PACMAN DID MOVE INTO THE PILL
		Assert.assertFalse(testGameFacade.getPacmanCurrentNodeIndex() == pacManLocAtTime5);
		Assert.assertTrue(testGameFacade.getScore() == 10.0);
		
		System.out.println("TIMESTEP 6:\t");
		System.out.println("pacmans location at time 6: " + testGameFacade.getPacmanCurrentNodeIndex());
		System.out.println("Active Pills after any consumption: " + activePillsAtTime6.toString());
		System.out.println("Is there a pill at index " + testGameFacade.getPacmanCurrentNodeIndex() + "? " + testGameFacade.pillModel.getPills().get(testGameFacade.getPacmanCurrentNodeIndex()));
		System.out.println("");
		
		//there is no active pill at 972
		Assert.assertFalse(activePillsAtTime6.contains(972));
		//there surely was a pill eaten
		Assert.assertTrue(activePillsAtTime6.size() < activePillsAtTime5.size());
		//there should be no pill at 972 in the pill model
		//TODO
		//Assert.assertFalse(testGameFacade.pillModel.getPills().get(972));
		
		//THERE IS NO LONGER A PILL IN THIS LOCATION
		//Assert.assertFalse(testGameFacade.pillModel.getPills().get(testGameFacade.getPacmanCurrentNodeIndex()));
		//Assert.assertTrue(testGameFacade.pillModel.getPillsEaten() > pillsEatenAtTime5);
		
		//DRAW THE GAME
		testExecutorFacade.forceGameView.showGame();
	
	}
	
}
