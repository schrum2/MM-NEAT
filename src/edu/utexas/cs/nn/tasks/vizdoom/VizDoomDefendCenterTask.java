package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;
import vizdoom.ScreenResolution;

public class VizDoomDefendCenterTask<T extends Network> extends VizDoomTask<T> {

	// Save the inputRow once instead of recalculating it on every time step
	private final int inputRow;
	
	public VizDoomDefendCenterTask() {
		super();
		inputRow = getRow();
		game.loadConfig("vizdoom/examples/config/defend_the_center.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/" + "defend_the_center.wad");
		game.setScreenResolution(ScreenResolution.RES_320X240);	
	}
	
	private int getRow() {
		float first;
		int second;
		if (game.getScreenWidth() / 4 == game.getScreenHeight() / 3) { 
			// ratio is 4:3
			first = (float) (game.getScreenWidth() * 0.3825);
			second = Math.round(first);
		} else if (game.getScreenWidth() / 16 == game.getScreenHeight() / 10) { 
			// ratio is 16:10
			first = (float) (game.getScreenWidth() * 0.32); 
			second = Math.round(first);
		} else if (game.getScreenWidth() / 16 == game.getScreenHeight() / 9) { 
			// ratio is 16:9
			first = (float) (game.getScreenWidth() * 0.29); 
			second = Math.round(first);
		} else { // ratio is 5:4
			first = (float) (game.getScreenWidth() * 0.41); 
			second = Math.round(first);
		}
		return second;
	}

	@Override
	public String[] sensorLabels() {
		String[] labels = new String[game.getScreenWidth() + 3]; //magic numbers, these can change later -Gab
		labels[0] = "Health";
		labels[1] = "Ammo";
		labels[2] = "Time";
		for(int i = 3; i < labels.length ; i++){
			labels[i] = "Column " + i;
		}
		return labels;
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
		//game.addAvailableGameVariable(null); Do we need to add something for time? Like, to allow the game to time out? -Gab
	}

	@Override
	public double[] getInputs(GameState s) {
		double[] temp = colorFromRow(s, inputRow, RED_INDEX);
		double[] inputs = new double[temp.length + 3];
		int in = 0;
		inputs[in++] = game.getGameVariable(GameVariable.HEALTH) / 100.0; //health
		inputs[in++] = game.getGameVariable(GameVariable.AMMO2) / 26.0; //ammo
		inputs[in++] = 1 - (game.getEpisodeTime() / (double)Parameters.parameters.integerParameter("doomEpisodeLength")); //time
		//System.out.println("Health: " + inputs[0] + " Ammo: " + inputs[1] + " Time: " + inputs[2]);
		for(int i = in; i < inputs.length; i++){
			inputs[i] = temp[i-3];
		}
		
		return inputs;
		//return colorFromRow(s, inputRow, RED_INDEX); // this needs to change to be in color later? -Gab
	}

	@Override
	public void setRewards() {
		//We need -1 for missed shots, +1 for hits, -1 for dying	
		game.setDeathPenalty(1);
		//we don't want a living penalty since the penalty for dying is there, we want to stay alive until the timeout
	}

	@Override
	public int numInputs() {
		return (game.getScreenWidth() + 3); // magic number here too, the 3 refers to the Health, Ammo, and Time -Gab
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
