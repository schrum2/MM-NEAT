package me.jakerg.rougelike;

import java.awt.Color;

import asciiPanel.AsciiPanel;

/**
 * This class extends the item class in order to create ladder items for a level 
 * @author kdste
 *
 */
public class Ladder extends Item{

	private static char glyph = '#'; //a ladder will always be specified by this glyph 
	private static Color color = AsciiPanel.brightCyan;
	public static final int INT_CODE = -6; //Solves magic number issue for this class 
	
	/**
	 * Constructor to allow ladders to be built
	 * The ladder can be picked up by the player
	 * @param world Specifies level it is placed in 
	 * @param x X coordinate of ladder 
	 * @param y Y coordinate of ladder
	 */
	public Ladder(World world, int x, int y) {
		super(world, glyph, color, x, y); //calls item class constructor 
		this.pickupable = true; 
	}

	/**
	 * Allows player to pick up the ladder
	 */
	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer())
			creature.addItem(this);
	}

}
