package me.jakerg.rougelike;

import java.util.HashMap;
import java.util.Map.Entry;

import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.*;

/**
 * Dungeon builder keeps track of the different rooms of the dungeon
 * @author gutierr8
 *
 */
public class DungeonBuilder {
	private Tile[][] tiles;
	private Dungeon dungeon;
	private Creature player;
	private Log log;
	private HashMap<String, World> levelWorlds;
	
	/**
	 * Create a new dungeon builder and convert the worlds
	 * @param dungeon
	 * @param player 
	 */
	public DungeonBuilder(Dungeon dungeon, Creature player, Log log) {
	    this.dungeon = dungeon;
	    this.player = player;
	    this.log = log;
	    createWorlds();
	}
	
	/**
	 * Create the worlds into something that we can understand
	 */
	private void createWorlds() {
		levelWorlds = new HashMap<>();
		HashMap<String, Node> map = dungeon.getLevels();
		for(Entry<String, Node> entry : map.entrySet()) {
			String name = entry.getKey();
			Level level = entry.getValue().level;
			World w = TileUtil.makeWorld(level.getLevel(), player, log);
			levelWorlds.put(name, w);
		}
	}

	/**
	 * Get the world based on the name
	 * @param n name of world
	 * @return World with given name
	 */
	public World getWorld(String n) {
		return levelWorlds.get(n);
	}
	
	/**
	 * Get the current world (where the player is)
	 * @return World where the player is
	 */
	public World getCurrentWorld() {
		String n = dungeon.getCurrentlevel().name;
		return getWorld(n);
	}
	
	/**
	 * Set the world
	 * @param n Name of world
	 * @param w World instance
	 */
	public void setWorld(String n, World w) {
		levelWorlds.put(n, w);
	}
}
