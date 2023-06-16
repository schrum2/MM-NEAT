package edu.southwestern.evolution.mome;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;

// TODO: Put your header comment here too
public class MOMEArchive<T> {

	
	ConcurrentHashMap<Vector<Integer>,Vector<Score<T>>> archive;
	//vector of integers represents coordinates for bins of arbitrary dimension
	//vector of scores are the scores of candidates in that bin
	

	//private int occupiedBins; 
	private BinLabels mapping;
	private boolean saveElites;	//would like to know what this is exactly
	private String archiveDir;
	
	public static final int MAX_SUB_POP_ALLOWED = 255; //this is the maximum number of individuals that can occupy a bin
		//could create a parameter to control it?
	
//	public int getOccupiedBins() {
//		return occupiedBins;
//	}
//
//	public void setOccupiedBins(int occupiedBins) {
//		this.occupiedBins = occupiedBins;
//	}

	public BinLabels getBinMapping() {
		return mapping;
	}

	public boolean isSaveElites() {
		return saveElites;
	}

	public void setSaveElites(boolean saveElites) {
		this.saveElites = saveElites;
	}

	public String getArchiveDir() {
		return archiveDir;
	}

	public void setArchiveDir(String archiveDir) {
		this.archiveDir = archiveDir;
	}
	
	
	/**
	 * TODO: explain more
	 * constructor
	 * @param saveElites
	 * @param archiveDirectoryName
	 * @param maximumNumberOfIndividualsInSubPops the max size of the population in each cell, currently unrestricted
	 */
	public MOMEArchive(boolean saveElites, String archiveDirectoryName, int maximumNumberOfIndividualsInSubPops) {
		this.saveElites = saveElites;
		// Initialize mapping
		try {
			mapping = (BinLabels) ClassCreation.createObject("mapElitesBinLabels"); // TODO: Change to what it was before: will simply use the MAP Elites labels parameter
		} catch (NoSuchMethodException e) {
			System.out.println("Failed to get Bin Mapping for MOME!");
			e.printStackTrace();
			System.exit(1);
		}
		
		//establish number of bins and initialize with none occupied
		int numBins = mapping.binLabels().size();
		System.out.println("Archive contains "+numBins+" number of bins");
		archive = new ConcurrentHashMap<Vector<Integer>,Vector<Score<T>>>(numBins);
//		occupiedBins = 0;
		
		//set subPopulationMaximum size
		//maxSubPopAllowed = Parameters.parameters.integerParameter("minecraftMaxSubPopAllowedInBins");
		
		// Archive directory
		String experimentDir = FileUtilities.getSaveDirectory();
		archiveDir = experimentDir + File.separator + archiveDirectoryName;
		if(saveElites) {
			new File(archiveDir).mkdirs(); // make directory
		}
	}
	
	/**
	 * copy constructor
	 * takes another archive, reevaluates shapes and adds them to the new archive
	 * this might end up different from the original
	 * @param other the archive you want to copy
	 */
	public MOMEArchive(MOMEArchive<T> other) {
		this(other.archive, other.mapping, other.archiveDir, other.saveElites);
	}
	/**
	 * takes the specifics of the above constructor
	 * @param otherArchiveHashMap	this is the original archive being copied from
	 * @param otherMapping	this is the original archive's BinLabels mapping being copied
	 * @param otherArchiveDirectory	the directory of the original archive that the new archive will save to
	 * @param otherSaveElites	the boolean for the original archives saveElites variable
	 */
	public MOMEArchive(ConcurrentHashMap<Vector<Integer>, Vector<Score<T>>> otherArchiveHashMap, BinLabels otherMapping, String otherArchiveDirectory, boolean otherSaveElites) {
		saveElites = false;	//don't save while reorganizing
		mapping = otherMapping;
		int numBins = otherMapping.binLabels().size();	
		archive = new ConcurrentHashMap<Vector<Integer>, Vector<Score<T>>>(numBins);
//		occupiedBins = 0;
		setArchiveDir(otherArchiveDirectory);	//will save in the same place

		//go through the original archive and add
		otherArchiveHashMap.forEach( (coords, subpop) -> {
			
			for(Score<T> s : subpop) {
			
				@SuppressWarnings("unchecked")
				Score<T> newScore = ((LonerTask<T>) MMNEAT.task).evaluate(s.individual);
				this.add(newScore);
			}
			
		});
		saveElites = otherSaveElites;
	}
	
	/**
	 * This adds an individual to the archive, recalculated the pareto front, and returns whether that candidate stayed in the archive
	 * get new individual, figure out index array of primitive int to vector, 
	 * get correct subpop from archive via vector lookup, take new individual and add to subpop 
	 * then recalculate pareito front
	 * returns bool about if something changed
	 * return true if new candidate added in some way
	 * false if not added
	 * @param candidate	the new shape to add to the population of a bin in the archive
	 * @return true if the candidate is in the archive after pareto front calculation and false if the candidate is not added
	 */
	public boolean add(Score<T> candidate) {
		if(candidate.usesTraditionalBehaviorVector()) {
			//unsupported case error
			throw new UnsupportedOperationException("This case is not supported");
		} else if(candidate.usesMAPElitesMapSpecification() && !getBinMapping().discard(candidate.MAPElitesBehaviorMap())) {
			//not discarded

			//returns an integer array of coordinates for the candidates bin
			int[] binIndex = getBinMapping().multiDimensionalIndices(candidate.MAPElitesBehaviorMap());	//grabs bin coordinates int array
			Vector<Integer> candidateBinCoordinates = new Vector<Integer>();
			for (int i = 0; i < binIndex.length; i++) {		//turns int array into vector
				candidateBinCoordinates.add(binIndex[i]);
			}
			// If the bin has never been filled before, then initialize as empty vector
			if(!archive.containsKey(candidateBinCoordinates)) {
				archive.put(candidateBinCoordinates, new Vector<Score<T>>());
			}
			//add the candidate (Score) to the vector of scores for that bin
			archive.get(candidateBinCoordinates).add(candidate);	
			
			// Recalculate Pareto front
			ArrayList<NSGA2Score<T>> front = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(archive.get(candidateBinCoordinates)));
			//check if the candidate it there and return if it is
			long candidateID = candidate.individual.getId();
			for (NSGA2Score<T> score : front) {
				if(score.individual.getId() == candidateID) {
					// Since the new individual is present, the Pareto front must have changed.
					// The Map needs to be updated, and we return true to indicate the change.
					archive.replace(candidateBinCoordinates, new Vector<>(front));
					return true;
				}
			}
			return false;
		} else {
			// In some domains, a flawed genotype can emerge which cannot produce a behavior vector. Obviously cannot be added to archive.
			return false; // nothing added
		}
	}
	
	/**
	 * from the archive it retrieves a random individual
	 * randomly picks a bin, then randomly picks an individual's score from the bin
	 * @return random individual from archive (Score<T>)
	 */
	public Score<T> getRandomIndividaul(){
		//grab a random individual from a random bin
		return RandomNumbers.randomElement(getRandomPopulation());
	}
	/**
	 * get's a random sub population from a random bin in the archive
	 * @return the identifiers of all the individuals in a random sub population in the archive
	 */
	public Vector<Score<T>> getRandomPopulation(){
		//grab a random bin
		return RandomNumbers.randomElement(archive.values());
	}
	
	//unsure if I even need this but made a stub
	/**
	 * this will return a Vector of all the Scores in the archive / basically the identifier of all individuals in the archive
	 * @return vector containing the Score of all individuals in the archive
	 */
	public Vector<Score<T>> getWholeArchiveScores(){
		//needed?
		Vector<Score<T>> vectorOfAllTheScores = new Vector<Score<T>>(archive.values().size());	//this is the result vector
		Collection<Vector<Score<T>>> allScores = archive.values();	//this returns a collection of all the scores/values in the archive
		for(Vector<Score<T>> score : allScores) {	//this loops through all the vectors of scores in the collection
			//loops through all the  vectors of scores
			vectorOfAllTheScores.addAll(score);		//this adds all the vectors from score to the result vector
		}
		return vectorOfAllTheScores;
	}
	
	/**
	 * this function returns the total number of individuals currently in the archive
	 * @return total number of individuals in the archive
	 */
	public int totalNumberOfIndividualsInArchive() {
		//System.out.println("total number of individuals in archive:" + archive.values().size());
		return archive.values().size();
	}
	
	/**
	 * returns all the scores for a specific bin
	 * @param keyBinCoordinates	the key which is the coordinates of the bin
	 * @return the scores for that bin
	 */
	public Vector<Score<T>> getScoresForBin(Vector<Integer> keyBinCoordinates){
		return archive.get(keyBinCoordinates);
	}
	
	/**
	 * return the total number of bins in use in the archive
	 * @return
	 */
	public int getNumberOfOccupiedBins() {
		return archive.size();
	}
	
	//TODO: the below methods would be useful it seems
	//what is objective? Like, what am I using to differentiate that?
	//Max fitness in each objective
	public float maxFitnessInEachObjective() {
		float maxFitness = 0;
		//for each bin
			//for each objective
				//check the max & compare
		return maxFitness;
	}
	
	//This should probably be passed maybe something else?
	//Min fitness in each objective across all scores in the archive
	public float[] minFitnessInEachObjective(int objectives) {
		float[] listOfMinFitnessForEachObjective = new float[objectives];
		Collection<Vector<Score<T>>> allVectorsOfScores = archive.values();	//this returns a collection of all the scores/values in the archive

		//for all scores
			//for all objectives
				//add to min fitness for it
		return listOfMinFitnessForEachObjective;
	}
	
	//Max sub pop size across all bins
	public int maxSubPopulationSizeInWholeArchive() {
		//System.out.println("maxSubPop");
		int maxSubPop = 0;
		Collection<Vector<Score<T>>> allVectorsOfScores = archive.values();	//this returns a collection of all the scores/values in the archive
		for(Vector<Score<T>> scoreVector : allVectorsOfScores) {	//for each bin
			if(scoreVector.size() > maxSubPop) {					//check population size
				maxSubPop = scoreVector.size();	
			}	
		}
		//System.out.println("maxSubPop:"+maxSubPop);
		return maxSubPop;
	}
	
	//Min sub pop size of all occupied bins in the archive
	public int minSubPopulationSizeInWholeArchive() {
		//System.out.println("minSubPop");
		int minSubPop = MAX_SUB_POP_ALLOWED;
		Collection<Vector<Score<T>>> allVectorsOfScores = archive.values();	//this returns a collection of all the scores/values in the archive
		for(Vector<Score<T>> scoreVector : allVectorsOfScores) {	//for each bin
			if((scoreVector.size() < minSubPop) && (scoreVector.size() != 0)) {					//check population size
				minSubPop = scoreVector.size();	
			}	
		}
		//System.out.println("minSubPop:"+minSubPop);
		return minSubPop;
	}
	
	//do not know what this is
	//Max hyper volume in one bin
	public int maxHyperVolumeInBin(Vector<Integer> keyBinCoordinates) {
		return 0;
	}
	
	//do not know what this is
	//hypervolume across all bins
	public int[] hyperVolumeOfAllBins() {
		int[] placeHolder = {0,0};
		return placeHolder;
	}
	
	//not sure I understand what this is
	//oh! is it an int?
	//ArrayList<NSGA2Score<T>> sizeOfCombinedParetoFrontAcrossAllBins() {
	public int sizeOfCombinedParetoFrontAcrossAllBins() {
		//create a result pareto front
		Vector<Score<T>> wholeArchiveFront = new Vector<Score<T>>(getWholeArchiveScores());
		ArrayList<NSGA2Score<T>> front = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(wholeArchiveFront));
		//System.out.println("front:"+front+"\n whole archive:" + archive);
		System.out.println("front size:"+front.size()+"\t whole archive size:" + archive.values().size());
		return front.size();
		//return front;
	}
	
	
	/**
	 * turns a vector of Scores into a float array. Unsure if actually needed
	 * @param scoresList the original scores
	 * @return	a float array containing the score values
	 */
	// TODO: We may not need this, but if we do, it will be for logging purposes
	public float[] turnVectorScoresIntoFloatArray(Vector<Score<T>> scoresList) {
		float[] result = new float[scoresList.size()];
		for(int i = 0; i < result.length; i++) {
			Score<T> score = scoresList.get(i);
			result[i] = score == null ? Float.NEGATIVE_INFINITY : new Double(score.behaviorIndexScore(i)).floatValue();
		}
		System.out.println("vector into FloatArray:"+result);

		return result;
	}
	
	//main for testing
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
//		int runNum = 50; 
//		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:nsga2test log:NSG2Test-Test saveTo:Test trackPseudoArchive:true netio:true lambda:37 maxGens:200 task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
//		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" mapElitesQDBaseOffset:525 io:true base:nsga2test log:NSG2Test-MAPElites saveTo:MAPElites netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:50 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
//		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" mapElitesQDBaseOffset:525 base:nsga2test log:NSG2Test-CMAES saveTo:CMAES trackPseudoArchive:true netio:true mu:37 lambda:37 maxGens:200 ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
//		 
	}

}
