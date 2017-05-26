package edu.utexas.cs.nn.tasks.microrts;

import micro.rts.units.UnitTypeTable;

/**
 * 
 * @author alicequint
 *
 *implemented by MicroRTSTask and the Co-evolving counterpart,
 *so that FF can access data tracked in their Evaluations.
 */
public interface MicroRTSInformation {

		public double getAverageUnitDifference();

		public int getBaseUpTime();

		public int getHarvestingEfficiency();

		public int getResourceGainValue();
		
		public UnitTypeTable getUnitTypeTable();
}
