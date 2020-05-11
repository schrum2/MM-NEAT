package me.jakerg.rougelike;

import java.awt.Color;

/**
 * This class abstracts the items that are present in the 
 * levels for the Rogue-like 
 *
 */
public abstract class Item {
	protected World world; // A dungeon to be created with get and set methods 
    public World getWorld() { return world; }
    public void setWorld(World w) { world = w; }

    // X and Y coordinates
    public int x;
    public int y;

    protected char glyph; //Corresponds to a symbol for a specific tile 
    public char glyph() { return glyph; }

    protected Color color; //corresponds to the color of an item
    public Color color() { return color; }
    
    protected boolean pickupable; // tells player if they can pick up the item or not
    public boolean isPickupable() { return pickupable; }
    
    protected boolean removable; // tells player if they can remove the item or not
    public boolean isRemovable() { return removable; }
    
    /**
     * Default constructor for an item
     * @param world 
     */
    public Item(World world) {
    	this.world = world;
    }
    
    /**
     * Constructor for Specific items 
     * @param world 
     * @param glpyh Describes type of tile to be placed at that coordinate
     * @param color Color of tile
     * @param x X coordinate of tile 
     * @param y Y coordinate of tile 
     */
    public Item(World world, char glpyh, Color color, int x, int y) {
    	this.world = world;
    	this.glyph = glpyh;
    	this.color = color;
    	this.x = x;
    	this.y = y;
    	this.removable = true;
    }
    
    /**
     * Used to update the item if needed 
     */
    public void update() {}
    
    
    public abstract void onPickup(Creature creature);
    
}
