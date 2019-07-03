package me.jakerg.rougelike;

import java.awt.Point;

public class Key extends Item{

	public Key(World world, Point p) {
		super(world);
		this.glyph = Tile.FLOOR.getGlyph();
		this.color = Tile.FLOOR.getColor();
		this.x = p.x;
		this.y = p.y;
		this.removable = false;
	}
	
	public void update() {
		System.out.println("Updating key");
		if(this.glyph != Tile.KEY.getGlyph() && !this.world.hasEnemies()) {
			showKey();
		}
	}

	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer() && this.glyph == Tile.KEY.getGlyph()) {
			System.out.println("Picking up the KEEEEEEYYYY");
			creature.addKey();
			this.world.removeItem(this);
		}
			
	}

	public void showKey() {
		this.glyph = Tile.KEY.getGlyph();
		this.color = Tile.KEY.getColor();
	}
}
