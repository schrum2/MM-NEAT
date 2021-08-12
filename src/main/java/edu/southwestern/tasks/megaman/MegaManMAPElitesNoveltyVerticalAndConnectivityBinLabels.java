package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.evolution.mapelites.generalmappings.TileNoveltyBinLabels;
import edu.southwestern.parameters.Parameters;

public class MegaManMAPElitesNoveltyVerticalAndConnectivityBinLabels extends BaseBinLabels {

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
						labels.add("Novelty"+((double) i/noveltyBinsPerDimension)+"-"+((double) (i+1)/noveltyBinsPerDimension)+"VerticalSegments"+j+"Connectivity"+r+"0-"+(r+1)+"0");
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

	@Override
	public String[] dimensions() {
		return new String[] {"Novelty", "Vertical Segments", "Connectivity"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {noveltyBinsPerDimension, maxNumSegments+1, TILE_GROUPS};
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// MEGAMAN
		MMNEAT.main(("runNumber:0 randomSeed:0 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamanTEST log:MegaManTEST-MegaManDirect2GAN saveTo:MegaManDirect2GAN megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:true useMultipleGANsMegaMan:false cleanFrequency:-1 recurrency:false saveAllChampions:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesNoveltyVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype noveltyBinAmount:20").split(" "));
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		double precentConnected = (Double) keys.get("Connectivity");
		int numVertical = (int) keys.get("Vertical Segments");
		// 100% connectivity is possible, which leads to an index of 10 (out of bounds) if not adjusted using Math.min
		int indexConnected = (int) Math.min(precentConnected*TILE_GROUPS,9);

		// Make global? Assign once?
		final int NOVELTY_BINS_PER_DIMENSION = Parameters.parameters.integerParameter("noveltyBinAmount");
		@SuppressWarnings("unchecked")
		List<List<Integer>> level = (List<List<Integer>>) keys.get("Level");
		double novelty = TileNoveltyBinLabels.levelNovelty(level);
		int noveltyIndex = Math.min((int)(novelty*NOVELTY_BINS_PER_DIMENSION), NOVELTY_BINS_PER_DIMENSION-1);

		return new int[] {noveltyIndex, numVertical, indexConnected};
	}
}
