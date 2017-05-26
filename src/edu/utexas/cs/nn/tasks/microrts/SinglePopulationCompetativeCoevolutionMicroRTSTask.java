package edu.utexas.cs.nn.tasks.microrts;

import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

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
import edu.utexas.cs.nn.tasks.microrts.fitness.ProgressiveFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.ai.HasEvaluationFunction;
import micro.ai.core.AI;
import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.PlayerAction;
import micro.rts.units.Unit;
import micro.rts.units.UnitTypeTable;

/**
 * 
 * @author alicequint
 * 
 * @param <T> NN
 */
public class SinglePopulationCompetativeCoevolutionMicroRTSTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask, MicroRTSInformation {

	private PhysicalGameState pgs;
	private PhysicalGameState initialPgs;
	private UnitTypeTable utt;
	private int MAXCYCLES = 5000;
	private JFrame w = null;
	private GameState gs;
	private boolean gameover;
	private int currentCycle;
	private boolean AiInitialized = false;

	private double averageUnitDifference;
	private int baseUpTime1;
	private int baseUpTime2;
	private int harvestingEfficiencyIndex1;
	private int harvestingEfficiencyIndex2;

	public static int RESOURCE_GAIN_VALUE = 2;
	public static int WORKER_OUT_OF_BOUNDS_PENALTY = 1;
	public static double WORKER_HARVEST_VALUE = .5; //relative to 1 resource, for use in pool

	NNEvaluationFunction<T> ef1;
	NNEvaluationFunction<T> ef2;
	RTSFitnessFunction ff;

	HasEvaluationFunction ai1 = null;
	HasEvaluationFunction ai2 = null;

	public SinglePopulationCompetativeCoevolutionMicroRTSTask() {
		utt = new UnitTypeTable();
		try {
			ef1 = (NNEvaluationFunction<T>) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSEvaluationFunction"));
			ef2 =(NNEvaluationFunction<T>) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSOpponentEvaluationFunction"));
			ff = (RTSFitnessFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSFitnessFunction"));
			initialPgs = PhysicalGameState.load("data/microRTS/maps/" + Parameters.parameters.stringParameter("map"), utt);
		} catch (JDOMException | IOException | NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for(String function : ff.getFunctions()){
			MMNEAT.registerFitnessFunction(function);
		}
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
	public List<Pair<String, String>> getSubstrateConnectivity() {
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

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		//reset:
		gameover = false;
		utt = new UnitTypeTable();
		averageUnitDifference = 0;
		currentCycle = 1;
		baseUpTime1 = 0;
		baseUpTime2 = 0;
		harvestingEfficiencyIndex1 = 0;
		harvestingEfficiencyIndex2 = 0;
		pgs = initialPgs.clone();
		gs = new GameState(pgs, utt);
		if(!AiInitialized)
			initializeAI();
		else{
			ef1.givePhysicalGameState(initialPgs);
			ef2.givePhysicalGameState(initialPgs);
		}
		ef1.setNetwork(individual);
		ef2.setNetwork(individual); //TODO probably have to find another one
		if(CommonConstants.watch)
			w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);
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
		return null;

	}

	private void initializeAI() {
		try {
			ai1 = (HasEvaluationFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSAgent"));
			ai2 = (HasEvaluationFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSOpponent"));
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
			System.exit(1);
		}
		ai1.setEvaluationFunction(ef1);
		ai2.setEvaluationFunction(ef2);
		AiInitialized = true;
	}

	@Override
	public double getAverageUnitDifference(){
		return averageUnitDifference;
	}

	@Override
	public int getBaseUpTime(){
		return baseUpTime1 - baseUpTime2;
	}

	@Override
	public int getHarvestingEfficiency(){
		return harvestingEfficiencyIndex1 - harvestingEfficiencyIndex2;
	}

	@Override
	public int getResourceGainValue(){
		return RESOURCE_GAIN_VALUE;
	}

	@Override
	public UnitTypeTable getUnitTypeTable() {
		return utt;
	}

}
