package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.network.TWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;

public class MMPTest {

	TWEANNGenotype tg1, tg2;
	MMP mmp1;
	final int MUTATIONS1 = 30;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "mmpRate:1.0" });
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		mutate(tg1);
	}

	public void mutate(TWEANNGenotype tg1) {
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
		}
	}

	@Test
	public void test_moduleMutation() {
		tg2 = (TWEANNGenotype) tg1.copy();
		new MMP().mutate(tg1);
		assertEquals(tg1.numModules, 2);
		TWEANN t1 = tg1.getPhenotype();
		int numNeuronsPerModule = t1.neuronsPerModule();
		assertEquals(tg1.links.get(numNeuronsPerModule - 1).sourceInnovation, tg1.links.get(tg1.links.size()-1).sourceInnovation);
		//for(int i = 0; i < )
		// tg1.

		// //test 1. Tests if moduleMutation method changes number of modules
		// tg2 = (TWEANNGenotype) tg1.copy();
		// int numLinksTryingToAdd = 1;
		// int numLinksAdded = tg1.moduleMutation(false, numLinksTryingToAdd);
		// assertTrue(tg1.numModules != tg2.numModules);
		// assertFalse(numLinksAdded == numLinksTryingToAdd);
		// new MMP().mutate(tg2);
		// assertFalse(TWEANNGenotype.sameStructure(tg2, tg1));
		//
		//
		// //test 2
		// mutate(tg1);
		// mutate(tg2);
		// TWEANNCrossover cross = new TWEANNCrossover();
		// TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
		// assertFalse(TWEANNGenotype.sameStructure(tg1, new2));

	}
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "io:false", "recurrency:false", "mmdRate:0.1" });
		MMNEAT.loadClasses();
		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);

		final int MUTATIONS1 = 30;

		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}

		DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
		DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
		p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
		tg1.getPhenotype().draw(p1, true);
		tg2.getPhenotype().draw(p2, true);

		new MMP().mutate(tg1);
		DrawingPanel p3 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1 MMD");
		p3.setLocation(0, TWEANN.NETWORK_VIEW_DIM + 10);
		tg1.getPhenotype().draw(p3, true);

		new MMP().mutate(tg2);
		DrawingPanel p4 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2 MMD");
		p4.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, TWEANN.NETWORK_VIEW_DIM + 10);
		tg2.getPhenotype().draw(p4, true);

		TWEANNCrossover cross = new TWEANNCrossover();
		TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);

		DrawingPanel p5 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 1");
		p5.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), 0);
		tg1.getPhenotype().draw(p5, true);

		DrawingPanel p6 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 2");
		p6.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), TWEANN.NETWORK_VIEW_DIM + 10);
		new2.getPhenotype().draw(p6, true);

	}
}
