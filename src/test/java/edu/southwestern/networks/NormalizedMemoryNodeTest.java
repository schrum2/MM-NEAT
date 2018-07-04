package edu.southwestern.networks;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

public class NormalizedMemoryNodeTest {

	@Test
	public void testMeanNetwork() {
		Parameters.initializeParameterCollections(new String[0]);
		ArrayList<NodeGene> nodes = new ArrayList<NodeGene>();
		nodes.add(TWEANNGenotype.newNodeGene(ActivationFunctions.FTYPE_ID, TWEANN.Node.NTYPE_INPUT, -1, false, 0, true));
		nodes.add(TWEANNGenotype.newNodeGene(ActivationFunctions.FTYPE_ID, TWEANN.Node.NTYPE_OUTPUT, -2, false, 0, true));
		ArrayList<LinkGene> links = new ArrayList<LinkGene>();
		links.add(TWEANNGenotype.newLinkGene(-1, -2, 1, 3, false));
		TWEANNGenotype tweanng = new TWEANNGenotype(nodes, links, 1, false, false, 0);
		TWEANN tweann = new TWEANN(tweanng);
		for (int i = 0; i < 100; i++) {
			double[] inputs = new double[1];
			inputs[0] = RandomNumbers.fullSmallRand();
			System.out.println("in test" + tweann.process(inputs)[0]);
			//assertEquals(tweann.process(inputs)[0], i, 0);
		}
	}
}
