package edu.utexas.cs.nn.tasks.gridTorus;

import java.util.ArrayList;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.NetworkGenotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorldExec;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.GridTorusObjective;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Alex Rollins, Jacob Schrum
 * A parent class which defines the Predator Prey task which evolves either the predator or the prey
 * (specified by the user which to evolve) while the other is kept static. The user also specifies the number
 * of preys and predators to be included, as well as their available actions. Runs the game so that predators attempt to 
 * eat (get to the same location) the prey as soon as possible while prey attempt to survive as long as possible
 */
public abstract class TorusPredPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask {

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
	 * @param affectsSelection
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
}
