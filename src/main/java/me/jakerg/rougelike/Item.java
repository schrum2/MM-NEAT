package me.jakerg.rougelike;

import java.awt.Color;

public abstract class Item {
	protected World world;
    public World getWorld() { return world; }
    public void setWorld(World w) { world = w; }

    public int x;
    public int y;

    protected char glyph;
    public char glyph() { return glyph; }

    protected Color color;
    public Color color() { return color; }
    
    protected boolean pickupable;
    public boolean isPickupable() { return pickupable; }
    
    public Item(World world) {
    	this.world = world;
    }
    
    public Item(World world, char glpyh, Color color, int x, int y) {
    	this.world = world;
    	this.glyph = glpyh;
    	this.color = color;
    	this.x = x;
    	this.y = y;
    }
    
    public void update() {}
    
    public abstract void onPickup(Creature creature);
    
}
