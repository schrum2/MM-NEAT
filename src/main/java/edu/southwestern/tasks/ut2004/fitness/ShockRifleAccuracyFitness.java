package edu.southwestern.tasks.ut2004.fitness;

import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import edu.southwestern.networks.Network;

/**
 *Measures the bot's accuracy with the shock rifle
 * @author Jacob
 */
public class ShockRifleAccuracyFitness<T extends Network> extends WeaponAccuracyFitness<T> {

	/**
	 * @return returns weapon accuracy with the schock rifle
	 */
	public ShockRifleAccuracyFitness() {
		super(UT2004ItemType.SHOCK_RIFLE);
	}
}
