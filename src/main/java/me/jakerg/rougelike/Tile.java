package me.jakerg.rougelike;

import java.awt.Color;
import asciiPanel.AsciiPanel;
import edu.southwestern.parameters.Parameters;

/**
 * Enumerator to model our tiles with a character representation and a color
 * @author gutierr8
 *
 */
public enum Tile {
	// Refer to Code Page 437 for the number representation of the char
	FLOOR((char)250, AsciiPanel.yellow, 0),
	VISITED('x', AsciiPanel.white, 101),
	PATH('x', AsciiPanel.red, 110), // To solution/Triforce
	WALL((char)219, AsciiPanel.yellow, 1),
	CURRENT((char)219, AsciiPanel.brightYellow, -99),
	EXIT((char)239, AsciiPanel.green, 4),
	DOOR((char)239, AsciiPanel.green, 3),
	WATER((char)177, AsciiPanel.cyan, 5), // this is the 'P' water block thing
	LOCKED_DOOR((char)239, AsciiPanel.red, -5),
	SOFT_LOCK_DOOR((char)239, AsciiPanel.brightBlue, -55),
	HIDDEN((char)178, AsciiPanel.yellow, -7),
	BOUNDS('x', AsciiPanel.brightBlack, -99),
	KEY('k', AsciiPanel.brightYellow, 6),
	TRIFORCE((char)30, AsciiPanel.brightYellow, 8),
	MOVABLE_BLOCK_UP((char)219, AsciiPanel.yellow, 10),
	MOVABLE_BLOCK_DOWN((char)219, AsciiPanel.yellow, 100),
	MOVABLE_BLOCK_LEFT((char)219, AsciiPanel.yellow, 1000),
	MOVABLE_BLOCK_RIGHT((char)219, AsciiPanel.yellow, 10000),
	PUZZLE_LOCKED((char)239, Color.ORANGE, -10);
	
	private char glyph; //char representation of the tile 
	private Color color; //color of tile 
	private int number; //number that identifies tile 
	
	/**
	 * Gets the glyph that describes the type of tile 
	 * @return
	 */
	public char getGlyph() {
		if(this == HIDDEN) {
			if(Parameters.parameters != null && Parameters.parameters.booleanParameter("rogueLikeDebugMode"))
				return glyph;
			else
				return WALL.glyph;
		}
		return glyph;
	}
	
	/**
	 * Gets the color of the tile
	 * @return Color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Gets the number associated with the tile 
	 * @return
	 */
	public int getNum() {
		return number;
	}
	
	/**
	 * Constructor for a tile
	 * @param glyph Glyph describing what type of tile 
	 * @param color COlor of the tile 
	 * @param number Integer number associated with the tile 
	 */
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
		return this == WALL || this == KEY || this.isMovable();
	}

	/**
	 * If a creature can walk on
	 * @return True if it's not a wall and not a bound
	 */
	public boolean isGround() {
		return this != WALL && this != BOUNDS && !this.isDoor();
	}
	
	/**
	 * tells if you are unable to pass through a block 
	 * @return True if it is water or it is a movable block 
	 */
	public boolean isBlock() {
		return this == WATER || this.isMovable();
	}
	
	/**
	 * Tells if the player can pass this tile
	 * @return True if the tile is ground and not blocked, or it is a key or triforce
	 */
	public boolean playerPassable() {
		return this.isGround() && !this.isBlock() || this.isInterest();
	}
	
	/**
	 * Tell if you can pass this tile, or if it is of interest
	 * @return True if floor, a key, or the triforce
	 */
	public boolean isStatePassable() {
		return this == FLOOR || this.isInterest();
	}
	
	/**
	 * Tells whether the tile is a door or not 
	 * @return True if the tile is a door
	 */
	public boolean isDoor() {
		return this == DOOR || this == HIDDEN || this == SOFT_LOCK_DOOR || this == LOCKED_DOOR || this == Tile.PUZZLE_LOCKED;
	}
	
	/**
	 * tells if the item is an item of interest
	 * @return True if the item is a key or triforce 
	 */
	public boolean isInterest() {
		return this == KEY || this == TRIFORCE;
	}
	
	/**
	 * If the tile is an exit
	 * @return True of the tile is EXIT
	 */
	public boolean isExit() {
		return this == EXIT;
	}
	
	/**
	 * Tells if a block is bombable or not 
	 * @return True if the tile is FLOOR, HIDDEN, or WALL
	 */
	public boolean isBombable() {
		return this == FLOOR || this == HIDDEN || this == WALL;
	}
	
	/**
	 * Tells if the tile is a key
	 * @return True if the tile is a key 
	 */
	public boolean isKey() {
		return this == KEY;
	}
	
	/**
	 * Tells whether a block is movable or not 
	 * @return True if you can move the block 
	 */
	public boolean isMovable() {
		return this == MOVABLE_BLOCK_UP || this == MOVABLE_BLOCK_DOWN || this == MOVABLE_BLOCK_LEFT || this == MOVABLE_BLOCK_RIGHT;
	}
	
	/**
	 * Gets the direction that you are traveling 
	 * @return The direction you are moving 
	 */
	public Move getDirection() {
		if(this == MOVABLE_BLOCK_UP)
			return Move.UP;
		else if(this == MOVABLE_BLOCK_DOWN)
			return Move.DOWN;
		else if(this == MOVABLE_BLOCK_LEFT)
			return Move.LEFT;
		else if(this == Tile.MOVABLE_BLOCK_RIGHT)
			return Move.RIGHT;
		else
			return Move.NONE;
	}
	
	/**
	 * Gets the number that represent a specific tile 
	 * @param num Integer representing the tile wanted
	 * @return The type of tile, floor tile if not found
	 */
	public static Tile findNum(int num) {
		for(Tile tile : Tile.values()) {
			if(num == tile.getNum())
				return tile;
		}
		return Tile.FLOOR;
	}

	/**
	 * Gets the type of movable block 
	 * @param d A direction
	 * @return Movable block type, null if not found
	 */
	public static Tile findByMove(Move d) {
		switch(d) {
		case UP:
			return MOVABLE_BLOCK_UP;
		case DOWN:
			return MOVABLE_BLOCK_DOWN;
		case LEFT:
			return MOVABLE_BLOCK_LEFT;
		case RIGHT:
			return MOVABLE_BLOCK_RIGHT;
		default:
			return null;	
		}
	}

}
