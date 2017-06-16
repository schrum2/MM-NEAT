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

public class VizDoomHealthGatherTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomHealthGatherTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
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
		return getSensorLabels(Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				(Parameters.parameters.integerParameter("doomInputWidth") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				(Parameters.parameters.integerParameter("doomInputHeight") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		
		addAction(new int[] { 1, 0, 0 }, "Move Forward");
		addAction(new int[] { 0, 1, 0 }, "Turn Left");
		addAction(new int[] { 0, 0, 1 }, "Turn Right");
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
		game.setLivingReward(1);
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
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:10000",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomHealthGatherTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomHealthGatherTask<TWEANN> vd = new VizDoomHealthGatherTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[3];
		if(Parameters.parameters.booleanParameter("hyperNEAT")){
			action[0] = rawOutputs[1]; // Forward
			action[1] = rawOutputs[3]; // Left
			action[2] = rawOutputs[5]; // Right
		}
			action[0] = rawOutputs[0]; // Forward
			action[1] = rawOutputs[1]; // Left
			action[2] = rawOutputs[2]; // Right
		return action;
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		outputs.add(new Triple<String, Integer, Integer>("D-Pad Outputs", 0, Substrate.OUTPUT_SUBSTRATE));
		
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
		dPad.addDeadNeuron(2,0);
		dPad.addDeadNeuron(1,1);	
	}
	
	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		
		outputs.add("D-Pad Outputs");
		
		return outputs;
	}
}
