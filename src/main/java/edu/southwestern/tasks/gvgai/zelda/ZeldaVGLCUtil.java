package edu.southwestern.tasks.gvgai.zelda;

import edu.southwestern.util.random.RandomNumbers;
import gvgai.tools.IO;

public class ZeldaVGLCUtil {

	public static final String ZELDA_LEVEL_PATH = "data/VGLC/Zelda/Processed/";
	
	/**
	 * Get converted VGLC -> GVG-AI level by specifying file name of source VGLC level
	 * @param fileName
	 * @return
	 */
	public static String[] convertZeldaLevelFileVGLCtoGVGAI(String fileName) {
		String[] level = new IO().readFile(fileName);
		return convertZeldaLevelVGLCtoGVGAI(level);
	}
	
	/**
	 * Take Zelda level as a String array (converted from VGLC file) and
	 * return a version with tiles swapped to those used by GVG-AI.
	 * @param level
	 * @return
	 */
	public static String[] convertZeldaLevelVGLCtoGVGAI(String[] level) {
		String[] result = new String[level.length];
		for(int i = 0; i < level.length; i++) {
			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < level[i].length(); j++) {
				sb.append(convertZeldaTileVGLCtoGVGAI(level[i].charAt(j)));
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
	 * A = avatar
	 * 1,2,3 = different enemies
	 * . = floor
	 * 
	 * The original game is much more complex. This is an attempt at a simple mapping.
	 * F -> .
	 * B -> w (in Zelda, blocks are sometimes movable, though the corpus does not indicate which ones can move)
	 * M -> Random choice between 1, 2, and 3
	 * P -> w (in Zelda, projectiles can move over these tiles)
	 * O -> . (not fully clear what this represents, but I think it is a passable tile)
	 * I -> w (not clear what this is either)
	 * D -> g (This is probably not a semantically appropriate choice for GVG-AI, but it should look good)
	 * S -> g (Also questionable, though a staircase probably is a way of exiting an area)
	 * W -> w
	 * - -> .
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
	 * For quick tests
	 * @param args
	 */
	public static void main(String[] args) {
		String[] level = convertZeldaLevelFileVGLCtoGVGAI(ZELDA_LEVEL_PATH+"tloz1_1.txt");
		
		for(String line : level) {
			System.out.println(line);
		}
	}
}
