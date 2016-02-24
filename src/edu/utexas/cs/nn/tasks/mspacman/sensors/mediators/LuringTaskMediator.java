/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

/**
 * false means do not sense edible ghosts. Luring should only occur when ghosts
 * are threats, not edible. Therefore, edible ghosts do not need to be sensed.
 *
 * @author Jacob Schrum
 */
public class LuringTaskMediator extends GhostTaskMediator {

    public LuringTaskMediator() {
        super(false);
    }
}
