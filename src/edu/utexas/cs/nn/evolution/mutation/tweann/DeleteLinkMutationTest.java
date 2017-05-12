package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.network.TWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;

public class DeleteLinkMutationTest {

	TWEANNGenotype tg1;
	TWEANNGenotype tg2;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false" });
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(5, 3, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		final int MUTATIONS1 = 30;

		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
		}
		tg2 = (TWEANNGenotype) tg1.copy();
	}

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
		tg1 = null;
		tg2 = null;
	}
	
	/**
	 * Tests ensuring proper functioning of DeleteLinkMutation by checking to make sure that 
	 * the size is updated accordingly for each link deletion, and that links can be added
	 * and subsequently deleted after emptying, etc.
	 */
	@Test
	public void realTest() { 
		tg1.meltNetwork();
		Parameters.parameters.setDouble("deleteLinkRate", 1.0);
		Parameters.parameters.setDouble("netLinkRate", 1.0);
		DeleteLinkMutation delete = new DeleteLinkMutation();
		int numLinks = tg1.links.size();
		for(int i = 1; i <= numLinks; i++){
			delete.mutate(tg1);
			assertFalse(tg1.equals(tg2));
			assertEquals(numLinks-i, tg1.links.size());	//checks that it has one fewer link every iteration	
		}
		assertEquals(tg1.links.size(), 0); //can remove links until there are none left
		delete.mutate(tg1); //nothing happens if we try to delete links that don't exist
		assertEquals(tg1.links.size(), 0);
		
		//can delete links that were added after it has been emptied
		NewLinkMutation link = new NewLinkMutation();
		link.mutate(tg1);
		assertEquals(tg1.links.size(), 1);
		delete.mutate(tg1);
		assertEquals(tg1.links.size(), 0);
	}
	
	/**
	 * Tests that all visual aspects of TWEANN network are functioning properly
	 */
	public void visualTest() {
		tg1.meltNetwork();
		DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
		DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
		p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
		tg1.getPhenotype().draw(p1, true);
		tg2.getPhenotype().draw(p2, true);
		
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
