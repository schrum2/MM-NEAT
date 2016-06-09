package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
		Parameters.initializeParameterCollections(new String[] { "io:false", "recurrency:false", "perLinkMutateRate:1.0"});
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
		double[] linkes = new double[links.size()];
		for(int i = 0; i < links.size(); i++) {
			linkes[i] = links.get(i).weight;
		}
		for(int i = 0; i < MUTATION_NUM; i ++ ) {
			tg1.mutate();
		}
		double[] newLinkes = new double[tg1.links.size()];
		for(int i = 0; i < tg1.links.size(); i ++) {
			newLinkes[i] = tg1.links.get(i).weight;
		}
		assertFalse(linkes.equals(newLinkes));
	}

}
