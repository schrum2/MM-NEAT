package me.jakerg.rougelike;

import java.awt.Color;

import asciiPanel.AsciiPanel;

public class Bomb extends Item{

	private int counter;
	public int counter() { return counter; }
	public void decrement() { counter--; }
	
	private int attack;
	public int attack() { return attack; }
	
	
	
	public Bomb(World world, char glpyh, Color color, int x, int y, int counter, int attack) {
		super(world, glpyh, color, x, y);
		this.counter = counter;
		this.attack = attack;
		this.pickupable = false;
	}
	
	public Bomb(World world, char glpyh, Color color, int x, int y, int counter, int attack, boolean pickupable) {
		super(world, glpyh, color, x, y);
		this.counter = counter;
		this.attack = attack;
		this.pickupable = pickupable;
	}
	
	public void update() {
		if(isPickupable()) return;
		decrement();
		if(counter == 1) color = AsciiPanel.red;
		else if(counter == 0) {
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
	
	public void attack(Creature other) {
		System.out.println(this.glyph + " attacking " + other.glyph());
        int amount = Math.max(0, attack - other.defenseValue()); // Get whatever is higher: 0 or the total attack value, dont want negative attack
        
        amount = (int)(Math.random() * amount) + 1; // Add randomness to ammount
        other.doAction("Bomb did " + amount + " damage to " + other.glyph());
        other.modifyHp(-amount); // Modify hp of the the other creature
	}
	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer())
			creature.addBomb();
		
	}
	
	

}
