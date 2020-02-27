package me.jakerg.rougelike;

import java.awt.Color;

public class Health extends Item{

	public Health(World world, char glpyh, Color color, int x, int y) {
		super(world, glpyh, color, x, y);
		this.pickupable = true;
	}

	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer()) {
			creature.addHP();
			RougelikeApp.PD.heartsCollected++;
		}
			
	}
}
