package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po;

import static org.junit.Assert.*;

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
public class VariableDirectionSortedPossibleGhostDistanceBlockTest {
	
	static VariableDirectionSortedPossibleGhostDistanceBlock firstClosest;
	static VariableDirectionSortedPossibleGhostDistanceBlock secondClosest;
	static VariableDirectionSortedPossibleGhostDistanceBlock thirdClosest;
	static VariableDirectionSortedPossibleGhostDistanceBlock fourthClosest;
	//should always return new int[0], there aren't five ghosts
	static VariableDirectionSortedPossibleGhostDistanceBlock fifthClosest;
	static OldToNewPacManIntermediaryController infoManager;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.southwestern.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.po.POCheckEachDirectionMediator", 
				"useGhostModel:true", "drawGhostPredictions:true", "partiallyObservablePacman:true",  "pacmanPO:true",
				"rawScorePacMan:true", "ghostPO:false", "observePacManPO:true", "usePillModel:false" });
		
		firstClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(0);
		secondClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(1);
		thirdClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(2);
		fourthClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(3);
		fifthClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(4);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		firstClosest = null;
		secondClosest = null;
		thirdClosest = null;
		fourthClosest = null;
		fifthClosest = null;
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
		//testExecutorFacade.forceGameView.showGame();
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
		
		
		//FOR 17 TIMESTEPS
		for(int i = 0; i < 17; i++) {
			//UPDATE THE GAME FOR i STEPS, SENDING PACMAN LEFT
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.LEFT);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		
		//DRAW THE GAME
		testExecutorFacade.forceGameView.showGame();
		
		//FOR 7 TIME STEPS
		for(int i = 0; i < 7; i++) {
			//UPDATE THE GAME, MOVE PACMAN UP
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.UP);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		
		//FOR 15 TIME STEPS
		for(int i = 0; i < 15; i++) {
			//UPDATE THE GAME, MOVE PACMAN RIGHT
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.RIGHT);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		//DRAW THE GAME STATE
		testExecutorFacade.forceGameView.showGame();
		
		assertArrayEquals(firstClosest.getTargets(testGameFacade), new int[0]);
		assertArrayEquals(secondClosest.getTargets(testGameFacade), new int[0]);
		assertArrayEquals(thirdClosest.getTargets(testGameFacade), new int[0]);
		assertArrayEquals(fourthClosest.getTargets(testGameFacade), new int[0]);
		assertArrayEquals(fifthClosest.getTargets(testGameFacade), new int[0]);
	
		
		//assert firstClosest.getTargets(testGameFacade) == new int[0] : "We cannot see any ghosts yet";
		//assert secondClosest.getTargets(testGameFacade) == new int[0] : "We cannot see any ghosts yet";
		//assert thirdClosest.getTargets(testGameFacade) == new int[0] : "We cannot see any ghosts yet";
		//assert fourthClosest.getTargets(testGameFacade) == new int[0] : "We cannot see any ghosts yet";
		//assert fifthClosest.getTargets(testGameFacade) == new int[0] : "We cannot see any ghosts yet";
		
		//FOR 15 TIME STEPS
		for(int i = 0; i < 15; i++) {
			//UPDATE THE GAME, MOVE PACMAN UP
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.UP);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		//DRAW THE GAME STATE
		testExecutorFacade.forceGameView.showGame();
		
		//TELL BLINKY TO MOVE DOWN
		blinky.setMove(MOVE.DOWN);
				
		//FOR 14 TIME STEPS
		for(int i = 0; i < 14; i++) {
			//UPDATE THE GAME, MOVE PACMAN LEFT
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.LEFT);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		
		//DRAW THE GAME STATE
		testExecutorFacade.forceGameView.showGame();
		
		//tell blinky to stand still
		blinky.setMove(MOVE.LEFT);
		
		Assert.assertFalse(firstClosest.getTargets(testGameFacade).length == 0);
		
		//FOR 9 TIME STEPS
		for(int i = 0; i < 9; i++) {
			//UPDATE THE GAME, MOVE PACMAN LEFT
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.NEUTRAL);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		
		//DRAW THE GAME STATE
		testExecutorFacade.forceGameView.showGame();
		
		pinky.setMove(MOVE.DOWN);
		
		//FOR 11 TIME STEPS
		for(int i = 0; i < 11; i++) {
			//UPDATE THE GAME, MOVE PACMAN LEFT
			testExecutorFacade.forceGame(testGameFacade, testPacManControllerFacade, testGhostControllerFacade, MOVE.NEUTRAL);
			
			//GET THE MODELS OF THE GAME STATE (ghostPredictions, PillModel) from infomanager (an OldToNewPacManIntermediaryController)
			informedGameFacade = infoManager.updateModels(testGameFacade.poG, 40);
			
			//UPDATE THE MODELS IN THE GAME FACADE
			testGameFacade.pillModel = informedGameFacade.pillModel;
			testGameFacade.ghostPredictions = informedGameFacade.ghostPredictions;
			Assert.assertNotNull(testGameFacade.ghostPredictions);
			
			//DRAW THE GAME STATE
			//testExecutorFacade.forceGameView.showGame();
			//testExecutorFacade.forceGameView.closeGame();
		}
		
		Assert.assertFalse(firstClosest.getTargets(testGameFacade).length == 0);
		Assert.assertFalse(secondClosest.getTargets(testGameFacade).length == 0);
		Assert.assertFalse(thirdClosest.getTargets(testGameFacade).length == 0);
		Assert.assertTrue(fourthClosest.getTargets(testGameFacade).length == 0);
		Assert.assertTrue(fifthClosest.getTargets(testGameFacade).length == 0);
		
		//DRAW THE GAME STATE
		testExecutorFacade.forceGameView.showGame();
		
//		System.out.println("WAITING FOR MISCUTIL IN VariableDirectionSortedPossibleGhostDistanceBlockTest");
//		MiscUtil.waitForReadStringAndEnterKeyPress();
	
	
	}
	
}
