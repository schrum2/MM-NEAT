package org.deeplearning4j.rl4j.mdp.vizdoom;

import vizdoom.Button;

import java.util.Arrays;
import java.util.List;

/**
 * NB: This scenario is NOT one of two scenarios tested in Doom.java, but Fiszel
 * does reference it in his article. I have imported it into Doom.java to try out.
 * 
 * Essentially a scrolling shooter scenario in which you are to dodge fireballs
 * shot at you by a continually increasing number of monsters. This scenario may
 * be the hardest one with which to condition a network, as monsters spawn anywhere
 * on the axis and may shoot in any direction.
 * 
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/1/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/17/19.
 */
public class TakeCover extends VizDoom {

    // Constructor enables scenario to be initialized from superclass values.
	public TakeCover(boolean render) {
        super(render); // Calls superclass constructor with variable render.
    }

    // This method enables user access to important details of the scenario, mostly
    // regarding under which circumstances it is initialized.
	public Configuration getConfiguration() {
        // We will use only the buttons that enable us to move left and right.
        List<Button> buttons = Arrays.asList(Button.MOVE_LEFT, Button.MOVE_RIGHT);
        return new Configuration("take_cover", 1, 1, 0, 2100, 0, buttons);
    }

    // This method enables users to instantiate this scenario.
    public TakeCover newInstance() {
        return new TakeCover(isRender());
    }
}

