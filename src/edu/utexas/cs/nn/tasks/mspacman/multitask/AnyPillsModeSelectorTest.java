package edu.utexas.cs.nn.tasks.mspacman.multitask;

import static org.junit.Assert.*;

import java.util.EnumMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.MiscUtil;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class AnyPillsModeSelectorTest {
	
	static AnyPillsModeSelector select;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator", 
		"pacmanMultitaskScheme:edu.utexas.cs.nn.tasks.mspacman.multitask.AnyGhostEdibleModeSelector"});
		select = new AnyPillsModeSelector();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		select = null;
	}

	@Test
	public void testMode() {
		GameFacade g = new GameFacade(new Game(0));
//		GameView gv = new GameView(g.newG).showGame();
		// Assign moves to the ghosts: NEUTRAL is default
		EnumMap<GHOST,MOVE> gm = new EnumMap<GHOST,MOVE>(GHOST.class);
		gm.put(GHOST.SUE, MOVE.NEUTRAL);
		g.newG.advanceGame(MOVE.LEFT, gm); // Everyone moves
		
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyPillsModeSelector.PILLS_PRESENT, select.mode());
		
		for(int i = 0; i < 100; i++) g.newG.advanceGame(MOVE.LEFT, gm);
		while(!g.anyIsEdible()) g.newG.advanceGame(MOVE.DOWN, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyPillsModeSelector.PILLS_PRESENT, select.mode());
		
		g.newG.playWithoutPills(); //removes normal pills, not power pills
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyPillsModeSelector.PILLS_PRESENT, select.mode()); //power pills still there
		
		g.newG.playWithoutPowerPills();
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyPillsModeSelector.NO_PILLS_PRESENT, select.mode());
	}

	@Test
	public void testNumModes() {
		assertEquals(2, select.numModes());
	}

	@Test
	public void testAssociatedFitnessScores() {
		assertArrayEquals(new int[]{2,3}, select.associatedFitnessScores());
	}

}
