package edu.utexas.cs.nn.tasks.microrts;

import edu.utexas.cs.nn.tasks.microrts.evaluation.NNEvaluationFunction;
import micro.ai.HasEvaluationFunction;
import micro.ai.core.AI;
import micro.ai.evaluation.EvaluationFunction;
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

		public int getBaseUpTime();

		public int getHarvestingEfficiency();
		
		public void setHarvestingEfficiency();

		public int getResourceGainValue();
		
		public UnitTypeTable getUnitTypeTable();
}
