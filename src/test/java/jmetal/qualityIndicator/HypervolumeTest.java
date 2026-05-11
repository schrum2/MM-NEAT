package jmetal.qualityIndicator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HypervolumeTest {

	Hypervolume hv;
	
	@Before
	public void setUp() {
		hv = new Hypervolume();
	}
	
	@Test
	public void testCalculateHypervolume() {
		double[][] scoresArrayForHypervolume = new double[][] {
			{6,13,9},
			{17,5,9},
			{7,10,16}
		};
		int numberOfPoints = 3;
		int numberOfObjectives = 3;
		assertEquals(1732, hv.calculateHypervolume(scoresArrayForHypervolume, numberOfPoints, numberOfObjectives), 0.0);
	}

}
