package edu.southwestern.tasks.mario.gan;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.idsia.mario.engine.level.Level;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.tasks.mario.level.LevelParser;

/**
 * Create Mario levels using a trained GAN as done in the Mario GAN paper.
 * 
 * @author Jacob Schrum
 *
 */
public class MarioGANUtil {

	private static GANProcess ganProcess = null;

	/**
	 * Start the GAN process running in Python if it has not started already.
	 * Otherwise, just return the reference to the process.
	 * @return Process running the Mario GAN
	 */
	private static GANProcess getGANProcess() {
		if(ganProcess == null) {
			// TODO
		}
		return ganProcess;
	}
	
	/**
	 * From MarioGAN
	 * 
	 * Map the value in R to (-1, 1)
	 * @param valueInR
	 * @return Range restricted value
	 */
	public static double mapToOne(double valueInR) {
		return ( valueInR / Math.sqrt(1+valueInR*valueInR) );
	}

	/**
	 * From MarioGAN
	 * 
	 * Perform the operation above to a whole array
	 * 
	 * @param arrayInR
	 * @return Array with values in range
	 */
	public static double[] mapArrayToOne(double[] arrayInR) {
		double[] newArray = new double[arrayInR.length];
		for(int i=0; i<newArray.length; i++) {
			double valueInR = arrayInR[i];
			newArray[i] = mapToOne(valueInR);
		}
		return newArray;
	}

	
	public static Level generateLevelFromGAN(String pathToGAN, double[] latentVector) {
		latentVector = mapArrayToOne(latentVector); // Range restrict the values
		// Generate a level from the vector
		// Brackets required since generator.py expects of list of multiple levels, though only one is being sent here
		try {
			getGANProcess().commSend("[" + Arrays.toString(latentVector) + "]");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1); // Cannot continue without the GAN process
		}
		String levelString = getGANProcess().commRecv(); // Response to command just sent
		Level[] levels = marioLevelsFromJson("[" +levelString + "]"); // Really only one level in this array
		Level level = levels[0];
		return level;
	}	
	
	/**
	 * From MarioGAN
	 * 
	 * Takes a json String representing several levels 
	 * and returns an array of all of those Mario levels.
	 * In order to convert a single level, it needs to be put into
	 * a json array by adding extra square brackets [ ] around it.
	 * @param json Json String representation of multiple Mario levels
	 * @return Array of those levels
	 */
	public static Level[] marioLevelsFromJson(String json) {
		List<List<List<Integer>>> allLevels = JsonReader.JsonToInt(json);
		Level[] result = new Level[allLevels.size()];
		int index = 0;
		for(List<List<Integer>> listRepresentation : allLevels) {
			result[index++] = LevelParser.createLevelJson(listRepresentation);
		}
		return result;
	}

}
