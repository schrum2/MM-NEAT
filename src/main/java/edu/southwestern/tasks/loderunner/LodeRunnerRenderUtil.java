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

/**
 * 
 * @author kdste
 *
 */
public class LodeRunnerRenderUtil {
	public static final String LODE_RUNNER_TILE_PATH = "data/VGLC/Lode Runner/Tiles/";
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/Lode Runner/Processed/";
	public static final int LODE_RUNNER_TILE_X = 10; // x length of an individual tile 
	public static final int LODE_RUNNER_TILE_Y = 10; // y length of an individual tile 
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to width in original game
	public static BufferedImage finalRender; 
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		List<List<Integer>> list = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
		BufferedImage[] images = loadImages(LODE_RUNNER_TILE_PATH);
		finalRender = getBufferedImage(list, images);
		//System.out.println(image);
	}

	/**
	 * 
	 * @param list
	 * @param images
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getBufferedImage(List<List<Integer>> list, BufferedImage[] images) throws IOException {
		int width = LODE_RUNNER_TILE_X*LODE_RUNNER_COLUMNS;
		int height = LODE_RUNNER_TILE_Y*LODE_RUNNER_ROWS;
		BufferedImage image = createBufferedImage(list, width, height, images);
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
	 * @param list JSON of the level 
	 * @param width Width of rendered image
	 * @param height HEight of rendered image 
	 * @param images Array of Buffered Images referring to the tiles 
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage createBufferedImage(List<List<Integer>> list, int width, int height, BufferedImage[] images) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		//loops through the grid in the applet to place tiles in order to render levels 
		for(int y = 0; y < height; y += LODE_RUNNER_TILE_Y) {
			for(int x = 0; x < width; x += LODE_RUNNER_TILE_X) {
				int xTile = x/LODE_RUNNER_TILE_X;
				int yTile = y/LODE_RUNNER_TILE_Y;
				BufferedImage tileImage = findTile(list, images, xTile, yTile); //finds the correct tile 
				g.drawImage(tileImage, x, y, null);	//places the correct tile		
			}
		}
		return image;
	}
	
	/**
	 * 
	 * @param list
	 * @param images
	 * @return
	 */
	private static BufferedImage findTile(List<List<Integer>> list, BufferedImage[] images, int xTile, int yTile) {
		BufferedImage tileImage = new BufferedImage(LODE_RUNNER_TILE_X, LODE_RUNNER_TILE_Y, BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < list.size(); i++) {
			for(int j = 0; j < list.get(i).size(); j++) {
				if(i == yTile && j == xTile && (list.get(i).get(j) != '[' || list.get(i).get(j) != ']')) {
					tileImage = images[list.get(i).get(j)];
				}
			}
		}
		return tileImage;
	}


	/**
	 * Loads in all of the tile images 
	 * @param filePath Directory that hold the tile images 
	 * @return An array of BufferedImages 
	 * @throws IOException
	 */
	private static BufferedImage[] loadImages(String filePath) throws IOException {
		BufferedImage[] tileList = new BufferedImage[8];
		File tile = new File(filePath+"empty.png");
		BufferedImage emptyTile = ImageIO.read(tile);
		tileList[0] = emptyTile;
		tile = new File(filePath+"gold.png");
		BufferedImage goldTile = ImageIO.read(tile);
		tileList[1] = goldTile;
		tile = new File(filePath+"spawn.png");
		BufferedImage spawnTile = ImageIO.read(tile);
		tileList[2] = spawnTile;
		tile = new File(filePath+"ground.png");
		BufferedImage groundTile = ImageIO.read(tile);
		tileList[3] = groundTile;
		tile = new File(filePath+"diggableGround.png");
		BufferedImage diggableGroundTile = ImageIO.read(tile);
		tileList[4] = diggableGroundTile;
		tile = new File(filePath+"enemy.png");
		BufferedImage enemyTile = ImageIO.read(tile);
		tileList[5] = enemyTile;
		tile = new File(filePath+"ladder.png");
		BufferedImage ladderTile = ImageIO.read(tile);
		tileList[6] = ladderTile;
		tile = new File(filePath+"rope.png");
		BufferedImage ropeTile = ImageIO.read(tile);
		tileList[7] = ropeTile;
		return tileList;
	}
	
	
	
}
