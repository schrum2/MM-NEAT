package edu.utexas.cs.nn.tasks.rlglue;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
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
public class RLGlueAgent<T extends Network> extends Organism<T>implements AgentInterface {

	public T policy;

	public RLGlueAgent() {
		super(null);
	}

	public int getNumberOutputs() {
		return MMNEAT.tso.getDiscreteActionRange(0).getMax() - MMNEAT.tso.getDiscreteActionRange(0).getMin() + 1;
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
	 * @param d
	 *            Immediate reward?
	 * @param o
	 *            Observation of state
	 * @return action to take in the state
	 */
	@Override
	public Action agent_step(double d, Observation o) {
		return getAction(o);
	}

	/**
	 * Does nothing currently
	 * 
	 * @param d
	 *            Final reward?
	 */
	@Override
	public void agent_end(double d) {
	}

	/**
	 * Give an observation, get the action the agent should take
	 * 
	 * @param o
	 * @return
	 */
	public Action getAction(Observation o) {
		Action action = new Action(MMNEAT.tso.getNumDiscreteActionDims(), MMNEAT.tso.getNumContinuousActionDims());

		double[] inputs = MMNEAT.rlGlueExtractor.extract(o);
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
