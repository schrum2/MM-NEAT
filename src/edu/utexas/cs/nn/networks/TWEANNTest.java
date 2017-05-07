package edu.utexas.cs.nn.networks;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.mutation.tweann.MMR;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class TWEANNTest {
	final int MUTATIONS1 = 10;
	TWEANNGenotype tg1, tg2;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false","allowMultipleFunctions:true", "recurrency:false", "mmdRate:0.1", "task:edu.utexas.cs.nn.tasks.breve2D.Breve2DTask"});
		//CommonConstants.freezeBeforeModeMutation = true;
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(5, 2, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		tg2 = new TWEANNGenotype(5, 2, 0);
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			//tg1.addRandomPreferenceNeuron(tg1.getPhenotype().numIn); // Schrum: not sure why this was here
			tg2.mutate();
			//tg2.addRandomPreferenceNeuron(tg2.getPhenotype().numIn); // Schrum: not sure why this was here
		}
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		tg2 = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void testHyperNEATDrawingPanel() { 
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false","allowMultipleFunctions:true", "recurrency:false", "hyperNEAT:true"});
		DrawingPanel p = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "p7");
		TWEANN t1 = tg1.getPhenotype();
		t1.draw(p);
		//assertEquals(p.getFrame().getHeight(), 1000);
	//	assertEquals(p.getFrame().getWidth(), 1000);
		p.getFrame().setVisible(false);
	}

	public void uselessTest() {
		double[] inputs = RandomNumbers.randomArray(tg1.numIn);

		//tg1.freezeInfluences(tg1.nodes.get(tg1.nodes.size()-2).innovation);
		DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
		DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
		p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
		tg1.getPhenotype().draw(p1, true);
		tg2.getPhenotype().draw(p2, true);

		new MMR().mutate(tg1);
		tg1.freezePreferenceNeurons();
		System.out.println("Frozen Pref:" + Arrays.toString(tg1.getPhenotype().process(inputs)));

		DrawingPanel p3 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1 MMD");
		p3.setLocation(0, TWEANN.NETWORK_VIEW_DIM + 10);
		tg1.getPhenotype().draw(p3, true);

		new MMR().mutate(tg2);
		tg2.freezePolicyNeurons();
		System.out.println("Frozen Policy:" + Arrays.toString(tg2.getPhenotype().process(inputs)));

		DrawingPanel p4 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2 MMD");
		p4.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, TWEANN.NETWORK_VIEW_DIM + 10);
		tg2.getPhenotype().draw(p4, true);

		//TWEANNCrossover cross = new TWEANNCrossover();
		//TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}

		System.out.println("Post Mutate Frozen Pref:" + Arrays.toString(tg1.getPhenotype().process(inputs)));
		System.out.println("Post Mutate Frozen Policy:" + Arrays.toString(tg2.getPhenotype().process(inputs)));

		DrawingPanel p5 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 1");
		p5.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), 0);
		tg1.getPhenotype().draw(p5, true);

		DrawingPanel p6 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 2");
		p6.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM + 10), TWEANN.NETWORK_VIEW_DIM + 10);
		//new2.getPhenotype().draw(p6, true);
		tg2.getPhenotype().draw(p6, true);
		
		

	}
}
