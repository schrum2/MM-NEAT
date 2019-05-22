package me.jakerg.rougelike;

import java.util.HashMap;
import java.util.Map.Entry;

import edu.southwestern.tasks.gvgai.zelda.level.*;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;

public class DungeonBuilder {
	private Tile[][] tiles;
	private Dungeon dungeon;
	private HashMap<String, Tile[][]> levelTiles;
	
	
	public DungeonBuilder(Dungeon dungeon) {
	    this.dungeon = dungeon;
	    createTiles();
	}
	
	private void createTiles() {
		levelTiles = new HashMap<>();
		HashMap<String, Node> map = dungeon.getLevels();
		for(Entry<String, Node> entry : map.entrySet()) {
			String name = entry.getKey();
			Level level = entry.getValue().level;
			Tile[][] tileSet = TileUtil.listToTile(level.getLevel());
			levelTiles.put(name, tileSet);
		}
	}

	public World build() {
	    return new World(tiles);
	}
	
	public DungeonBuilder getLevel() {
		this.tiles = getCurrentTiles();
		return this;
	}
	
	public Tile[][] getCurrentTiles(){
		String n = dungeon.getCurrentlevel().name;
		return levelTiles.get(n);
	}
}
