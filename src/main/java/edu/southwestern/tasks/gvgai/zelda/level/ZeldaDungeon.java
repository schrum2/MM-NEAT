package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon.Node;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import gvgai.core.game.BasicGame;
import gvgai.tools.Vector2d;
import gvgai.tracks.singlePlayer.tools.human.Agent;
import math.geom2d.Vector2D;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;


public abstract class ZeldaDungeon<T> {
	
	private static final int ZELDA_HEIGHT = (176/11)*16;//Parameters.parameters.integerParameter("zeldaImageHeight");
	private static final int ZELDA_WIDTH = 176;//Parameters.parameters.integerParameter("zeldaImageWidth");
	
	private Level[][] dungeon = null;
	private Dungeon dungeonInstance = null;
	JPanel dungeonGrid;
	
	/**
	 * Function specified by the specific dungeon making process to make their own dungeon
	 * @param phenotypes The phenotypes to generate the dungeon from
	 * @param numRooms Number of rooms for the dungeon
	 * @return 2D array of levels
	 */
	public abstract Level[][] makeDungeon(ArrayList<T> phenotypes, int numRooms);
	
	/**
	 * Convert the 2D array of levels to a dungeon
	 * @return converted Dungeon
	 */
	public Dungeon convertDungeon() {
		if (dungeon == null) return null;
		Dungeon dungeonInstance = new Dungeon();
		
		UUID[][] uuidLabels = new UUID[dungeon.length][dungeon[0].length];
		String[][] levelThere = new String[dungeon.length][dungeon[0].length];
		
		for(int y = 0; y < dungeon.length; y++) {
			for(int x = 0; x < dungeon[y].length; x++) {
				if(dungeon[y][x] != null) {
					if(uuidLabels[y][x] == null)
						uuidLabels[y][x] = UUID.randomUUID();
					String name = uuidLabels[y][x].toString();
					levelThere[y][x] = name;
					Node newNode = dungeonInstance.newNode(name, dungeon[y][x]);
					
					addAdjacencyIfAvailable(dungeonInstance, uuidLabels, newNode, x + 1, y, "RIGHT");
					addAdjacencyIfAvailable(dungeonInstance, uuidLabels, newNode, x, y - 1, "UP");
					addAdjacencyIfAvailable(dungeonInstance, uuidLabels, newNode, x - 1, y, "LEFT");
					addAdjacencyIfAvailable(dungeonInstance, uuidLabels, newNode, x, y + 1, "DOWN");
				}	
			}
		}
		
		String name = uuidLabels[(uuidLabels.length - 1) / 2][(uuidLabels[0].length - 1) /2].toString();
		
		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(levelThere);
		
		this.dungeonInstance = dungeonInstance;
		return dungeonInstance;
	}
	
	/**
	 * For each node, if there's a level next to it (based on the direction and coordinates) add the necessary edges
	 * @param dungeonInstance Instance of the dungeon
	 * @param uuidLabels Unique IDs for each level
	 * @param newNode The node to add the edges to
	 * @param x X coordinate to check
	 * @param y Y coordinate to check
	 * @param direction String direction (UP, DOWN, LEFT, RIGHT)
	 */
	private void addAdjacencyIfAvailable(Dungeon dungeonInstance, UUID[][] uuidLabels, Node newNode, int x, int y, String direction) {
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return;
		
		int tileToSetTo = 3; // Door tile number
		
		if(dungeon[y][x] == null) // If theres no dungeon there set the tiles to wall
			tileToSetTo = 1;
		
		setLevels(direction, newNode, tileToSetTo); // Set the doors in the levels
		
		if(dungeon[y][x] == null) return; // Finally get out if there's no adjacency
		
		if(uuidLabels[y][x] == null) uuidLabels[y][x] = UUID.randomUUID(); // Get the unique ID of the level
		String whereTo = uuidLabels[y][x].toString(); // This will be the where to in the edge

		// Set the edges based on the direction
		switch(direction) {
		case("UP"):
			addUpAdjacencies(newNode, whereTo);
			break;
		case("RIGHT"):
			setRightAdjacencies(newNode, whereTo);
			break;
		case("DOWN"):
			addDownAdjacencies(newNode, whereTo);
			break;	
		case("LEFT"):
			setLeftAdjacencies(newNode, whereTo);
			break;
		default: return;
		}
		
	}
	
	/**
	 * Set edges when you're going UP
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private void addUpAdjacencies(Node newNode, String whereTo) {
		int y = 1;
		for(int x = 4; x <= 6; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 13);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going DOWN
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private void addDownAdjacencies(Node newNode, String whereTo) {
		int y = 14;
		for(int x = 4; x <= 6; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 2);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going RIGHT
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private void setRightAdjacencies(Node newNode, String whereTo) {
		int x = 9;
		for(int y = 7; y <= 8; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(2, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going LEFT
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private void setLeftAdjacencies(Node newNode, String whereTo) {
		int x = 1;
		for(int y = 7; y <= 8; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(8, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	private void setLevels(String direction, Node node, int tile) {
		List<List<Integer>> level = node.level.intLevel;
		if(direction == "UP" || direction == "DOWN") { // Add doors at top or bottom
			int y = (direction == "UP") ? 1 : 14; // Set y based on side 1 if up 14 if bottom
			for(int x = 4; x <= 6; x++) {
				level.get(y).set(x, tile);
			}
		} else if (direction == "LEFT" || direction == "RIGHT") { // Add doors at left or right
			int x = (direction == "LEFT") ? 1 : 9; // Set x based on side 1 if left 9 if right
			for(int y = 7; y <=8; y++) {
				level.get(y).set(x, tile);
			}
		}
	}

	/**
	 * Function specified by the dungeon to get a 2D list of ints from the latent vector
	 * @param phenotype The phenotype of the level
	 * @return 2D list of the level
	 */
	public abstract List<List<Integer>> getLevelFromLatentVector(T phenotype);
	
	
	/**
	 * Show the dungeon to the viewer, this is also where the actualy dungeon making happens
	 * @param phenotypes Latent vectors of levels
	 * @param numRooms Number of rooms to fill the level with
	 */
	public void showDungeon(ArrayList<T> phenotypes, int numRooms) {
		dungeon = makeDungeon(phenotypes, numRooms);
//		dungeon = postHocDungeon(dungeon);
		
		convertDungeon(); // Make dungeon instance
		
		JFrame frame = new JFrame("Dungeon Viewer");
		frame.setSize(1000, 1000);
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel buttons = new JPanel();
		
		JButton playDungeon = new JButton("Play Dungeon");
		playDungeon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!Parameters.parameters.booleanParameter("gvgAIForZeldaGAN")) {
					new Thread() {
						public void run() {
							RougelikeApp.startDungeon(dungeonInstance);
						}
					}.start();
				} else {
					GameBundle bundle = ZeldaGANLevelBreederTask.setUpGameWithDungeon(dungeonInstance);
					new Thread() {
						public void run() {
							// True is to watch the game being played
							GVGAIUtil.runDungeon(bundle, true, dungeonInstance);
						}
					}.start();
				}
			}
			
		});
		buttons.add(playDungeon);
		
		JCheckBox useGvg = new JCheckBox("Use GVG-AI", Parameters.parameters.booleanParameter("gvgAIForZeldaGAN"));
		useGvg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Parameters.parameters.changeBoolean("gvgAIForZeldaGAN");
			}
			
		});
		buttons.add(useGvg);
		
		JButton saveDungeon = new JButton("Save Dungeon");
		saveDungeon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Whoops");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("JSON file", "json"));
				int option = fileChooser.showSaveDialog(null);
				if(option == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					Gson gson = new GsonBuilder()
							.setPrettyPrinting()
							.create();
					
					try {
						FileWriter writer = new FileWriter(filePath);
						gson.toJson(dungeonInstance, writer);
						writer.flush();
						writer.close();
					} catch (JsonIOException | IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		buttons.add(saveDungeon);
		
		JButton loadDungeon = new JButton("Load Dungeon");
		loadDungeon.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("JSON file", "json"));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileChooser.showOpenDialog(null);
				if(option == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					dungeonInstance = Dungeon.loadFromJson(filePath);
					dungeon = dungeonInstance.getLevelArrays();
					container.remove(dungeonGrid); 
					dungeonGrid = getDungeonGrid(numRooms);
					container.add(dungeonGrid);
					frame.validate();
					frame.repaint();
				}
			}
			
		});
		buttons.add(loadDungeon);
		
		container.add(buttons);
		
		dungeonGrid = getDungeonGrid(numRooms);
		
		container.add(dungeonGrid);
		
		frame.add(container);
		frame.setVisible(true);
	}
	
	/**
	 * Helper function to generate the dungeon view grid
	 * @param numRooms Number of rooms to set the grid layout
	 * @return JPanel with dungeon image icons
	 */
	protected JPanel getDungeonGrid(int numRooms) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(numRooms, numRooms));
		
		for(int i = 0; i < dungeon.length; i++) {
			for(int j = 0; j < dungeon[i].length; j++) {
				if(dungeon[i][j] != null) {
					BufferedImage level = getButtonImage(dungeon[i][j], ZELDA_WIDTH * 3 / 4, ZELDA_HEIGHT * 3 / 4); //creates image rep. of level)
					ImageIcon img = new ImageIcon(level.getScaledInstance(ZELDA_WIDTH * 3 / 4, ZELDA_HEIGHT * 3 / 4, Image.SCALE_FAST)); //creates image of level
					JLabel imageLabel = new JLabel(img); // places level on label
					panel.add(imageLabel); //add label to panel
				} else {
					JLabel blankText = new JLabel("");
					blankText.setForeground(Color.WHITE);
					JPanel blankBack = new JPanel();
					blankBack.setBackground(Color.BLACK);
					blankBack.add(blankText);
					panel.add(blankBack);
				}
			}
		}
		
		return panel;
	}

	/**
	 * Helper function to map the rooms if they have an adjacent room
	 * @param d 2D list of levels
	 * @return 2D list of levels with doors
	 */
	public Level[][] postHocDungeon(Level[][] d) {
		for(int y = 0; y < d.length; y++) {
			for(int x = 0; x < d[y].length; x++) {
				if(d[y][x] != null) {
					List<List<Integer>> level = d[y][x].intLevel;
					
					// Top
					
					int xL = 5;
					int yL = 0;
					
					if(shouldPostHoc(d, y - 1, x)) {
						level.get(yL++).set(xL, 4);
						while(level.get(yL).get(xL) != 0)
							level.get(yL++).set(xL, 0);
					}
					
					// Left
					
					xL = 0;
					yL = 8;
					
					if(shouldPostHoc(d, y, x - 1)) {
						level.get(yL).set(xL++, 4);
						while(level.get(yL).get(xL) != 0)
							level.get(yL).set(xL++, 0);						
					}

					// Right
					
					xL = 10;
					yL = 8;
					
					if(shouldPostHoc(d, y, x + 1)) {
						level.get(yL).set(xL--, 4);
						while(level.get(yL).get(xL) != 0)
							level.get(yL).set(xL--, 0);
					}
						
					// bottom
					
					xL = 5;
					yL = 15;
					
					if(shouldPostHoc(d, y + 1, x)) {
						level.get(yL--).set(xL, 4);
						while(level.get(yL).get(xL) != 0)
							level.get(yL--).set(xL, 0);
					}

				}
			}
		}
		
		return d;
	}
	
	/**
	 * Helper function to see if there's an adjacent room
	 * @param d 2D list of levels to check
	 * @param y Y coordinate to check
	 * @param x X coordinate to check
	 * @return True if there's a room at that coordinate
	 */
	private boolean shouldPostHoc(Level[][] d, int y, int x) {
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return false;
		
		if(dungeon[y][x] == null) return false;
		
		return true;
	}

	/**
	 * Helper funciton to get the button image for the dungeon viewer
	 * @param level Level to get the image for
	 * @param width Width of image in pixels
	 * @param height Height of image in pixels
	 * @return BufferedImage for Image label
	 */
	private BufferedImage getButtonImage(Level level, int width, int height) {
		GameBundle bundle = ZeldaGANLevelBreederTask.setUpGameWithLevelFromList(level.getLevel());
		return GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
	}
	
	/**
	 * Helper class to represent the levels in the dungeon
	 * @author gutierr8
	 *
	 */
	public static class Level{
		List<List<Integer>> intLevel;
		String[] stringLevel;
		Tile[][] rougeTiles;
		
		public Level(List<List<Integer>> intLevel) {
			this.intLevel = intLevel;
			this.rougeTiles = TileUtil.listToTile(intLevel);
		}
		
		public List<List<Integer>> getLevel(){
			return this.intLevel;
		}
		
		public String[] getStringLevel(Point startingPoint) {
			return this.stringLevel = ZeldaVGLCUtil.convertZeldaRoomListtoGVGAI(intLevel, startingPoint);
		}
		
		public Tile[][] getTiles(){
			return this.rougeTiles;
		}
	}
	
}
