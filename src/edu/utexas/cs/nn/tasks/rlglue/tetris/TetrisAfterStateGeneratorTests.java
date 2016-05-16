package edu.utexas.cs.nn.tasks.rlglue.tetris;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;
import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class TetrisAfterStateGeneratorTests {
	/*
	 * We need the following tests:
	 * 1. simple test that a row is deleted in the first given afterstate
	 * 2. simple test that checks the first two afterstates, and gets no row deleted then row deleted
	 * 3. test for one of the earlier examples to check the actions from the given actionlist (method not yest created)
	 */
	
	@Test
	public void test_afterstate_occurance() { // This checks if all afterstates are present in the returned hash and This part tests that the actions of the action list do give you the given state
		TetrisState testState = new TetrisState(); // makes a Tetris state to test with
		//simple example used
		testState.worldState[190] = 1;
		testState.worldState[191] = 1;
		testState.worldState[196] = 1;
		testState.worldState[197] = 1;
		testState.worldState[198] = 1;
		testState.worldState[199] = 1;
		HashSet<Pair<TetrisState, ArrayList<Integer>>> holder = TertisAfterStateGenerator.evaluateAfterStates(testState);
		HashSet<TetrisState> justStates = new HashSet<TetrisState>();
		
		//WOO BOY GET READY FOR A LOT OF TESTING CODE NONSENSE -Gab
		TetrisState resultV1 = new TetrisState(); //Vertical 1
		resultV1.worldState[190] = 1;
		resultV1.worldState[191] = 1;
		resultV1.worldState[196] = 1;
		resultV1.worldState[197] = 1;
		resultV1.worldState[198] = 1;
		resultV1.worldState[199] = 1;
		//mobile block portion
		resultV1.worldState[150] = 1;
		resultV1.worldState[160] = 1;
		resultV1.worldState[170] = 1;
		resultV1.worldState[180] = 1;

		TetrisState resultV2 = new TetrisState(); //Vertical 2
		resultV2.worldState[190] = 1;
		resultV2.worldState[191] = 1;
		resultV2.worldState[196] = 1;
		resultV2.worldState[197] = 1;
		resultV2.worldState[198] = 1;
		resultV2.worldState[199] = 1;
		//mobile block portion
		resultV2.worldState[151] = 1;
		resultV2.worldState[161] = 1;
		resultV2.worldState[171] = 1;
		resultV2.worldState[181] = 1;

		TetrisState resultV3 = new TetrisState(); //Vertical 3
		resultV3.worldState[190] = 1;
		resultV3.worldState[191] = 1;
		resultV3.worldState[196] = 1;
		resultV3.worldState[197] = 1;
		resultV3.worldState[198] = 1;
		resultV3.worldState[199] = 1;
		//mobile block portion
		resultV3.worldState[162] = 1;
		resultV3.worldState[172] = 1;
		resultV3.worldState[182] = 1;
		resultV3.worldState[192] = 1;

		TetrisState resultV4 = new TetrisState(); //Vertical 4
		resultV4.worldState[190] = 1;
		resultV4.worldState[191] = 1;
		resultV4.worldState[196] = 1;
		resultV4.worldState[197] = 1;
		resultV4.worldState[198] = 1;
		resultV4.worldState[199] = 1;
		//mobile block portion
		resultV4.worldState[163] = 1;
		resultV4.worldState[173] = 1;
		resultV4.worldState[183] = 1;
		resultV4.worldState[193] = 1;

		TetrisState resultV5 = new TetrisState(); //Vertical 5
		resultV5.worldState[190] = 1;
		resultV5.worldState[191] = 1;
		resultV5.worldState[196] = 1;
		resultV5.worldState[197] = 1;
		resultV5.worldState[198] = 1;
		resultV5.worldState[199] = 1;
		//mobile block portion
		resultV5.worldState[164] = 1;
		resultV5.worldState[174] = 1;
		resultV5.worldState[184] = 1;
		resultV5.worldState[194] = 1;

		TetrisState resultV6 = new TetrisState(); //Vertical 6
		resultV6.worldState[190] = 1;
		resultV6.worldState[191] = 1;
		resultV6.worldState[196] = 1;
		resultV6.worldState[197] = 1;
		resultV6.worldState[198] = 1;
		resultV6.worldState[199] = 1;
		//mobile block portion
		resultV6.worldState[165] = 1;
		resultV6.worldState[175] = 1;
		resultV6.worldState[185] = 1;
		resultV6.worldState[195] = 1;

		TetrisState resultV7 = new TetrisState(); //Vertical 7
		resultV7.worldState[190] = 1;
		resultV7.worldState[191] = 1;
		resultV7.worldState[196] = 1;
		resultV7.worldState[197] = 1;
		resultV7.worldState[198] = 1;
		resultV7.worldState[199] = 1;
		//mobile block portion
		resultV7.worldState[156] = 1;
		resultV7.worldState[166] = 1;
		resultV7.worldState[176] = 1;
		resultV7.worldState[186] = 1;
		
		TetrisState resultV8 = new TetrisState(); //Vertical 8
		resultV8.worldState[190] = 1;
		resultV8.worldState[191] = 1;
		resultV8.worldState[196] = 1;
		resultV8.worldState[197] = 1;
		resultV8.worldState[198] = 1;
		resultV8.worldState[199] = 1;
		//mobile block portion
		resultV8.worldState[157] = 1;
		resultV8.worldState[167] = 1;
		resultV8.worldState[177] = 1;
		resultV8.worldState[187] = 1;
		
		TetrisState resultV9 = new TetrisState(); //Vertical 9
		resultV9.worldState[190] = 1;
		resultV9.worldState[191] = 1;
		resultV9.worldState[196] = 1;
		resultV9.worldState[197] = 1;
		resultV9.worldState[198] = 1;
		resultV9.worldState[199] = 1;
		//mobile block portion
		resultV9.worldState[158] = 1;
		resultV9.worldState[168] = 1;
		resultV9.worldState[178] = 1;
		resultV9.worldState[188] = 1;

		TetrisState resultV10 = new TetrisState(); //Vertical 10
		resultV10.worldState[190] = 1;
		resultV10.worldState[191] = 1;
		resultV10.worldState[196] = 1;
		resultV10.worldState[197] = 1;
		resultV10.worldState[198] = 1;
		resultV10.worldState[199] = 1;
		//mobile block portion
		resultV10.worldState[159] = 1;
		resultV10.worldState[169] = 1;
		resultV10.worldState[179] = 1;
		resultV10.worldState[189] = 1;

		TetrisState resultH1 = new TetrisState(); //Horizontal 1
		resultH1.worldState[190] = 1;
		resultH1.worldState[191] = 1;
		resultH1.worldState[196] = 1;
		resultH1.worldState[197] = 1;
		resultH1.worldState[198] = 1;
		resultH1.worldState[199] = 1;
		//mobile block portion
		resultH1.worldState[180] = 1;
		resultH1.worldState[181] = 1;
		resultH1.worldState[182] = 1;
		resultH1.worldState[183] = 1;

		TetrisState resultH2 = new TetrisState(); //Horizontal 2
		resultH2.worldState[190] = 1;
		resultH2.worldState[191] = 1;
		resultH2.worldState[196] = 1;
		resultH2.worldState[197] = 1;
		resultH2.worldState[198] = 1;
		resultH2.worldState[199] = 1;
		//mobile block portion
		resultH2.worldState[181] = 1;
		resultH2.worldState[182] = 1;
		resultH2.worldState[183] = 1;
		resultH2.worldState[184] = 1;

		TetrisState resultH3 = new TetrisState(); //Horizontal 3
		//everything is 0'd

		TetrisState resultH4 = new TetrisState(); //Horizontal 4
		resultH4.worldState[190] = 1;
		resultH4.worldState[191] = 1;
		resultH4.worldState[196] = 1;
		resultH4.worldState[197] = 1;
		resultH4.worldState[198] = 1;
		resultH4.worldState[199] = 1;
		//mobile block portion
		resultH4.worldState[183] = 1;
		resultH4.worldState[184] = 1;
		resultH4.worldState[185] = 1;
		resultH4.worldState[186] = 1;

		TetrisState resultH5 = new TetrisState(); //Horizontal 5
		resultH5.worldState[190] = 1;
		resultH5.worldState[191] = 1;
		resultH5.worldState[196] = 1;
		resultH5.worldState[197] = 1;
		resultH5.worldState[198] = 1;
		resultH5.worldState[199] = 1;
		//mobile block portion
		resultH5.worldState[184] = 1;
		resultH5.worldState[185] = 1;
		resultH5.worldState[186] = 1;
		resultH5.worldState[187] = 1;

		TetrisState resultH6 = new TetrisState(); //Horizontal 6
		resultH6.worldState[190] = 1;
		resultH6.worldState[191] = 1;
		resultH6.worldState[196] = 1;
		resultH6.worldState[197] = 1;
		resultH6.worldState[198] = 1;
		resultH6.worldState[199] = 1;
		//mobile block portion
		resultH6.worldState[185] = 1;
		resultH6.worldState[186] = 1;
		resultH6.worldState[187] = 1;
		resultH6.worldState[188] = 1;
		
		TetrisState resultH7 = new TetrisState(); //Horizontal 7
		resultH7.worldState[190] = 1;
		resultH7.worldState[191] = 1;
		resultH7.worldState[196] = 1;
		resultH7.worldState[197] = 1;
		resultH7.worldState[198] = 1;
		resultH7.worldState[199] = 1;
		//mobile block portion
		resultH7.worldState[186] = 1;
		resultH7.worldState[187] = 1;
		resultH7.worldState[188] = 1;
		resultH7.worldState[189] = 1;
				
		for(Pair<TetrisState, ArrayList<Integer>> i : holder){ // transfers only the tetris states to the hash set
			justStates.add(i.t1);
		}
		
		assertTrue(justStates.contains(resultV1));
		assertTrue(justStates.contains(resultV2));
		assertTrue(justStates.contains(resultV3));
		assertTrue(justStates.contains(resultV4));
		assertTrue(justStates.contains(resultV5));
		assertTrue(justStates.contains(resultV6));
		assertTrue(justStates.contains(resultV7));
		assertTrue(justStates.contains(resultV8));
		assertTrue(justStates.contains(resultV9));
		assertTrue(justStates.contains(resultV10));
		assertTrue(justStates.contains(resultH1));
		assertTrue(justStates.contains(resultH2));
		assertTrue(justStates.contains(resultH3));
		assertTrue(justStates.contains(resultH4));
		assertTrue(justStates.contains(resultH5));
		assertTrue(justStates.contains(resultH6));
		assertTrue(justStates.contains(resultH7));
		
		
		//This part tests that the actions of the action list do give you the given state
		for(Pair<TetrisState, ArrayList<Integer>> j : holder){
			TetrisState copyState = new TetrisState(testState); 
			ArrayList<Integer> actionList = j.t2;
			//actions
			for(int p = 0; p < actionList.size(); p++){
				copyState.take_action(actionList.get(p)); //move left
				copyState.update(); //update
			}
			assertTrue(j.t1.equals(copyState));
		}
		
	}
	
	@Test
	public void different_starting_blocks_positions(){ // This tests for the starting X and Y of each piece
		TetrisViewer testView = new TetrisViewer(); //make a TetrisViewer
		TetrisState testState = new TetrisState(); // makes a Tetris state to test with
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.currentBlockId = 1;
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.currentBlockId = 2;
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.currentBlockId = 3;
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.currentBlockId = 4;
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.currentBlockId = 5;
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.currentBlockId = 6;
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		
		testState.spawn_block();
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.spawn_block();
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
		testState.spawn_block();
		testView.update(testState);
		System.out.println("For Piece " + testState.currentBlockId + ", x = " + testState.currentX + ", y = " + testState.currentY + ", and the rotation is " + testState.currentRotation);
		MiscUtil.waitForReadStringAndEnterKeyPress(); 
	}
	
	@Test
	public void not_at_spawn(){ // This tests for available afterstates given that actions have been taken previously
		TetrisState testState = new TetrisState(); // makes a Tetris state to test with
	}

	@Test
	public void test_rotate_against_wall() { // This tests what a piece will do when it must rotate in a place it cannot
		
	}
	
	
}
