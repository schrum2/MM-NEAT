package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class MegaManMAPElitesNoveltyVerticalAndConnectivityBinLabels implements BinLabels {

	public static final int TILE_GROUPS = 10;

	List<String> labels = null;

	private int maxNumSegments;
	private int noveltyBinsPerDimension; // amount of bins for the Novelty dimension
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			maxNumSegments = Parameters.parameters.integerParameter("megaManGANLevelChunks"); // Get number of level segments
			noveltyBinsPerDimension = Parameters.parameters.integerParameter("noveltyBinAmount"); // Get number of novelty bins
			
			labels = new ArrayList<String>((maxNumSegments+1)*(maxNumSegments+1)*(TILE_GROUPS)); 
			for(int i = 0; i < noveltyBinsPerDimension; i++) { // Novelty Segments
				for(int j = 0; j <= maxNumSegments; j++) { 
					for(int r = 0; r < TILE_GROUPS; r++) {
						labels.add("Novelty["+((double) i/noveltyBinsPerDimension)+"-"+((double) (i+1)/noveltyBinsPerDimension)+"]VerticalSegments["+j+"]Connectivity["+r+"0-"+(r+1)+"0]");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { // Converts multi-dimensional archive to single 1D index
		int novelty = multi[0];
		int numVertical = multi[1];
		int indexConnected = multi[2];
		int binIndex =(novelty*(maxNumSegments+1) + numVertical)*TILE_GROUPS+indexConnected;
		return binIndex;
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// MEGAMAN
		MMNEAT.main(("runNumber:0 randomSeed:0 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamanTEST log:MegaManTEST-MegaManDirect2GAN saveTo:MegaManDirect2GAN megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:true useMultipleGANsMegaMan:false cleanFrequency:-1 recurrency:false saveAllChampions:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesNoveltyVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype noveltyBinAmount:20").split(" "));
	}

}
