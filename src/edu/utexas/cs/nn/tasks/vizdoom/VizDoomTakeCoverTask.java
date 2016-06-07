package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomTakeCoverTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;

	public VizDoomTakeCoverTask() {
		super();
		if(!Parameters.parameters.booleanParameter("doomFullScreenInput")){
			inputRow = getRow(game.getScreenWidth(), game.getScreenHeight());
		} else {
			inputRow = -1; // this is for a check 
        }
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/take_cover.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/take_cover.wad");
		game.setDoomMap("map01");
	}

	@Override
	public String[] sensorLabels() {
		if(Parameters.parameters.booleanParameter("doomFullScreenInput") && inputRow == -1){
			return screenSensorLabels(game.getScreenWidth(), game.getScreenHeight());
		}
		return rowSensorLabels(game.getScreenWidth());
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		addAction(new int[] { 1, 0 }, "Move left");
		addAction(new int[] { 0, 1 }, "Move right");
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.HEALTH);
	}

	@Override
	public double[] getInputs(GameState s) {
		if(Parameters.parameters.booleanParameter("doomFullScreenInput") && inputRow == -1){
			return colorFromScreen(s, RED_INDEX);
		}
		return colorFromRow(s, inputRow, RED_INDEX);
	}

	@Override
	public void setRewards() {
		game.setLivingReward(1);
	}

	@Override
	public int numInputs() {
		if(Parameters.parameters.booleanParameter("doomFullScreenInput") && inputRow == -1){
			return (game.getScreenHeight() * game.getScreenWidth());
		}
		return game.getScreenWidth();
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:10000",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomTakeCoverTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomTakeCoverTask<TWEANN> vd = new VizDoomTakeCoverTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
