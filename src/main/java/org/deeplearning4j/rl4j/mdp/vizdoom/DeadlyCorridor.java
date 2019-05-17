package org.deeplearning4j.rl4j.mdp.vizdoom;

import vizdoom.Button;

import java.util.Arrays;
import java.util.List;

/**
 * The task in this scenario is to maneuver through a hallway with multiple enemies
 * shooting at you. You may, of course, shoot and kill some or all of them while
 * doing so; you may also just run through it without maiming them at all.
 * 
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/1/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/17/19.
 */
public class DeadlyCorridor extends VizDoom {

    // Constructor enables scenario to be initialized from superclass values.
	public DeadlyCorridor(boolean render) {
        super(render); // Calls superclass constructor with variable render.
    }

    // This method enables user access to important details of the scenario, mostly
    // regarding under which circumstances it is initialized.
    public Configuration getConfiguration() {
        /**
         *  This scenario requires the most buttons out of the five VizDoom.java
         *  subclasses, as the network may pursue a variety of options in moving
         *  through the corridor (mentioned above).
         */
        List<Button> buttons = Arrays.asList(Button.ATTACK, Button.MOVE_LEFT, 
         Button.MOVE_RIGHT, Button.MOVE_FORWARD, Button.TURN_LEFT, Button.TURN_RIGHT);
        return new Configuration("deadly_corridor", 0.0, 5, 100, 2100, 0, buttons);
    }

    // This method enables users to instantiate this scenario.
    public DeadlyCorridor newInstance() {
        return new DeadlyCorridor(isRender());
    }
}

