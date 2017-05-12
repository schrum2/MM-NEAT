package edu.utexas.cs.nn.tasks.mspacman.multitask;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.EnumMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.MiscUtil;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class AnyGhostEdibleModeSelectorTest {

	static AnyGhostEdibleModeSelector select;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask", "multitaskModes:2", 
				"pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator", 
				"pacmanMultitaskScheme:edu.utexas.cs.nn.tasks.mspacman.multitask.AnyGhostEdibleModeSelector"});
		//MMNEAT.loadClasses(); // Don't actually need any of the classes
		
		select = new AnyGhostEdibleModeSelector();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		select = null;
	}

	@Test
	public void testMode() {
		// Create a Ms. Pac-Man game
		GameFacade g = new GameFacade(new Game(0));
		// View the game to create tests, but disable afterward		
//		GameView gv = new GameView(g.newG).showGame();

		// Assign moves to the ghosts: NEUTRAL is default
		EnumMap<GHOST,MOVE> gm = new EnumMap<GHOST,MOVE>(GHOST.class);
		gm.put(GHOST.BLINKY, MOVE.NEUTRAL);
		gm.put(GHOST.INKY, MOVE.NEUTRAL);
		gm.put(GHOST.PINKY, MOVE.NEUTRAL);
		gm.put(GHOST.SUE, MOVE.NEUTRAL);
		g.newG.advanceGame(MOVE.LEFT, gm); // Everyone moves
		
		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();
				
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Ghosts still inside lair
		assertEquals(AnyGhostEdibleModeSelector.NO_EDIBLE_GHOSTS, select.mode());
		
		// Loop until ghosts all leave lair
		while(g.anyActiveGhostInLair()) {
			g.newG.advanceGame(MOVE.LEFT, gm);
		}

		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();
				
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Ghosts outside lair, but none are edible
		assertEquals(AnyGhostEdibleModeSelector.NO_EDIBLE_GHOSTS, select.mode());

		// Move down enough to get power pill
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		
		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Power pill just eaten, so all are edible
		assertEquals(AnyGhostEdibleModeSelector.SOME_EDIBLE_GHOST, select.mode());

		
		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();

		// Chase the edible ghosts
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		
	
		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();
	
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// One ghost eaten, but rest still edible
		assertEquals(AnyGhostEdibleModeSelector.SOME_EDIBLE_GHOST, select.mode());

		// Move toward other ghost
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.DOWN, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	

		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Second ghost eaten, but rest still edible
		assertEquals(AnyGhostEdibleModeSelector.SOME_EDIBLE_GHOST, select.mode());

		// Chase another ghost
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Third ghost eaten, one edible, but one threat has exited lair
		assertEquals(AnyGhostEdibleModeSelector.SOME_EDIBLE_GHOST, select.mode());

		// Flee threat
		g.newG.advanceGame(MOVE.LEFT, gm); 	
		g.newG.advanceGame(MOVE.LEFT, gm); 	
		g.newG.advanceGame(MOVE.LEFT, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);   
		
		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Edible time wore off: all threats now
		assertEquals(AnyGhostEdibleModeSelector.NO_EDIBLE_GHOSTS, select.mode());

		// Get another power pill
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm); 	
		g.newG.advanceGame(MOVE.RIGHT, gm);  	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 		
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		g.newG.advanceGame(MOVE.UP, gm); 	
		
		// The game has to be passed to the mode selector before each decision
		select.giveGame(g);
		// Ghosts all edible again
		assertEquals(AnyGhostEdibleModeSelector.SOME_EDIBLE_GHOST, select.mode());
		
		// Update view
//		gv.repaint();
		// Pause for user to press enter
//		MiscUtil.waitForReadStringAndEnterKeyPress();

	}

	@Test
	public void testNumModes() {
		assertEquals(2, select.numModes());
	}

	@Test
	public void testAssociatedFitnessScores() {
		Parameters.parameters.setBoolean("awardProperPowerPillEating",	 true);
		Parameters.parameters.setBoolean("punishImproperPowerPillEating",true);
		Parameters.parameters.setBoolean("levelObjective",				 true);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{12,13}); //TTT
		
		Parameters.parameters.setBoolean("levelObjective",				 false);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{12 , 13}); //TTF
		
		Parameters.parameters.setBoolean("punishImproperPowerPillEating",false);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{12 , 3}); //TFF
		
		Parameters.parameters.setBoolean("levelObjective",				 true);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{12 , 21}); //TFT
		
		Parameters.parameters.setBoolean("awardProperPowerPillEating",	 false);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{1 , 21}); //FFT
		
		Parameters.parameters.setBoolean("levelObjective",				 false);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{1 , 3}); //FFF
		
		Parameters.parameters.setBoolean("punishImproperPowerPillEating",true);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{1 , 13}); //FTF
		
		Parameters.parameters.setBoolean("levelObjective",				 true);
		select = new AnyGhostEdibleModeSelector();
		assertArrayEquals(select.associatedFitnessScores(), new int[]{1 , 13}); //FTT
	}

}
