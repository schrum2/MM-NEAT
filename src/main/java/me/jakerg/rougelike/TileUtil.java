package me.jakerg.rougelike;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TileUtil {
	/**
	 * Convert the ints to tiles
	 * @param intLevel 2D list of ints
	 * @return 2D Tile array
	 */
	public static Tile[][] listToTile(ArrayList<ArrayList<Integer>> intLevel){
		Tile[][] tiles = new Tile[intLevel.get(0).size()][intLevel.size()];
		// Some of the operations are reversed because of how the level was made
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[i].length; j++) {
				try {
					tiles[i][j] = mapIntToTile(intLevel.get(j).get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return tiles;
	}
	
	/**
	 * Make the world based on 2D ints
	 * @param level 2D list of ints
	 * @param player 
	 * @return World
	 */
	public static World makeWorld(ArrayList<ArrayList<Integer>> level, Creature player, Log log) {
		Tile[][] tiles = listToTile(level);
		World newWorld = new World(tiles);
		newWorld.addCreature(player);
		CreatureFactory cf = new CreatureFactory(newWorld, log);
		
		// Convert ints to enemies (only 2 in this case)
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[i].length; j++) {
				if(level.get(j).get(i) == 2) {
					cf.newEnemy(i, j, player);
					newWorld.setEnemyRoom(true);
				} else if(level.get(j).get(i) == -6) {
					newWorld.dropItem(new Ladder(newWorld, i, j));
				} else {
					Tile t = Tile.findNum(level.get(j).get(i));
					if(t != null) {
						if(t.equals(Tile.KEY))
							newWorld.dropItem(new Key(newWorld, new Point(i, j)));
						else if(t.isMovable())
							newWorld.dropItem(new MovableBlock(newWorld, new Point(i, j), t.getDirection()));
					}
				}
			}
		}
		
		return newWorld;
	}
	
	/**
	 * Based on the block return the corresponding Tile
	 * @param block Int representing the block
	 * @return Tile of block
	 * @throws Exception 
	 */
	private static Tile mapIntToTile(int block) throws Exception {
		Tile tile =  Tile.findNum(block);
		if(tile == null)
			tile = Tile.FLOOR;
		else if (tile.equals(Tile.KEY) || tile.isMovable())
			tile = Tile.FLOOR;
		return tile;
	}
}
