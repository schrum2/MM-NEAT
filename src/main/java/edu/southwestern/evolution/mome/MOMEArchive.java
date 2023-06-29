package edu.southwestern.evolution.mome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cern.colt.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MultiobjectiveUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Archive class for MOME. This creates an archive that works with MOME. 
 * The archive is a ConcurrentHashMap of the bin coordinates paired with the individuals in that bin.
 * 
 * 
 * @author lewisj
 *
 * @param <T>
 */
public class MOMEArchive<T> {

	
	ConcurrentHashMap<Vector<Integer>,Vector<Score<T>>> archive;
	//vector of integers represents coordinates for bins of arbitrary dimension
	//vector of scores are the scores of candidates in that bin
	

	//private int occupiedBins; 
	private BinLabels mapping;
	private boolean saveElites;	//would like to know what this is exactly
	private String archiveDir;
	private int maximumNumberOfIndividualsInSubPops;	//MOME allows for more than one individual to be in a bin, 
														//this controls the maximum number of individuals in the bin.

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
	public int maxSubpopSizeAllowed() {
		return maximumNumberOfIndividualsInSubPops;
	}
	
	
	/**
	 * TODO: explain more
	 * constructor that only takes the boolean of whether to 
	 * saveElites, what the archive directory is, and the maximum number of individuals
	 * may not need to pass the max number of individuals if its just a parameter
	 * @param saveElites	
	 * @param archiveDirectoryName
	 */
	public MOMEArchive(boolean saveElites, String archiveDirectoryName) {
		this.saveElites = saveElites;
		// Initialize mapping
		try {
			mapping = (BinLabels) ClassCreation.createObject("mapElitesBinLabels");
		} catch (NoSuchMethodException e) {
			System.out.println("Failed to get Bin Mapping for MOME!");
			e.printStackTrace();
			System.exit(1);
		}
		
		//establish number of bins and initialize with none occupied
		int numberOfBinLabels = mapping.binLabels().size();
		System.out.println("Archive contains "+numberOfBinLabels+" number of bins");
		archive = new ConcurrentHashMap<Vector<Integer>,Vector<Score<T>>>(numberOfBinLabels);
//		occupiedBins = 0;
		maximumNumberOfIndividualsInSubPops = Parameters.parameters.integerParameter("maximumMOMESubPopulationSize");
		System.out.println("max number of individuals in subpop set to: "+ maximumNumberOfIndividualsInSubPops + " parameter: "+ Parameters.parameters.integerParameter("maximumMOMESubPopulationSize"));
	
		// Archive directory
		String experimentDir = FileUtilities.getSaveDirectory();
		archiveDir = experimentDir + File.separator + archiveDirectoryName;
		System.out.println("MOME ARCHIVE archiveDir: " + archiveDir);		//TODO: delete later
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
				System.out.println("New bin uncovered: "+candidateBinCoordinates);
				archive.put(candidateBinCoordinates, new Vector<Score<T>>());
			}
			//add the candidate (Score) to the vector of scores for that bin
			Vector<Score<T>> subpopInBin = archive.get(candidateBinCoordinates);
			synchronized(subpopInBin) {
				//System.out.println("Synchrnoized on "+subpopInBin.hashCode());
				subpopInBin.add(candidate);	
				// Recalculate Pareto front
				ArrayList<NSGA2Score<T>> front = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(subpopInBin));
				assert allNondominated(front) : "How can there be dominated points in the Pareto front?\n"+front;
				//check if the candidate it there and return if it is
				long candidateID = candidate.individual.getId();
				for (NSGA2Score<T> score : front) {

					if(score.individual.getId() == candidateID) {
						
						// Since the new individual is present, the Pareto front must have changed.
						// Check that the front contains the correct max number of individuals and remove if not
						if((front.size() > maximumNumberOfIndividualsInSubPops) && (maximumNumberOfIndividualsInSubPops > 0)) {	//check the subpop size
//							System.out.println("max subpop set to: "+ maximumNumberOfIndividualsInSubPops + " currently: "+ front.size());
							int sizeBefore = front.size();
							front = discardRandomIndividualFromFront(candidate, front);
//							System.out.println(" after discard: "+ front.size());
							assert front.size() == sizeBefore - 1 : "Should have removed one individual: "+front;

							assert (front.size() <= maximumNumberOfIndividualsInSubPops) : "subpop size exceeds max size that is allowed. front:" + front.size();
							assert (!front.contains(candidate)) : "deleted candidate instead of random individual";
//							System.out.println("archive bin size: "+ archive.get(candidateBinCoordinates).size() + " front: "+ front.size());
						}
						assert maximumNumberOfIndividualsInSubPops >= front.size() : "after if statement in add, front larger than max, front:" + front.size();
						//update map
						Vector<Score<T>> newBinContents = new Vector<>(front);
						synchronized(newBinContents) {
							System.out.println("Synchrnoized on "+newBinContents.hashCode());
							archive.replace(candidateBinCoordinates, newBinContents);
							System.out.println("bin size after replacement with front:" + archive.get(candidateBinCoordinates).size());
							assert (archive.get(candidateBinCoordinates).size() <= maximumNumberOfIndividualsInSubPops) : "the number of individuals in this subpop exceed the maximum number that is allowed after replacing with front";
							assert (archive.get(candidateBinCoordinates).size() == front.size()) : archive.get(candidateBinCoordinates).size()+" = subpop size != front size = "+front.size() + ",\nfront="+front+"\nbin="+archive.get(candidateBinCoordinates);

							// I think the code from this assert was freezing the execution somehow
							//assert checkLargestSubpopNotGreaterThanMaxLimit() : "after adding and going through other asserts the largest subpop is greater than the limit"
							// + " largest subpop:" + maxSubPopulationSizeInWholeArchive() + " size of current subpop:" + archive.get(candidateBinCoordinates).size();
							//conditionalEliteSave(candidate, candidateBinCoordinates);	//this saves a condidate, but currently saves all created individuals which is too many
							System.out.println("Unlock "+newBinContents.hashCode());
						}
						//System.out.println("Unlock "+subpopInBin.hashCode() + " ADD");
						return true;	//candidate was added
					}
				}
				//System.out.println("Unlock "+subpopInBin.hashCode() + " NO ADD");
				return false;	//candidate wasn't added
			}
		} else {
			// In some domains, a flawed genotype can emerge which cannot produce a behavior vector. Obviously cannot be added to archive.
			return false; // nothing added
		}
	}
	
	// Should only be called from assertion when all of the points really are non-dominated
	private boolean allNondominated(ArrayList<NSGA2Score<T>> front) {
		for(int i = 0; i < front.size(); i++) {
			for(int j = 0; j < front.size(); j++) {
				if(i != j) {
					NSGA2Score<T> scoreI = front.get(i);
					NSGA2Score<T> scoreJ = front.get(j);
					
					assert !scoreI.isBetter(scoreJ) : "How is " + scoreI + " better than " + scoreJ + "?";
					assert !scoreJ.isBetter(scoreI) : "How is " + scoreJ + " better than " + scoreI + "?";
					
					double[] iNums = scoreI.scores;
					double[] jNums = scoreJ.scores;
					
					assert iNums.length == jNums.length;
					
					boolean iBetterOnce = false;
					boolean jBetterOnce = false;
					int numEqual = 0;
					
					for(int k = 0; k < iNums.length; k++) {
						if(iNums[k] > jNums[k]) iBetterOnce = true;
						if(jNums[k] > iNums[k]) jBetterOnce = true;
						if(jNums[k] == iNums[k]) numEqual++;
					}
					
					assert (iBetterOnce && jBetterOnce) || numEqual == iNums.length: "Not right! iNums = "+Arrays.toString(iNums) + ", jNums = " + Arrays.toString(jNums);
					
					// Will never reach with assertions
					if(!((iBetterOnce && jBetterOnce) || numEqual == iNums.length) ) return false;
				}
			}
		}
		
		return true;
	}
	/**
	 * This takes a front and discards a random individual that is not the one just added to the front
	 * @param individualToKeep the individual you can not discard
	 * @param front the front that you are modifying
	 * @return a front with the new population (the original front minus one individual that was not the newest one added)
	 */
	public ArrayList<NSGA2Score<T>> discardRandomIndividualFromFront(Score<T> individualToKeep, ArrayList<NSGA2Score<T>> front) {
		//discard individual that isn't the one that is set there
		int attempts = 0;
		Score<T> individualToDiscard;
		do {
			individualToDiscard = RandomNumbers.randomElement(front);
			System.out.println("Random discard attempt: " + (++attempts));
		} while (individualToKeep.individual.getId() == individualToDiscard.individual.getId());
		
		assert (individualToDiscard != individualToKeep) : "to discard and to keep individual scores are the same";
		assert (individualToDiscard.individual.getId() != individualToKeep.individual.getId()) : "to discard and to keep individual scores are the same";
		
		boolean result = front.remove(individualToDiscard);
		assert result : "Removal not successful: " + individualToDiscard + "\nfrom: "+front;
		if (!result) {
			System.out.println("discard failed");
		}
		assert (!front.contains(individualToDiscard)) : "individual to discard still in front";
		assert (front.size() <= maximumNumberOfIndividualsInSubPops) : "size of front exceeds max allowed in discard method after discard, front size:" + front.size();

		return front;
	}
	

	
//POPULATION RELATED METHODS
	/**
	 * from the archive it retrieves a random individual
	 * randomly picks a bin, then randomly picks an individual's score from the bin
	 * @return random individual from archive (Score<T>)
	 */
	public Score<T> getRandomIndividual(){
		//grab a random individual from a random bin
		if(!Parameters.parameters.booleanParameter("momeSelectsUniformlyAcrossWholeArchive")) {
			return RandomNumbers.randomElement(getRandomPopulation());
		} else {
			Vector<Score<T>> allIndividuals = getWholeArchiveScores();
			return RandomNumbers.randomElement(allIndividuals);
		}
	}

	/**
	 * from the archive it retrieves a random individual from a given bin
	 * @param binCoordinates Vector representing the multidimensional coordinates of a bin cell
	 * @return random individual from archive (Score<T>) in specified bin
	 */
	public Score<T> getRandomIndividual(Vector<Integer> binCoordinates){
		//grab a random individual from a specified bin
		return RandomNumbers.randomElement(getScoresForBin(binCoordinates));
	}
	
	/**
	 * get's a random sub population from a random bin in the archive
	 * @return the identifiers of all the individuals in a random sub population in the archive
	 */
	public Vector<Score<T>> getRandomPopulation(){
		//grab a random bin
		return RandomNumbers.randomElement(archive.values());
	}
	
	/**
	 * gets an ArrayList of the populations genotypes
	 */
	public ArrayList<Genotype<T>> getPopulation() {
		//System.out.println("in get population");
		 ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.size());

		 archive.forEach( (coords, subpop) -> {	////goes through the archive
			 for(Score<T> s : subpop) {		//goes through the scores of the subpop
				 result.add(s.individual);
			 }
		 });
		return result;
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
	 * gets the population size of every bin in the archive
	 * creates an array the size of all bin labels, initiated to 0, and adds the sizes of all occupied bins
	 * @return the size of the population for every bin (used and unused)
	 */
	public int[] populationSizeForEveryBin() {
		//System.out.println("popSizes: ");
		int[] populationSizes = new int[mapping.binLabels().size()];

		for (Vector<Integer> keyVector : archive.keySet()) {
			int oneDBinIndex = mapping.oneDimensionalIndex(ArrayUtil.intArrayFromArrayList(keyVector));
			populationSizes[oneDBinIndex] = archive.get(keyVector).size();
		}

		return populationSizes;
	}

	/**
	 * checks if the largest subpop is greater than the maximum allowed
	 * @return true if the max subpop size is less than the max allowed, false if it is more than the max allowed
	 */
	public boolean checkLargestSubpopNotGreaterThanMaxLimit() {
		if (maximumNumberOfIndividualsInSubPops == -1) 
			return true; // No restrictions here
		else if (maxSubPopulationSizeInWholeArchive() > maximumNumberOfIndividualsInSubPops) 
			return false;
		else
			return true;
	}
	/**
	 * checks if a gives list is greater than the max subpop allowed
	 * @param listOfPopSizes a list of sizes to check
	 * @return true if it is <= max subpop allowed, false if greater than max subpop allowed
	 */
	public boolean checkIfNotGreaterThanMaxSubpop(int[] listOfPopSizes) {
		for (int i = 0; i < listOfPopSizes.length; i++) {
			if (listOfPopSizes[i] > maximumNumberOfIndividualsInSubPops) {
				return false;
			}
		}
		return true;
	}

	/**
	 * this gets the largest population for a single bin from the whole archive
	 * @return the number of the most individuals present in one bin
	 */
	public int maxSubPopulationSizeInWholeArchive() {
		//System.out.println("maxSubPop");
		int maxSubPop = 0;
		Collection<Vector<Score<T>>> allVectorsOfScores = archive.values();	//this returns a collection of all the scores/values in the archive
		for(Vector<Score<T>> scoreVector : allVectorsOfScores) {	//for each bin
			if(scoreVector.size() > maxSubPop) {					//check population size
				assert !(maxSubPop > maximumNumberOfIndividualsInSubPops && maximumNumberOfIndividualsInSubPops > -1) : "Population too big: "+scoreVector.size() + ":" + scoreVector;
				maxSubPop = scoreVector.size();	
			}	
		}
		//System.out.println("maxSubPop:"+maxSubPop);
		return maxSubPop;
	}

	public String archiveDebug() {
		String result = "";
		for(Entry<Vector<Integer>, Vector<Score<T>>> pair : archive.entrySet()) {
			result += ""+pair.getKey() + "->" + pair.getValue().size() + ":" + pair.getValue() + "\n";
		}
		return result;
	}
	
	//Min sub pop size of all occupied bins in the archive
	/**
	 * this returns the least number of individuals in a subpopulation from the whole archive
	 * this does not include empty bins
	 * @return the least number of individuals in a single bin from all the bin in the archive
	 */
	public int minSubPopulationSizeInWholeArchive() {
		//System.out.println("minSubPop");
		int minSubPop = Integer.MAX_VALUE;
		Collection<Vector<Score<T>>> allVectorsOfScores = archive.values();	//this returns a collection of all the scores/values in the archive
		for(Vector<Score<T>> scoreVector : allVectorsOfScores) {	//for each bin
			assert (scoreVector.size() != 0) : "Can't have occupied bin with no occupants";
			if(scoreVector.size() < minSubPop) {					//check population size
				minSubPop = scoreVector.size();	
			}	
		}
		//System.out.println("minSubPop:"+minSubPop);
		return minSubPop;
	}

	/**
	 * return the total number of bins in use in the archive
	 * @return the total number of bins that currently have at least one individual
	 */
	public int getNumberOfOccupiedBins() {
		return archive.size();
	}
	
//SCORE RELATED METHODS
	/**
	 * this returns a Vector of all the Scores in the archive 
	 * @return vector containing the Score of all individuals in the archive
	 */
	public Vector<Score<T>> getWholeArchiveScores(){
		Vector<Score<T>> vectorOfAllTheScores = new Vector<Score<T>>(archive.values().size());	//this is the result vector
		Collection<Vector<Score<T>>> allScores = archive.values();	//this returns a collection of all the scores/values in the archive
		for(Vector<Score<T>> score : allScores) {	//this loops through all the vectors of scores in the collection
			vectorOfAllTheScores.addAll(score);		//this adds all the vectors from score to the result vector
		}
		return vectorOfAllTheScores;
	}
	
	/**
	 * returns all the scores for a specific bin
	 * @param keyBinCoordinates	the key which is the coordinates of the bin
	 * @return the scores for that bin
	 */
	public Vector<Score<T>> getScoresForBin(Vector<Integer> keyBinCoordinates){
		return archive.get(keyBinCoordinates);
	}

//MAX FITNESS FUNCTIONS BELOW
	/**
	 * For the whole archive, get the max scores in each objective
	 * within all the scores in the archive this finds the max for each objective from that pool
	 * takes whole archive of scores
	 * returns max over all scores
	 * MIN MIRROR: minFitnessInWholeArchiveXObjective
	 * @return max fitnesss in each objective for the whole archive
	 */
	public double[] maxFitnessInWholeArchiveXObjective() {
		double[] maxFitnessScores = ArrayUtil.doubleSpecified(MMNEAT.task.numObjectives(), Double.NEGATIVE_INFINITY);
		Vector<Score<T>> allScores = getWholeArchiveScores();

		for(Score<T> member : allScores) {	//for all the scores in the whole archive
			for (int iObjective = 0; iObjective < maxFitnessScores.length; iObjective++) {	
				maxFitnessScores[iObjective] = Math.max(maxFitnessScores[iObjective], member.scores[iObjective]);	//max for i objective compared with member i objective
			}
		}
		return maxFitnessScores;
	}
	
	/**
	 * For a given archive bin, get the max scores in each objective
	 * MIN MIRROR: minFitnessInSingleBinXObjectives
	 * @param keyBinCoordinates vector of multidimensional bin coordinates
	 * @return max fitnesss in each objective within bin sub-pop
	 */
	public double[] maxFitnessInSingleBinXObjectives(Vector<Integer> keyBinCoordinates) {
		double[] maxFitnessScores = ArrayUtil.doubleSpecified(MMNEAT.task.numObjectives(), Double.NEGATIVE_INFINITY);
		Vector<Score<T>> subPop = archive.get(keyBinCoordinates);
		// get the sub-pop
		for(Score<T> member : subPop) {
			for (int i = 0; i < maxFitnessScores.length; i++) {
				maxFitnessScores[i] = Math.max(maxFitnessScores[i], member.scores[i]);
			}
		}
		return maxFitnessScores;
	}
	
	/**
	 * finds the max score for all bins in all objectives, sorted by bins with an array of objective scores
	 * MIN MIRROR: minScorebyBinXObjective
	 * @return maxScores[bin][objective] for the whole archive
	 */
	public double[][] maxScorebyBinXObjective() {		
		//System.out.println("in max score");

		//initialize the result variable
		double[][] maxScoresByBinXObjective = new double[mapping.binLabels().size()][];
		for(int i = 0; i < maxScoresByBinXObjective.length; i++) {
			// The unoccupied bins also need this initialization
			maxScoresByBinXObjective[i] = ArrayUtil.doubleSpecified(MMNEAT.task.numObjectives(), Double.NEGATIVE_INFINITY);
		}
		//go through bins to grab an array of max scores for each objective for that bin
		for(Vector<Integer> key : archive.keySet()) {
			int oneDBinIndex = mapping.oneDimensionalIndex(ArrayUtil.intArrayFromArrayList(key));
			maxScoresByBinXObjective[oneDBinIndex] = maxFitnessInSingleBinXObjectives(key);
		}
		return maxScoresByBinXObjective;
	}
	

//	MIN FITNESS FUNCTIONS BELOW

	/**
	 * Pools all the scores from the archive and creates an array of the min for each objective
	 * MAX MIRROR: maxFitnessInWholeArchiveXObjective
	 * @return the min fitness score for each objective across the whole archive
	 */
	public double[] minFitnessInWholeArchiveXObjective() {
		double[] minFitnessScores = ArrayUtil.doubleSpecified(MMNEAT.task.numObjectives(), Double.NEGATIVE_INFINITY);

		Vector<Score<T>> allScores = getWholeArchiveScores();	//all the scores in the archive in one vector

		for(Score<T> member : allScores) {	//for each score check if that objective's min fitness is the min overall
			for (int j = 0; j < minFitnessScores.length; j++) {
				minFitnessScores[j] = Math.min(minFitnessScores[j], member.scores[j]);
			}
		}
		return minFitnessScores;
	}
	
	/**
	 * gets all the min scores for each objective from the given bin
	 * MAX MIRROR: maxFitnessInSingleBinXObjectives
	 * @param keyBinCoordinates the coordinates identifying the bin to search
	 * @return the min fitness score for each objective from this bin
	 */
	public double[] minFitnessInSingleBinXObjectives(Vector<Integer> keyBinCoordinates) {
		double[] minFitnessScores = ArrayUtil.doubleSpecified(MMNEAT.task.numObjectives(), Double.POSITIVE_INFINITY);
		Vector<Score<T>> subPop = archive.get(keyBinCoordinates);
		// get the sub-pop
		for(Score<T> member : subPop) {
			for (int i = 0; i < minFitnessScores.length; i++) {
				minFitnessScores[i] = Math.min(minFitnessScores[i], member.scores[i]);
			}
		}
		return minFitnessScores;
	}

	/**
	 * finds the min score for all bins in all objectives, sorted by bins with an array of objective scores
	 * same as above but minimum scores
	 * MAX MIRROR: maxScorebyBinXObjective
	 * @return minScores[bin][objective] for the whole archive
	 */
	public double[][] minScorebyBinXObjective() {
		//initialize the result variable
		double[][] minScoresByBinXObjective = new double[mapping.binLabels().size()][];
		for(int i = 0; i < minScoresByBinXObjective.length; i++) {
			// The unoccupied bins also need this initialization
			minScoresByBinXObjective[i] = ArrayUtil.doubleSpecified(MMNEAT.task.numObjectives(), Double.NEGATIVE_INFINITY);
		}
		//go through bins to grab an array of min scores for each objective for that bin
		for(Vector<Integer> key : archive.keySet()) {
			int oneDBinIndex = mapping.oneDimensionalIndex(ArrayUtil.intArrayFromArrayList(key));
			minScoresByBinXObjective[oneDBinIndex] = minFitnessInSingleBinXObjectives(key);
		}
		return minScoresByBinXObjective;
	}
	
	/**
	 * Gets the hypervolume of the population of a given bin.
	 * @param keyBinCoordinates the coordinates of the bin, used to get the subpop of Scores
	 * @return the maxHypervolume of the given bin
	 */
	public double maxHyperVolumeInBin(Vector<Integer> keyBinCoordinates) {
		List<Score<T>> listOfScores = archive.get(keyBinCoordinates);
		return MultiobjectiveUtil.hypervolumeFromParetoFront(listOfScores);
	}
	
	/**
	 * Returns an array containing the hypervolume of every bin in the archive
	 * @return the hypervolume of every bin in the archive
	 */
	public double[] hyperVolumeOfAllBins() {
		double[] hyperVolumeOfAllBins = new double[mapping.binLabels().size()];

		for(Vector<Integer> key : archive.keySet()) {
			int oneDBinIndex = mapping.oneDimensionalIndex(ArrayUtil.intArrayFromArrayList(key));
			hyperVolumeOfAllBins[oneDBinIndex] = maxHyperVolumeInBin(key); //index that bin into the array using its oneDBinIndex
		}
		return hyperVolumeOfAllBins;
	}
	
	/**
	 * calculates the hypervolume of all bins added together
	 * @return the total hypervolume of all bin's hypervolumes added together
	 */
	public double totalHyperVolumeOfAllBinsCombined() {
		double[] hypervolumeOfAllBins = hyperVolumeOfAllBins();
		double totalHypervolume = 0;
		for (int i = 0; i < hypervolumeOfAllBins.length; i++) {
			totalHypervolume += hypervolumeOfAllBins[i];
		}
		return totalHypervolume;
	}

	/**
	 * Creates a pareto front using all the scores in the archive and then returns the size.
	 * @return size of a parento front made from all the scores in the archive
	 */
	public int sizeOfCombinedParetoFrontAcrossAllBins() {
		//create a result pareto front
		Vector<Score<T>> wholeArchiveFront = new Vector<Score<T>>(getWholeArchiveScores());
		ArrayList<NSGA2Score<T>> front = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(wholeArchiveFront));
		return front.size();
	}
	
	/**
	 * create pareto front for whole archive and convert to vector
	 * @return vector pareto front of whole arechive
	 */
	public Vector<Score<T>> getCombinedParetoFrontWholeArchive(){
		Vector<Score<T>> wholeArchiveFront = new Vector<Score<T>>(getWholeArchiveScores());
		ArrayList<NSGA2Score<T>> front = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(wholeArchiveFront));
		return new Vector<>(front);
	}

	/**
	 * saves individual shapes to the archive. Currently saves all added shapes, will create space issue
	 * TODO: figure out how to limit the number of saves and overwrite desired individuals
	 * @param candidate	the score of the individual being saved
	 * @param candidateBinCoordinates the bin identifier and key for finding score
	 */
	private void conditionalEliteSave(Score<T> candidate, Vector<Integer> candidateBinCoordinates) {	//int binIndex needed?
		if (saveElites) {
			int binIndex = getOneDBinIndex(candidateBinCoordinates);
			String binPath = getArchiveDir() + File.separator + mapping.binLabels().get(binIndex) + candidate.individual.getId();
			Serialization.save(candidate.individual, binPath + "-elite");
			// Write scores as simple text file (less to write than xml)
			try {
				PrintStream ps = new PrintStream(new File(binPath + "-scores.txt"));
				for(Double score : candidate.getTraditionalDomainSpecificBehaviorVector()) {
					ps.println(score);
				}
			} catch (FileNotFoundException e) {
				System.out.println("Could not write scores for " + candidate.individual.getId() + ":" + candidate.getTraditionalDomainSpecificBehaviorVector());
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
//	
//	/**
//	 * turns a vector of Scores into a float array. Unsure if actually needed
//	 * @param scoresList the original scores
//	 * @return	a float array containing the score values
//	 */
//	//this doesn't work due to behavior index score being mapelites bin index specific
//	public float[] turnVectorScoresIntoFloatArray(Vector<Score<T>> scoresList) {
//		System.out.println("in vector to float");
//		float[] result = new float[scoresList.size()];
//		for(int i = 0; i < result.length; i++) {
//			Score<T> score = scoresList.get(i);
//			System.out.println("score:"+score);
//			System.out.println(" what is this? " + score.behaviorIndexScore());
//
//
//			//result[i] = score == null ? Float.NEGATIVE_INFINITY : new Double(score.behaviorIndexScore(i)).floatValue();
//			result[i] = new Double(score.behaviorIndexScore(2)).floatValue();
//		}
//		System.out.println("vector into FloatArray:"+result);
//
//		return result;
//	}
//	
//	public int binLabelsSize() {
//		return mapping.binLabels().size();
//	}
	
	/**
	 * Gets the oneDBinIndex for a bin using a bin coordinates vector.
	 * @param keyVector the bin coordinates for the bin whose index you need
	 * @return an index for the bin that was given
	 */
	public int getOneDBinIndex(Vector<Integer> keyVector) {
		return mapping.oneDimensionalIndex(ArrayUtil.intArrayFromArrayList(keyVector));
	}
	
	/**
	 * The bin coordinates of the given individual score as an index.
	 * Uses a score (value) to find it's bin coordinates
	 * @param individualScore the score being used to look up the bin coordinates
	 * @return the bin coordinates of the given score
	 */
	public int[] getBinIndexCoordinates(Score<T> candidate) {
		return getBinMapping().multiDimensionalIndices(candidate.MAPElitesBehaviorMap());
	}
	
	/**
	 * returns the bin label for the bin of the given score
	 * @param score a given score
	 * @return the bin label for it's bin
	 */
	public String getIndividualBinLabel(Score<T> score) {
		BinLabels archiveBinLabelsClass = MMNEAT.getArchiveBinLabelsClass();
		return archiveBinLabelsClass.binLabels().get(archiveBinLabelsClass.oneDimensionalIndex(score.MAPElitesBehaviorMap()));
	}
	/**
	 * only works for occupied bins
	 * @param binCoordinates
	 * @return the bin label of the given bin coordinates
	 */
	public String getBinLabel(Vector<Integer> binCoordinates) {
		//get the bin label using a single individual?
		assert archive.get(binCoordinates).size() != 0 : "bin is empty";
		return getIndividualBinLabel(archive.get(binCoordinates).elementAt(0));
	}

	/**
	 * Gets the bin coordinates from a given individual and returns as a vector.
	 * @param individualScore the given score to find the bin coordinates of
	 * @return the bin coordinates as a vector for the given score.
	 */
	public Vector<Integer> getBinCoordinatesFromScore(Score<T> individualScore){
		int[] binIndex = getBinMapping().multiDimensionalIndices(individualScore.MAPElitesBehaviorMap());	//grabs bin coordinates int array
		Vector<Integer> individualBinCoordinates = new Vector<Integer>();
		for (int i = 0; i < binIndex.length; i++) {		//turns int array into vector
			individualBinCoordinates.add(binIndex[i]);
		}
		return individualBinCoordinates;
	}
	
	//main for testing
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
//		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:nsga2test log:NSG2Test-Test saveTo:Test trackPseudoArchive:true netio:true lambda:37 maxGens:200 task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
//		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" mapElitesQDBaseOffset:525 io:true base:nsga2test log:NSG2Test-MAPElites saveTo:MAPElites netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:50 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
//		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" mapElitesQDBaseOffset:525 base:nsga2test log:NSG2Test-CMAES saveTo:CMAES trackPseudoArchive:true netio:true mu:37 lambda:37 maxGens:200 ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
//		 below taken from TypeCountFitness
//			MMNEAT.main(("runNumber:1 randomSeed:99 maximumMOMESubPopulationSize:2 numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:10 maxGens:1 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:10 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftTypeCountFitness:true minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" crossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" ")); 

//			MMNEAT.main(("runNumber:105 randomSeed:99							   									   numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:10 maxGens:1 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:10 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing
//			MMNEAT.main(("runNumber:3 randomSeed:99 saveWholeMinecraftArchiveAtEnd:false minecraftOccupiedCountFitness:true maximumMOMESubPopulationSize:5 numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:10 maxGens:15 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftTypeCountFitness:true minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" crossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" ")); 
			
		//MMNEAT.main(("runNumber:6 randomSeed:99 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:true minecraftMaximizeVolumeFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:100 maxGens:60000 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:15 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftmoo log:MinecraftMOO-MOMEFlyVsMissileDirectSmallPOCapped saveTo:MOMEFlyVsMissileDirectSmallPOCapped extraSpaceBetweenMinecraftShapes:100 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeX:50 minecraftTargetDistancefromShapeZ:0 minecraftMissileFitness:true rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false interactWithMapElitesInWorld:false ea:edu.southwestern.evolution.mome.MOME mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 minecraftClearWithGlass:false maximumMOMESubPopulationSize:10").split(" ")); 

		
		
		MMNEAT.main("runNumber:1 randomSeed:1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:true minecraftMaximizeVolumeFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:100 maxGens:60000 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:15 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:TESTING-MOMEFlyVsMissileDirectSmallPOCappedCompass saveTo:MOMEFlyVsMissileDirectSmallPOCappedCompass extraSpaceBetweenMinecraftShapes:100 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeX:50 minecraftTargetDistancefromShapeZ:0 minecraftMissileFitness:true parallelMAPElitesInitialize:true rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false interactWithMapElitesInWorld:false ea:edu.southwestern.evolution.mome.MOME mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 minecraftClearWithGlass:false maximumMOMESubPopulationSize:10 minecraftCompassMissileTargets:true".split(" "));
	}

}
