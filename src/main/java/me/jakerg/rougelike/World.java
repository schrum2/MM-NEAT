package me.jakerg.rougelike;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import asciiPanel.AsciiPanel;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Class to represent a room
 * @author gutierr8
 *
 */
public class World {
	private Tile[][] tiles; // 2D list of tiles to render
	private List<Creature> creatures; // A list of creatures
	private List<Item> items;
	private int width;
	private int height;
	private DungeonBuilder db;
	
	private boolean enemyRoom;
	public boolean isEnemyRoom() { return enemyRoom; };
	public void setEnemyRoom(boolean b) { enemyRoom = b; };
	
	private boolean locked = false;
	public boolean locked()	{ return locked; }
	
	/**
	 * Must initialize World with tiles
	 * @param tiles 2D tiles of world
	 */
	public World(Tile[][] tiles) {
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.creatures = new LinkedList<>();
		this.items = new LinkedList<>();
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Get the tile at coordinations
	 * @param x x position of tile
	 * @param y y position of tile
	 * @return tile within tiles or our of bounds
	 */
	public Tile tile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.BOUNDS;
		else
			return tiles[x][y];
	}
	
	/**
	 * Get the glyph of the tile at coords
	 * @param x x position of glyph
	 * @param y y position of glyph
	 * @return character representation of tile at coords
	 */
	public char glyph(int x, int y) {
		return tile(x, y).getGlyph();
	}
	
	/**
	 * Get the color of the tile at coords
	 * @param x x position of color
	 * @param y y position of color
	 * @return color representation of tile at coords
	 */
	public Color color(int x, int y) {
		return tile(x, y).getColor();
	}

	/**
	 * Called from a creature, dig if we can at coords
	 * @param x x position to dig
	 * @param y y position to dig
	 */
	public void dig(int x, int y) {
		if(tile(x, y).isDiggable())
			tiles[x][y] = Tile.FLOOR;
		
	}
	
	public boolean move(int x, int y, Move creature) {
		if(tile(x, y).isMovable() && tile(x, y).getDirection().equals(creature)) {
			Tile move = tile(x, y);
			dig(x, y);
			Point p = move.getDirection().getPoint();
			x += p.x;
			y += p.y;
			tiles[x][y] = Tile.WATER;
			return true;
		}
		return false;
	}
	
	/**
	 * Place a bomb tile at coords
	 * @param x X coord to place bomb
	 * @param y Y coord
	 */
	public boolean placeBomb(int x, int y) {
		if(item(x, y) != null) return false;
		System.out.println(tile(x, y));
		if(tile(x, y).isBombable()) {
			items.add(new Bomb(this, 'b', AsciiPanel.white, x, y, 4, 5));
			return true;
		}
			
		return false;
	}
	
	public void addItem(Item item) {
		if(item(item.x, item.y) == null) {
			items.add(item);
		}
		
	}
	
	/**
	 * Set the tiles of the world
	 * @param tiles
	 */
	public void setNewTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	/**
	 * Get the creature at coords
	 * @param x X position to look at
	 * @param y Y position to look at
	 * @return Creature if there's one at coords otherwise null
	 */
	public Creature creature(int x, int y) {
		for(Creature c : creatures)
			if(c.x == x && c.y == y) 
				return c;
		
		return null;
	}
	
	/**
	 * Add a creature to the list and set coords
	 * @param x X position of creature
	 * @param y Y position of creature
	 * @param c Creature to add
	 */
	public void addCreatureAt(int x, int y, Creature c) {
		creatures.add(c);
		c.x = x;
		c.y = y;
	}
	
	public void addCreature(Creature c) {
		creatures.add(c);
	}
	
	/**
	 * Remove creature
	 * @param other Creature to remove
	 */
	public void remove(Creature other) {
		creatures.remove(other);
	}
	
	/**
	 * Get the item at x and y location
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return
	 */
	public Item item(int x, int y) {
		for(Item i : items)
			if(i.x == x && i.y == y) 
				return i;
		
		return null;
	}
	
	public void removeItem(Item i) {
		items.remove(i);
	}
	
	/**
	 * Update the creatures (move around)
	 */
	public void update() {		
		for(Creature c : creatures) {
			c.update();
			if(Parameters.parameters != null && Parameters.parameters.booleanParameter("rogueLikeDebugMode"))
				System.out.println(c.glyph() + "'s health : " + c.hp());
		}
		
		creatures.removeIf(c -> c.hp() < 1);
		
		for(Item i : items) {
			if(Parameters.parameters != null && Parameters.parameters.booleanParameter("rogueLikeDebugMode"))
				System.out.println("Updating item : " + i.glyph + " at (" + i.x + ", " + i.y + ")" );
			i.update();
		}

		
		checkToUnlock();
	}
	
	public Creature getPlayer() {
		for(Creature c : creatures) {
			if(c.isPlayer())
				return c;
		}
		
		return null;
	}

	/**
	 * Check to unlocked the room if the room is locked
	 */
	private void checkToUnlock() {
		if(!hasEnemies()) {
			unlockRoom();
			locked = false;
		}
	}

	/**
	 * Go through and replace tiles that are locked doors with unlocked doors
	 */
	private void unlockRoom() {
		for(int y = 0; y < tiles.length; y++)
			for(int x = 0; x < tiles[y].length; x++)
				if(tiles[y][x].equals(Tile.SOFT_LOCK_DOOR))
					tiles[y][x] = Tile.DOOR;
	}

	/**
	 * Function to add a creature at a random location (useless for dungone)
	 * @param creature
	 */
	public void addAtEmptyLocation(Creature creature){
	    int x;
	    int y;

	    // Check random coordinations until a coord is ground
	    do {
	        x = RandomNumbers.randomGenerator.nextInt(width);
	        y = RandomNumbers.randomGenerator.nextInt(height);
	    }
	    while (!tile(x,y).isGround());

	    creature.x = x;
	    creature.y = y;
	}

	/**
	 * Bomb the location, essentially set it to a floor
	 * @param wx World X
	 * @param wy World Y
	 */
	public void bomb(int wx, int wy) {
		changeToDoor(wx, wy, Tile.HIDDEN);
	}
	
	public void unlockDoors(int wx, int wy) {
		changeToDoor(wx, wy, Tile.LOCKED_DOOR);
	}
	
	private void changeToDoor(int wx, int wy, Tile t) {
		if(tile(wx, wy).equals(t)) {
			
			tiles[wx][wy] = Tile.DOOR;
			
			// Recursively unlock other doors
			changeToDoor(wx + 1, wy, t);
			changeToDoor(wx, wy + 1, t);
			changeToDoor(wx - 1, wy, t);
			changeToDoor(wx, wy - 1, t);
		}
	}

	public DungeonBuilder getDb() {
		return db;
	}

	public void setDb(DungeonBuilder db) {
		this.db = db;
	}

	/**
	 * Fully unlock doors (both hidden and locked) around x and y
	 * @param x X world coordinates
	 * @param y Y world coordinates
	 */
	public void fullUnlock(int x, int y) {
		changeToDoor(x + 1, y, Tile.LOCKED_DOOR);
		changeToDoor(x, y + 1, Tile.LOCKED_DOOR);
		changeToDoor(x - 1, y, Tile.LOCKED_DOOR);
		changeToDoor(x, y - 1, Tile.LOCKED_DOOR);
		
		changeToDoor(x + 1, y, Tile.HIDDEN);
		changeToDoor(x, y + 1, Tile.HIDDEN);
		changeToDoor(x - 1, y, Tile.HIDDEN);
		changeToDoor(x, y - 1, Tile.HIDDEN);
		
		// Only unlock puzzle doors if the room does not contain a locked puzzle block.
		// For example, if the flow of the level assures a room with a puzzle block must
		// be encountered before the following room, then that following room may not contain
		// a puzzle block and thus should be unlocked upon entry. However, if there are
		// puzzle blocks in both rooms on either side of a door, then it should only
		// be unlocked moving in one direction, until the puzzle block on the other side
		// is also pushed.
		if(!containsLockedPuzzleBlock()) {
			changeToDoor(x + 1, y, Tile.PUZZLE_LOCKED);
			changeToDoor(x, y + 1, Tile.PUZZLE_LOCKED);
			changeToDoor(x - 1, y, Tile.PUZZLE_LOCKED);
			changeToDoor(x, y - 1, Tile.PUZZLE_LOCKED);
		}
	}
	
	public boolean containsLockedPuzzleBlock() {
		for(Item i : items) {
			if(i instanceof MovableBlock) {
				MovableBlock mb = (MovableBlock) i;
				if(mb.stillLocked()) return true;
			}
		}
		return false;
	}

	/**
	 * Drop the specified item in the world
	 * @param i Item to drop
	 */
	public void dropItem(Item i) {
		items.add(i);
	}
	
	/*
	 * Check to lock the room, if the room has more than one enemy
	 */
	public void checkToLock() {
//		if(hasEnemies()) {
//			lockRoom();
//			locked = true;
//		}
	}

	/**
	 * Go through and replaced unlocked doors and hidden doors with locked doors
	 */
//	private void lockRoom() {
//		for(int y = 0; y < tiles.length; y++)
//			for(int x = 0; x < tiles[y].length; x++)
//				if(tiles[y][x].equals(Tile.DOOR) || tiles[y][x].equals(Tile.HIDDEN))
//					tiles[y][x] = Tile.LOCKED_DOOR;
//			
//	}

	/**
	 * Check if there are enemy creatures in the room
	 * @return True if there are enemies, false if not
	 */
	public boolean hasEnemies() {
		for(Creature c : creatures)
			if(c.glyph() == 'e')
				return true;
		
		return false;
			
	}

	/**
	 * Force the key to showup on the world, used for debugging purposes
	 */
	public void forceKey() {
		for(Item i : items) {
			if(i instanceof Key) {
				((Key) i).showKey();
				return;
			}
		}
	}

	/**
	 * Change all puzzle locked doors to unlocked doors
	 */
	public void unlockPuzzle() {
		for(int y = 0; y < tiles.length; y++) {
			for(int x = 0; x < tiles[y].length; x++) {
				if(tiles[y][x].equals(Tile.PUZZLE_LOCKED))
					tiles[y][x] = Tile.DOOR;
			}
		}
	}
	
	/**
	 * Respawn enemies in room if player has no bombs and the room has enemies before
	 * @param player Player for the enemy to reference to
	 * @param log Log to call doAction
	 */
	public void respawnEnemies(Creature player, Log log) {
		System.out.println("Attempting to respawn enemies...");
		if(isEnemyRoom() && !hasEnemies()) {
			CreatureFactory cf = new CreatureFactory(this, log);
			int numEnemies = RandomNumbers.randomGenerator.nextInt(3) + 1;
			for(int i = 0; i < numEnemies; i++) {
				System.out.println("Respawning");
				int x, y;
				
				do {
					x = (int) RandomNumbers.boundedRandom(0, width);
					y = (int) RandomNumbers.boundedRandom(0, height);
			    }
			    while (!tile(x, y).playerPassable());
				
				cf.newEnemy(x, y, player);
			}
		}
	}
	
	// Remove block where player is standing
	public void removeSpawn() {
		tiles[5][5] = Tile.FLOOR;
	}
}
