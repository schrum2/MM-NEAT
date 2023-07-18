package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MultiobjectiveUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Archive for MAP Elites
 *
 * @param <T>
 */
public class Archive<T> {
	
	Vector<Score<T>> archive; // Vector is used because it is thread-safe
	private int occupiedBins; 
	private BinLabels mapping;
	private boolean saveElites;
	private String archiveDir;

	public Archive(boolean saveElites, String archiveDirectoryName) {
		this.saveElites = saveElites;
		// Initialize mapping
		try {
			mapping = (BinLabels) ClassCreation.createObject("mapElitesBinLabels");
		} catch (NoSuchMethodException e) {
			System.out.println("Failed to get Bin Mapping for MAP Elites!");
			e.printStackTrace();
			System.exit(1);
		}
		int numBins = mapping.binLabels().size();
		System.out.println("Archive contains "+numBins+" number of bins");
		archive = new Vector<Score<T>>(numBins);
		occupiedBins = 0;
		// Archive directory
		String experimentDir = FileUtilities.getSaveDirectory();
		setArchiveDir(experimentDir + File.separator + archiveDirectoryName);
		if(saveElites) {
			new File(getArchiveDir()).mkdirs(); // make directory
		}
		for(int i = 0; i < numBins; i++) {
			archive.add(null); // Place holder for first individual and future elites
		}
	}
	
	/**
	 * Create a new archive by inserting the contents of another.
	 * Sort of like copying, but involves reevaluation, so the new
	 * one will only be identical if all evaluations come out the same.
	 * 
	 * @param other Archive to draw contents from
	 */
	public Archive(Archive<T> other) {
		this(other.getArchive(), other.mapping, other.getArchiveDir(), other.saveElites);
	}
	
	public Archive(Vector<Score<T>> other, BinLabels otherMapping, String otherDir, boolean otherSave) {
		saveElites = false; // Don't save while reorganizing
		mapping = otherMapping;
		int numBins = otherMapping.binLabels().size();
		archive = new Vector<Score<T>>(numBins);
		occupiedBins = 0;
		setArchiveDir(otherDir); // Will save in the same place!

		// Fill with null values before actually selecting individuals to copy over
		for(int i = 0; i < numBins; i++) {
			archive.add(null); // Place holder for first individual and future elites
		}
		// Loop through original archive
		other.parallelStream().forEach( (s) -> {
			if(s != null) { // Ignore empty cells
				@SuppressWarnings("unchecked")
				Score<T> newScore = ((MAPElites<T>) MMNEAT.ea).task.evaluate(s.individual);
				this.add(newScore);
			}
		});
		
		// Ok to save moving forward
		saveElites = otherSave;
	}
	
	/**
	 * Number of occupied bins (non null).
	 * Note that this access is not synchronized.
	 * Could be subject to race conditions.
	 * 
	 * @return number of occupied bins
	 */
	public int getNumberOfOccupiedBins() {
		return occupiedBins;
	}
	
	/**
	 * The raw Vector of the archive, including many null slots.
	 * The size of this exactly equals the number of cells that "could" be occupied.
	 * @return Vector of Score instances in the archive
	 */
	public Vector<Score<T>> getArchive(){
		return archive;
	}
	
	/**
	 * Get the scores of all elites for each bin.
	 * Also casts down to float
	 * @return
	 */
	public float[] getEliteScores() {
		float[] result = new float[archive.size()];
		for(int i = 0; i < result.length; i++) {
			Score<T> score = archive.get(i);
			result[i] = score == null ? Float.NEGATIVE_INFINITY : new Double(score.behaviorIndexScore(i)).floatValue();
		}
		return result;
	}
	
	/**
	 * gets the scores in the otherStats index for each bin
	 * instead of getting the fitness score for each bin, it gets the score in a specific otherStats
	 * Also casts down to float
	 * based on getEliteScores()
	 * @param index the index in Score.otherScores that is being retrieved
	 * @return list of scores within the Scores otherStats at given index for each bin
	 */
	public float[] getOtherStatsScores(int index) {
		float[] result = new float[archive.size()];
		for(int i = 0; i < result.length; i++) {	//i retrieves Score from archive
			Score<T> score = archive.get(i);		//index is for the otherStat being retrieved
			result[i] = score == null ? Float.NEGATIVE_INFINITY : new Double(score.otherStats[index]).floatValue();
//			System.out.println("result[i]:"+result[i]);
		}
		return result;
	}
	
	/**
	 * Directory where the archive is being saved on disk
	 * @return Path to directory
	 */
	public String getArchiveDirectory() {
		return getArchiveDir();
	}
	
	/**
	 * Method for putting individuals in bins
	 * @return
	 */
	public BinLabels getBinMapping() { 
		return mapping;
	}
		
	/**
	 * Given an ArchivedOrganism (which contains some evaluation information about the genotype),
	 * figure out which bin it belongs in and add it at the front if it is a new elite.
	 * Otherwise, add it at the end.
	 * @param candidate Organism containing genotype and eval information
	 * @return Whether organism was a new elite
	 */
	public boolean add(Score<T> candidate) {
		if(candidate.usesTraditionalBehaviorVector()) {
			// Java's new stream features allow for easy parallelism
			// When using the whole behavior vector, have to wastefully check every index
			IntStream stream = IntStream.range(0, archive.size());
			long newElites = stream.parallel().filter((i) -> {
				synchronized(this) { // Don't want some other elite to be added after retrieving a null elite
					Score<T> elite = archive.get(i);
					return replaceIfBetter(candidate, i, elite);
				}
			}).count(); // Number of bins whose elite was replaced
			//System.out.println(newElites + " elites were replaced");
			// Whether any elites were replaced
			return newElites > 0;
		} else if(candidate.usesMAPElitesMapSpecification() && !getBinMapping().discard(candidate.MAPElitesBehaviorMap())) {
			int oneD = getBinMapping().oneDimensionalIndex(candidate.MAPElitesBehaviorMap());
			boolean result = false;
			synchronized(this) { // Make sure elite at the index does not change while considering replacement
				// Synchronizing on the whole archive seems unnecessary ... maybe just the index? How?
				Score<T> currentBinOccupant = getElite(oneD);
				result = replaceIfBetter(candidate, oneD, currentBinOccupant);
			}
			return result;
			
			
			// TODO: Why are we inserting if the binning scheme says to discard it?
		} else if(candidate.usesMAPElitesBinSpecification()) {
			int[] candidateBinIndices = candidate.MAPElitesBinIndex();
			int oneD = this.getBinMapping().oneDimensionalIndex(candidateBinIndices);
			boolean result = false;
			synchronized(this) { // Make sure elite at the index does not change while considering replacement
				// Synchronizing on the whole archive seems unnecessary ... maybe just the index? How?
				Score<T> currentBinOccupant = getElite(oneD);
				result = replaceIfBetter(candidate, oneD, currentBinOccupant);
			}
			return result;
		} else {
			// In some domains, a flawed genotype can emerge which cannot produce a behavior vector. Obviously cannot be added to archive.
			return false; // nothing added
		}
	}

	/**
	 * Candidate replaces currentOccupant of bin with binIndex if its score is better.
	 * @param candidate Score instance for new candidate
	 * @param binIndex Bin index
	 * @param currentOccupant Score instance of current bin occupant (a former elite)
	 * @return true if current occupant was repalced
	 */
	private boolean replaceIfBetter(Score<T> candidate, int binIndex, Score<T> currentOccupant) {
		double candidateScore = candidate.behaviorIndexScore(binIndex);
		// Score cannot be negative infinity. Next, check if the bin is empty, or the candidate is better than the elite for that bin's score
		if(candidateScore > Float.NEGATIVE_INFINITY && (currentOccupant == null || candidateScoreIsBetterThanCurrentOccupantScore(binIndex, currentOccupant, candidateScore))) {
			archive.set(binIndex, candidate.copy()); // Replace elite
			if(currentOccupant == null) { // Size is actually increasing
				synchronized(this) {
					occupiedBins++; // Shared variable
				}
			}
			conditionalEliteSave(candidate, binIndex);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Whether new candidate score is better than the score of a current occupant, which must be non-null.
	 * A parameter setting can indicate whether strict superiority or soft superiority is required.
	 * 
	 * @param binIndex Bin in question
	 * @param currentOccupant Non-null occupant of that bin
	 * @param candidateScore Score of a new candidate
	 * @return Whether the score is better in some sense
	 */
	private boolean candidateScoreIsBetterThanCurrentOccupantScore(int binIndex, Score<T> currentOccupant, double candidateScore) {
		assert currentOccupant != null;
		if(Parameters.parameters.booleanParameter("mapElitesReplaceOnEquality")) {
			return candidateScore >= currentOccupant.behaviorIndexScore(binIndex);
		} else {
			return candidateScore > currentOccupant.behaviorIndexScore(binIndex);
		}
	}
	
	/**
	 * Return set of Scores that are elites (each occupied bin)
	 * across the whole archive.
	 * 
	 * @return Set of champion Score instances containing genotypes
	 */
	public synchronized Set<Score<T>> getChampions() {
		// Filter out null entries
		Stream<Score<T>> nonNullStream = archive.parallelStream().filter(s -> s != null);
		// Find one of the max scores (at least one should exist)
		Optional<Score<T>> maxScore = nonNullStream.max(new Comparator<Score<T>>(){
			@Override
			public int compare(Score<T> o1, Score<T> o2) {
				return (int) Math.signum(o1.behaviorIndexScore() - o2.behaviorIndexScore());
			}
		});
		// Get the set of all max scores
		return archive.parallelStream().filter(s -> s != null).filter(s -> s.behaviorIndexScore() == maxScore.get().behaviorIndexScore()).collect(Collectors.toSet());
	}
	
	/**
	 * Do a hypervolume calculation across the whole archive treating
	 * the otherStats within each Score instance at the actual objective
	 * scores for the purpose of the calculation.
	 * 
	 * @return resulting hypervolume over otherStats
	 */
	public Pair<Double, List<Score<T>>> getHypervolumeAndParetoFrontAcrossOtherStats() {
		Set<Score<T>> champions = getChampions();
		List<Score<T>> scoresOfOtherStats = Score.getScoresOfOtherStats(champions);
		// Assume there is at least one score
		double[] minObjectiveScores = new double[scoresOfOtherStats.get(0).scores.length];
		for(int i = 0; i < minObjectiveScores.length; i++) {
			// It is assumed that each other stat used with MAP Elites is a component from a weighted sum.
			// However, some component fitnesses have negative minimum values. min scores are needed to shift
			// the range to start at 0. We add 1 to the index since we
			// skip over the actual fitness function (just one) and only get other stats.
			minObjectiveScores[i] = MMNEAT.fitnessFunctionMinScore(1 + i);
		}
		return MultiobjectiveUtil.hypervolumeAndParetoFrontFromPopulation(scoresOfOtherStats, minObjectiveScores);
	}
	
	/**
	 * Remove an elite from the archive
	 * @param binIndex 1D index of elite in archive
	 * @return Element that was removed
	 */
	public synchronized Score<T> removeElite(int binIndex) {
		Score<T> current = archive.get(binIndex);
		archive.set(binIndex, null);
		if(current != null) {
			occupiedBins--;
		}
		return current;
	}

	/**
	 * Save the candidate to disk since since it replaced the former bin occupant (or was first)
	 * @param candidate Score with information to save
	 * @param binIndex Index in bin
	 */
	private void conditionalEliteSave(Score<T> candidate, int binIndex) {
		// Need to save all elites so that re-load on resume works
		if(saveElites) {
			// Easier to reload on resume if file name is uniform. Will also save space by overwriting
			String binPath = getArchiveDir() + File.separator + mapping.binLabels().get(binIndex);
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

	/**
	 * Given the multiple dimensions corresponding to this particular archive,
	 * use a one dimensional index for if the multiple dimensions are reduced
	 * to get the corresponding archive elite from its bin.
	 * 
	 * to a single array in row-major order
	 * @param binIndices array of individual indices
	 * @return elite individual score instance 
	 */
	public Score<T> getElite(int[] binIndices) {
		int oneD = mapping.oneDimensionalIndex(binIndices);
		try {
			return archive.get(oneD);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException(Arrays.toString(binIndices) + " -> " + oneD);
		}
	}

	/**
	 * Elite individual from specified bin, or null if empty
	 * @param binIndex 1D archive index
	 * @return elite individual score instance
	 */
	public Score<T> getElite(int binIndex) {
		assert archive.size() != 0;
		assert binIndex < archive.size() : binIndex + " of "+ archive.size()+":" +this.getBinMapping().binLabels();
		return archive.get(binIndex);
	}
	
	/**
	 * Get the score of the elite for a given bin, or negative infinity
	 * if the bin is empty.
	 * @param binIndex
	 * @return Best score
	 */
	public double getBinScore(int binIndex) {
		Score<T> elite = getElite(binIndex);
		return elite == null ? Float.NEGATIVE_INFINITY : elite.behaviorIndexScore(binIndex);
	}
	
	/**
	 * Random index, but the bin is guaranteed to be occupied
	 * @return Index in the 1D complete archive that contains an elite (not empty)
	 */
	public int randomOccupiedBinIndex() {
		int steps = -1, originalSteps = -1, occupiedCount= -1;
		int archiveSize = archive.size();
		try {
			steps = RandomNumbers.randomGenerator.nextInt(occupiedBins);
			originalSteps = steps;
			occupiedCount = 0;
			for(int i = 0; i < archiveSize; i++) {
				if(archive.get(i) != null) {
					occupiedCount++;
					if(steps == 0) {
						return i;
					} else {
						steps--;
					}
				}
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Could not pick random occupied bin with occupiedBins = "+occupiedBins+"; "+steps+" steps left out of "+originalSteps +". occupiedCount = "+occupiedCount);
		}
		throw new IllegalStateException("The number of occupied bins ("+occupiedBins+") and the archive size ("+archiveSize+") have a problem. "+steps+" steps left out of "+originalSteps +". occupiedCount = "+occupiedCount);
	}
	
	/**
	 * Select random bin index
	 * @return index of a random bin
	 */
	public int randomBinIndex() {
		return RandomNumbers.randomGenerator.nextInt(archive.size());
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		//int runNum = 50; 
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:nsga2test log:NSG2Test-Test saveTo:Test trackPseudoArchive:true netio:true lambda:37 maxGens:200 task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" mapElitesQDBaseOffset:525 io:true base:nsga2test log:NSG2Test-MAPElites saveTo:MAPElites netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:50 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" mapElitesQDBaseOffset:525 base:nsga2test log:NSG2Test-CMAES saveTo:CMAES trackPseudoArchive:true netio:true mu:37 lambda:37 maxGens:200 ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		
		MMNEAT.main(("runNumber:1 randomSeed:1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:false minecraftMaximizeVolumeFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:100 maxGens:60000 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:22 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftmoo log:MinecraftMOO-MEWSFlyVsMissileDirectSmallPOCappedCompass saveTo:MEWSFlyVsMissileDirectSmallPOCappedCompass extraSpaceBetweenMinecraftShapes:100 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeX:25 minecraftTargetDistancefromShapeZ:0 minecraftMissileFitness:false rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false interactWithMapElitesInWorld:false ea:edu.southwestern.evolution.mapelites.MAPElites mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 minecraftClearWithGlass:false maximumMOMESubPopulationSize:10 minecraftCompassMissileTargets:true parallelMAPElitesInitialize:true minecraftWeightedSumsMissileAndChangeCenterOfMassFitness:true").split(" "));
		//the above parameters run minecraftWeightedSumsMissileAndChangeCenterOfMassFitness:true with mu:100 maxGens:60000 steadyStateIndividualsPerGeneration:100
		//for testing weighted sums with timed evaluations
	}

	public String getArchiveDir() {
		return archiveDir;
	}

	public void setArchiveDir(String archiveDir) {
		this.archiveDir = archiveDir;
	}
}
