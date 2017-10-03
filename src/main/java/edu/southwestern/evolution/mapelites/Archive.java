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
	private BinLabels<T> mapping;
	private boolean saveElites;
	private String archiveDir;

	@SuppressWarnings("unchecked")
	public Archive(boolean saveElites) {
		this.saveElites = saveElites;
		// Initialize mapping
		try {
			mapping = (BinLabels<T>) ClassCreation.createObject("mapElitesBinLabels");
		} catch (NoSuchMethodException e) {
			System.out.println("Failed to get Bin Mapping for MAP Elites!");
			e.printStackTrace();
			System.exit(1);
		}
		int numBins = mapping.binLabels().size();
		archive = new Vector<Score<T>>(numBins);
		// Archive directory
		String experimentDir = FileUtilities.getSaveDirectory();
		archiveDir = experimentDir + File.separator + "archive";
		// Subdirectories for each bin
		for(int i = 0; i < numBins; i++) {
			if(saveElites) {
				String binPath = archiveDir + File.separator + mapping.binLabels().get(i);
				// Create all of the bin directories
				new File(binPath).mkdirs(); // make directory
			}
			archive.add(null); // Place holder for first individual and future elites
		}
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
			result[i] = score == null ? Float.NEGATIVE_INFINITY : score.behaviorVector.get(i).floatValue();
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
	public BinLabels<T> getBinMapping() { 
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
		// Java's new stream features allow for easy parallelism
		IntStream stream = IntStream.range(0, archive.size());
		long newElites = stream.parallel().filter((i) -> {
			Score<T> elite = archive.get(i);
			double candidateScore = candidate.behaviorVector.get(i);
			// If the bin is empty, or the candidate is better than the elite for that bin's score
			if(elite == null || candidateScore > elite.behaviorVector.get(i)) {
				archive.set(i, candidate.copy()); // Replace elite
				// Need to save all elites so that re-load on resume works
				if(saveElites) {
					// Easier to reload on resume if file name is uniform. Will also save space by overwriting
					String binPath = archiveDir + File.separator + mapping.binLabels().get(i);
					Easy.save(candidate.individual, binPath + File.separator + "elite.xml");
					// Write scores as simple text file (less to write than xml)
					try {
						PrintStream ps = new PrintStream(new File(binPath + File.separator + "scores.txt"));
						for(Double score : candidate.behaviorVector) {
							ps.println(score);
						}
					} catch (FileNotFoundException e) {
						System.out.println("Could not write scores for " + candidate.individual.getId() + ":" + candidate.behaviorVector);
						e.printStackTrace();
						System.exit(1);
					}
				}
				return true;
			} else {
				return false;
			}
		}).count(); // Number of bins whose elite was replaced
		//System.out.println(newElites + " elites were replaced");
		// Whether any elites were replaced
		return newElites > 0;
	}

	/**
	 * Elite individual from specified bin, or null if empty
	 * @param binIndex
	 * @return
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
		return elite == null ? Double.NEGATIVE_INFINITY : elite.behaviorVector.get(binIndex);
	}
	
	/**
	 * Select random bin index
	 * @return index of a random bin
	 */
	public int randomBinIndex() {
		return RandomNumbers.randomGenerator.nextInt(archive.size());
	}
}
