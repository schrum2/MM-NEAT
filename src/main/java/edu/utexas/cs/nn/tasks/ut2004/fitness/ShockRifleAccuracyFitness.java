/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.fitness;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import edu.utexas.cs.nn.networks.Network;

/**
 *
 * @author Jacob
 */
public class ShockRifleAccuracyFitness<T extends Network> extends WeaponAccuracyFitness<T> {

	public ShockRifleAccuracyFitness() {
		super(ItemType.SHOCK_RIFLE);
	}
}
