package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import gvgai.core.game.BasicGame;
import gvgai.tracks.singlePlayer.tools.human.Agent;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;


public abstract class ZeldaDungeon<T> {
	
	private static final int ZELDA_HEIGHT = (176/11)*16;//Parameters.parameters.integerParameter("zeldaImageHeight");
	private static final int ZELDA_WIDTH = 176;//Parameters.parameters.integerParameter("zeldaImageWidth");
	
	public static Heuristic<GridAction,ZeldaState> manhattan = new Heuristic<GridAction,ZeldaState>() {

		@Override
		public double h(ZeldaState s) {
			Dungeon d = s.getDungeon();
			Point goalPoint = d.getCoords(d.getGoal());
			int gDX = goalPoint.x;
			int gDY = goalPoint.y;
			
			int w = s.getDungeon().getLevelWidth();
			int h = s.getDungeon().getLevelHeight();
			
			Point g = d.getGoalPoint();
			int gX = g.x;
			int gY = g.y;
			int i = Math.abs(s.x - gX) + Math.abs(s.y - gY);
			int j = Math.abs(gDX - s.dX) * w + Math.abs(gDY - s.dY) * h;
			
			
			
			return i + j; 
		}
	};
	
	
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
		
		String[][] uuidLabels = new String[dungeon.length][dungeon[0].length];
		
		for(int y = 0; y < dungeon.length; y++) {
			for(int x = 0; x < dungeon[y].length; x++) {
				if(dungeon[y][x] != null) {
					if(uuidLabels[y][x] == null)
						uuidLabels[y][x] = UUID.randomUUID().toString();
					String name = uuidLabels[y][x];
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
		dungeonInstance.setLevelThere(uuidLabels);
		
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
	private void addAdjacencyIfAvailable(Dungeon dungeonInstance, String[][] uuidLabels, Node newNode, int x, int y, String direction) {
		int tileToSetTo = 3; // Door tile number
		
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length || 
				dungeon[y][x] == null) // If theres no dungeon there set the tiles to wall
			tileToSetTo = Tile.WALL.getNum();
		
		setLevels(direction, newNode, tileToSetTo); // Set the doors in the levels
		findAndAddGoal(dungeonInstance, newNode);
		
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return;
		if(dungeon[y][x] == null) return; // Finally get out if there's no adjacency
		
		if(uuidLabels[y][x] == null) uuidLabels[y][x] = UUID.randomUUID().toString(); // Get the unique ID of the level
		String whereTo = uuidLabels[y][x]; // This will be the where to in the edge

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
	
	private void findAndAddGoal(Dungeon dungeon, Node newNode) {
		List<List<Integer>> ints = newNode.level.intLevel;
		String name = newNode.name;
		for(int y = 0; y < ints.size(); y++) {
			for(int x = 0; x < ints.get(y).size(); x++) {
				if(ints.get(y).get(x).equals(Tile.TRIFORCE.getNum())) {
					dungeon.setGoalPoint(new Point(x, y));
					dungeon.setGoal(name);
				}
			}
		}
	}

	/**
	 * Set edges when you're going UP
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private void addUpAdjacencies(Node newNode, String whereTo) {
		int y, minX, maxX = 0, startY;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			y = 1;
			minX = 4;
			minX = 6;
			startY = 13;
		} else {
			y = 1;
			minX = 7;
			maxX = 8;
			startY = 8;			
		}

		for(int x = minX; x <= maxX; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, startY);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going DOWN
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private void addDownAdjacencies(Node newNode, String whereTo) {
		int y, minX, maxX;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			y = 14;
			minX = 4;
			maxX = 6;
		} else {
			y = 9;
			minX = 7;
			maxX = 8;

		}
		for(int x = minX; x <= maxX; x++) {
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
		int x, minY, maxY;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			x = 9;
			minY = 7;
			maxY = 8;
		} else {
			x = 14;
			minY = 4;
			maxY = 6;
		}
		
		for(int y = minY; y <= maxY; y++) {
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
		int x, minY, maxY = 0, startX;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")){
			x = 1;
			minY = 7;
			minY = 8;
			startX = 8;
		} else {
			x = 1;
			minY = 4;
			maxY = 6;
			startX = 13;
		}
		for(int y = minY; y <= maxY; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(startX, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}

	}
	
	private void setLevels(String direction, Node node, int tile) {
		List<List<Integer>> level = node.level.intLevel;
		// Randomize tile only if the door being placed actually leads to another room
		if(tile == 3) {
			if(Math.random() > 0.3)
				tile = (Math.random() > 0.5) ? Tile.LOCKED_DOOR.getNum() : Tile.HIDDEN.getNum(); // Randomize 5 (locked door) or 7 (bombable wall)
			
			if(tile == Tile.LOCKED_DOOR.getNum()) placeRandomKey(level); // If the door is now locked place a random key in the level
		}
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
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
		} else {
			if(direction.equals("UP")  || direction.equals("DOWN")) { // Add doors at top or bottom
				int y = (direction.equals("UP")) ? 1 : 9; // Set x based on side 1 if left 9 if right
				for(int x = 7; x <=8; x++) {
					level.get(y).set(x, tile);
				}
			} else if (direction.equals("LEFT") || direction.equals("RIGHT") ) { // Add doors at left or right
				int x = (direction.equals("LEFT")) ? 1 : 14; // Set y based on side 1 if up 14 if bottom
				for(int y = 4; y <= 6; y++) {
					level.get(y).set(x, tile);
				}
			}
		}

	}

	/**
	 * Place a random key tile on the floor
	 * @param level
	 */
	public static void placeRandomKey(List<List<Integer>> level) {
		int x, y;
		
		do {
	        x = (int)(Math.random() * level.get(0).size());
	        y = (int)(Math.random() * level.size());
	    }
	    while (!Tile.findNum(level.get(y).get(x)).playerPassable());
		
		level.get(y).set(x, Tile.KEY.getNum()); 
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
				ZeldaState initial = new ZeldaState(5, 5, 0, dungeonInstance);
				
				Search<GridAction,ZeldaState> search = new AStarSearch<>(manhattan);
				ArrayList<GridAction> result = search.search(initial);
				
				if(result != null)
					for(GridAction a : result)
						System.out.println(a.getD().toString());
//				
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
			return TileUtil.listToTile(intLevel);
		}
	}

	/**
	 * Place a key starting in the middle of the level and going to the upper left
	 * @param intLevel 2D list of ints
	 */
	public static void placeNormalKey(List<List<Integer>> intLevel) {
		int x = intLevel.get(0).size() / 2;
		int y = intLevel.size() / 2;
		
		while(!Tile.findNum(intLevel.get(y).get(x)).playerPassable()){
			x--;
			y--;
		}
		
		intLevel.get(y).set(x, Tile.KEY.getNum());
		
	}
	
}
