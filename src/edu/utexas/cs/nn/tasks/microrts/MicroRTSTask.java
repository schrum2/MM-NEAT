package edu.utexas.cs.nn.tasks.microrts;

import java.io.IOException;
import java.util.List;

//import javax.swing.JFrame;

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
 *
 * @param <T> NN
 */
public class MicroRTSTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask, MicroRTSInformation{

	private PhysicalGameState pgs;
	private PhysicalGameState initialPgs;
	private UnitTypeTable utt;
	private PhysicalGameStateJFrame w = null;
	private GameState gs;
	public boolean AiInitialized = false;

	private double averageUnitDifference;
	private int baseUpTime;
	private int harvestingEfficiencyIndex;

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
		MMNEAT.registerFitnessFunction("win?", false);
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
		return MicroRTSUtility.getSubstrateInformation(pgs);
	} 

	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		return MicroRTSUtility.getSubstrateConnectivity(pgs);
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
		return MicroRTSUtility.oneEval((AI) ai1, ai2, this, ff, w);

	} //END oneEval

	/**
	 *initializes ai (only called once for efficiency) 
	 * @return 
	 */
	void initializeAI() {
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

	@Override
	public double getAverageUnitDifference(){return averageUnitDifference;}
	@Override
	public int getBaseUpTime(){return baseUpTime;}
	@Override
	public void setBaseUpTime(int but) {baseUpTime = but;}
	@Override
	public int getHarvestingEfficiency(){return harvestingEfficiencyIndex;}
	@Override
	public void setHarvestingEfficiency(int hei) {harvestingEfficiencyIndex = hei;}
	@Override
	public UnitTypeTable getUnitTypeTable() {return utt;}
	@Override
	public GameState getGameState() {return gs;}
	@Override
	public PhysicalGameState getPhysicalGameState() {return pgs;}
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

	//these methods here because they needed to be in Interface
	//they do not affect anything
	@Override
	public int getBaseUpTime2() {return -1;}
	@Override
	public void setBaseUpTime2(int but) {return;}
	@Override
	public int getHarvestingEfficiency2() {return -1;}
}
