package me.jakerg.rougelike;

import java.awt.Color;
import java.awt.Point;
import asciiPanel.AsciiPanel;
import edu.southwestern.util.random.RandomNumbers;

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

		for(int i = 0; i < creature.maxHp(); i++) {
			Color c = AsciiPanel.brightRed;
			if(i > creature.hp())
				c = AsciiPanel.brightBlack;
			
			terminal.write((char) 3, oX + i, oY + 3, c);
			
		}
		
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
			item.onPickup(creature);
			if(item.removable)
				item.world.removeItem(item);
			if(item instanceof MovableBlock)
				return;
		}

		
		if(tile.playerPassable() ) {
			creature.x = x;
			creature.y = y;
		} 
		if(tile.equals(Tile.DOOR) && !creature.getWorld().locked()) {
			Point exitPoint = new Point(x, y);
//			 Get the point to move to based on where the player went in from
			if(RougelikeApp.DEBUG)
				System.out.println("Exiting at " + exitPoint);
			creature.getWorld().remove(creature);
			Point p = creature.getDungeon().getNextNode(exitPoint.toString());
			if(p != null) {
				creature.getDungeonBuilder().getCurrentWorld().fullUnlock(p.x, p.y);
				if(creature.bombs() <= 0 || RandomNumbers.randomCoin(0.4)) {
					creature.getDungeonBuilder().getCurrentWorld().respawnEnemies(creature, creature.log());
				}
				creature.getDungeonBuilder().getCurrentWorld().addCreature(creature);
				if(RougelikeApp.DEBUG)
					System.out.println("Starting point :" + p);
				creature.x  = p.x;
				creature.y = p.y;
				creature.setDirection(Move.NONE);
			}
		}
		if(tile.equals(Tile.LOCKED_DOOR) && !creature.getWorld().locked()) {
			
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
