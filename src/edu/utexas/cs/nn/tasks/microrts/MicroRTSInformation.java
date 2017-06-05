package edu.utexas.cs.nn.tasks.microrts;

import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.UnitTypeTable;

/**
 * 
 * @author alicequint
 *
 *implemented by MicroRTSTask and the Co-evolving counterpart,
 *so that FF and MicroRTSUtility can access data tracked in their Evaluations.
 */
public interface MicroRTSInformation {
	
	//returns value stored in variable:
	public double getAverageUnitDifference();
	
	//stores passed-in value in averageUnitDifference
	public void setAvgUnitDiff(double diff);
	
	//returns players's value stored in variable: baseUpTime or baseUpTime2
	public int getBaseUpTime(int player);
	
	//stores passed-in value in BaseUpTime or BaseUpTime2 based on player 
	public void setBaseUpTime(int but, int player);
	
	//returns player's value stored in variable: harvestingEfficienyIndex or harvestingEfficienyIndex2  
	public int getHarvestingEfficiency(int player);
	
	//stores passed-in value in 
	public void setHarvestingEfficiency(int hei, int player);
	
	//returns current unit type table
	public UnitTypeTable getUnitTypeTable();
	
	//returns current game state
	public GameState getGameState();
	
	//returns current physical game state
	public PhysicalGameState getPhysicalGameState();
	
	//for progressive fitness function
	//returns value stored in constant RESOURCE_GAIN_VALUE
	public int getResourceGainValue();
}
