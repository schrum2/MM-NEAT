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
	FLOOR((char)250, AsciiPanel.yellow),
	WALL((char)219, AsciiPanel.yellow),
	CURRENT((char)219, AsciiPanel.brightYellow),
	EXIT((char)239, AsciiPanel.green),
	DOOR((char)239, AsciiPanel.green),
	LOCKED_DOOR((char)239, AsciiPanel.red),
	HIDDEN((char)178, AsciiPanel.yellow),
	BOUNDS('x', AsciiPanel.brightBlack),
	KEY('k', AsciiPanel.brightYellow);
	
	private char glyph;
	private Color color;
	
	public char getGlyph() {
		return glyph;
	}
	
	public Color getColor() {
		return color;
	}
	
	Tile(char glyph, Color color){
		this.glyph = glyph;
		this.color = color;
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
		return this != WALL && this != BOUNDS && this != LOCKED_DOOR && this != HIDDEN;
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
}
