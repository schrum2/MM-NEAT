package edu.southwestern.tasks.gvgai.zelda;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.util.random.RandomNumbers;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tools.IO;
import gvgai.tracks.singlePlayer.tools.human.Agent;

public class ZeldaVGLCUtil {

	public static final String ZELDA_LEVEL_PATH = "data/VGLC/Zelda/Processed/";
	public static final int ZELDA_ROOM_COLUMNS = 11; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int ZELDA_ROOM_ROWS = 16; // Equivalent to width in original game
	
	/**
	 * Get converted VGLC -> GVG-AI level by specifying file name of source VGLC level
	 * @param fileName
	 * @param startLocation This location is replaced with the 'A' tile for the avatar, unless it is null
	 * @return
	 */
	public static String[] convertZeldaLevelFileVGLCtoGVGAI(String fileName, Point startLocation) {
		String[] level = new IO().readFile(fileName);
		return convertZeldaLevelVGLCtoGVGAI(level, startLocation);
	}
	
	/**
	 * Takes VGLC text file for a single dungeon, identifies the individual rooms of the dungeon,
	 * converts them to lists of lists of number codes, and combines the results into a list of
	 * rooms (where each room is a list of lists).
	 * @param fileName
	 * @return
	 */
	public static List<List<List<Integer>>> convertZeldaLevelFileVGLCtoListOfRooms(String fileName) {
		String[] level = new IO().readFile(fileName);
		List<List<List<Integer>>> rooms = new ArrayList<>();
		// Parse out rooms
		for(int i = 0; i < level.length; i += ZELDA_ROOM_ROWS) { // Move down by one room "height"
			for(int j = 0; j < level[i].length(); j+= ZELDA_ROOM_COLUMNS) { // Move right by one room "width"
				if(level[i].charAt(j) != '-') { // If the upper-right corner is not "void" then there is a room
					// Separate the room from the rest of the level
					String[] room = new String[ZELDA_ROOM_ROWS];
					for(int r = 0; r < ZELDA_ROOM_ROWS; r++) { // Each row of the room
						room[r] = level[i+r].substring(j, j+ZELDA_ROOM_COLUMNS); // Only the columns for that room
					}
					// Convert individual room to list of codes and add to rooms list
					rooms.add(convertZeldaLevelVGLCtoRoomAsList(room));
				}
			}
		}		
		return rooms;
	}
	
	/**
	 * Takes VGLC room and converts it to a list of lists of numeric codes
	 * @param room String array representing one dungeon room
	 * @return List of lists representing one room
	 */
	public static List<List<Integer>> convertZeldaLevelVGLCtoRoomAsList(String[] room) {
		List<List<Integer>> result = new ArrayList<>(ZELDA_ROOM_ROWS);
		for(int i = 0; i < room.length; i++) {
			result.add(new ArrayList<Integer>(ZELDA_ROOM_COLUMNS)); // Next row of room
			for(int j = 0; j < room[i].length(); j++) {
				int code = convertZeldaTileVGLCtoNumberCode(room[i].charAt(j));
				result.get(i).add(code);
			}
		}
		return result;
	}
	
	/**
	 * Take Zelda level as a String array (converted from VGLC file) and
	 * return a version with tiles swapped to those used by GVG-AI.
	 * @param level
	 * @param startLocation This location is replaced with the 'A' tile for the avatar, unless it is null
	 * @return
	 */
	public static String[] convertZeldaLevelVGLCtoGVGAI(String[] level, Point startLocation) {
		String[] result = new String[level.length];
		for(int i = 0; i < level.length; i++) {
			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < level[i].length(); j++) {
				char tile = convertZeldaTileVGLCtoGVGAI(level[i].charAt(j));
				if(new Point(i,j).equals(startLocation)) { // Replace designated start location with Zelda avatar
					tile = 'A';
				}
				sb.append(tile);
			}
			result[i] = sb.toString();
		}
		return result;
	}
	
	/**
	 * VGLC uses the following tiles:
	 * F = FLOOR
	 * B = BLOCK
	 * M = MONSTER
	 * P = ELEMENT (LAVA, WATER)
	 * O = ELEMENT + FLOOR (LAVA/BLOCK, WATER/BLOCK)
	 * I = ELEMENT + BLOCK
	 * D = DOOR
	 * S = STAIR
	 * W = WALL
	 * - = VOID
	 * 
	 * Whereas GVG-AI uses these tiles
	 * w = wall
	 * g = gate
	 * + = key
	 * A = avatar (This is never used)
	 * 1,2,3 = different enemies
	 * . = floor
	 * 
	 * The original game is much more complex. This is an attempt at a simple mapping.
	 * The mapping also assigns arbitrary number codes for GAN learning.
	 * F -> 0:.
	 * B -> 1:w (in Zelda, blocks are sometimes movable, though the corpus does not indicate which ones can move)
	 * M -> 2:Random choice between 1, 2, and 3
	 * P -> 3:w (in Zelda, projectiles can move over these tiles)
	 * O -> 0:. (not fully clear what this represents, but I think it is a passable tile)
	 * I -> 3:w (not clear what this is either)
	 * D -> 4:g (This is probably not a semantically appropriate choice for GVG-AI, but it should look good)
	 * S -> 4:g (Also questionable, though a staircase probably is a way of exiting an area)
	 * W -> 3:w
	 * - -> 0:.
	 * 
	 * @param tile From VGLC
	 * @return Corresponding tile for GVG-AI version of Zelda
	 */
	public static char convertZeldaTileVGLCtoGVGAI(char tile) {
		switch(tile) {
			case 'F':
			case 'O':
			case '-':
				return '.';
			case 'B': 
			case 'P':
			case 'I':
			case 'W':
				return 'w';
			case 'M':
				return (char)('1' + RandomNumbers.randomGenerator.nextInt(3)); // 1, 2, or 3
			case 'D':
			case 'S':
				return 'g';
			default:
				throw new IllegalArgumentException("Invalid Zelda tile from VGLV: " + tile);
		}
	}

	/**
	 * Shares the mapping above, but returns the arbitrary number code.
	 * Used for GAN learning.
	 * @param tile A VGLC tile
	 * @return Number code associated with different classes of tile
	 */
	public static int convertZeldaTileVGLCtoNumberCode(char tile) {
		switch(tile) {
		case 'F':
		case 'O':
		case '-':
			return 0;	// Passable
		case 'B': 
		case 'P':
		case 'I':
		case 'W':
			return 3;	// Impassable
		case 'M':
			return 2;	// Monster
		case 'D':
		case 'S':
			return 4;	// Door
		default:
			throw new IllegalArgumentException("Invalid Zelda tile from VGLV: " + tile);
		}
	}

	
	/**
	 * For quick tests
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		//MMNEAT.loadClasses();
		
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		String game = "zelda";
		String gamesPath = "data/gvgai/examples/gridphysics/";
		String game_file = gamesPath + game + ".txt";
		int playerID = 0;
		int seed = 0;

		String[] level = convertZeldaLevelFileVGLCtoGVGAI(ZELDA_LEVEL_PATH+"tloz1_1.txt", new Point(40,63));
		
		for(String line : level) {
			System.out.println(line);
		}
		
		List<List<List<Integer>>> roomList = convertZeldaLevelFileVGLCtoListOfRooms(ZELDA_LEVEL_PATH+"tloz1_1.txt");
		
		System.out.println(roomList);
		
		Agent agent = new Agent();
		agent.setup(null, 0, true); // null = no log, true = human 

		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		GVGAIUtil.runOneGame(toPlay, level, true, agent, seed, playerID);

	}
}
