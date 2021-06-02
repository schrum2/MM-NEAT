package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for Mario GAN using Level
 * Novelty, Decoration Frequency, and Leniency
 * 
 * @author Maxx Batterton
 *
 */
public class MarioMAPElitesNoveltyDecorAndLeniencyBinLabels implements BinLabels {

	List<String> labels = null;
	private int levelBinsPerDimension; // amount of bins for the Decor and Leniency dimensions
	private int noveltyBinsPerDimension; // amount of bins for the Novelty dimension
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			levelBinsPerDimension = Parameters.parameters.integerParameter("marioGANLevelChunks");
			noveltyBinsPerDimension = Parameters.parameters.integerParameter("noveltyBinAmount");
			
			int size = (levelBinsPerDimension+1)*levelBinsPerDimension*levelBinsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < noveltyBinsPerDimension; i++) { // Distinct Segments
				for(int j = 0; j < levelBinsPerDimension; j++) { // Negative Space
					for(int r = -(levelBinsPerDimension/2); r < levelBinsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Novelty["+((double) i/noveltyBinsPerDimension)+"-"+((double) (i+1)/noveltyBinsPerDimension)+"]DecorFrequency["+j+"0-"+(j+1)+"0]Leniency["+r+"0-"+(r+1)+"0]");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (int) ((multi[0])*levelBinsPerDimension + (multi[1])*levelBinsPerDimension + multi[2]);
		return binIndex;
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException { // TODO: remove, just left it in in case it doesn't fully work
		int runNum = 320;
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:mariolevelsnoveltydecorleniency log:MarioLevelsNoveltyDecorLeniency-ME saveTo:ME marioGANLevelChunks:10 noveltyBinAmount:"+runNum+" marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:20000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesNoveltyDecorAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000").split(" "));
	}

}