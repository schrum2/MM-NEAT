package me.jakerg.rougelike;

import java.awt.Point;

public class MovableBlock extends Item{

	private Move move;
	
	public MovableBlock(World world, Point p, Move move) {
		super(world);
		this.x = p.x;
		this.y = p.y;
		this.glyph = Tile.MOVABLE_BLOCK_UP.getGlyph();
		this.color = Tile.MOVABLE_BLOCK_UP.getColor();
		this.move = move;
	}

	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer() && creature.getLastDirection().equals(move)) {
			System.out.println("Moved block");
			this.x = move.getPoint().x + x;
			this.y = move.getPoint().y + y;
			this.world.unlockPuzzle();
		}
	}

}
