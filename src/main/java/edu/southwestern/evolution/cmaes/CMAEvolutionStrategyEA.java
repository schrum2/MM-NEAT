package edu.southwestern.evolution.cmaes;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;

public class CMAEvolutionStrategyEA extends MuLambda<ArrayList<Double>> {

	public CMAEvolutionStrategyEA(int mltype, SinglePopulationTask<ArrayList<Double>> task, int mu, int lambda,
			boolean io) {
		super(mltype, task, mu, lambda, io);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Genotype<ArrayList<Double>>> generateChildren(int numChildren,
			ArrayList<Score<ArrayList<Double>>> parentScores) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Genotype<ArrayList<Double>>> selection(int numParents,
			ArrayList<Score<ArrayList<Double>>> scores) {
		// TODO Auto-generated method stub
		return null;
	}

}
