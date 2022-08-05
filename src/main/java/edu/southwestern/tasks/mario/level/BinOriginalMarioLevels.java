package edu.southwestern.tasks.mario.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.mario.MarioLevelTask;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * An idea out of Dagstuhl. Take the original Mario levels and
 * place them into MAP Elites archives to see where they land.
 * 
 * @author Jacob Schrum
 *
 */
public class BinOriginalMarioLevels {
	/**
	 * Loop through the original Mario levels and put them in each possible
	 * MAP Elites archive.
	 * 
	 * @param args
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws FileNotFoundException, Exception {
		MarioLevelUtil.removeMarioLevelBuffer = false;
		String[] binningSchemes = new String[] {
				"MarioMAPElitesPercentDecorateCoverageAndLeniencyBinLabels" //, // New
				//"MarioMAPElitesDecorAndLeniencyBinLabels", // Never used in publication
				//"MarioMAPElitesDecorNSAndLeniencyBinLabels", // Original CPPN2GAN paper
				//"MarioMAPElitesDistinctChunksNSAndDecorationBinLabels", // CPPN2GAN ToG article
				//"MarioMAPElitesDistinctChunksNSAndLeniencyBinLabels" // Never used in publication
		};
		// Load all levels
		List<List<List<Integer>>> levelCollection = new ArrayList<>();
		String dir = "data/VGLC/SuperMarioBrosNewEncoding/overworld";
		//String dir = "data/VGLC/SuperMarioBros";
		File dirFile = new File(dir);
		for(File levelFile : dirFile.listFiles()) {
			if(levelFile.getName().endsWith(".txt")) {
				System.out.println(levelFile);
				//int[][] grid = OldLevelParser.readLevel(new Scanner(levelFile));
				int[][] grid = LevelParser.readLevel(new Scanner(levelFile));
				//System.out.println(Arrays.deepToString(grid));
				List<List<Integer>> levelAsLists = new ArrayList<>();
				for(int[] row: grid) {
					int numSegments = row.length / MarioLevelTask.SEGMENT_WIDTH_IN_BLOCKS; 
					int choppedLength = numSegments * MarioLevelTask.SEGMENT_WIDTH_IN_BLOCKS;
					System.out.println("Chop "+row.length+" down to "+choppedLength);
					int[] chopped = new int[choppedLength];
					System.arraycopy(row, 0, chopped, 0, choppedLength);
					List<Integer> rowList = ArrayUtil.intListFromArray(chopped);
					System.out.println(rowList);
					levelAsLists.add(rowList);
				}
				
//				for(List<Integer> row : levelAsLists) {
//					System.out.println(row);
//				}
//				MiscUtil.waitForReadStringAndEnterKeyPress();
				
				levelCollection.add(levelAsLists);
			}
		}

		// Apply each binning scheme
		for(String binningSchemeClassName : binningSchemes) {
			String binningSchemeName = binningSchemeClassName.substring(14, binningSchemeClassName.length() - 9);
			int marioGANLevelChunks = 10; // This is what we used with MarioGAN
			Parameters.initializeParameterCollections(new String[] {
					// Experimenting with restricted ranges
					//"marioMinDecorationIndex:2", "marioMaxDecorationIndex:4",
					//"marioMinLeniencyIndex:4", "marioMaxLeniencyIndex:5",
					//"marioMinSpaceCoverageIndex:3","marioMaxSpaceCoverageIndex:7",
					"base:dagstuhlmario","log:DagstuhlMario-"+binningSchemeName,"saveTo:"+binningSchemeName,
					"task:edu.southwestern.tasks.mario.FakeMarioLevelTask",
					"marioGANLevelChunks:"+marioGANLevelChunks, "mu:0",
					"io:true","netio:true","watch:false",
					"marioSimpleAStarDistance:true",
					"marioGANUsesOriginalEncoding:false",
					"steadyStateIndividualsPerGeneration:1",
					"marioProgressPlusJumpsFitness:false",
					"marioProgressPlusTimeFitness:false",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"mapElitesBinLabels:edu.southwestern.tasks.mario.binningschemes."+binningSchemeClassName});
			MMNEAT.loadClasses();

			MarioLevelTask<ArrayList<Double>> marioLevelTask = (MarioLevelTask<ArrayList<Double>>) MMNEAT.task;
			MAPElites me = (MAPElites) MMNEAT.ea;

			System.out.println(binningSchemeName);
			for(List<List<Integer>> levelAsLists : levelCollection) {
				HashMap<String,Object> map = new HashMap<>();
				System.out.println("Evaluate level");
				marioLevelTask.evaluateOneLevel(levelAsLists, 0, MMNEAT.genotype, map);
				Archive archive = MMNEAT.getArchive();
				System.out.println(Arrays.toString(archive.getBinMapping().multiDimensionalIndices(map)));
				//System.out.println(map);
				//System.out.println(Arrays.toString( (((ArrayList<double[]>) map.get("Level Stats"))).get(0)));
				// No genotype or scores
				Score score = new Score(map,MMNEAT.genotype,new double[0]);
				me.fileUpdates(archive.add(score));
			}   
			me.finalCleanup();
			MMNEAT.clearClasses();
		}
	}

}
