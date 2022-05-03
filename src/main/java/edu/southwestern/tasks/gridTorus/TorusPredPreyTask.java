package edu.southwestern.tasks.gridTorus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.NetworkGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.evolution.nsga2.tug.TUGTask;
import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusPredPreyGame;
import edu.southwestern.gridTorus.TorusWorldExec;
import edu.southwestern.gridTorus.controllers.TorusPredPreyController;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gridTorus.cooperative.CooperativePredatorsVsStaticPreyTask;
import edu.southwestern.tasks.gridTorus.objectives.GridTorusObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorCatchCloseObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorCatchCloseQuickObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorCatchObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorEatEachPreyQuicklyObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorMinimizeDistanceFromIndividualPreyObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorMinimizeDistanceFromPreyObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorMinimizeGameTimeObjective;
import edu.southwestern.tasks.gridTorus.objectives.PredatorRawalRajagopalanMiikkulainenObjective;
import edu.southwestern.tasks.gridTorus.objectives.PreyLongSurvivalTimeObjective;
import edu.southwestern.tasks.gridTorus.objectives.PreyMaximizeDistanceFromPredatorsObjective;
import edu.southwestern.tasks.gridTorus.objectives.PreyMaximizeGameTimeObjective;
import edu.southwestern.tasks.gridTorus.objectives.PreyMinimizeCaughtObjective;
import edu.southwestern.tasks.gridTorus.objectives.PreyRawalRajagopalanMiikkulainenObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.IndividualPredatorCatchObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.IndividualPredatorMinimizeDistanceFromIndividualPreyObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.IndividualPredatorMinimizeDistanceFromPreyObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.IndividualPreyMaximizeDistanceFromClosestPredatorObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.IndividualPreyMaximizeDistanceFromIndividualPredatorObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.IndividualPreyMaximizeDistanceFromPredatorsObjective;
import edu.southwestern.tasks.gridTorus.objectives.cooperative.PredatorHerdPreyObjective;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.Plot;
import edu.southwestern.util.util2D.Tuple2D;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.*;

/**
 *
 * @author Alex Rollins, Jacob Schrum, Lauren Gillespie A parent class which
 *         defines the Predator Prey task which evolves either the predator or
 *         the prey (specified by the user which to evolve) while the other is
 *         kept static. The user also specifies the number of preys and
 *         predators to be included, as well as their available actions. Runs
 *         the game so that predators attempt to eat (get to the same location)
 *         the prey as soon as possible while prey attempt to survive as long as
 *         possible
 * @param <T>
 *            Network phenotype being evolved
 */
public abstract class TorusPredPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask, HyperNEATTask {

	public static final String[] ALL_ACTIONS = new String[] { "UP", "RIGHT", "DOWN", "LEFT", "NOTHING" };
	public static final String[] MOVEMENT_ACTIONS = new String[] { "UP", "RIGHT", "DOWN", "LEFT" };

	public static final int HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION = 3;

	/**
	 * The getter method that returns the list of controllers for the predators
	 *
	 * @param individual
	 *            the genotype that will be given to all predator agents
	 *            (homogeneous team)
	 * @return list of controllers for predators
	 */
	public abstract TorusPredPreyController[] getPredAgents(Genotype<T> individual);

	// Remember which agents are evolved. Can be cast to
	// NNTorusPreyPreyController later
	public TorusPredPreyController[] evolved = null;

	/**
	 * The getter method that returns the list of controllers for the preys
	 *
	 * @param individual
	 *            the genotype that will be given to all prey agents
	 *            (homogeneous team)
	 * @return list of controllers for prey
	 */
	public abstract TorusPredPreyController[] getPreyAgents(Genotype<T> individual);

	// boolean to indicate which agent is to be evolved
	public static boolean preyEvolve;
	// boolean to indicate if competitive coevolution is happening
	public final boolean competitive;

	// list of fitness scores
	public ArrayList<ArrayList<GridTorusObjective<T>>> objectives = new ArrayList<ArrayList<GridTorusObjective<T>>>();
	// list of other scores, which don't effect evolution
	public ArrayList<ArrayList<GridTorusObjective<T>>> otherScores = new ArrayList<ArrayList<GridTorusObjective<T>>>();

	private TorusWorldExec exec;

	/**
	 * ONLY FOR COMPETITIVE COEVOLUTION
	 * This creates a torusPredPreyTask for competitive coevolution
	 */
	public TorusPredPreyTask(){
		this(true, true);
	}

	/**
	 * constructor for a PredPrey Task where either the predators are evolved
	 * while prey are kept static or prey are evolved while predators are kept
	 * static
	 *
	 * @param preyEvolve, true if prey are being evolved, false if predators are being evolved
	 */
	public TorusPredPreyTask(boolean preyEvolve){
		this(preyEvolve, false);
	}

	/**
	 * constructor for PredPrey task with either predators or prey being evolved
	 * or both (if competitive coevolution) 
	 * 
	 * @param preyEvolve, true if prey are being evolved, false if predators evolved
	 * @param competitive, true if competitive coevolution
	 */
	public TorusPredPreyTask(boolean preyEvolve, boolean competitive) {
		super();
		TorusPredPreyTask.preyEvolve = preyEvolve;
		this.competitive = competitive;
		if (CommonConstants.monitorInputs && TWEANN.inputPanel != null) {
			TWEANN.inputPanel.dispose();
		}
	}

	public final void addObjective(GridTorusObjective<T> o, ArrayList<ArrayList<GridTorusObjective<T>>> list, int pop) {
		addObjective(o,list,true, pop);
	}

	/**
	 * for adding fitness scores (turned on by command line parameters)
	 *
	 * @param o
	 *            objective/fitness score
	 * @param list
	 *            of fitness scores
	 * @param affectsSelection  
	 *            true if objective score
	 *            false if other score
	 * @param pop Index of population
	 */
	public final void addObjective(GridTorusObjective<T> o, ArrayList<ArrayList<GridTorusObjective<T>>> list, boolean affectsSelection, int pop) {
		list.get(pop).add(o);
		MMNEAT.registerFitnessFunction(o.getClass().getSimpleName(),affectsSelection,pop);
	}

	@Override
	/**
	 * A method that evaluates a single genotype Provides fitness for that
	 * genotype based on the game time as well as other scores
	 *
	 * @param individual
	 *            genotype being evaluated
	 * @param num
	 *            number of current evaluation
	 * @return A Pair of double arrays containing the fitness and other scores
	 */
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		//long time = System.currentTimeMillis(); // For timing
		TorusPredPreyController[] predAgents = getPredAgents(individual);
		TorusPredPreyController[] preyAgents = getPreyAgents(individual);

		TorusPredPreyGame game = runEval(predAgents, preyAgents);

		// gets the controller of the evolved agent(s), gets its network, and
		// stores the number of modules for that network
		int numModes = ((NNTorusPredPreyController) evolved[0]).nn.numModules();
		// this will store the number of times each module is used by each agent
		int[] overallAgentModeUsage = new int[numModes];
		for (TorusPredPreyController agent : evolved) {
			// get the list of all modules used by this agent and store how many
			// times that module is used in that spot in the array
			int[] thisAgentModeUsage = ((NNTorusPredPreyController) agent).nn.getModuleUsage();
			// combine this agent's module usage with the module usage of all agents
			overallAgentModeUsage = ArrayUtil.zipAdd(overallAgentModeUsage, thisAgentModeUsage);
		}

		double[] fitnesses = new double[objectives.get(0).size()];
		double[] otherStats = new double[otherScores.get(0).size()];

		// Fitness function requires an organism, so make this genotype into an organism
		// this erases information stored about module usage, so was saved in
		// order to be reset after the creation of this organism
		Organism<T> organism = new NNTorusPredPreyAgent<T>(individual, !preyEvolve);
		for (int i = 0; i < objectives.get(0).size(); i++) {
			fitnesses[i] = objectives.get(0).get(i).score(game, organism);
		}
		for (int i = 0; i < otherScores.get(0).size(); i++) {
			otherStats[i] = otherScores.get(0).get(i).score(game,organism);		
		}

		// The above code erased module usage, so this sets the module usage
		// back to what it was
		((NetworkGenotype<T>) individual).setModuleUsage(overallAgentModeUsage);

		//System.out.println("oneEval: " + (System.currentTimeMillis() - time));
		return new Pair<double[], double[]>(fitnesses, otherStats);
	}

	public TorusPredPreyGame runEval(TorusPredPreyController[] predAgents, TorusPredPreyController[] preyAgents) {
		exec = new TorusWorldExec();
		TorusPredPreyGame game;
		if (CommonConstants.watch) {
			game = exec.runGameTimed(predAgents, preyAgents, true);
		} else {
			game = exec.runExperiment(predAgents, preyAgents);
		}

		// dispose of all panels inside of agents/controllers
		if (CommonConstants.monitorInputs) {
			// Dispose of existing panels
			for (int i = 0; i < evolved.length; i++) {
				((NNTorusPredPreyController) (evolved)[i]).networkInputs.dispose();
			}
		}

		return game;
	}

	/**
	 * @return the number of fitness scores for this genotype
	 */
	@Override
	public int numObjectives() {
		return objectives.get(0).size();
	}

	/**
	 * @return the number of other scores for this genotype
	 */
	@Override
	public int numOtherScores() {
		return otherScores.get(0).size();
	}

	/**
	 * @return the starting goals of this genotype in an array
	 */
	@Override
	public double[] startingGoals() {
		return minScores();
	}

	/**
	 * @return the minimum possible scores (worst scores) for this genotype
	 */
	@Override
	public double[] minScores() {
		double[] result = new double[numObjectives()];
		for (int i = 0; i < result.length; i++) {
			result[i] = objectives.get(0).get(i).minScore();
		}
		return result;
	}

	/**
	 * For agent evolving
	 *
	 * @return agent's sensory labels in a string array
	 */
	@Override
	public String[] sensorLabels() {
		return TorusPredPreyTask.preyEvolve 
				? (new NNTorusPredPreyController(null,false)).sensorLabels()
						: (new NNTorusPredPreyController(null,true)).sensorLabels();
	}

	/**
	 * For evolving agent Defines the genotype's possible actions (whether it
	 * can do nothing or not) based on what the user indicated in a command line
	 * parameter (the default does not include the do nothing action)
	 *
	 * @return agent's output labels in a string array
	 */
	@Override
	public String[] outputLabels() {
		return outputLabels(!preyEvolve);
	}

	public static String[] outputLabels(boolean forPredators) {
		// if it is the predator evolving
		if (forPredators) {
			return Parameters.parameters.booleanParameter("allowDoNothingActionForPredators") ? ALL_ACTIONS : MOVEMENT_ACTIONS;
		} else {// the prey is evolving
			return Parameters.parameters.booleanParameter("allowDoNothingActionForPreys") ? ALL_ACTIONS : MOVEMENT_ACTIONS;
		}
	}

	/**
	 * Accesses the time stamps for the current game being executed, use for
	 * evaluation purposes.
	 * 
	 * @return Number of elapsed steps in simulation
	 */
	@Override
	public double getTimeStamp() {
		return exec.game.getTime();
	}

	/**
	 * make n copies of the designated static controller
	 * @param <T> Some kind of TorusPredPreyController
	 * @param isPred, true if predator, false if prey
	 * @param num, number of controllers
	 * @return Array of TorusPredPreyControllers
	 */
	public static <T extends TorusPredPreyController> TorusPredPreyController[] getStaticControllers(boolean isPred, int num) {
		TorusPredPreyController[] staticAgents = new TorusPredPreyController[num];
		try {
			for (int i = 0; i < num; i++) {
				staticAgents[i] = (TorusPredPreyController) ClassCreation.createObject(isPred ? "staticPredatorController" : "staticPreyController");
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("Could not load static agents");
			System.exit(1);
		}
		return staticAgents;		
	}

	/**
	 * retrieve the evolved controllers for the evolved agent's genotype
	 * 
	 * @param <T>
	 * @param container An array that will be filled with the newly created controllers
	 * @param g, the genotype
	 * @param isPred, true if predator, false if prey
	 */
	public static <T extends Network> void getEvolvedControllers(TorusPredPreyController[] container, Genotype<T> g, boolean isPred){
		getEvolvedControllers(container, g, isPred, 0, container.length);
	}	

	/**
	 * retrieve the evolved controllers for the evolved agent's genotype
	 * 
	 * @param <T>
	 * @param container An array that will be filled with the newly created controllers
	 * @param g, the genotype
	 * @param isPred, true if predator, false if prey
	 * @param startIndex, the starting index to fill the evolved controller from
	 * @param numCopies, the ending index for this portion of the evolved controller
	 */
	public static <T extends Network> void getEvolvedControllers(TorusPredPreyController[] container, Genotype<T> g, boolean isPred, int startIndex, int numCopies){
		//copy g into an array
		@SuppressWarnings("unchecked")
		Genotype<T>[] agents = new Genotype[numCopies];
		for(int i = 0; i < agents.length; i++) {
			agents[i] = g.copy();
		}
		getEvolvedControllers(container, agents, isPred, startIndex);
	}

	/**
	 * retrieve the evolved controllers for each of the evolved agents
	 * 
	 * @param <T>
	 * @param container An array that will be filled with the newly created controllers
	 * @param genotypes genotypes for whole team
	 * @param isPred, true if predator, false if prey
	 */
	public static <T extends Network> void getEvolvedControllers(TorusPredPreyController[] container, Genotype<T>[] genotypes, boolean isPred){
		getEvolvedControllers(container, genotypes, isPred, 0);
	}	

	/**
	 * retrieve the evolved controllers for each of the evolved agents
	 * 
	 * @param <T>
	 * @param container An array that will be filled with the newly created controllers
	 * @param genotypes genotypes for each agent
	 * @param isPred, true if predator, false if prey
	 * @param startIndex, the starting index to fill the evolved controller from
	 */
	public static <T extends Network> void getEvolvedControllers(TorusPredPreyController[] container, Genotype<T>[] genotypes, boolean isPred, int startIndex){
		int typeIndex = 0;
		for (int i = startIndex; i < startIndex + genotypes.length; i++) {
			// true to indicate that this is a predator
			container[i] = new NNTorusPredPreyAgent<T>(genotypes[typeIndex], isPred).getController();
			// if requested, adds visual panels for each of the evolved agents showing its inputs
			// (offsets to other agents), outputs (possible directional movements), and game time
			if (CommonConstants.monitorInputs) {
				preyEvolve = !isPred;
				DrawingPanel panel = new DrawingPanel(Plot.BROWSE_DIM, (int) (Plot.BROWSE_DIM * 3.5), (isPred ? "Predator " + typeIndex : "Prey " + typeIndex));
				((NNTorusPredPreyController) container[i]).networkInputs = panel;
				panel.setLocation(i * (Plot.BROWSE_DIM + 10), 0);
				Offspring.fillInputs(panel, genotypes[typeIndex]);
			}
			typeIndex++;
		}
	}

	/**
	 * Converts the addAllObjectives method to be used for non-coevolution, just giving 
	 * the a single list/population which is then copied into every member of the team
	 */
	public void addAllObjectives(){
		//pop and isPrey value do not matter for non-competitive non-coevolution
		addAllObjectives(0, false);
	}	

	/**
	 * Converts addAllObjectives to be used for non-competitive-coevolution, just giving the 
	 * extraneous true value to isPrey because it does not matter for non-competitive-coevolution
	 * @param pop, current population
	 */
	public void addAllObjectives(int pop){
		//isPreyPop value does not matter for non-competitive coevolution
		addAllObjectives(pop, false);
	}

	/**
	 * A method which adds all of the fitness objectives and other scores for 
	 * the evolving agent to the lists of objectives and other scores
	 * so that they can be included in the evolution
	 * 
	 * @param pop, current population
	 * @param isPreyPop, if this is a prey population (only for competitive coevolution)
	 */
	public void addAllObjectives(int pop, boolean isPreyPop){
		System.out.println("Adding objectives for population: " + pop);

		objectives.add(new ArrayList<GridTorusObjective<T>>());
		otherScores.add(new ArrayList<GridTorusObjective<T>>());

		if(competitive){
			if(isPreyPop){
				addPreyObjectives(pop);
			} else{
				addPredatorObjectives(pop);
			}
			//only add other scores to the first population, because the other scores
			//will have all possible objectives for both predators and prey
			if(pop == 0){
				addPredatorOther(pop);
				addPreyOther(pop);
			}
		} else{
			if(preyEvolve){ 
				addPreyObjectives(pop);
				//only add other scores to the first population, because the other scores
				//will have all possible prey objectives
				if(pop == 0){
					addPreyOther(pop);
				}
			} else{
				addPredatorObjectives(pop);
				//only add other scores to the first population, because the other scores
				//will have all possible predator objectives
				if(pop == 0){
					addPredatorOther(pop);
				}
			}
		}
	}

	/**
	 * Adds every possible predator objective as an other score
	 * 
	 * @param pop, current population
	 */
	private void addPredatorOther(int pop){
		//add other scores to be able to show each fitness score even if it's not effecting evolution
		//Predator other scores
		addObjective(new PredatorCatchObjective<T>(), otherScores, false, pop);
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
			addObjective(new IndividualPredatorCatchObjective<T>(i), otherScores, false, pop);
		}
		addObjective(new PredatorCatchCloseObjective<T>(), otherScores, false, pop);
		addObjective(new PredatorMinimizeGameTimeObjective<T>(), otherScores, false, pop);
		addObjective(new PredatorHerdPreyObjective<T>(), otherScores, false, pop);
		addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), otherScores, false, pop);
		addObjective(new PredatorMinimizeDistanceFromPreyObjective<T>(), otherScores, false, pop);
		if(Parameters.parameters.integerParameter("torusPreys") == 2)
			addObjective(new PredatorRawalRajagopalanMiikkulainenObjective<T>(), otherScores, false, pop);
		addObjective(new PredatorCatchCloseQuickObjective<T>(), otherScores, false, pop);
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
			addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), otherScores, false, pop);
		}
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
			addObjective(new IndividualPredatorMinimizeDistanceFromPreyObjective<T>(i), otherScores, false, pop);
		}
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
			for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), otherScores, false, pop);
			}
		}
	}

	/**
	 * Add all the corresponding fitness objectives to the list of objectives at the current population.
	 * Adds every possible objective as an other score if it is the first population, and no other 
	 * scores at all if it is not the first population.
	 * 
	 * @param pop, current population
	 */
	private void addPredatorObjectives(int pop){
		//Predator fitness scores
		if (Parameters.parameters.booleanParameter("predatorCatch"))
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorCatchClose"))
			addObjective(new PredatorCatchCloseObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorMinimizeTotalTime"))
			addObjective(new PredatorMinimizeGameTimeObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorsEatQuick"))
			addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorMinimizeDistance"))
			addObjective(new PredatorMinimizeDistanceFromPreyObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorRRM"))
			addObjective(new PredatorRawalRajagopalanMiikkulainenObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorCatchCloseQuick"))
			addObjective(new PredatorCatchCloseQuickObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("predatorMinimizeIndividualDistance")){
			//get separate distance fitness functions for each prey and add them as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives, pop);
			}
		}
		if (Parameters.parameters.booleanParameter("indivPredMinDist")){
			//get separate distance fitness functions for each predator and add them as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromPreyObjective<T>(i), objectives, pop);
			}
		}
		if (Parameters.parameters.booleanParameter("indivPredMinDistIndivPrey")){
			//get separate distance fitness functions for each predator to each prey individually and add them all as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives, pop);
				}
			}
		}

		
		//The following ten setups test individual versus team selection
		//homogeneous setups
		if(Parameters.parameters.booleanParameter("homogeneousTeamSelection")){
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorCatchObjective<T>(i), objectives, pop);
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives, pop);
				}
			}
		}
		if(Parameters.parameters.booleanParameter("homogeneousAggregateTeamSelection")){
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives, pop);
			}
		}
		if(Parameters.parameters.booleanParameter("homogeneousTeamAndAggregateTeamSelection")){
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives, pop);
			}
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorCatchObjective<T>(i), objectives, pop);
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives, pop);
				}
			}
		}
		//cooperative setups
		if(Parameters.parameters.booleanParameter("cooperativeIndividualSelection")){
			addObjective(new IndividualPredatorCatchObjective<T>(pop), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(pop,i), objectives, pop);
			}
		}
		if(Parameters.parameters.booleanParameter("cooperativeAggregateTeamSelection")){
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives, pop);
			}
		}
		if(Parameters.parameters.booleanParameter("cooperativeIndividualAndAggregateTeamSelection")){
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			addObjective(new IndividualPredatorCatchObjective<T>(pop), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(pop,i), objectives, pop);
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives, pop);
			}
		}
		if(Parameters.parameters.booleanParameter("cooperativeBalancedIndividualAndTeamSelection")){
			//all populations are given MultiIndivCC fitnesses with just the individual distance
			//function corresponding to that agent of this population to each prey
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(pop,i), objectives, pop);
			}
		}
		if(Parameters.parameters.booleanParameter("cooperativeTeamSelection")){
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorCatchObjective<T>(i), objectives, pop);
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives, pop);
				}
			}
		}
		//cooperativeIndividualAndTeamSelection is irrelevant because cooperative team selection already includes the cooperativeIndividualSelection functions
		//So it will just have two copies of the same fitnesses 
		if(Parameters.parameters.booleanParameter("cooperativeIndividualAndTeamSelection")){
			addObjective(new IndividualPredatorCatchObjective<T>(pop), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(pop,i), objectives, pop);
			}
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorCatchObjective<T>(i), objectives, pop);
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives, pop);
				}
			}
		}
		if(Parameters.parameters.booleanParameter("cooperativeTeamAndAggregateTeamSelection")){
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new PredatorMinimizeDistanceFromIndividualPreyObjective<T>(i), objectives, pop);
			}
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				addObjective(new IndividualPredatorCatchObjective<T>(i), objectives, pop);
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPreys"); j++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(i,j), objectives, pop);
				}
			}
		}

		
		//Cooperative predator fitness options
		if(Parameters.parameters.booleanParameter("predatorCoOpCCQ")){
			//all populations are given MultiIndivCCQ fitnesses with just the individual distance
			//function corresponding to that agent of this population to each prey
			addObjective(new PredatorCatchObjective<T>(), objectives, pop);
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(pop,i), objectives, pop);
			}
			addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), objectives, pop);
		}
		if(Parameters.parameters.booleanParameter("predatorOneHerdCoOpCCQ")){
			//give the first population just the herding objective
			if(pop == 0){
				addObjective(new PredatorHerdPreyObjective<T>(), objectives, pop);
			} else{
				//all remaining populations are given MultiIndivCCQ fitnesses with just the individual distance
				//function corresponding to that agent of this population to each prey
				addObjective(new PredatorCatchObjective<T>(), objectives, pop);
				for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
					addObjective(new IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T>(pop,i), objectives, pop);
				}
				addObjective(new PredatorEatEachPreyQuicklyObjective<T>(), objectives, pop);
			}
		}
	}

	/**
	 * Adds every possible prey objective as an other score 
	 * 
	 * @param pop, current population
	 */
	private void addPreyOther(int pop){
		//add other scores to be able to show each fitness score even if it's not effecting evolution
		//Prey other scores
		addObjective(new PreyMinimizeCaughtObjective<T>(), otherScores, false, pop);
		addObjective(new PreyRawalRajagopalanMiikkulainenObjective<T>(), otherScores, false, pop);
		addObjective(new PreyMaximizeGameTimeObjective<T>(), otherScores, false, pop);
		addObjective(new PreyLongSurvivalTimeObjective<T>(), otherScores, false, pop);
		addObjective(new PreyMaximizeDistanceFromPredatorsObjective<T>(), otherScores, false, pop);
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
			addObjective(new IndividualPreyMaximizeDistanceFromPredatorsObjective<T>(i), otherScores, false, pop);
		}
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
			addObjective(new IndividualPreyMaximizeDistanceFromClosestPredatorObjective<T>(i), otherScores, false, pop);
		}
		for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
			for(int j = 0; j < Parameters.parameters.integerParameter("torusPredators"); j++){
				addObjective(new IndividualPreyMaximizeDistanceFromIndividualPredatorObjective<T>(j,i), otherScores, false, pop);
			}
		}
	}

	/**
	 * Add all the corresponding fitness objectives to the list of objectives at the current population.
	 * Adds every possible objective as an other score if it is the first population, and no other 
	 * scores at all if it is not the first population.
	 * 
	 * @param pop, current population
	 */
	private void addPreyObjectives(int pop){
		//Include the offset from the objectives list for competitive coevolution if necessary
		int competitiveIndex = pop;
		if(competitive){
			competitiveIndex -= Parameters.parameters.integerParameter("torusPredators");
		}

		//Prey fitness scores
		if (Parameters.parameters.booleanParameter("preyMinimizeCaught"))
			addObjective(new PreyMinimizeCaughtObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("preyRRM"))
			addObjective(new PreyRawalRajagopalanMiikkulainenObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("preyMaximizeTotalTime"))
			addObjective(new PreyMaximizeGameTimeObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("preyLongSurvivalTime"))
			addObjective(new PreyLongSurvivalTimeObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("preyMaximizeDistance"))
			addObjective(new PreyMaximizeDistanceFromPredatorsObjective<T>(), objectives, pop);
		if (Parameters.parameters.booleanParameter("indivPreyMaxDist")){
			//get separate distance fitness functions for each prey and add them as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				addObjective(new IndividualPreyMaximizeDistanceFromPredatorsObjective<T>(i), objectives, pop);
			}
		}
		if (Parameters.parameters.booleanParameter("indivPreyMaxDistIndivPred")){
			//get separate distance fitness functions for each prey to each predator individually and add them all as objectives
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				for(int j = 0; j < Parameters.parameters.integerParameter("torusPredators"); j++){
					addObjective(new IndividualPreyMaximizeDistanceFromIndividualPredatorObjective<T>(j,i), objectives, pop);
				}
			}
		}

		//Cooperative prey fitness options
		if(Parameters.parameters.booleanParameter("preyCoOpCCQ")){
			//all populations are given MultiIndivCCQ fitnesses with just the individual distance
			//function corresponding to that agent of this population
			addObjective(new PreyMinimizeCaughtObjective<T>(), objectives, pop);
			addObjective(new IndividualPreyMaximizeDistanceFromClosestPredatorObjective<T>(competitiveIndex), objectives, pop);
			addObjective(new PreyLongSurvivalTimeObjective<T>(), objectives, pop);
		}
	}

	// These values will be defined before they are needed
	private static List<Substrate> substrateInformation = null;
	private static int numSubstrateInputs = -1;
	private static boolean substrateForPredators = false;
	private static boolean substrateForPrey = false;
	private static int secondSubstrateStartingIndex = -1;

	/**
	 * If run with hyperNEAT, gets substrate information for cppn to process.
	 * Save this information, because we only need to calculate it once.
	 *
	 * @return list of all substrates in domain
	 */
	@Override
	public List<Substrate> getSubstrateInformation() {
		if (substrateInformation == null) {
			// these parameters are called repeatedly, therefore created local
			// variables to improve efficiency
			int torusWidth = Parameters.parameters.integerParameter("torusXDimensions");
			int torusHeight = Parameters.parameters.integerParameter("torusYDimensions");
			boolean senseTeammates = Parameters.parameters.booleanParameter("torusSenseTeammates");

			// used for locating substrate in vector space: Spacing an placement
			// is somewhat arbitray ... for display purposes
			Triple<Integer, Integer, Integer> firstInputLocation = new Triple<Integer, Integer, Integer>(0, 0, 0);
			Triple<Integer, Integer, Integer> secondInputLocation = new Triple<Integer, Integer, Integer>(4, 0, 0);
			Triple<Integer, Integer, Integer> processingLocation = new Triple<Integer, Integer, Integer>(senseTeammates ? 2 : 0, 4, 0);
			Triple<Integer, Integer, Integer> outputLocation = new Triple<Integer, Integer, Integer>(senseTeammates ? 2 : 0, 8, 0);
			// Used for input and processing layers
			Pair<Integer, Integer> substrateDimension = new Pair<Integer, Integer>(torusWidth, torusHeight);
			Pair<Integer, Integer> outputSubstrateDimension = new Pair<Integer, Integer>(HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION, HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION);
			// Ordering of input substrate names

			Substrate predator = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, preyEvolve ? firstInputLocation : secondInputLocation, "input_predator");
			Substrate prey = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, preyEvolve ? secondInputLocation : firstInputLocation, "input_prey");

			substrateInformation = new LinkedList<Substrate>();
			// order of pred/prey substrate important, helps in sorting later on
			// in get substrate inputs method
			// Input layers
			numSubstrateInputs = 0;
			Substrate firstSubstrate = preyEvolve ? predator : prey;
			numSubstrateInputs += firstSubstrate.getSize().t1 * firstSubstrate.getSize().t2;
			secondSubstrateStartingIndex = numSubstrateInputs;
			substrateInformation.add(firstSubstrate);
			if (senseTeammates) {
				Substrate secondSubstrate = preyEvolve ? prey : predator;
				numSubstrateInputs += secondSubstrate.getSize().t1 * secondSubstrate.getSize().t2;
				substrateInformation.add(secondSubstrate);
			}

			substrateForPredators = preyEvolve || senseTeammates;
			substrateForPrey = !preyEvolve || senseTeammates;

			// Processing layer
			substrateInformation.add(new Substrate(substrateDimension, Substrate.PROCCESS_SUBSTRATE, processingLocation, "process_0"));
			// Output layer
			substrateInformation.add(new Substrate(outputSubstrateDimension, Substrate.OUTPUT_SUBSTRATE, outputLocation, "output_0"));
		}
		return substrateInformation;
	}

	private List<SubstrateConnectivity> substrateConnectivity = null;

	/**
	 * Returns a list of connections between substrates
	 *
	 * @return list of connections between substrates
	 */
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
		if (substrateConnectivity == null) {
			substrateConnectivity = new LinkedList<SubstrateConnectivity>();
			substrateConnectivity.add(new SubstrateConnectivity(preyEvolve ? "input_predator" : "input_prey", "process_0", SubstrateConnectivity.CTYPE_FULL));
			if (Parameters.parameters.booleanParameter("torusSenseTeammates"))
				substrateConnectivity.add(new SubstrateConnectivity(preyEvolve ? "input_prey" : "input_predator", "process_0", SubstrateConnectivity.CTYPE_FULL));
			substrateConnectivity.add(new SubstrateConnectivity("process_0", "output_0", SubstrateConnectivity.CTYPE_FULL));
		}
		return substrateConnectivity;
	}

	/**
	 * gets the inputs for the cppn. 1.0 corresponds to an agent at that
	 * location, 0.0 corresponds to no agent
	 *
	 * @param subs
	 * @return double[] double array containing all inputs to cppn from torus
	 *         gridworld
	 */
	public double[] getSubstrateInputs(List<Substrate> subs) {
		int torusWidth = this.exec.game.getWorld().width();
		double[] inputs = new double[numSubstrateInputs]; // defaults to 0.0

		if (substrateForPredators) {
			TorusAgent[] preds = exec.game.getPredators();
			List<Tuple2D> predsCoord = getCoordinates(preds);
			List<Integer> predsIndices = getIndices(predsCoord, torusWidth);
			for (Integer index : predsIndices) {
				inputs[index] = 1.0; // There is an agent at this position
			}
		}
		if (substrateForPrey) {
			TorusAgent[] prey = exec.game.getPrey();
			List<Tuple2D> preyCoord = getCoordinates(prey);
			List<Integer> preyIndices = getIndices(preyCoord, torusWidth);
			for (Integer index : preyIndices) {
				// push past all indices of the first substrate
				inputs[secondSubstrateStartingIndex + index] = 1.0; 
			}
		}
		return inputs;
	}

	/**
	 * gets the indices of agents in a torusWorld from the coordinates of each
	 * agent
	 *
	 * @param coords
	 *            list containing coordinates of each agent
	 * @param substrateWidth
	 *            width of substrate agents are located in (for calculating the
	 *            actual index)
	 * @return list of indices
	 */
	private List<Integer> getIndices(List<Tuple2D> coords, int substrateWidth) {
		List<Integer> indices = new LinkedList<Integer>();
		for (Tuple2D tuple : coords) {
			indices.add(indexFromCoordinates(tuple.x, tuple.y, substrateWidth));
		}
		return indices;
	}

	/**
	 * gets coordinates of each agent from an array of agents
	 *
	 * @param agents
	 *            array of agents
	 * @return coordinates of agents
	 */
	private List<Tuple2D> getCoordinates(TorusAgent[] agents) {
		List<Tuple2D> coords = new LinkedList<Tuple2D>();
		for (TorusAgent agent : agents) {
			if (agent != null) { // Prey are set to null after being eaten.
				coords.add(agent.getPosition());
			}
		}
		return coords;
	}

	/**
	 * gets the index of an agent from its coordinates
	 *
	 * @param x
	 *            x-coordinate of agent
	 * @param y
	 *            y-coordinate of agent
	 * @param substrateWidth
	 *            width of substrate agent is located in
	 * @return one-dimensional index in substrate
	 */
	private int indexFromCoordinates(double x, double y, int substrateWidth) {
		return (int) ((substrateWidth * y) + x);
	}
	
	/**
	 * Default behavior
	 */
	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	/**
	 * Default behavior
	 */
	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}
	
	/**
	 * Clears connectivity and information
	 */
	@Override
	public void flushSubstrateMemory() {
		substrateConnectivity = null;
		substrateInformation = null;
	}
	
	@Override
	public void postConstructionInitialization() {
		int numInputs = determineNumPredPreyInputs();
		MMNEAT.setNNInputParameters(numInputs, outputLabels().length);
	}

	/**
	 * Finds the number of inputs for the predPrey task, which is based on the
	 * type of agent that is being evolved's sensor inputs defined in its
	 * controller This has to be done to prevent a null pointer exception when
	 * first getting the sensor labels/number of sensors
	 * 
	 * @return numInputs
	 */
	public static int determineNumPredPreyInputs() {
		//this is probably covering all the cases, but this must cover all cases for all types
		//of predators tasks. 
		boolean isPredator = 
				MMNEAT.task instanceof TorusEvolvedPredatorsVsStaticPreyTask || 
				MMNEAT.task instanceof CooperativePredatorsVsStaticPreyTask;
		return determineNumPredPreyInputs(isPredator);
	}

	public static int determineNumPredPreyInputs(boolean isPredator) {
		NNTorusPredPreyController temp = new NNTorusPredPreyController(null, isPredator);
		return temp.getNumInputs();
	}
}
