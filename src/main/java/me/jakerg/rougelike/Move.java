package me.jakerg.rougelike;

/**
 * Enumerator to keep track of the latest direction of the player
 * @author gutierr8
 *
 */
public enum Move {
	UP("UP"), DOWN("DOWN"), LEFT("LEFT"), RIGHT("RIGHT"), NONE("NONE");
	
	private String direction;
	private String direction() { return this.direction; };
	
	Move (String direction) {
		this.direction = direction;
	}
}
