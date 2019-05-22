package me.jakerg.rougelike;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {
	FLOOR((char)250, AsciiPanel.yellow),
	WALL((char)219, AsciiPanel.yellow),
	EXIT((char)239, AsciiPanel.green),
	DOOR((char)194, AsciiPanel.brightCyan),
	BOUNDS('x', AsciiPanel.brightBlack);
	
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

	public boolean isDiggable() {
		return this == WALL;
	}

	public boolean isGround() {
		return this != WALL && this != BOUNDS;
	}
	
	public boolean isExit() {
		return this == EXIT;
	}
}
