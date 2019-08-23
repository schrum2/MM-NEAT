package me.jakerg.rougelike;

/**
 * Basic enemy class that just wanders
 * @author gutierr8
 *
 */
public class EnemyAi extends CreatureAi{
	private Creature player;

	public EnemyAi(Creature creature) {
		super(creature);
		player = null;
	}
	
	public EnemyAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}
	
	public void setPlayer(Creature player) {
		this.player = player;
	}
	
	
	public void onUpdate() {
		if(player != null && playerInRange(4)) moveTowardsPlayer();
		else wander(); // Just wander once
	}
	
	private void moveTowardsPlayer() {
		System.out.println("Moving towards player at (" + player.x + ", " + player.y + ")");
		int dX = player.x - creature.x;
		int dY = player.y - creature.y;
		System.out.println("\t dX :" + dX + ", dY: " + dY);
		
		if(dX == 0 && dY == 0) {
			creature.setHP(0);
			creature.doAction("You ate " + creature.glyph());
			return;
		}
		
		int mX = 0;
		int mY = 0;
		if(dX > 0) mX = 1;
		else if(dX == 0) mX = 0;
		else mX = -1;
		
		if(mX == 0) {
			if(dY > 0) mY = 1;
			else if(dY == 0) mY = 0;
			else mY = -1;
		}

		
		creature.moveBy(mX, mY);
	}

	private boolean playerInRange(double range) {
		int dX = player.x - creature.x;
		int dY = player.y - creature.y;
		
		double r = Math.sqrt(dX * dX + dY * dY);
		return r < range;
	}

}
