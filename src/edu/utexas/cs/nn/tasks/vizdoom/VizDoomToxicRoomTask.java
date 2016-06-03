package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomToxicRoomTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;
	
	public VizDoomToxicRoomTask() {
		super();
		inputRow = getRow(game.getScreenWidth(), game.getScreenHeight()); 
	}

        @Override
        public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/health_gathering.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/health_gathering.wad");
		game.setDoomMap("map01");
        }
        
	@Override
	public String[] sensorLabels() {
		return rowSensorLabels(game.getScreenWidth());
	}

	@Override
	public void setDoomActions() {
		// Adds buttons that will be allowed.
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		game.addAvailableButton(Button.MOVE_FORWARD);

		addAction(new int[] { 1, 0, 1 }, "Turn left and and move forward");
		addAction(new int[] { 0, 1, 1 }, "Turn right and and move forward");
		addAction(new int[] { 0, 0, 1 }, "Move forward");
	}

	@Override
	public void setDoomStateVariables() {
		// Adds game variables that will be included in state.
		game.addAvailableGameVariable(GameVariable.HEALTH);
		// game.addAvailableGameVariable(GameVariable.ON_GROUND); // what does this count? just curious -Gab
	}

	@Override
	public double[] getInputs(GameState s) {
		// basic shoot task was "colorFromRow(s, inputRow, RED_INDEX)"
		// What would we want here?
		return colorFromRow(s, inputRow, RED_INDEX);
	}

	@Override
	public void setRewards() {
		game.setLivingReward(-1); // needs to be higher? -Gab		
	}

	@Override
	public int numInputs() {
		return game.getScreenWidth();
	}
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomToxicRoomTask", "trials:3", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomToxicRoomTask<TWEANN> vd = new VizDoomToxicRoomTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
