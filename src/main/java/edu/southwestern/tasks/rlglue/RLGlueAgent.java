package edu.southwestern.tasks.rlglue;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.util.stats.StatisticsUtilities;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageParser;
import rlVizLib.messaging.agent.AgentMessages;

/**
 * Standard RL-Glue agent. Most RL-Glue domains are so standardized that this
 * generic agent will work with any of them. However, more specialized agents
 * can extend/override this class
 * 
 * @author Jacob Schrum, Gabby Gonzalez
 * @param <T>
 *            Type of phenotype
 */
public class RLGlueAgent<T extends Network> extends Organism<T> implements AgentInterface {

	public T policy;

	public RLGlueAgent() {
		super(null);
	}

	/**
	 * Returns the number of outputs for a given agent
	 * @return action range max - action range min + 1
	 */
	public int getNumberOutputs() {
		return RLGlueTask.tso.getDiscreteActionRange(0).getMax() - RLGlueTask.tso.getDiscreteActionRange(0).getMin() + 1;
	}

	/**
	 * This "replace" is used to test genotypes in the task methods
	 */
	@Override
	public void replaceGenotype(Genotype<T> newGenotype) {
		super.replaceGenotype(newGenotype);
		policy = newGenotype.getPhenotype();
	}

	/**
	 * Policy is taken in earlier and changed by Phenotype
	 * 
	 * @param inputs
	 * @return outputs from network
	 */
	public double[] consultPolicy(double[] inputs) {
		return policy.process(inputs);
	}

	/**
	 * Not really needed because TaskSpec is already extracted in MMNEAT
	 * 
	 * @param taskSpec
	 *            String description of task sent from RL Glue
	 */
	@Override
	public void agent_init(String taskSpec) {
	}

	/**
	 * Getter for the starting action of the observation
	 * 
	 * @param o
	 *            generic observation
	 * @return Action to take given the observation
	 */
	@Override
	public Action agent_start(Observation o) {
		return getAction(o);
	}

	/**
	 * Getter for the "next" action of the observation
	 * 
	 * @param reward Immediate reward?
	 * @param o Observation of state
	 * @return action to take in the state
	 */
	@Override
	public Action agent_step(double reward, Observation o) {
		return getAction(reward, o);
	}

	/**
	 * Does nothing currently
	 * 
	 * @param reward Final reward
	 */
	@Override
	public void agent_end(double reward) {
	}

	public Action getAction(Observation o) {
		return getAction(0.0, o); // Reward of 0.0 if not specified
	}
	
	/**
	 * Give an observation, get the action the agent should take
	 * @param reward Immediate reward from action
	 * @param o Observation from new state
	 * @return
	 */
	public Action getAction(double reward, Observation o) {
		Action action = new Action(RLGlueTask.tso.getNumDiscreteActionDims(), RLGlueTask.tso.getNumContinuousActionDims());

		double[] inputs = RLGlueTask.rlGlueExtractor.extract(o);
		double[] outputs = this.consultPolicy(inputs);

		// A lot of limiting assumptions are being made about the types of
		// actions available in RL Glue
		action.intArray[0] = StatisticsUtilities.argmax(outputs);

		return action;
	}

	/**
	 * Cleans the policy of the agent
	 */
	@Override
	public void agent_cleanup() {
		policy.flush(); // resets recurrent activations
	}

	/**
	 * Takes in a message and responds accordingly with another message
	 * 
	 * @param theMessage
	 * @return
	 */
	@Override
	public String agent_message(String theMessage) {
		AgentMessages theMessageObject;
		try {
			theMessageObject = AgentMessageParser.parseMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent agent a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}

		if (theMessageObject.canHandleAutomatically(this)) {
			return theMessageObject.handleAutomatically(this);
		}
		System.err.println("Didn't know how to respond to message.");
		return null;
	}
}
