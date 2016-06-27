package edu.utexas.cs.nn.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
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
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class MarioTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask, HyperNEATTask {

	private EvaluationOptions options;
	public static final int MARIO_OUTPUTS = 5; //need to find a way to make sure this isn't hardcoded

	public MarioTask(){
    	options = new CmdLineOptions(new String[0]);
        options.setLevelDifficulty(Parameters.parameters.integerParameter("marioLevelDifficulty"));
        options.setMaxFPS(!CommonConstants.watch); // Run fast when not watching
        options.setVisualization(CommonConstants.watch);
        options.setTimeLimit(Parameters.parameters.integerParameter("marioTimeLimit"));
        MMNEAT.registerFitnessFunction("Progress");
        
        if(Parameters.parameters.booleanParameter("moMario")){
        	 MMNEAT.registerFitnessFunction("Time");
        	 //MMNEAT.registerFitnessFunction("Hits");
        }
	}
	
	@Override
	public int numObjectives() {
		if(Parameters.parameters.booleanParameter("moMario")){
			return 2;
		} else {
			return 1;
		}
	}

	@Override
	public double getTimeStamp() {
		// Not sure we can use this? -Gab
		return 0;
	}

	@Override
	public String[] sensorLabels() {
		int xStart = Parameters.parameters.integerParameter("marioInputStartX");
		int yStart = Parameters.parameters.integerParameter("marioInputStartY");
		int width = Parameters.parameters.integerParameter("marioInputWidth");
		int height = Parameters.parameters.integerParameter("marioInputHeight");
		int xEnd = height + xStart;
		int yEnd = width + yStart;
		int worldBuffer = 0;
		int enemiesBuffer = (width * height);
		String[] labels = new String[((width * height) * 2) + 1];
		for(int x = xStart; x < xEnd; x++){
			for(int y = yStart; y < yEnd; y++){
				labels[worldBuffer++] = "Object at (" + x + ", " + y + ")";
				labels[enemiesBuffer++] = "Enemy at (" + x + ", " + y + ")";
			}
		}
		labels[enemiesBuffer++] = "Bias";		
		return labels;
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Left", "Right", "Down", "Jump", "Speed"};
		//Note: These may not be correct, as there are only 5/6 -Gab
	}

	
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		Pair<double[], double[]> evalResults;
		double distanceTravelled = 0;
		double marioMode = 0;
		double timeSpent = 0;
		options.setAgent(new NNMarioAgent<T>(individual));
		options.setLevelRandSeed(RandomNumbers.randomGenerator.nextInt(Integer.MAX_VALUE));
		Evaluator evaluator = new Evaluator(options);
		List<EvaluationInfo> results = evaluator.evaluate();
		for (EvaluationInfo result : results) {
			distanceTravelled += result.computeDistancePassed();
			timeSpent = result.timeSpentOnLevel;
			if(result.marioStatus == Mario.STATUS_WIN){
				timeSpent = result.totalTimeGiven;
			}
			marioMode = result.marioMode;
		}
		distanceTravelled = distanceTravelled / results.size();
		
		if(Parameters.parameters.booleanParameter("moMario")){
			//evalResults = new Pair<double[], double[]>(new double[] { distanceTravelled, timeSpent, marioMode }, new double[0]);
			evalResults = new Pair<double[], double[]>(new double[] { distanceTravelled, timeSpent }, new double[0]);
		} else {
			evalResults = new Pair<double[], double[]>(new double[] { distanceTravelled }, new double[0]);			
		}
		return evalResults;
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
    			"task:edu.utexas.cs.nn.tasks.mario.MarioTask", "marioInputStartX:-3", "marioInputStartY:-2", 
    			"marioInputWidth:12", "marioInputHeight:5", "showMarioInputs:false", "moMario:true", "marioJumpTimeout:20", "marioStuckTimeout:100"});
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

    /**
	 * Method that returns a list of information about the substrate layers
	 * contained in the network.
	 *
	 * @return List of Substrates in order from inputs to hidden to output
	 *         layers
	 */
	@Override
	public List<Substrate> getSubstrateInformation(){
		int height = Parameters.parameters.integerParameter("marioInputHeight");
		int width = Parameters.parameters.integerParameter("marioInputWidth");
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		Substrate inputsWorld = new Substrate(new Pair<Integer, Integer>(width, height), 
				Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Inputs World");
		subs.add(inputsWorld);
		Substrate inputsEnemies = new Substrate(new Pair<Integer, Integer>(width, height), 
				Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Inputs Enemies");
		subs.add(inputsEnemies);
		Substrate processing = new Substrate(new Pair<Integer, Integer>(width, height), 
				Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.PROCCESS_SUBSTRATE, 0), "Processing");
		subs.add(processing);
		Substrate outputsDpad = new Substrate(new Pair<Integer, Integer>(3, 2), //3 by 2 d-pad
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Outputs D-Pad");
		subs.add(outputsDpad);
		Substrate outputSpeed = new Substrate(new Pair<Integer, Integer>(1, 1), //1 by 1 button for speed
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Output Speed");
		subs.add(outputSpeed);
		Substrate outputJump = new Substrate(new Pair<Integer, Integer>(1, 1), //1 by 1 button for speed
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Output Jump");
		subs.add(outputJump);
		return subs;
	}

	/**
	 * Each Substrate has a unique String name, and this method returns a list
	 * of String pairs indicating which Substrates are connected: The Substrate
	 * from the first in the pair has links leading into the neurons in the
	 * Substrate second in the pair.
	 *
	 * @return Last of String pairs where all Strings are names of Substrates
	 *         for the domain.
	 */
	@Override
	public List<Pair<String, String>> getSubstrateConnectivity(){
		ArrayList<Pair<String, String>> conn = new ArrayList<Pair<String, String>>();
		conn.add(new Pair<String, String>("Inputs World", "Processing"));
		conn.add(new Pair<String, String>("Inputs Enemies", "Processing"));
		conn.add(new Pair<String, String>("Processing", "Outputs D-Pad"));	
		conn.add(new Pair<String, String>("Processing", "Output Speed"));	
		conn.add(new Pair<String, String>("Processing", "Output Jump"));	
		return conn;
	}
}
