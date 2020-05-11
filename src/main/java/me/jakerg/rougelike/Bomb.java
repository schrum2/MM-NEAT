package me.jakerg.rougelike;

import java.awt.Color;

import asciiPanel.AsciiPanel;
import edu.southwestern.util.random.RandomNumbers;

/**
 * This class extends the item class to create bombs to be present in the level 
 * @author kdste
 *
 */
public class Bomb extends Item{

	private int counter; //timer for the bomb, after counter gets to 0 it explodes  
	public int counter() { return counter; }
	public void decrement() { counter--; }
	
	private int attack; //tracks how much damage the bomb does to the opponent 
	public int attack() { return attack; }
	
	
	/**
	 * Default constructor, makes players not be able to pick up the bomb
	 * @param world Represents the level being played 
	 * @param glpyh Specifies that this item is a bomb
	 * @param color Sets Color
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param counter Track the life of the bomb
	 * @param attack Tells how much damage the bomb will do to an enemy 
	 */
	public Bomb(World world, char glpyh, Color color, int x, int y, int counter, int attack) {
		super(world, glpyh, color, x, y);
		this.counter = counter;
		this.attack = attack;
		this.pickupable = false;
	}
	
	/**
	 * Second constructor, allows user to set whether or not they want the bomb to be pickupable 
	 * by adding a parameter to control that boolean value
	 * @param world Represents the level being played 
	 * @param glpyh Specifies that this item is a bomb
	 * @param color Sets Color
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param counter Track the life of the bomb
	 * @param attack Tells how much damage the bomb will do to an enemy
	 * @param pickupable Allows bomb to be picked up, or not
	 */
	public Bomb(World world, char glpyh, Color color, int x, int y, int counter, int attack, boolean pickupable) {
		super(world, glpyh, color, x, y);
		this.counter = counter;
		this.attack = attack;
		this.pickupable = pickupable;
	}
	
	/**
	 * Updates the bomb to see if it has exploded yet
	 * Also adds attack damage to an enemy if the bomb explodes near them. 
	 */
	public void update() {
		if(isPickupable()) return;
		decrement();
		if(counter == 1) color = AsciiPanel.red;
		else if(counter == 0) {
			RougelikeApp.PD.bombsUsed++;
			world.removeItem(this);
			for(int wx = x - 1; wx < x + 1; wx++) {
				for(int wy = y - 1; wy < y + 1; wy++) {
					Creature c = world.creature(wx, wy);
					if (c != null) attack(c);
				}
			}
			world.bomb(x, y);
		}
	}
	
	/**
	 * Subtracts attack points from enemy when the player is successful in throwing a bomb
	 * @param other An enemy creature that is being targeted by a bomb
	 */
	public void attack(Creature other) {
		System.out.println(this.glyph + " attacking " + other.glyph());
        int amount = Math.max(0, attack - other.defenseValue()); // Get whatever is higher: 0 or the total attack value, dont want negative attack
        
        amount = RandomNumbers.randomGenerator.nextInt(amount) + 1; // Add randomness to ammount
        other.doAction("Bomb did " + amount + " damage to " + other.glyph());
        other.modifyHp(-amount); // Modify hp of the the other creature
	}
	
	/**
	 * The player is now holding the bomb when this method is called 
	 */
	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer()) {
			RougelikeApp.PD.bombsCollected++;
			creature.addBomb();
		}
			
		
	}
	
	

}
