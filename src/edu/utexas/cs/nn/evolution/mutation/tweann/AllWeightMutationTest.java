package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.parameters.Parameters;

public class AllWeightMutationTest {

	TWEANNGenotype tg1;
	int MUTATION_NUM = 20;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "perLinkMutateRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype();
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		ArrayList<LinkGene> links = tg1.links;
		System.out.println("tg1.links: " + links);
		AllWeightMutation AWM = new AllWeightMutation();
				
		double[] linkes = new double[links.size()];
		System.out.println("  Original: " + links.size());
		int originalLength = links.size();
		for(int i = 0; i < links.size(); i++) {
			linkes[i] = links.get(i).weight;
			System.out.println("	Weight at " + i + " is " + links.get(i).weight);
		}

		for(int m = 0; m < MUTATION_NUM; m++) {
			AWM.mutate(tg1);
		}
		
		double[] newLinkes = new double[links.size()];
		System.out.println("  Mutated: " + links.size());
		for(int j = 0; j < links.size(); j++) {
			newLinkes[j] = links.get(j).weight;
			System.out.println("	Weight at " + j + " is " + links.get(j).weight);
		}
		System.out.println("original links: " + Arrays.toString(linkes));
		System.out.println("new links: " + Arrays.toString(newLinkes));
		
		assertEquals(originalLength, links.size()); // the number of links won't change when mutated
		assertFalse(linkes.equals(newLinkes));
	}

}
