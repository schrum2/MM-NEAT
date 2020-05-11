package me.jakerg.rougelike;

import java.awt.Point;

/**
 * This class extends the item class in order to create key items for levels 
 * @author kdste
 *
 */

public class Key extends Item{

	/**
	 * Default constructor for creating a key item
	 * @param world Specifies the level it is placed in 
	 * @param p Specifies point to place the key in the level
	 */
	public Key(World world, Point p) {
		super(world); //calls default constructor from the item class which only takes a world as a parameter
		this.glyph = Tile.FLOOR.getGlyph(); //gets type of tile the key is on
		this.color = Tile.FLOOR.getColor(); //gets color of tile the key is on
		this.x = p.x; //X coordinate from the point in the parameter
		this.y = p.y; //Y coordinate from the point in the parameter
		this.removable = false; //makes key stationary until picked up 
	}

	/**
	 * Makes teh key item appear, if available, when the key is not already shown
	 * and there are no enemies left in the room
	 */
	public void update() {
		if(this.glyph != Tile.KEY.getGlyph() && !this.world.hasEnemies()) {
			showKey();
		}
	}

	/**
	 * Allows the player to pick up the key and removes the key from the level 
	 */
	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer() && this.glyph == Tile.KEY.getGlyph()) {
			creature.addKey();
			RougelikeApp.PD.keysCollected++;
			this.world.removeItem(this);
		}
			
	}

	/**
	 * Uncovers the key after the conditions are met
	 * conditions: not already a key and no enemies left in the room 
	 */
	public void showKey() {
		this.glyph = Tile.KEY.getGlyph();
		this.color = Tile.KEY.getColor();
	}
}
