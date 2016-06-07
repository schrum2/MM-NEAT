package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomBasicShootTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;

	public VizDoomBasicShootTask() {
		super();
		if(!Parameters.parameters.booleanParameter("doomFullScreenInput")){
			inputRow = getRow(game.getScreenWidth(), game.getScreenHeight());
		} else {
			inputRow = -1; // this is for a check 
        }
	}

        @Override
        public void taskSpecificInit() {
        game.loadConfig("vizdoom/examples/config/basic.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/basic.wad");
		game.setDoomMap("map01");
	}
        
        @Override
	public void setDoomActions() {
		// Adds buttons that will be allowed.
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.ATTACK);

		// Define some actions. Each list entry corresponds to declared buttons:
		// MOVE_LEFT, MOVE_RIGHT, ATTACK
		// more combinations are naturally possible but only 3 are included for
		// transparency when watching.
		//addAction(new int[] { 1, 0, 1 }, "Left and Shoot");
		//addAction(new int[] { 0, 1, 1 }, "Right and Shoot");
		addAction(new int[] { 0, 0, 1 }, "Still and Shoot");
		// Other actions added as of 6/1/2016 by Gabby Gonzalez
		addAction(new int[] { 1, 0, 0 }, "Left");
		addAction(new int[] { 0, 1, 0 }, "Right");
		//addAction(new int[] { 0, 0, 0 }, "Still");
	}

	@Override
	public void setRewards() {
		game.setLivingReward(-1);
	}
	
	@Override
	public void setDoomStateVariables() {
		// Adds game variables that will be included in state.
		game.addAvailableGameVariable(GameVariable.AMMO2);
	}

	@Override
	public int numInputs() {
		if(Parameters.parameters.booleanParameter("doomFullScreenInput") && inputRow == -1){
			return (game.getScreenHeight() * game.getScreenWidth());
		}
		return game.getScreenWidth();
	}

	/**
	 * Inputs are from a single row in roughly the middle of the screen. The row
	 * should cross through the eye of the monster, creating a high contrast.
	 * Also, only the red color values are checked.
	 * 
	 * @param s
	 *            Game state
	 * @return scaled red color values from specified row
	 */
	@Override
	public double[] getInputs(GameState s) {
		if(Parameters.parameters.booleanParameter("doomFullScreenInput") && inputRow == -1){
			return colorFromScreen(s, RED_INDEX);
		}
		return colorFromRow(s, inputRow, RED_INDEX);
	}
        
	/**
	 * For this particular task, the labels should match the row inputs
	 * (coordinates)
	 * 
	 * @return
	 */
	@Override
	public String[] sensorLabels() {
		if(Parameters.parameters.booleanParameter("doomFullScreenInput") && inputRow == -1){
			return screenSensorLabels(game.getScreenWidth(), game.getScreenHeight());
		}
		return rowSensorLabels(game.getScreenWidth());
	}

	/**
	 * Test run in the domain
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomBasicShootTask", "trials:3", "printFitness:true", "doomFullScreenInput:true" });
		MMNEAT.loadClasses();
		VizDoomBasicShootTask<TWEANN> vd = new VizDoomBasicShootTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
