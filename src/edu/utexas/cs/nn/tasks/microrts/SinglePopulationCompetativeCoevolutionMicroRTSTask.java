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
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.tasks.microrts.evaluation.NNEvaluationFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.iterativeevolution.MapSequence;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import micro.ai.HasEvaluationFunction;
import micro.ai.core.AI;
import micro.gui.PhysicalGameStateJFrame;
import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.UnitTypeTable;

/**
 * @author alicequint
 * 
 * evolves NNs for microRTS against opponents that are 
 * controlled by other members of the same population
 * 
 * @param <T> NN
 */
public class SinglePopulationCompetativeCoevolutionMicroRTSTask<T extends Network> extends SinglePopulationCoevolutionTask<T> implements NetworkTask, HyperNEATTask, MicroRTSInformation {

	private PhysicalGameState pgs;
	private PhysicalGameState initialPgs;
	private UnitTypeTable utt;
	private PhysicalGameStateJFrame w = null;
	private GameState gs;
	private boolean AiInitialized = false;
	private MapSequence maps = null;
	private String mapName;

	private double averageUnitDifference;
	private int baseUpTime1;
	private int baseUpTime2;
	private int harvestingEfficiencyIndex1;
	private int harvestingEfficiencyIndex2;
	private double percentEnemiesDestroyed1;
	private double percentEnemiesDestroyed2;

	NNEvaluationFunction<T> ef1;
	NNEvaluationFunction<T> ef2;
	RTSFitnessFunction ff;

	HasEvaluationFunction ai1 = null;
	HasEvaluationFunction ai2 = null;

	@SuppressWarnings("unchecked")
	public SinglePopulationCompetativeCoevolutionMicroRTSTask() {
		utt = new UnitTypeTable();
		//create objects
		try {
			ef1 = (NNEvaluationFunction<T>) ClassCreation.createObject("microRTSEvaluationFunction");
			ef2 = (NNEvaluationFunction<T>) ClassCreation.createObject("microRTSEvaluationFunction");
			ff = (RTSFitnessFunction) ClassCreation.createObject("microRTSFitnessFunction");
			initialPgs = PhysicalGameState.load("data/microRTS/maps/" + Parameters.parameters.stringParameter("map"), utt);
			
			if(Parameters.parameters.classParameter("microRTSMapSequence") != null)
				maps = (MapSequence) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSMapSequence")); 
		} catch (JDOMException | IOException | NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ff.setCoevolution(true);
		ef1.setCoevolution(true);
		ef2.setCoevolution(true);
		for(String function : ff.getFunctions()){
			MMNEAT.registerFitnessFunction(function);
		}
		//creates a copy of the physical game state so that it can be edited without changing initialPgs
		pgs = initialPgs.clone();
		ef1.givePhysicalGameState(pgs);
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

	@Override
	public List<Substrate> getSubstrateInformation() {
		return MicroRTSUtility.getSubstrateInformation(pgs);

	}

	@Override
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity() {
		return MicroRTSUtility.getSubstrateConnectivity(pgs);
	}

	/**
	 * @return ef1's sensor labels if it and ef2 are the same, or
	 * an array containing ef1's sensor labels followed by ef2's
	 * if they are different.
	 */
	@Override
	public String[] sensorLabels() {
		if(ef1.getClass() == ef2.getClass())
			return ef1.sensorLabels();
		else {
			String[] labels = new String[ef1.sensorLabels().length+ef2.sensorLabels().length];
			for(int i = 0; i < ef1.sensorLabels().length; i++){
				labels[i] = ef1.sensorLabels()[i];
			}
			for(int i = 0; i < ef2.sensorLabels().length; i++){
				labels[i+ef1.sensorLabels().length] = ef2.sensorLabels()[i];
			}
			return null;
		}
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}

	/**
	 * all actions performed in a single evaluation of a genotype
	 * 
	 * @param individual
	 *            genotype to be evaluated
	 * @param num
	 *            which evaluation is currently being performed
	 * @return Combination of fitness scores (multiobjective possible), and
	 *         other scores (for tracking non-fitness data)
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> evaluateGroup(ArrayList<Genotype<T>> group) {
		//reset:
		utt = new UnitTypeTable();
		averageUnitDifference = 0;
		baseUpTime1 = 0;
		baseUpTime2 = 0;
		harvestingEfficiencyIndex1 = 0;
		harvestingEfficiencyIndex2 = 0;
		pgs = initialPgs.clone();
		gs = new GameState(pgs, utt);
		if(!AiInitialized)
			initializeAI();
		ef1.setNetwork(group.get(0)); //pass each agent its own neural network
		ef2.setNetwork(group.get(1));
		if(CommonConstants.watch)
			w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);
		ArrayList<Pair<double[], double[]>> al = (MicroRTSUtility.oneEval((AI)ai1, (AI)ai2, this, ff, w));
		return al;
	}

	/**
	 *initializes ai (only called once for efficiency) 
	 * @return 
	 */
	void initializeAI() {
		try {
			ai1 = (HasEvaluationFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSAgent"));
			ai2 = (HasEvaluationFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSAgent"));
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
			System.exit(1);
		}
		ai1.setEvaluationFunction(ef1);
		ai2.setEvaluationFunction(ef2);
		AiInitialized = true;
	}

	@Override
	public double[] minScores() {
		return new double[numObjectives()]; //all 0's, not necessarily correct
	}

	@Override
	public int groupSize() {
		return 2;
	}

	@Override
	public void preEval() {
		if(Parameters.parameters.classParameter("microRTSMapSequence") != null){
			String newMapName = maps.getAppropriateMap(MMNEAT.ea.currentGeneration());
			if (!newMapName.equals(mapName)){ // Change the map
				try {
					// The new map is in the new initial game state
					initialPgs = PhysicalGameState.load("data/microRTS/maps/" + newMapName, utt);
					mapName = newMapName;
				} catch (JDOMException | IOException e) {
					e.printStackTrace(); System.exit(1);
				}
			}
		}

	}

	@Override
	public int getBaseUpTime(int player){
		if(player == 1) return baseUpTime1;
		else if(player == 2) return baseUpTime2;
		else throw new IllegalArgumentException("not a valid player: " + player);
	}
	@Override
	public void setBaseUpTime(int but, int player) {
		if(player == 1) baseUpTime1 = but;
		else if(player == 2) baseUpTime2 = but;
		else throw new IllegalArgumentException("not a valid player: " + player);
	}
	@Override
	public int getHarvestingEfficiency(int player){
		if(player == 1) return harvestingEfficiencyIndex1;
		else if (player == 2) return harvestingEfficiencyIndex2;
		else throw new IllegalArgumentException("not a valid player: " + player);
	}
	@Override
	public void setHarvestingEfficiency(int hei, int player) {
		if(player == 1) harvestingEfficiencyIndex1 = hei;
		else if(player == 2) harvestingEfficiencyIndex2 = hei;
		else throw new IllegalArgumentException("not a valid player: " + player);
	}
	@Override
	public double getPercentEnemiesDestroyed(int player) {
		if(player == 1)	return percentEnemiesDestroyed1;
		if(player == 2) return percentEnemiesDestroyed2;
		else throw new IllegalArgumentException("not a valid player: " + player);
	}
	@Override
	public void setPercentEnemiesDestroyed(double enemies, int player) {
		if(player == 1) percentEnemiesDestroyed1 = enemies;
		else if (player == 2) percentEnemiesDestroyed2 = enemies;
		else throw new IllegalArgumentException("not a valid player: " + player);
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
		return Math.max(ef1.getNumInputSubstrates(),ef2.getNumInputSubstrates());
	}
}
