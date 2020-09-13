package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Vector;
import java.util.stream.IntStream;

import edu.southwestern.scores.Score;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import wox.serial.Easy;

public class Archive<T> {
	
	Vector<Score<T>> archive; // Vector is used because it is thread-safe
	private int occupiedBins;
	private BinLabels mapping;
	private boolean saveElites;
	private String archiveDir;

	public BinLabels getBinLabelsClass() {
		return mapping;
	}
	
	public Archive(boolean saveElites) {
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
		archive = new Vector<Score<T>>(numBins);
		occupiedBins = 0;
		// Archive directory
		String experimentDir = FileUtilities.getSaveDirectory();
		archiveDir = experimentDir + File.separator + "archive";
		if(saveElites) {
			new File(archiveDir).mkdirs(); // make directory
		}
		for(int i = 0; i < numBins; i++) {
			archive.add(null); // Place holder for first individual and future elites
		}
	}

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
	 * Directory where the archive is being saved on disk
	 * @return Path to directory
	 */
	public String getArchiveDirectory() {
		return archiveDir;
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
				Score<T> elite = archive.get(i);
				return replaceIfBetter(candidate, i, elite);
			}).count(); // Number of bins whose elite was replaced
			//System.out.println(newElites + " elites were replaced");
			// Whether any elites were replaced
			return newElites > 0;
		} else if(candidate.usesMAPElitesBinSpecification()) {
			int[] candidateBinIndices = candidate.MAPElitesBinIndex();
			Score<T> currentBinOccupant = getElite(candidateBinIndices);
			return replaceIfBetter(candidate, this.getBinMapping().oneDimensionalIndex(candidateBinIndices), currentBinOccupant);
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
		if(candidateScore > Double.NEGATIVE_INFINITY && (currentOccupant == null || candidateScore > currentOccupant.behaviorIndexScore(binIndex))) {
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
	 * Save the candidate to disk since since it replaced the former bin occupant (or was first)
	 * @param candidate Score with information to save
	 * @param binIndex Index in bin
	 */
	private void conditionalEliteSave(Score<T> candidate, int binIndex) {
		// Need to save all elites so that re-load on resume works
		if(saveElites) {
			// Easier to reload on resume if file name is uniform. Will also save space by overwriting
			String binPath = archiveDir + File.separator + mapping.binLabels().get(binIndex);
			Easy.save(candidate.individual, binPath + "-elite.xml");
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
		return archive.get(mapping.oneDimensionalIndex(binIndices));
	}

	/**
	 * Elite individual from specified bin, or null if empty
	 * @param binIndex 1D archive index
	 * @return elite individual score instance
	 */
	public Score<T> getElite(int binIndex) {
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
		return elite == null ? Double.NEGATIVE_INFINITY : elite.behaviorIndexScore(binIndex);
	}
	
	/**
	 * Random index, but the bin is guarranteed to be occupied
	 * @return
	 */
	public int randomOccupiedBinIndex() {
		int steps = RandomNumbers.randomGenerator.nextInt(occupiedBins);
		int originalSteps = steps;
		int occupiedCount = 0;
		for(int i = 0; i < archive.size(); i++) {
			if(archive.get(i) != null) {
				occupiedCount++;
				if(steps == 0) {
					return i;
				} else {
					steps--;
				}
			}
		}
		throw new IllegalStateException("The number of occupied bins ("+occupiedBins+") and the archive size ("+archive.size()+") have a problem. "+steps+" steps left out of "+originalSteps +". occupiedCount = "+occupiedCount);
	}
	
	/**
	 * Select random bin index
	 * @return index of a random bin
	 */
	public int randomBinIndex() {
		return RandomNumbers.randomGenerator.nextInt(archive.size());
	}
}
