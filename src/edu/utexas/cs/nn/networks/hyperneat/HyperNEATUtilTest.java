package edu.utexas.cs.nn.networks.hyperneat;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.random.RandomNumbers;
/**
 * JUnit testing class for hyperNEATutil class
 * @author Lauren Gillespie
 *
 */
public class HyperNEATUtilTest {

	Substrate[] subs;
	ArrayList<Node> nodes;
	TWEANN  t1;
	HyperNEATTask htask;
	@Before
	public void setUp() throws Exception {

		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"task:edu.utexas.cs.nn.tasks.rlglue.tetris.HyperNEATTetrisTask", "rlGlueAgent:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "hyperNEAT:true" });
		MMNEAT.loadClasses();
		htask = (HyperNEATTask) MMNEAT.task;
		//MMNEAT.task = (Task) htask;
		TWEANNGenotype tg1 = new TWEANNGenotype();
		t1 = new TWEANN(tg1);
		subs = new Substrate[3];
		Substrate sub1 = new Substrate(new Pair<Integer, Integer>(5, 5), 0, new Triple<Integer, Integer, Integer>(0, 0, 0), "I_0");
		subs[0] = sub1;
		nodes = new ArrayList<Node>();
		Substrate sub2 = new Substrate(new Pair<Integer, Integer>(5, 5), 1, new Triple<Integer, Integer, Integer>(0, 1, 0), "H_0");
		subs[1] = sub2;
		Substrate sub3 = new Substrate(new Pair<Integer, Integer>(2, 2), 2, new Triple<Integer, Integer, Integer>(0, 2, 0), "O_0");
		subs[2] = sub3;
		long l = addNodes(subs[0], Node.NTYPE_INPUT, 0);
		l = addNodes(subs[1], Node.NTYPE_HIDDEN, l);
		l = addNodes(subs[2], Node.NTYPE_OUTPUT, l);
	}
 
	public long addNodes(Substrate sub, int ntype, long l) {
		for(int i = 0; i < sub.size.t1 * sub.size.t2; i ++) {
			Node n = t1.new Node(ActivationFunctions.FTYPE_SIGMOID, ntype, l++);
			n.artificiallySetActivation(RandomNumbers.fullSmallRand());
			nodes.add(n);
		}
		return l;
	}
	@After
	public void tearDown() throws Exception {
		subs = null;
		nodes = null;
		MMNEAT.clearClasses();
	}

	/**
	 * Visual test of drawSubstrate method
	 */
	@Test
	public void testDrawSubstrateVisual() {
		DrawingPanel dp = HyperNEATUtil.drawSubstrate(subs[0], nodes, 0);
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		for(Node n : nodes) {
			n.artificiallySetActivation(RandomNumbers.fullSmallRand());
		}
		HyperNEATUtil.drawSubstrate(dp, subs[0], nodes, 0);
		MiscUtil.waitForReadStringAndEnterKeyPress();
	}
	
	@Test
	public void testDrawSubstratesVisual() { 
		HyperNEATUtil.drawSubstrates(nodes);

		MiscUtil.waitForReadStringAndEnterKeyPress();
	}
}
