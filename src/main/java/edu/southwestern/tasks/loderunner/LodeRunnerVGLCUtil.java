package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import gvgai.tools.IO;

/**
 * This class converts VGLC LodeRunner levels into JSON files 
 * @author kdste
 *
 */
public class LodeRunnerVGLCUtil {
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/Lode Runner/Processed/";
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to width in original game
	
	/**
	 * Converts all the levels in the VGLC to JSON form 
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		HashSet<List<List<Integer>>> levelSet = new HashSet<>(); //creates set to represent the level 
		for(int i = 1; i <= 150; i++) {
			String file = "Level " + i + ".txt"; //format for the LodeRunner level files 
			List<List<Integer>> levelList = convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + file); //converts to JSON 
			levelSet.add(levelList); //adds the converted list to the set for the level 
		}
		System.out.println(levelSet); //prints converted JSON files to the console 
	}

	/**
	 * Converts the VGLC level of LodeRunner to JSON form to be able to be passed into the GAN
	 * @param fileName File that holds the VGLC of a lode runner level 
	 * @return
	 */
	public static List<List<Integer>> convertLodeRunnerLevelFileVGLCtoListOfLevel(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>(LODE_RUNNER_ROWS);
		//loops through levels to get characters and convert them 
		for(int i = 0; i < LODE_RUNNER_ROWS; i++) { 
			complete.add(new ArrayList<Integer>(LODE_RUNNER_COLUMNS)); //adds a new array list to the list at index i 
			for(int j = 0; j < LODE_RUNNER_COLUMNS; j++) { //fills that array list that got added to create the row 
				int tileCode = convertLodeRunnerTileVGLCtoNumberCode(level[i].charAt(j));
				complete.get(i).add(tileCode); //adds the tile code for conversion
			}
		}
		return complete;
	}

	/**
	 * Converts tile codes to numbers for JSON conversion
	 * @param tile Character describing the tile 
	 * @return The number associated with that tile
	 */
	private static int convertLodeRunnerTileVGLCtoNumberCode(char tile) {
		switch(tile) {
		case '.': //empty
		case 'G': //gold
		case 'M': //spawn 
			return 0;	// Passable
		case 'B': //regular ground
		case 'b': //diggable ground 
			return 1;	// Impassable, i.e. solid ground 
		case 'E': //enemy 
			return 2; //TODO might remove this later 
		case '#': //ladder
		case '-': //rope
			return 3; //climbable
		default:
			throw new IllegalArgumentException("Invalid Zelda tile from VGLV: " + tile);

		}
	}
}
