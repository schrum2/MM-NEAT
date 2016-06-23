package edu.utexas.cs.nn.tasks.vizdoom;

import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomMultiDeathMatchTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomMultiDeathMatchTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/multi.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/multi_deathmatch.wad");
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
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.MOVE_BACKWARD);
		
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		
		game.addAvailableButton(Button.ATTACK);
		
		game.addAvailableButton(Button.TURN_LEFT_RIGHT_DELTA);
		game.addAvailableButton(Button.LOOK_UP_DOWN_DELTA);
		
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move Forward");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0 }, "Move Left");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0, 0, 0 }, "Move Right");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0, 0, 0 }, "Move Backward");
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0 }, "Turn Left");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0, 0, 0 }, "Turn Right");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1, 0, 0 }, "Attack");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 0 }, "Turn left/right delta");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1 }, "Look up/down delta");
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.HEALTH);
		game.addAvailableGameVariable(GameVariable.AMMO3);
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
		game.setDeathPenalty(1);
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
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomMultiDeathMatchTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomMultiDeathMatchTask<TWEANN> vd = new VizDoomMultiDeathMatchTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[9];
		action[0] = rawOutputs[1]; // Forward
		action[1] = rawOutputs[3]; // Left
		action[2] = rawOutputs[5]; // Right
		action[3] = rawOutputs[7]; // Backward
		action[4] = rawOutputs[9]; // Turn Left
		action[5] = rawOutputs[10]; // Turn Right
		action[6] = rawOutputs[11]; // Attack
		action[7] = rawOutputs[12]; // Turn left/right delta
		action[8] = rawOutputs[13]; // Look up/down delta
		return action;
	}

	@Override
	public void addOutputSubstrates(List<Substrate> subs) {
		Substrate dpad = new Substrate(new Pair<Integer, Integer>(3, 3), 
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "D-Pad Outputs");
		subs.add(dpad);
		Substrate cstick = new Substrate(new Pair<Integer, Integer>(2, 1), 
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "C-Stick Outputs");
		subs.add(cstick);
		Substrate buttons = new Substrate(new Pair<Integer, Integer>(1, 1), 
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Button Output");
		subs.add(buttons);
		Substrate etc = new Substrate(new Pair<Integer, Integer>(2, 1), 
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Misc. Outputs");
		subs.add(etc);
	}

	@Override
	public void addOutputConnections(List<Pair<String, String>> conn) {
		conn.add(new Pair<String, String>("Processing", "D-Pad Outputs"));
		conn.add(new Pair<String, String>("Processing", "C-Stick Outputs"));
		conn.add(new Pair<String, String>("Processing", "Button Output"));
		conn.add(new Pair<String, String>("Processing", "Misc. Outputs"));
	}
}
