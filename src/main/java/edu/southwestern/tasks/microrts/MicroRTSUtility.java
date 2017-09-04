package edu.southwestern.tasks.microrts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.microrts.fitness.ProgressiveFitnessFunction;
import edu.southwestern.tasks.microrts.fitness.RTSFitnessFunction;
import edu.southwestern.tasks.microrts.fitness.WinLossFitnessFunction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
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
	
	private static MicroRTSInformation task;
	private static boolean stepByStep = Parameters.parameters.booleanParameter("stepByStep");

	public static <T> ArrayList<Pair<double[], double[]>> oneEval(AI ai1, AI ai2, MicroRTSInformation mrtsInfo, RTSFitnessFunction ff, PhysicalGameStateJFrame w) {		
		AI[] ais = new AI[]{ai1,ai2};
		ArrayList<Integer> workerWithResourceID = new ArrayList<>(); //change to hashset		
		//for % destroyed ff
		HashSet<Long> createdUnitIDs1 = new HashSet<>();
		HashSet<Long> createdUnitIDs2 = new HashSet<>();
		//for fitness functions
		boolean[] baseAlive = new boolean[ais.length]; // default to false
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

		Unit currentUnit; 
		boolean[] baseDeathRecorded = new boolean[ais.length];

		int currentCycle = 0;
		
		int previousCreatedUnitsIDSize = 0;
		
		do{ //simulate game:
			// Each agent acts	
			for(int i = 0; i < ais.length; i++) {
				try {
					PlayerAction pa = ais[i].getAction(i, gs); //throws exception
					gs.issueSafe(pa);
				} catch (Exception e) { e.printStackTrace(); System.exit(1); }				
			}
			
			if(stepByStep && currentCycle %2 == 0){
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			
			pgs = gs.getPhysicalGameState(); //update after actions
			if(prog){ //if our FitnessFunction needs us to record information throughout the game
				currentUnit = null;	
				unitDifferenceNow = 0;
				for(int i = 0; i < baseAlive.length; i++) {
					baseAlive[i] = false;
				}
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
								for(int k = 0; k < baseAlive.length; k++) {
									// updateBaseIsAlive expects player values 1 and 2 rather than 0 and 1
									baseAlive[k] = baseAlive[k] || updateBaseIsAlive(currentUnit, k+1);
								}
							}
						} //end if (there is a unit on this space)
					}//end j
				}//end i
				
				assert previousCreatedUnitsIDSize <= createdUnitIDs2.size() : "createdUnitIDs2 decreased in size!!! "
						+previousCreatedUnitsIDSize + " ==> " + createdUnitIDs2.size() + " T: " + currentCycle;
				assert createdUnitIDs2.size() > 0 : "units not found! createdUnitIDs2.size() did not find any units. T: " + currentCycle;
				
				for(int k = 0; k < baseAlive.length; k++) {
					if((!baseAlive[k]) && (!baseDeathRecorded[k]) && (k == 0 || coevolution)) { //records base1 death time if the base was NOT the last unit destroyed
						// setBaseUpTime expects player values 1 and 2 rather than 0 and 1
						task.setBaseUpTime(gs.getTime(), k+1);
						baseDeathRecorded[k] = true;
					}				
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
			for(int k = 0; k < baseAlive.length; k++) {
				if(!baseDeathRecorded[k] && (k == 0 || coevolution))
					task.setBaseUpTime(gs.getTime(), k+1);
			}			
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
				task.setPercentEnemiesDestroyed(((createdUnitIDs2.size() - terminalUnits2) * 100.0 ) / createdUnitIDs2.size(), 1); 
				if(coevolution)
					task.setPercentEnemiesDestroyed(((createdUnitIDs1.size() - terminalUnits1) * 100.0 ) / createdUnitIDs1.size(), 2);
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
		// For some reason, player values are 1 and 2 while getPlayer results are 0 and 1
		return (u.getPlayer() + 1 == player);
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
		List<Triple<String, String, Boolean>> result = HyperNEATUtil.getSubstrateConnectivity(getNumInputSubstrates(), outputNames);
		if(Parameters.parameters.booleanParameter("mRTSResourceProportion")) { // This substrate should not allow convolution
			int index = getNumInputSubstrates() - 1;
			for(Triple<String, String, Boolean> triple : result) {
				if(triple.t1.equals("Input(" + index + ")")) { // This is the resource proportion substrate
					triple.t3 = Boolean.FALSE; // Convolution not allowed
				}
				
			}
		}
		return result;
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
