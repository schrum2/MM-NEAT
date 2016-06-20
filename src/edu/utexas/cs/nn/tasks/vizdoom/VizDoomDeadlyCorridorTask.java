package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomDeadlyCorridorTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomDeadlyCorridorTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/deadly_corridor.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/deadly_corridor.wad");
		game.setDoomMap("map01");
	}

	@Override
	public String[] sensorLabels() {
		return getSensorLabels(Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				(Parameters.parameters.integerParameter("doomInputWidth") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				(Parameters.parameters.integerParameter("doomInputHeight") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.ATTACK);
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.MOVE_BACKWARD);
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0 }, "Move left");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0 }, "Move right");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0 }, "Attack");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0 }, "Move forward");		
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0 }, "Move backward");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0 }, "Turn left");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1 }, "Turn right");
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.HEALTH);
	}

	@Override
	public double[] getInputs(GameState s) {
		double[] inputs = getInputs(s, Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				Parameters.parameters.integerParameter("doomInputWidth"), 
				Parameters.parameters.integerParameter("doomInputHeight"), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
		if(Parameters.parameters.integerParameter("doomInputPixelSmudge") > 1){
			return smudgeInputs(inputs, Parameters.parameters.integerParameter("doomInputWidth"), 
					Parameters.parameters.integerParameter("doomInputHeight"), 
					Parameters.parameters.integerParameter("doomInputColorVal"), 
					Parameters.parameters.integerParameter("doomInputPixelSmudge"));
		}else{
			return inputs;
		}
	}

	@Override
	public void setRewards() {
		game.setLivingReward(0);
		game.setDeathPenalty(100);
	}

	@Override
	public int numInputs() {
		int smudge = Parameters.parameters.integerParameter("doomInputPixelSmudge");
		int width = Parameters.parameters.integerParameter("doomInputWidth") / smudge;
		int height = Parameters.parameters.integerParameter("doomInputHeight") / smudge;
		
		if(Parameters.parameters.integerParameter("doomInputColorVal") == 3){
			return (width * height * 3);
		}
		return (width * height);
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:2100",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDeadlyCorridorTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomDeadlyCorridorTask<TWEANN> vd = new VizDoomDeadlyCorridorTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public Pair<Integer, Integer> outputSubstrateSize() {
		return null; //complex, wait to write
	}
}
