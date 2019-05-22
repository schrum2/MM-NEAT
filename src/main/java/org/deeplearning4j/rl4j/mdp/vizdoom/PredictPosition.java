package org.deeplearning4j.rl4j.mdp.vizdoom;

import vizdoom.Button;

import java.util.Arrays;
import java.util.List;

/**
 * NB: This scenario is NOT one of two scenarios tested in Doom.java, but Fiszel
 * does reference it in his article. I have imported it into Doom.java to try out.
 * 
 * Similar to Basic.java, except now the monster is free to move on its axis and
 * you remain put with only a semi-circular range of motion. Your objective is to
 * shoot one round in such a way that its trajectory intersects with the monster's
 * path; that is, when it reaches the other end of the room, it hits and kills the
 * monster.
 * 
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/1/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/17/19.
 */
public class PredictPosition extends VizDoom {

    // Constructor enables scenario to be initialized from superclass values.
    public PredictPosition(boolean render) {
        super(render); // Calls superclass constructor with variable render.
    }

    // This method enables user access to important details of the scenario, mostly
    // regarding under which circumstances it is initialized.
    public Configuration getConfiguration() {
    	// Only the left and right turn and attack buttons are used.
        List<Button> buttons = Arrays.asList(Button.TURN_LEFT, Button.TURN_RIGHT, Button.ATTACK);
        return new Configuration("predict_position", -0.0001, 1, 0, 2100, 35, buttons);
    }

    // This method enables users to instantiate this scenario.
    public PredictPosition newInstance() {
        return new PredictPosition(isRender());
    }
}


