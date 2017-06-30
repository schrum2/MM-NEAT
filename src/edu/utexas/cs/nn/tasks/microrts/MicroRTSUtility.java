package edu.utexas.cs.nn.tasks.microrts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.microrts.fitness.ProgressiveFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.RTSFitnessFunction;
import edu.utexas.cs.nn.tasks.microrts.fitness.WinLossFitnessFunction;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import micro.ai.core.AI;
import micro.gui.PhysicalGameStateJFrame;
import micro.gui.PhysicalGameStatePanel;
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
	private static boolean prog = Parameters.parameters.classParameter("microRTSFitnessFunction").equals(ProgressiveFitnessFunction.class) 
							   || Parameters.parameters.classParameter("microRTSFitnessFunction").equals(WinLossFitnessFunction.class);
	private static boolean coevolution;
	
	public final static int processingDepth = Parameters.parameters.integerParameter("HNProcessDepth"); //not used yet
	public final static int processingWidth = Parameters.parameters.integerParameter("HNProcessWidth"); //not used yet
	
	private static MicroRTSInformation task;
	private static boolean stepByStep = Parameters.parameters.booleanParameter("stepByStep");

	public static <T> ArrayList<Pair<double[], double[]>> oneEval(AI ai1, AI ai2, MicroRTSInformation mrtsInfo, RTSFitnessFunction ff, PhysicalGameStateJFrame w) {		
		ArrayList<Integer> workerWithResourceID = new ArrayList<>(); //change to hashset		
		//for % destroyed ff
		HashSet<Long> createdUnitIDs1 = new HashSet<>();
		HashSet<Long> createdUnitIDs2 = new HashSet<>();
		//for fitness functions
		boolean base1Alive = false;
		boolean base2Alive = false;
		int unitDifferenceNow = 0;
		
		task = mrtsInfo;
		coevolution = ff.getCoevolution();
		GameState gs = task.getGameState();
		PhysicalGameState pgs = gs.getPhysicalGameState(); //task.getPhysicalGameState();
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
		
		int previousCreatedUnitsIDSize = 0;
		
		do{ //simulate game:
			try {
				pa1 = ai1.getAction(0, gs); //throws exception
				gs.issueSafe(pa1);
			} catch (Exception e1) { e1.printStackTrace();System.exit(1); }
			try {
				pa2 = ai2.getAction(1, gs); //throws exception
				gs.issueSafe(pa2);
			} catch (Exception e) { e.printStackTrace();System.exit(1); }
			
			if(stepByStep && currentCycle %2 == 0){
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			
			pgs = gs.getPhysicalGameState(); //update after actions
			if(prog){ //if our FitnessFunction needs us to record information throughout the game
				currentUnit = null;	
				unitDifferenceNow = 0;
				base1Alive = false;
				base2Alive = false;
				//				System.out.println("-------------------------------------------");
				for(int i = 0; i < pgs.getWidth(); i++){
					for(int j = 0; j < pgs.getHeight(); j++){

						currentUnit = pgs.getUnitAt(i, j);
						if(currentUnit!=null){
							//							System.out.println(i + "," + j + " has " + currentUnit);

							if(currentUnit.getPlayer() == 0){
								createdUnitIDs1.add(currentUnit.getID());
							}
							else if(currentUnit.getPlayer() == 1){
								createdUnitIDs2.add(currentUnit.getID());
							}

							unitDifferenceNow = updateUnitDifference(currentUnit, unitDifferenceNow);
							if(currentUnit.getType().name.equals("Worker")) {
								updateHarvestingEfficiency(workerWithResourceID, currentUnit, coevolution, task);
							}
							if(currentUnit.getType().name.equals("Base")){
								base1Alive = base1Alive || updateBaseIsAlive(currentUnit, 1);
								base2Alive = base2Alive || updateBaseIsAlive(currentUnit, 2);
							}
						} //end if (there is a unit on this space)
					}//end j
				}//end i
				
				assert previousCreatedUnitsIDSize <= createdUnitIDs2.size() : "createdUnitIDs2 decreased in size!!! "
						+previousCreatedUnitsIDSize + " ==> " + createdUnitIDs2.size() + " T: " + currentCycle;
				assert createdUnitIDs2.size() > 0 : "units not found! createdUnitIDs2.size() did not find any units. T: " + currentCycle;
				
				if((!base1Alive) && (!baseDeath1Recorded)) { //records base1 death time if the base was NOT the last unit destroyed
					task.setBaseUpTime(gs.getTime(), 1);
					baseDeath1Recorded = true;
				}
				if(!base2Alive && !baseDeath2Recorded && coevolution) { 
					task.setBaseUpTime(gs.getTime(), 2);
					baseDeath2Recorded = true;
				}
				currentCycle++;
				averageUnitDifference += (unitDifferenceNow - averageUnitDifference) / (1.0*currentCycle); //incremental calculation of the avg.
			} //end if prog
			gameover  = gs.cycle();
			if(CommonConstants.watch) w.repaint();
		}while(!gameover && gs.getTime()< maxCycles);
		
		ff.setGameEndTime(gs.getTime());
		int terminalUnits1= 0;
		int terminalUnits2= 0;
		if(prog){ //count remaining units, update
			if(!baseDeath1Recorded)
				task.setBaseUpTime(gs.getTime(), 1);
			if(!baseDeath2Recorded && coevolution)
				task.setBaseUpTime(gs.getTime(), 2);
			for(int i = 0; i < pgs.getWidth(); i++){
				for(int j = 0; j < pgs.getHeight(); j++){
					currentUnit = pgs.getUnitAt(i, j);
					if(currentUnit!=null){
						if(currentUnit.getPlayer() == 0)
							terminalUnits1++;
						else if(currentUnit.getPlayer() == 1)
							terminalUnits2++;
					}
				}
			}
			try{
				//createdIds' size should never = 0 because all players start with a base
				task.setPercentEnemiesDestroyed(((createdUnitIDs2.size() - terminalUnits2) * 100 ) / createdUnitIDs2.size(), 1); 
				if(coevolution)
					task.setPercentEnemiesDestroyed(((createdUnitIDs1.size() - terminalUnits1) * 100 ) / createdUnitIDs1.size(), 2);
			} catch(ArithmeticException e){
				System.out.println("Units 2 Ever created: " + createdUnitIDs2 + " : " + createdUnitIDs2.size()); //only shows units alive at the end
				System.out.println("Units 2 At End: " + terminalUnits2);
				System.out.println("Units 1" + createdUnitIDs1 + " : " + createdUnitIDs1.size());
				System.out.println("Units 2 At End: " + terminalUnits1);
				w = PhysicalGameStatePanel.newVisualizer(gs,MicroRTSUtility.WINDOW_LENGTH,MicroRTSUtility.WINDOW_LENGTH,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
				w.repaint();
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			task.setAvgUnitDiff(averageUnitDifference);
		}
		
		if(CommonConstants.watch)
			w.dispose();

		return ff.getFitness(gs);
	}

	private static int updateUnitDifference(Unit u, int unitDifferenceNow){
		if(u.getPlayer() == 0){
			return unitDifferenceNow+ u.getCost();
		} else if(u.getPlayer() == 1){
			return unitDifferenceNow- u.getCost();
		} else 
			return unitDifferenceNow;
	}

	private static void updateHarvestingEfficiency(ArrayList<Integer> workerWithResourceID, Unit u, boolean coevolution, MicroRTSInformation task){
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

	//Assumes bases exist at the start of the game
	private static boolean updateBaseIsAlive(Unit u, int player) {
		if(u.getPlayer() == 0 && player == 1){
			return true; //base1Alive
		} else if(u.getPlayer() == 1 && player == 2){
			return true; //base2Alive
		} else
			return false; //'player' does not match unit's player
	}

	/**
	 * HyperNEAT Method that returns a list of information about the substrate layers
	 * contained in the network.
	 *
	 * @return List of Substrates in order from inputs to hidden to output
	 *         layers
	 */
	public static List<Substrate> getSubstrateInformation(PhysicalGameState pgs) {
		List<Triple<String, Integer, Integer>> output = new LinkedList<>();
		output.add(new Triple<>("Utility", 1,1));
		int numInputSubstrates = getNumInputSubstrates();
		List<Substrate> result = HyperNEATUtil.getSubstrateInformation(pgs.getWidth(), pgs.getHeight(), numInputSubstrates, output);
		
		if(Parameters.parameters.booleanParameter("mRTSResourceProportion")) {
			// SimpleResourceProportionSubstrate is at the end, and has unusual size 
			Substrate previous = result.get(numInputSubstrates - 1);
			result.set(numInputSubstrates - 1, new Substrate(new Pair<>(1,1), previous.getStype(), previous.getSubLocation(), previous.getName(), previous.getFtype()));
		}
		
//		for(Substrate s: result) {
//			System.out.println(s);
//		}
		
		return result;
	} 

	/**
	 * HyperNEAT method that connects substrates to eachother
	 * @param pgs 
	 * 			physical game state in use
	 * @return
	 */
	public static List<Triple<String, String, Boolean>> getSubstrateConnectivity(PhysicalGameState pgs) {
		List<String> outputNames = new LinkedList<>();
		outputNames.add("Utility");
		return HyperNEATUtil.getSubstrateConnectivity(getNumInputSubstrates(), outputNames);
	}
	
	private static int getNumInputSubstrates() {
		return ((MicroRTSInformation) MMNEAT.task).getNumInputSubstrates();
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
