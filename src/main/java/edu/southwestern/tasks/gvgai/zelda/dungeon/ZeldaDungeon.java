package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;


public abstract class ZeldaDungeon<T> {
	
	private Level[][] dungeon = null;
	protected Dungeon dungeonInstance = null;
	JPanel dungeonGrid;
	private ArrayList<T> originalPhenotypes;

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
		addAdjacencyIfAvailable(dungeonInstance, dungeon, uuidLabels, newNode, x, y, direction, Double.NaN);
	}

	/**
	 * For each node, if there's a level next to it (based on the direction and coordinates) add the necessary edges
	 * @param dungeonInstance Instance of the dungeon
	 * @param dungeon 
	 * @param uuidLabels Unique IDs for each level
	 * @param newNode The node to add the edges to
	 * @param x X coordinate to check
	 * @param y Y coordinate to check
	 * @param direction String direction (UP, DOWN, LEFT, RIGHT)
	 * @param doorEncoding Special encoding of door type. If NaN, then just decide type randomly.
	 * @return whether or not a locked door was created
	 */
	public static boolean addAdjacencyIfAvailable(Dungeon dungeonInstance, Level[][] dungeon, String[][] uuidLabels, Node newNode, int x, int y, String direction, double doorEncoding) {
		int tileToSetTo = Tile.DOOR.getNum(); // Door tile number
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length || 
				dungeon[y][x] == null) // If theres no dungeon there set the tiles to wall
			tileToSetTo = Tile.WALL.getNum();

		boolean lockedDoor = setLevels(direction, newNode, tileToSetTo, doorEncoding); // Set the doors in the levels
		findAndAddGoal(dungeonInstance, newNode);

		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return false;
		if(dungeon[y][x] == null) return false; // Finally get out if there's no adjacency

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
		default:
		}
		return lockedDoor;
	}
	/**
	 * Finds where the triforce is and marks that room as the goal 
	 * @param dungeon Dungeon instance
	 * @param newNode Room in dungeon 
	 */
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
	

	/**
	 * Creates door connecting rooms. 
	 * 
	 * @param direction Direction being moved out of the room
	 * @param node Room being modified
	 * @param tile New tile for door location: Will simply be a door or wall, but this method changes some doors to "special" doors
	 * @param encodedDoorType Special encoding of door type. If NaN, then just decide type randomly.
	 * @return whether or not a locked door was created
	 */
	private static boolean setLevels(String direction, Node node, int tile, double encodedDoorType) {
		List<List<Integer>> level = node.level.intLevel;
		// Randomize tile only if the door being placed actually leads to another room
		if(tile == Tile.DOOR.getNum()) {
			// NaN means use chance to create door type
			//if there is no door type specified then it is randomized
			if(Double.isNaN(encodedDoorType)) {
				if(RandomNumbers.randomCoin(0.7)) {
					tile = (RandomNumbers.coinFlip()) ? Tile.LOCKED_DOOR.getNum() : Tile.HIDDEN.getNum(); // Randomize 5 (locked door) or 7 (bombable wall)
					if(tile == Tile.LOCKED_DOOR.getNum()) ZeldaLevelUtil.placeRandomKey(level, RandomNumbers.randomGenerator); // If the door is now locked place a random key in the level
				}
			} else { // Assume CPPN provided coded interpretation of door type
				Random rand = new Random(Double.doubleToLongBits(encodedDoorType)); //declares random variable to produce random placement of special doors
				//if puzzle doors are not allowed do it the original way that disallows puzzle doors
				if(!(Parameters.parameters.booleanParameter("zeldaCPPNtoGANAllowsPuzzleDoors"))) {
					if(encodedDoorType > 0.66) {
						tile = Tile.LOCKED_DOOR.getNum();
					} else if(encodedDoorType > 0.33) {
						tile = Tile.HIDDEN.getNum();
					} else if(encodedDoorType > 0.00) {
						tile = Tile.SOFT_LOCK_DOOR.getNum();
					}
					// else remain a plain door

				}else { //allows Puzzle doors to be added with a 25% likelihood of getting any of the four types of doors
					if(encodedDoorType > 0.75) {
						tile = Tile.LOCKED_DOOR.getNum();
					} else if(encodedDoorType > 0.5) {
						tile = Tile.HIDDEN.getNum();
					} else if(encodedDoorType > 0.25) {
						tile = Tile.SOFT_LOCK_DOOR.getNum();
					} else if(encodedDoorType > 0.00) {
						tile = Tile.PUZZLE_LOCKED.getNum();
					}
					// else remain a plain door
					//places a puzzle block if the door is a puzzle
					//player must move the puzzle block to unlock the door
					if(tile == Tile.PUZZLE_LOCKED.getNum()) 
						ZeldaLevelUtil.placePuzzle(direction, level, rand);
				}
				// Places keys randomly, but based on CPPN, so it will be consistent. Places too many keys ... one per locked door in each room. A bit boring.
				if(!(Parameters.parameters.booleanParameter("zeldaCPPN2GANSparseKeys")) && tile == Tile.LOCKED_DOOR.getNum()) ZeldaLevelUtil.placeRandomKey(level, rand); // If the door is now locked place a random key in the level
			}
		}
		ZeldaLevelUtil.setDoors(direction, node, tile);
		return tile == Tile.LOCKED_DOOR.getNum();
	}
	/**
	 * takes in a door tile and spits out the appropriate encodedDoorType
	 * for setLevels
	 * @param doorTile the int representing a door tile type
	 * @return the appropriate encodedDoorType for setLevels
	 */
	public static double encodedValueForDoorType(int doorTile) {
		if(!(Parameters.parameters.booleanParameter("zeldaCPPNtoGANAllowsPuzzleDoors"))) {
			switch(doorTile) {
			case -5: // Tile.LOCKED_DOOR.getNum()
				return 0.8;
			case 3: // Tile.DOOR.getNum()
				return -0.5;
			case -7: //Tile.HIDDEN.getNum()
				return .4;
			case -55: //Tile.SOFT_LOCK_DOOR.getNum()
				return .1;
			}
		} else {
			switch(doorTile) {
			case -5: // Tile.LOCKED_DOOR.getNum()
				return 0.8;
			case 3: // Tile.DOOR.getNum()
				return -0.5;
			case -7: //Tile.HIDDEN.getNum()
				return .6;
			case -55: //Tile.SOFT_LOCK_DOOR.getNum()
				return .3;
			case -10: //Tile.PUZZLE_LOCKED.getNum()
				return .1;
			}
		}
		throw new IllegalArgumentException("Tile "+doorTile+" is not recognized as a valid door tile");
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
		originalPhenotypes = phenotypes;
		dungeonInstance = makeDungeon(originalPhenotypes, numRooms);

		JFrame frame = new JFrame("Dungeon Viewer");
		frame.setSize(1000, 1000);

		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		JPanel buttons = new JPanel();

		JButton playDungeon = new JButton("Play Dungeon");
		playDungeon.setToolTipText("Play this dungeon using an ASCII-based Rogue-like interface.");
		playDungeon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ZeldaState initial = new ZeldaState(5, 5, 0, dungeonInstance);

				Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
				ArrayList<GridAction> result = search.search(initial);

				if(result != null)
					for(GridAction a : result)
						System.out.println(a.getD().toString());

				new Thread() {
					@Override
					public void run() {
						RougelikeApp.startDungeon(dungeonInstance);
					}
				}.start();
				Parameters.parameters.setBoolean("netio", false);
			}

		});
		buttons.add(playDungeon);

		JButton newDungeon = new JButton("Remake Dungeon");
		newDungeon.setToolTipText("Regenerate the dungeon using the same rooms originally selected.");
		newDungeon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int exceptionCount = 0;
				boolean success = false;
				// Fails sometimes. Give three chances to get it right (need to find root cause and fix)
				while(exceptionCount < 3 && ! success) {
					try {
						dungeonInstance = makeDungeon(originalPhenotypes, numRooms);
						container.remove(dungeonGrid); 
						dungeonGrid = getDungeonGrid(numRooms);
						container.add(dungeonGrid);
						frame.validate();
						frame.repaint();
						success = true;
					} catch (Exception e) {
						exceptionCount++;
						e.printStackTrace();
					}
				}
			}

		});
		buttons.add(newDungeon);

		if(Parameters.parameters.booleanParameter("dungeonizeAdvancedOptions")) {

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
		}

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

		dungeonInstance.markReachableRooms();
		BufferedImage image = DungeonUtil.imageOfDungeon(dungeonInstance);
		JLabel label = new JLabel(new ImageIcon(image));
		panel.add(label);

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
			//intLevel.get(0).set(0, Tile.TRIFORCE.getNum());
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

		/**
		 * Is there a locked door in the bottom wall if the given room
		 * @return If bottom wall has a locked door
		 */
		public boolean bottomExitIsLockedDoor() {
			if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
				return intLevel.get(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START).get(ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE) == Tile.LOCKED_DOOR.getNum();
			} else {
				return intLevel.get(ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE).get(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START) == Tile.LOCKED_DOOR.getNum();	
			}
		}
			
		/**
		 * Is there a locked door in the right wall if the given room
		 * @return If right wall has a locked door
		 */
		public boolean rightExitIsLockedDoor() {
			if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
				//checks all the doors in the room to see if they are locked doors, landscape 
				return intLevel.get(ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE).get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START) == Tile.LOCKED_DOOR.getNum();
			} else {
				return intLevel.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START).get(ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE) == Tile.LOCKED_DOOR.getNum();
			}
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
			while(x != -1 && y != -1 && !Tile.findNum(ints.get(y).get(x)).playerPassable()) {
				if(x % 2 == 0)
					x--;
				else
					y--;
			}
			// The code above sometimes reached -1 and caused an exception
			if(x == -1 || y == -1) {
				// Keep the choice deterministic. Find first available floor tile (this is rare anyway)
				boolean found = false;
				for(int i = 2; i < ints.size(); i++) {
					for(int j = 2; j < ints.get(0).size(); j++) {
						if(Tile.findNum(ints.get(i).get(j)).playerPassable()) {
							x = j;
							y = i;
							found = true;
							break;
						}
					}
					if(found) break;
				}

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
