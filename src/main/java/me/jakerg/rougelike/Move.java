package me.jakerg.rougelike;

import java.awt.Point;

/**
 * Enumerator to keep track of the latest direction of the player
 * @author gutierr8
 *
 */
public enum Move {
	UP("UP", new Point(0, -1)), 
	DOWN("DOWN", new Point(0, 1)), 
	LEFT("LEFT", new Point(-1, 0)), 
	RIGHT("RIGHT", new Point(1, 0)),
	NONE("NONE", new Point(0, 0));
	
	private String direction;
	public String direction() { return this.direction; };
	
	private Point point;
	public Point point() { return point; }
	
	Move (String direction, Point p) {
		this.direction = direction;
		this.point = p;
	}
	
	public static Move getByString(String s) {
		switch(s) {
		case "UP":
			return UP;
		case "DOWN":
			return DOWN;
		case "LEFT":
			return LEFT;
		case "RIGHT":
			return RIGHT;
		default:
			return NONE;
		}
	}

	public Point getPoint() {
		return point;
	}

	public Move opposite() {
		if(this == UP)
			return DOWN;
		else if(this == DOWN)
			return UP;
		else if(this == LEFT)
			return RIGHT;
		else if(this == RIGHT)
			return LEFT;
		
		return null;
	}
}
