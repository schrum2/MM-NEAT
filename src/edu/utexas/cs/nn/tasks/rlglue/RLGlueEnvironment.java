package edu.utexas.cs.nn.tasks.rlglue;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import rlVizLib.Environments.EnvironmentBase;
import java.util.ArrayList;

/**
 * A domain for use with RL-Glue
 * 
 * @author Jacob Schrum
 */
public abstract class RLGlueEnvironment extends EnvironmentBase {

        /**
         * Required by RL Glue. The TaskSpec is a basic clarification
         * of how the task works (everything the agent needs to know)
         * @return task specification
         */
	public abstract TaskSpec makeTaskSpec();

        /**
         * Only matters if you want to use Behavioral Diversity
         * with the RL Glue domain. This is used by the domain-specific
         * behavior characterization.
         * @return List of numbers characterizing behavior in the domain.
         */
	public abstract ArrayList<Double> getBehaviorVector();
}
