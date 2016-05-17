package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisViewer;
import edu.utexas.cs.nn.util.MiscUtil;

public class BertsekasTsitsiklisTetrisExtractorTests {

	@Test
	public void array_outputs() {
		TetrisViewer testView = new TetrisViewer(); //make a TetrisViewer
		TetrisState testState = new TetrisState(); // makes a Tetris state to test with
		BertsekasTsitsiklisTetrisExtractor BTTE = new BertsekasTsitsiklisTetrisExtractor();
		//line piece
		testState.worldState[166] = 1;
		testState.worldState[167] = 1;
		testState.worldState[168] = 1;
		testState.worldState[169] = 1;
		//S piece
		testState.worldState[171] = 1;
		testState.worldState[172] = 1;
		testState.worldState[180] = 1;
		testState.worldState[181] = 1;
		//J piece 1
		testState.worldState[192] = 1;
		testState.worldState[193] = 1;
		testState.worldState[194] = 1;
		testState.worldState[182] = 1;
		//J piece 2
		testState.worldState[197] = 1;
		testState.worldState[187] = 1;
		testState.worldState[177] = 1;
		testState.worldState[178] = 1;
		//tri piece
		testState.worldState[195] = 1;
		testState.worldState[185] = 1;
		testState.worldState[175] = 1;
		testState.worldState[186] = 1;
		testView.update(testState);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		Observation o = testState.get_observation();
		System.out.println("Observation created");
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		double[] inputs = BTTE.extract(o); 
		int first_length = inputs.length - (testState.worldWidth - 1) - 3;
		int second_length = inputs.length - 3;
		for(int i = 0; i < first_length ; i++){
			
		}
		for(int i = first_length; i < first_length ; i++){
			
		}
		System.out.println(Arrays.toString(inputs));
		MiscUtil.waitForReadStringAndEnterKeyPress();

	}
}
