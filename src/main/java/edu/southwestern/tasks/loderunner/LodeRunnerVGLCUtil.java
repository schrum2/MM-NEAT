package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import gvgai.tools.IO;
import icecreamyou.LodeRunner.LodeRunner;

/**
 * This class converts VGLC Lode Runner levels into JSON files 
 * @author kdste
 *
 */
public class LodeRunnerVGLCUtil {
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/Lode Runner/Processed/";
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room width from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to height in original game
	public static final int ICE_CREAM_YOU_WIDTH = 960;
	public static final int ICE_CREAM_YOU_HEIGHT = 880;
	
	/**
	 * Converts all the levels in the VGLC to JSON form 
	 * @param args
	 */
	public static void main(String[] args) {
//		HashSet<List<List<Integer>>> levelSet = new HashSet<>(); //creates set to represent the level 
//		for(int i = 1; i <= 100; i++) {
//			String file = "Level " + i + ".txt"; //format for the LodeRunner level files 
//			List<List<Integer>> levelList = convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + file); //converts to JSON 
//			levelSet.add(levelList); //adds the converted list to the set for the level 
//		}
//		System.out.println(levelSet); //prints converted JSON files to the console 
		//String level = convertLodeRunnerVGLCtoIceCreamYou(LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
		//List<List<Integer>> level = convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
		//String levelIce = convertLodeRunnerJSONtoIceCreamYou(level);
		//System.out.println(levelIce);	
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				new LodeRunner(level);
//			}
//		});
		
//		String levelList = "["+convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 133.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 98.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 132.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 35.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 119.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 64.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 85.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 138.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 142.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 125.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 56.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 103.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 54.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 136.txt")+", "+
//							convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 148.txt")+"]";
//		System.out.println(levelList);
	}

	/**
	 * Converts the VGLC level of LodeRunner to JSON form to be able to be passed into the GAN
	 * @param fileName File that holds the VGLC of a lode runner level 
	 * @return
	 */
	public static ArrayList<List<Integer>> convertLodeRunnerLevelFileVGLCtoListOfLevel(String fileName) {
		String[] level = new IO().readFile(fileName);
		ArrayList<List<Integer>> complete = new ArrayList<>(LODE_RUNNER_ROWS);
		//loops through levels to get characters and convert them 
		for(int i = 0; i < level.length; i++) { 
			List<Integer> row = new ArrayList<>(LODE_RUNNER_COLUMNS);//creates new List to be a new row of the JSON 
			for(int j = 0; j < level[i].length(); j++) { //fills that array list that got added to create the row
				if(level[i].charAt(j) != '[' || level[i].charAt(j) != ']') {
					//int tileCode = convertLodeRunnerTileVGLCtoNumberCode(level[i].charAt(j)); //8 tile mapping 
					//int tileCode = convertLodeRunnerTileVGLCtoNumberCodeNoSpawn(level[i].charAt(j)); //6 tile mapping 
					int tileCode = convertLodeRunnerTileVGLCtoNumberCodeNoSpawnBothGroundTiles(level[i].charAt(j)); //7 tile mapping
					row.add(tileCode);
				}
			}
			complete.add(row); //adds a new array list to the list at index i 
		}
		return complete;
	}
	
	/**
	 * Converts the VGLC level of LodeRunner to JSON form to be able to be passed into the GAN
	 * @param fileName File that holds the VGLC of a lode runner level 
	 * @return
	 */
	public static List<List<Integer>> convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<Integer>> complete = new ArrayList<>(LODE_RUNNER_ROWS);
		//loops through levels to get characters and convert them 
		for(int i = 0; i < level.length; i++) { 
			List<Integer> row = new ArrayList<>(LODE_RUNNER_COLUMNS);//creates new List to be a new row of the JSON 
			for(int j = 0; j < level[i].length(); j++) { //fills that array list that got added to create the row
				if(level[i].charAt(j) != '[' || level[i].charAt(j) != ']') {
					int tileCode = convertLodeRunnerTileVGLCtoNumberCodeForLodeRunnerState(level[i].charAt(j)); //8 tile mapping 
					row.add(tileCode);
				}
			}
			complete.add(row); //adds a new array list to the list at index i 
		}
		return complete;
	}

	//original mapping with each individual tile 
	/**
	 * Converts tile codes to numbers for JSON conversion
	 * @param tile Character describing the tile 
	 * @return The number associated with that tile
	 */
//	private static int convertLodeRunnerTileVGLCtoNumberCode(char tile) {
//		switch(tile) {
//		case '.': //empty, passable
//			return 0;	
//		case 'G': //gold, passable, pickupable
//			return 1; 
//		case 'M': //spawn, passable 
//			return 2;	
//		case 'B': //regular ground, solid
//			return 3;
//		case 'b': //diggable ground, solid 
//			return 4;	 
//		case 'E': //enemy, damaging 
//			return 5; 
//		case '#': //ladder, passable, climbable
//			return 6;
//		case '-': //rope, passable, climbable 
//			return 7;
//		default:
//			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);
//
//		}
//	}
	
	/**
	 * Converts tile codes to numbers for JSON conversion removes the spawn tile to avoid placing multiple 
	 * Also combines both ground tiles to be represented by only diggable ground tiles
	 * @param tile Character describing the tile 
	 * @return The number associated with that tile
	 */
	private static int convertLodeRunnerTileVGLCtoNumberCodeNoSpawn(char tile) {
		switch(tile) {
		case '.': //empty, passable
		case 'M': //spawn, passable
			return 0;	
		case 'G': //gold, passable, pickupable
			return 1; 
		case 'E': //enemy, damaging 
			return 2; 
		case 'B': //regular ground, solid
		case 'b': //diggable ground, solid 
			return 3; 
		case '#': //ladder, passable, climbable
			return 4;
		case '-': //rope, passable, climbable 
			return 5;
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);

		}
	}
	
	/**
	 * Converts tile codes to numbers for JSON conversion removes the spawn tile to avoid placing multiple 
	 * Distinguishes between the two types of ground tiles 
	 * @param tile Character describing the tile 
	 * @return The number associated with that tile
	 */
	// Do not need separate but similar methods for each possible tile count. Just have a method for the higher tile count,
	// and unused tiles will never crop up.
	private static int convertLodeRunnerTileVGLCtoNumberCodeNoSpawnBothGroundTiles(char tile) {
		switch(tile) {
		case '.': //empty, passable
		case 'M': //spawn, passable
			return 0;	
		case 'G': //gold, passable, pickupable
			return 1; 
		case 'E': //enemy, damaging 
			return 2; 
		case 'b': //diggable ground, solid 
			return 3; 
		case '#': //ladder, passable, climbable
			return 4;
		case '-': //rope, passable, climbable 
			return 5;
		case 'B': //regular ground, solid
			return 6; 
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);

		}
	}
	
	/**
	 * Converts tile codes to numbers for JSON conversion removes the spawn tile to avoid placing multiple 
	 * used in the ldoe runner state because we need the spawn point 
	 * Distinguishes between the two types of ground tiles 
	 * @param tile Character describing the tile 
	 * @return The number associated with that tile
	 */
	private static int convertLodeRunnerTileVGLCtoNumberCodeForLodeRunnerState(char tile) {
		switch(tile) {
		case '.': //empty, passable
			return 0;	
		case 'G': //gold, passable, pickupable
			return 1; 
		case 'E': //enemy, damaging 
			return 2; 
		case 'b': //diggable ground, solid 
			return 3; 
		case '#': //ladder, passable, climbable
			return 4;
		case '-': //rope, passable, climbable 
			return 5;
		case 'B': //regular ground, solid
			return 6; 
		case 'M': //spawn, passable
			return 7; 
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);

		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return A string that holds the entire level in the correct format
	 */
	public static String convertLodeRunnerVGLCtoIceCreamYou(String fileName) {
		String[] level = new IO().readFile(fileName);
		String playFormat = "";
		String tile = "";
		for(int i =0; i < level.length; i++) {
			for(int j = 0; j < level[i].length();j++) {
				if(level[i].charAt(j) != '[' || level[i].charAt(j) != ']') {
					int x = i; 
					int y = j; 
					tile = convertLodeRunnerTileVGLCtoIceCreamYou(level[i].charAt(j), x, y);
					playFormat += tile;
				}
			}
		}
		return playFormat;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return A string that holds the entire level in the correct format
	 */
	public static String convertLodeRunnerJSONtoIceCreamYou(List<List<Integer>> level) {
		String playFormat = "";
		String tile = "";
		for(int i =0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size();j++) {
				if(level.get(i).get(j) != '[' || level.get(i).get(j) != ']') {
					int x = i; 
					int y = j; 
					tile = convertLodeRunnerTileJSONtoIceCreamYou(level.get(i).get(j), x, y);
					playFormat += tile;
				}
			}
		}
		return playFormat;
	}
	
	/**
	 * Converts a tile from the VGLC to be playable in IceCreamYou
	 * TODO: Fix the scaling!!!!!!!!!! there are still a lot of issues  
	 * @param tile Tile from VGLC 
	 * @param x X coordinate
	 * @param y Y coordinate 
	 * @return A string that represents the specified tile in IceCreamYou format 
	 */
	private static String convertLodeRunnerTileJSONtoIceCreamYou(int tile, int x, int y) {
		switch(tile) {
		case 0:
			return "";
		case 1: //gold, passable, pickupable
			return "coin:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 2: //enemy, damaging 
			return "enemy:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 3: //diggable ground, solid 
			return "diggable:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 4: //ladder, passable, climbable
			return "ladder:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 5: //rope, passable, climbable
			return "bar:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n"; 
		case 6://regular ground, solid
			return "solid:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 7: 
			return "player:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+",1\n";
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);
		}
	}
	/**
	 * Converts a tile from the VGLC to be playable in IceCreamYou
	 * TODO: Fix the scaling!!!!!!!!!! there are still a lot of issues  
	 * @param tile Tile from VGLC 
	 * @param x X coordinate
	 * @param y Y coordinate 
	 * @return A string that represents the specified tile in IceCreamYou format 
	 */
	private static String convertLodeRunnerTileVGLCtoIceCreamYou(char tile, int x, int y) {
		switch(tile) {
		case '.':
			return "";
		case 'M': 
			return "player:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+",1\n";
		case 'B'://regular ground, solid
			return "solid:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 'b': //diggable ground, solid 
			return "diggable:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case '#': //ladder, passable, climbable
			return "ladder:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case '-': //rope, passable, climbable
			return "bar:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n"; 
		case 'E': //enemy, damaging 
			return "enemy:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n";
		case 'G': //gold, passable, pickupable
			return "coin:"+y*(ICE_CREAM_YOU_WIDTH/LODE_RUNNER_COLUMNS)+","+x*(ICE_CREAM_YOU_HEIGHT/LODE_RUNNER_ROWS)+"\n"; 
		default:
			throw new IllegalArgumentException("Invalid Lode Runner tile from VGLV: " + tile);
		}
	}
}
