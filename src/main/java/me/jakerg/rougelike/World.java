package me.jakerg.rougelike;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import asciiPanel.AsciiPanel;

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
	
	/**
	 * Place a bomb tile at coords
	 * @param x X coord to place bomb
	 * @param y Y coord
	 */
	public boolean placeBomb(int x, int y) {
		if(item(x, y) != null) return false;
		if(tile(x, y).isBombable()) {
			items.add(new Bomb(this, 'b', AsciiPanel.white, x, y, 4, 5));
			return true;
		}
			
		return false;
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
		for(Item i : items)
			i.update();
		
		for(Creature c : creatures)
			c.update();		
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
	        x = (int)(Math.random() * width);
	        y = (int)(Math.random() * height);
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
}
