package edu.utexas.cs.nn.tasks.gridTorus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.NetworkGenotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorldExec;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.GridTorusObjective;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import edu.utexas.cs.nn.util.datastructures.*;

/**
 *
 * @author Alex Rollins, Jacob Schrum, Lauren Gillespie
 * A parent class which defines the Predator Prey task which evolves either the predator or the prey
 * (specified by the user which to evolve) while the other is kept static. The user also specifies the number
 * of preys and predators to be included, as well as their available actions. Runs the game so that predators attempt to 
 * eat (get to the same location) the prey as soon as possible while prey attempt to survive as long as possible
 */
public abstract class TorusPredPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask, HyperNEATTask {

	/**
	 * The getter method that returns the list of controllers for the predators
	 * @param individual the genotype that will be given to all predator agents (homogeneous team)
	 * @return list of controllers for predators
	 */
	public abstract TorusPredPreyController[] getPredAgents(Genotype<T> individual);

	/**
	 * The getter method that returns the list of controllers for the preys
	 * @param individual the genotype that will be given to all prey agents (homogeneous team)
	 * @return list of controllers for prey
	 */
	public abstract TorusPredPreyController[] getPreyAgents(Genotype<T> individual);

	//boolean to indicate which agent is to be evolved
	private boolean preyEvolve;

	//list of fitness scores
	protected ArrayList<GridTorusObjective<T>> objectives = new ArrayList<GridTorusObjective<T>>();

	private TorusWorldExec exec;

	/**
	 * constructor for a PredPrey Task where either the predators are evolved while prey are kept
	 * static or prey are evolved while predators are kept static 
	 * @param preyEvolve if true prey are being evolved; if false predators are
	 * being evolved
	 */
	public TorusPredPreyTask(boolean preyEvolve) {
		super();
		this.preyEvolve = preyEvolve;
		if (CommonConstants.monitorInputs && TWEANN.inputPanel != null) {
			TWEANN.inputPanel.dispose();
		}
	}



	/**
	 * for adding fitness scores (turned on by command line parameters)
	 * @param o objective/fitness score
	 * @param list of fitness scores
	 * @param affectsSelection//???
	 */
	public final void addObjective(GridTorusObjective<T> o, ArrayList<GridTorusObjective<T>> list) {
		list.add(o);
		MMNEAT.registerFitnessFunction(o.getClass().getSimpleName()); 
	}

	@Override
	/**
	 * A method that evaluates a single genotype
	 * Provides fitness for that genotype based on the game time as well as other scores 
	 * @param individual genotype being evaluated
	 * @param num number of current evaluation
	 * @return A Pair of double arrays containing the fitness and other scores 
	 */
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		TorusPredPreyController[] predAgents = getPredAgents(individual);
		TorusPredPreyController[] preyAgents = getPreyAgents(individual);
		exec = new TorusWorldExec();
		TorusPredPreyGame game;
		if (CommonConstants.watch) {
			game = exec.runGameTimed(predAgents, preyAgents, true);
		} else {
			game = exec.runExperiment(predAgents, preyAgents);
		}
		double[] fitnesses = new double[objectives.size()];

		//---------Need to save module usage because it will be lost---------
		//store the list of the agents being evolved
		TorusPredPreyController[] evolvedAgents = preyEvolve ? preyAgents : predAgents;

		//dispose of all panels inside of agents/controllers
		if (CommonConstants.monitorInputs) {
			// Dispose of existing panels
			for (int i = 0; i < evolvedAgents.length; i++) {
				((NNTorusPredPreyController) (evolvedAgents)[i]).networkInputs.dispose();
			}
		}

		//gets the controller of the evolved agent(s), gets its network, and stores the number of modules for that network
		int numModes = ((NNTorusPredPreyController) evolvedAgents[0]).nn.numModules();
		//this will store the number of times each module is used by each agent 
		int[] overallAgentModeUsage = new int[numModes];  
		for(TorusPredPreyController agent : evolvedAgents){
			//get the list of all modules used by this agent and store how many times that module is used in that spot in the array
			int[] thisAgentModeUsage = ((NNTorusPredPreyController) agent).nn.getModuleUsage();
			//combine this agent's module usage with the module usage of all agents
			overallAgentModeUsage = ArrayUtil.zipAdd(overallAgentModeUsage, thisAgentModeUsage);
		}

		//Fitness function requires an organism, so make this genotype into an organism
		//this erases information stored about module usage, so was saved in order to be reset after the creation of this organism
		Organism<T> organism = new NNTorusPredPreyAgent<T>(individual, !preyEvolve);
		for (int j = 0; j < objectives.size(); j++) {
			fitnesses[j] = objectives.get(j).score(game, organism);
		}

		//The above code erased module usage, so this sets the module usage back to what it was 
		((NetworkGenotype<T>) individual).setModuleUsage(overallAgentModeUsage);

		double[] otherStats = new double[0];
		return new Pair<double[], double[]>(fitnesses, otherStats);
	}
	/**
	 * @return the number of minimum scores for this genotype of this task
	 */
	public int numObjectives() {
		return objectives.size();
	}
	/**
	 * @return the starting goals of this genotype in an array
	 */
	public double[] startingGoals() {
		return minScores();
	}

	@Override
	/**
	 * @return the minimum possible scores (worst scores) for this genotype
	 * if it is a prey then the min score is 0 and if it's a predator min score is the total time limit
	 */
	public double[] minScores() {
		double[] result = new double[numObjectives()];
		for (int i = 0; i < result.length; i++) {
			result[i] = objectives.get(i).minScore();
		}
		return result;
	}

	/**
	 * For agent evolving
	 * @return agent's sensory labels in a string array 
	 */
	public String[] sensorLabels() {
		String[] sensors = new String[2 * (Parameters.parameters.integerParameter("torusPreys") + Parameters.parameters.integerParameter("torusPredators"))];
		String[] predSensors = NNTorusPredPreyController.sensorLabels(Parameters.parameters.integerParameter("torusPredators"), "Pred");
		String[] preySensors = NNTorusPredPreyController.sensorLabels(Parameters.parameters.integerParameter("torusPreys"), "Prey");
		//if it is the predator who will evolve, get its sensor labels
		if (!preyEvolve) {
			//if the ability to sense teammates has been turned on, include sensors to the agents of this agent's
			//own type in addition to sensors to the enemies
			if(Parameters.parameters.booleanParameter("torusSenseTeammates")){
				//put the prey sensors into the sensors array followed by the predator sensors
				System.arraycopy(preySensors, 0, sensors, 0, preySensors.length);
				System.arraycopy(predSensors, 0, sensors, preySensors.length, predSensors.length);
				return sensors;
			}else{
				return preySensors;
			}
		}//if it is the prey who is evolving, get its sensor labels
		else {
			//if the ability to sense teammates has been turned on, include sensors to the agents of this agent's
			//own type in addition to sensors to the enemies
			if(Parameters.parameters.booleanParameter("torusSenseTeammates")){
				//put the predator sensors into the sensors array followed by the prey sensors
				System.arraycopy(predSensors, 0, sensors, 0, predSensors.length);
				System.arraycopy(preySensors, 0, sensors, predSensors.length, preySensors.length);
				return sensors;
			}else{
				return predSensors;
			}
		}
	}

	/**
	 * For evolving agent
	 * Defines the genotype's possible actions (whether it can do nothing or not) based on what the
	 * user indicated in a command line parameter (the default does not include the do nothing action)
	 * @return agent's output labels in a string array 
	 */
	public String[] outputLabels() {
		//if it is the predator evolving
		if (!preyEvolve) {
			return Parameters.parameters.booleanParameter("allowDoNothingActionForPredators")
					? new String[]{"UP", "RIGHT", "DOWN", "LEFT", "NOTHING"}
			: new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
		}
		//the prey is evolving
		return Parameters.parameters.booleanParameter("allowDoNothingActionForPreys")
				? new String[]{"UP", "RIGHT", "DOWN", "LEFT", "NOTHING"}
		: new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
	}

	/**
	 * Accesses the time stamps for the current game being executed, use for
	 * evaluation purposes.
	 */
	public double getTimeStamp() {
		return exec.game.getTime();
	}

	/**
	 * if run with hyperNEAT, gets substrate information for cppn to process
	 * @return List<Substrate> list of all substrates in domain
	 */
	@Override
	public List<Substrate> getSubstrateInformation() {

		//these parameters are called repeatedly, therefore created local variables to improve efficiency
		Integer torusWidth = Parameters.parameters.integerParameter("torusXDimensions");
		Integer torusHeight = Parameters.parameters.integerParameter("torusYDimensions");
		boolean senseTeammates = Parameters.parameters.booleanParameter("torusSenseTeammates");

		//used for locating substrate in vector space
		int substrateXCoord = 4;
		int substrateYCoord = 4;
		int outputSize = 3;

		List<Substrate> subs = new LinkedList<Substrate>();
		if(preyEvolve) {//order of pred/prey substrate important, helps in sorting later on in get substrate inputs method
			Substrate predator = new Substrate(new Pair<Integer, Integer>(torusWidth, torusHeight), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 0,0), "input_predator");
			subs.add(predator);
			if(senseTeammates) {
				Substrate prey = new Substrate(new Pair<Integer, Integer>(torusWidth, torusHeight), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(substrateXCoord,0,0), "input_prey");
				subs.add(prey);
			}	
		} else {
			Substrate prey = new Substrate(new Pair<Integer, Integer>(torusWidth, torusHeight), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(substrateXCoord, 0,0), "input_prey");
			subs.add(prey);
			if(senseTeammates) {
				Substrate predator = new Substrate(new Pair<Integer, Integer>(torusWidth, torusHeight), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0,0,0), "input_predator");
				subs.add(predator);
			}
		}
		substrateXCoord = substrateXCoord/2;//arranges substrate location so process and output are centered if two input substrates present
		if(!senseTeammates){//else no need to recenter if only one input substrate
			substrateXCoord = 0;
		} 
		Substrate processing = new Substrate(new Pair<Integer, Integer>(torusWidth, torusHeight), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(substrateXCoord, substrateYCoord, 0), "process_0");
		subs.add(processing);
		Substrate output = new Substrate(new Pair<Integer, Integer>(outputSize, outputSize), Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(substrateXCoord, substrateYCoord*2, 0), "output_0");
		subs.add(output);
		return subs;
	}

	/**
	 * Returns a list of connections between substrates
	 * @return List<Pair<String, String>> list of connections between substrates
	 */
	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		List<Pair<String, String>> connectivity = new LinkedList<Pair<String, String>>();
		if(preyEvolve) {
			if(Parameters.parameters.booleanParameter("torusSenseTeammates")) {
				connectivity.add(new Pair<String, String>("input_prey", "process_0"));
			}
			connectivity.add(new Pair<String, String>("input_predator", "process_0"));
		} else {
			if(Parameters.parameters.booleanParameter("torusSenseTeammates")) {
				connectivity.add(new Pair<String, String>("input_predator", "process_0"));
			}
			connectivity.add(new Pair<String, String>("input_prey", "process_0"));
		}
		connectivity.add(new Pair<String, String>("process_0", "output_0"));
		return connectivity;
	}

	/**
	 * gets the inputs for the cppn. 1.0 corresponds to an agent at that location, 0.0 corresponds to no agent 
	 * @return double[] double array containing all inputs to cppn from torus gridworld
	 */
	@Override
	public double[] getSubstrateInputs(List<Substrate> subs) {
		List<Substrate> inputSubs = getInputSubstrates(subs);
		int size = inputSize(inputSubs);
		double[] inputs = new double[size];

		TorusAgent[] preds = exec.game.getPredators();
		List<Tuple2D> predsCoord = getCoordinates(preds);
		Substrate sPreds = getSubstrate(inputSubs, "input_predator");
		List<Integer> predsIndices = getIndices(predsCoord, sPreds.size.t1);

		TorusAgent[] prey = exec.game.getPrey();
		List<Tuple2D> preyCoord = getCoordinates(prey);
		Substrate sPrey = getSubstrate(inputSubs, "input_prey");
		List<Integer> preyIndices = getIndices(preyCoord, sPrey.size.t1);


		for(int i = 0; i < inputs.length; i++) {
			if(preyEvolve) {
				preyIndices = this.scaleIndices(preyIndices, sPreds.size);
				if(predsIndices.contains(i) || preyIndices.contains(i)) inputs[i] = 1.0;
			} else {
				predsIndices = this.scaleIndices(predsIndices, sPrey.size);
				if(predsIndices.contains(i) || preyIndices.contains(i)) inputs[i] = 1.0;
			}
		}

		return inputs;
	}

	/**
	 * scales indices in order to match them up for input array
	 * @param indices list of indices to be scaled
	 * @param coords size of substrate preceding indices
	 * @return indices scaled to include substrate coords
	 */
	private List<Integer> scaleIndices(List<Integer> indices, Pair<Integer, Integer> coords) {
		List<Integer> scaledIndices = new LinkedList<Integer>();
		int size = coords.t1*coords.t2;
		for(int i = 0; i < indices.size(); i++) {
			scaledIndices.add(indices.get(i) + size);
		}
		return scaledIndices;
	}

	/**
	 * gets the substrate of given name from a list of substrate, returns null if none exists
	 * @param subs list of substrates
	 * @param name name of substrate to look for
	 * @return given substrate
	 */
	private Substrate getSubstrate(List<Substrate> subs, String name) {
		for(int i = 0; i < subs.size(); i++) {
			if(subs.get(i).getName().equals(name)) return subs.get(i);
		}
		return null;
	}

	/**
	 * gets the indices of agents in a torusWorld from the coordinates of each agent
	 * @param coords list containing coordinates of each agent
	 * @param substrateWidth width of substrate agents are located in (for calculating the actual index)
	 * @return list of indices
	 */
	private List<Integer> getIndices(List<Tuple2D> coords, int substrateWidth) {
		List<Integer> indices = new LinkedList<Integer>();
		for(int i = 0; i < coords.size(); i++) {
			indices.add(indexFromCoordinates(coords.get(i).x, coords.get(i).y, substrateWidth));
		}
		return indices;
	}

	/**
	 * gets coordinates of each agent from an array of agents
	 * @param agents array of agents
	 * @return coordinates of agents
	 */
	private List<Tuple2D> getCoordinates(TorusAgent[] agents) {
		List<Tuple2D> coords = new LinkedList<Tuple2D>(); 
		for(int i = 0; i < agents.length; i++) {
			coords.add(agents[i].getPosition());
		}
		return coords;
	}
	/**
	 * returns size of proper array.
	 * guaranteed every substrate in list should be in input array
	 * @param inputSubs list of input substrates
	 * @return size of corresponding int input aray
	 */
	private int inputSize(List<Substrate> inputSubs) {
		int size = 0;
		for(int i = 0; i < inputSubs.size(); i++) {
			size += inputSubs.get(i).size.t1* inputSubs.get(i).size.t2;
		}
		return size;
	}

	/**
	 * gets the index of an agent from its coordinates
	 * @param x x-coordinate of agent
	 * @param y y-coordinate of agent
	 * @param substrateWidth width of substrate agent is located in
	 * @return index of substrate
	 */
	private Integer indexFromCoordinates(double x, double y, int substrateWidth) {
		return (int) (substrateWidth*(y-1) + x);
	}

	/**
	 * returns a list of ONLY input substrates
	 * @param subs list of all substrates in domain
	 * @return list of input substrates
	 */
	private List<Substrate> getInputSubstrates(List<Substrate> subs) {
		List<Substrate> inputSubs = new LinkedList<Substrate>();
		for(int i = 0; i < subs.size(); i++) {
			if(subs.get(i).stype == 0) inputSubs.add(subs.get(i));
		}
		return inputSubs;
	}
}
