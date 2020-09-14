package edu.southwestern.tasks.zelda;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * New binning scheme for MAP-Elites
 * makes fitness based off of number of distinct and backtracked rooms.
 * @author Ben Capps
 *
 */
public class ZeldaMAPElitesDistinctAndBackTrackRoomsBinLabels implements BinLabels {
	
	List<String> labels = null;
	private int maxNumRooms;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			maxNumRooms = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks") * Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
			labels = new ArrayList<String>((maxNumRooms+1)*(maxNumRooms+1)*(maxNumRooms+1));
			for(int i = 0; i <= maxNumRooms; i++) { 
				for(int j = 0; j <= maxNumRooms; j++) { 
					for(int r = 0; r <= maxNumRooms; r++) {
						labels.add("DistinctRooms["+i+"]BackTrackedRooms["+j+"]Rooms["+r+"]");
						
					}
				}
			}
		}
		return labels;
	}
	
	@Override
	public int oneDimensionalIndex(int[] multi) {
		int numDistinctRooms = multi[0];
		int numBackTrackRooms = multi[1];
		int numRoomsReachable = multi[2];
		int mapElitesBinIndex = (numDistinctRooms*(maxNumRooms+1) + numBackTrackRooms)*(maxNumRooms+1) + numRoomsReachable;
		return mapElitesBinIndex;
	}
	
	//"mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesDistinctAndBackTrackRoomsBinLabels"
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException{
		MMNEAT.main("runNumber:0 randomSeed:0 zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:10 makeZeldaLevelsPlayable:false base:zeldacppntogan log:ZeldaCPPNtoGAN-MAPElites saveTo:MAPElites zeldaGANLevelWidthChunks:3 zeldaGANLevelHeightChunks:3 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:50000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaCPPNtoGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesDistinctAndBackTrackRoomsBinLabels steadyStateIndividualsPerGeneration:100".split(" "));
	}	
}
