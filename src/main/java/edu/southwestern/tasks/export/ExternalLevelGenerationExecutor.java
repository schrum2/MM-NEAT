package edu.southwestern.tasks.export;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.DummyGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;

/**
 * This class has a main method that should be able to run any GAN-based level generator task,
 * though it doesn't need to specifically get levels from a GAN. It actually only cares about
 * getting a List<List<Integer>> level representation for a tile-based level in any game.
 * Then it provides fitness and behavior characteristic information. All interaction
 * is via the console so that an external program can launch a compiled jar file to
 * interact with the task.
 * 
 * @author Jacob Schrum
 *
 */
public class ExternalLevelGenerationExecutor {
	
	public static void main(String[] args) {
		// The script that calls this must specify all parameters associated with the desited task
		Parameters.initializeParameterCollections(args);
		MMNEAT.loadClasses();
		GANProcess.terminateGANProcess(); // Kill the Python GAN process that is spawned
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JsonLevelGenerationTask<List<Double>> task = (JsonLevelGenerationTask) MMNEAT.task;
		System.out.println("READY"); // Tell Python program we are ready to receive;
		// Loop until Python program sends exit string
		String input = "";
		Scanner consoleFromPython = new Scanner(System.in);
		while(true) {
			input = consoleFromPython.nextLine(); // A json level as a list of lists of integers
			if(input.equals("exit")) break;
			List<List<List<Integer>>> levels = JsonReader.JsonToInt("["+input+"]"); // Wrap in extra array to match type
			List<List<Integer>> level = levels.get(0); // There is only one
			// Not how random seeds were originally generated, but should still be deterministic
			double psuedoRandomSeed = level.hashCode(); //getRandomSeedForSpawnPoint(individual); //creates the seed to be passed into the Random instance 
			HashMap<String,Object> behaviorCharacteristics = new HashMap<String,Object>();
			Genotype<List<Double>> individual = new DummyGenotype<List<Double>>();
			task.evaluateOneLevel(level, psuedoRandomSeed, individual, behaviorCharacteristics);
			int[] archiveDimensions = MMNEAT.getArchiveBinLabelsClass().multiDimensionalIndices(behaviorCharacteristics);
			// Print to Python
			System.out.println(Arrays.toString(archiveDimensions)); // MAP Elites archive indices
			// The output order from this is unpredictable. Capture each of these lines using a dictionary in Python
			for(Entry<String,Object> p : behaviorCharacteristics.entrySet()) { 
				System.out.println(p.getKey() + " = " + p.getValue());
			}
			System.out.println("MAP DONE"); // You can check for this string in Python to know when the HashMap is done
		}
		consoleFromPython.close();
	}
}
