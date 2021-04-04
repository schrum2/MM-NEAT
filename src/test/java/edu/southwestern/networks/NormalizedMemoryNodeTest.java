package edu.southwestern.networks;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class NormalizedMemoryNodeTest {

	//@Test
	public void testMeanNetwork() {
		Parameters.initializeParameterCollections(new String[0]);
		ArrayList<NodeGene> nodes = new ArrayList<NodeGene>();
		nodes.add(TWEANNGenotype.newNodeGene(ActivationFunctions.FTYPE_ID, TWEANN.Node.NTYPE_INPUT, -1, false, 0, true));
		nodes.add(TWEANNGenotype.newNodeGene(ActivationFunctions.FTYPE_ID, TWEANN.Node.NTYPE_OUTPUT, -2, false, 0, true));
		ArrayList<LinkGene> links = new ArrayList<LinkGene>();
		links.add(TWEANNGenotype.newLinkGene(-1, -2, 1, 3, false));
		TWEANNGenotype tweanng = new TWEANNGenotype(nodes, links, 1, false, 0);
		TWEANN tweann = new TWEANN(tweanng);
		ArrayList<Double> inputsSoFar = new ArrayList<Double>();
		ArrayList<Double> inputActivationsSoFar = new ArrayList<Double>();
		for (double i = -1; i < 1; i += .01) {
			inputsSoFar.add(i);
			double[] inputs = new double[1];
			inputs[0] = i;
			double finalOutput = tweann.process(inputs)[0];
			
			double mean = StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(inputsSoFar));
			double var = StatisticsUtilities.populationVariance(ArrayUtil.doubleArrayFromList(inputsSoFar));
			double expectedActivation = (i - mean) / Math.sqrt(var + NormalizedMemoryNode.EPSILON);
			
			// Check outputs of input neuron
			assertEquals(tweann.nodes.get(0).activation, expectedActivation, 0.00000000001);
			
			inputActivationsSoFar.add(expectedActivation);
			mean = StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(inputActivationsSoFar));
			var = StatisticsUtilities.populationVariance(ArrayUtil.doubleArrayFromList(inputActivationsSoFar));
			expectedActivation = (expectedActivation - mean) / Math.sqrt(var + NormalizedMemoryNode.EPSILON);
			
			// Check output of actual output neuron
			assertEquals(finalOutput, expectedActivation, 0.00000000001);
			
//			System.out.println("inputsSoFar:" + inputsSoFar);
//			System.out.println("inputActivationsSoFar:" + inputActivationsSoFar);
			
		}
	}
}
