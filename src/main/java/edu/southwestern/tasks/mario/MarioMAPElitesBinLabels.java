package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class MarioMAPElitesBinLabels implements BinLabels {
	
	List<String> labels = null;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			final int BINS_PER_DIMENSION = Parameters.parameters.integerParameter("marioGANLevelChunks");
			
			int size = BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < BINS_PER_DIMENSION; i++) { // Decoration
				for(int j = 0; j < BINS_PER_DIMENSION; j++) { // Negative Space
					for(int r = -(BINS_PER_DIMENSION/2); r < BINS_PER_DIMENSION/2; r++) { // Leniency allows negative range
						labels.add("Decoration["+i+"0-"+(i+1)+"0]NS["+j+"0-"+(j+1)+"0]Leniency["+r+"0-"+(r+1)+"0]");
					}
				}
			}
		}
		return labels;
	}

}