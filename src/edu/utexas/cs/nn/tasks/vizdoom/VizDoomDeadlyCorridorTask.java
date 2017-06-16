package edu.utexas.cs.nn.tasks.vizdoom;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Triple;
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
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.MOVE_BACKWARD);
		
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		
		game.addAvailableButton(Button.ATTACK);
			
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0 }, "Move Forward");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0 }, "Move Left");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0 }, "Move Right");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0 }, "Move Backward");		
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0 }, "Turn Left");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0 }, "Turn Right");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1 }, "Attack");
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
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false", "doomEpisodeLength:2100",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDeadlyCorridorTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomDeadlyCorridorTask<TWEANN> vd = new VizDoomDeadlyCorridorTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[7];
		if(Parameters.parameters.booleanParameter("hyperNEAT")){
			action[0] = rawOutputs[1]; // Forward
			action[1] = rawOutputs[3]; // Left
			action[2] = rawOutputs[5]; // Right
			action[3] = rawOutputs[7]; // Backward
			action[4] = rawOutputs[9]; // Turn Left
			action[5] = rawOutputs[10]; // Turn Right
			action[6] = rawOutputs[11]; // Attack
		} else {
			action[0] = rawOutputs[0]; // Forward
			action[1] = rawOutputs[1]; // Left
			action[2] = rawOutputs[2]; // Right
			action[3] = rawOutputs[3]; // Backward
			action[4] = rawOutputs[4]; // Turn Left
			action[5] = rawOutputs[5]; // Turn Right
			action[6] = rawOutputs[6]; // Attack
		}
		return action;
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		
		outputs.add(new Triple<String, Integer, Integer>("D-Pad Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		// Corners and center of D-pad are not used
//		dpad.addDeadNeuron(0,0);
//		dpad.addDeadNeuron(0,2);
//		dpad.addDeadNeuron(1,1);
//		dpad.addDeadNeuron(2,0);
//		dpad.addDeadNeuron(2,2);
		
		outputs.add(new Triple<String, Integer, Integer>("C-Stick Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		outputs.add(new Triple<String, Integer, Integer>("Button Output", 0, Substrate.OUTPUT_SUBSTRATE));
		
		return outputs;
	}

	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		
		outputs.add("D-Pad Outputs");
		outputs.add("C-Stick Outputs");
		outputs.add("Button Output");
		
		return outputs;
	}
}
