package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.google.common.util.concurrent.CycleDetectingLockFactory.WithExplicitOrdering;

import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

public class MinecraftBlockCompareExperiment implements Experiment {

	private static String shapeOneFileName;
	private static String shapeTwoFileName;
	
	@Override
	public void init() {
		shapeOneFileName = Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		shapeTwoFileName = Parameters.parameters.stringParameter("minecraftBlockListTextFileSecond");
		System.out.println("Load: "+ shapeOneFileName + " & "+ shapeTwoFileName);
	}

	@Override
	public void run() {
		try {
			File shapeOneTextFile = new File(shapeOneFileName);
			File shapeTwoTextFile = new File(shapeTwoFileName);
			
			// set up special corner & generate shapes
			MinecraftClient.clearAreaAroundSpecialCorner();
			generateMultipleShapesFromFiles(shapeOneTextFile, shapeTwoTextFile);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * takes two text files and generates shapes next to each other in minecraft
	 * @param shapeOneTextFile the text file containing the blocks of the first shape
	 * @param shapeTwoTextFile the text file containing the blocks of the second shape
	 * @throws FileNotFoundException
	 */
	static public void generateMultipleShapesFromFiles(File shapeOneTextFile, File shapeTwoTextFile) throws FileNotFoundException {
		
		// create augmented corner for the second shape (currently modifies x coordinate by the spaceBetweenMinecraftShapes parameter
		MinecraftCoordinates shapeTwoAugmentedCorner = MinecraftClient.POST_EVALUATION_CORNER; // augmented Evaluation Corner
		shapeTwoAugmentedCorner.t1 = (int) (MinecraftClient.POST_EVALUATION_CORNER.t1 - Parameters.parameters.doubleParameter("spaceBetweenMinecraftShapes"));
		
		// creates the final shape list by shifting passed file lists and combining into final list
		List<Block> shapeWithShiftedCoordinatesBlockList = shiftBlocks(shapeOneTextFile, MinecraftClient.POST_EVALUATION_CORNER); // sets first shape to POST_EVALUATION_CORNER
		List<Block> finalShapesBlockList = shapeWithShiftedCoordinatesBlockList;	// adds shifted blocks list to final shapes block list
		shapeWithShiftedCoordinatesBlockList = shiftBlocks(shapeTwoTextFile, shapeTwoAugmentedCorner);	// creates a list with the shifted blocks, shifted based on POST_EVALUATION_CORNER
		finalShapesBlockList.addAll(shapeWithShiftedCoordinatesBlockList);			// adds second shape block list to final shapes block list
				
				
		MinecraftClient.getMinecraftClient().spawnBlocks(finalShapesBlockList); // spawns the final shapes in minecraft at the POST_EVALUATION_CORNER
	}

	/**
	 * shifts the original shape to the new corner
	 * @param shapeTextFile text file that contains the block list of the shape
	 * @param newCorner the new corner to shift the shape two
	 * @return the list of blocks shifted to the new corner
	 * @throws FileNotFoundException
	 */
	static List<Block> shiftBlocks(File shapeTextFile, MinecraftCoordinates newCorner) throws FileNotFoundException {
		List<Block> shapeOriginalBlockList = MinecraftUtilClass.loadMAPElitesOutputFile(shapeTextFile); // get block list from output file 
		//corner in reference to shape corner?
		MinecraftCoordinates shapeOriginalCorner = MinecraftUtilClass.minCoordinates(shapeOriginalBlockList); // Original (inner/shape) corner for shape two (or close to it)
		List<Block> shiftedBlockList = MinecraftUtilClass.shiftBlocksBetweenCorners(shapeOriginalBlockList, shapeOriginalCorner, newCorner); //create list of blocks with shifted coordinates
		
//		System.out.println("Spawning " + shiftedBlocks.size() + " blocks from " + dir);
//		for(Block b: shiftedBlocks) {
//			System.out.println(b);
//		}
		return shiftedBlockList;
	}

	@Override
	public boolean shouldStop() {
		return false;
	}
}
