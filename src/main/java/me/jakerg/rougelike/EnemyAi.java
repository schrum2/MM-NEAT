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
		double dX = player.x - creature.x;
		double dY = player.y - creature.y;
		
		if(dX == 0 && dY == 0) {
			creature.setHP(0);
			creature.doAction("You ate " + creature.glyph());
			return;
		}
		
		// Enemy should only move by 1 space
		int mX = (int) Math.signum(dX);
		int mY = (int) Math.signum(dY);

		creature.moveBy(mX, mY);
	}

	private boolean playerInRange(double range) {
		int dX = player.x - creature.x;
		int dY = player.y - creature.y;
		
		double r = Math.sqrt(dX * dX + dY * dY);
		return r < range;
	}

}
