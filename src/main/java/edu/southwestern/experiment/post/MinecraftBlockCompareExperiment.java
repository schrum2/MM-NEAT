package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.google.common.util.concurrent.CycleDetectingLockFactory.WithExplicitOrdering;

import edu.southwestern.MMNEAT.MMNEAT;
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
			
			//System.out.println("Clear space for both shapes");
			// set up special corner & generate shapes
			MinecraftClient.clearAreaAroundPostEvaluationCorner(); // TODO: May need to call twice around each specific corner
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
		//System.out.println("inside generate multiple shapes from files");

		// create augmented corner for the second shape (currently modifies x coordinate by the spaceBetweenMinecraftShapes parameter
		MinecraftCoordinates shapeTwoAugmentedEvaluationCorner = new MinecraftCoordinates(MinecraftClient.POST_EVALUATION_CORNER); // augmented Evaluation Corner

		// teleport to -506 100 520 to see shapes 
		shapeTwoAugmentedEvaluationCorner.t1 = MinecraftClient.POST_EVALUATION_CORNER.t1 - Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		//System.out.println("created evaluation corners. Augmented:"+ shapeTwoAugmentedEvaluationCorner + "original:"+ MinecraftClient.POST_EVALUATION_CORNER);
		
		// creates the final shape list by shifting passed file lists and combining into final list
		List<Block> shapeWithShiftedCoordinatesBlockList = shiftBlocks(shapeOneTextFile, MinecraftClient.POST_EVALUATION_CORNER); // sets first shape to POST_EVALUATION_CORNER
		List<Block> finalShapesBlockList = shapeWithShiftedCoordinatesBlockList;	// adds shifted blocks list to final shapes block list
		//TODO: shapeWithShiftedCoordinatesBlockList = shiftBlocks(shapeTwoTextFile, shapeTwoAugmentedEvaluationCorner);	// creates a list with the shifted blocks, shifted based on POST_EVALUATION_CORNER
		List<Block> shiftedShapeTwoBlocks = shiftBlocks(shapeTwoTextFile, shapeTwoAugmentedEvaluationCorner);	// creates a list with the shifted blocks, shifted based on POST_EVALUATION_CORNER		
		finalShapesBlockList.addAll(shiftedShapeTwoBlocks);			// adds second shape block list to final shapes block list
		//finalShapesBlockList.addAll(shapeTwoAugmentedCorner);
		//System.out.println("added shape 2 to final shape list");
//		System.out.println("Spawning " + finalShapesBlockList.size());
//		for(Block b: finalShapesBlockList) {
//			System.out.println(b);
//		}
		MinecraftClient.getMinecraftClient().spawnBlocks(finalShapesBlockList); // spawns the final shapes in minecraft at the POST_EVALUATION_CORNER

	}

	/**
	 * shifts the original shape to the new corner
	 * @param shapeTextFile text file that contains the block list of the shape
	 * @param newEvaluationCorner the new corner to shift the shape two
	 * @return the list of blocks shifted to the new corner
	 * @throws FileNotFoundException
	 */
	static List<Block> shiftBlocks(File shapeTextFile, MinecraftCoordinates newEvaluationCorner)/**passed evaluation corner**/ throws FileNotFoundException {
		List<Block> shapeOriginalBlockList = MinecraftUtilClass.loadMAPElitesOutputFile(shapeTextFile); // get block list from output file 
		
		//System.out.println("newCorner:"+ newEvaluationCorner);

		//corner in reference to shape corner?
		MinecraftCoordinates shapeOriginalShapeCorner = MinecraftUtilClass.minCoordinates(shapeOriginalBlockList); // Original (inner/shape) corner for shape two (or close to it)
		//System.out.println("originalCorner:"+ shapeOriginalShapeCorner);

		List<Block> shiftedBlockList = MinecraftUtilClass.shiftBlocksBetweenCorners(shapeOriginalBlockList, shapeOriginalShapeCorner, newEvaluationCorner); //create list of blocks with shifted coordinates
		
		System.out.println("Spawning " + shiftedBlockList.size());
		for(Block b: shiftedBlockList) {
			System.out.println(b);
		}
		return shiftedBlockList;
	}

	@Override
	public boolean shouldStop() {
		return false;
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main("minecraftRaceFlyingMachines minecraftBlockListTextFile:testingForRacing\\NS3UD1EW3_98.50000_45529.txt minecraftBlockListTextFileSecond:testingForRacing\\NS4UD0EW2_98.40000_45698.txt".split(" "));
			//MMNEAT.main("minecraftRaceFlyingMachines minecraftBlockListTextFile:testingForRacing\\NS3UD1EW3_98.50000_45529.txt minecraftBlockListTextFileSecond:testingForRacing\\NS3UD1EW3_98.50000_45529.txt".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
