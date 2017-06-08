package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class CommonTaskUtil {
	
	public static Pair<DrawingPanel, DrawingPanel> getDrawingPanels(Genotype gene){
		
		DrawingPanel panel = null;
		DrawingPanel cppnPanel = null;
		
		
		if(CommonConstants.watch){
			if(gene instanceof TWEANNGenotype){ // Genotype must be a TWEANNGenotype to draw on either Drawing Panel
	
				panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolved Network " + gene.getId());
				
				if(gene instanceof HyperNEATCPPNGenotype){ // Genotype must be a HyperNEATCPPNGenotype to draw the cppnPanel
					cppnPanel = new DrawingPanel(500, 500, "Evolved CPPN");
				}
			}
		}
		return new Pair<DrawingPanel, DrawingPanel>(panel, cppnPanel);
	}
	
}