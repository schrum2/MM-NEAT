package edu.southwestern.tasks.rlglue.tetris;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rlcommunity.environments.tetris.TetrisState;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.parameters.Parameters;

public class TetrisAfterStateGeneratorTests {

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"task:edu.southwestern.tasks.rlglue.tetris.TetrisTask", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.BertsekasTsitsiklisTetrisExtractor" });
		TetrisState.forceResetBlocks();
		MMNEAT.loadClasses();
	}
	
	/**
	 * Tests that all afterstates are present in the returned hash and This part tests 
	 * that the actions of the action list do give you the given state
	 */
	@Test
	public void test_afterstate_occurance() { 
		TetrisState testState = new TetrisState(); 
		// simple example used
		testState.worldState[190] = 1;
		testState.worldState[191] = 1;
		testState.worldState[196] = 1;
		testState.worldState[197] = 1;
		testState.worldState[198] = 1;
		testState.worldState[199] = 1;
		testState.currentX = 3;
		testState.currentY = -2;
		testState.currentRotation = 0;
		HashSet<TetrisStateActionPair> holder = TetrisAfterStateGenerator.generateAfterStates(testState);

		// WOO BOY GET READY FOR A LOT OF TESTING CODE NONSENSE -Gab
		TetrisState resultV1 = new TetrisState(testState); // Vertical 1
		resultV1.worldState[150] = 1;
		resultV1.worldState[160] = 1;
		resultV1.worldState[170] = 1;
		resultV1.worldState[180] = 1;
		resultV1.currentX -= 6;
		resultV1.currentY += 14;
		resultV1.currentRotation += 1;

		TetrisState resultV2 = new TetrisState(testState); // Vertical 2
		resultV2.worldState[151] = 1;
		resultV2.worldState[161] = 1;
		resultV2.worldState[171] = 1;
		resultV2.worldState[181] = 1;
		resultV2.currentX -= 5;
		resultV2.currentY += 14;
		resultV2.currentRotation += 1;

		TetrisState resultV3 = new TetrisState(testState); // Vertical 3
		resultV3.worldState[162] = 1;
		resultV3.worldState[172] = 1;
		resultV3.worldState[182] = 1;
		resultV3.worldState[192] = 1;
		resultV3.currentX -= 4;
		resultV3.currentY += 15;
		resultV3.currentRotation += 1;

		TetrisState resultV4 = new TetrisState(testState); // Vertical 4
		resultV4.worldState[163] = 1;
		resultV4.worldState[173] = 1;
		resultV4.worldState[183] = 1;
		resultV4.worldState[193] = 1;
		resultV4.currentX -= 3;
		resultV4.currentY += 15;
		resultV4.currentRotation += 1;

		TetrisState resultV5 = new TetrisState(testState); // Vertical 5
		resultV5.worldState[164] = 1;
		resultV5.worldState[174] = 1;
		resultV5.worldState[184] = 1;
		resultV5.worldState[194] = 1;
		resultV5.currentX -= 2;
		resultV5.currentY += 15;
		resultV5.currentRotation += 1;

		TetrisState resultV6 = new TetrisState(testState); // Vertical 6
		resultV6.worldState[165] = 1;
		resultV6.worldState[175] = 1;
		resultV6.worldState[185] = 1;
		resultV6.worldState[195] = 1;
		resultV6.currentX -= 1;
		resultV6.currentY += 15;
		resultV6.currentRotation += 1;

		TetrisState resultV7 = new TetrisState(testState); // Vertical 7
		resultV7.worldState[156] = 1;
		resultV7.worldState[166] = 1;
		resultV7.worldState[176] = 1;
		resultV7.worldState[186] = 1;
		resultV7.currentX -= 0;
		resultV7.currentY += 14;
		resultV7.currentRotation += 1;

		TetrisState resultV8 = new TetrisState(testState); // Vertical 8
		resultV8.worldState[157] = 1;
		resultV8.worldState[167] = 1;
		resultV8.worldState[177] = 1;
		resultV8.worldState[187] = 1;
		resultV8.currentX += 1;
		resultV8.currentY += 14;
		resultV8.currentRotation += 1;

		TetrisState resultV9 = new TetrisState(testState); // Vertical 9
		resultV9.worldState[158] = 1;
		resultV9.worldState[168] = 1;
		resultV9.worldState[178] = 1;
		resultV9.worldState[188] = 1;
		resultV9.currentX += 2;
		resultV9.currentY += 14;
		resultV9.currentRotation += 1;

		TetrisState resultV10 = new TetrisState(testState); // Vertical 10
		resultV10.worldState[159] = 1;
		resultV10.worldState[169] = 1;
		resultV10.worldState[179] = 1;
		resultV10.worldState[189] = 1;
		resultV10.currentX += 3;
		resultV10.currentY += 14;
		resultV10.currentRotation += 1;

		TetrisState resultH1 = new TetrisState(testState); // Horizontal 1
		resultH1.worldState[180] = 1;
		resultH1.worldState[181] = 1;
		resultH1.worldState[182] = 1;
		resultH1.worldState[183] = 1;
		resultH1.currentX -= 4;
		resultH1.currentY += 16;

		TetrisState resultH2 = new TetrisState(testState); // Horizontal 2
		resultH2.worldState[181] = 1;
		resultH2.worldState[182] = 1;
		resultH2.worldState[183] = 1;
		resultH2.worldState[184] = 1;
		resultH2.currentX -= 3;
		resultH2.currentY += 16;

		TetrisState resultH3 = new TetrisState(); // Horizontal 3
		resultH3.currentX -= 2;
		resultH3.currentY += 17;

		TetrisState resultH4 = new TetrisState(testState); // Horizontal 4
		resultH4.worldState[183] = 1;
		resultH4.worldState[184] = 1;
		resultH4.worldState[185] = 1;
		resultH4.worldState[186] = 1;
		resultH4.currentX -= 1;
		resultH4.currentY += 16;

		TetrisState resultH5 = new TetrisState(testState); // Horizontal 5
		resultH5.worldState[184] = 1;
		resultH5.worldState[185] = 1;
		resultH5.worldState[186] = 1;
		resultH5.worldState[187] = 1;
		resultH5.currentX -= 0;
		resultH5.currentY += 16;

		TetrisState resultH6 = new TetrisState(testState); // Horizontal 6
		resultH6.worldState[185] = 1;
		resultH6.worldState[186] = 1;
		resultH6.worldState[187] = 1;
		resultH6.worldState[188] = 1;
		resultH6.currentX += 1;
		resultH6.currentY += 16;

		TetrisState resultH7 = new TetrisState(testState); // Horizontal 7
		resultH7.worldState[186] = 1;
		resultH7.worldState[187] = 1;
		resultH7.worldState[188] = 1;
		resultH7.worldState[189] = 1;
		resultH7.currentX += 2;
		resultH7.currentY += 16;

		assertTrue(holder.contains(new TetrisStateActionPair(resultV1, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV2, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV3, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV4, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV5, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV6, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV7, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV8, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV9, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultV10, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH1, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH2, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH3, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH4, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH5, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH6, null)));
		assertTrue(holder.contains(new TetrisStateActionPair(resultH7, null)));

		// This part tests that the actions of the action list do give you the
		// given state
		for (TetrisStateActionPair j : holder) {
			TetrisState copyState = new TetrisState(testState);
			List<Integer> actionList = j.t2;
			// actions
			for (int p = 0; p < actionList.size(); p++) {
				copyState.take_action(actionList.get(p)); // move left
				copyState.update(); // update
			}
			assertTrue(j.t1.equals(copyState));
		}

	}

	/**
	 * Not a formal test. It is still useful to determine block information, 
	 * so we will leave this commented here for now -Gab
	 */
	@Test
	public void different_starting_blocks_positions() { 
		// TetrisViewer testView = new TetrisViewer(); 
		// TetrisState testState = new TetrisState(); 

		// these are testing each shape in the testing environment
		/*
		 * testView.update(testState); //System.out.println("For Piece " +
		 * testState.currentBlockId + ", x = " + testState.currentX + ", y = " +
		 * testState.currentY + ", and the rotation is " +
		 * testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentBlockId = 1; testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentBlockId = 2; testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentBlockId = 3; testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentBlockId = 4; testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentBlockId = 5; testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentBlockId = 6; testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 */

		// These are for random spawns
		/*
		 * testState.spawn_block(); testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.spawn_block(); testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.spawn_block(); testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.spawn_block(); testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.spawn_block(); testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.spawn_block(); testView.update(testState);
		 * //System.out.println("For Piece " + testState.currentBlockId +
		 * ", x = " + testState.currentX + ", y = " + testState.currentY +
		 * ", and the rotation is " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 */

		// this was to test the orientations
		/*
		 * testState.currentBlockId = 6; testView.update(testState);
		 * //System.out.println("x = " + testState.currentX + ", y = " +
		 * testState.currentY + ", r = " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentRotation++; testView.update(testState);
		 * //System.out.println("x = " + testState.currentX + ", y = " +
		 * testState.currentY + ", r = " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentRotation++; testView.update(testState);
		 * //System.out.println("x = " + testState.currentX + ", y = " +
		 * testState.currentY + ", r = " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 * testState.currentRotation++; testView.update(testState);
		 * //System.out.println("x = " + testState.currentX + ", y = " +
		 * testState.currentY + ", r = " + testState.currentRotation);
		 * MiscUtil.waitForReadStringAndEnterKeyPress();
		 */

	}

	/**
	 * Tests that we get all the available afterstates given the mobile block is at spawn
	 */
	@Test
	public void afterstates_at_spawn() {
		TetrisState testState = new TetrisState(); 
		testState.currentBlockId = 4; 
		testState.currentX = 3;
		testState.currentY = -1;
		HashSet<TetrisStateActionPair> holder = TetrisAfterStateGenerator.generateAfterStates(testState);
		HashSet<TetrisState> justStates = new HashSet<TetrisState>();
		assertTrue(holder.size() >= 17 && holder.size() < 34);
		for (TetrisStateActionPair i : holder) {
			justStates.add(i.t1);
		}
		assertTrue(justStates.size() == 17);
	}

	/**
	 * Tests that we get all the available afterstates given the mobile block has previously taken actions
	 */
	@Test
	public void afterstates_after_action() {
		TetrisState testState = new TetrisState();
		testState.currentBlockId = 4; // simulating piece 4 at spawn point
		testState.currentX = 3 + 2; // right by 2
		testState.currentY = -1 + 5; // down by 5
		testState.currentRotation = 3; // third rotation
		HashSet<TetrisStateActionPair> holder = TetrisAfterStateGenerator.generateAfterStates(testState);
		HashSet<TetrisState> justStates = new HashSet<TetrisState>();

		for (TetrisStateActionPair i : holder) { 
			justStates.add(i.t1);
		}
		assertTrue(justStates.size() == 17);

		testState.currentBlockId = 4; // simulating piece 4 at spawn point
		testState.currentX = 3 + 4; // right by 2
		testState.currentY = -1 + 8; // down by 5
		testState.currentRotation = 3; // third rotation
		HashSet<TetrisStateActionPair> holder2 = TetrisAfterStateGenerator.generateAfterStates(testState);
		HashSet<TetrisState> justStates2 = new HashSet<TetrisState>();
		assertTrue(holder.size() >= 17 && holder2.size() < 34);
		for (TetrisStateActionPair i : holder2) { 
			justStates2.add(i.t1);
		}
		assertTrue(justStates2.size() == 17);

	}

	/**
	 * Tests that we get all the available afterstates given a column is blocked off
	 */
	@Test
	public void afterstates_with_blocked_wall() { 
		TetrisState testState = new TetrisState(); 
		testState.currentBlockId = 4; // simulating piece 4 at spawn point
		testState.currentX = 3;
		testState.currentY = -1;
		testState.worldState[20] = 1;
		testState.worldState[30] = 1;
		testState.worldState[40] = 1;
		testState.worldState[50] = 1;
		testState.worldState[60] = 1;
		testState.worldState[70] = 1;
		testState.worldState[80] = 1;
		testState.worldState[90] = 1;
		testState.worldState[100] = 1;
		testState.worldState[110] = 1;
		testState.worldState[120] = 1;
		testState.worldState[130] = 1;
		testState.worldState[140] = 1;
		testState.worldState[150] = 1;
		testState.worldState[160] = 1;
		testState.worldState[170] = 1;
		testState.worldState[180] = 1;
		testState.worldState[190] = 1;

		HashSet<TetrisStateActionPair> holder = TetrisAfterStateGenerator.generateAfterStates(testState);
		HashSet<TetrisState> justStates = new HashSet<TetrisState>();
		for (TetrisStateActionPair i : holder) { 
			justStates.add(i.t1);
		}
		assertTrue(justStates.size() == 15);
	}

	/**
	 * Tests that a mobile block will still try to fit under another block if that is deems the best afterstate
	 */
	@Test
	public void fits_under_block() { 
		TetrisState testState = new TetrisState(); 
		testState.currentBlockId = 5; // simulating piece 5 at spawn point
		testState.currentX = 3;
		testState.currentY = -2;

		testState.worldState[160] = 1;
		testState.worldState[166] = 1;
		testState.worldState[170] = 1;
		testState.worldState[171] = 1;
		testState.worldState[176] = 1;
		testState.worldState[180] = 1;
		testState.worldState[181] = 1;
		testState.worldState[186] = 1;
		testState.worldState[187] = 1;
		testState.worldState[189] = 1;
		testState.worldState[190] = 1;
		testState.worldState[194] = 1;
		testState.worldState[196] = 1;
		testState.worldState[197] = 1;
		testState.worldState[198] = 1;
		testState.worldState[199] = 1;

		TetrisState temp = new TetrisState(testState); 
		temp.worldState[172] = 6;
		temp.worldState[182] = 6;
		temp.worldState[191] = 6;
		temp.worldState[192] = 6;
		temp.currentX -= 3;
		temp.currentY += 18;
		temp.currentRotation = 3;

		HashSet<TetrisStateActionPair> holder = TetrisAfterStateGenerator.generateAfterStates(testState);
		assertTrue(holder.contains(new TetrisStateActionPair(temp, null)));
	}

	/**
	 * Tests that the fall action will be used in situations that benefits the agent
	 */
	@Test
	public void fall_in_action_list() { 
		TetrisState testState = new TetrisState();
		testState.currentBlockId = 1; // simulating piece 5 at spawn point
		testState.currentX = 3;
		testState.currentY = -2;

		testState.worldState[180] = 1;
		testState.worldState[181] = 1;
		testState.worldState[182] = 1;
		testState.worldState[185] = 1;
		testState.worldState[186] = 1;
		testState.worldState[187] = 1;
		testState.worldState[188] = 1;
		testState.worldState[189] = 1;
		testState.worldState[190] = 1;
		testState.worldState[191] = 1;
		testState.worldState[192] = 1;
		testState.worldState[195] = 1;
		testState.worldState[196] = 1;
		testState.worldState[197] = 1;
		testState.worldState[198] = 1;
		testState.worldState[199] = 1;

		HashSet<TetrisStateActionPair> holder = TetrisAfterStateGenerator.generateAfterStates(testState);

		TetrisState pos1 = new TetrisState(testState);
		pos1.worldState[160] = 2;
		pos1.worldState[161] = 2;
		pos1.worldState[170] = 2;
		pos1.worldState[171] = 2;
		pos1.currentX -= 4;
		pos1.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos1, null)));

		TetrisState pos2 = new TetrisState(testState);
		pos2.worldState[162] = 2;
		pos2.worldState[161] = 2;
		pos2.worldState[172] = 2;
		pos2.worldState[171] = 2;
		pos2.currentX -= 3;
		pos2.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos2, null)));

		TetrisState pos3 = new TetrisState(testState);
		pos3.worldState[162] = 2;
		pos3.worldState[163] = 2;
		pos3.worldState[172] = 2;
		pos3.worldState[173] = 2;
		pos3.currentX -= 2;
		pos3.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos3, null)));

		TetrisState pos4 = new TetrisState();
		pos4.currentX -= 1;
		pos4.currentY += 18;
		assertTrue(holder.contains(new TetrisStateActionPair(pos4, null)));

		TetrisState pos5 = new TetrisState(testState);
		pos5.worldState[164] = 2;
		pos5.worldState[165] = 2;
		pos5.worldState[174] = 2;
		pos5.worldState[175] = 2;
		pos5.currentX += 0;
		pos5.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos5, null)));

		TetrisState pos6 = new TetrisState(testState);
		pos6.worldState[166] = 2;
		pos6.worldState[165] = 2;
		pos6.worldState[176] = 2;
		pos6.worldState[175] = 2;
		pos6.currentX += 1;
		pos6.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos6, null)));

		TetrisState pos7 = new TetrisState(testState);
		pos7.worldState[166] = 2;
		pos7.worldState[167] = 2;
		pos7.worldState[176] = 2;
		pos7.worldState[177] = 2;
		pos7.currentX += 2;
		pos7.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos7, null)));

		TetrisState pos8 = new TetrisState(testState);
		pos8.worldState[168] = 2;
		pos8.worldState[167] = 2;
		pos8.worldState[178] = 2;
		pos8.worldState[177] = 2;
		pos8.currentX += 3;
		pos8.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos8, null)));

		TetrisState pos9 = new TetrisState(testState);
		pos9.worldState[168] = 2;
		pos9.worldState[169] = 2;
		pos9.worldState[178] = 2;
		pos9.worldState[179] = 2;
		pos9.currentX += 4;
		pos9.currentY += 16;
		assertTrue(holder.contains(new TetrisStateActionPair(pos9, null)));

		// test the sizes and contents of action lists
		for (TetrisStateActionPair i : holder) {
			if (i.equals(new TetrisStateActionPair(pos1, null))) {
				assertEquals(i.t2.size(), 5);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos2, null))) {
				assertEquals(i.t2.size(), 4);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos3, null))) {
				assertEquals(i.t2.size(), 3);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(0, 0, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos4, null))) {
				assertEquals(i.t2.size(), 2);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(0, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos5, null))) {
				assertEquals(i.t2.size(), 1);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos6, null))) {
				assertEquals(i.t2.size(), 2);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(1, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos7, null))) {
				assertEquals(i.t2.size(), 3);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(1, 1, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos8, null))) {
				assertEquals(i.t2.size(), 4);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 5));
				assertTrue(i.t2.equals(temp));
			} else if (i.equals(new TetrisStateActionPair(pos9, null))) {
				assertEquals(i.t2.size(), 5);
				List<Integer> temp = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 5));
				assertTrue(i.t2.equals(temp));
			}
		}
	}
}