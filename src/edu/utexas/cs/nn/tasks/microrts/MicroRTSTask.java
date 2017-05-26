package edu.utexas.cs.nn.tasks.microrts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;

//import javax.swing.JFrame;

import org.jdom.JDOMException;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
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
import edu.utexas.cs.nn.tasks.microrts.evaluation.NNEvaluationFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.ProgressiveFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.TerminalFitnessFunction;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import micro.ai.HasEvaluationFunction;
import micro.ai.RandomBiasedAI;
//import micro.ai.abstraction.WorkerRush;
//import micro.ai.abstraction.pathfinding.BFSPathFinding;
import micro.ai.core.AI;
import micro.ai.evaluation.SimpleSqrtEvaluationFunction3;
import micro.ai.mcts.uct.UCT;
import micro.ai.minimax.RTMiniMax.IDRTMinimax;
import micro.gui.PhysicalGameStatePanel;
//import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.PlayerAction;
import micro.rts.units.Unit;
import micro.rts.units.UnitTypeTable;

/**
 * 
 * @author alicequint
 *
 * @param <T> phenotype
 */
public class MicroRTSTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask{

	private PhysicalGameState pgs;
	private PhysicalGameState initialPgs;
	private UnitTypeTable utt;
	private int MAXCYCLES = 5000;
	private JFrame w = null;
	private GameState gs;
	private GameState igs; //initial game state
	private boolean gameover;
	private int currentCycle;
	private boolean AiInitialized = false;

	private double averageUnitDifference;
	private int baseUpTime;
	private int harvestingEfficiencyIndex;

	public static int RESOURCE_GAIN_VALUE = 2;
	public static int WORKER_OUT_OF_BOUNDS_PENALTY = 1;
	public static double WORKER_HARVEST_VALUE = .5; //relative to 1 resource, for use in pool

	NNEvaluationFunction<T> ef;
	NNEvaluationFunction<T> ef2;
	RTSFitnessFunction ff;
	
	HasEvaluationFunction ai1 = null;
	AI ai2 = null;

	@SuppressWarnings("unchecked")
	public MicroRTSTask() {
		utt = new UnitTypeTable();
		try {
			ef = (NNEvaluationFunction<T>) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSEvaluationFunction"));
			if(Parameters.parameters.classParameter("microRTSOpponentEvaluationFunction") != null)
				ef2 =(NNEvaluationFunction<T>) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSOpponentEvaluationFunction"));
			ff = (RTSFitnessFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSFitnessFunction"));
			initialPgs = PhysicalGameState.load("data/microRTS/maps/" + Parameters.parameters.stringParameter("map"), utt);
			pgs = initialPgs.clone();
		} catch (JDOMException | IOException | NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for(String function : ff.getFunctions()){
			MMNEAT.registerFitnessFunction(function);
		}
		ef.givePhysicalGameState(pgs);
		if(ef2 != null)
			ef2.givePhysicalGameState(pgs);
		ff.givePhysicalGameState(pgs);
		ff.setMaxCycles(5000);
		ff.giveTask(this);
		igs = new GameState(pgs, utt);
		gs = new GameState(pgs, utt);
		
	}

	@Override
	public int numObjectives() {
		return ff.getFunctions().length;
	}

	@Override
	public double getTimeStamp() {
		return gs == null ? 0 : gs.getTime();
	}

	/**
	 * default behavior
	 */
	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	/**
	 * default behavior
	 */
	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}

	/**
	 * Method that returns a list of information about the substrate layers
	 * contained in the network.
	 *
	 * @return List of Substrates in order from inputs to hidden to output
	 *         layers
	 */
	@Override
	public List<Substrate> getSubstrateInformation() {
		int height = pgs.getHeight();
		int width = pgs.getWidth();
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		Substrate inputsBoardState = new Substrate(new Pair<Integer, Integer>(width, height),
				Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Inputs Board State");
		Substrate processing = new Substrate(new Pair<Integer, Integer>(width, height), 
				Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.PROCCESS_SUBSTRATE, 0), "Processing");
		Substrate output = new Substrate(new Pair<Integer, Integer>(1,1),
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Output");
		subs.add(inputsBoardState);
		subs.add(processing);
		subs.add(output);
		return subs;
	} 

	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		ArrayList<Pair<String, String>> conn = new ArrayList<Pair<String, String>>();
		conn.add(new Pair<String, String>("Inputs Board State", "Processing"));
		conn.add(new Pair<String, String>("Processing","Output"));
		if(Parameters.parameters.booleanParameter("extraHNLinks")) {
			conn.add(new Pair<String, String>("Inputs Board State","Output"));
		}
		return conn;
	}

	@Override
	public String[] sensorLabels() {
		ef.givePhysicalGameState(pgs);
		return ef.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}

	/**
	 * all actions performed in a single evaluation of a genotype
	 * loop taken from GameVisualSimulationTest, the rest based on MsPacManTask.oneEval()
	 * 
	 * @param individual
	 *            genotype to be evaluated
	 * @param num
	 *            which evaluation is currently being performed
	 * @return Combination of fitness scores (multiobjective possible), and
	 *         other scores (for tracking non-fitness data)
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		//reset:
		gameover = false;
		utt = new UnitTypeTable();
		averageUnitDifference = 0;
		currentCycle = 1;
		baseUpTime = 0;
		harvestingEfficiencyIndex = 0;
		//file io should happen in the constructor, reset here TODO
		pgs = initialPgs.clone();
		gs = new GameState(pgs, utt);
		if(!AiInitialized)
			initializeAI();
		else{
			ef.givePhysicalGameState(initialPgs);
			if(ef2 != null){
				ef2.givePhysicalGameState(initialPgs);
			}
		}
		ef.setNetwork(individual);
		if(CommonConstants.watch)
			w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
		do{
			PlayerAction pa1;
			try {
				pa1 = ai1.getAction(0, gs); //throws exception
				gs.issueSafe(pa1);
			} catch (Exception e1) { e1.printStackTrace();System.exit(1); }
			PlayerAction pa2;
			try {
				pa2 = ai2.getAction(1, gs); //throws exception
				gs.issueSafe(pa2);
			} catch (Exception e) { e.printStackTrace();System.exit(1); }
			if(Parameters.parameters.classParameter("microRTSFitnessFunction").equals(ProgressiveFitnessFunction.class)){
				int unitDifferenceNow = 0;
				int maxBaseX = -1, maxBaseY = -1;
				double resourcePool = 0;
				double formerResourcePool = 0;
				Unit currentUnit;
				boolean baseAlive = false;
				boolean baseDeathRecorded = false;
				for(int i = 0; i < pgs.getWidth(); i++){
					for(int j = 0; j < pgs.getHeight(); j++){
						baseAlive = false;
						currentUnit = pgs.getUnitAt(i, j);
						if(currentUnit!=null){
							if(currentUnit.getPlayer() == 0){
								unitDifferenceNow++;
								resourcePool += currentUnit.getCost();
								if(currentUnit.getType().name.equals("Base")){
									resourcePool += currentUnit.getResources();
									if(currentUnit.getX() > maxBaseX) maxBaseX = currentUnit.getX();
									if(currentUnit.getY() > maxBaseY) maxBaseY = currentUnit.getY();
									baseAlive = true;
									assert(baseDeathRecorded == false): "base was created after all previous bases have been destroyed!";
								} //end if(base)
								else if(currentUnit.getType().name.equals("Worker")){
									if(currentUnit.getResources() > 0){
										resourcePool += WORKER_HARVEST_VALUE;
										if(!isUnitInRange(currentUnit, 0, 0, maxBaseX, maxBaseY)){
											harvestingEfficiencyIndex-=WORKER_OUT_OF_BOUNDS_PENALTY;
										}
									}
								}
							} //end if (unit is ours)
							else if(currentUnit.getPlayer() == 1) unitDifferenceNow--;
						}
					}//end j
				}//end i
				if(!baseAlive && !baseDeathRecorded) {
					baseUpTime = currentCycle;
					baseDeathRecorded = true;
				}
				if(resourcePool > formerResourcePool){
					harvestingEfficiencyIndex += RESOURCE_GAIN_VALUE;
				}
				formerResourcePool = resourcePool;
				averageUnitDifference += (unitDifferenceNow - averageUnitDifference) / currentCycle;
			} //end if(Parameters.. = progressive)
			gameover = gs.cycle();
			currentCycle++;
			if(CommonConstants.watch) w.repaint();
		}while(!gameover && gs.getTime()<MAXCYCLES);

		if(CommonConstants.watch) 
			w.dispose();
		return ff.getFitness(gs);
	} //END oneEval

	private void initializeAI() {
		try {
			ai1 = (HasEvaluationFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSAgent"));
			ai2 = (AI) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSOpponent"));
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
			System.exit(1);
		}
		ai1.setEvaluationFunction(ef);
		if(Parameters.parameters.classParameter("microRTSOpponentEvaluationFunction")!= null)
				((HasEvaluationFunction) ai2).setEvaluationFunction(ef2);
		AiInitialized = true;
	}

	//to be used by fitness function
	public double getAverageUnitDifference(){
		return averageUnitDifference;
	}

	//to be used by fitness function
	public int getBaseUpTime(){
		return baseUpTime;
	}

	//to be used by fitness function
	public int getHarvestingEfficiency(){
		return harvestingEfficiencyIndex;
	}

	public int getResourceGainValue(){
		return RESOURCE_GAIN_VALUE;
	}

	/**
	 * 
	 * @param u unit to be judged
	 * @param x1 top left x of range
	 * @param y1 top left y of range
	 * @param x2 bottom right x of range
	 * @param y2 bottom right y of range
	 * @return true if u is within or on the borders
	 */
	private boolean isUnitInRange(Unit u, int x1, int y1, int x2, int y2){
		int unitX = u.getX(); 
		int unitY = u.getY();
		if(unitX < x1 || unitY < y1 || unitX > x2 || unitY > y2){
			return false;
		} else
			return true;
	}

	public static void main(String[] rags){
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "watch:true"});
		//			MMNEAT.loadClasses();
		//			MicroRTSTask<TWEANN> test = new MicroRTSTask<>();
		//			TWEANNGenotype g = new TWEANNGenotype();
		//			Pair<double[], double[]> result = test.oneEval(g, -1);
		//			System.out.println(Arrays.toString(result.t1)+ " , "+Arrays.toString(result.t2));
		System.out.println();
	}

	public UnitTypeTable getUnitTypeTable() {
		return utt;
	}
}
