/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ensemble;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 * The general idea is to be a specialization of the AverageArbitrator.
 * The specialization comes in when certain modes are entirely disabled,
 * so as to removed the choice or influence of a certain mode.
 * 
 * @author Jacob Schrum
 */
public abstract class OverlappingArbitrator extends AverageArbitrator {

    /**
     * Number of modes
     * @return 
     */
    public abstract int modes();

    public abstract boolean[] modesToConsider(GameFacade gf);
    
    public abstract String[] modeLabels();
    
    /**
     * preferences[net][out]
     * 
     * @param game
     * @param preferences
     * @return 
     */
    @Override
    public double[] newDirectionalPreferences(GameFacade game, double[][] preferences) {
        assert modes() == preferences.length : "Modes " + modes() + " not equal to preference groups " + preferences.length;
        boolean[] consider = modesToConsider(game);
        int keeperModes = ArrayUtil.countOccurrences(true, consider);
        double[][] reducedPreferences = new double[keeperModes][];
        int index = 0;
        for(int i = 0; i < consider.length; i++) {
            if(consider[i]) {
                //System.out.println("\tKeeping preferences: " + Arrays.toString(preferences[i]));
                reducedPreferences[index++] = preferences[i];
            }
        }
        return super.newDirectionalPreferences(game, reducedPreferences);
    }
}
