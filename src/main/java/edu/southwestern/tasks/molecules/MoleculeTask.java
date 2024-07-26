package edu.southwestern.tasks.molecules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.molecules.smiles.SMILESUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class MoleculeTask extends NoisyLonerTask<String> {

	private int numFitnessFunctions;
	private static final int NUM_OTHER_SCORES = 3;
	private static final double MIN_CELSIUS = -273.15;
	private static final double WORST_TARGET_FITNESS = -1500;
	
	private double bestFitnessEver;
	
	public MoleculeTask() {
		numFitnessFunctions = 0;
		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			MMNEAT.registerFitnessFunction("moleculeTargetMeltingAndBoilingPointFitness");
			numFitnessFunctions++;
		}
		
		if(numFitnessFunctions == 1) { 
			bestFitnessEver = WORST_TARGET_FITNESS;
		} else {
			throw new UnsupportedOperationException("The molecule task is not set up to handle multiobjective evolution yet, and should have exactly one fitness function.");
		}
		
		MMNEAT.registerFitnessFunction("moleculeTargetMeltingAndBoilingPointFitness",null,false,0,WORST_TARGET_FITNESS);
		MMNEAT.registerFitnessFunction("meltingPoint",null,false,0,MIN_CELSIUS);
		MMNEAT.registerFitnessFunction("boilingPoint",null,false,0,MIN_CELSIUS);
	}
	
	@Override
	public void postConstructionInitialization() {
		// Nothing
	}
	
	public double[] minScores() {
		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			return new double[] {WORST_TARGET_FITNESS};
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
	public Pair<double[], double[]> oneEval(Genotype<String> individual, int num, HashMap<String,Object> behaviorCharacteristics) {
		
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		
		// Get melting point and boiling point first
		Pair<Double, Double> pair = MoleculeMeltingAndBoilingPointProcess.smilesMeltingAndBoilingPoints((SMILESStringGenotype) individual);
		//System.out.println(pair);
		double meltingPoint = pair.t1;
		double boilingPoint = pair.t2;
		
		double targetFitness;
		if(meltingPoint == MoleculeMeltingAndBoilingPointProcess.BAD_RESULT && boilingPoint == MoleculeMeltingAndBoilingPointProcess.BAD_RESULT) {
			targetFitness = WORST_TARGET_FITNESS;
		} else {
			double targetMeltingPoint = Parameters.parameters.doubleParameter("smilesTargetMeltingPoint");
			double targetBoilingPoint = Parameters.parameters.doubleParameter("smilesTargetBoilingPoint");

			double meltDifference = targetMeltingPoint - meltingPoint;
			double boilDifference = targetBoilingPoint - boilingPoint;
			double differenceFromTarget = Math.sqrt(meltDifference*meltDifference + boilDifference*boilDifference);
			// Negated since the goal is a value of 0
			targetFitness = -differenceFromTarget;
		}

		if(Parameters.parameters.booleanParameter("moleculeTargetMeltingAndBoilingPointFitness")) {
			fitnesses.add(targetFitness);
		} else {
			throw new IllegalStateException("There needs to be some kind of fitness function");
		}

		double[] otherScores = new double[] {targetFitness, meltingPoint, boilingPoint};
		
		if(MMNEAT.usingDiversityBinningScheme) {
			behaviorCharacteristics.put("MeltingPoint", meltingPoint);
			behaviorCharacteristics.put("BoilingPoint", boilingPoint);
			
			String smilesString = individual.getPhenotype();
			behaviorCharacteristics.put("Carbon Count", SMILESUtil.carbonCount(smilesString));
			behaviorCharacteristics.put("Oxygen Count", SMILESUtil.oxygenCount(smilesString));
			behaviorCharacteristics.put("Nitrogen Count", SMILESUtil.nitrogenCount(smilesString));
			
			behaviorCharacteristics.put("Single Bond Count", SMILESUtil.singleBondCount(smilesString));
			behaviorCharacteristics.put("Double Bond Count", SMILESUtil.doubleBondCount(smilesString));
			behaviorCharacteristics.put("Triple Bond Count", SMILESUtil.tripleBondCount(smilesString));
			
			// Assume there is just one fitness score at index 0
			behaviorCharacteristics.put("binScore", fitnesses.get(0));
			
			
			if(CommonConstants.netio) {
				// Assumes we are using target fitness to evolve to a goal of 0.0
				if(fitnesses.get(0) > bestFitnessEver) {
					@SuppressWarnings("unchecked")
					Archive<String> archive = MMNEAT.getArchive();
					BinLabels binLabels = MMNEAT.getArchiveBinLabelsClass();
					int dim1D = binLabels.oneDimensionalIndex(behaviorCharacteristics);
					String label = binLabels.binLabels().get(dim1D);
					String filename = "CHAMPION-F"+fitnesses.get(0)+"-"+label+".txt";
					String fullPath = archive.getArchiveDirectory() + File.separator + filename;
					try {
						PrintStream ps = new PrintStream(new File(fullPath));
						ps.println(smilesString);
						ps.close();
					} catch (FileNotFoundException e) {
						System.err.println("Problem saving "+fullPath);
						e.printStackTrace();
					}
				}
			}
		}
		
		if(fitnesses.get(0) > bestFitnessEver) {
			bestFitnessEver = fitnesses.get(0);
		}
				
		if(CommonConstants.watch) {
			System.out.println(individual + " has MP " + meltingPoint + " and BP " + boilingPoint + " and fitness " + fitnesses + ":" + behaviorCharacteristics);
		}
		
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}
	
	public void finalCleanup() {
		// Close the Fortran programs running in the background
		MoleculeMutatorProcess.terminateMutatorProcess();
		MoleculeMeltingAndBoilingPointProcess.terminateMelingBoilingPointProcess();
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Elitist EA
//		MMNEAT.main(("runNumber:1 randomSeed:1 watch:true trials:1 mu:1000 base:molecules log:Molecules-TargetMeltingBoiling "+
//					 "saveTo:TargetMeltingBoiling maxGens:50 io:true netio:true mating:false "+
//				 	 "task:edu.southwestern.tasks.molecules.MoleculeTask cleanFrequency:-1 saveAllChampions:true "+
//					 "genotype:edu.southwestern.evolution.genotypes.SMILESStringGenotype "+
//				 	 "smilesTargetMeltingPoint:179.44000148773193 smilesTargetBoilingPoint:379.65999603271484 "+
//					 "moleculeTargetMeltingAndBoilingPointFitness:true").split(" "));
		
		// MAP Elites
		MMNEAT.main(("runNumber:1 randomSeed:1 watch:false trials:1 mu:10 base:molecules log:Molecules-TargetTypeCounts "+
				 "saveTo:TargetTypeCounts maxGens:50000 io:true netio:true mating:false "+
			 	 "task:edu.southwestern.tasks.molecules.MoleculeTask cleanFrequency:-1 saveAllChampions:true "+
				 "genotype:edu.southwestern.evolution.genotypes.SMILESStringGenotype "+
			 	 // MAP Elites settings added here
				 "ea:edu.southwestern.evolution.mapelites.MAPElites "+
				 "experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment "+
				 "steadyStateIndividualsPerGeneration:100 "+
				 "mapElitesBinLabels:edu.southwestern.tasks.molecules.smiles.MoleculeAtomTypeCountsBinLabels "+
				 "steadyStateArchetypeSaving:false "+
			 	 // Fitness related
			 	 "smilesTargetMeltingPoint:179.44000148773193 smilesTargetBoilingPoint:379.65999603271484 "+
				 "moleculeTargetMeltingAndBoilingPointFitness:true").split(" "));

	}
}
