package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po;

import static org.junit.Assert.*;

//import java.util.Arrays;
import java.util.EnumMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.ExecutorFacade;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.facades.GhostControllerFacade;
import edu.southwestern.tasks.mspacman.facades.PacManControllerFacade;
import edu.southwestern.tasks.popacman.controllers.OldToNewPacManIntermediaryController;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;
import oldpacman.controllers.NewPacManController;
import pacman.game.Game;
import pacman.game.GameView;
import popacman.CustomExecutor;
import popacman.CustomExecutor.Builder;
import popacman.DummyBlinkyForTesting;
import popacman.DummyInkyForTesting;
import popacman.DummyPacMan;
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
				"rawScorePacMan:true", "ghostPO:false", "observePacManPO:false", 
				//remove when done
				"watch:true" });
		
		firstClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(0);
		secondClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(1);
		thirdClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(2);
		fourthClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(3);
		fifthClosest = new VariableDirectionSortedPossibleGhostDistanceBlock(4);
		infoManager = new OldToNewPacManIntermediaryController(new DummyPacMan());
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
		NewPacManController controller = null;
		try {
			controller = (NewPacManController) ClassCreation.createObject("staticPacMan");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PacManControllerFacade pmcf = new PacManControllerFacade(new OldToNewPacManIntermediaryController(controller));
		
		//CREATE a GameFacade
		GameFacade gf = new GameFacade(new Game(0));	
		// View the game to create tests, but disable afterward		
		GameView gv = new GameView(gf.poG).showGame();
		
		//CREATE TESTING GHOSTS
		DummyBlinkyForTesting blinky = new DummyBlinkyForTesting(GHOST.BLINKY);
		DummyInkyForTesting inky = new DummyInkyForTesting(GHOST.INKY);
		DummyPinkyForTesting pinky = new DummyPinkyForTesting(GHOST.PINKY); 
		DummySueForTesting sue = new DummySueForTesting(GHOST.SUE);
		//PUT TESTING GHOSTS IN A MASController
		MASController boo = MASController.masControllerFactory(false, blinky, inky, pinky, sue);
		//PUT MASController IS A GhostControllerFacade
		GhostControllerFacade gcf = new GhostControllerFacade(boo);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		

		
		
		for(int i = 0; i < 17; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.LEFT);
			//gf.poG.advanceGame(MOVE.LEFT, gm);
		}
		gv.showGame();
		
		for(int i = 0; i < 7; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.UP);
			//gf.poG.advanceGame(MOVE.UP, gm);
		}
		gv.showGame();
		
		for(int i = 0; i < 15; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.RIGHT);
			//gf.poG.advanceGame(MOVE.RIGHT, gm);
		}
		gv.showGame();
		
		assert firstClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert secondClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert thirdClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert fourthClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		assert fifthClosest.getTargets(gf) == new int[0] : "We cannot see any ghosts yet";
		
		for(int i = 0; i < 15; i++) {
			ex.forceGame(gf, pmcf, gcf,MOVE.UP);
		}
		gv.showGame();
		
		for(int i = 0; i < 14; i++) {
			ex.forceGame(gf, pmcf, gcf, MOVE.LEFT);
		}
		gv.showGame();
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
	
	
	}
	
}
