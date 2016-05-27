package edu.utexas.cs.nn.tasks.rlglue.tetris;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.BertsekasTsitsiklisTetrisExtractor;
import edu.utexas.cs.nn.util.MiscUtil;

public class TetrisAfterStateAgentTests {
	@Test
	public void does_scale() {
		// Test borrowed from BertsekasTsitsiklisTetrisExtractorTests THANKS ME,
		// YOU'RE WELCOME ME -Gab
		// TetrisViewer testView = new TetrisViewer(); //make a TetrisViewer
		TetrisState testState = new TetrisState(); // makes a Tetris state to
													// test with
		BertsekasTsitsiklisTetrisExtractor BTTE = new BertsekasTsitsiklisTetrisExtractor();
		// line piece
		testState.worldState[166] = 1;
		testState.worldState[167] = 1;
		testState.worldState[168] = 1;
		testState.worldState[169] = 1;
		testState.currentX += 2;
		testState.currentY += 14;
		// S piece
		testState.worldState[171] = 1;
		testState.worldState[172] = 1;
		testState.worldState[180] = 1;
		testState.worldState[181] = 1;
		// J piece 1
		testState.worldState[192] = 1;
		testState.worldState[193] = 1;
		testState.worldState[194] = 1;
		testState.worldState[182] = 1;
		// J piece 2
		testState.worldState[197] = 1;
		testState.worldState[187] = 1;
		testState.worldState[177] = 1;
		testState.worldState[178] = 1;
		// tri piece
		testState.worldState[195] = 1;
		testState.worldState[185] = 1;
		testState.worldState[175] = 1;
		testState.worldState[186] = 1;
		// System.out.println(testState);
		// MiscUtil.waitForReadStringAndEnterKeyPress();

		Observation o = testState.get_observation();
		double[] inputs = BTTE.scaleInputs(BTTE.extract(o));

		double[] expected = new double[] { 0.1, 0.15, 0.15, 0.05, 0.05, 0.15, 0.2, 0.2, 0.2, 0.2, 0.05, 0, 0.1, 0, 0.1,
				0.05, 0, 0, 0, 0.2, 0.045, 1 };
		for (int i = 0; i < inputs.length; i++) {
			// System.out.println("When the input is at " + i + ", scaled is " +
			// inputs[i] + " and expected is " + expected[i]);
			assertEquals(inputs[i], expected[i], 0.0);
		}
		// System.out.println(Arrays.toString(inputs));
	}
}
