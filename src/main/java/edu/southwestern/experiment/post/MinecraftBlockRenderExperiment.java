package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

/**
 * Load a single elite from MAP Elites output text file to spawn it
 * into the Minecraft world for observation.
 * 
 * @author Alejandro Medina
 *
 */
public class MinecraftBlockRenderExperiment implements Experiment {
	
	private static String dir; 
	
	@Override
	public void init() {
		dir = Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		System.out.println("Load: "+ dir);
	}

	@Override
	public void run() {
		List<Block> blocks;
		try {
			blocks = MinecraftUtilClass.loadMAPElitesOutputFile(new File(dir)); // get block list from output file
			System.out.println("Spawning " + blocks.size() + " blocks from " + dir);
			for(Block b: blocks) {
				System.out.println(b);
			}
				
			MinecraftClient.getMinecraftClient().spawnBlocks(blocks); // spawn blocks in minecraft world
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldStop() {
		return false;
	}
}
