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
 * into the Minecraft world for observation.
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
					System.out.println((count++) + " of " + files.length);
					//System.out.println(individual);
					List<Block> shiftedBlocks = shiftBlocks(new File(dir + File.separator + individual));
					seen.add(shiftedBlocks); // Won't add duplicates
				}
				count = 0;
				for(List<Block> shiftedBlocks: seen) {
					System.out.println((count++) + " of " + seen.size());
					MinecraftClient.getMinecraftClient().spawnBlocks(shiftedBlocks);
					System.out.println("Press enter to continue");
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
			} else {
				// Is a single text file
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
		ChangeCenterOfMassFitness.clearAreaAroundSpecialCorner();
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
