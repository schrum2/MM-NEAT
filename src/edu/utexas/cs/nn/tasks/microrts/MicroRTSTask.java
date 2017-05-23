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
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.TerminalFitnessFunction;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import micro.ai.RandomBiasedAI;
//import micro.ai.abstraction.WorkerRush;
//import micro.ai.abstraction.pathfinding.BFSPathFinding;
import micro.ai.core.AI;
import micro.ai.evaluation.SimpleSqrtEvaluationFunction3;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.ai.mcts.uct.UCT;
import micro.ai.minimax.RTMiniMax.IDRTMinimax;
import micro.gui.PhysicalGameStatePanel;
//import micro.gui.PhysicalGameStatePanel;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.PlayerAction;
import micro.rts.units.Unit;
import micro.rts.units.UnitTypeTable;

public class MicroRTSTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask{

	private PhysicalGameState pgs;
	private UnitTypeTable utt;
	private int MAXCYCLES = 5000;
	private JFrame w = null;
	private GameState gs;
	
	NNEvaluationFunction<T> ef;
	RTSFitnessFunction ff;

	@SuppressWarnings("unchecked")
	public MicroRTSTask() {
		utt = new UnitTypeTable();
		try {
			ef = (NNEvaluationFunction<T>) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSEvaluationFunction"));
			ff = (RTSFitnessFunction) ClassCreation.createObject(Parameters.parameters.classParameter("microRTSFitnessFunction"));
			pgs = PhysicalGameState.load("data/microRTS/maps/" + Parameters.parameters.stringParameter("map"), utt);
			
		} catch (JDOMException | IOException | NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		MMNEAT.registerFitnessFunction("win/loss");
		MMNEAT.registerFitnessFunction("time");
		MMNEAT.registerFitnessFunction("unit-difference");
		ef.givePhysicalGameState(pgs);
		ff.givePhysicalGameState(pgs);
		ff.setMaxCycles(5000);
		gs = null;
	}

	@Override
	public int numObjectives() {
		return 3; // Once you generalize the fitness function, this will need to change
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
		//reset
		boolean gameover = false;
		utt = new UnitTypeTable();
		try {
			pgs = PhysicalGameState.load("data/microRTS/maps/" + Parameters.parameters.stringParameter("map"), utt);
			//PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		gs = new GameState(pgs, utt);
		ef.setNetwork(individual);
		ef.givePhysicalGameState(pgs);
		//AI ai1 = new NaiveMCTS(100,-1,100,10, 0.3f, 0.0f, 0.4f, new RandomBiasedAI(), ef, true);
		AI ai1 = new UCT(100, -1, 100, 10, new RandomBiasedAI(), ef);
		//AI ai1 = new WorkerRush(utt, new BFSPathFinding());
		AI ai2 = new RandomBiasedAI();

		if(CommonConstants.watch){
			w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
		}
		do{
			PlayerAction pa1;
			try {
				pa1 = ai1.getAction(0, gs); //throws exception
				gs.issueSafe(pa1);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}

			PlayerAction pa2;
			try {
				pa2 = ai2.getAction(1, gs); //throws exception
				gs.issueSafe(pa2);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			// simulate:
			gameover = gs.cycle();
			if(CommonConstants.watch)
				w.repaint();
		}while(!gameover && gs.getTime()<MAXCYCLES);
		if(CommonConstants.watch){
			w.dispose();
		}
		return ff.getFitness(gs);
	} //END oneEval

		public static void main(String[] rags){
			Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "watch:true", ""});
//			MMNEAT.loadClasses();
//			MicroRTSTask<TWEANN> test = new MicroRTSTask<>();
//			TWEANNGenotype g = new TWEANNGenotype();
//			Pair<double[], double[]> result = test.oneEval(g, -1);
//			System.out.println(Arrays.toString(result.t1)+ " , "+Arrays.toString(result.t2));
			System.out.println(Parameters.parameters.booleanParameter("netio"));
		}

}
