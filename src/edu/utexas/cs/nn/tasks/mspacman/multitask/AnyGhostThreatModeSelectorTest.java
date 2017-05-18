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

public class AnyGhostThreatModeSelectorTest {

	static AnyGhostThreatModeSelector select;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator", 
		"pacmanMultitaskScheme:edu.utexas.cs.nn.tasks.mspacman.multitask.AnyGhostEdibleModeSelector"});

		select = new AnyGhostThreatModeSelector();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		select = null;
	}

	@Test
	public void testMode() {
		GameFacade g = new GameFacade(new Game(0));
//		GameView gv = new GameView(g.newG).showGame(); //
		EnumMap<GHOST,MOVE> gm = new EnumMap<GHOST,MOVE>(GHOST.class);
		gm.put(GHOST.BLINKY, MOVE.NEUTRAL);
		gm.put(GHOST.INKY, MOVE.NEUTRAL);
		gm.put(GHOST.PINKY, MOVE.NEUTRAL);
		gm.put(GHOST.SUE, MOVE.NEUTRAL);
		g.newG.advanceGame(MOVE.LEFT, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyGhostThreatModeSelector.ALL_GHOSTS_EDIBLE, select.mode()); //not literally edible but in the lair 
		// Loop until ghosts all leave lair
		while(g.anyActiveGhostInLair()) {
			g.newG.advanceGame(MOVE.LEFT, gm);
		}
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyGhostThreatModeSelector.SOME_GHOST_THREATENING, select.mode());
		//go down until eat the power pill
		while(!g.anyIsEdible()){
			g.newG.advanceGame(MOVE.DOWN, gm);
		}
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		assertEquals(AnyGhostThreatModeSelector.ALL_GHOSTS_EDIBLE, select.mode());
	}

	@Test
	public void testNumModes() {
		assertEquals(2, select.numModes());
	}

	@Test
	public void testAssociatedFitnessScores() {
		assertArrayEquals(new int[]{3,1}, select.associatedFitnessScores());
	}

}
