package me.jakerg.rougelike;

import java.util.List;

public class TileUtil {
	public static Tile[][] listToTile(List<List<Integer>> level){
		Tile[][] tiles = new Tile[level.get(0).size()][level.size()];
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[i].length; j++) {
				tiles[i][j] = mapIntToTile(level.get(j).get(i));
			}
		}
		return tiles;
	}
	
	private static Tile mapIntToTile(int block) {
		switch(block) {
		case 0: return Tile.FLOOR;
		case 1: return Tile.WALL;
		case 3: return Tile.DOOR;
		case 4: return Tile.EXIT;
		default: return Tile.FLOOR;
		}
	}
}
