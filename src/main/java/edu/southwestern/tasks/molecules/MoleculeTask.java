package edu.southwestern.tasks.molecules;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class MoleculeTask<T> extends NoisyLonerTask<T> {

	private int numFitnessFunctions;
	private static final int NUM_OTHER_SCORES = 2;
	
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
	
	public double[] minScores() {
		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			return new double[] {-1500};
		}
		throw new IllegalStateException("No fitness defined");
	}

	public int numOtherScores() {
		return NUM_OTHER_SCORES;
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
		//System.out.println(pair);
		double meltingPoint = pair.t1;
		double boilingPoint = pair.t2;
		
		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			double targetMeltingPoint = Parameters.parameters.doubleParameter("smilesTargetMeltingPoint");
			double targetBoilingPoint = Parameters.parameters.doubleParameter("smilesTargetBoilingPoint");
			
			double meltDifference = targetMeltingPoint - meltingPoint;
			double boilDifference = targetBoilingPoint - boilingPoint;
			double differenceFromTarget = Math.sqrt(meltDifference*meltDifference + boilDifference*boilDifference);
			// Negated since the goal is a value of 0
			fitnesses.add(-differenceFromTarget);
		}
		
		double[] otherScores = new double[] {meltingPoint, boilingPoint};
		
		if(CommonConstants.watch) {
			System.out.println(individual + " has MP " + meltingPoint + " and BP " + boilingPoint + " and fitness " + fitnesses);
		}
		
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}
	
	public void finalCleanup() {
		// Close the Fortran programs running in the background
		MoleculeMutatorProcess.terminateMutatorProcess();
		MoleculeMeltingAndBoilingPointProcess.terminateMelingBoilingPointProcess();
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main(("runNumber:1 randomSeed:1 watch:true trials:1 mu:1000 base:molecules log:Molecules-TargetMeltingBoiling "+
					 "saveTo:TargetMeltingBoiling maxGens:50 io:true netio:true mating:false "+
				 	 "task:edu.southwestern.tasks.molecules.MoleculeTask cleanFrequency:-1 saveAllChampions:true "+
					 "genotype:edu.southwestern.evolution.genotypes.SMILESStringGenotype "+
				 	 "smilesTargetMeltingPoint:179.44000148773193 smilesTargetBoilingPoint:379.65999603271484 "+
					 "moleculeTargetMeltingAndBoilingPointFitness:true").split(" "));
	}
}
