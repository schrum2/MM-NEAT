package edu.southwestern.tasks.microrts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.JDOMException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.microrts.evaluation.NNEvaluationFunction;
import edu.southwestern.tasks.microrts.fitness.RTSFitnessFunction;
import edu.southwestern.tasks.microrts.iterativeevolution.EnemySequence;
import edu.southwestern.tasks.microrts.iterativeevolution.MapSequence;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;
import micro.ai.HasEvaluationFunction;
import micro.ai.core.AI;
import micro.gui.PhysicalGameStateJFrame;
import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;
import micro.rts.units.UnitTypeTable;

/**
 * 
 * evolves NNs for microRTS against opponents 
 * that are Not controlled by a NN.
 * 
 * @author alicequint
 * 
 * @param <T> NN
 */
public class MicroRTSTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask, MicroRTSInformation{

	private PhysicalGameState pgs;
	private PhysicalGameState initialPgs;
	private UnitTypeTable utt;
	private PhysicalGameStateJFrame w = null;
	private GameState gs;
	private String mapName;
	private boolean AiInitialized = false;
	private MapSequence maps = null;
	private EnemySequence enemySequencePlan = null;
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
			pgs = initialPgs.cloneIncludingTerrain();

			if(Parameters.parameters.classParameter("microRTSMapSequence") != null)
				maps = (MapSequence) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSMapSequence")); 

		} catch (JDOMException | IOException | NoSuchMethodException e) { 
			e.printStackTrace();
			System.exit(1);
		}
		ff.setCoevolution(false);
		ef.setCoevolution(false);
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
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
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
		if(enemySequencePlan == null){
			try {
				enemySequencePlan = (EnemySequence) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSEnemySequence"));
			} 
			catch (NoSuchMethodException e1) { e1.printStackTrace(); System.exit(1); }
			catch (NullPointerException e){} //enemies will not change over time, this is fine.
		}
		if(maps != null){
			String newMapName = maps.getAppropriateMap(((GenerationalEA) MMNEAT.ea).currentGeneration());
			if (!newMapName.equals(mapName)){ // Change the map
				System.out.println("loading new map: " + newMapName);
				try {
					// The new map is in the new initial game state
					initialPgs = PhysicalGameState.load("data/microRTS/maps/" + newMapName, utt);

					assert !initialPgs.getUnits().isEmpty(): "initial pgs has no units after map load";

					assert (unitsExist(0, initialPgs)): "player 0 does not have any units to start";
					assert (unitsExist(1, initialPgs)): "player 1 does not have any units to start";

					mapName = newMapName;
				} catch (JDOMException | IOException e) {
					e.printStackTrace(); System.exit(1);
				}
				ff.informOfMapSwitch();
			}
		}
	}

	private boolean unitsExist(int player, PhysicalGameState pgs){
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){

				Unit currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit!=null){
					//							System.out.println(i + "," + j + " has " + currentUnit);

					if(currentUnit.getPlayer() == player){
						return true;
					}
				} //end if (there is a unit on this space)
			}//end j
		}//end i
		return false;
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
		if(!AiInitialized)
			initializeAI();
		else{
			ef.givePhysicalGameState(initialPgs);
			if(ef2 != null)
				ef2.givePhysicalGameState(initialPgs);
		}
		if(enemySequencePlan!=null){ //growing sets of opponents
			ArrayList<AI> potentialNewEnemySet = enemySequencePlan.getAppropriateEnemySet(((GenerationalEA) MMNEAT.ea).currentGeneration(), ff);
			if(enemySet == null){
				enemySet = potentialNewEnemySet;
			}
		} else { //single opponent
			enemySet = new ArrayList<>(1); // will only contain the following enemy:
			enemySet.add(ai2);
		}
		ef.setNetwork(individual);

		double[][] fitnesses = new double[enemySet.size()][numObjectives()];
		double[][] others 	 = new double[enemySet.size()][numOtherScores()];

		assert enemySet.size() > 0 : "enemy set doesnt contain anything";

		for(int i = 0; i < enemySet.size(); i++){ //perform one evaluation for every enemy in the set
			reset();
			assert (unitsExist(0, pgs)): "player 0 does not have any units to start";
			assert (unitsExist(1, pgs)): "player 1 does not have any units to start";
			gs = new GameState(pgs, utt);
			if(CommonConstants.watch){
				w = PhysicalGameStatePanel.newVisualizer(gs,MicroRTSUtility.WINDOW_LENGTH,MicroRTSUtility.WINDOW_LENGTH,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
			}

			ai2 = enemySet.get(i);
			if(CommonConstants.watch){
				System.out.println("Current Enemy: "+ ai2.getClass().getName());
			}
			ArrayList<Pair<double[], double[]>> currentEval = MicroRTSUtility.oneEval((AI) ai1, ai2, this, ff, w);
			ff.setNumEvals(ef.getNumEvals());
			fitnesses[i] = currentEval.get(0).t1;
			others[i] 	 = currentEval.get(0).t2;
		}

		Pair<double[], double[]> averageResults = NoisyLonerTask.averageResults(fitnesses,others);
		return averageResults;
	}

	/**
	 * resets the conditions of the game to be how they are supposed
	 * to be at the beginning of an evaluation
	 */
	private void reset(){
		utt = new UnitTypeTable();
		averageUnitDifference = 0;
		baseUpTime = 0;
		harvestingEfficiencyIndex = 0;
		// Clone the initial game state; start from beginning
		pgs = initialPgs.cloneIncludingTerrain();
		ef.givePhysicalGameState(pgs);
	}
	/**
	 *initializes ai (only called once for efficiency in this oneEval) 
	 * @return 
	 */
	private void initializeAI() {
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

	@Override
	public int getNumInputSubstrates() {
		return ef.getNumInputSubstrates();
	}

	@Override
	public void flushSubstrateMemory() {
		// Does nothing: This task does not cache substrate information
	}
}
