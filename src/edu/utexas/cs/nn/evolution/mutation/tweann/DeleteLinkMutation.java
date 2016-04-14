package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.network.TWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 * Mutation that deletes a single link from a TWEANN
 *
 * @author Jacob Schrum
 */
public class DeleteLinkMutation extends TWEANNMutation {

    public DeleteLinkMutation() {
        super("deleteLinkRate");
    }

    public void mutate(Genotype<TWEANN> genotype) {
        ((TWEANNGenotype) genotype).deleteLinkMutation();
    }
    
    /**
     * Runs a test procedure to verify correct functioning of the mutation.
     * This should be made into a proper JUnit Test
     * @param args
     */
    public static void main(String[] args) {
        Parameters.initializeParameterCollections(new String[]{"io:false","netio:false","recurrency:false","mmdRate:0.1","deleteLinkRate:0.3"});
        //MONE.loadClasses();
        TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
        MMNEAT.genotype = tg1.copy();
        EvolutionaryHistory.initArchetype(0);
        TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);

        final int MUTATIONS1 = 30;

        for (int i = 0; i < MUTATIONS1; i++) {
            tg1.mutate();
            //tg2.mutate();
        }
        
        DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
        DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
        p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
        tg1.getPhenotype().draw(p1, true);
        tg2.getPhenotype().draw(p2, true);
    
        TWEANNCrossover cross = new TWEANNCrossover();
        TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
        
        DrawingPanel p5 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 1");
        p5.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM+10), 0);
        tg1.getPhenotype().draw(p5, true);
        
        DrawingPanel p6 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 2");
        p6.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM+10), TWEANN.NETWORK_VIEW_DIM+10);
        new2.getPhenotype().draw(p6, true);
        
    }    
}
