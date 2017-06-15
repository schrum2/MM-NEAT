package edu.utexas.cs.nn.networks.hyperneat;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.random.RandomNumbers;
/**
 * JUnit testing class for hyperNEATutil class
 * @author Lauren Gillespie
 *
 */
public class HyperNEATUtilTest {

	//global variables
	Substrate[] subs;
	ArrayList<Node> nodes;
	TWEANN  t1;
	HyperNEATTask htask;
	TWEANNGenotype tg1;
	
	/**
	 * sets up a new substrate array before each test
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"task:edu.utexas.cs.nn.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueAgent:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "hyperNEAT:true", "showWeights:true" });
		MMNEAT.loadClasses();
		htask = (HyperNEATTask) MMNEAT.task;
		//MMNEAT.task = (Task) htask;
		tg1 = new TWEANNGenotype();
		t1 = new TWEANN(tg1);
		subs = new Substrate[3];
		Substrate sub1 = new Substrate(new Pair<Integer, Integer>(10, 20), 0, new Triple<Integer, Integer, Integer>(0, 0, 0), "I_0");
		
		subs[0] = sub1;
		nodes = new ArrayList<Node>();
		Substrate sub2 = new Substrate(new Pair<Integer, Integer>(10, 20), 1, new Triple<Integer, Integer, Integer>(0, 1, 0), "H_0");
		subs[1] = sub2;
		Substrate sub3 = new Substrate(new Pair<Integer, Integer>(1, 1), 2, new Triple<Integer, Integer, Integer>(0, 2, 0), "O_0");
		subs[2] = sub3;
		long l = addNodes(subs[0], Node.NTYPE_INPUT, 0);
		l = addNodes(subs[1], Node.NTYPE_HIDDEN, l);
		l = addNodes(subs[2], Node.NTYPE_OUTPUT, l);
		for(Node n : nodes) {
			n.artificiallySetActivation(RandomNumbers.fullSmallRand());
		}
	}
 
	/**
	 * Adds nodes and gives them artificial activations
	 * @param sub substrate
	 * @param ntype ftype of node
	 * @param l inno #
	 * @return biggest used inno #
	 */
	public long addNodes(Substrate sub, int ntype, long l) {
		for(int i = 0; i < sub.getSize().t1 * sub.getSize().t2; i ++) {
			Node n = t1.new Node(ActivationFunctions.FTYPE_SIGMOID, ntype, l++);
			n.artificiallySetActivation(RandomNumbers.fullSmallRand());
			nodes.add(n);
		}
		return l;
	}
	/**
	 * Tears down between tests
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		subs = null;
		nodes = null;
		t1 = null;
		htask = null;
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests the draw substrate method
	 */
	@Test
	public void testDrawSubstrateVisual() {
		HyperNEATUtil.drawSubstrate(subs[0], nodes, 0);
	}
	
	/**
	 * Tests draw substrates method
	 */
	@Test
	public void testDrawSubstratesVisual() { 
		HyperNEATUtil.drawSubstrates(nodes);
	}
	
	@Test
	public void testDrawWeightsVisual() {
//		ArrayList<DrawingPanel> weightPanels = HyperNEATUtil.drawWeight(tg1, htask);
//		DrawingPanel d =  HyperNEATUtil.drawWeight(subs[0], subs[1], 0, 1);
	}
	
	@Test
	public void testResetSubstrates(){
		HyperNEATUtil.resetSubstrates();
	}
	
	@Test
	public void testNumBiasOutputsNeeded(){
		assertEquals(2, HyperNEATUtil.numBiasOutputsNeeded(htask));
	}
	
	@Test
	public void testIndexFirstBiasOutput(){
		assertEquals(2, HyperNEATUtil.indexFirstBiasOutput(htask));
	}
	
	@Test
	public void testGetSubstrateInformationHyperNEAT(){
		
		List<Triple<String, Integer, Integer>> output = new ArrayList<Triple<String, Integer, Integer>>();
		output.add(new Triple<String, Integer, Integer>("o1", 5, 5));
		output.add(new Triple<String, Integer, Integer>("o2", 10, 10));
		output.add(new Triple<String, Integer, Integer>("o3", 1, 1));
		
		List<Substrate> test = HyperNEATUtil.getSubstrateInformation(10, 20, 3, 2, 3, output); // Should have 12 Substrates
		
		for(int i = 0; i < 9; i++){ // Tests the size of the non-output substrates
			assertEquals(test.get(i).getSize(), new Pair<Integer, Integer>(10, 20));
		}
		
		// Tests the Size of the three Output Substrates
		assertEquals(test.get(9).getSize(), new Pair<Integer, Integer>(5, 5));
		assertEquals(test.get(10).getSize(), new Pair<Integer, Integer>(10, 10));
		assertEquals(test.get(11).getSize(), new Pair<Integer, Integer>(1, 1));
		
		// Tests the Names of each Substrate
		
		for(int i = 0; i < 3; i++){
			assertEquals(test.get(i).getName(), "Input(" + i + ")");			
		}
		
		int index = 3;
		for(int i = 0; i < 3; i++){ // Process Depth
			for(int j = 0; j < 2; j++){ // Process Width
				assertEquals(test.get(index++).getName(), "process(" + j + "," + i + ")");
			}
		}
		
		assertEquals(test.get(9).getName(), "o1");
		assertEquals(test.get(10).getName(), "o2");
		assertEquals(test.get(11).getName(), "o3");
	}
	
	@Test
	public void testGetSubstrateConnectivityHyperNEAT(){
		
		List<String> outputNames = new ArrayList<String>();
		outputNames.add("o1");
		outputNames.add("o2");
		outputNames.add("o3");
		
		List<Triple<String,String,Boolean>> test = HyperNEATUtil.getSubstrateConnectivity(3, 2, 3, outputNames, false);
		
		assertEquals(test.get(0), new Triple<String, String, Boolean>("Input(0)", "process(0,0)", Boolean.TRUE));
		
		int index = 0;
		
		for(int i = 0; i < 2; i++){ // Process Width
			for(int j = 0; j < 3; j++){ // Number of Inputs
				assertEquals(test.get(index++), new Triple<String, String, Boolean>("Input("+ j +")", "process(" + i + ",0)", Boolean.TRUE));
			}
		}
		
		
		for(int i = 0; i < 2; i++){ // Process Depth-1
			for(int j = 0; j < 2; j++){ // Process Width
				for(int k = 0; k < 2; k++){ // Process Width
					assertEquals(test.get(index++), new Triple<String, String, Boolean>("process("+ j + "," + i +")", "process(" + k + ","+ (i+1) + ")", Boolean.TRUE));
				}
			}
		}
		
		for(int i = 0; i < 2; i++){ // Process Width
			for(int j = 0; j < 3; j++){ // Number of Outputs
				assertEquals(test.get(index++), new Triple<String, String, Boolean>("process(" + i + ",3)", "o" + (j+1),  Boolean.FALSE));
			}
		}
		
	}
	
	
}
