package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions.MsPacManAction;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ActionBlockLoadedInputOutputMediator extends BlockLoadedInputOutputMediator {

    public ArrayList<MsPacManAction> actions;

    public ActionBlockLoadedInputOutputMediator() {
        super();
        actions = new ArrayList<MsPacManAction>();
    }

    @Override
    public String[] outputLabels() {
        String[] labels = new String[actions.size()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = actions.get(i).getClass().getSimpleName();
        }
        return labels;
    }

    /**
     * One output corresponding to the selection of each action
     *
     * @return
     */
    @Override
    public int numOut() {
        return actions.size();
    }
}
