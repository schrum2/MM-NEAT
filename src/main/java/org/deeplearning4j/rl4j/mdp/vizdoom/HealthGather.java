package org.deeplearning4j.rl4j.mdp.vizdoom;

import vizdoom.Button;

import java.util.Arrays;
import java.util.List;

/**
 * In this scenario, you spawn in a room with an acid floor. Your task is to
 * collect as many health boxes as possible to keep you alive.
 * 
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/1/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/17/19.
 */
public class HealthGather extends VizDoom {

    // Constructor enables scenario to be initialized from superclass values.
    public HealthGather(boolean render) {
        super(render); // Calls superclass constructor with variable render.
    }

    // This method enables user access to important details of the scenario, mostly
    // regarding under which circumstances it is initialized.
    public Configuration getConfiguration() {
        // In this example, we are not able to move side-to-side or backwards, so
    	// we just go forward and turn whenever necessary.
    	List<Button> buttons = Arrays.asList(Button.MOVE_FORWARD, Button.TURN_LEFT, Button.TURN_RIGHT);
        return new Configuration("health_gathering", 1, 100, 0, 700, 0, buttons);
    }

    // This method enables users to instantiate this scenario.
    public HealthGather newInstance() {
        return new HealthGather(isRender());
    }
}
