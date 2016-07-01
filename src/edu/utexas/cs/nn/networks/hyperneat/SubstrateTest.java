package edu.utexas.cs.nn.networks.hyperneat;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class SubstrateTest {

	Substrate sub = new Substrate(new Pair<Integer, Integer>(5, 5), 0, new Triple<Integer, Integer, Integer>(0, 0, 0),
			"I_0");
	@After
	public void tearDown() throws Exception {
		sub = null;
		MMNEAT.clearClasses();
	}
	
	@Test
	public void test() {
		assertTrue(sub.getName().equals("I_0"));
		assertTrue(sub.getSize().t1 * sub.getSize().t2 == 25);
		assertTrue(sub.getStype() != 1 && sub.getStype() != 2);
		assertTrue(sub.getSubLocation().equals(new Triple<Integer, Integer, Integer>(0, 0, 0)));
		System.out.println(sub.toString());
	}

}
