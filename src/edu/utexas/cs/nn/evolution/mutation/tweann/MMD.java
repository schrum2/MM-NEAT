/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.network.TWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class MMD extends ModuleMutation {

    public MMD() {
        super("mmdRate");
    }

    @Override
    public void addMode(TWEANNGenotype genotype) {
        genotype.modeDuplication();
    }

    public static void main(String[] args) {
        Parameters.initializeParameterCollections(new String[]{"io:false","recurrency:false"});
        MMNEAT.loadClasses();
        TWEANNGenotype tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
        MMNEAT.genotype = tg1.copy();
        EvolutionaryHistory.initArchetype(0);
        TWEANNGenotype tg2 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);

        final int MUTATIONS1 = 30;

        for (int i = 0; i < MUTATIONS1; i++) {
            tg1.mutate();
            tg2.mutate();
        }
        
        tg1.freezePolicyNeurons();
        tg2.freezePreferenceNeurons();

        DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1");
        DrawingPanel p2 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2");
        p2.setLocation(TWEANN.NETWORK_VIEW_DIM + 10, 0);
        tg1.getPhenotype().draw(p1, true);
        tg2.getPhenotype().draw(p2, true);

        tg1.modeDuplication();
        DrawingPanel p3 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 1 MMD");
        p3.setLocation(0, TWEANN.NETWORK_VIEW_DIM+10);
        tg1.getPhenotype().draw(p3, true);
        
        System.out.println("Should all be true");
        TWEANN t1 = tg1.getPhenotype();
        // This loop confirms that the new mode is a duplicate of the previous
        for(int i = 0; i < 20; i++) {
            double[] in = RandomNumbers.randomArray(t1.numInputs());
            t1.process(in);
            System.out.println(Arrays.equals(t1.modeOutput(0),t1.modeOutput(1)));
        }
        
        tg2.modeDuplication();
        DrawingPanel p4 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Net 2 MMD");
        p4.setLocation(TWEANN.NETWORK_VIEW_DIM+10, TWEANN.NETWORK_VIEW_DIM+10);
        tg2.getPhenotype().draw(p4, true);
    
        TWEANNCrossover cross = new TWEANNCrossover();
        TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
        
        DrawingPanel p5 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 1");
        p5.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM+10), 0);
        tg1.getPhenotype().draw(p5, true);
        
        DrawingPanel p6 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Cross Result 2");
        p6.setLocation(2 * (TWEANN.NETWORK_VIEW_DIM+10), TWEANN.NETWORK_VIEW_DIM+10);
        new2.getPhenotype().draw(p6, true);
        
        System.out.println("Should all be false");
        TWEANN t2 = new2.getPhenotype();
        // Crossed modes should not be the same
        for(int i = 0; i < 20; i++) {
            double[] in = RandomNumbers.randomArray(t2.numInputs());
            t2.process(in);
            System.out.println(Arrays.equals(t2.modeOutput(0),t2.modeOutput(1)));
        }

        System.out.println("Should all be false");
        TWEANN t3 = tg1.getPhenotype();
        // Crossed modes should not be the same
        for(int i = 0; i < 20; i++) {
            double[] in = RandomNumbers.randomArray(t3.numInputs());
            t3.process(in);
            System.out.println(Arrays.equals(t3.modeOutput(0),t3.modeOutput(1)));
        }
    }
}
