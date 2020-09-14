package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * Binning scheme for MAPelites
 * @author Benjamin Capps
 *
 */
public class MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels implements BinLabels {
	public static final int TILE_GROUPS = 10;

	List<String> labels = null;

	private int maxNumSegments;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			maxNumSegments = Parameters.parameters.integerParameter("megaManGANLevelChunks");
			labels = new ArrayList<String>((maxNumSegments+1)*(maxNumSegments+1)*(TILE_GROUPS));
			for(int i = 0; i <= maxNumSegments; i++) { 
				for(int j = 0; j <= maxNumSegments; j++) { 
					for(int r = 0; r < TILE_GROUPS; r++) {
						labels.add("DistinctSegments["+i+"]VerticalSegments["+j+"]Connectivity["+r+"0-"+(r+1)+"0]");
						
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int numDistinctSegments = multi[0];
		int numVertical = multi[1];
		int indexConnected = multi[2];
		int binIndex =(numDistinctSegments*(maxNumSegments+1) + numVertical)*TILE_GROUPS+indexConnected;
		return binIndex;
	}

	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException{

		MMNEAT.main("runNumber:0 randomSeed:0 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamancppntogan log:MegaManCPPNToGAN-MAPElites saveTo:MAPElites megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManCPPNtoGANLevelTask cleanOldNetworks:false useThreeGANsMegaMan:true allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100".split(" "));
		

	}
}
