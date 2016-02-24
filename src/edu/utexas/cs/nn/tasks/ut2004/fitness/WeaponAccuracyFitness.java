/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.fitness;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

/**
 *
 * @author Jacob
 */
public class WeaponAccuracyFitness<T extends Network> extends UT2004FitnessFunction<T> {

    private final ItemType weapon;

    public WeaponAccuracyFitness(ItemType weapon) {
        this.weapon = weapon;
    }

    public double fitness(Organism<T> individual) {
        double ammoUsed = game.ammoUsed(weapon);
        if (ammoUsed == 0) {
            return 0;
        } else {
            return (1.0 * game.getWeaponDamage(weapon)) / ammoUsed;
        }
    }
}
