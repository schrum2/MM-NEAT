package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomHealthGatherTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;

	public VizDoomHealthGatherTask() {
		super();
		if(!Parameters.parameters.booleanParameter("doomFullScreenInput")){
			inputRow = getRow(game.getScreenWidth(), game.getScreenHeight());
		} else {
			inputRow = -1; // this is for a check 
        } 
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/health_gathering.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/health_gathering.wad");
		//game.setDoomScenarioPath("vizdoom/scenarios/health_gathering_supreme.wad");
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
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		game.addAvailableButton(Button.MOVE_FORWARD);
		addAction(new int[] { 1, 0, 0 }, "Turn left");
		addAction(new int[] { 0, 1, 0 }, "Turn right");
		addAction(new int[] { 0, 0, 1 }, "Move forward");
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
		game.setDeathPenalty(100);
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
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomHealthGatherTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomHealthGatherTask<TWEANN> vd = new VizDoomHealthGatherTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
