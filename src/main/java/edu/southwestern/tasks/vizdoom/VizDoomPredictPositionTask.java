package edu.southwestern.tasks.vizdoom;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Triple;
import vizdoom.Button;
import vizdoom.GameState;

public class VizDoomPredictPositionTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomPredictPositionTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/predict_position.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/predict_position.wad");
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
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);	
		
		game.addAvailableButton(Button.ATTACK);	
	
		addAction(new int[] { 1, 0, 0 }, "Turn Left");
		addAction(new int[] { 0, 1, 0 }, "Turn Right");
		
		addAction(new int[] { 0, 0, 1 }, "Attack");
	}

	@Override
	public void setDoomStateVariables() {
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

		game.setLivingReward(-0.001);
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
		Parameters.initializeParameterCollections(new String[] { "watch:false", "io:false", "netio:false", "doomEpisodeLength:300",
				"task:edu.southwestern.tasks.vizdoom.VizDoomPredictPositionTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomPredictPositionTask<TWEANN> vd = new VizDoomPredictPositionTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[3];
		action[0] = rawOutputs[0]; // Turn Left
		action[1] = rawOutputs[1]; // Turn Right
		action[2] = rawOutputs[2]; // Attack
		return null;
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		
		outputs.add(new Triple<String, Integer, Integer>("C-Stick Outputs", 2, 1));
		outputs.add(new Triple<String, Integer, Integer>("Button Output", 1, 1));
		
		return outputs;
	}
	
	public void addDeadNeurons(List<Substrate> subs){
		// This Task does not have Dead Neurons
	}
	
	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		
		outputs.add("C-Stick Outputs");
		outputs.add("Button Output");
		
		return outputs;
	}
}
