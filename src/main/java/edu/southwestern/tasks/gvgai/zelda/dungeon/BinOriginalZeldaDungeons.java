package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.zelda.ZeldaDungeonTask;

public class BinOriginalZeldaDungeons {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException, Exception {
		
		// Load parameters here first, since some needs to be set for the loading to work
		Parameters.initializeParameterCollections(new String[] {
				"randomSeed:0", "rougeEnemyHealth:2",
				"io:false","netio:false","watch:false",
				"zeldaDungeonBackTrackRoomFitness:true","zeldaDungeonDistinctRoomFitness:true",
				"zeldaDungeonDistanceFitness:false","zeldaDungeonFewRoomFitness:false",
				"zeldaDungeonTraversedRoomFitness:true","zeldaPercentDungeonTraversedRoomFitness:true",
				"zeldaDungeonRandomFitness:false"});
		
		String[] binningSchemes = new String[] {
				"ZeldaMAPElitesWallWaterRoomsBinLabels", // WWR in Journal paper, also in original CPPN2GAN paper
				//"ZeldaMAPElitesNoveltyAndBackTrackRoomBinLabels", // Never used in publication
				"ZeldaMAPElitesDistinctAndBackTrackRoomsBinLabels" // Distinct BTR in ToG article
		};
		// Load all dungeons
		String[] dungeonList = new String[] {
				"tloz1_1_flip",
				//"tloz1_2_flip",
				"tloz2_1_flip",
				//"tloz2_2_flip",
				"tloz3_1_flip",
				//"tloz3_2_flip",
				"tloz4_1_flip",
				//"tloz4_2_flip",
				"tloz5_1_flip",
				//"tloz5_2_flip",
				"tloz6_1_flip",
				//"tloz6_2_flip",
				"tloz7_1_flip",
				//"tloz7_2_flip",
				"tloz8_1_flip",
				//"tloz8_2_flip",
				"tloz9_1_flip" //,
				//"tloz9_2_flip"
		};
		
		List<Dungeon> dungeonCollection = new ArrayList<>();
		for(String name : dungeonList) {
			System.out.println("Loading "+name);
			dungeonCollection.add(LoadOriginalDungeon.loadOriginalDungeon(name, false));
		}

		// Apply each binning scheme
		for(String binningSchemeClassName : binningSchemes) {
			String binningSchemeName = binningSchemeClassName.substring(14, binningSchemeClassName.length() - 9);
			Parameters.initializeParameterCollections(new String[] {
					"base:dagstuhlzelda","log:DagstuhlZelda-"+binningSchemeName,"saveTo:"+binningSchemeName,
					"task:edu.southwestern.tasks.zelda.FakeZeldaDungeonTask",
					"mu:0","io:true","netio:true","watch:false",
					"steadyStateIndividualsPerGeneration:1",
					"zeldaDungeonBackTrackRoomFitness:true","zeldaDungeonDistinctRoomFitness:true",
					"zeldaDungeonDistanceFitness:false","zeldaDungeonFewRoomFitness:false",
					"zeldaDungeonTraversedRoomFitness:true","zeldaPercentDungeonTraversedRoomFitness:true",
					"noveltyBinAmount:10",
					"zeldaGANLevelWidthChunks:8","zeldaGANLevelHeightChunks:8", // Largest Zelda levels is 8 by 8
					"zeldaDungeonRandomFitness:false",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"mapElitesBinLabels:edu.southwestern.tasks.zelda."+binningSchemeClassName});
			MMNEAT.loadClasses();

			@SuppressWarnings("rawtypes")
			MAPElites me = (MAPElites) MMNEAT.ea;

			System.out.println(binningSchemeName);
			for(Dungeon dungeon : dungeonCollection) {
				System.out.println("Evaluate dungeon");
				Score<Network> score = ZeldaDungeonTask.evaluateDungeon(MMNEAT.genotype, dungeon);
				HashMap<String,Object> map = score.MAPElitesBehaviorMap();
				@SuppressWarnings("rawtypes")
				Archive archive = MMNEAT.getArchive();
				System.out.println(Arrays.toString(archive.getBinMapping().multiDimensionalIndices(map)));
				me.fileUpdates(archive.add(score));
			}   
			me.finalCleanup();
			MMNEAT.clearClasses();
		}
	}
}
