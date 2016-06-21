package edu.utexas.cs.nn.tasks.vizdoom;

import java.util.Arrays;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import vizdoom.Button;
import vizdoom.DoomGame;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomDefendCenterTask<T extends Network> extends VizDoomTask<T> {

	private final boolean moVizDoom;
	
	public VizDoomDefendCenterTask() {
		super();
		moVizDoom = Parameters.parameters.booleanParameter("moVizDoom");
	
		if(moVizDoom) { 
			//Register fitness functions
			MMNEAT.registerFitnessFunction("Time Spent Alive");
			MMNEAT.registerFitnessFunction("Targets Hit"); 
			//Register the "other" score
			MMNEAT.registerFitnessFunction("Doom Reward", null, false);
		} else {
			//Register the 1 fitness
			MMNEAT.registerFitnessFunction("Doom Reward");
		}
	}

	@Override
	public Pair<double[], double[]> getFitness(DoomGame game){
		double[] fitness = new double[] { game.getTotalReward() }; // default
		double[] other = new double[] {};
		if(moVizDoom) {
			fitness = new double[] { game.getEpisodeTime(), game.getGameVariable(GameVariable.KILLCOUNT) };
			other = new double[] { game.getTotalReward() };
			//Note, not 100% sure this is the correct way to get the current time and number of enemies killed -Gab
		}
		return new Pair<double[], double[]>(fitness, other);
	}
	
	@Override
	public int numObjectives() {
		if(moVizDoom) {
			return 2;
		} else {
			return 1;
		}
	}
	
	@Override
	public int numOtherScores() {
		if(moVizDoom) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/defend_the_center.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/defend_the_center.wad");
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
		game.addAvailableButton(Button.ATTACK);
		game.addAvailableButton(Button.TURN_RIGHT);
		
		addAction(new int[] { 1, 0, 0 }, "Turn left");
		addAction(new int[] { 0, 1, 0 }, "Stand still and shoot");
		addAction(new int[] { 0, 0, 1 }, "Turn right");
		
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
		//We need -1 for missed shots, +1 for hits, -1 for dying	
		game.setDeathPenalty(1);
		//we don't want a living penalty since the penalty for dying is there, we want to stay alive until the timeout
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
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDefendCenterTask", "trials:3", "printFitness:true", "doomInputStartX:0", 
				"doomInputStartY:70", "doomInputHeight:15", "doomInputWidth:200", "doomInputPixelSmudge:5", 
				"doomSmudgeStat:edu.utexas.cs.nn.util.stats.MostExtreme", "stepByStep:true", "showVizDoomInputs:true"});
		MMNEAT.loadClasses();
		VizDoomDefendCenterTask<TWEANN> vd = new VizDoomDefendCenterTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}

	@Override
	public Pair<Integer, Integer> outputSubstrateSize() {
		return new Pair<Integer, Integer>(actions.size(), 1);
	}
}
