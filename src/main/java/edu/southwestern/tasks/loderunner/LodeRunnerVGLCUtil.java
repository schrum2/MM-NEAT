package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import gvgai.tools.IO;

public class LodeRunnerVGLCUtil {
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/LodeRunner/Processed/";
	public static final int LODE_RUNNER_COLUMNS = 31; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 21; // Equivalent to width in original game
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		HashSet<List<List<Integer>>> levelSet = new HashSet<>();
		//for(int i = 1; i <= 150; i++) {
			//String file = "Level " + i + ".txt";
			//List<List<Integer>> levelList = convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH+file);
			List<List<Integer>> levelList = convertLodeRunnerLevelFileVGLCtoListOfLevel("C:/Users/kdste/Documents/GitHub/MM-NEAT/data/VGLC/Lode Runner/Processed/Level 1.txt");
			levelSet.addAll((Collection<? extends List<List<Integer>>>) levelList);
		//}
		System.out.println(levelSet);
	}

	public static List<List<Integer>> convertLodeRunnerLevelFileVGLCtoListOfLevel(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>();
		//loops through levels to get characters and convert them 
		for(int i = 0; i < LODE_RUNNER_ROWS; i++) { 
			List<Integer> rowList = new ArrayList<>(LODE_RUNNER_COLUMNS);
			complete.add(rowList);
			for(int j = 0; j < LODE_RUNNER_COLUMNS; j++) {
				int code = convertLodeRunnerTileVGLCtoNumberCode(level[i].charAt(j));
				System.out.println("i = " + i);
				System.out.println("j = " + j);
				System.out.println("code = " + code);
				rowList.add(code);
				System.out.println(rowList);
			}
			//complete.get(i).addAll(rowList);
		}
		return complete;
	}

	private static int convertLodeRunnerTileVGLCtoNumberCode(char tile) {
		switch(tile) {
		case '.': //empty
		case 'G': //gold
		case 'M': //spawn 
		case '#': //ladder
		case '-': //rope
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
