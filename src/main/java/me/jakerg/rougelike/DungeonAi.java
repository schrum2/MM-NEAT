package me.jakerg.rougelike;

import java.awt.Point;
import java.util.Map.Entry;

import asciiPanel.AsciiPanel;
import edu.southwestern.util.datastructures.Pair;

/**
 * Dungeon creature that's controllable as a player
 * @author gutierr8
 *
 */
public class DungeonAi extends CreatureAi{

	public DungeonAi(Creature creature) {
		super(creature);

	}
	
	/**
	 * Display stats to screen
	 */
	public void display(AsciiPanel terminal, int oX, int oY) {
		terminal.write("Keys x" + creature.keys(), oX, oY);
		terminal.write("Bombs x" + creature.bombs(), oX, oY + 1); 

		for(int i = 0; i < creature.hp(); i++)
			terminal.write((char) 3, oX + i, oY + 3, AsciiPanel.brightRed);
	}
	
	/**
	 * Whenever a character is moved
	 */
	public void onEnter(int x, int y, Tile tile) {		
		// If the tile the character is trying to move to group, then move character at point
		if(tile.isGround()) {
			creature.x = x;
			creature.y = y;
		} 
		if(tile.isExit()) {
			double cX = creature.x;
			double cY = creature.y;
			
			// Get the exit point and convert it into something the dungeon can understand
			if(cX == 10.0 && cY == 8.0) {
				cX = 450.;
				cY = 400.;
			} else if(cX == 5.0 && cY == 15.0) {
				cX = 250.;
				cY = 700.;
			} else if(cX == 5.0 && cY == 0.0) {
				cX = 250.;
				cY = 50.;
			} else if (cX == 0. && cY == 8.) {
				cX = 50.;
				cY = 400.;
			}
			
			// Convert to string
			String exitPoint = cX + " : " + cY;
			// Get the point to move to based on where the player went in from
			creature.getWorld().remove(this.creature);
			Point p = creature.getDungeon().getNextNode(exitPoint);
			
			// The way the points were made is reversed so GVG-AI could use them properly, so we NEED to reverse them
			creature.x  = p.y;
			creature.y = p.x;
			creature.setDirection(Move.NONE);
		}
	}


}
