package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.GroupTask;

public class MultiPopulationCompetativeCoevolutionBoardGameTask extends GroupTask{

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
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void finalCleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Score> evaluate(Genotype[] team) {
		// TODO Auto-generated method stub
		return null;
	}

}
