package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po;

import static org.junit.Assert.*;

import org.junit.AfterClass;
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
				"rawScorePacMan:true", "ghostPO:false", "observePacManPO:true", "usePillModel:false",
				//remove when done
				"watch:true" });
		
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
		ExecutorFacade ex = new ExecutorFacade(b.build());
		
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
		PacManControllerFacade pmcf = new PacManControllerFacade(infoManager);
		
		//CREATE a GameFacade
		GameFacade gf = new GameFacade(new Game(0));	
		// View the game to create tests, but disable afterward		
		
		//CREATE TESTING GHOSTS
		DummyBlinkyForTesting blinky = new DummyBlinkyForTesting(GHOST.BLINKY);
		DummyInkyForTesting inky = new DummyInkyForTesting(GHOST.INKY);
		DummyPinkyForTesting pinky = new DummyPinkyForTesting(GHOST.PINKY); 
		DummySueForTesting sue = new DummySueForTesting(GHOST.SUE);
		//PUT TESTING GHOSTS IN A MASController
		MASController boo = MASController.masControllerFactory(false, blinky, inky, pinky, sue);
		//PUT MASController IS A GhostControllerFacade
		GhostControllerFacade gcf = new GhostControllerFacade(boo);
		
		GameFacade igf = null;
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//TO DRAW A FRAME (AND CLOSE IT)//
		//ex.forceGameView.showGame();
		//ex.forceGameView.closeGame();		
		//////////////////////////////////
		
		//TO ADVANCE THE GAME/////////////////////
		//ex.forceGame(gf, pmcf, gcf, MOVE.LEFT);
		//THE MOVE DECIDES WHAT PACMAN DOES
		//////////////////////////////////////////
		
		blinky.setMove(MOVE.LEFT);
		pinky.setMove(MOVE.LEFT);
		inky.setMove(MOVE.LEFT);
		sue.setMove(MOVE.LEFT);
			
		for(int i = 0; i < 17; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.LEFT);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		
		assert infoManager.ghostPredictions != null : "They are set when infoManager gets a move";
		ex.forceGameView.showGame();
		
		for(int i = 0; i < 7; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.UP);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		
		
		for(int i = 0; i < 15; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.RIGHT);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		ex.forceGameView.showGame();
		
		System.out.println(firstClosest.getTargets(gf).toString());
		
		assert firstClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert secondClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert thirdClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert fourthClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert fifthClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		
		for(int i = 0; i < 15; i++) {
			ex.forceGame(gf, pmcf, gcf,MOVE.UP);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		ex.forceGameView.showGame();
		
		blinky.setMove(MOVE.DOWN);
		
		for(int i = 0; i < 14; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.LEFT);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		ex.forceGameView.showGame();
		
		blinky.setMove(MOVE.LEFT);
		
		for(int i = 0; i < 9; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.NEUTRAL);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		ex.forceGameView.showGame();
		
		pinky.setMove(MOVE.DOWN);
		
		for(int i = 0; i < 11; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.NEUTRAL);
			igf = infoManager.updateModels(gf.poG, 40);
			gf.pillModel = igf.pillModel;
			gf.ghostPredictions = igf.ghostPredictions;
			//ex.forceGameView.showGame();
			//ex.forceGameView.closeGame();
		}
		ex.forceGameView.showGame();	
		
		
		System.out.println("WAITING FOR MISCUTIL IN VariableDirectionSortedPossibleGhostDistanceBlockTest");
		MiscUtil.waitForReadStringAndEnterKeyPress();
	
	
	}
	
}
