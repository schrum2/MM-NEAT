
package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftShapeTask;
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

	
	private static ArrayList<MinecraftFitnessFunction> fitnessFunctions;
	private static String dir; 
	private static HashSet<List<Block>> seen;
	
	
	@Override
	public void init() {
		dir = Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		fitnessFunctions = MinecraftShapeTask.defineFitnessFromParameters();
		System.out.println("Load: "+ dir);
	}

	@Override
	public void run() {
//		try {
			seen = new HashSet<>();
			HashMap<List<Block>, String> fileNames = new HashMap<>();
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
							fileNames.put(shiftedBlocks, individual);
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
						double[] fitnessScores = MinecraftShapeTask.calculateFitnessScores(MinecraftClient.POST_EVALUATION_SHAPE_CORNER, fitnessFunctions, shiftedBlocks).t1;

						for(int j = 0; j < fitnessFunctions.size(); j++) {
							System.out.print(fitnessFunctions.get(j).getClass().getSimpleName() + ": ");
							System.out.println(fitnessScores[j]);
						}
						System.out.println("Currently watching: "+fileNames.get(seenList[i]));
						System.out.println("Press enter to continue, 'b' to go back, 'r' to repeat");
						String input = MiscUtil.waitForReadStringAndEnterKeyPress();
						if(input.equals("b")) i-=2;
						else if(input.equals("r")) i--;
						
					} while(tryAgain);
				}
			} else {
				throw new UnsupportedOperationException("Does not actually work with single files. Load a directory instead");
				// Is a single text file
//				MinecraftClient.clearAreaAroundPostEvaluationCorner();
//				MinecraftBlockRenderExperiment.generateOneShapeFromFile(file);
			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}

	
	@Override
	public boolean shouldStop() {
		return false;
	}
	//used for testing purposes 
	public static void main(String[] args) {
		try {
			//MMNEAT.main("minecraftEvaluate minecraftBlockListTextFile:minecraftaccumulate\\MEObserverVectorPistonOrientation0\\flyingMachines netio:false spaceBetweenMinecraftShapes:10 minecraftChangeCenterOfMassFitness:true minecraftAccumulateChangeInCenterOfMass:true watch:true minecraftClearSleepTimer:400".split(" "));
			//MMNEAT.main("minecraftEvaluate minecraftBlockListTextFile:CHEATER netio:false spaceBetweenMinecraftShapes:10 minecraftChangeCenterOfMassFitness:true minecraftAccumulateChangeInCenterOfMass:true watch:true minecraftClearSleepTimer:400".split(" "));
			MMNEAT.main("minecraftEvaluate minecraftBlockListTextFile:WINNER netio:false spaceBetweenMinecraftShapes:10 minecraftChangeCenterOfMassFitness:true minecraftAccumulateChangeInCenterOfMass:true watch:true minecraftClearSleepTimer:400".split(" "));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
