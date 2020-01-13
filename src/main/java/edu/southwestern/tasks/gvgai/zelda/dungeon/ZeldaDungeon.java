package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import gvgai.core.game.BasicGame;
import gvgai.tracks.singlePlayer.tools.human.Agent;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;


public abstract class ZeldaDungeon<T> {
	
	private static final int ZELDA_HEIGHT = (176/11)*16;//Parameters.parameters.integerParameter("zeldaImageHeight");
	private static final int ZELDA_WIDTH = 176;//Parameters.parameters.integerParameter("zeldaImageWidth");\
	
	
	private Level[][] dungeon = null;
	protected Dungeon dungeonInstance = null;
	JPanel dungeonGrid;
	
	public ZeldaDungeon() {}
	
	public ZeldaDungeon(Dungeon dungeon) {
		this.dungeonInstance = dungeon;
	}
	
	/**
	 * Convert the 2D array of levels to a dungeon
	 * @param numRooms 
	 * @param phenotypes 
	 * @return converted Dungeon
	 * @throws Exception 
	 */
	public abstract Dungeon makeDungeon(ArrayList<T> phenotypes, int numRooms) throws Exception;
	
	/**
	 * For each node, if there's a level next to it (based on the direction and coordinates) add the necessary edges
	 * @param dungeonInstance Instance of the dungeon
	 * @param dungeon 
	 * @param uuidLabels Unique IDs for each level
	 * @param newNode The node to add the edges to
	 * @param x X coordinate to check
	 * @param y Y coordinate to check
	 * @param direction String direction (UP, DOWN, LEFT, RIGHT)
	 */
	public static void addAdjacencyIfAvailable(Dungeon dungeonInstance, Level[][] dungeon, String[][] uuidLabels, Node newNode, int x, int y, String direction) {
		int tileToSetTo = 3; // Door tile number
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length || 
				dungeon[y][x] == null) // If theres no dungeon there set the tiles to wall
			tileToSetTo = Tile.WALL.getNum();
		
		setLevels(direction, newNode, tileToSetTo); // Set the doors in the levels
		findAndAddGoal(dungeonInstance, newNode);
		
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return;
		if(dungeon[y][x] == null) return; // Finally get out if there's no adjacency
		
		if(uuidLabels[y][x] == null) uuidLabels[y][x] = UUID.nameUUIDFromBytes(RandomNumbers.randomByteArray(16)).toString(); // Get the unique ID of the level
		String whereTo = uuidLabels[y][x]; // This will be the where to in the edge

		// Set the edges based on the direction
		switch(direction) {
		case("UP"):
			ZeldaLevelUtil.addUpAdjacencies(newNode, whereTo);
			break;
		case("RIGHT"):
			ZeldaLevelUtil.addRightAdjacencies(newNode, whereTo);
			break;
		case("DOWN"):
			ZeldaLevelUtil.addDownAdjacencies(newNode, whereTo);
			break;	
		case("LEFT"):
			ZeldaLevelUtil.addLeftAdjacencies(newNode, whereTo);
			break;
		default: return;
		}
		
	}
	
	private static void findAndAddGoal(Dungeon dungeon, Node newNode) {
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

	private static void setLevels(String direction, Node node, int tile) {
		List<List<Integer>> level = node.level.intLevel;
		// Randomize tile only if the door being placed actually leads to another room
		if(tile == 3) {
			if(RandomNumbers.randomCoin(0.7))
				tile = (RandomNumbers.coinFlip()) ? Tile.LOCKED_DOOR.getNum() : Tile.HIDDEN.getNum(); // Randomize 5 (locked door) or 7 (bombable wall)
			
			if(tile == Tile.LOCKED_DOOR.getNum()) ZeldaLevelUtil.placeRandomKey(level); // If the door is now locked place a random key in the level
		}
		ZeldaLevelUtil.setDoors(direction, node, tile);
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
	 * @throws Exception 
	 */
	public void showDungeon(ArrayList<T> phenotypes, int numRooms) throws Exception {
		dungeonInstance = makeDungeon(phenotypes, numRooms);
		
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
				
				Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
				ArrayList<GridAction> result = search.search(initial);
				
				if(result != null)
					for(GridAction a : result)
						System.out.println(a.getD().toString());
//				
				if(!Parameters.parameters.booleanParameter("gvgAIForZeldaGAN")) {
					new Thread() {
						@Override
						public void run() {
							RougelikeApp.startDungeon(dungeonInstance);
						}
					}.start();
				} else {
					GameBundle bundle = ZeldaGANLevelBreederTask.setUpGameWithDungeon(dungeonInstance);
					new Thread() {
						@Override
						public void run() {
							// True is to watch the game being played
							GVGAIUtil.runDungeon(bundle, true, dungeonInstance);
						}
					}.start();
				}
				Parameters.parameters.setBoolean("netio", false);
			}
			
		});
		buttons.add(playDungeon);
		
		JCheckBox useGvg = new JCheckBox("Use GVG-AI", Parameters.parameters.booleanParameter("gvgAIForZeldaGAN"));
		useGvg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Parameters.parameters.changeBoolean("gvgAIForZeldaGAN");
				container.remove(dungeonGrid); 
				dungeonGrid = getDungeonGrid(numRooms);
				container.add(dungeonGrid);
				frame.validate();
				frame.repaint();
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
					try {
						dungeonInstance.saveToJson(filePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
		
		JPanel enemySlider = new JPanel();
		
		JLabel enemyLabel = new JLabel("Enemy Health");
		JLabel enemyNumber = new JLabel("1");
		
		JSlider enemyHealth = new JSlider(1, 21);
		enemyHealth.setValue(1);
		enemyHealth.setPaintTicks(true);
		enemyHealth.setMajorTickSpacing(10);
		enemyHealth.setPaintLabels(true);
		enemyHealth.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Parameters.parameters.setInteger("rougeEnemyHealth", (int) enemyHealth.getValue());
				enemyNumber.setText(String.valueOf(enemyHealth.getValue())); 
			}
			
		});
		
		enemySlider.add(enemyLabel);
		enemySlider.add(enemyNumber);
		enemySlider.add(enemyHealth);
		
		buttons.add(enemySlider);
		
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
		
		if(!Parameters.parameters.booleanParameter("gvgAIForZeldaGAN")) {
			BufferedImage image = DungeonUtil.imageOfDungeon(dungeonInstance);
			JLabel label = new JLabel(new ImageIcon(image));
			panel.add(label);
		} else {
			panel.setLayout(new GridLayout(numRooms, numRooms));
			String[][] levelThere = dungeonInstance.getLevelThere();
			for(int i = 0; i < levelThere.length; i++) {
				for(int j = 0; j < levelThere[i].length; j++) {
					if(levelThere[i][j] != null) {
						Node n = dungeonInstance.getNodeAt(j, i);
						BufferedImage level = getButtonImage(n, ZELDA_WIDTH * 3 / 4, ZELDA_HEIGHT * 3 / 4); //creates image rep. of level)
						ImageIcon img = new ImageIcon(level.getScaledInstance(level.getWidth(), level.getHeight(), Image.SCALE_FAST)); //creates image of level
						JLabel imageLabel = new JLabel(img); // places level on label
						imageLabel.setPreferredSize(new Dimension(300, 300));
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
	 * @param n Level to get the image for
	 * @param width Width of image in pixels
	 * @param height Height of image in pixels
	 * @return BufferedImage for Image label
	 */
	private BufferedImage getButtonImage(Node n, int width, int height) {
		if(Parameters.parameters.booleanParameter("gvgAIForZeldaGAN")) {
			Level level = n.level;
			List<List<Integer>> list = level.getLevel();
			GameBundle bundle = ZeldaGANLevelBreederTask.setUpGameWithLevelFromList(list);
			return GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
		} else {
			return DungeonUtil.getLevelImage(n, dungeonInstance);
		}
		
	}
	
	/**
	 * Helper class to represent the levels in the dungeon
	 * @author gutierr8
	 *
	 */
	public static class Level{
		public List<List<Integer>> intLevel;
		public String[] stringLevel;
		public Tile[][] rougeTiles;
		
		public Level(List<List<Integer>> intLevel) {
			this.intLevel = intLevel;
			this.rougeTiles = TileUtil.listToTile(intLevel);
		}

		public List<List<Integer>> getLevel(){
			return this.intLevel;
		}
		
		public String[] getStringLevel(Point startingPoint) {
			List<List<Integer>> listInts = intLevel;
			return this.stringLevel = ZeldaVGLCUtil.convertZeldaRoomListtoGVGAI(listInts, startingPoint);
		}
		
		public Tile[][] getTiles(){
			if(rougeTiles == null)
				rougeTiles = TileUtil.listToTile(intLevel);
			return rougeTiles;
		}
		
		public void tileLayout() {
			if(rougeTiles == null) return;
			
//			try {
//				PrintStream ps = new PrintStream(System.out, true, Charset.forName("cp437"));
//				
//				for(int y = 0; y < rougeTiles.length; y++) {
//					for(int x = 0; x < rougeTiles[0].length; x++) {
//						ps.print(rougeTiles[y][x].getGlyph());
//					}
//					ps.print('\n');
//				}
//				
//				ps.print('\n');
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
				
		}
		
		public boolean hasTile(Tile t) {
			int i = t.getNum();
			for(int y = 0; y < intLevel.size(); y++) {
				for(int x = 0; x < intLevel.get(y).size(); x++) {
					if(i == intLevel.get(y).get(x))
						return true;
				}
			}
			
			return false;
		}

		public Level placeTriforce(Dungeon dungeon) {
			List<List<Integer>> ints = intLevel;
			int x = (ints.get(0).size() - 1) / 2;
			int y = (ints.size() - 1) / 2;
			while(!Tile.findNum(ints.get(y).get(x)).playerPassable()) {
				if(x % 2 == 0)
					x--;
				else
					y--;
			}
			ints.get(y).set(x, Tile.TRIFORCE.getNum());
			intLevel = ints;
			if(dungeon != null)
				dungeon.setGoalPoint(new Point(x, y));;
			return this;
		}
		
		public List<Point> getFloorTiles(){
			List<Point> points = new LinkedList<>();
			for(int y = 0; y < intLevel.size(); y++)
				for(int x = 0; x < intLevel.get(y).size(); x++) {
					Tile t = Tile.findNum(intLevel.get(y).get(x));
					if(t.playerPassable())
						points.add(new Point(x, y));
				}

			
			return points;
					
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
