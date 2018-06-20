package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.crossover.network.TWEANNCrossover;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.graphics.DrawingPanel;

public class MMRTest {
//TODO
	
	MMR mmr;
	TWEANNGenotype tg1, tg2;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false","recurrency:false", "mmrRate:1.0" });
		MMNEAT.loadClasses();
		mmr = new MMR();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		tg2 = (TWEANNGenotype) tg1.copy();
	}

	@After
	public void tearDown() throws Exception {
		mmr = null;
		tg1 = null;
		tg2 = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		//tg1 and tg2 are identical
		assertEquals(tg1.numIn, tg2.numIn);
		assertEquals(tg1.numOut, tg2.numOut);
		//mutation occurs and this is no longer true
		mmr.mutate(tg1);
		assertEquals(tg1.numModules, 2);
		
		assertEquals(tg1.numOut, tg2.numOut * 2);
		TWEANN tg = tg1.getPhenotype();
		System.out.println("num in and out" + tg.numInputs() + " : " + tg.numOutputs());
		double[] inputs = {.1, .2, .3, .4, .5};
		tg.process(inputs);
		//tests that no duplicates are being added, and that ouput functions properly
		for(int i = 0; i < tg.moduleOutput(0).length; i++) {
			assertTrue( tg.moduleOutput(0)[i] != tg.moduleOutput(1)[i]);
		}
	}
	/**
	 * Test to make sure MM(R) works. Should be made into proper JUnit Test.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "io:false", "recurrency:false", "mmrRate:0.1" });
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

//		DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
//		DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
//		p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
//		tg1.getPhenotype().draw(p1, true);
//		tg2.getPhenotype().draw(p2, true);

		new MMR().mutate(tg1);
//		DrawingPanel p3 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1 MMD");
//		p3.setLocation(0, TWEANN.NETWORK_VIEW_DIM + 10);
//		tg1.getPhenotype().draw(p3, true);

		new MMR().mutate(tg2);
//		DrawingPanel p4 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2 MMD");
//		p4.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, TWEANN.NETWORK_VIEW_DIM + 10);
//		tg2.getPhenotype().draw(p4, true);

		TWEANNCrossover cross = new TWEANNCrossover();
		TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);

//		DrawingPanel p5 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 1");
//		p5.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), 0);
//		tg1.getPhenotype().draw(p5, true);

//		DrawingPanel p6 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 2");
//		p6.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), TWEANN.NETWORK_VIEW_DIM + 10);
//		new2.getPhenotype().draw(p6, true);

	}
}
