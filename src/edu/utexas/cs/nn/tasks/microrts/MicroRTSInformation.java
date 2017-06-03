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

	// TODO: Comments need to clarify what all of these do
	
	public double getAverageUnitDifference();

	public void setAvgUnitDiff(double diff);

	public int getBaseUpTime();

	public void setBaseUpTime(int but);

	public int getHarvestingEfficiency();

	public void setHarvestingEfficiency(int hei);

	public UnitTypeTable getUnitTypeTable();

	public GameState getGameState();

	public PhysicalGameState getPhysicalGameState();

	//for co-evolution: TODO: These need more clarification. Does the "2" mean for the second player?
	// If so, then you should instead generalize the original versions of the methods to take an index
	// identifying the player. Instead of having getBaseUpTime and getBaseUpTime2, just have getBaseUpTime(int player)
	int getBaseUpTime2();

	void setBaseUpTime2(int but);

	int getHarvestingEfficiency2();

	//for progressive fitness function
	public int getResourceGainValue();
}
