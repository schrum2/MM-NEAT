package edu.utexas.cs.nn.tasks.rlglue;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import rlVizLib.Environments.EnvironmentBase;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class RLGlueEnvironment extends EnvironmentBase {

    public abstract TaskSpec makeTaskSpec();

    public abstract ArrayList<Double> getBehaviorVector();
}
