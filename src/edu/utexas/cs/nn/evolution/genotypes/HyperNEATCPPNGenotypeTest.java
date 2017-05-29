package edu.utexas.cs.nn.evolution.genotypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.networks.NetworkUtil;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
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
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", 
										"task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask","linkExpressionThreshold:-1"});
		MMNEAT.loadClasses();
		hcppn = new HyperNEATCPPNGenotype();
		cppn = hcppn.getCPPN();
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
	}
	
	@After
	public void tearDown() {
		hcppn = null;
		cppn = null;
		subs = null;
		connections = null;
		sIMap = null;
		MMNEAT.task = null;
		MMNEAT.clearClasses();
	}

	/**
	 * Tests creation of list of nodes in substrates. Order of substrates is not
	 * imortant as long as mapping is accurate
	 */
	@Test
	public void testCreateSubstrateNodesSlow() {
		ArrayList<NodeGene> nodes = hcppn.createSubstrateNodes((HyperNEATTask) MMNEAT.task,  hcppn.getCPPN(), subs);
		//Asserts that the size of the array list created is equal to the sum of the areas of the two substrates
		// (sub1 height * sub1 width + sub2 height * sub2 width)
		assertEquals(nodes.size(), subs.get(sub1Index).getSize().t1 * subs.get(sub1Index).getSize().t2
				+ subs.get(sub2Index).getSize().t1 * subs.get(sub2Index).getSize().t2);
		//asserts that the size of the ArrayList created is equivalent to the genotype's innovation ID
		assertEquals(hcppn.innovationID, nodes.size());
		//asserts that genes were generated in the right order
		for(long i = 0; i < nodes.size(); i++) {
			assertEquals(nodes.get((int) i).innovation, i);
		}
	}

	/**
	 * Tests looping through the two substrates to be connected and setting
	 * those connections
	 */
	@Test
	public void testLoopThroughLinks() {
		int indexOfTest = 0;
		ArrayList<LinkGene> newLinks = new ArrayList<LinkGene>();
		hcppn.loopThroughLinks((HyperNEATTask) MMNEAT.task, newLinks, cppn, indexOfTest, subs.get(sub1Index), subs.get(sub2Index), sub1Index, sub2Index, subs);
		ILocated2D scaledSourceCoordinates = CartesianGeometricUtilities.centerAndScale(new Tuple2D(0, 0),
				subs.get(sub1Index).getSize().t1, subs.get(sub1Index).getSize().t2);
		Tuple2D size = new Tuple2D(subs.get(sub2Index).getSize().t1 - 1, subs.get(sub2Index).getSize().t2 - 1);
		
		//assert size is (4,4)
		
		ILocated2D scaledTargetCoordinates = CartesianGeometricUtilities.centerAndScale(size,
				subs.get(sub2Index).getSize().t1, subs.get(sub2Index).getSize().t2);
		
		assertEquals(scaledSourceCoordinates.getY(), -1, .001);
		assertEquals(scaledSourceCoordinates.getX(), -1, .001);
		assertEquals(scaledTargetCoordinates.getX(), 1, .01);
		assertEquals(scaledTargetCoordinates.getY(), 1, .01);
	
		int index = 0;
		for(long i = 0; i <= 24; i++) { //neuron innovation numbers present in first substrate
			for(long j = 25; j <= 49; j++) { //neuron innovation numbers present in second substrate
				assertEquals(newLinks.get(index).sourceInnovation, i);
				assertEquals(newLinks.get(index).targetInnovation, j);
				index++;
			}
		}
		
//		assertEquals(newLinks.get(sub1Index).sourceInnovation, hcppn.getInnovationID(0, 0, sub1Index, subs));
//		assertEquals(newLinks.get(sub1Index).sourceInnovation, hcppn.getInnovationID(0, 0, endingIndex, subs));
	}

	/**
	 * Checks to make sure get phenotype returns the created network and that
	 * the getCPPN returns the cppn used
	 */
	@Test
	public void testGetPhenotype()  {
		hcppn.getPhenotype();
		assertTrue(!hcppn.getPhenotype().equals(hcppn.getCPPN()));
	}

	/**
	 * Tests that the hash mapping is correct
	 */
	@Test
	public void testHashMapping() {
		assertTrue(sIMap.get(subs.get(sub1Index).getName()).equals(sub1Index));
	}

	/**
	 * tests link expression calculation works
	 */
	@Test
	public void testLinkExpressionThreshold() {
		// This particular test needs the default expression threashold to work
		CommonConstants.linkExpressionThreshold = 0.2;
		
		double x = 1.0;
		double y = -1.0;
		assertEquals(NetworkUtil.calculateWeight(x), .8, .0001);
		assertEquals(NetworkUtil.calculateWeight(y), -.8, .0001);
		// More tests needed

		// Set back to -1 so all linkes are expressed
		CommonConstants.linkExpressionThreshold = -1;	
	}

	/**
	 * Tests that leo is used correctly
	 */
	@Test
	public void testLeo() {
		//System.out.println("\t\tRESET!");
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask", "hyperNEAT:true", "leo:true", "linkExpressionThreshold:-2.0", "evolveHyperNEATBias:false"});
		MMNEAT.loadClasses();
		hcppn = new HyperNEATCPPNGenotype();
		//System.out.println("\t\t" + hcppn.numOut);
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		assertEquals(CommonConstants.leo, true);
		TWEANN cppn = hcppn.getCPPN();
		HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
		
		System.out.println("CPPN: " + cppn.toString());
		System.out.println("network:" + hcppn.getPhenotype());
		assertEquals(cppn.numOutputs(),task.getSubstrateConnectivity().size() * 2);
		assertEquals(hcppn.links.size(), HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS * task.getSubstrateConnectivity().size() * 2);

		TWEANN t = hcppn.getPhenotype();
		ArrayList<Node> nodes = t.nodes;
		for(int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if(node.ntype == Node.NTYPE_INPUT) { 
				assertEquals(node.outputs.size(), 9);
			}else if(node.ntype == Node.NTYPE_HIDDEN) {
				assertEquals(node.outputs.size(), 8);
			}else {
				assertEquals(node.outputs.size(), 0);
			}
		}
	}

	/**
	 * Tests that the biases are inserted correctly
	 */
	@Test
	public void testBias() { 
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"hyperNEAT:true", "task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask", "evolveHyperNEATBias:true", "linkExpressionThreshold:0.0"});
		MMNEAT.loadClasses();
		assertTrue(CommonConstants.evolveHyperNEATBias);
		hcppn = new HyperNEATCPPNGenotype();
		TWEANN t = hcppn.getPhenotype();
		ArrayList<Node> nodes = t.nodes;
		for(Node node : nodes) {
			if(node.ntype != TWEANN.Node.NTYPE_INPUT)
				assertTrue(node + " bias is " + node.bias, node.bias != 0.0);
		}
		t.flush();
		nodes = t.nodes;
		double[] biases = new double[nodes.size()];
		int x = 0;
		for(Node node: nodes) {
			if(node.ntype != TWEANN.Node.NTYPE_INPUT)
				assertTrue(node.bias != 0.0);
			biases[x++] = node.bias;
		}
		double[] inputs = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		t.process(inputs);
		nodes = t.nodes;
		int y = 0;
		for(Node node : nodes) { 
			assertEquals(biases[y++], node.bias, .0001);
		}
	}
}
