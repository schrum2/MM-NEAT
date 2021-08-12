package edu.southwestern.tasks.zelda;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.study.DungeonNovelty;

/**
 * New binning scheme for MAP-Elites
 * makes fitness based off of number of distinct and backtracked rooms.
 * @author Ben Capps
 *
 */
public class ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels extends BaseBinLabels {
	private int noveltyBinsPerDimension;
	// Don't use?
	//public static final double MAX_EXPECTED_NOVELTY = .6;
	List<String> labels = null;
	private int maxNumRooms;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			noveltyBinsPerDimension = Parameters.parameters.integerParameter("noveltyBinAmount");
			maxNumRooms = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks") * Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
			labels = new ArrayList<String>((noveltyBinsPerDimension)*(maxNumRooms+1)*(maxNumRooms+1));
			for(int i = 0; i < noveltyBinsPerDimension; i++) { 
				for(int j = 0; j <= maxNumRooms; j++) { 
					for(int r = 0; r <= maxNumRooms; r++) {
						labels.add("Novelty"+i+"BackTrackedRooms"+j+"Rooms"+r);
						
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int noveltyIndex = multi[0];
		int numBackTrackRooms = multi[1];
		int numRoomsReachable = multi[2];
		int mapElitesBinIndex = (noveltyIndex*(maxNumRooms+1) + numBackTrackRooms)*(maxNumRooms+1) + numRoomsReachable;
		return mapElitesBinIndex;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Novelty", "Backtracked Rooms", "Reachable Rooms"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {noveltyBinsPerDimension, maxNumRooms+1, maxNumRooms+1};
	}
	
	//"mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesDistinctAndBackTrackRoomsBinLabels"
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException{

		MMNEAT.main("runNumber:0 randomSeed:0 zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:100 makeZeldaLevelsPlayable:false base:zeldacppntogan log:ZeldaCPPNtoGAN-MAPElites saveTo:MAPElites zeldaGANLevelWidthChunks:5 zeldaGANLevelHeightChunks:5 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:50000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaCPPNtoGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels steadyStateIndividualsPerGeneration:100".split(" "));
		//MMNEAT.main("runNumber:0 randomSeed:0 zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false zeldaDungeonBackTrackRoomFitness:true watch:true trials:1 mu:10 makeZeldaLevelsPlayable:false base:zeldagan log:ZeldaGAN-FitnessTemp saveTo:FitnessTemp zeldaGANLevelWidthChunks:10 zeldaGANLevelHeightChunks:10 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:5000000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype".split(" "));	


	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int numBackTrackRooms = (int) keys.get("Backtracked Rooms");
		int numRoomsReachable = (int) keys.get("Reachable Rooms");

		// Make global? Assign once?
		final int NOVELTY_BINS_PER_DIMENSION = Parameters.parameters.integerParameter("noveltyBinAmount");

		double novelty = DungeonNovelty.averageDungeonNovelty((Dungeon) keys.get("Dungeon"));
		int noveltyIndex = Math.min((int)(novelty*NOVELTY_BINS_PER_DIMENSION), NOVELTY_BINS_PER_DIMENSION-1);

		return new int[] {noveltyIndex, numBackTrackRooms, numRoomsReachable};
	}
}

