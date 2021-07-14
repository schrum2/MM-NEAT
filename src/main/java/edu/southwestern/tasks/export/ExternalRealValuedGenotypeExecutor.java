package edu.southwestern.tasks.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;

/**
 * Can in theory run any NoisyLonerTask whose genotype is a real-valued genotype.
 * The real-valued vectors are generated externally, and score and behavior
 * characteristic information are printed to be read by the controlling process.
 * 
 * @author Jacob Schrum
 *
 */
public class ExternalRealValuedGenotypeExecutor {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Need to specify usual MM-NEAT parameters. However, task must work with a genotype that is a real-valued vector.");
			System.exit(1);
		}
		// The script that calls this must specify all parameters associated with the desired task
		Parameters.initializeParameterCollections(args);
		MMNEAT.loadClasses();
		LonerTask<List<Double>> task = (LonerTask) MMNEAT.task;
		System.out.println("READY"); // Tell Python program we are ready to receive;
		// Loop until Python program sends exit string
		String input = "";
		Scanner consoleFromPython = new Scanner(System.in);
		while(true) {
			input = consoleFromPython.nextLine(); // A json 1D array of doubles
			if(input.equals("exit")) break;
			double[] genotype = JsonReader.JsonToDoubleArray(input);
			// Add potential to clip ranges on solution vector values
			Genotype individual = MMNEAT.genotype instanceof BoundedRealValuedGenotype ? new BoundedRealValuedGenotype(genotype) : new RealValuedGenotype(genotype);
			Score<List<Double>> s = task.evaluate(individual);
			HashMap<String,Object> behaviorCharacteristics = s.MAPElitesBehaviorMap();
			int[] archiveDimensions = MMNEAT.getArchiveBinLabelsClass().multiDimensionalIndices(behaviorCharacteristics);
			// Print to Python
			System.out.println(Arrays.toString(archiveDimensions)); // MAP Elites archive indices
			// The output order from this is unpredictable. Capture each of these lines using a dictionary in Python
			for(Entry<String,Object> p : behaviorCharacteristics.entrySet()) { 
				Object value = p.getValue();
				String display = p.getKey() + " = " + value;
				if(value instanceof ArrayList && ((ArrayList) value).get(0) instanceof double[]) { // Required for level stats in Mario
					display = "";
					int i = 0;
					ArrayList<double[]> list = ((ArrayList<double[]>) value);
					for(double[] array : list) {
						display += "stats[" + (i++) + "] = " + Arrays.toString(array);
						if(i < list.size()) display += "\n";
					}
				}
				System.out.println(display);
			}
			System.out.println("MAP DONE"); // You can check for this string in Python to know when the HashMap is done
		}
		GANProcess.terminateGANProcess(); // Kill the Python GAN process that might be running
		consoleFromPython.close();
	}
}
