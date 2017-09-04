/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.fitness;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import edu.southwestern.networks.Network;

/**
 *
 * @author Jacob
 */
public class ShockRifleAccuracyFitness<T extends Network> extends WeaponAccuracyFitness<T> {

	public ShockRifleAccuracyFitness() {
		super(ItemType.SHOCK_RIFLE);
	}
}
