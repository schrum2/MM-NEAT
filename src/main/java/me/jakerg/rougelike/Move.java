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

	public Point getPoint() {
		return point;
	}
}
