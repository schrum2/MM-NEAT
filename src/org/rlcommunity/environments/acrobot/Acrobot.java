package org.rlcommunity.environments.acrobot;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rlcommunity.environments.acrobot.AcrobotState;
import org.rlcommunity.environments.acrobot.messages.StateResponse;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import org.rlcommunity.environments.acrobot.visualizer.AcrobotVisualizer;
import org.rlcommunity.environments.cartpole.CartPole;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

import edu.utexas.cs.nn.tasks.rlglue.RLGlueEnvironment;
import edu.utexas.cs.nn.tasks.rlglue.acrobot.AcrobotViewer;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;
import rlVizLib.messaging.interfaces.HasImageInterface;

public class Acrobot extends RLGlueEnvironment implements HasAVisualizerInterface, HasImageInterface {
  private final AcrobotState theState;
    
    /*Constructor Business*/
    public Acrobot() {
        this(getDefaultParameters());
    }

    public Acrobot(ParameterHolder p) {
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

        theState=new AcrobotState(randomStartStates,transitionNoise,randomSeed);
    }
    //This method creates the object that can be used to easily set different problem parameters
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
    

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P){
        Acrobot theAcrobot=new Acrobot(P);
        String taskSpec=theAcrobot.makeTaskSpecString();
        return new TaskSpecPayload(taskSpec, false, "");
    }

    //private String makeTaskSpec(){
    public TaskSpec makeTaskSpec(){
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta1(),theState.getMaxTheta1()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta2(),theState.getMaxTheta2()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta1Dot(),theState.getMaxTheta1Dot()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta2Dot(),theState.getMaxTheta2Dot()));

        theTaskSpecObject.addDiscreteAction(new IntRange(0, theState.getNumActions()-1));

        //Apparently we don't say the reward range.
        theTaskSpecObject.setRewardRange(new DoubleRange(-1.0d,0.0d));
        theTaskSpecObject.setExtra("EnvName:Acrobot");
        
        return new TaskSpec(theTaskSpecObject);
    
        // From original
//        String taskSpecString = theTaskSpecObject.toTaskSpec();
//        TaskSpec.checkTaskSpec(taskSpecString);
//        return taskSpecString;        
    }

    private String makeTaskSpecString(){
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta1(),theState.getMaxTheta1()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta2(),theState.getMaxTheta2()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta1Dot(),theState.getMaxTheta1Dot()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(-theState.getMaxTheta2Dot(),theState.getMaxTheta2Dot()));

        theTaskSpecObject.addDiscreteAction(new IntRange(0, theState.getNumActions()-1));

        //Apparently we don't say the reward range.
        theTaskSpecObject.setRewardRange(new DoubleRange(-1.0d,0.0d));
        theTaskSpecObject.setExtra("EnvName:Acrobot");
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;        
    }

    public AcrobotState getCurrentState(){
        return theState;
    }



    /*Beginning of RL-GLUE methods*/
    public String env_init() {
        return makeTaskSpecString();
    }


    public Observation env_start() {
        theState.reset();
        return makeObservation();
    }

    public Reward_observation_terminal env_step(Action a) {
        if ((a.intArray[0] < 0) || (a.intArray[0] > 2)) {
            System.out.printf("Invalid action %d, selecting null action randomly\n", a.intArray[0]);
            a.intArray[0] =1;
        }
        theState.update(a.intArray[0]);


        Reward_observation_terminal ro = new Reward_observation_terminal();
        ro.r = -1;
        ro.o = makeObservation();

        ro.terminal = 0;
        if (theState.isTerminal()) {
            ro.r=0.0d;
            ro.terminal = 1;
        }
        
		if (AcrobotViewer.current != null) {
			AcrobotViewer.current.reset(theState);
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				Logger.getLogger(CartPole.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
        return ro;
    }

    public void env_cleanup() {
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (Exception e) {
            System.err.println("Someone sent Acrobot a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }

        //		If it wasn't handled automatically, maybe its a custom message
        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {

            String theCustomType = theMessageObject.getPayLoad();
            if (theCustomType.equals("GETACROBOTSTATE")) {
                //It is a request for the state
                StateResponse theResponseObject = new StateResponse(theState.getLastAction(),theState.getTheta1(), theState.getTheta2(), theState.getTheta1Dot(), theState.getTheta2Dot());
                return theResponseObject.makeStringResponse();
            }
        }

        System.out.println("We need some code written in Env Message for  Acrobot!");
        Thread.dumpStack();

        return null;
    }

    /*End of RL-Glue Methods*/
    /*Beginning of RL-VIZ Methods*/
    @Override
    protected Observation makeObservation() {
        Observation obs = new Observation(0, 4);
        obs.doubleArray[0] = theState.getTheta1();
        obs.doubleArray[1] = theState.getTheta2();

        obs.doubleArray[2] = theState.getTheta1Dot();
        obs.doubleArray[3] = theState.getTheta2Dot();
        return obs;
    }
    /*End of RL-VIZ Methods*/



    public String getVisualizerClassName() {
        return AcrobotVisualizer.class.getName();
    }
    
    public URL getImageURL() {
       URL imageURL = Acrobot.class.getResource("/images/acrobot.png");
       return imageURL;
   }


    public static void main(String[] args){
        EnvironmentLoader L=new EnvironmentLoader(new Acrobot());
        L.run();
    }

    @Override
	public ArrayList<Double> getBehaviorVector() {
		ArrayList<Double> result = new ArrayList<Double>(4);
		result.add(theState.getTheta1());
		result.add(theState.getTheta2());
		result.add(theState.getTheta1Dot());
		result.add(theState.getTheta2Dot());
		return result;
	}
}

class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Acrobot 1.0";
    }

    public String getShortName() {
        return "Acrobot";
    }

    public String getAuthors() {
        return "Brian Tanner from Adam White from Richard S. Sutton?";
    }

    public String getInfoUrl() {
        return "http://library.rl-community.org/acrobot";
    }

    public String getDescription() {
        return "Acrobot problem from the reinforcement learning library.";
    }
}
