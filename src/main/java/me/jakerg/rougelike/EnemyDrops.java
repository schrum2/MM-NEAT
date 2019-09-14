package me.jakerg.rougelike;

import asciiPanel.AsciiPanel;
import edu.southwestern.parameters.Parameters;

public class EnemyDrops extends Drops<Item> {

	public EnemyDrops(Creature creature) {
		super();
		double healthWeight = Parameters.parameters.doubleParameter("healthDropRate");
		double bombWeight = Parameters.parameters.doubleParameter("bombDropRate");
		
		this.addEntry(new Bomb(creature.getWorld(), 'b', AsciiPanel.white, creature.x, creature.y, 4, 5, true), 
				bombWeight);
		this.addEntry(new Health(creature.getWorld(), (char)3, AsciiPanel.brightRed, creature.x, creature.y), 
				healthWeight);
		
		double rest = 100 - healthWeight - bombWeight;
		if(rest > 0)
			this.addEntry(null, rest);
	}
	
	public Item getDrop() {
		return this.getDrop();
	}
	
}
