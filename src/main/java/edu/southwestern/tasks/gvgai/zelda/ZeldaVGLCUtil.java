package edu.southwestern.tasks.gvgai.zelda;

import edu.southwestern.util.random.RandomNumbers;

public class ZeldaVGLCUtil {

	public static String[] convertZeldaLevelVGLCtoGVGAI(String[] level) {
		return null; //TODO
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
}
