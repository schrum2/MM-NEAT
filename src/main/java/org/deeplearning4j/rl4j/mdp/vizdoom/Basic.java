package org.deeplearning4j.rl4j.mdp.vizdoom;

import vizdoom.Button;

import java.util.Arrays;
import java.util.List;

/**
 * NB: This scenario is NOT one of two scenarios tested in Doom.java, but Fiszel
 * does reference it in his article. I have imported it into Doom.java to try out.
 * 
 * In this scenario, you spawn in a room opposite a monster. Your task is, simply,
 * to shoot it. What influences the network to learn, however, is that the monster
 * will reset anywhere on the axis and remain put; your goal is to move left or right
 * until the monster is directly in front of you, then kill it.
 * 
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/1/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/17/19.
 */
public class Basic extends VizDoom {

    // Constructor enables scenario to be initialized from superclass values.
	public Basic(boolean render) {
        super(render); // Calls superclass constructor with variable render.
    }

    // This method enables user access to important details of the scenario, mostly
    // regarding under which circumstances it is initialized.
    public Configuration getConfiguration() {
        // We will use only the buttons to move left and right, as well as the one to shoot.
    	List<Button> buttons = Arrays.asList(Button.ATTACK, Button.MOVE_LEFT, Button.MOVE_RIGHT);
        return new Configuration("basic", -0.01, 1, 0, 700, 0, buttons);
    }

    // This method enables users to instantiate this scenario.
    public Basic newInstance() {
        return new Basic(isRender());
    }
}
