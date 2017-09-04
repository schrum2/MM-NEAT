package org.rlcommunity.environments.acrobot.visualizer;

import org.rlcommunity.environments.acrobot.messages.StateRequest;
import org.rlcommunity.environments.acrobot.messages.StateResponse;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.GenericScoreComponent;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.interfaces.GlueStateProvider;

public class AcrobotVisualizer extends AbstractVisualizer implements GlueStateProvider {

    private TinyGlue theGlueState = null;
    private StateResponse currentState = null;

    private void checkState() {
        if (currentState == null) {
            updateState();
        }
    }

    public AcrobotVisualizer(TinyGlue theGlueState) {
        super();
        this.theGlueState = theGlueState;
        SelfUpdatingVizComponent theAcrobotVisualizer = new AcrobotBotComponent(this);
        SelfUpdatingVizComponent theAcrobotCounter = new GenericScoreComponent(this);

        addVizComponentAtPositionWithSize(theAcrobotVisualizer, 0, 0, 1.0, 1.0);
        addVizComponentAtPositionWithSize(theAcrobotCounter, 0, 0, 1.0, 1.0);
    }

    public double getTheta1() {
        checkState();
        return currentState.getTheta1();
    }

    public double getTheta2() {
        checkState();
        return currentState.getTheta2();
    }

    public double getTheta1Dot() {
        checkState();
        return currentState.getTheta1Dot();
    }

    public double getTheta2Dot() {
        checkState();
        return currentState.getTheta2Dot();
    }

    public TinyGlue getTheGlueState() {
        return theGlueState;
    }

    void updateState() {
        currentState=StateRequest.Execute();
    }
}