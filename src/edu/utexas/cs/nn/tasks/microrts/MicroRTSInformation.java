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

	public double getAverageUnitDifference();

	public void setAvgUnitDiff(double diff);

	public int getBaseUpTime();

	public void setBaseUpTime(int but);

	public int getHarvestingEfficiency();

	public void setHarvestingEfficiency(int hei);

	public UnitTypeTable getUnitTypeTable();

	public GameState getGameState();

	public PhysicalGameState getPhysicalGameState();

	//for co-evolution:
	int getBaseUpTime2();

	void setBaseUpTime2(int but);

	int getHarvestingEfficiency2();
}
