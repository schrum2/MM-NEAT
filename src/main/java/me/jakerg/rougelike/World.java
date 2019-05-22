package me.jakerg.rougelike;

import java.awt.Color;

public class World {
	private Tile[][] tiles;
	private int width;
	private int height;
	
	public World(Tile[][] tiles) {
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Tile tile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.BOUNDS;
		else
			return tiles[x][y];
	}
	
	public char glyph(int x, int y) {
		return tile(x, y).getGlyph();
	}
	
	public Color color(int x, int y) {
		return tile(x, y).getColor();
	}

	public void dig(int x, int y) {
		if(tile(x, y).isDiggable())
			tiles[x][y] = Tile.FLOOR;
		
	}
	
	public void setNewTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	public void addAtEmptyLocation(Creature creature){
	    int x;
	    int y;

	    do {
	        x = (int)(Math.random() * width);
	        y = (int)(Math.random() * height);
	    }
	    while (!tile(x,y).isGround());

	    creature.x = x;
	    creature.y = y;
	}
}
