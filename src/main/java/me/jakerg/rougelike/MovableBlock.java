package me.jakerg.rougelike;

import java.awt.Point;

/**
 * This class extends the item class to allow for movable blocks to be created for levels 
 * @author kdste
 *
 */
public class MovableBlock extends Item{

	private Move move; //creates a new move object based on the enumeration of Move from the Move class
	private boolean moved; //did the item move? yes or no? 
	
	/**
	 * Constructor for a movable block 
	 * @param world Specifies level 
	 * @param p Specifies the point where the clock is 
	 * @param move Tracks the movement of the block 
	 */
	public MovableBlock(World world, Point p, Move move) {
		super(world); //calls default constructor from the item class
		this.x = p.x; 
		this.y = p.y;
		this.glyph = Tile.MOVABLE_BLOCK_UP.getGlyph(); //gets type of block 
		this.color = Tile.MOVABLE_BLOCK_UP.getColor(); //gets color of block 
		this.move = move;
		moved = false; //false until the player moves the box 
		this.removable = false; //movable blocks are not removable 
	}
	
	/**
	 * If the block has not moved, then it is in the locked position
	 * @return Whether block is locked
	 */
	public boolean stillLocked() {
		return !moved;
	}

	/**
	 * Allows player to move the block
	 * Updates and tracks the movement of the block after the player moves it 
	 */
	@Override
	public void onPickup(Creature creature) {
		if(creature.isPlayer() && creature.getLastDirection().equals(move) && !moved) {
			System.out.println("Moved block");
			this.x = move.getPoint().x + x;
			this.y = move.getPoint().y + y;
			this.world.unlockPuzzle();
			moved = true;
		}
	}

}
