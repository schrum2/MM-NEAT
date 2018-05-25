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

public class AnyGhostThreatModeSelectorTest {

	static AnyGhostThreatModeSelector select;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.southwestern.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator", 
		"pacmanMultitaskScheme:edu.southwestern.tasks.mspacman.multitask.AnyGhostEdibleModeSelector"});

		select = new AnyGhostThreatModeSelector();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		select = null;
	}

	@Test
	public void testMode() {
		GameFacade g = new GameFacade(new Game(0));
//		GameView gv = new GameView(g.oldG).showGame(); //
		EnumMap<GHOST,MOVE> gm = new EnumMap<GHOST,MOVE>(GHOST.class);
		gm.put(GHOST.BLINKY, MOVE.NEUTRAL);
		gm.put(GHOST.INKY, MOVE.NEUTRAL);
		gm.put(GHOST.PINKY, MOVE.NEUTRAL);
		gm.put(GHOST.SUE, MOVE.NEUTRAL);
		g.oldG.advanceGame(MOVE.LEFT, gm);
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyGhostThreatModeSelector.ALL_GHOSTS_EDIBLE, select.mode()); //not literally edible but in the lair 
		// Loop until ghosts all leave lair
		while(g.anyActiveGhostInLair()) {
			g.oldG.advanceGame(MOVE.LEFT, gm);
		}
//		gv.repaint();
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		select.giveGame(g);
		assertEquals(AnyGhostThreatModeSelector.SOME_GHOST_THREATENING, select.mode());
		//go down until eat the power pill
		while(!g.anyIsEdible()){
			g.oldG.advanceGame(MOVE.DOWN, gm);
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
