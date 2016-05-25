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
 * @author Alex Rollins, Jacob Schrum, Lauren Gillespie A parent class which
 * defines the Predator Prey task which evolves either the predator or the prey
 * (specified by the user which to evolve) while the other is kept static. The
 * user also specifies the number of preys and predators to be included, as well
 * as their available actions. Runs the game so that predators attempt to eat
 * (get to the same location) the prey as soon as possible while prey attempt to
 * survive as long as possible
 * @param <T> Network phenotype being evolved
 */
public abstract class TorusPredPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask, HyperNEATTask {

    public static final String[] ALL_ACTIONS = new String[]{"UP", "RIGHT", "DOWN", "LEFT", "NOTHING"};
    public static final String[] MOVEMENT_ACTIONS = new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
    
    public static final int HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION = 3;

    /**
     * The getter method that returns the list of controllers for the predators
     *
     * @param individual the genotype that will be given to all predator agents
     * (homogeneous team)
     * @return list of controllers for predators
     */
    public abstract TorusPredPreyController[] getPredAgents(Genotype<T> individual);
    // Remember which agents are evolved. Can be cast to NNTorusPreyPreyController later
    protected TorusPredPreyController[] evolved = null;
    
    /**
     * The getter method that returns the list of controllers for the preys
     *
     * @param individual the genotype that will be given to all prey agents
     * (homogeneous team)
     * @return list of controllers for prey
     */
    public abstract TorusPredPreyController[] getPreyAgents(Genotype<T> individual);

    //boolean to indicate which agent is to be evolved
    private final boolean preyEvolve;

    //list of fitness scores
    protected ArrayList<GridTorusObjective<T>> objectives = new ArrayList<GridTorusObjective<T>>();

    private TorusWorldExec exec;

    /**
     * constructor for a PredPrey Task where either the predators are evolved
     * while prey are kept static or prey are evolved while predators are kept
     * static
     *
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
     *
     * @param o objective/fitness score
     * @param list of fitness scores
     */
    public final void addObjective(GridTorusObjective<T> o, ArrayList<GridTorusObjective<T>> list) {
        list.add(o);
        MMNEAT.registerFitnessFunction(o.getClass().getSimpleName());
    }

    @Override
    /**
     * A method that evaluates a single genotype Provides fitness for that
     * genotype based on the game time as well as other scores
     *
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

        //dispose of all panels inside of agents/controllers
        if (CommonConstants.monitorInputs) {
            // Dispose of existing panels
            for (int i = 0; i < evolved.length; i++) {
                ((NNTorusPredPreyController) (evolved)[i]).networkInputs.dispose();
            }
        }

        //gets the controller of the evolved agent(s), gets its network, and stores the number of modules for that network
        int numModes = ((NNTorusPredPreyController) evolved[0]).nn.numModules();
        //this will store the number of times each module is used by each agent 
        int[] overallAgentModeUsage = new int[numModes];
        for (TorusPredPreyController agent : evolved) {
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
    @Override
    public int numObjectives() {
        return objectives.size();
    }

    /**
     * @return the starting goals of this genotype in an array
     */
    @Override
    public double[] startingGoals() {
        return minScores();
    }

    /**
     * @return the minimum possible scores (worst scores) for this genotype if
     * it is a prey then the min score is 0 and if it's a predator min score is
     * the total time limit
     */
    @Override
    public double[] minScores() {
        double[] result = new double[numObjectives()];
        for (int i = 0; i < result.length; i++) {
            result[i] = objectives.get(i).minScore();
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
    	return ((NNTorusPredPreyController) (evolved[0])).sensorLabels();
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
        //if it is the predator evolving
        if (!preyEvolve) {
            return Parameters.parameters.booleanParameter("allowDoNothingActionForPredators")
                    ? ALL_ACTIONS
                    : MOVEMENT_ACTIONS;
        } else {//the prey is evolving
            return Parameters.parameters.booleanParameter("allowDoNothingActionForPreys")
                    ? ALL_ACTIONS
                    : MOVEMENT_ACTIONS;
        }
    }

    /**
     * Accesses the time stamps for the current game being executed, use for
     * evaluation purposes.
     * @return Number of elapsed steps in simulation
     */
    @Override
    public double getTimeStamp() {
        return exec.game.getTime();
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

    	if(substrateInformation == null) {
        //these parameters are called repeatedly, therefore created local variables to improve efficiency
        int torusWidth = Parameters.parameters.integerParameter("torusXDimensions");
        int torusHeight = Parameters.parameters.integerParameter("torusYDimensions");
        boolean senseTeammates = Parameters.parameters.booleanParameter("torusSenseTeammates");

        //used for locating substrate in vector space: Spacing an placement is somewhat arbitray ... for display purposes
        Triple<Integer, Integer, Integer> firstInputLocation = new Triple<Integer, Integer, Integer>(0,0,0);
        Triple<Integer, Integer, Integer> secondInputLocation = new Triple<Integer, Integer, Integer>(4,0,0);
        Triple<Integer, Integer, Integer> processingLocation = new Triple<Integer, Integer, Integer>(senseTeammates ? 2 : 0,4,0);
        Triple<Integer, Integer, Integer> outputLocation = new Triple<Integer, Integer, Integer>(senseTeammates ? 2 : 0,8,0);
        // Used for input and processing layers
        Pair<Integer, Integer> substrateDimension = new Pair<Integer, Integer>(torusWidth, torusHeight);
        Pair<Integer, Integer> outputSubstrateDimension = new Pair<Integer, Integer>(HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION, HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION);
        // Ordering of input substrate names
        
        Substrate predator = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, preyEvolve ? firstInputLocation : secondInputLocation, "input_predator");
        Substrate prey = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, preyEvolve ? secondInputLocation : firstInputLocation, "input_prey");
                    
        substrateInformation = new LinkedList<Substrate>();
        //order of pred/prey substrate important, helps in sorting later on in get substrate inputs method
        // Input layers
        numSubstrateInputs = 0;
        Substrate firstSubstrate = preyEvolve ? predator : prey;
        numSubstrateInputs += firstSubstrate.size.t1 * firstSubstrate.size.t2;
        secondSubstrateStartingIndex = numSubstrateInputs;
        substrateInformation.add(firstSubstrate);
        if(senseTeammates) {
        	Substrate secondSubstrate = preyEvolve ? prey : predator;
        	numSubstrateInputs += secondSubstrate.size.t1 * secondSubstrate.size.t2;
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

    
    private List<Pair<String, String>> substrateConnectivity = null;
    
    /**
     * Returns a list of connections between substrates
     *
     * @return list of connections between substrates
     */
    @Override
    public List<Pair<String, String>> getSubstrateConnectivity() {
    	if(substrateConnectivity == null){
        substrateConnectivity = new LinkedList<Pair<String, String>>();
        substrateConnectivity.add(new Pair<String, String>(preyEvolve ? "input_predator" : "input_prey", "process_0"));
        if(Parameters.parameters.booleanParameter("torusSenseTeammates")) 
        	substrateConnectivity.add(new Pair<String, String>(preyEvolve ? "input_prey" : "input_predator", "process_0"));            
        substrateConnectivity.add(new Pair<String, String>("process_0", "output_0"));
    	}
        return substrateConnectivity;
    }

    /**
     * gets the inputs for the cppn. 1.0 corresponds to an agent at that
     * location, 0.0 corresponds to no agent
     *
     * @param subs 
     * @return double[] double array containing all inputs to cppn from torus
     * gridworld
     */
    @Override
    public double[] getSubstrateInputs(List<Substrate> subs) {
        int torusWidth = this.exec.game.getWorld().width();
        double[] inputs = new double[numSubstrateInputs]; // defaults to 0.0

        if(substrateForPredators) {
        TorusAgent[] preds = exec.game.getPredators();
        List<Tuple2D> predsCoord = getCoordinates(preds);
        List<Integer> predsIndices = getIndices(predsCoord, torusWidth);
        for(Integer index: predsIndices) {
            inputs[index] = 1.0; // There is an agent at this position
        }
        
        }
        
        if(substrateForPrey) {
        TorusAgent[] prey = exec.game.getPrey();
        List<Tuple2D> preyCoord = getCoordinates(prey);
        List<Integer> preyIndices = getIndices(preyCoord, torusWidth);
        for(Integer index: preyIndices) {
            inputs[secondSubstrateStartingIndex + index] = 1.0; // push past all indices of the first substrate
        }
        }
        
        return inputs;
    }

    /**
     * gets the indices of agents in a torusWorld from the coordinates of each
     * agent
     *
     * @param coords list containing coordinates of each agent
     * @param substrateWidth width of substrate agents are located in (for
     * calculating the actual index)
     * @return list of indices
     */
    private List<Integer> getIndices(List<Tuple2D> coords, int substrateWidth) {
        List<Integer> indices = new LinkedList<Integer>();
        for (Tuple2D tuple: coords) {
            indices.add(indexFromCoordinates(tuple.x, tuple.y, substrateWidth));
        }
        return indices;
    }

    /**
     * gets coordinates of each agent from an array of agents
     *
     * @param agents array of agents
     * @return coordinates of agents
     */
    private List<Tuple2D> getCoordinates(TorusAgent[] agents) {
        List<Tuple2D> coords = new LinkedList<Tuple2D>();
        for (TorusAgent agent : agents) {
        	if(agent != null) { // Prey are set to null after being eaten.
        		coords.add(agent.getPosition());
        	}
        }
        return coords;
    }

    /**
     * gets the index of an agent from its coordinates
     *
     * @param x x-coordinate of agent
     * @param y y-coordinate of agent
     * @param substrateWidth width of substrate agent is located in
     * @return one-dimensional index in substrate
     */
    private int indexFromCoordinates(double x, double y, int substrateWidth) {
        return (int) ((substrateWidth * y) + x);
    }
}
