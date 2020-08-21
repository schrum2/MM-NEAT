package me.jakerg.rougelike;

import java.awt.Color;

/**
 * This class extends the item class to create Health items to be displayed in the level
 * @author kdste
 *
 */

public class Health extends Item{

	/**
	 * Constructor for a new instance of a health item 
	 * Sets health items to always be able to be picked up by the player 
	 * @param world Specifies the world the health item is placed in 
	 * @param glpyh Specifies that the item is a health item 
	 * @param color Describes color of the tile
	 * @param x X coordinate that the item will be placed
	 * @param y Y coordinate that the item will be placed
	 */
	public Health(World world, char glpyh, Color color, int x, int y) {
		super(world, glpyh, color, x, y); //calls the constructor from the item class that is extended in this class
		this.pickupable = true; 
	}

	/**
	 * Adds for health points to a player that picks up a health item
	 * Does not add health points if the creature is not a player
	 */
	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer()) {
			creature.addHP();
			RougelikeApp.PD.heartsCollected++;
		}
			
	}
}
