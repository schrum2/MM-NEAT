package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.parameters.Parameters;

public class ActivationFunctionMutationTest {

	TWEANNGenotype tg1;
	int NUM_MUTATIONS = 20;
	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "netChangeActivationRate:1.0", "allowMultipleFunctions:true"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		//System.out.println(tg1);
		
		//Mutate the network to give it an interesting structure before testing
		for(int i = 0; i < NUM_MUTATIONS; i++) {
			//System.out.println(EvolutionaryHistory.archetypes[0]);
			tg1.mutate();
		}
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		//The list of nodes before the mutation
		ArrayList<NodeGene> originalNodes = tg1.nodes;
		//The list of activation functions before the mutation
		ArrayList<Integer> originalFunc = new ArrayList<Integer>();
		//The list of activation functions after the mutation
		ArrayList<Integer> mutatedFunc = new ArrayList<Integer>();
		//The mutator
		ActivationFunctionMutation afm = new ActivationFunctionMutation();
		//System.out.println("original nodes: "  + originalNodes.toString());
		
		//Copy the genotype to tg2 for mutation
		TWEANNGenotype tg2 = (TWEANNGenotype) tg1.copy();
		
		afm.mutate(tg2); // Should change exactly one function, but may change it back to itself

		//System.out.println("new nodes: " + tg1.nodes.toString());
		for(int i = 0; i < originalNodes.size(); i++) { 
			originalFunc.add(tg1.nodes.get(i).ftype);
			mutatedFunc.add(tg2.nodes.get(i).ftype);
		}
		System.out.println("original functions: " + originalFunc.toString());
		System.out.println("new functions: " + mutatedFunc.toString());
		
		//compare the number of differences in the function lists of the two genomes
		int numDifferences = 0;
		for(int i = 0; i < originalFunc.size(); i++) {
			if(!originalFunc.get(i).equals(mutatedFunc.get(i))){
				numDifferences++;
			}
		}
		
		//we want to assert that the mutated function list either differs by one function or that the two are the same
		assertTrue(numDifferences < 2);

	}

}
