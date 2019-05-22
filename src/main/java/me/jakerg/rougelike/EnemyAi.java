package me.jakerg.rougelike;

/**
 * Basic enemy class that just wanders
 * @author gutierr8
 *
 */
public class EnemyAi extends CreatureAi{

	public EnemyAi(Creature creature) {
		super(creature);
		// TODO Auto-generated constructor stub
	}
	
	public void onUpdate() {
		wander(); // Just wander once
	}

}
