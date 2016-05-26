package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomBasicShootTask<T extends Network> extends VizDoomTask<T> {

    public void setDoomActions() {
        // Adds buttons that will be allowed.
        game.addAvailableButton(Button.MOVE_LEFT);
        game.addAvailableButton(Button.MOVE_RIGHT);
        game.addAvailableButton(Button.ATTACK);

        // Define some actions. Each list entry corresponds to declared buttons:
        // MOVE_LEFT, MOVE_RIGHT, ATTACK
        // more combinations are naturally possible but only 3 are included for transparency when watching.
        addAction(new int[]{1, 0, 1}, "Left and Shoot");
        addAction(new int[]{0, 1, 1}, "Right and Shoot");
        addAction(new int[]{0, 0, 1}, "Still and Shoot");
    }
	
    public void setDoomStateVariables() {
        // Adds game variables that will be included in state.
        game.addAvailableGameVariable(GameVariable.AMMO2);
    }
    
    
    public static void main(String[] args) {
    	Parameters.initializeParameterCollections(new String[]{"watch:false","io:false","netio:false","task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomBasicShootTask", "trials:3", "printFitness:true"});
    	MMNEAT.loadClasses();
    	VizDoomBasicShootTask<TWEANN> vd = new VizDoomBasicShootTask<TWEANN>();
    	TWEANNGenotype individual = new TWEANNGenotype();
    	System.out.println(CommonConstants.trials);
    	System.out.println(vd.evaluate(individual));
    	System.out.println(vd.evaluate(individual));
    }

	@Override
	public int numInputs() {
		return game.getScreenWidth();
	}

	@Override
	public double[] getInputs(GameState s) {
		// TODO Auto-generated method stub
		return null;
	}
}
