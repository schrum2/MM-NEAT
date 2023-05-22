
package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.fitness.ChangeCenterOfMassFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.util.MiscUtil;

/**
 * Based off of MinecraftBlockRenderExperiment.java
 * 
 * Load a single elite from MAP Elites output text file to spawn it
 * into the Minecraft world for observation and further scoring. OR, if the provided file
 * is actually a directory, then it is assumed to be full of text
 * files describing Minecraft shapes, 
 * 
 * @author Travis Rafferty
 *
 */
public class MinecraftBlockEvaluateExperiment implements Experiment{

	
	private static MinecraftFitnessFunction fitnessFunction = new ChangeCenterOfMassFitness();
	private static String dir; 
	private static HashSet<List<Block>> seen;
	
	
	@Override
	public void init() {
		dir = Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		System.out.println("Load: "+ dir);
	}

	@Override
	public void run() {
		try {
			seen = new HashSet<>();
			File file = new File(dir);
			if(file.isDirectory()) {
				int count = 0;
				String[] files = file.list();
				for(String individual : files) {
					if(individual.endsWith(".txt")) {
						System.out.println((count++) + " of " + files.length);
						//System.out.println(individual);
						try {
							List<Block> shiftedBlocks = shiftBlocks(new File(dir + File.separator + individual));
							seen.add(shiftedBlocks); // Won't add duplicates
						} catch(Exception e) {
							System.out.println("Error adding/reading "+individual);
							e.printStackTrace();
						}
					}
				}
				@SuppressWarnings("unchecked")
				List<Block>[] seenList = (List<Block>[]) new List[seen.size()]; 
				seenList = seen.toArray(seenList);
				System.out.println("Discard "+Parameters.parameters.integerParameter("minecraftBlockLoadSkip"));
				boolean clear = true;
				for(int i = Parameters.parameters.integerParameter("minecraftBlockLoadSkip"); i < seenList.length; i++) {
					List<Block> shiftedBlocks = seenList[i];
					boolean tryAgain = false;
					do {
						MinecraftCoordinates noShiftCoordinates = new MinecraftCoordinates(0,0,0);
						double fitness = fitnessFunction.fitnessScore(noShiftCoordinates, shiftedBlocks);
						System.out.println(fitness);
					} while(tryAgain);
				}
			} else {
				// Is a single text file
				ChangeCenterOfMassFitness.clearAreaAroundSpecialCorner();
				generateOneShapeFromFile(file);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param blockTextFile
	 * @throws FileNotFoundException
	 */
	public void generateOneShapeFromFile(File blockTextFile) throws FileNotFoundException {
		List<Block> shiftedBlocks = shiftBlocks(blockTextFile);
		MinecraftClient.getMinecraftClient().spawnBlocks(shiftedBlocks); // spawn blocks in minecraft world
	}

	private List<Block> shiftBlocks(File blockTextFile) throws FileNotFoundException {
		List<Block> blocks = MinecraftUtilClass.loadMAPElitesOutputFile(blockTextFile); // get block list from output file
		MinecraftCoordinates corner = MinecraftUtilClass.minCoordinates(blocks); // Original corner (or close to it)
		List<Block> shiftedBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(blocks, corner, ChangeCenterOfMassFitness.SPECIAL_CORNER);
		
//		System.out.println("Spawning " + shiftedBlocks.size() + " blocks from " + dir);
//		for(Block b: shiftedBlocks) {
//			System.out.println(b);
//		}
		return shiftedBlocks;
	}

	@Override
	public boolean shouldStop() {
		return false;
	}
}
