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
 * This class Renders a level of Lode Runner into a window  
 * @author kdste
 *
 */
public class LodeRunnerRenderUtil {
	public static final String LODE_RUNNER_TILE_PATH = "data/VGLC/Lode Runner/Tiles/"; //file path for tiles 
	public static final String LODE_RUNNER_LEVEL_PATH = "data/VGLC/Lode Runner/Processed/"; //file path for levels 
	public static final int LODE_RUNNER_TILE_X = 8; // x length of an individual tile 
	public static final int LODE_RUNNER_TILE_Y = 8; // y length of an individual tile 
	public static final int LODE_RUNNER_COLUMNS = 32; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int LODE_RUNNER_ROWS = 22; // Equivalent to width in original game
	public static BufferedImage FINAL_RENDER; //gets the final rendered image 
	
	public static final int RENDERED_IMAGE_WIDTH = LODE_RUNNER_TILE_X*LODE_RUNNER_COLUMNS; //width of the final rendered level 
	public static final int RENDERED_IMAGE_HEIGHT = LODE_RUNNER_TILE_Y*LODE_RUNNER_ROWS; //height of the final rendered level 
	
	/**
	 * Sets up a level to be rendered by converting the VGLC data to JSON and then 
	 * placing the correct tile in the right place to visualize the level 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//original mapping with 8 tiles 
//		List<List<Integer>> list = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
//		BufferedImage[] images = loadImages(LODE_RUNNER_TILE_PATH); //Initializes the array that hold the tile images 
//		FINAL_RENDER = getBufferedImage(list, images); //puts the final rendered level into a buffered image 
		//no spawn mapping with 6 tiles 
		List<List<Integer>> list = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel(LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
		BufferedImage[] images = loadImagesNoSpawnTwoGround(LODE_RUNNER_TILE_PATH); //Initializes the array that hold the tile images 
		FINAL_RENDER = getBufferedImage(list, images); //puts the final rendered level into a buffered image
	}

	/**
	 * Displays the BufferedImage in a JPanel  
	 * @param list JSON of the level 
	 */
	public static BufferedImage getBufferedImage(List<List<Integer>> list) throws IOException {
		BufferedImage[] images = loadImagesNoSpawnTwoGround(LODE_RUNNER_TILE_PATH); //Initializes the array that hold the tile images 
		return getBufferedImage(list, images);
	}
	
	/**
	 * Displays the BufferedImage in a JPanel  
	 * @param list JSON of the level 
	 * @param images Array of tile images 
	 * @return Final BufferedImage of the whole level in a window 
	 * @throws IOException In case the file can't be found 
	 */
	public static BufferedImage getBufferedImage(List<List<Integer>> list, BufferedImage[] images) throws IOException {
		System.out.println("About to create");
		BufferedImage image = createBufferedImage(list, RENDERED_IMAGE_WIDTH, RENDERED_IMAGE_HEIGHT, images); //gets the image of the level 
		System.out.println("created");
		//this code displays the level in a window 
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(RENDERED_IMAGE_WIDTH, RENDERED_IMAGE_HEIGHT, Image.SCALE_FAST)));
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		return image;
	}

	/**
	 * Puts tiles into BufferedImage to fully render the level 
	 * @param list JSON of the level 
	 * @param width Width of rendered image
	 * @param height HEight of rendered image 
	 * @param images Array of Buffered Images referring to the tiles 
	 * @return A BufferedImage of the level 
	 * @throws IOException In case the file can't be found
	 */
	public static BufferedImage createBufferedImage(List<List<Integer>> list, int width, int height, BufferedImage[] images) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		//loops through the grid in the applet to place tiles in order to render levels 
		System.out.println("About to loop");
		for(int y = 0; y < height; y += LODE_RUNNER_TILE_Y) {
			for(int x = 0; x < width; x += LODE_RUNNER_TILE_X) {
				int xTile = x/LODE_RUNNER_TILE_X;
				int yTile = y/LODE_RUNNER_TILE_Y;
				System.out.println("Find tile");
				BufferedImage tileImage = findTile(list, images, xTile, yTile); //finds the correct tile 
				g.drawImage(tileImage, x, y, null);	//draws the correct tile		
			}
		}
		System.out.println("Done looping");
		return image;
	}
	
	/**
	 * Finds the correct tile to be placed into the rendered level based off the number in the JSON representation of the level 
	 * @param list JSON of level 
	 * @param images Array of tile images 
	 * @return The tile needed as a BufferedImage 
	 */
	private static BufferedImage findTile(List<List<Integer>> list, BufferedImage[] images, int xTile, int yTile) {
		return images[list.get(yTile).get(xTile)];
	}


	//this is for the old mapping that includes all tiles, commented out because we updated the mapping 
//	/**
//	 * Loads in all of the tile images, the index corresponds to the number for that tile 
//	 * @param filePath Directory that hold the tile images 
//	 * @return An array of BufferedImages holding all the tiles for Lode Runner
//	 * @throws IOException In case the file can't be found
//	 */
//	public static BufferedImage[] loadImages(String filePath) throws IOException {
//		BufferedImage[] tileList = new BufferedImage[8];
//		File tile = new File(filePath+"empty.png");
//		BufferedImage emptyTile = ImageIO.read(tile);
//		tileList[0] = emptyTile;
//		tile = new File(filePath+"gold.png");
//		BufferedImage goldTile = ImageIO.read(tile);
//		tileList[1] = goldTile;
//		tile = new File(filePath+"spawn.png");
//		BufferedImage spawnTile = ImageIO.read(tile);
//		tileList[2] = spawnTile;
//		tile = new File(filePath+"ground.png");
//		BufferedImage groundTile = ImageIO.read(tile);
//		tileList[3] = groundTile;
//		tile = new File(filePath+"diggableGround.png");
//		BufferedImage diggableGroundTile = ImageIO.read(tile);
//		tileList[4] = diggableGroundTile;
//		tile = new File(filePath+"enemy.png");
//		BufferedImage enemyTile = ImageIO.read(tile);
//		tileList[5] = enemyTile;
//		tile = new File(filePath+"ladder.png");
//		BufferedImage ladderTile = ImageIO.read(tile);
//		tileList[6] = ladderTile;
//		tile = new File(filePath+"rope.png");
//		BufferedImage ropeTile = ImageIO.read(tile);
//		tileList[7] = ropeTile;
//		return tileList;
//	}
//	

	/**
	 * Loads in all of the tile images, the index corresponds to the number for that tile excluding spawn and multiple types of ground 
	 * @param filePath Directory that hold the tile images 
	 * @return An array of BufferedImages holding all the tiles for Lode Runner
	 * @throws IOException In case the file can't be found
	 */
	public static BufferedImage[] loadImagesNoSpawnTwoGround(String filePath) throws IOException {
		BufferedImage[] tileList = new BufferedImage[7];
		File tile = new File(filePath+"empty.png");
		BufferedImage emptyTile = ImageIO.read(tile);
		tileList[0] = emptyTile;
		tile = new File(filePath+"gold.png");
		BufferedImage goldTile = ImageIO.read(tile);
		tileList[1] = goldTile;
		tile = new File(filePath+"enemy.png");
		BufferedImage enemyTile = ImageIO.read(tile);
		tileList[2] = enemyTile;
		tile = new File(filePath+"diggableGround.png");
		BufferedImage diggableGroundTile = ImageIO.read(tile);
		tileList[3] = diggableGroundTile;
		tile = new File(filePath+"ladder.png");
		BufferedImage ladderTile = ImageIO.read(tile);
		tileList[4] = ladderTile;
		tile = new File(filePath+"rope.png");
		BufferedImage ropeTile = ImageIO.read(tile);
		tileList[5] = ropeTile;
		tile = new File(filePath+"ground.png");
		BufferedImage groundTile = ImageIO.read(tile);
		tileList[6] = groundTile;
		return tileList;
	}
	
	
	
}
