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

public class VizDoomDeathMatchTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomDeathMatchTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/deathmatch.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/deathmatch.wad");
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
		game.addAvailableButton(Button.SPEED);
		game.addAvailableButton(Button.STRAFE);
		
		game.addAvailableButton(Button.SELECT_PREV_WEAPON);
		game.addAvailableButton(Button.SELECT_NEXT_WEAPON);
		
		game.addAvailableButton(Button.LOOK_UP_DOWN_DELTA);
		game.addAvailableButton(Button.TURN_LEFT_RIGHT_DELTA);
		game.addAvailableButton(Button.MOVE_LEFT_RIGHT_DELTA);
		
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move Forward");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move Left");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move Right");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Move Backward");
		
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "Turn Left");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, "Turn Right");
		
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, "Attack");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, "Speed");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, "Strafe");
		
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, "Previous weapon");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, "Next weapon");
		
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
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false", "doomEpisodeLength:4200",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDeathMatchTask", "trials:8", "printFitness:true","showVizDoomInputs:true", "stepByStep:true"});
		MMNEAT.loadClasses();
		VizDoomDeathMatchTask<TWEANN> vd = new VizDoomDeathMatchTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		
		outputs.add(new Triple<String, Integer, Integer>("D-Pad Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		outputs.add(new Triple<String, Integer, Integer>("C-Stick Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		outputs.add(new Triple<String, Integer, Integer>("Button Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		outputs.add(new Triple<String, Integer, Integer>("Bumper Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		outputs.add(new Triple<String, Integer, Integer>("Misc. Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		
		return outputs;
	}
	
	public void addDeadNeurons(List<Substrate> subs){
		Substrate dPad = null; // Stores the Substrate associated with the D-Pad
		
		for(Substrate sub : subs){
			if(sub.getName().equals("D-Pad Outputs")){
				dPad = sub;
			}
		}
		
		// Corners and center of D-pad are not used
		dPad.addDeadNeuron(0,0);
		dPad.addDeadNeuron(0,2);
		dPad.addDeadNeuron(1,1);
		dPad.addDeadNeuron(2,0);
		dPad.addDeadNeuron(2,2);
	}
	
	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		
		outputs.add("D-Pad Outputs");
		outputs.add("C-Stick Outputs");
		outputs.add("Button Outputs");
		outputs.add("Bumper Outputs");
		outputs.add("Misc. Outputs");
		
		return outputs;
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[14];
		if(Parameters.parameters.booleanParameter("hyperNEAT")){
			action[0] = rawOutputs[1]; // Forward
			action[1] = rawOutputs[3]; // Left
			action[2] = rawOutputs[5]; // Right
			action[3] = rawOutputs[7]; // Backward
			action[4] = rawOutputs[9]; // Turn Left
			action[5] = rawOutputs[10]; // Turn Right
			action[6] = rawOutputs[11]; // Attack
			action[7] = rawOutputs[12]; // Speed
			action[8] = rawOutputs[13]; // Strafe
			action[9] = rawOutputs[14]; // Previous Weapon
			action[10] = rawOutputs[15]; // Next Weapon
			action[11] = rawOutputs[16]; // Look up/down delta
			action[12] = rawOutputs[17]; // Turn left/right delta
			action[13] = rawOutputs[18]; // Move left/right delta
		} else {
			action[0] = rawOutputs[0]; // Forward
			action[1] = rawOutputs[1]; // Left
			action[2] = rawOutputs[2]; // Right
			action[3] = rawOutputs[3]; // Backward
			action[4] = rawOutputs[4]; // Turn Left
			action[5] = rawOutputs[5]; // Turn Right
			action[6] = rawOutputs[6]; // Attack
			action[7] = rawOutputs[7]; // Speed
			action[8] = rawOutputs[8]; // Strafe
			action[9] = rawOutputs[9]; // Previous Weapon
			action[10] = rawOutputs[10]; // Next Weapon
			action[11] = rawOutputs[11]; // Look up/down delta
			action[12] = rawOutputs[12]; // Turn left/right delta
			action[13] = rawOutputs[13]; // Move left/right delta
		}
		return action;
	}
}
