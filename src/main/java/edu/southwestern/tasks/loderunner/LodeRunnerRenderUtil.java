package edu.southwestern.tasks.loderunner;

import java.awt.image.BufferedImage;
import java.util.List;


public class LodeRunnerRenderUtil {
	public static final int LODE_RUNNER_TILE_X = 8; // x length of an individual tile 
	public static final int LODE_RUNNER_TILE_Y = 7; // y length of an individual tile 
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to width in original game
	
	
	public static void main(String[] args) {
		
	}

	
	public static BufferedImage getBufferedImage(List<List<Integer>> list) {
		BufferedImage image = new BufferedImage(LODE_RUNNER_TILE_X*LODE_RUNNER_COLUMNS, LODE_RUNNER_TILE_Y*LODE_RUNNER_ROWS, BufferedImage.TYPE_INT_ARGB);
		
		return image;
	}
	
}
