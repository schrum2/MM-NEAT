package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.parameters.Parameters;

public class ActivationFunctionMutationTest {

	TWEANNGenotype tg1;
	int NUM_MUTATIONS = 20;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "netChangeActivationRate:1.0", "allowMultipleFunctions:true"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		for(int i = 0; i < NUM_MUTATIONS; i++) {
			tg1.mutate();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		ArrayList<NodeGene> originalNodes = tg1.nodes;
		ArrayList<Integer> originalFunc = new ArrayList<Integer>();
		ArrayList<Integer> mutatedFunc = new ArrayList<Integer>();
		ActivationFunctionMutation afm = new ActivationFunctionMutation();
		System.out.println("original nodes: "  + originalNodes.toString());
		for(int i = 0; i < 100; i ++) {
			afm.mutate(tg1);
			tg1.mutate();
		}
		System.out.println("new nodes: " + tg1.nodes.toString());
		for(int i = 0; i < originalNodes.size(); i++) { 
			originalFunc.add(originalNodes.get(i).ftype);
			mutatedFunc.add(tg1.nodes.get(i).ftype);
		}
		System.out.println("original functions: " + originalFunc.toString());
		System.out.println("new functions: " + mutatedFunc.toString());
		assertFalse(originalFunc.equals(mutatedFunc));
	}

}
