package edu.utexas.cs.nn.tasks.rlglue.tetris;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.BertsekasTsitsiklisTetrisExtractor;

public class TetrisAfterStateAgentTests {

	/**
	 * Instantiates parameters
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"task:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisTask", "rlGlueAgent:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.BertsekasTsitsiklisTetrisExtractor" });
		MMNEAT.loadClasses();
	}

	/**
	 * Tests that the outputs scale correctly
	 */
	@Test
	public void does_scale() {
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
		double[] inputs = BTTE.scaleInputs(BTTE.extract(o));

		double[] expected = new double[] { 0.1, 0.15, 0.15, 0.05, 0.05, 0.15, 0.2, 0.2, 0.2, 0.2, 0.05, 0, 0.1, 0, 0.1,
				0.05, 0, 0, 0, 0.2, 0.045, 1 };
		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i], expected[i], 0.0);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@Test
	public void action_sequence() {
		CommonConstants.randomArgMaxTieBreak = false;
		TetrisAfterStateAgent afterStateAgent = new TetrisAfterStateAgent();
		TetrisState testState = new TetrisState();
		afterStateAgent.policy = new Network() {
			@Override
			public int numInputs() {
				return MMNEAT.networkInputs;
			}
			@Override
			public int numOutputs() {
				return MMNEAT.networkOutputs;
			}
			@Override
			public int effectiveNumOutputs() {
				return MMNEAT.networkOutputs;
			}
			@Override
			public double[] process(double[] inputs) {
				return new double[]{1};
			}
			@Override
			public void flush() {
			}
			@Override
			public boolean isMultitask() {
				return false;
			}
			@Override
			public void chooseMode(int mode) {				
			}
			@Override
			public int lastModule() {
				return 0;
			}
			@Override
			public double[] moduleOutput(int mode) {
				return null;
			}
			@Override
			public int numModules() {
				return 1;
			}
			@Override
			public int[] getModuleUsage() {
				return null;
			}
		};
		BertsekasTsitsiklisTetrisExtractor BTTE = new BertsekasTsitsiklisTetrisExtractor();
		// line piece
		testState.worldState[166] = 1;
		testState.worldState[167] = 1;
		testState.worldState[168] = 1;
		testState.worldState[169] = 1;
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
		
		int oldX = testState.currentX;
		int oldY = testState.currentY;
		Observation o = testState.get_observation();
		assertTrue(afterStateAgent.currentActionList.isEmpty());
		Action a = afterStateAgent.getAction(o);
		
		assertFalse(afterStateAgent.currentActionList.isEmpty());
		assertEquals(afterStateAgent.currentActionList.size(), 4);
		assertEquals(a.getInt(0), TetrisState.RIGHT); // 1
		testState.take_action(a.getInt(0));
		assertEquals(testState.currentX, oldX + 1);
		assertEquals(testState.currentY, oldY);
		oldX = testState.currentX;
		
		Action b = afterStateAgent.getAction(o);
		assertEquals(afterStateAgent.currentActionList.size(), 3);
		assertEquals(b.getInt(0), TetrisState.RIGHT); // 1
		testState.take_action(b.getInt(0));
		assertEquals(testState.currentX, oldX + 1);
		assertEquals(testState.currentY, oldY);
		oldX = testState.currentX;
		
		Action c = afterStateAgent.getAction(o);
		assertEquals(afterStateAgent.currentActionList.size(), 2);
		assertEquals(c.getInt(0), TetrisState.CW); // 2
		testState.take_action(c.getInt(0));
		assertEquals(testState.currentX, oldX);
		assertEquals(testState.currentY, oldY);
		
		Action d = afterStateAgent.getAction(o);
		assertEquals(afterStateAgent.currentActionList.size(), 1);
		assertEquals(d.getInt(0), TetrisState.FALL); // 5
		testState.take_action(d.getInt(0));
		assertEquals(testState.currentX, oldX);
		assertEquals(testState.currentY, oldY + 11); // needs to fall all the way
		oldY = testState.currentY;
		
		Action e = afterStateAgent.getAction(o);
		assertEquals(afterStateAgent.currentActionList.size(), 0);
		assertEquals(e.getInt(0), TetrisState.NONE); // 4
		testState.take_action(e.getInt(0));
		assertEquals(testState.currentX, oldX);
		assertEquals(testState.currentY, oldY);
		
	}
}
