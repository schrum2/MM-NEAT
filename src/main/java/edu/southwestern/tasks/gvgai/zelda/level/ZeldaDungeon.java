package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon.Node;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import gvgai.core.game.BasicGame;
import gvgai.tools.Vector2d;
import gvgai.tracks.singlePlayer.tools.human.Agent;
import math.geom2d.Vector2D;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;


public abstract class ZeldaDungeon<T> {
	
	private static final int ZELDA_HEIGHT = (176/11)*16;//Parameters.parameters.integerParameter("zeldaImageHeight");
	private static final int ZELDA_WIDTH = 176;//Parameters.parameters.integerParameter("zeldaImageWidth");
	
	private Level[][] dungeon = null;
	
	public abstract Level[][] makeDungeon(ArrayList<T> phenotypes, int numRooms);
	
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
		
		return dungeonInstance;
	}
	
	private void addAdjacencyIfAvailable(Dungeon dungeonInstance, UUID[][] uuidLabels, Node newNode, int x, int y, String direction) {
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return;
		
		int tileToSetTo = 3;
		
		if(dungeon[y][x] == null) // If theres no dungeon there set the tiles to wall
			tileToSetTo = 1;
		
		setLevels(direction, newNode, tileToSetTo);
		
		if(dungeon[y][x] == null) return; // Finally get out if there's no adjacency
		
		if(uuidLabels[y][x] == null) uuidLabels[y][x] = UUID.randomUUID();
		String whereTo = uuidLabels[y][x].toString();

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
	
	// Going up to down
	private void addUpAdjacencies(Node newNode, String whereTo) {
		int y = 1;
		for(int x = 4; x <= 6; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 13);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	// Going down to up
	private void addDownAdjacencies(Node newNode, String whereTo) {
		int y = 14;
		for(int x = 4; x <= 6; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 2);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	// Going from right to left
	private void setRightAdjacencies(Node newNode, String whereTo) {
		int x = 9;
		for(int y = 7; y <= 8; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(2, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	// Going from left to right
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

	public abstract List<List<Integer>> getLevelFromLatentVector(T phenotype);
	
	public void showDungeon(ArrayList<T> phenotypes, int numRooms) {
		dungeon = makeDungeon(phenotypes, numRooms);
//		dungeon = postHocDungeon(dungeon);
		
		JFrame frame = new JFrame("Dungeon Viewer");
		frame.setSize(1000, 1000);
		
		JPanel dungeonGrid = new JPanel();
		dungeonGrid.setLayout(new GridLayout(numRooms, numRooms));
		
		for(int i = 0; i < dungeon.length; i++) {
			for(int j = 0; j < dungeon[i].length; j++) {
				if(dungeon[i][j] != null) {
					BufferedImage level = getButtonImage(dungeon[i][j], ZELDA_WIDTH, ZELDA_HEIGHT); //creates image rep. of level)
					ImageIcon img = new ImageIcon(level.getScaledInstance(ZELDA_WIDTH, ZELDA_HEIGHT, Image.SCALE_DEFAULT)); //creates image of level
					JLabel imageLabel = new JLabel(img); // places level on label
					dungeonGrid.add(imageLabel); //add label to panel
				} else {
					JLabel blankText = new JLabel("");
					blankText.setForeground(Color.WHITE);
					JPanel blankBack = new JPanel();
					blankBack.setBackground(Color.BLACK);
					blankBack.add(blankText);
					dungeonGrid.add(blankBack);
				}
			}
		}
		
		frame.add(dungeonGrid);
		frame.setVisible(true);
	}
	
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
	
	private boolean shouldPostHoc(Level[][] d, int y, int x) {
		if(x < 0 || x >= dungeon[0].length || y < 0 || y >= dungeon.length) return false;
		
		if(dungeon[y][x] == null) return false;
		
		return true;
	}

	private BufferedImage getButtonImage(Level level, int width, int height) {
		GameBundle bundle = ZeldaGANLevelBreederTask.setUpGameWithLevelFromList(level.getLevel());
		return GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
	}
	
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
