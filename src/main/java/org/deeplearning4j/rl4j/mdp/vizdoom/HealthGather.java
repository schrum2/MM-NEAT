package org.deeplearning4j.rl4j.mdp.vizdoom;

import vizdoom.Button;

import java.util.Arrays;
import java.util.List;

public class HealthGather extends VizDoom {

    public HealthGather(boolean render) {
        super(render);
    }

    public Configuration getConfiguration() {

        List<Button> buttons = Arrays.asList(Button.MOVE_FORWARD, Button.TURN_LEFT, Button.TURN_RIGHT);

        return new Configuration("health_gathering", 1, 100, 0, 700, 0, buttons);
    }

    public HealthGather newInstance() {
        return new HealthGather(isRender());
    }
}
