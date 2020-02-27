package edu.southwestern.tasks.zelda;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Bins are based on the percentage of floor space occupied by walls, the percentage of floor space occupied by water, and the number of rooms.
 * 
 * @author schrum2
 *
 */
public class ZeldaMAPElitesBinLabels implements BinLabels {

	public static final int TILE_GROUPS = 10;
	
	List<String> labels = null;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			int maxNumRooms = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks") * Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
			labels = new ArrayList<String>(TILE_GROUPS*TILE_GROUPS*(maxNumRooms+1));
			for(int i = 0; i < TILE_GROUPS; i++) { // Wall tile percent
				for(int j = 0; j < TILE_GROUPS; j++) { // Water tile percent
					for(int r = 0; r <= maxNumRooms; r++) {
						labels.add("Wall["+i+"0-"+(i+1)+"0]Water["+j+"0-"+(j+1)+"0]Rooms"+r);
					}
				}
			}
		}
		return labels;
	}

}
