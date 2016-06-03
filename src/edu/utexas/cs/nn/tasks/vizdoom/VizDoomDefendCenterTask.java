package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomDefendCenterTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;

	public VizDoomDefendCenterTask() {
		super();
		inputRow = getRow(game.getScreenWidth(), game.getScreenHeight()); 
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/defend_the_center.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/defend_the_center.wad");
		game.setDoomMap("map01");
	}

	@Override
	public String[] sensorLabels() {
		return rowSensorLabels(game.getScreenWidth());
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		game.addAvailableButton(Button.ATTACK);
		addAction(new int[] { 1, 0, 0 }, "Turn left");
		addAction(new int[] { 0, 1, 0 }, "Turn right");
		addAction(new int[] { 0, 0, 1 }, "Stand still and shoot");
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.HEALTH);
		game.addAvailableGameVariable(GameVariable.AMMO2);
	}

	@Override
	public double[] getInputs(GameState s) {
		return colorFromRow(s, inputRow, RED_INDEX);
	}

	@Override
	public void setRewards() {
		//We need -1 for missed shots, +1 for hits, -1 for dying	
		game.setDeathPenalty(1);
		//we don't want a living penalty since the penalty for dying is there, we want to stay alive until the timeout
	}

	@Override
	public int numInputs() {
		return game.getScreenWidth();
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:2100",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDefendCenterTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomDefendCenterTask<TWEANN> vd = new VizDoomDefendCenterTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
