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
import edu.southwestern.util.MiscUtil;

/**
 * Load a single elite from MAP Elites output text file to spawn it
 * into the Minecraft world for observation. OR, if the provided file
 * is actually a directory, then it is assumed to be full of text
 * files describing Minecraft shapes, and all are loaded in sequence.
 * 
 * @author Alejandro Medina
 *
 */
public class MinecraftBlockRenderExperiment implements Experiment {
	
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
						try {
							System.out.println(i + " of " + seenList.length);
							if(clear) MinecraftClient.clearAreaAroundSpecialCorner();
							MinecraftClient.getMinecraftClient().spawnBlocks(shiftedBlocks);
							System.out.println("Press enter to continue, 'b' to go back, 'r' to repeat, 'k' proceed without clearing");
							String input = MiscUtil.waitForReadStringAndEnterKeyPress();
							if(input.equals("b")) i-=2;
							else if(input.equals("r")) i--;
							else if(input.equals("k")) clear=false;
							else clear = true;
						}catch(Exception e) {
							System.out.println("Error loading this: "+shiftedBlocks);
							tryAgain = MiscUtil.yesTo("Try again?");
						}
					} while(tryAgain);
				}
			} else {
				// Is a single text file
				MinecraftClient.clearAreaAroundSpecialCorner();
				generateOneShapeFromFile(file);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * generates a shape using blockTextFile
	 * @param blockTextFile text file that holds a shape
	 * @throws FileNotFoundException
	 */
	static public void generateOneShapeFromFile(File blockTextFile) throws FileNotFoundException {
		List<Block> shiftedBlocks = shiftBlocks(blockTextFile);
		MinecraftClient.getMinecraftClient().spawnBlocks(shiftedBlocks); // spawn blocks in minecraft world
	}
	/**
	 * Shifts blocks to special corner 
	 * @param blockTextFile text file that holds a shape
	 * @return list of the same blocks at the special corner
	 * @throws FileNotFoundException
	 */
	static List<Block> shiftBlocks(File blockTextFile) throws FileNotFoundException {
		List<Block> blocks = MinecraftUtilClass.loadMAPElitesOutputFile(blockTextFile); // get block list from output file
		MinecraftCoordinates corner = MinecraftUtilClass.minCoordinates(blocks); // Original corner (or close to it)
		List<Block> shiftedBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(blocks, corner, MinecraftClient.POST_EVALUATION_CORNER);
		
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
