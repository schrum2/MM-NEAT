package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.breve2D.Breve2DTask;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.Plot;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class CommonTaskUtil {

	public static final int NETWORK_WINDOW_OFFSET = 0;

	public static Pair<DrawingPanel, DrawingPanel> getDrawingPanels(Genotype<?> genotype){

		DrawingPanel panel = null;
		DrawingPanel cppnPanel = null;

		if (genotype instanceof TWEANNGenotype) {
			if (CommonConstants.showNetworks) {
				panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolved Network "+genotype.getId());
				panel.setLocation(NETWORK_WINDOW_OFFSET, 0);
				TWEANN network = ((TWEANNGenotype) genotype).getPhenotype();
				//System.out.println("Draw network with " + network.numInputs() + " inputs");
				network.draw(panel);
				if(genotype instanceof HyperNEATCPPNGenotype) {
					HyperNEATCPPNGenotype hngt = (HyperNEATCPPNGenotype) genotype;
					if( Parameters.parameters.booleanParameter("showCPPN")) {
						cppnPanel = new DrawingPanel(500, 500, "Evolved CPPN");
						cppnPanel.setLocation(TWEANN.NETWORK_VIEW_DIM + NETWORK_WINDOW_OFFSET, 0);
						hngt.getCPPN().draw(cppnPanel);
					}
					if(Parameters.parameters.booleanParameter("showWeights")){
						// Weight panels disposed of in HyperNEATUtil
						HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
						HyperNEATUtil.drawWeight(hngt.getSubstrateGenotype(task),task); //TODO
					}

				}
			}
			if (CommonConstants.viewModePreference && TWEANN.preferenceNeuronPanel == null && TWEANN.preferenceNeuron()) {
				TWEANN.preferenceNeuronPanel = new DrawingPanel(Plot.BROWSE_DIM, Plot.BROWSE_DIM, "Preference Neuron Activation");
				TWEANN.preferenceNeuronPanel.setLocation(Plot.BROWSE_DIM + Plot.EDGE, Plot.BROWSE_DIM + Plot.TOP);
			}
			// this does not happen for TorusPredPreyTasks because the
			// "Individual Info" panel is unnecessary, as panels for each
			// evolved agents are already shown with monitorInputs with all
			// of their sensors and information
			if (CommonConstants.monitorInputs && !(MMNEAT.task instanceof TorusPredPreyTask) && !(MMNEAT.task instanceof Breve2DTask)) {
				Offspring.fillInputs((TWEANNGenotype) genotype);
			}
		}
		return new Pair<DrawingPanel, DrawingPanel>(panel, cppnPanel);
	}

}