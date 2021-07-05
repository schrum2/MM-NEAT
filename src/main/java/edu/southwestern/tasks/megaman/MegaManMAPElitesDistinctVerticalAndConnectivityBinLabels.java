package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * Binning scheme for MAPelites
 * @author Benjamin Capps
 *
 */
public class MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels extends BaseBinLabels {
	public static final int TILE_GROUPS = 10;

	List<String> labels = null;

	private int maxNumSegments;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			maxNumSegments = Parameters.parameters.integerParameter("megaManGANLevelChunks"); // Get number of level segments
			labels = new ArrayList<String>((maxNumSegments+1)*(maxNumSegments+1)*(TILE_GROUPS)); 
			for(int i = 0; i <= maxNumSegments; i++) { // Create labels based on the number of level segments and tile groups
				for(int j = 0; j <= maxNumSegments; j++) { 
					for(int r = 0; r < TILE_GROUPS; r++) {
						labels.add("DistinctSegments"+i+"VerticalSegments"+j+"Connectivity"+r+"0-"+(r+1)+"0");
						
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { // Converts multi-dimensional archive to single 1D index
		int numDistinctSegments = multi[0];
		int numVertical = multi[1];
		int indexConnected = multi[2];
		int binIndex =(numDistinctSegments*(maxNumSegments+1) + numVertical)*TILE_GROUPS+indexConnected;
		return binIndex;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Distinct Segments", "Vertical Segments", "Connectivity"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {maxNumSegments+1, maxNumSegments+1, TILE_GROUPS};
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException{

		MMNEAT.main("runNumber:0 randomSeed:0 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamancppntogan log:MegaManCPPNToGAN-MAPElites saveTo:MAPElites megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManCPPNtoGANLevelTask cleanOldNetworks:false useMultipleGANsMegaMan:true allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100".split(" "));
		

	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		double precentConnected = (Double) keys.get("Connectivity");
		int numVertical = (int) ((double) keys.get("Vertical Segments"));
		int numDistinctSegments = (int) ((double) keys.get("Distinct Segments"));
		// 100% connectivity is possible, which leads to an index of 10 (out of bounds) if not adjusted using Math.min
		int indexConnected = (int) Math.min(precentConnected*TILE_GROUPS,9);
		return new int[] {numDistinctSegments, numVertical, indexConnected};
	}
}
