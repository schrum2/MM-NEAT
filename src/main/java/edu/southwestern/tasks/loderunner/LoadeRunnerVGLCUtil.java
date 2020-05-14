package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import gvgai.tools.IO;

public class LoadeRunnerVGLCUtil {
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/LodeRunner/Processed/";
	public static final int LODE_RUNNER_COLUMNS = 31; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 21; // Equivalent to width in original game
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		HashSet<List<List<Integer>>> levelSet = new HashSet<>();
		
	}

	public static List<List<Integer>> convertLodeRunnerLevelFileVGLCtoListOfLevel(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>();
		//loops through the rows level 
		for(int i = 0; i < LODE_RUNNER_ROWS; i++) { 
			for(int j = 0; j < LODE_RUNNER_COLUMNS; j++) {
				int code = convertLodeRunnerTileVGLCtoNumberCode(level[i].charAt(j)) ;
				complete.get(i).add(code);
			}
		}
		return complete;
	}

	private static int convertLodeRunnerTileVGLCtoNumberCode(char tile) {
		switch(tile) {
		case '.': //empty
		case '#': //ladder
		case '-': //rope
		case 'G': //gold
		case 'M': //spawn 
			return 0;	// Passable
		case 'B': //regular ground
		case 'b': //diggable ground 
			return 1;	// Impassable, i.e. solid ground 
		case 'E': //enemy 
			return 2; //TODO might remove this later 
		default:
			throw new IllegalArgumentException("Invalid Zelda tile from VGLV: " + tile);

		}
	}
}
