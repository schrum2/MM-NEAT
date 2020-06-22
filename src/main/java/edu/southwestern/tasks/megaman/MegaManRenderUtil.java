package edu.southwestern.tasks.megaman;

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

//import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;
/**
 * This class renders a level given a List<List<Integer>>
 * @author Benjamin Capps
 *
 */
public class MegaManRenderUtil {
	public static final String MEGA_MAN_TILE_PATH = "data/VGLC/MegaMan/Tiles/"; //file path for tiles 
	public static final String MEGA_MAN_LEVEL_PATH = "data/VGLC/MegaMan/Enhanced/"; //file path for levels 
	public static final int MEGA_MAN_TILE_X = 45; // x length of an individual tile 
	public static final int MEGA_MAN_TILE_Y = 45; // y length of an individual tile
	public static List<List<Integer>> level = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MEGA_MAN_LEVEL_PATH +"megaman_1_"+5+".txt");

//	public static final int MEGA_MAN_COLUMNS = level.get(0).size(); 
//	public static final int MEGA_MAN_ROWS = level.size();
	public static final int MEGA_MAN_SCREEN_COLUMNS = 16;
	public static final int MEGA_MAN_SCREEN_ROWS = 14;
	public static BufferedImage FINAL_RENDER; //gets the final rendered image 
	//method not constant
//	public static final int RENDERED_IMAGE_WIDTH = MEGA_MAN_TILE_X*MEGA_MAN_COLUMNS; //width of the final rendered level 
//	public static final int RENDERED_IMAGE_HEIGHT = MEGA_MAN_TILE_Y*MEGA_MAN_ROWS; //height of the final rendered level 
	private static BufferedImage[] tileList = null;
	
	public static int renderedImageWidth(int col) {
		return MEGA_MAN_TILE_X*col;
	}
	public static int renderedImageHeight(int row) {
		return MEGA_MAN_TILE_Y*row;
	}
	/**
	 * Sets up a level to be rendered by converting the VGLC data to JSON and then 
	 * placing the correct tile in the right place to visualize the level 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		List<List<Integer>> list = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MEGA_MAN_LEVEL_PATH + "megaman_1_"+1+".txt");
		BufferedImage[] images = loadImagesForASTAR(MEGA_MAN_TILE_PATH); //Initializes the array that hold the tile images 
		FINAL_RENDER = getBufferedImage(level, images); //puts the final rendered level into a buffered image
	}

	/**
	 * Displays the BufferedImage in a JPanel  
	 * @param list JSON of the level 
	 */
	public static BufferedImage getBufferedImage(List<List<Integer>> list) throws IOException {
		BufferedImage[] images = loadImagesForASTAR(MEGA_MAN_TILE_PATH); //Initializes the array that hold the tile images 
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
		BufferedImage image = createBufferedImage(list, renderedImageWidth(list.get(0).size()), renderedImageHeight(list.size()), images); //gets the image of the level 
		//this code displays the level in a window 
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(renderedImageWidth(list.get(0).size()), renderedImageHeight(list.size()), Image.SCALE_FAST)));
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		return image;
	}
	
	/**
	 * this fixes the window and renders the level according to its size
	 * @param list JSON of the level 
	 * @param images Array of tile images 
	 * @return Final BufferedImage of the whole level in a window 
	 * @throws IOException In case the file can't be found 
	 */
	public static BufferedImage getBufferedImageWithRelativeRendering(List<List<Integer>> list, BufferedImage[] images) throws IOException {
		BufferedImage image = createBufferedImage(list, renderedImageWidth(list.get(0).size()), renderedImageHeight(list.size()), images); //gets the image of the level 
		//this code displays the level in a window 
		
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(1600, 900, Image.SCALE_AREA_AVERAGING)));
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
	 * @param height Height of rendered image 
	 * @return A BufferedImage of the level 
	 * @throws IOException In case the file can't be found
	 */
	public static BufferedImage createBufferedImage(List<List<Integer>> list, int width, int height) throws IOException {
		BufferedImage[] images = loadImagesForASTAR(MEGA_MAN_TILE_PATH); //Initializes the array that hold the tile images
		return createBufferedImage(list, width, height, images);
	}

	/**
	 * Puts tiles into BufferedImage to fully render the level 
	 * @param list JSON of the level 
	 * @param width Width of rendered image
	 * @param height Height of rendered image 
	 * @param images Array of Buffered Images referring to the tiles 
	 * @return A BufferedImage of the level 
	 * @throws IOException In case the file can't be found
	 */
	public static BufferedImage createBufferedImage(List<List<Integer>> list, int width, int height, BufferedImage[] images) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		//loops through the grid in the applet to place tiles in order to render levels 
		for(int y = 0; y < height; y += MEGA_MAN_TILE_Y) {
			for(int x = 0; x < width; x += MEGA_MAN_TILE_X) {
				int xTile = x/MEGA_MAN_TILE_X;
				int yTile = y/MEGA_MAN_TILE_Y;
				BufferedImage tileImage = findTile(list, images, xTile, yTile); //finds the correct tile 
				g.drawImage(tileImage, x, y, null);	//draws the correct tile		
			}
		}
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

	/**
	 * Loads in all of the tile images, the index corresponds to the number for that tile excluding spawn and multiple types of ground 
	 * @param filePath Directory that hold the tile images 
	 * @return An array of BufferedImages holding all the tiles for Mega Man
	 * @throws IOException In case the file can't be found
	 */
	public static BufferedImage[] loadImagesForASTAR(String filePath) throws IOException {
		
		if(tileList==null) {
			tileList = new BufferedImage[16];
			File tile = new File(filePath+"Empty.PNG");
			BufferedImage emptyTile = ImageIO.read(tile);
			tileList[0] = emptyTile;
			tile = new File(filePath+"Solid.PNG");
			BufferedImage solidTile = ImageIO.read(tile);
			tileList[1] = solidTile;
			tile = new File(filePath+"Ladder.PNG");
			BufferedImage ladderTile = ImageIO.read(tile);
			tileList[2] = ladderTile;
			tile = new File(filePath+"Hazard.PNG");
			BufferedImage HazardTile = ImageIO.read(tile);
			tileList[3] = HazardTile;
			tile = new File(filePath+"Empty.PNG");
			BufferedImage Breakable = ImageIO.read(tile);
			tileList[4] = Breakable;
			tile = new File(filePath+"MovingPlatform.PNG");
			BufferedImage movingPlatform = ImageIO.read(tile);
			tileList[5] = movingPlatform;
			tile = new File(filePath+"Solid.png");
			BufferedImage Cannon = ImageIO.read(tile);
			tileList[6] = Cannon;
			tile = new File(filePath+"Orb1.png");
			BufferedImage Orb = ImageIO.read(tile);
			tileList[7] = Orb;
			tile = new File(filePath+"Spawn.png");
			BufferedImage Spawn = ImageIO.read(tile);
			tileList[8] = Spawn;
			tile = new File(filePath+"Null.png");
			BufferedImage Null = ImageIO.read(tile);
			tileList[9] = Null;
			tile = new File(filePath+"Water.png");
			BufferedImage water = ImageIO.read(tile);
			tileList[10] = water;
			tile = new File(filePath+"Enemy11.png");
			BufferedImage enemy = ImageIO.read(tile);
			tileList[11] = enemy;
			tile = new File(filePath+"Enemy12.png");
			BufferedImage enemy2 = ImageIO.read(tile);
			tileList[12] = enemy2;
			tile = new File(filePath+"Enemy13.png");
			BufferedImage enemy3 = ImageIO.read(tile);
			tileList[13] = enemy3;
			tile = new File(filePath+"Enemy14.png");
			BufferedImage enemy4 = ImageIO.read(tile);
			tileList[14] = enemy4;
			tile = new File(filePath+"Enemy15.png");
			BufferedImage enemy5 = ImageIO.read(tile);
			tileList[15] = enemy5;
		}
		return tileList;
	}
}
