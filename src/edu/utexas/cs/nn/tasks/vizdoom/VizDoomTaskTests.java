package edu.utexas.cs.nn.tasks.vizdoom;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.Average;

public class VizDoomTaskTests {

	@Before
	public void setUp() throws Exception {
//		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false"});//TODO
//		MMNEAT.loadClasses();
		VizDoomTask.smudgeStat = new Average();
	}
	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void correct_value_test() {
		double[] inputs = {0.2, 0.4, 0.5, 0.6, 0.9, 0.5, 0.2, 0.1, 0.2, 0.1,
						   0.1, 0.5, 0.6, 0.5, 0.8, 0.7, 0.6, 0.4, 0.2, 0.2};
		double[] expectedSmudge = {0.3, 0.55, 0.725, 0.325, 0.175};
		double[] actualSmudge = VizDoomTask.smudgeInputs(inputs, 10, 2, VizDoomTask.RED_INDEX, 2);
		for(int i = 0; i < expectedSmudge.length; i++){
			assertEquals(expectedSmudge[i], actualSmudge[i], 0.01);
		}
	}

	@Test
	public void size_test() {
		double[] inputs = ArrayUtil.doubleOnes(40);
		double[] smudge1 = VizDoomTask.smudgeInputs(inputs, 10, 4, VizDoomTask.RED_INDEX, 1);
		double[] smudge2 = VizDoomTask.smudgeInputs(inputs, 10, 4, VizDoomTask.RED_INDEX, 2);
		double[] smudge3 = VizDoomTask.smudgeInputs(inputs, 10, 4, VizDoomTask.RED_INDEX, 3);
		double[] smudge4 = VizDoomTask.smudgeInputs(inputs, 10, 4, VizDoomTask.RED_INDEX, 4);
		assertEquals(smudge1.length, 40);
		assertEquals(smudge2.length, 10);
		assertEquals(smudge3.length, 3);
		assertEquals(smudge4.length, 2);
	}
	
	@Test
	public void position_test() {
		double[] inputs = {0.1, 0.1, 0.2, 0.2, 0.3, 0.3, 0.4, 0.4, 0.5, 0.5,
				           0.1, 0.1, 0.2, 0.2, 0.3, 0.3, 0.4, 0.4, 0.5, 0.5,
				           0.6, 0.6, 0.7, 0.7, 0.8, 0.8, 0.9, 0.9, 0.0, 0.0,
				           0.6, 0.6, 0.7, 0.7, 0.8, 0.8, 0.9, 0.9, 0.0, 0.0};
		double[] expectedSmudge = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0};
		double[] actualSmudge = VizDoomTask.smudgeInputs(inputs, 10, 4, VizDoomTask.RED_INDEX, 2);
		assertEquals(actualSmudge.length, expectedSmudge.length);
		for(int i = 0; i < expectedSmudge.length; i++){
			assertEquals(expectedSmudge[i], actualSmudge[i], 0.01);
		}
	}
}
