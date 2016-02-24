/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionCountAllPillsInKStepsBlock extends VariableDirectionBlock {

    public final int stepCount;

    public VariableDirectionCountAllPillsInKStepsBlock(int dir) {
        this(dir, Parameters.parameters.integerParameter("smallStepSimDepth"));
    }

    public VariableDirectionCountAllPillsInKStepsBlock(int dir, int k) {
        super(dir);
        this.stepCount = k;
    }

    public double wallValue() {
        return 0;
    }

    @Override
    public double getValue(GameFacade gf) {
        final int currentLocation = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(currentLocation);
        final int next = neighbors[dir];
        assert next != -1 : "The next direction is not viable!";
        ArrayList<Integer> visited = new ArrayList<Integer>();
        visited.add(currentLocation);
        int[] targets = getCountTargets(gf);
        double count = VariableDirectionKStepCountBlock.countMembersAlongPath(gf, visited, targets, currentLocation, next, stepCount, true);
//        if(CommonConstants.watch && count == targets.length){
//            gf.addPoints(Color.blue, targets);
//        }
        return count == targets.length ? 1 : 0;
    }

    public int[] getCountTargets(GameFacade gf){
        return gf.getActivePillsIndices();
    }

    @Override
    public String getLabel() {
        return "All " + getType() + " Within " + stepCount + " Steps Ahead";
    }

    public String getType(){
        return "Active Pills";
    }
}
