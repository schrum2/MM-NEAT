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

public class VizDoomDefendLineTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomDefendLineTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/defend_the_line.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/defend_the_line.wad");
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
		
		addAction(new int[] { 1, 0, 0 }, "Left");
		addAction(new int[] { 0, 1, 0 }, "Right");
		
		addAction(new int[] { 0, 0, 1 }, "Still and Shoot");	
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.AMMO2);
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
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDefendLineTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomDefendLineTask<TWEANN> vd = new VizDoomDefendLineTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	//not needed, but easier to just implement
	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		double[] action = new double[3];
		action[0] = rawOutputs[0]; // Left
		action[1] = rawOutputs[1]; // Right
		action[2] = rawOutputs[2]; // Shoot
		return action;
	}

	@Override
	public void addOutputSubstrates(List<Substrate> subs) {
		Substrate dpadOutputs = new Substrate(new Pair<Integer, Integer>(2, 1), 
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "D-Pad Outputs");
		subs.add(dpadOutputs);
		Substrate buttonOutputs = new Substrate(new Pair<Integer, Integer>(1, 1), 
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Button Output");
		subs.add(buttonOutputs);
	}

	@Override
	public void addOutputConnections(List<Pair<String, String>> conn) {
		conn.add(new Pair<String, String>("Processing", "D-Pad Outputs"));
		conn.add(new Pair<String, String>("Processing", "Button Output"));		
	}
}
