
package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.fitness.ChangeCenterOfMassFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.util.MiscUtil;

/**
 * Based off of MinecraftBlockRenderExperiment.java
 * 
 * Load a single elite from MAP Elites output text file to spawn it
 * into the Minecraft world for observation and further scoring. OR, if the provided file
 * is actually a directory, then it is assumed to be full of text
 * files describing Minecraft shapes, and all are loaded in sequence.
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
							List<Block> shiftedBlocks = MinecraftBlockRenderExperiment.shiftBlocks(new File(dir + File.separator + individual));
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

				for(int i = Parameters.parameters.integerParameter("minecraftBlockLoadSkip"); i < seenList.length; i++) {
					List<Block> shiftedBlocks = seenList[i];
					boolean tryAgain = false;
					do {
						System.out.println("Evaluate shape");
						double fitness = fitnessFunction.fitnessScore(ChangeCenterOfMassFitness.SPECIAL_CORNER, shiftedBlocks);
						ChangeCenterOfMassFitness.resetPreviousResults();
						System.out.println("Fitness was: " + fitness);
						
						
						System.out.println("Press enter to continue, 'b' to go back, 'r' to repeat");
						String input = MiscUtil.waitForReadStringAndEnterKeyPress();
						if(input.equals("b")) i-=2;
						else if(input.equals("r")) i--;
						
					} while(tryAgain);
				}
			} else {
				// Is a single text file
				ChangeCenterOfMassFitness.clearAreaAroundSpecialCorner();
				MinecraftBlockRenderExperiment.generateOneShapeFromFile(file);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public boolean shouldStop() {
		return false;
	}
	//used for testing purposes 
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[] {"minecraftEvaluate","minecraftBlockListTextFile:BROKEN","netio:false","spaceBetweenMinecraftShapes:10"});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
