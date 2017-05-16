package edu.utexas.cs.nn.tasks.microrts;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

//import javax.swing.JFrame;

import org.jdom.JDOMException;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.ai.RandomBiasedAI;
import micro.ai.abstraction.WorkerRush;
import micro.ai.abstraction.pathfinding.BFSPathFinding;
import micro.ai.core.AI;
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

	public MicroRTSTask() {
		utt = new UnitTypeTable();
		try {
			pgs = PhysicalGameState.load("data/microRTS/maps/16x16/basesWorkers16x16.xml", utt);
			//		        PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	@Override
	public int numObjectives() {
		return 3; //this will probably change! 
	}

	@Override
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numCPPNInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Substrate> getSubstrateInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] outputLabels() {
		// TODO Auto-generated method stub
		return null;
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
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {return null;} //TODO this is the real one! just not using params yet
	public Pair<double[], double[]> testOneEval() {

		GameState gs = new GameState(pgs, utt);
		boolean gameover = false;

		AI ai1 = new WorkerRush(utt, new BFSPathFinding()); //this is the one being evaluated
		AI ai2 = new RandomBiasedAI();

		JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);

		do{ //original code in GameVisualSimulationTest contains lines that deal with making the game more viewable (slow)
			PlayerAction pa1;
			try {
				pa1 = ai1.getAction(0, gs);
				gs.issueSafe(pa1);
			} catch (Exception e) {
				System.out.println("player 1 crashed while thinking of a move");
				e.printStackTrace();
			}
			PlayerAction pa2;
			try {
				pa2 = ai2.getAction(1, gs);
				gs.issueSafe(pa2);
			} catch (Exception e) {
				System.out.println("player 2 crashed while thinking of a move");
				e.printStackTrace();
			}
			// simulate:
			gameover = gs.cycle();
			w.repaint();
			
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}


		}while(!gameover && gs.getTime()<MAXCYCLES);

		Pair<double[], double[]> score = new Pair<>(new double[3], new double[0]); 
			//first[]:{victory, time, unitDifference, } on a scale from -1 to 1, except unit difference, which starts at 0 and can go up or down

		//TODO make magic numbers into constants
		//potential scoring methods: # units created, # structures built, time, # enemy units destroyed, 
		//unit # differential (how hard it wins)
		int gameEndTime = gs.getTime();
		List<Unit> unitsLeft = gs.getUnits();
		System.out.println(unitsLeft.get(6).getType().name);
		
		if(gs.winner() == 0){ //victory organism being tested! 
			score.t1[0] = 1;
			score.t1[1] = (double) (MAXCYCLES - gameEndTime) / MAXCYCLES * 2 - 1; //lower time is better
			for(Unit u : unitsLeft){
				if(u.getType().name != "Resource") score.t1[2] += u.getType().cost;
			}
		} else { //defeat for organism being tested
			score.t1[0] = -1;
			score.t1[1] = (double) (MAXCYCLES - gameEndTime) / MAXCYCLES * -2 + 1; //holding out for longer is better
			for(Unit u : unitsLeft){
				if(u.getType().name != "Resource") score.t1[2] -= u.getType().cost;
			}
		}
		return fitnessFunction(gs);
	} //END testOne eval

	private Pair<double[], double[]> fitnessFunction(GameState terminalGameState) {
		Pair<double[], double[]> score = new Pair<>(new double[3], new double[0]); 
		//first[]:{victory, time, unitDifference, } on a scale from -1 to 1, except unit difference, which starts at 0 and can go up or down

	//TODO make magic numbers into constants
	//potential scoring methods: # units created, # structures built, time, # enemy units destroyed, 
	//unit # differential (how hard it wins)
	int gameEndTime = terminalGameState.getTime();
	List<Unit> unitsLeft = terminalGameState.getUnits();
	System.out.println(unitsLeft.get(6).getType().name);
	
	if(terminalGameState.winner() == 0){ //victory organism being tested! 
		score.t1[0] = 1;
		score.t1[1] = (double) (MAXCYCLES - gameEndTime) / MAXCYCLES * 2 - 1; //lower time is better
		for(Unit u : unitsLeft){
			if(u.getType().name != "Resource") score.t1[2] += u.getType().cost;
		}
	} else { //defeat for organism being tested
		score.t1[0] = -1;
		score.t1[1] = (double) (MAXCYCLES - gameEndTime) / MAXCYCLES * -2 + 1; //holding out for longer is better
		for(Unit u : unitsLeft){
			if(u.getType().name != "Resource") score.t1[2] -= u.getType().cost;
		}
	}
	return score;
	} //END fitnessFunction

	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false"});
		MMNEAT.loadClasses();
		MicroRTSTask<TWEANN> test = new MicroRTSTask<>();
		Pair<double[], double[]> result = test.testOneEval();
		System.out.println(Arrays.toString(result.t1)+ " , "+Arrays.toString(result.t2));
	}

}
