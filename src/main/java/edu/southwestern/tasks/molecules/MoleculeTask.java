package edu.southwestern.tasks.molecules;

import java.util.ArrayList;
import java.util.HashMap;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class MoleculeTask<T> extends NoisyLonerTask<T> {

	private int numFitnessFunctions;

	public MoleculeTask() {
		numFitnessFunctions = 0;
		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			MMNEAT.registerFitnessFunction("moleculeTargetMeltingAndBoilingPointFitness");
			numFitnessFunctions++;
		}
		
		MMNEAT.registerFitnessFunction("meltingPoint",false);
		MMNEAT.registerFitnessFunction("boilingPoint",false);
	}
	
	@Override
	public void postConstructionInitialization() {
		// Nothing
	}
	
	@Override
	public int numObjectives() {
		return numFitnessFunctions;
	}

	@Override
	public double getTimeStamp() {
		// Not used
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String,Object> behaviorCharacteristics) {
		
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		
		// Get melting point and boiling point first
		Pair<Double, Double> pair = MoleculeMeltingAndBoilingPointProcess.smilesMeltingAndBoilingPoints((SMILESStringGenotype) individual);
		double meltingPoint = pair.t1;
		double boilingPoint = pair.t2;
		
		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			double targetMeltingPoint = Parameters.parameters.doubleParameter("smilesTargetMeltingPoint");
			double targetBoilingPoint = Parameters.parameters.doubleParameter("smilesTargetBoilingPoint");
			
			double meltDifference = targetMeltingPoint - meltingPoint;
			double boilDifference = targetBoilingPoint - boilingPoint;
			double fitness = Math.sqrt(meltDifference*meltDifference + boilDifference*boilDifference);
			
			fitnesses.add(fitness);
		}
		
		double[] otherScores = new double[] {meltingPoint, boilingPoint};
		
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}
	
	
}
