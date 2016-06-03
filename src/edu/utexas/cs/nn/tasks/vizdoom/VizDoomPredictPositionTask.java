package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomPredictPositionTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;

	public VizDoomPredictPositionTask() {
		super();
		inputRow = getRow(game.getScreenWidth(), game.getScreenHeight()); 
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/predict_position.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/predict_position.wad");
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
		addAction(new int[] { 0, 0, 1 }, "Attack");

	}

	@Override
	public void setDoomStateVariables() {
	}

	@Override
	public double[] getInputs(GameState s) {
		return colorFromRow(s, inputRow, RED_INDEX);
	}

	@Override
	public void setRewards() {

		game.setLivingReward(-0.001);
	}

	@Override
	public int numInputs() {
		return game.getScreenWidth();
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:300",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomPredictPositionTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomPredictPositionTask<TWEANN> vd = new VizDoomPredictPositionTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
