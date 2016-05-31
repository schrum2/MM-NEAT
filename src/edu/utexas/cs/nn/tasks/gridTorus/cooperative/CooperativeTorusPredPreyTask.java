package edu.utexas.cs.nn.tasks.gridTorus.cooperative;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.CooperativeTask;

public class CooperativeTorusPredPreyTask <T extends Network> extends CooperativeTask
implements TUGTask, NetworkTask {
	
	/**
	 * One genotype for each member of the team, and one score for each member
	 * as well
	 *
	 * @param team
	 *            list of the genotypes of the teammates
	 * @return list of scores to assign to each teammate
	 */
	public ArrayList<Score> evaluate(Genotype[] team){

		return null;
	}

	@Override
	public int numberOfPopulations() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] objectivesPerPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] otherStatsPerPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void finalCleanup() {
		// TODO Auto-generated method stub
		
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

	@Override
	public double[] startingGoals() {
		// TODO Auto-generated method stub
		return null;
	}

}
