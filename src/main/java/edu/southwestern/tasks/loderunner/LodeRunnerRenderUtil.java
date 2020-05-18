package edu.southwestern.tasks.loderunner;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class LodeRunnerRenderUtil {
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/Lode Runner/Tiles";
	public static final int LODE_RUNNER_TILE_X = 8; // x length of an individual tile 
	public static final int LODE_RUNNER_TILE_Y = 7; // y length of an individual tile 
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to width in original game
	
	
	public static void main(String[] args) throws IOException {
		List<List<Integer>> list = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel("data/VGLC/Lode Runner/Processed/Level 1.txt");
		BufferedImage image = getBufferedImage(list);
		System.out.println(image);
	}

	
	public static BufferedImage getBufferedImage(List<List<Integer>> list) throws IOException {
		int width = LODE_RUNNER_TILE_X*LODE_RUNNER_COLUMNS;
		int height = LODE_RUNNER_TILE_Y*LODE_RUNNER_ROWS;
		BufferedImage[] images = loadImages(LODE_RUNNER_LEVEL_PATH);
		BufferedImage image = setTilesForBufferedImage(list, width, height, images);
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(width/2, height/2, Image.SCALE_FAST)));
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		return image;
	}

	/**
	 * Puts tiles into BufferedImage
	 * @param list
	 * @param width
	 * @param height
	 * @param images
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage setTilesForBufferedImage(List<List<Integer>> list, int width, int height, BufferedImage[] images) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		for(int y = 0; y < height; y += LODE_RUNNER_TILE_Y) {
			for(int x = 0; x < width; x += LODE_RUNNER_TILE_X) {
				BufferedImage tileImage = selectTile(images);
				g.drawImage(tileImage, x, y, null);				
			}
		}
		return image;
	}
	
	private static BufferedImage selectTile(BufferedImage[] images) {
		
		return null;
	}
	
	/**
	 * Loads in all of the tile images 
	 * @param filePath Directory that hold the tile images 
	 * @return An array of BufferedImages 
	 * @throws IOException
	 */
	private static BufferedImage[] loadImages(String filePath) throws IOException {
		BufferedImage[] tileList = new BufferedImage[8];
		File tile = new File(filePath+"/empty.png");
		BufferedImage emptyTile = ImageIO.read(tile);
		tileList[0] = emptyTile;
		tile = new File(filePath+"/gold.png");
		BufferedImage goldTile = ImageIO.read(tile);
		tileList[1] = goldTile;
		tile = new File(filePath+"/spawn.png");
		BufferedImage spawnTile = ImageIO.read(tile);
		tileList[2] = spawnTile;
		tile = new File(filePath+"/ground.png");
		BufferedImage groundTile = ImageIO.read(tile);
		tileList[3] = groundTile;
		tile = new File(filePath+"/diggableGround.png");
		BufferedImage diggableGroundTile = ImageIO.read(tile);
		tileList[4] = diggableGroundTile;
		tile = new File(filePath+"/enemy.png");
		BufferedImage enemyTile = ImageIO.read(tile);
		tileList[5] = enemyTile;
		tile = new File(filePath+"/ladder.png");
		BufferedImage ladderTile = ImageIO.read(tile);
		tileList[6] = ladderTile;
		tile = new File(filePath+"/rope.png");
		BufferedImage ropeTile = ImageIO.read(tile);
		tileList[7] = ropeTile;
		return tileList;
	}
	
	
	
}
