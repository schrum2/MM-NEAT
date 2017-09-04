package edu.southwestern.tasks.rlglue.featureextractors.tetris;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class ExtendedBertsekasTsitsiklisTetrisExtractorTests {
	
	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"task:edu.southwestern.tasks.rlglue.tetris.TetrisTask", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.ExtendedBertsekasTsitsiklisTetrisExtractor" });
		MMNEAT.loadClasses();
	}

	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests that the number of holes in a TetrisState is correctly identified
	 */
	@Test
	public void number_of_holes() {
		TetrisState testState = new TetrisState();
		BertsekasTsitsiklisTetrisExtractor EBTTE = new ExtendedBertsekasTsitsiklisTetrisExtractor();
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
		double[] inputs = EBTTE.scaleInputs(EBTTE.extract(o));
		System.out.println(inputs.length);
		System.out.println(Arrays.toString(inputs));
		double[] expected = EBTTE.scaleInputs(new double[] { 2, 3, 3, 1, 1, 3, 4, 4, 4, 4, 1, 0, 2, 0, 2, 1, 0, 0, 0, 4,
				9, 1, 1, 1, 0, 0, 0, 0, 2, 0, 2, 3 });
		System.out.println(expected.length);
		System.out.println(Arrays.toString(expected));
		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i], expected[i], 0.0);
		}
	}
}
