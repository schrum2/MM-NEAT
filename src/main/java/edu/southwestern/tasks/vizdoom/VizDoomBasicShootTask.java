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
import vizdoom.GameVariable;

public class VizDoomBasicShootTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomBasicShootTask() {
		super();
		//Register the 1 fitness
		MMNEAT.registerFitnessFunction("Doom Reward");
	}

    @Override
    public void taskSpecificInit() {
        game.loadConfig("vizdoom/examples/config/basic.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/basic.wad");
		game.setDoomMap("map01");
	}
        
        @Override
	public void setDoomActions() {
		// Adds buttons that will be allowed.
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		
		game.addAvailableButton(Button.ATTACK);
		
		// Define some actions. Each list entry corresponds to declared buttons:
		// MOVE_LEFT, MOVE_RIGHT, ATTACK
		// more combinations are naturally possible but only 3 are included for
		// transparency when watching.
		addAction(new int[] { 1, 0, 0 }, "Left");
		addAction(new int[] { 0, 1, 0 }, "Right");
		
		addAction(new int[] { 0, 0, 1 }, "Still and Shoot");
	}

	@Override
	public void setRewards() {
		game.setLivingReward(-1);
	}
	
	@Override
	public void setDoomStateVariables() {
		// Adds game variables that will be included in state.
		game.addAvailableGameVariable(GameVariable.AMMO2);
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

	/**
	 * Inputs are from a single row in roughly the middle of the screen. The row
	 * should cross through the eye of the monster, creating a high contrast.
	 * Also, only the red color values are checked.
	 * 
	 * @param s
	 *            Game state
	 * @return scaled red color values from specified row
	 */
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
        
	/**
	 * For this particular task, the labels should match the row inputs
	 * (coordinates)
	 * 
	 * @return
	 */
	@Override
	public String[] sensorLabels() {
		return getSensorLabels(Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				(Parameters.parameters.integerParameter("doomInputWidth") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				(Parameters.parameters.integerParameter("doomInputHeight") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	/**
	 * Test run in the domain
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false",
				"task:edu.southwestern.tasks.vizdoom.VizDoomBasicShootTask", "trials:5", "printFitness:true", 
				"doomFullScreenInput:false", "doomInputWidth:200", "doomInputHeight:5", "doomInputStartX:0", 
				"doomInputStartY:75","doomInputColorVal:2", "doomInputPixelSmudge:5", "doomSmudgeStat:edu.southwestern.util.stats.MostExtreme", "hyperNEAT:true", "extraHNLinks:true"});
		MMNEAT.loadClasses();
		VizDoomBasicShootTask<TWEANN> vd = new VizDoomBasicShootTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		
		outputs.add(new Triple<String, Integer, Integer>("D-Pad Outputs", 2, 1));
		outputs.add(new Triple<String, Integer, Integer>("Button Output", 1, 1));
		
		return outputs;
	}

	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		
		outputs.add("D-Pad Outputs");
		outputs.add("Button Output");
		
		return outputs;
	}

	public void addDeadNeurons(List<Substrate> subs){
		// This Task does not have Dead Neurons
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
}
