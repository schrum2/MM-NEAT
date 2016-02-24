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
package org.rlcommunity.environments.mountaincar;

import edu.utexas.cs.nn.tasks.rlglue.RLGlueEnvironment;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import org.rlcommunity.environments.mountaincar.messages.MCGoalResponse;
import org.rlcommunity.environments.mountaincar.messages.MCHeightResponse;
import org.rlcommunity.environments.mountaincar.messages.MCStateResponse;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import java.util.Random;
import org.rlcommunity.environments.mountaincar.visualizer.MountainCarVisualizer;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;
import rlVizLib.general.hasVersionDetails;

/*
 * July 2007
 * This is the Java Version MountainCar Domain from the RL-Library.  
 * Brian Tanner ported it from the Existing RL-Library to Java.
 * I found it here: http://rlai.cs.ualberta.ca/RLR/environment.html
 * 
 * 
 * This is quite an advanced environment in that it has some fancy visualization
 * capabilities which have polluted the code a little.  What I'm saying is that 
 * this is not the easiest environment to get started with.
 */
import rlVizLib.messaging.environmentShell.TaskSpecPayload;
import rlVizLib.messaging.interfaces.HasImageInterface;

public class MountainCar extends RLGlueEnvironment implements
        getEnvMaxMinsInterface,
        getEnvObsForStateInterface,
        HasAVisualizerInterface,
        HasImageInterface {

    static final int numActions = 3;
    protected final MountainCarState theState;    //Used for env_save_state and env_save_state, which don't exist anymore, but we will one day bring them back
    //through the messaging system and RL-Viz.
    //Problem parameters have been moved to MountainCar State
    private Random randomGenerator = new Random();

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        MountainCar theMC = new MountainCar(P);
        String taskSpecString = theMC.makeTaskSpec().getStringRepresentation();
        return new TaskSpecPayload(taskSpecString, false, "");
    }

    public TaskSpec makeTaskSpec() {
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(theState.minPosition, theState.maxPosition));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(theState.minVelocity, theState.maxVelocity));
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 2));
        theTaskSpecObject.setRewardRange(new DoubleRange(-1, 0));
        theTaskSpecObject.setExtra("EnvName:Mountain-Car Revision:" + this.getClass().getPackage().getImplementationVersion());

        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);

        return new TaskSpec(theTaskSpecObject);

    }

    public MountainCarState getState() {
        return theState;
    }

    public String env_init() {
        return makeTaskSpec().getStringRepresentation();

    }

    /**
     * Restart the car on the mountain. Pick a random position and velocity if
     * randomStarts is set.
     *
     * @return
     */
    public Observation env_start() {
        theState.reset();

        return makeObservation();
    }

    /**
     * Takes a step. If an invalid action is selected, choose a random action.
     *
     * @param theAction
     * @return
     */
    public Reward_observation_terminal env_step(Action theAction) {

        int a = theAction.intArray[0];

        if (a > 2 || a < 0) {
            System.err.println("Invalid action selected in mountainCar: " + a);
            a = randomGenerator.nextInt(3);
        }

        theState.update(a);

        return makeRewardObservation(theState.getReward(), theState.inGoalRegion());
    }

    /**
     * Return the ParameterHolder object that contains the default parameters
     * for mountain car. The only parameter is random start states.
     *
     * @return
     */
    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());

        p.addIntegerParam("RandomSeed(0 means random)", 0);
        p.addBooleanParam("RandomStartStates", false);
        p.addDoubleParam("TransitionNoise[0,1]", 0.0d);
        p.setAlias("noise", "TransitionNoise[0,1]");
        p.setAlias("seed", "RandomSeed(0 means random)");
        return p;
    }

    /**
     * Create a new mountain car environment using parameter settings in p.
     *
     * @param p
     */
    public MountainCar(ParameterHolder p) {
        super();
        boolean randomStartStates = false;
        double transitionNoise = 0.0d;
        long randomSeed = 0L;

        if (p != null) {
            if (!p.isNull()) {
                randomStartStates = p.getBooleanParam("RandomStartStates");
                transitionNoise = p.getDoubleParam("noise");
                randomSeed = p.getIntegerParam("seed");
            }
        }
        theState = new MountainCarState(randomStartStates, transitionNoise, randomSeed);

    }

    public MountainCar() {
        this(getDefaultParameters());
    }

    /**
     * Handles messages that find out the version, what visualizer is available,
     * etc.
     *
     * @param theMessage
     * @return
     */
    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent mountain Car a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            String theResponseString = theMessageObject.handleAutomatically(this);
            return theResponseString;
        }

        //If it wasn't handled automatically, maybe its a custom Mountain Car Message
        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {

            String theCustomType = theMessageObject.getPayLoad();

            if (theCustomType.equals("GETMCSTATE")) {
                //It is a request for the state
                double position = theState.getPosition();
                double velocity = theState.getVelocity();
                double height = theState.getHeightAtPosition(position);
                int lastAction = theState.getLastAction();
                double slope = theState.getSlope(position);
                MCStateResponse theResponseObject = new MCStateResponse(lastAction, position, velocity, height, slope);
                return theResponseObject.makeStringResponse();
            }

            if (theCustomType.startsWith("GETHEIGHTS")) {
                Vector<Double> theHeights = new Vector<Double>();

                StringTokenizer theTokenizer = new StringTokenizer(theCustomType, ":");
                //throw away the first token
                theTokenizer.nextToken();

                int numQueries = Integer.parseInt(theTokenizer.nextToken());
                for (int i = 0; i < numQueries; i++) {
                    double thisPoint = Double.parseDouble(theTokenizer.nextToken());
                    theHeights.add(theState.getHeightAtPosition(thisPoint));
                }

                MCHeightResponse theResponseObject = new MCHeightResponse(theHeights);
                return theResponseObject.makeStringResponse();
            }

            if (theCustomType.startsWith("GETMCGOAL")) {
                MCGoalResponse theResponseObject = new MCGoalResponse(theState.goalPosition);
                return theResponseObject.makeStringResponse();
            }

        }
        System.err.println("We need some code written in Env Message for MountainCar.. unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }

    public static void main(String[] args) {
        EnvironmentLoader L = new EnvironmentLoader(new MountainCar());
        L.run();
    }

    /**
     * Turns theState object into an observation.
     *
     * @return
     */
    @Override
    protected Observation makeObservation() {
        return theState.makeObservation();
    }

    public void env_cleanup() {
        theState.reset();
    }

    /**
     * The value function will be drawn over the position and velocity. This
     * method provides the max values for those variables.
     *
     * @param dimension
     * @return
     */
    public double getMaxValueForQuerableVariable(int dimension) {
        if (dimension == 0) {
            return theState.maxPosition;
        } else {
            return theState.maxVelocity;
        }
    }

    /**
     * The value function will be drawn over the position and velocity. This
     * method provides the min values for those variables.
     *
     * @param dimension
     * @return
     */
    public double getMinValueForQuerableVariable(int dimension) {
        if (dimension == 0) {
            return theState.minPosition;
        } else {
            return theState.minVelocity;
        }
    }

    /**
     * Given a state, return an observation. This is trivial in mountain car
     * because the observation is the same as the internal state
     *
     * @param theState
     * @return
     */
    public Observation getObservationForState(Observation theState) {
        return theState;
    }

    /**
     * How many state variables are there (used for value function drawing)
     *
     * @return
     */
    public int getNumVars() {
        return 2;
    }

    public String getVisualizerClassName() {
        return MountainCarVisualizer.class.getName();
    }

    /**
     * So we can draw a pretty image in the visualizer before we start
     *
     * @return
     */
    public URL getImageURL() {
        URL imageURL = MountainCar.class.getResource("/images/mountaincar.png");
        return imageURL;
    }

    @Override
    public ArrayList<Double> getBehaviorVector() {
        ArrayList<Double> result = new ArrayList<Double>(2);
        MountainCarState state = this.getState();
        result.add(state.getPosition());
        result.add(state.getVelocity());
        return result;
    }
}

/**
 * This is a little helper class that fills in the details about this
 * environment for the fancy print outs in the visualizer application.
 *
 * @author btanner
 */
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Mountain Car 1.30";
    }

    public String getShortName() {
        return "Mount-Car";
    }

    public String getAuthors() {
        return "Richard Sutton, Adam White, Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://library.rl-community.org/environments/mountaincar";
    }

    public String getDescription() {
        return "RL-Library Java Version of the classic Mountain Car RL-Problem.";
    }
}
