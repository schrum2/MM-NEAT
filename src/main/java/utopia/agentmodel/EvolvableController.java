package utopia.agentmodel;

import java.io.Serializable;
import machinelearning.evolution.evolvables.Evolvable;
import machinelearning.networks.FunctionApproximator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 12:19:22 PM
 */
public abstract class EvolvableController extends Controller implements Evolvable, Serializable {
    //transient public MyAgentStats stats = null;
    transient public double evalTime = 0;
//    public MyAgentStats getStats(){
//        MyAgentStats result = stats;
//        evalTime = stats.evalTime;
//        this.stats = null;
//        return result;
//    }
    transient public String filename = "";
    //schrum2: Needed by BD
    public abstract FunctionApproximator getFunctionApproximator();
}
