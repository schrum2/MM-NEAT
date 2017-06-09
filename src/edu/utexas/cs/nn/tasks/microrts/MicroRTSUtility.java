package edu.utexas.cs.nn.tasks.microrts;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.microrts.fitness.ProgressiveFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import micro.ai.core.AI;
import micro.gui.PhysicalGameStateJFrame;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.PlayerAction;
import micro.rts.units.Unit;

/**
 * contains methods used by both MicroRTSTask and 
 * SinglePopulationCompetativeCoevolutionMicroRTSTask
 * 
 * @author alicequint
 * 
 */
public class MicroRTSUtility {

	public static final int WINDOW_LENGTH = 640;
	private static boolean prog = Parameters.parameters.classParameter("microRTSFitnessFunction").equals(ProgressiveFitnessFunction.class);
	private static boolean coevolution;
	static boolean base1Alive = false;
	static boolean base2Alive = false;

	private static int unitDifferenceNow = 0;
	private static GameState gs;

	//for % destroyed
	private static int uniqueAllTime1;
	private static int uniqueAllTime2;
	private static int unitDeaths1;
	private static int unitDeaths2;
	private static ArrayList<Unit> unitsAliveBefore = new ArrayList<Unit>();
	private static ArrayList<Unit> unitsStillAlive = new ArrayList<Unit>();

	private static ArrayList<Integer> workerWithResourceID = new ArrayList<>();

	public static <T> ArrayList<Pair<double[], double[]>> oneEval(AI ai1, AI ai2, MicroRTSInformation task, RTSFitnessFunction ff, PhysicalGameStateJFrame w) {		

		coevolution = ff.getCoevolution();
		gs = task.getGameState();
		PhysicalGameState pgs = task.getPhysicalGameState();
		boolean gameover = false;
		double averageUnitDifference = 0;
		//evaluates to correct number of cycles in accordance with competition rules: 8x8 => 3000, 16x16 => 4000, 24x24 => 5000, etc.
		int maxCycles = 1000 * (int) Math.ceil(Math.sqrt(pgs.getHeight()));
		ff.setMaxCycles(maxCycles);
		PlayerAction pa1;
		PlayerAction pa2;

		Unit currentUnit; 
		boolean baseDeath1Recorded = false;
		boolean baseDeath2Recorded = false;

		int currentCycle = 0;

		do{ //simulate game:
			try {
				pa1 = ai1.getAction(0, gs); //throws exception
				gs.issueSafe(pa1);
			} catch (Exception e1) { e1.printStackTrace();System.exit(1); }
			try {
				pa2 = ai2.getAction(1, gs); //throws exception
				gs.issueSafe(pa2);
			} catch (Exception e) { e.printStackTrace();System.exit(1); }

			if(prog){ //if our FitnessFunction needs us to record information throughout the game
				currentUnit = null;	
				unitDifferenceNow = 0;
				base1Alive = false;
				base2Alive = false;

				for(int i = 0; i < pgs.getWidth(); i++){
					for(int j = 0; j < pgs.getHeight(); j++){
						
						currentUnit = pgs.getUnitAt(i, j);
						if(currentUnit!=null){
							
							updateUnitsAlive(currentUnit);
							updateUnitDifference(currentUnit);
							if(currentUnit.getType().name.equals("Worker"))
								updateHarvestingEfficiency(currentUnit, coevolution, task);
							if(currentUnit.getType().name.equals("Base"))
								updateBaseIsAlive(currentUnit, coevolution);
							
						} //end if (there is a unit on this space)
					}//end j
				}//end i
				updateUnitDeaths();
				
				if(!base1Alive && !baseDeath1Recorded) {
					task.setBaseUpTime(gs.getTime(), 1);
					baseDeath1Recorded = true;
				}
				if(!base2Alive && !baseDeath2Recorded && coevolution) {
					task.setBaseUpTime(gs.getTime(), 2);
					baseDeath2Recorded = true;
				}
				currentCycle++;
				averageUnitDifference += (unitDifferenceNow - averageUnitDifference) / (1.0*currentCycle); //incremental calculation of the avg.
			} //end if(Parameters.. = progressive)
			gameover  = gs.cycle();
			if(CommonConstants.watch) w.repaint();
		}while(!gameover && gs.getTime()< maxCycles);
		ff.setGameEndTime(gs.getTime());
		
		//actually it looks like both of these 2 things are being calculated wrongly. oops.
		System.out.println("!!! " + unitDeaths2 + " out of " + uniqueAllTime2 + " = "+((unitDeaths2 * 100 ) / uniqueAllTime2) + " % !!!!");
		task.setPercentEnemiesDestroyed((unitDeaths2 * 100 ) / uniqueAllTime2, 1);
		if(coevolution)
			task.setPercentEnemiesDestroyed((unitDeaths1 * 100 ) / uniqueAllTime1, 2);
		task.setAvgUnitDiff(averageUnitDifference);
		if(CommonConstants.watch) 
			w.dispose();

		return ff.getFitness(gs);
	}

	private static void updateUnitsAlive(Unit u) { //add new units to stillAlive
		if(!unitsStillAlive.contains(u)){
			unitsStillAlive.add(u);

			if(!unitsAliveBefore.contains(u)){ //new unit created!
				if(u.getPlayer() == 0)
					uniqueAllTime1++;
				else if(u.getPlayer() == 1)
					uniqueAllTime2++;
			}
		}
	}
	
	private static void updateUnitDeaths() { //Remove from aliveBefore all that is not also in stillAlive 
		for(Unit u : unitsAliveBefore){
			if(!unitsStillAlive.contains(u)){
				unitsAliveBefore.remove(u);
				if(u.getPlayer() == 0){
					unitDeaths1++;
				} else if(u.getPlayer() == 1){
					unitDeaths2++;
				}
			}
		}
		for(Unit u : unitsStillAlive){ //about to move to the next game state: make unitsAliveBefore = unitsStillAlive
			if(!unitsAliveBefore.contains(u)){
				unitsAliveBefore.add(u);
			}
		}
	}

	private static void updateUnitDifference(Unit u){
		if(u.getPlayer() == 0){
			unitDifferenceNow+= u.getCost();
		} else if(u.getPlayer() == 1){
			unitDifferenceNow-= u.getCost();
		}
	}

	private static void updateHarvestingEfficiency(Unit u, boolean coevolution, MicroRTSInformation task){
		//assume the unit is a worker
		int id = (int) u.getID();
		int player = u.getPlayer()+1; //+1 because this methods returns 0 or 1, but we want to use it as 1 or 2
		if( (u.getPlayer() == 0 || coevolution) && u.getResources() >= 1 && !workerWithResourceID.contains(id))
			workerWithResourceID.add(id);
		else if(u.getResources() <= 0 && workerWithResourceID.contains(id)){
			workerWithResourceID.remove(workerWithResourceID.indexOf(id));
			//add one to player's harvesting efficiency 
			task.setHarvestingEfficiency(task.getHarvestingEfficiency(player)+1,player);
		}
	}

	//Assumes bases exist at the start of the map
	private static void updateBaseIsAlive(Unit u, boolean coevolution) {
		if(u.getPlayer() == 0){
			base1Alive = true;
		} else if(u.getPlayer() == 1){
			base2Alive = true;
		}
	}

	/**
	 * HyperNEAT Method that returns a list of information about the substrate layers
	 * contained in the network.
	 *
	 * @return List of Substrates in order from inputs to hidden to output
	 *         layers
	 */
	public static List<Substrate> getSubstrateInformation(PhysicalGameState pgs) {
		int height = pgs.getHeight();
		int width = pgs.getWidth();
		ArrayList<Substrate> subs = new ArrayList<Substrate>();

		Substrate inputsBoardState = new Substrate(new Pair<Integer, Integer>(width, height),
				Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Inputs Board State");
		subs.add(inputsBoardState);

		//		if(Parameters.parameters.booleanParameter("")) {
		//			
		//		}
		//		if( My){
		//			
		//		}

		// Alice: when ComplexEvaluationFunction is more developped, this method will
		// need to be generalized so that it can work with any combination of substrate parameters

		Substrate processing = new Substrate(new Pair<Integer, Integer>(width, height), 
				Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.PROCCESS_SUBSTRATE, 0), "Processing");
		subs.add(processing);
		Substrate output = new Substrate(new Pair<Integer, Integer>(1,1),
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Output");
		subs.add(output);

		return subs;
	} 

	/**
	 * HyperNEAT method that connects substrates to eachother
	 * @param pgs 
	 * 			physical game state in use
	 * @return
	 */
	public static List<Pair<String, String>> getSubstrateConnectivity(PhysicalGameState pgs) {
		ArrayList<Pair<String, String>> conn = new ArrayList<Pair<String, String>>();

		//Alice: when ComplexEvaluationFunction is more developped, here is where additional substrates will be connected to each other

		conn.add(new Pair<String, String>("Inputs Board State", "Processing"));			

		conn.add(new Pair<String, String>("Processing","Output"));
		if(Parameters.parameters.booleanParameter("extraHNLinks")) {
			conn.add(new Pair<String, String>("Inputs Board State","Output"));
		}
		return conn;
	}

	/**
	 * determines whether unit is in given range.
	 * @param u unit to be judged
	 * @param x1 top left x of range
	 * @param y1 top left y of range
	 * @param x2 bottom right x of range
	 * @param y2 bottom right y of range
	 * @return true if u is within or on the borders
	 */
	public static boolean isUnitInRange(Unit u, int x1, int y1, int x2, int y2){
		int unitX = u.getX(); 
		int unitY = u.getY();
		if(y1 > y2){ //if "top left" is lower
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}
		if(x1 > x2){ //if "top left" is farther right
			int temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if(unitX < x1 || unitY < y1 || unitX > x2 || unitY > y2){
			return false;
		} else
			return true;
	}
}
