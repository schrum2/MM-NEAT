package edu.utexas.cs.nn.tasks.microrts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.JDOMException;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.microrts.evaluation.NNEvaluationFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.iterativeevolution.EnemySequence;
import edu.utexas.cs.nn.tasks.microrts.iterativeevolution.MapSequence;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.ai.HasEvaluationFunction;
import micro.ai.core.AI;
import micro.gui.PhysicalGameStateJFrame;
import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.UnitTypeTable;

/**
 * @author alicequint
 * evolves NNs for microRTS against opponents 
 * that are Not controlled by a NN.
 * @param <T> NN
 */
public class MicroRTSTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask, MicroRTSInformation{

	private PhysicalGameState pgs;
	private PhysicalGameState initialPgs;
	private UnitTypeTable utt;
	private PhysicalGameStateJFrame w = null;
	private GameState gs;
	private String mapName;
	public boolean AiInitialized = false;
	private MapSequence maps = null;
	private EnemySequence enemies = null;
	private ArrayList<AI> enemySet;
	
	private double averageUnitDifference;
	private int baseUpTime;
	private int harvestingEfficiencyIndex;
	private double percentEnemiesDestroyed;

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
				ef2 = (NNEvaluationFunction<T>) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSOpponentEvaluationFunction"));
			ff = (RTSFitnessFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSFitnessFunction"));
			initialPgs = PhysicalGameState.load("data/microRTS/maps/" + Parameters.parameters.stringParameter("map"), utt);
			pgs = initialPgs.clone();
			
			if(Parameters.parameters.classParameter("microRTSMapSequence") != null)
				maps = (MapSequence) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSMapSequence")); 

		} catch (JDOMException | IOException | NoSuchMethodException e) { 
			e.printStackTrace();
			System.exit(1);
		}
		ff.setCoevolution(false);
		for(String function : ff.getFunctions()){
			MMNEAT.registerFitnessFunction(function);
		}
		for(String other : ff.getOtherScores()){
			MMNEAT.registerFitnessFunction(other, false);
		}
		ef.givePhysicalGameState(pgs);
		if(ef2 != null)
			ef2.givePhysicalGameState(pgs);
		ff.givePhysicalGameState(pgs);
		ff.setMaxCycles(5000);
		ff.giveTask(this);
		gs = new GameState(pgs, utt);
	}

	@Override
	public int numObjectives() {
		return ff.getFunctions().length;
	}
	
	@Override
	public int numOtherScores() {
		return ff.getOtherScores().length;
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
		return MicroRTSUtility.getSubstrateInformation(initialPgs);
	} 

	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		return MicroRTSUtility.getSubstrateConnectivity(initialPgs);
	}

	@Override
	public String[] sensorLabels() {
		return ef.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}

	/**
	 * called before each evaluation.
	 * If a MapSequence is used, then the new map is loaded here. This order of events is
	 * required in order to make sure networks are displayed properly, because an agent's
	 * network is displayed before the oneEval method below is called. Therefore, the map
	 * gets set here, then the neural network is drawn in LonerTask, then the oneEval method
	 * below executes.
	 */
	public void preEval() {
		if(enemies == null){
			try {
				enemies = (EnemySequence) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSEnemySequence"));
			} catch (NoSuchMethodException e1) { e1.printStackTrace(); System.exit(1);}
		}
		if(maps != null){
			String newMapName = maps.getAppropriateMap(MMNEAT.ea.currentGeneration());
			if (!newMapName.equals(mapName)){ // Change the map
				try {
					// The new map is in the new initial game state
					initialPgs = PhysicalGameState.load("data/microRTS/maps/" + newMapName, utt);
					mapName = newMapName;
				} catch (JDOMException | IOException e) {
					e.printStackTrace(); System.exit(1);
				}
				ff.informOfMapSwitch();
			}
		}
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
		utt = new UnitTypeTable();
		averageUnitDifference = 0;
		baseUpTime = 0;
		harvestingEfficiencyIndex = 0;
		// Clone the initial game state: fresh start
		pgs = initialPgs.clone();
		// Evaluation function needs to track this state as it changes
		ef.givePhysicalGameState(pgs);
		gs = new GameState(pgs, utt);
		if(!AiInitialized)
			initializeAI();
		else{
			ef.givePhysicalGameState(initialPgs);
			if(ef2 != null)
				ef2.givePhysicalGameState(initialPgs);
		}
		if(Parameters.parameters.classParameter("microRTSEnemySequence")!=null){
			enemySet = enemies.getAppropriateEnemy(MMNEAT.ea.currentGeneration());
		} else {
			enemySet.add(ai2);
		}
		ef.setNetwork(individual);
		if(CommonConstants.watch)
			w = PhysicalGameStatePanel.newVisualizer(gs,MicroRTSUtility.WINDOW_LENGTH,MicroRTSUtility.WINDOW_LENGTH,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
		
		System.out.println(enemySet.toString());
		double[][] fitnesses = new double[enemySet.size()][numObjectives()];
		double[][] others 	 = new double[enemySet.size()][numOtherScores()];
		
		for(int i = 0; i < enemySet.size(); i++){
			ai2 = enemySet.get(i);
			if(CommonConstants.watch){
				System.out.println("Current Enemy: "+ ai2.getClass().getName());
			}
			ArrayList<Pair<double[], double[]>> currentEval = MicroRTSUtility.oneEval((AI) ai1, ai2, this, ff, w);
			fitnesses[i] = currentEval.get(0).t1;
			others[i] 	 = currentEval.get(0).t1;
		}
		Pair<double[], double[]> averageResults = NoisyLonerTask.averageResults(fitnesses,others);
		return averageResults;
	} //END oneEval

	/**
	 *initializes ai (only called once for efficiency) 
	 * @return 
	 */
	void initializeAI() {
		try {
			ai1 = (HasEvaluationFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSAgent"));
			if(Parameters.parameters.classParameter("microRTSEnemySequence") == null)
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

	@Override
	public int getBaseUpTime(int player){
		if(player == 1)return baseUpTime;
		else throw new IllegalArgumentException("MicroRTSTask is not equipped to record results for > 1 player");
	}
	@Override
	public void setBaseUpTime(int but, int player) {
		if(player == 1)baseUpTime = but;
		else throw new IllegalArgumentException("MicroRTSTask is not equipped to record results for > 1 player");

	}
	@Override
	public int getHarvestingEfficiency(int player){
		if(player == 1) return harvestingEfficiencyIndex;
		else throw new IllegalArgumentException("MicroRTSTask is not equipped to record results for > 1 player");
	}
	@Override
	public void setHarvestingEfficiency(int hei, int player) {
		if(player == 1) harvestingEfficiencyIndex = hei;
		else throw new IllegalArgumentException("MicroRTSTask is not equipped to record results for > 1 player");
	}
	@Override
	public double getPercentEnemiesDestroyed(int player) {
		if(player == 1) return percentEnemiesDestroyed;
		else throw new IllegalArgumentException("MicroRTSTask is not equipped to record results for > 1 player");
	}

	@Override
	public void setPercentEnemiesDestroyed(double enemies, int player) {
		if(player == 1) percentEnemiesDestroyed = enemies;
		else throw new IllegalArgumentException("MicroRTSTask is not equipped to record results for > 1 player");
	}
	@Override
	public UnitTypeTable getUnitTypeTable() {return utt;}
	@Override
	public GameState getGameState() {return gs;}
	@Override
	public PhysicalGameState getPhysicalGameState() {return pgs;}
	@Override
	public double getAverageUnitDifference(){return averageUnitDifference;}
	@Override
	public void setAvgUnitDiff(double diff) {averageUnitDifference = diff;}
	
	//for progressive fitness function
	@Override
	public int getResourceGainValue() {
		return MicroRTSUtility.RESOURCE_GAIN_VALUE;
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
}
