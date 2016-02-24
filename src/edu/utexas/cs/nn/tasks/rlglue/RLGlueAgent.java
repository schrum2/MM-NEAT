package edu.utexas.cs.nn.tasks.rlglue;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageParser;
import rlVizLib.messaging.agent.AgentMessages;

/**
 *
 * @author Jacob Schrum
 */
public class RLGlueAgent<T extends Network> extends Organism<T> implements AgentInterface {

    TaskSpec TSO = null;
    public T policy;

    public RLGlueAgent(int numObjectives) {
        super(null);
    }

    @Override
    public void replaceGenotype(Genotype<T> newGenotype) {
        super.replaceGenotype(newGenotype);
        policy = newGenotype.getPhenotype();
    }

    public double[] consultPolicy(double[] inputs) {
        return policy.process(inputs);
    }

    public void agent_init(String taskSpec) {
        TSO = new TaskSpec(taskSpec);
    }

    public Action agent_start(Observation o) {
        return getAction(o);
    }

    public Action agent_step(double d, Observation o) {
        return getAction(o);
    }

    public void agent_end(double d) {
    }

    private Action getAction(Observation o) {
        Action action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());

        double[] inputs = MMNEAT.rlGlueExtractor.extract(o);
        double[] outputs = this.consultPolicy(inputs);

        // A lot of limiting assumptions are being made about the types of actions available in RL Glue
        action.intArray[0] = StatisticsUtilities.argmax(outputs);

        return action;
    }

    public void agent_cleanup() {
        policy.flush();
    }

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
