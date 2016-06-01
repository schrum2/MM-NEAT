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
		game.loadConfig("vizdoom/examples/config/defend_the_center.cfg");
		inputRow = getRow(); 
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
		// Should we add these as well? Or are they also not necessary? -Gab
//		addAction(new int[] { 1, 0, 1 }, "Turn left and and shoot");
//		addAction(new int[] { 0, 1, 1 }, "Turn right and and shoot");
//		addAction(new int[] { 0, 0, 0 }, "Stand still");
	}

	@Override
	public void setDoomStateVariables() {
		// TODO Auto-generated method stub
		game.addAvailableGameVariable(GameVariable.HEALTH);
		game.addAvailableGameVariable(GameVariable.AMMO2);
		//game.addAvailableGameVariable(null); Do we need to add something for time? Like, to allow the game to time out? -Gab
	}

	@Override
	public double[] getInputs(GameState s) {
		return colorFromRow(s, inputRow, RED_INDEX); // this needs to change to be in color later? -Gab
	}

	@Override
	public void setRewards() {
		//We need -1 for missed shots, +1 for hits, -1 for dying	
		game.setDeathPenalty(-1);
		//we don't want a living penalty since the penalty for dying is there, we want to stay alive until the timeout
	}

	@Override
	public int numInputs() {
		return game.getScreenWidth() + 3; // magic number here too, the 3 refers to the Health, Ammo, and Time -Gab
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false", "doomEpisodeLength:2100",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomBasicShootTask", "trials:3", "printFitness:true", "scenarioWad:defend_the_center.wad" });
		MMNEAT.loadClasses();
		VizDoomBasicShootTask<TWEANN> vd = new VizDoomBasicShootTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
