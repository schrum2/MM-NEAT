package edu.utexas.cs.nn.evolution.mutation.tweann;



import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.Parameters;

public class NewLinkMutationTest {
	
	final int MUTATIONS1 = 30;//number of mutations that occur
	public static final int SIZE = 20;//number of iterations of test
	
	TWEANNGenotype tg1, tg2, tg3;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "recurrency:false","mmdRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		tg2 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
	}

	public void mutate(TWEANNGenotype tg1, TWEANNGenotype tg2) {
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}
	}
	
	@Test
	public void testLinkMutation() {
		tg2 =(TWEANNGenotype) tg1.copy();
		double weight = 0.5;
		System.out.print(tg1.nodes.size());
		tg1.spliceNode(0, tg1.biggestInnovation()+ 1, tg1.biggestInnovation()+ 1, tg1.nodes.get(0).innovation, weight, weight, tg1.links.get(tg1.links.size()-1).innovation, tg1.links.get(tg1.links.size()-1).innovation);
		
//		System.out.println(tg1.toString());
//		System.out.println(tg2.toString());
//		System.out.println("------------------------");
//		System.out.println("num Links before mutation aka tg2: " + tg2.numLinks(false) );
//		System.out.println("num Links after linkMutation aka tg3: " + tg3.numLinks(false) );
//		System.out.println("num Links after newLinkMutation called aka tg1: " + tg1.numLinks(false) );
//		System.out.println("------------------------");
//		System.out.println("before innovation # "  + " after innovation " + tg1.biggestInnovation());
//		assertFalse(TWEANNGenotype.sameStructure(tg2, tg1));
//		assertFalse(tg2.numLinks(false) == tg1.numLinks(false));
//		assertEquals(tg2.numLinks(false) + 1, tg1.numLinks(false), .0001);
	}

}
