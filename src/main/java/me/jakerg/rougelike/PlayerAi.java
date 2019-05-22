package me.jakerg.rougelike;

/**
 * Player AI from starter code
 * @author gutierr8
 *
 */
public class PlayerAi extends CreatureAi{

	public PlayerAi(Creature creature) {
		super(creature);
		// TODO Auto-generated constructor stub
	}
	
	public void onEnter(int x, int y, Tile tile) {
		if(tile.isGround()) {
			creature.x = x;
			creature.y = y;
		} else if (tile.isDiggable()) {
			creature.dig(x, y);
		}
		System.out.println("Trying to move");
	}

}
