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

public class ClosestGhostModeSelectorTest {

	static private ClosestGhostModeSelector select;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.southwestern.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator", 
		"pacmanMultitaskScheme:edu.southwestern.tasks.mspacman.multitask.AnyGhostEdibleModeSelector"});
		select = new ClosestGhostModeSelector();
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
		g.oldG.advanceGame(MOVE.LEFT, gm); // Everyone moves

//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(ClosestGhostModeSelector.CLOSEST_THREAT, select.mode());

		for(int i = 0; i < 100; i++) g.oldG.advanceGame(MOVE.LEFT, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(ClosestGhostModeSelector.CLOSEST_THREAT, select.mode());
		
		while(!g.anyIsEdible()) g.oldG.advanceGame(MOVE.DOWN, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(ClosestGhostModeSelector.CLOSEST_EDIBLE, select.mode());
		
		for(int i = 0; i < 15; i++) g.oldG.advanceGame(MOVE.UP, gm);
		for(int i = 0; i < 5; i++) g.oldG.advanceGame(MOVE.RIGHT, gm);
		for(int i = 0; i < 70; i++) g.oldG.advanceGame(MOVE.UP, gm); //eat nearest ghost
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(ClosestGhostModeSelector.CLOSEST_EDIBLE, select.mode());
		
		for(int i = 0; i < 15; i++) g.oldG.advanceGame(MOVE.DOWN, gm); 
		for(int i = 0; i < 20; i++) g.oldG.advanceGame(MOVE.RIGHT, gm);
		for(int i = 0; i < 15; i++) g.oldG.advanceGame(MOVE.LEFT, gm); //waste time formerly eaten ghost becomes closest 
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(ClosestGhostModeSelector.CLOSEST_THREAT, select.mode());
	}

	@Test
	public void testNumModes() {
		assertEquals(2, select.numModes());
	}

	@Test
	public void testAssociatedFitnessScores() {
		assertArrayEquals(new int[]{1,3}, select.associatedFitnessScores());
	}

}
