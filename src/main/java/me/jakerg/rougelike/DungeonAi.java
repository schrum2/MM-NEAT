package me.jakerg.rougelike;

import java.awt.Point;
import asciiPanel.AsciiPanel;

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
		
		terminal.write("Items", oX, oY + 5);
		int i = 0;
		for(Item item: creature.getItems()) {
			terminal.write(item.glyph(), oX + i, oY + 6, item.color());
			i += 2;
		}
			
	}
	
	/**
	 * Whenever a character is moved
	 */
	public void onEnter(int x, int y, Tile tile) {		
		// If the tile the character is trying to move to group, then move character at point
		Item item = creature.getWorld().item(x, y);
		if(item != null) {
			creature.addItem(item);
			creature.getWorld().removeItem(item);
		}

		
		if(tile.playerPassable()) {
			creature.x = x;
			creature.y = y;
		} 
		if(tile.equals(Tile.DOOR)) {
			Point exitPoint = new Point(creature.x, creature.y);
//			 Get the point to move to based on where the player went in from
			System.out.println("Exiting at " + exitPoint);
			creature.getWorld().remove(this.creature);
			Point p = creature.getDungeon().getNextNode(exitPoint.toString());
			creature.getDungeonBuilder().getCurrentWorld().fullUnlock(p.x, p.y);
			System.out.println("Starting point :" + p);
			creature.x  = p.x;
			creature.y = p.y;
			creature.setDirection(Move.NONE);
		}
		if(tile.equals(Tile.LOCKED_DOOR)) {
			
			if(creature.keys() > 0) {
				creature.numKeys--;
				creature.getWorld().unlockDoors(x, y);
				creature.doAction("You unlocked a door");
			} else
				creature.doAction("You need a key to open the door");
		}
		if(tile.isKey()){
			creature.x = x;
			creature.y = y;
			
			creature.numKeys++;
			creature.getWorld().dig(x, y);
			creature.doAction("You picked up a key");
		}
		if(tile.equals(Tile.TRIFORCE)) {
			creature.setWin(true);
		}
		if(tile.equals(Tile.BLOCK) && creature.hasItem('#') && !creature.getWorld().tile(creature.x, creature.y).equals(Tile.BLOCK)) {
			creature.x = x;
			creature.y = y;
		}
	}


}
