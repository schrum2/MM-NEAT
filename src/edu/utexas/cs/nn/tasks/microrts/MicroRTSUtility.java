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
 * @author alicequint
 * contains methods used by both MicroRTSTask and SinglePopulationCompetativeCoevolutionMicroRTSTask
 */
public class MicroRTSUtility {
	
	public static final int WINDOW_LENGTH = 640;
	public static final int RESOURCE_GAIN_VALUE = 2;
	private static final int WORKER_OUT_OF_BOUNDS_PENALTY = 1;
	private static final double WORKER_HARVEST_VALUE = .5; //relative to 1 resource, for use in pool
	private static boolean prog = Parameters.parameters.classParameter("microRTSFitnessFunction").equals(ProgressiveFitnessFunction.class);
	
	public static <T> ArrayList<Pair<double[], double[]>> oneEval(AI ai1, AI ai2, MicroRTSInformation task, RTSFitnessFunction ff, PhysicalGameStateJFrame w) {		
		
		GameState gs = task.getGameState();
		PhysicalGameState pgs = task.getPhysicalGameState();
		boolean gameover = false;
		int averageUnitDifference = 0;
		//evaluates to correct number of cycles in accordance with competition rules: 8x8 => 3000, 16x16 => 4000, 24x24 => 5000, etc.
		int maxCycles = 1000 * (int) Math.ceil(Math.sqrt(pgs.getHeight()));
		ff.setMaxCycles(maxCycles);
		PlayerAction pa1;
		PlayerAction pa2;
		int unitDifferenceNow = 0;
		int maxBaseX = -1, maxBaseY = -1;
		double resourcePool = 0;
		double formerResourcePool = 0;
		Unit currentUnit;
		boolean base1Alive = false;
		boolean base2Alive = false;
		boolean baseDeathRecorded = false;
		
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
				unitDifferenceNow = 0;
				maxBaseX = -1; //Eventually will have to change this to accomodate maps where multiple bases will not be in a straight line
				maxBaseY = -1;
				resourcePool = 0;
				formerResourcePool = 0;
				currentUnit = null;				
				baseDeathRecorded = false;
				for(int i = 0; i < pgs.getWidth(); i++){
					for(int j = 0; j < pgs.getHeight(); j++){
						base1Alive = false;
						base2Alive = false; //TODO update p2's values, maybe make some of this stuff into methods: 
						currentUnit = pgs.getUnitAt(i, j);
						if(currentUnit!=null){
							if(currentUnit.getPlayer() == 0){
								unitDifferenceNow++;
								resourcePool += currentUnit.getCost();
								if(currentUnit.getType().name.equals("Base")){
									resourcePool += currentUnit.getResources();
									if(currentUnit.getX() > maxBaseX) maxBaseX = currentUnit.getX(); //if its a new base record its location
									if(currentUnit.getY() > maxBaseY) maxBaseY = currentUnit.getY();
									base1Alive = true;
									assert(baseDeathRecorded == false): "base was created after all previous bases have been destroyed!";
								} //end if(base)
								else if(currentUnit.getType().name.equals("Worker")){
									if(currentUnit.getResources() > 0){
										resourcePool += WORKER_HARVEST_VALUE;
										if(!isUnitInRange(currentUnit, 
												currentUnit.getPlayer() == 0 ? 0 : pgs.getWidth(), 
												currentUnit.getPlayer() == 0 ? 0 : pgs.getWidth(), 
														maxBaseX, maxBaseY)){
											task.setHarvestingEfficiency (task.getHarvestingEfficiency(1) - WORKER_OUT_OF_BOUNDS_PENALTY, 1); //for ai1
										}
									}
								}
							} //end if (unit is player 1's)
							else if(currentUnit.getPlayer() == 1){
								unitDifferenceNow--;
							} //end if (unit is player 2's)
						}
					}//end j
				}//end i
				if(!base1Alive && !baseDeathRecorded) {
					task.setBaseUpTime(gs.getTime(), 1);
					baseDeathRecorded = true;
				}
				if(resourcePool > formerResourcePool){
					task.setHarvestingEfficiency(task.getHarvestingEfficiency(1) + RESOURCE_GAIN_VALUE, 1);
				}
				formerResourcePool = resourcePool;
				averageUnitDifference = (unitDifferenceNow - averageUnitDifference) / (gs.getTime()+1);
			} //end if(Parameters.. = progressive)
			gameover  = gs.cycle();
			if(CommonConstants.watch) w.repaint();
		}while(!gameover && gs.getTime()< maxCycles);
		task.setAvgUnitDiff(averageUnitDifference);
		if(CommonConstants.watch) 
			w.dispose();
		
		return ff.getFitness(gs);
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
