package edu.southwestern.tasks;

import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNforDL4JGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATVisualizationUtil;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gridTorus.TorusPredPreyTask;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.Plot;
import edu.southwestern.util.datastructures.Pair;

public class CommonTaskUtil {

	public static final int NETWORK_WINDOW_OFFSET = 0;

	public static List<DrawingPanel> lastSubstrateWeightPanelsReturned = null;
	
	public static Pair<DrawingPanel, DrawingPanel> getDrawingPanels(Genotype<?> genotype){

		// This is not a TWEANNGenotype because it generates a DL4J network,
		// but it contains a TWEANNGenotype that can be used to display the appropriate network.
		if(genotype instanceof HyperNEATCPPNforDL4JGenotype) {
			genotype = ((HyperNEATCPPNforDL4JGenotype) genotype).getCPPN();
		}

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
						// Dispose of weight panels
						if(lastSubstrateWeightPanelsReturned != null) {
							for(DrawingPanel dp: lastSubstrateWeightPanelsReturned) {
								dp.dispose();
							}
						}
						HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
						lastSubstrateWeightPanelsReturned = HyperNEATVisualizationUtil.drawWeight(hngt, task, hngt.numModules());
					}
					if(!HyperNEATCPPNGenotype.constructingNetwork && CommonConstants.hyperNEAT && CommonConstants.monitorSubstrates)  {
						if(TWEANN.subsPanel != null) {
							for(DrawingPanel dp : TWEANN.subsPanel) {
								dp.dispose();
							}
						}
						TWEANN.subsPanel = null;
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
			if (CommonConstants.monitorInputs && !(MMNEAT.task instanceof TorusPredPreyTask)) {
				Offspring.fillInputs((TWEANNGenotype) genotype);
			}
		}
		return new Pair<DrawingPanel, DrawingPanel>(panel, cppnPanel);
	}

}