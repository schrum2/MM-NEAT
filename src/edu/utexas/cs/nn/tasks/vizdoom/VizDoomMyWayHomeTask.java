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

public class VizDoomMyWayHomeTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomMyWayHomeTask() {
		super();
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/my_way_home.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/my_way_home.wad");
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
		game.addAvailableButton(Button.MOVE_FORWARD);
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		addAction(new int[] { 1, 0, 0, 0, 0 }, "Turn left");
		addAction(new int[] { 0, 1, 0, 0, 0 }, "Turn right");
		addAction(new int[] { 0, 0, 1, 0, 0 }, "Move forward");
		addAction(new int[] { 0, 0, 0, 1, 0 }, "Move left");
		addAction(new int[] { 0, 0, 0, 0, 1 }, "Move right");
	
	}

	@Override
	public void setDoomStateVariables() {
		game.addAvailableGameVariable(GameVariable.AMMO0);
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

		game.setLivingReward(-0.0001);
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
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomMyWayHomeTask", "trials:8", "printFitness:true"});
		MMNEAT.loadClasses();
		VizDoomMyWayHomeTask<TWEANN> vd = new VizDoomMyWayHomeTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public Pair<Integer, Integer> outputSubstrateSize() {
		// TODO Auto-generated method stub
		return null;
	}
}
