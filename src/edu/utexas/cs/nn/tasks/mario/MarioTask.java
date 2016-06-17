package edu.utexas.cs.nn.tasks.mario;

import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.Evaluator;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class MarioTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	private EvaluationOptions options;

	public MarioTask(){
    	options = new CmdLineOptions(new String[0]);
        options.setLevelDifficulty(Parameters.parameters.integerParameter("marioLevelDifficulty"));
        options.setMaxFPS(!CommonConstants.watch); // Run fast when not watching
        options.setVisualization(CommonConstants.watch);
        options.setTimeLimit(Parameters.parameters.integerParameter("marioTimeLimit"));
        MMNEAT.registerFitnessFunction("Progress");
	}
	
	@Override
	public int numObjectives() {
		return 1; // default, just looking at distance traveled
	}

	@Override
	public double getTimeStamp() {
		// Not sure we can use this? -Gab
		return 0;
	}

	@Override
	public String[] sensorLabels() {
		
		return new String[]{"Top Left", "Top Middle", "Top Right", "Mid Left", "Center", "Mid Right", "Bottom Left", "Bottom Middle", "Bottom Right", "Bias"};
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Left", "Right", "Down", "Jump", "Speed", "??"};
		//Note: These may not be correct, as there are only 5/6 -Gab
	}

	
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		double distanceTravelled = 0;
		options.setAgent(new NNMarioAgent<T>(individual));
		options.setLevelRandSeed(RandomNumbers.randomGenerator.nextInt(Integer.MAX_VALUE));
		Evaluator evaluator = new Evaluator(options);
		List<EvaluationInfo> results = evaluator.evaluate();
		for (EvaluationInfo result : results) {
			distanceTravelled += result.computeDistancePassed();
		}
		distanceTravelled = distanceTravelled / results.size();
		return new Pair<double[], double[]>(new double[] { distanceTravelled }, new double[0]);
	}
	
	/**
	 * Setter for the task options 
         * @param options settings about evaluation
	 */
    public void setOptions(EvaluationOptions options) {
        this.options = options;
    }

    /**
     * Getter for the task options
     * @return EvaluationOptions options 
     */
    public EvaluationOptions getOptions() {
        return options;
    }

    /**
     * Simple test of MarioTask
     * @param args 
     */
    public static void main(String[] args){
    	Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", 
    			"task:edu.utexas.cs.nn.tasks.mario.MarioTask"});
    	MMNEAT.loadClasses();
    	EvolutionaryHistory.initArchetype(0);
    	TWEANNGenotype tg = new TWEANNGenotype(10,5,0);
    	MarioTask<TWEANN> mt = new MarioTask<TWEANN>();
    	Agent controller = new NNMarioAgent<TWEANN>(tg);
    	
    	EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        
        options.setMaxFPS(false);
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        options.setLevelRandSeed((int) (Math.random () * Integer.MAX_VALUE));
        options.setLevelDifficulty(3);
        mt.setOptions(options);
    	
    	mt.oneEval(tg, 0);
    	
    }
}
