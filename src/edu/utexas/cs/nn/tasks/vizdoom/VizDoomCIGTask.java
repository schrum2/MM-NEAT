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

public class VizDoomCIGTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomCIGTask() {
		super();
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/cig.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/cig.wad");
		game.setDoomMap("map01");
	}

	@Override
	public String[] sensorLabels() {
		return getSensorLabels(Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				Parameters.parameters.integerParameter("doomInputWidth"), 
				Parameters.parameters.integerParameter("doomInputHeight"), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	@Override
	public void setDoomActions() {
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		game.addAvailableButton(Button.ATTACK);
		
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.MOVE_LEFT);
		
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.MOVE_BACKWARD);
		game.addAvailableButton(Button.TURN_LEFT_RIGHT_DELTA);
		game.addAvailableButton(Button.LOOK_UP_DOWN_DELTA);
		
		addAction(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0 }, "Turn left");
		addAction(new int[] { 0, 1, 0, 0, 0, 0, 0, 0, 0 }, "Turn right");
		addAction(new int[] { 0, 0, 1, 0, 0, 0, 0, 0, 0 }, "Attack");
		addAction(new int[] { 0, 0, 0, 1, 0, 0, 0, 0, 0 }, "Move right");
		addAction(new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0 }, "Move left");
		addAction(new int[] { 0, 0, 0, 0, 0, 1, 0, 0, 0 }, "Move forward");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 1, 0, 0 }, "Move backward");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 0 }, "Turn left/right delta");
		addAction(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1 }, "Look up/down delta");
		
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.HEALTH);
	}

	@Override
	public double[] getInputs(GameState s) {
		return getInputs(s, Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				Parameters.parameters.integerParameter("doomInputWidth"), 
				Parameters.parameters.integerParameter("doomInputHeight"), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	@Override
	public void setRewards() {
	}

	@Override
	public int numInputs() {
		if(Parameters.parameters.integerParameter("doomInputColorVal") == 3){
			return (Parameters.parameters.integerParameter("doomInputWidth") * Parameters.parameters.integerParameter("doomInputHeight") * 3);
		}
		return (Parameters.parameters.integerParameter("doomInputWidth") * Parameters.parameters.integerParameter("doomInputHeight"));
	}

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false", "doomEpisodeLength:25200",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomCIGTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomCIGTask<TWEANN> vd = new VizDoomCIGTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public double[] interpretOutputs(double[] rawOutputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Triple<String, Integer, Integer>> getOutputInfo() {
		List<Triple<String, Integer, Integer>> outputs = new ArrayList<Triple<String, Integer, Integer>>();
		// TODO Auto-generated method stub
		return outputs;
	}

	@Override
	public List<String> getOutputNames() {
		List<String> outputs = new ArrayList<String>();
		// TODO Auto-generated method stub
		return outputs;
	}
	
	public void addDeadNeurons(List<Substrate> subs){
		// This Task does not have Dead Neurons
	}
	
}
