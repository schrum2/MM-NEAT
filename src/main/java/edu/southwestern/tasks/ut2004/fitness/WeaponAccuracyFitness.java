package edu.southwestern.tasks.ut2004.fitness;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measures an organism's accuracy with weapons 
 * @author Jacob
 */
public class WeaponAccuracyFitness<T extends Network> extends UT2004FitnessFunction<T> {

	private final ItemType weapon; 

	/**
	 * sets the weapon type
	 * @param weapon (weapon to find accuracy for)
	 */
	public WeaponAccuracyFitness(ItemType weapon) {
		this.weapon = weapon;
	}
	
	/**
	 * compares damage done to ammo used to determine accuracy
	 * @return returns the accuracy with the weapon
	 */
	public double fitness(Organism<T> individual) {
		double ammoUsed = game.ammoUsed(weapon);
		if (ammoUsed == 0) {
			return 0;
		} else {
			return (1.0 * game.getWeaponDamage(weapon)) / ammoUsed;
		}
	}
}
