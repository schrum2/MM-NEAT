package edu.southwestern.tasks.mspacman.multitask;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.EnumMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import oldpacman.game.Game;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;

public class CompleteTaskModeSelectorTest {

	private static CompleteTaskModeSelector select;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.southwestern.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator", 
		"pacmanMultitaskScheme:edu.southwestern.tasks.mspacman.multitask.AnyGhostEdibleModeSelector"});
		select = new CompleteTaskModeSelector();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		select = null;
	}

	@Test
	public void testMode() {
		GameFacade g = new GameFacade(new Game(0));
//		GameView gv = new GameView(g.oldG).showGame();
		// Assign moves to the ghosts: NEUTRAL is default
		EnumMap<GHOST,MOVE> gm = new EnumMap<GHOST,MOVE>(GHOST.class);
		gm.put(GHOST.SUE, MOVE.NEUTRAL);
		g.oldG.advanceGame(MOVE.LEFT, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(CompleteTaskModeSelector.EAT_PILLS, select.mode());
		
		for(int i = 0; i < 350; i++) g.oldG.advanceGame(MOVE.LEFT, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(CompleteTaskModeSelector.LURE, select.mode());
		
		while(!g.anyIsEdible()) g.oldG.advanceGame(MOVE.DOWN, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(CompleteTaskModeSelector.EAT_GHOSTS, select.mode());
	}

	@Test
	public void testNumModes() {
		assertEquals(3, select.numModes());
	}

	@Test
	public void testAssociatedFitnessScores() {
		assertArrayEquals(new int[]{13,14,19}, select.associatedFitnessScores());
	}

}
