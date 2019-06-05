package me.jakerg.rougelike;

import java.awt.Color;
import asciiPanel.AsciiPanel;

/**
 * Enumerator to model our tiles with a character representation and a color
 * @author gutierr8
 *
 */
public enum Tile {
	// Refer to Code Page 437 for the number representation of the char
	FLOOR((char)250, AsciiPanel.yellow, 0),
	WALL((char)219, AsciiPanel.yellow, 1),
	CURRENT((char)219, AsciiPanel.brightYellow, -99),
	EXIT((char)239, AsciiPanel.green, 4),
	DOOR((char)239, AsciiPanel.green, 3),
	BLOCK((char)177, AsciiPanel.yellow, 5),
	LOCKED_DOOR((char)239, AsciiPanel.red, -5),
	HIDDEN((char)178, AsciiPanel.yellow, -7),
	BOUNDS('x', AsciiPanel.brightBlack, -99),
	KEY('k', AsciiPanel.brightYellow, 6),
	TRIFORCE((char)30, AsciiPanel.brightYellow, 8);
	
	private char glyph;
	private Color color;
	private int number;
	
	public char getGlyph() {
		return glyph;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getNum() {
		return number;
	}
	
	Tile(char glyph, Color color, int number){
		this.glyph = glyph;
		this.color = color;
		this.number = number;
	}

	/**
	 * Only diggable walls
	 * @return True if the tile is a wall
	 */
	public boolean isDiggable() {
		return this == WALL || this == KEY;
	}

	/**
	 * If a creature can walk on
	 * @return True if it's not a wall and not a bound
	 */
	public boolean isGround() {
		return this != WALL && this != BOUNDS && this != HIDDEN && this != LOCKED_DOOR;
	}
	
	public boolean isBlock() {
		return this == BLOCK;
	}
	
	public boolean playerPassable() {
		return this.isGround() && !this.isBlock();
	}
	
	/**
	 * If the tile is an exit
	 * @return True of the tile is EXIT
	 */
	public boolean isExit() {
		return this == EXIT;
	}
	
	public boolean isBombable() {
		return this == HIDDEN;
	}
	
	public boolean isKey() {
		return this == KEY;
	}
	
	public static Tile findNum(int num) {
		if(num == 2) return Tile.FLOOR; //Enemy
		for(Tile tile : Tile.values()) {
			if(num == tile.getNum())
				return tile;
		}
		System.out.println("Couldn't find tile for : " + num);
		return null;
	}

}
