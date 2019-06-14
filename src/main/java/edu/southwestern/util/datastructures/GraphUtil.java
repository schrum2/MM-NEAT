package edu.southwestern.util.datastructures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.level.Grammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;
import edu.southwestern.util.datastructures.Graph.Node;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.CreatureFactory;
import me.jakerg.rougelike.Item;
import me.jakerg.rougelike.Log;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;
import me.jakerg.rougelike.World;

public class GraphUtil {
	
	/**
	 * Save a graph of type that extends Grammar, will be saved as a DOT file
	 * @param graph Graph instance with type extending Grammar
	 * @param file File location as a string, including the .dot
	 * @throws IOException
	 */
	public static void saveGrammarGraph(Graph<? extends Grammar> graph, String file) throws IOException {
		File f = new File(file);
		BufferedWriter w = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
		w.write("graph {\n");
		
		Graph<? extends Grammar>.Node n = graph.root();
		List<Graph<? extends Grammar>.Node> visited = new ArrayList<>();
		Queue<Graph<? extends Grammar>.Node> queue = new LinkedList<>();
		queue.add(n);
		visited.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();
			w.write(node.getID() + "[label=\"" + node.getData().getLevelType() + "\"]\n");
			for(Graph<? extends Grammar>.Node v : node.adjacencies()) {
				if(!visited.contains(v)) {
					visited.add(v);
					queue.add(v);
				}
			}
			
		}
	
		n = graph.root();
		visited = new ArrayList<>();
		queue = new LinkedList<>();
		queue.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();

			visited.add(node);
			for(Graph<? extends Grammar>.Node v : node.adjacencies()) {
				if(!visited.contains(v)) {
					w.write(node.getID() + " -- " + v.getID() +"\n");
					queue.add(v);				
				}
			}
			
		}
		
		
		w.write("}");
		w.close();
	}
	
	/**
	 * Take a graph of type grammar and make a dungeon out of it using BFS
	 * @param graph Graph to use
	 * @return Dungeon from the graph
	 * @throws Exception
	 */
	public static Dungeon convertToDungeon(Graph<? extends Grammar> graph) throws Exception {
		Dungeon dungeon = new Dungeon();
		String[][] levelThere = new String[100][100];
		int x = (levelThere.length - 1) / 2;
		int y = (levelThere.length - 1) / 2;

		Graph<? extends Grammar>.Node n = graph.root();
		
		Level l = loadLevel(n, dungeon);
		Dungeon.Node dNode = dungeon.newNode(n.getID(), l);
		levelThere[y][x] = dNode.name;
		dungeon.setCurrentLevel(dNode.name);
		
		Queue<Graph<? extends Grammar>.Node> backlog = new LinkedList<>();
		
		List<Graph<? extends Grammar>.Node> visited = new ArrayList<>();
		Queue<Graph<? extends Grammar>.Node> queue = new LinkedList<>();
		queue.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();
			Dungeon.Node dN = dungeon.getNode(node.getID());
			dN.grammar = (ZeldaGrammar) node.getData();
			visited.add(node);
			graph.addNode((Graph.Node) node);
			Point p = getCoords(levelThere, node.getID());
			
			handleBacklog(levelThere, dungeon, backlog, visited);
			if(p == null)
				throw new Exception("Node : " + node.getID() + " not found in level there");
			
			List<Graph<? extends Grammar>.Node> adjs = new LinkedList<>(node.adjacencies);
			
			while(!adjs.isEmpty()) {
				Random r = new Random();
				Graph<? extends Grammar>.Node adjNode = adjs.remove(r.nextInt(adjs.size()));
				
				if(!visited.contains(adjNode) && !queue.contains(adjNode)) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						System.out.println("Placing " + adjNode.getID() + " at (" + legal.x + ", " + legal.y + ")");
						levelThere[legal.y][legal.x] = adjNode.getID();
						Level newLevel = loadLevel(adjNode, dungeon);
						Dungeon.Node newNode = dungeon.newNode(adjNode.getID(), newLevel);
						int tile = (node.getData().getLevelType() == "l") ? Tile.LOCKED_DOOR.getNum() : Tile.DOOR.getNum();
						setAdjacencies(dN, p, legal, newNode.name, tile);
						setAdjacencies(newNode, legal, p, dN.name, tile);
						queue.add(adjNode);
					} else {
//						backlog.add(adjNode);
						print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
//						throw new Exception("Didn't get a legal point for node: " + adjNode.getID() + " from node : " + node.getID());
					}
				} else if (visited.contains(adjNode) && !node.getData().equals(ZeldaGrammar.LOCK)
						&& !adjNode.getData().equals(ZeldaGrammar.LOCK)) {
					Dungeon.Node newNode = dungeon.getNode(adjNode.getID());
					int tile = Tile.DOOR.getNum();
					Point to = getCoords(levelThere, adjNode.getID());
					setAdjacencies(dN, p, to, newNode.name, tile);
					setAdjacencies(newNode, to, p, dN.name, tile);
				}
				print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
				System.out.println();
			}

		}
		dungeon.setLevelThere(ZeldaLevelUtil.trimLevelThere(levelThere));
		return dungeon;
	}
	
	/**
	 * Backlog is where there are too many adjacencies for a node to add, which the additional adjacencies get added to the backlog.
	 * The backlog looks for the adjacencies and attempts to add to an adjacency if it's already there in the graph
	 * @param levelThere 2D representation of the level where each cell is the name of the level
	 * @param dungeon Dungeon instance
	 * @param backlog Queue of the adjancencies to take care of
	 * @param visited 
	 * @throws Exception
	 */
	private static void handleBacklog(String[][] levelThere, Dungeon dungeon, 
			Queue<Graph<? extends Grammar>.Node> backlog, List<Graph<? extends Grammar>.Node> visited) throws Exception {
		while(!backlog.isEmpty()) {
			Graph<? extends Grammar>.Node node = backlog.poll();
			for(Graph<? extends Grammar>.Node adjNode : node.adjacencies()) {
				Point p = getCoords(levelThere, adjNode.getID());
				if(p != null) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						System.out.println("Placing from backlog: " + node.getID() + " at (" + legal.x + ", " + legal.y + ")");
						levelThere[legal.y][legal.x] = node.getID();
						Level newLevel = loadLevel(node, dungeon);
						Dungeon.Node newNode = dungeon.newNode(node.getID(), newLevel);
						newNode.grammar = (ZeldaGrammar) node.getData();
						Dungeon.Node dN = dungeon.getNode(adjNode.getID());
						
						int tile = (node.getData().getLevelType() == "l") ? Tile.LOCKED_DOOR.getNum() : Tile.DOOR.getNum();
						
						setAdjacencies(dN, p, legal, newNode.name, tile);
						setAdjacencies(newNode, legal, p, dN.name, tile);
					}
				}
			}
			visited.add(node);
		}
	}

	/**
	 * Print any 2D array, for debugging purposes
	 * @param array
	 */
	private static void print2DArray(String[][] array) {
		for(String[] row : array) {
			for(String s : row) {
				System.out.print(s +",");
			}
			System.out.println();
		}
	}

	/**
	 * Set the adjacencies, the exit and starting points
	 * @param fromNode Node where the ajancencie originates
	 * @param from exit Point
	 * @param to starting Point
	 * @param whereTo Name of the room the starting point is going to
	 * @param tile Tile to place the at exit point as a number
	 * @throws Exception
	 */
	private static void setAdjacencies(Dungeon.Node fromNode, Point from,
			Point to, String whereTo, int tile) throws Exception {
		String direction = getDirection(from, to);
		
		if(direction == null) return;

		switch(direction) {
		case "UP":
			ZeldaLevelUtil.addUpAdjacencies(fromNode, whereTo);
			break;
		case "DOWN":
			ZeldaLevelUtil.addDownAdjacencies(fromNode, whereTo);
			break;
		case "LEFT":
			ZeldaLevelUtil.addLeftAdjacencies(fromNode, whereTo);
			break;
		case "RIGHT":
			ZeldaLevelUtil.addRightAdjacencies(fromNode, whereTo);
			break;
		default:
			throw new Exception ("DIRECTION AINT HEREE");
		}
		
		ZeldaLevelUtil.setDoors(direction, fromNode.level.intLevel, tile);
	}
	
	
	/**
	 * Get the direction as a string based on the from and to point, must be next to each other
	 * @param from The origin point
	 * @param to The point to get the direction 
	 * @return Direction as a string
	 */
	private static String getDirection(Point from, Point to) {
		int dX = from.x - to.x;
		int dY = from.y - to.y;
		if(dX == -1 && dY == 0)
			return "RIGHT";
		else if(dX == 1 && dY == 0)
			return "LEFT";
		else if(dX == 0 && dY == -1)
			return "DOWN";
		else if(dX == 0 && dY == 1)
			return "UP";
		else
			return null;
	}

	/**
	 * Checks neighboring coordinates, randomly, based off of p
	 * @param p The origin as a Point
	 * @param levelThere 2D representation of the dungeon
	 * @return Point where the next level is going to be place
	 */
	private static Point getNextLegalPoint(Point p, String[][] levelThere) {
		int y = p.y;
		int x = p.x;
		List<Point> options = new LinkedList<>(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
		while(!options.isEmpty()) {
			Random r = new Random();
			Point opt = options.remove(r.nextInt(options.size()));
			x = opt.x;
			y = opt.y;
			
			if(x >= 0 && x < levelThere[0].length && y >= 0 && y < levelThere.length) {
				if(levelThere[y][x] == null) {
					System.out.println(levelThere[y][x]);
					return new Point(x, y);
				}
					
			}
		
		}
		return null;
	}

	/**
	 * Get the coordinates of a name
	 * @param levelThere 2D representation of a dungeon
	 * @param n name to check for
	 * @return Point of where the name is in the dungeon, null if it wasn't found
	 */
	private static Point getCoords(String[][] levelThere, String n) {
		for(int y = levelThere.length - 1; y >= 0; y--) {
			for(int x = 0; x < levelThere[0].length; x++) {
				if(levelThere[y][x] == n)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	/**
	 * Load one empty level and populate based on tile type
	 * @param n Node to load for
	 * @param dungeon Dungeon to add to
	 * @return Modified level based off of n
	 * @throws FileNotFoundException
	 */
	private static Level loadLevel(Graph<? extends Grammar>.Node n, Dungeon dungeon) throws FileNotFoundException {
		Level level = loadOneLevel(new File("data/VGLC/Zelda/n.txt"));
		switch(n.getData().getLevelType()) {
		case "k":
			ZeldaLevelUtil.placeRandomKey(level.intLevel);
			break;
		case "n":
		case "l":
			break;
		case "e":
			ZeldaLevelUtil.addRandomEnemy(level.intLevel);
			break;
		case "t":
			level = level.placeTriforce(dungeon);
			dungeon.setGoal(n.getID());
			break;
		}
		return level;
	}

	/**
	 * Load a level based off of the file, assumes using the normal dungeon layout
	 * @param file File to load 
	 * @return Level representation of the file
	 * @throws FileNotFoundException
	 */
	private static Level loadOneLevel(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String[] levelString = new String[11];
		int i = 0;
		while(scanner.hasNextLine())
			levelString[i++] = scanner.nextLine();
			
		List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
		Level level = new Level(levelInt);
		scanner.close();
		return level;
	}
	
	public static BufferedImage imageOfDungeon(Dungeon dungeon) {
		int BLOCK_HEIGHT = dungeon.getCurrentlevel().level.intLevel.size() * 16;
		int BLOCK_WIDTH = dungeon.getCurrentlevel().level.intLevel.get(0).size() * 16;
		String[][] levelThere = dungeon.getLevelThere();
		int width = levelThere[0].length * BLOCK_WIDTH;
		int height = levelThere.length * BLOCK_HEIGHT;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		
		g2d.setRenderingHint(
			    RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(
			    RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height);
		Graphics g = image.getGraphics();
		
//		Font f = new Font("Trebuchet MS", Font.PLAIN, BLOCK_SIZE / 2);
//		g.setFont(f);
		
		for(int y = 0; y < levelThere.length; y++) {
			for(int x = 0; x < levelThere[y].length; x++) {
				Dungeon.Node n = dungeon.getNodeAt(x, y);
				System.out.println("(" + x + ", " + y + ")");
				int oX = x * BLOCK_WIDTH;
				int oY = y * BLOCK_HEIGHT;
				if(n != null) {
					BufferedImage bi = getLevelImage(n, dungeon);
					g.setColor(Color.GRAY);
					g.fillRect(oX, oY, oX + BLOCK_WIDTH, oY + BLOCK_HEIGHT);
					g.drawImage(bi, oX, oY, null);
//					g.setColor(Color.BLACK);
//					oX = (oX + BLOCK_SIZE) - (BLOCK_SIZE / 2) - (BLOCK_SIZE / 4);
//					oY = (oY + BLOCK_SIZE) - (BLOCK_SIZE / 2) + (BLOCK_SIZE / 4);
//					g.drawString(n.grammar.getLevelType(), oX, oY);
				} else {
					g.setColor(Color.BLACK);
					g.fillRect(oX, oY, oX + BLOCK_WIDTH, oY + BLOCK_HEIGHT);
				}
			}
		}
		
		g.dispose();
		g2d.dispose();
		
		return image;
	}
	
	public static BufferedImage getLevelImage(Dungeon.Node node, Dungeon dungeon) {
		int lHeight = node.level.intLevel.size();
		int lWidth = node.level.intLevel.get(0).size();
		
		AsciiPanel panel = new AsciiPanel(lWidth, lHeight, AsciiFont.CP437_16x16);
		
		drawToPanel(panel, node, dungeon);
		
		int w = panel.getCharWidth() * lWidth;
		int h = panel.getCharHeight() * lHeight;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		panel.paint(g);
		g.dispose();
		return image;
		
	}

	private static void drawToPanel(AsciiPanel panel, Dungeon.Node node, Dungeon dungeon) {
		World world = null;
		Log log = new Log(0);
		CreatureFactory cf = new CreatureFactory(world, log);
		Creature p = cf.newDungeonPlayer(dungeon);
		world = TileUtil.makeWorld(node.level.intLevel, p, log);
		boolean isStart = dungeon.getCurrentlevel().equals(node);
		if(isStart) {
			p.x = 5;
			p.y = 5;
		}
		for (int y = 0; y < world.getHeight(); y++){
            for (int x = 0; x < world.getWidth(); x++){
            	
            	// If there's a creature at that position display it
            	Creature c = world.creature(x, y);
            	Item i = world.item(x, y);
            	if (c != null && (!c.isPlayer() || isStart) )
            		panel.write(c.glyph(), c.x, c.y, c.color());
            	else if(i != null)
            		panel.write(i.glyph(), i.x, i.y, i.color());
            	else
            		panel.write(world.glyph(x, y), x, y, world.color(x, y));
            }
        }
	}
	
}
