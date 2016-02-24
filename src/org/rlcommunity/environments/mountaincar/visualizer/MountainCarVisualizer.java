/*
 Copyright 2007 Brian Tanner
 http://rl-library.googlecode.com/
 brian@tannerpages.com
 http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.rlcommunity.environments.mountaincar.visualizer;

import java.util.Vector;
import org.rlcommunity.environments.mountaincar.messages.MCGoalRequest;
import org.rlcommunity.environments.mountaincar.messages.MCGoalResponse;
import org.rlcommunity.environments.mountaincar.messages.MCHeightRequest;
import org.rlcommunity.environments.mountaincar.messages.MCHeightResponse;
import org.rlcommunity.environments.mountaincar.messages.MCStateRequest;
import org.rlcommunity.environments.mountaincar.messages.MCStateResponse;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.visualization.interfaces.ValueFunctionDataProvider;
import rlVizLib.visualization.interfaces.GlueStateProvider;
import rlVizLib.messaging.agent.AgentValueForObsRequest;
import rlVizLib.messaging.agent.AgentValueForObsResponse;
import rlVizLib.messaging.environment.EnvObsForStateRequest;
import rlVizLib.messaging.environment.EnvObsForStateResponse;
import rlVizLib.messaging.environment.EnvRangeRequest;
import rlVizLib.messaging.environment.EnvRangeResponse;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.AgentOnValueFunctionVizComponent;
import rlVizLib.visualization.GenericScoreComponent;
import rlVizLib.visualization.ValueFunctionVizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import org.rlcommunity.rlglue.codec.types.Observation;

import org.rlcommunity.rlglue.codec.types.Action;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.visualization.SelfUpdatingVizComponent;

public class MountainCarVisualizer extends AbstractVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider, GlueStateProvider {

    private Vector<Double> mins = null;
    private Vector<Double> maxs = null;
    private MCStateResponse theCurrentState = null;
    private Vector<Double> theQueryPositions = null;
    private Vector<Double> theHeights = null;
    double minHeight = Double.MIN_VALUE;
    double maxHeight = Double.MAX_VALUE;
    double goalPosition = 0.5;
    private int lastStateUpdateTimeStep = -1;
    private int lastAgentValueUpdateTimeStep = -1;
    private boolean printedQueryError = false;
    //Will have to find a way to easily generalize this and move it to vizlib
    private TinyGlue glueState = null;
    //This is a little interface that will let us dump controls to a panel somewhere.
    DynamicControlTarget theControlTarget = null;
    private ValueFunctionVizComponent theValueFunction;
    private AgentOnValueFunctionVizComponent theAgentOnValueFunction;

    public MountainCarVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget) {
        super();

        this.glueState = glueState;
        this.theControlTarget = theControlTarget;

        setupVizComponents();
    }

    protected void setupVizComponents() {
        theValueFunction = new ValueFunctionVizComponent(this, theControlTarget, this.glueState);
        theAgentOnValueFunction = new AgentOnValueFunctionVizComponent(this, this.glueState);
        SelfUpdatingVizComponent mountain = new MountainVizComponent(this);
        SelfUpdatingVizComponent carOnMountain = new CarOnMountainVizComponent(this);
        SelfUpdatingVizComponent scoreComponent = new GenericScoreComponent(this);

        super.addVizComponentAtPositionWithSize(theValueFunction, 0, .5, 1.0, .5);
        super.addVizComponentAtPositionWithSize(theAgentOnValueFunction, 0, .5, 1.0, .5);

        super.addVizComponentAtPositionWithSize(mountain, 0, 0, 1.0, .5);
        super.addVizComponentAtPositionWithSize(carOnMountain, 0, 0, 1.0, .5);
        super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
    }

    public void updateEnvironmentVariableRanges() {
        //Get the Ranges (internalize this)
        EnvRangeResponse theERResponse = EnvRangeRequest.Execute();

        if (theERResponse == null) {
            System.err.println("Asked an Environment for Variable Ranges and didn't get back a parseable message.");
            Thread.dumpStack();
            System.exit(1);
        }

        mins = theERResponse.getMins();
        maxs = theERResponse.getMaxs();
    }

    public double getMaxValueForDim(int whichDimension) {
        if (maxs == null) {
            updateEnvironmentVariableRanges();
        }
        return maxs.get(whichDimension);
    }

    public double getMinValueForDim(int whichDimension) {
        if (mins == null) {
            updateEnvironmentVariableRanges();
        }
        return mins.get(whichDimension);
    }

    public Vector<Observation> getQueryObservations(Vector<Observation> theQueryStates) {
        EnvObsForStateResponse theObsForStateResponse = EnvObsForStateRequest.Execute(theQueryStates);

        if (theObsForStateResponse == null) {
            System.err.println("Asked an Environment for Query Observations and didn't get back a parseable message.");
            Thread.dumpStack();
            System.exit(1);
        }
        return theObsForStateResponse.getTheObservations();
    }
    AgentValueForObsResponse theValueResponse = null;

    public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
        int currentTimeStep = glueState.getTotalSteps();

        boolean needsUpdate = false;
        if (currentTimeStep != lastAgentValueUpdateTimeStep) {
            needsUpdate = true;
        }
        if (theValueResponse == null) {
            needsUpdate = true;
        } else if (theValueResponse.getTheValues().size() != theQueryObs.size()) {
            needsUpdate = true;
        }
        if (needsUpdate) {
            try {
                theValueResponse = AgentValueForObsRequest.Execute(theQueryObs);
                lastAgentValueUpdateTimeStep = currentTimeStep;
            } catch (NotAnRLVizMessageException e) {
                theValueResponse = null;
            }
        }

        if (theValueResponse == null) {
            if (!printedQueryError) {
                printedQueryError = true;
                System.err.println("In the Mountain Car Visualizer: Asked an Agent for Values and didn't get back a parseable message.  I'm not printing this again.");
                theValueFunction.setEnabled(false);
                theAgentOnValueFunction.setEnabled(false);
            }
            //Return NULL and make sure that gets handled
            return null;
        }

        return theValueResponse.getTheValues();
    }

    public double getCurrentStateInDimension(int whichDimension) {
        ensureStateExists();
        if (whichDimension == 0) {
            return theCurrentState.getPosition();
        } else {
            return theCurrentState.getVelocity();
        }
    }

    public int getLastAction() {
        ensureStateExists();
        return theCurrentState.getAction();
    }

    private void ensureStateExists() {
        if (theCurrentState == null) {
            updateAgentState(true);
        }
    }

    public double getHeight() {
        ensureStateExists();
        return theCurrentState.getHeight();
    }

    public double getSlope() {
        ensureStateExists();
        return theCurrentState.getSlope();
    }

    public double getMaxHeight() {
        if (theQueryPositions == null) {
            initializeHeights();
        }
        return minHeight;
    }

    public double getMinHeight() {
        if (theQueryPositions == null) {
            initializeHeights();
        }
        return maxHeight;
    }

    public Vector<Double> getSampleHeights() {
        if (theHeights == null) {
            initializeHeights();
        }
        return theHeights;
    }

    public Vector<Double> getSamplePositions() {
        if (theQueryPositions == null) {
            initializeHeights();
        }
        return theQueryPositions;
    }

    public synchronized void updateAgentState(boolean force) {
        //Only do this if we're on a new time step
        int currentTimeStep = glueState.getTotalSteps();

        if (theCurrentState == null || currentTimeStep != lastStateUpdateTimeStep || force) {
            theCurrentState = MCStateRequest.Execute();
            lastStateUpdateTimeStep = currentTimeStep;
        }
    }

    public Vector<Double> getHeightsForPositions(Vector<Double> theQueryPositions) {
        MCHeightResponse heightResponse = MCHeightRequest.Execute(theQueryPositions);
        return heightResponse.getHeights();
    }

    public double getGoalPosition() {
        MCGoalResponse goalResponse = MCGoalRequest.Execute();
        return goalResponse.getGoalPosition();
    }

    public void initializeHeights() {
        //Because we can change the shape of the curve we have no guarantees what
        // the max and min heights of the mountains may turn out to be...
        // this takes a quick sample based approach to find out what is a good approximation
        //for the min and the max.
        double minPosition = getMinValueForDim(0);
        double maxPosition = getMaxValueForDim(0);

        int pointsToDraw = 500;
        double theRangeSize = maxPosition - minPosition;
        double pointIncrement = theRangeSize / (double) pointsToDraw;

        theQueryPositions = new Vector<Double>();
        for (double i = minPosition; i < maxPosition; i += pointIncrement) {
            theQueryPositions.add(i);
        }
        theHeights = this.getHeightsForPositions(theQueryPositions);

        maxHeight = Double.MIN_VALUE;
        minHeight = Double.MAX_VALUE;
        for (Double thisHeight : theHeights) {
            if (thisHeight > maxHeight) {
                maxHeight = thisHeight;
            }
            if (thisHeight < minHeight) {
                minHeight = thisHeight;
            }
        }
    }

    @Override
    public String getName() {
        return "Mountain Car 1.3 ";
    }

    //This is the one required from RLVizLib, ours has a forcing parameter.  Should update the VizLib
    public void updateAgentState() {
        updateAgentState(false);
    }

    public TinyGlue getTheGlueState() {
        return glueState;
    }
}
