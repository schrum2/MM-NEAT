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
		if(tile.equals(Tile.DOOR)) {
			double cX = creature.x;
			double cY = creature.y;
			Point exitPoint = new Point(creature.x, creature.y);
			System.out.println("Exit point is " + exitPoint);
			
			String startingPoint = creature.getDungeon().getCurrentlevel().adjacency.get(exitPoint.toString()).t2.toString();
			System.out.println("Starting point would be ... " + startingPoint);
			
//			 Get the point to move to based on where the player went in from
			creature.getWorld().remove(this.creature);
			Point p = creature.getDungeon().getNextNode(exitPoint.toString());
			
			// The way the points were made is reversed so GVG-AI could use them properly, so we NEED to reverse them
			creature.x  = p.x;
			creature.y = p.y;
			creature.setDirection(Move.NONE);
		}
	}


}
