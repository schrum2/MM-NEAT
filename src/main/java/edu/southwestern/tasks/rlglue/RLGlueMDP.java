package edu.southwestern.tasks.rlglue;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import edu.southwestern.MMNEAT.MMNEAT;

/**
 * Class to treat RL Glue environments as DL4J MDP instances.
 * @author Jacob Schrum
 */
public class RLGlueMDP implements MDP<EncodableObservation, Integer, DiscreteSpace> {

	private RLGlueEnvironment environment;
	private DiscreteSpace discreteActionSpace;
	private ArrayObservationSpace<EncodableObservation> observationSpace;
	private boolean done;

	public RLGlueMDP(RLGlueEnvironment environment) {
		this.environment = environment;
		// Assume one output per possible action
		this.discreteActionSpace = new DiscreteSpace(MMNEAT.networkOutputs);
		// Assume each feature is one aspect of an observation
		this.observationSpace = new ArrayObservationSpace<>(new int[] {MMNEAT.rlGlueExtractor.numFeatures()});
		this.done = false;
	}
	
	@Override
	public ObservationSpace<EncodableObservation> getObservationSpace() {
		return observationSpace;
	}

	@Override
	public DiscreteSpace getActionSpace() {
		return discreteActionSpace;
	}

	@Override
	public EncodableObservation reset() {
		environment.env_cleanup(); // end episode 
		environment.env_init(); // set up environment
		return new EncodableObservation(environment.env_start()); // start the next episode
	}

	@Override
	public void close() {
		environment.env_cleanup();
	}

	@Override
	public StepReply<EncodableObservation> step(Integer action) {
		//Action rlGlueAction = new Action(MMNEAT.tso.getNumDiscreteActionDims(), MMNEAT.tso.getNumContinuousActionDims());
		Action rlGlueAction = new Action(1, 0); // I think that there is only ever a single discrete action to choose
		// A lot of limiting assumptions are being made about the types of
		// actions available in RL Glue
		rlGlueAction.intArray[0] = action;
		// RL Glue step
		Reward_observation_terminal result = environment.env_step(rlGlueAction);
		// Remember whether episode just finished
		done = result.isTerminal();
		// Repackage in MDP's equivalent to Reward_observation_terminal		
		return new StepReply<>(new EncodableObservation(result.getObservation()), result.getReward(), done, null);
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public MDP<EncodableObservation, Integer, DiscreteSpace> newInstance() {
		return new RLGlueMDP(environment);
	}

}
