package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import static org.junit.Assert.*;

import org.junit.Test;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

public class BertsekasTsitsiklisTetrisExtractorTests {

	/**
	 * Tests that the outputs given
	 */
	@Test
	public void array_outputs() {
		TetrisState testState = new TetrisState();
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
		Observation o = testState.get_observation();
		double[] inputs = BTTE.extract(o);

		double[] expected = new double[] { 2, 3, 3, 1, 1, 3, 4, 4, 4, 4, 1, 0, 2, 0, 2, 1, 0, 0, 0, 4, 9, 1 };

		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i], expected[i], 0.0);
		}
	}

	/**
	 * Tests that the outputs given by the extractor are what is expected
	 */
	@Test
	public void array_outputs_blind() { 
		TetrisState testState = new TetrisState();
		BertsekasTsitsiklisTetrisExtractor BTTE = new BertsekasTsitsiklisTetrisExtractor();
		// line piece
		testState.worldState[165] = 1;
		testState.worldState[175] = 1;
		testState.worldState[185] = 1;
		testState.worldState[195] = 1;
		testState.currentX -= 1;
		testState.currentY += 15;
		testState.currentRotation = 1;
		// J piece
		testState.worldState[180] = 1;
		testState.worldState[181] = 1;
		testState.worldState[182] = 1;
		testState.worldState[192] = 1;
		// tri piece 1
		testState.worldState[199] = 1;
		testState.worldState[189] = 1;
		testState.worldState[179] = 1;
		testState.worldState[188] = 1;
		// tri piece 2
		testState.worldState[167] = 1;
		testState.worldState[177] = 1;
		testState.worldState[187] = 1;
		testState.worldState[178] = 1;

		Observation o = testState.get_observation();
		double[] inputs = BTTE.scaleInputs(BTTE.extract(o));

		double[] expected = BTTE
				.scaleInputs(new double[] { 2, 2, 2, 0, 0, 4, 0, 4, 3, 3, 0, 0, 2, 0, 4, 4, 4, 1, 0, 4, 4, 1 });
		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i], expected[i], 0.0);
		}
	}
}
