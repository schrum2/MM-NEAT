package me.jakerg.rougelike;

import java.util.List;

public class TileUtil {
	/**
	 * Convert the ints to tiles
	 * @param level 2D list of ints
	 * @return 2D Tile array
	 */
	public static Tile[][] listToTile(List<List<Integer>> level){
		Tile[][] tiles = new Tile[level.get(0).size()][level.size()];
		// Some of the operations are reversed because of how the level was made
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[i].length; j++) {
				tiles[i][j] = mapIntToTile(level.get(j).get(i));
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
	public static World makeWorld(List<List<Integer>> level, Creature player, Log log) {
		Tile[][] tiles = listToTile(level);
		World newWorld = new World(tiles);
		newWorld.addCreature(player);
		CreatureFactory cf = new CreatureFactory(newWorld, log);
		
		// Convert ints to enemies (only 2 in this case)
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[i].length; j++) {
				if(level.get(j).get(i) == 2) {
					cf.newEnemey(i, j, player);
				}
			}
		}
		
		return newWorld;
	}
	
	/**
	 * Based on the block return the corresponding Tile
	 * @param block Int representing the block
	 * @return Tile of block
	 */
	private static Tile mapIntToTile(int block) {
		switch(block) {
		case 0: return Tile.FLOOR;
		case 1: return Tile.WALL;
		case 3: return Tile.DOOR;
		case 4: return Tile.EXIT;
		case 5: System.out.println("Got a hidden door here"); return Tile.LOCKED_DOOR;
		case 6: return Tile.KEY;
		case 7: return Tile.HIDDEN;
		case 8: return Tile.TRIFORCE;
		default: return Tile.FLOOR;
		}
	}
}
