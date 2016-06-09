package edu.utexas.cs.nn.evolution.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 * JUnit test for most basic case of a hyperNEATCPPNGenotype
 * 
 * @author gillespl
 *
 */
public class HyperNEATCPPNGenotypeTest {

	// hardcoded indices of substrate from list
	public static int sub1Index = 0;
	public static int sub2Index = 1;

	HyperNEATCPPNGenotype hcppn;
	TWEANN cppn;
	LinkedList<Substrate> subs;
	LinkedList<Pair<String, String>> connections;
	HashMap<String, Integer> sIMap;

	/**
	 * Sets up test environment
	 */
	@Before
	public void setUp() {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"mmdRate:1.0" });
		MMNEAT.loadClasses();
		hcppn = new HyperNEATCPPNGenotype();
		cppn = new TWEANN(new TWEANNGenotype());
		subs = new LinkedList<Substrate>();
		connections = new LinkedList<Pair<String, String>>();
		subs.add(new Substrate(new Pair<Integer, Integer>(5, 5), 0, new Triple<Integer, Integer, Integer>(0, 0, 0),"I_0"));// only 2 substrates in this test
		subs.add(new Substrate(new Pair<Integer, Integer>(5, 5), 0, new Triple<Integer, Integer, Integer>(1, 0, 0),"I_1"));// both input substrates
		// only one connection, between the two substrates
		connections.add(new Pair<String, String>(subs.get(0).getName(), subs.get(1).getName()));
		// links connection list index to substrate
		sIMap = new HashMap<String, Integer>();
		for (int i = 0; i < subs.size(); i++) {
			sIMap.put(subs.get(i).getName(), i);
		}
		System.out.println("----------------Set up---------------------");
		System.out.println("hcppn: " + hcppn.toString());
		System.out.println("cppn: " + cppn.toString());
		System.out.println("subs: " + subs.toString());
		System.out.println("connections: " + connections.toString());
		System.out.println("sIMap: " + sIMap.toString());
	}

	@After
	public void tearDown() {
		hcppn = null;
		cppn = null;
		subs = null;
		connections = null;
		sIMap = null;
		MMNEAT.clearClasses();
	}

	/**
	 * Tests creation of list of nodes in substrates. Order of substrates is not
	 * imortant as long as mapping is accurate
	 */
	@Test
	public void testCreateSubstrateNodesSlow() {
		ArrayList<NodeGene> nodes = hcppn.createSubstrateNodes(subs);
		assertEquals(nodes.size(), subs.get(sub1Index).getSize().t1 * subs.get(sub1Index).getSize().t2
				+ subs.get(sub2Index).getSize().t1 * subs.get(sub2Index).getSize().t2);
		assertEquals(hcppn.innovationID, nodes.size());
		tearDown();
	}

	// Schrum: Should this be removed now?
	// /**
	// * Tests creation of list of links. Utilizes other methods tested below
	// * Only a viable test when command line parameter for test for expression
	// threshold is off.
	// * Otherwise, all other functions of createNodeLinks method checked in
	// other tests
	// */
	// *@Test
	// *public void testCreateNodeLinks() {
	// * ArrayList<LinkGene> links = hcppn.createNodeLinks(cppn, connections,
	// subs, sIMap);
	// * int sizeLinks = subs.get(sub1Index).size.t1*subs.get(sub2Index).size.t2
	// * subs.get(sub2Index).size.t1*subs.get(sub2Index).size.t2;
	// * assertEquals(sizeLinks, links.size());
	// *}
	// */

	/**
	 * Tests looping through the two substrates to be connected and setting
	 * those connections
	 */
	@Test
	public void testLoopThroughLinks() {
		int endingIndex = connections.size() - 1;
		int indexOfTest = 0;
		ArrayList<LinkGene> newLinks = new ArrayList<LinkGene>();
		hcppn.loopThroughLinks(newLinks, cppn, indexOfTest, subs.get(sub1Index), subs.get(sub2Index), sub1Index, sub2Index, subs);
		// below check only necessary when parameter for including link is not
		// on
		// assertEquals(subs.get(sub1Index).size.t1*subs.get(sub1Index).size.t2*subs.get(sub2Index).size.t1*subs.get(sub2Index).size.t2,
		// newLinks.size());

		ILocated2D scaledSourceCoordinates = CartesianGeometricUtilities.centerAndScale(new Tuple2D(0, 0),
				subs.get(sub1Index).size.t1, subs.get(sub1Index).size.t2);
		Tuple2D size = new Tuple2D(subs.get(sub2Index).size.t1 - 1, subs.get(sub2Index).size.t2 - 1);
		ILocated2D scaledTargetCoordinates = CartesianGeometricUtilities.centerAndScale(size,
				subs.get(sub2Index).size.t1, subs.get(sub2Index).size.t2);

		assertEquals(scaledSourceCoordinates.getY(), -1, .001);
		assertEquals(scaledSourceCoordinates.getX(), -1, .001);
		assertEquals(scaledTargetCoordinates.getX(), 1, .01);
		assertEquals(scaledTargetCoordinates.getY(), 1, .01);

		assertEquals(newLinks.get(sub1Index).sourceInnovation, hcppn.getInnovationID(0, 0, sub1Index, subs));
		assertEquals(newLinks.get(sub1Index).sourceInnovation, hcppn.getInnovationID(0, 0, endingIndex, subs));
	}

	@Test
	public void testHashMapping() {
		assertTrue(sIMap.get(subs.get(sub1Index).getName()).equals(sub1Index));
	}

	/**
	 * Test function to get innovation ID of a node
	 */
	@Test
	public void testGetInnovationID() {
		int indexOfTest = 0;
		ArrayList<LinkGene> newLinks = new ArrayList<LinkGene>();
		hcppn.loopThroughLinks(newLinks, cppn, indexOfTest, subs.get(sub1Index), subs.get(sub2Index), sub1Index, sub2Index, subs);
	}

	/**
	 * tests link expression calculation works
	 */
	@Test
	public void testLinkExpressionThreshold() {
		double x = 1.0;
		double y = -1.0;
		assertEquals(hcppn.calculateWeight(x), .8, .0001);
		assertEquals(hcppn.calculateWeight(y), -.8, .0001);
	}
}
