package edu.southwestern.evolution.mapelites.generalmappings;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class TileNoveltyBinLabels implements BinLabels {
	
	List<String> labels = null;
	private int noveltyBinsPerDimension; // amount of bins for the Novelty dimension
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			noveltyBinsPerDimension = Parameters.parameters.integerParameter("noveltyBinAmount");
			
			int size = noveltyBinsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < noveltyBinsPerDimension; i++) { // Distinct Segments
				labels.add("Novelty["+((double) i/noveltyBinsPerDimension)+"-"+((double) (i+1)/noveltyBinsPerDimension)+"]");
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		return multi[0];
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// MEGAMAN
		//MMNEAT.main(("runNumber:0 randomSeed:0 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamanTEST log:MegaManTEST-MegaManDirect2GAN saveTo:MegaManDirect2GAN megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:true useMultipleGANsMegaMan:false cleanFrequency:-1 recurrency:false saveAllChampions:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.TileNoveltyBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype noveltyBinAmount:500").split(" "));
		// MARIO
		//MMNEAT.main(("runNumber:500 randomSeed:500 base:mariolevelsTEST log:MarioLevelsTEST-ME saveTo:ME marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.TileNoveltyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 noveltyBinAmount:500").split(" "));
		// MARIO CPPN2GAN
		MMNEAT.main(("runNumber:500 randomSeed:500 base:mariolevelsTEST log:MarioLevelsTEST-MECPPN2GAN saveTo:MECPPN2GAN marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNtoGANLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.TileNoveltyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 includeCosineFunction:true includeIdFunction:true noveltyBinAmount:500").split(" "));
		// ZELDA
		//MMNEAT.main(("runNumber:500 randomSeed:500 zeldaCPPN2GANSparseKeys:true zeldaALlowPuzzleDoorUglyHack:false zeldaCPPNtoGANAllowsRaft:true zeldaCPPNtoGANAllowsPuzzleDoors:true zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistinctRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:100 makeZeldaLevelsPlayable:false base:zeldadungeonsdistinctbtrooms log:ZeldaDungeonsDistinctBTRooms-Direct2GAN saveTo:Direct2GAN zeldaGANLevelWidthChunks:5 zeldaGANLevelHeightChunks:5 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:100000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.TileNoveltyBinLabels steadyStateIndividualsPerGeneration:100 noveltyBinAmount:500").split(" "));

	}
}
