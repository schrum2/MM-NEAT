package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import edu.southwestern.scores.Score;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import wox.serial.Easy;

public class Archive<T> {
	
	/**
	 * Stores all individuals in bins (looked up by hash keys) where each bin is
	 * sorted so that the first individual (index 0) is the fittest (elite) individual.
	 */
	ArrayList<Score<T>> archive;
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
		archive = new ArrayList<Score<T>>(numBins);
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
	 * @return
	 */
	public double[] getEliteScores() {
		double[] result = new double[archive.size()];
		for(int i = 0; i < result.length; i++) {
			Score<T> score = archive.get(i);
			result[i] = score == null ? Double.NEGATIVE_INFINITY : score.behaviorVector.get(i);
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
		boolean newElite = false; // new elite not added yet
		for(int i = 0; i < archive.size(); i++) {
			Score<T> elite = archive.get(i);
			double candidateScore = candidate.behaviorVector.get(i);
			// If the bin is empty, or the candidate is better than the elite for that bin's score
			if(elite == null || candidateScore > elite.behaviorVector.get(i)) {
				archive.set(i, candidate); // Replace elite
				newElite = true;
				// Need to save all elites so that re-load on resume works
				if(saveElites) {
					//String fileName = "ELITEindividual" + candidate.individual.getId() + ".xml";
					// Easier to reload on resume if file name is uniform. Will also save space by overwriting
					String binPath = archiveDir + File.separator + mapping.binLabels().get(i);
					Easy.save(candidate.individual, binPath + File.separator + "elite.xml");
					// Write scores as simpel text file (less to write than xml)
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
			}
		}		
		return newElite;
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
