package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomDeathMatchTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;

	public VizDoomDeathMatchTask() {
		super();
		inputRow = getRow(game.getScreenWidth(), game.getScreenHeight()); 
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/deathmatch.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/deathmatch.wad");
		game.setDoomMap("map01");
	}

	@Override
	public String[] sensorLabels() {
		return rowSensorLabels(game.getScreenWidth());
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.ATTACK);
		game.addAvailableButton(Button.SPEED);
		game.addAvailableButton(Button.STRAFE);
		
		game.addAvailableButton(Button.MOVE_RIGHT);	game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_BACKWARD);		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.TURN_RIGHT);		game.addAvailableButton(Button.TURN_LEFT);
		
		game.addAvailableButton(Button.SELECT_NEXT_WEAPON);
		game.addAvailableButton(Button.SELECT_PREV_WEAPON);
		
		game.addAvailableButton(Button.LOOK_UP_DOWN_DELTA);
		game.addAvailableButton(Button.TURN_LEFT_RIGHT_DELTA);
		game.addAvailableButton(Button.MOVE_LEFT_RIGHT_DELTA);
		
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Attack");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Speed up");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Strafe");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move right");
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move left");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move backward");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, "Move forward");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, "Turn right");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, "Turn left");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, "Next weapon");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, "Previous weapon");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, "Look up/down delta");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 }, "Turn left/right delta");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, "Move left/right delta");

	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.KILLCOUNT);
		game.addAvailableGameVariable(GameVariable.HEALTH);
		game.addAvailableGameVariable(GameVariable.ARMOR);
		game.addAvailableGameVariable(GameVariable.SELECTED_WEAPON);
		game.addAvailableGameVariable(GameVariable.SELECTED_WEAPON_AMMO);
	}

	@Override
	public double[] getInputs(GameState s) {
		return colorFromRow(s, inputRow, RED_INDEX);
	}

	@Override
	public void setRewards() {
	}

	@Override
	public int numInputs() {
		return game.getScreenWidth();
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:4200",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDeathMatchTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomDeathMatchTask<TWEANN> vd = new VizDoomDeathMatchTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
