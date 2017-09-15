package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.util.*;

import edu.southwestern.parameters.Parameters;
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
	private Map<String,ArrayList<Score<T>>> archive;
	private BinMapping<T> mapping;
	private boolean saveElites;
	private String archiveDir;
	private int mapElitesBinSize;

	@SuppressWarnings("unchecked")
	public Archive(boolean saveElites) {
		// Initialize mapping
		archive = new HashMap<String,ArrayList<Score<T>>>();
		this.saveElites = saveElites;
		this.mapElitesBinSize = Parameters.parameters.integerParameter("mapElitesBinSize");
		if(saveElites) {
			String experimentDir = FileUtilities.getSaveDirectory();
			archiveDir = experimentDir + File.separator + "archive";
			new File(archiveDir).mkdirs();
		}
		try {
			mapping = (BinMapping<T>) ClassCreation.createObject("mapElitesBinMapping");
		} catch (NoSuchMethodException e) {
			System.out.println("Failed to get Bin Mapping for MAP Elites!");
			e.printStackTrace();
			System.exit(1);
		}
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
	public BinMapping<T> getBinMapping() { 
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
		String binLabel = mapping.binForScore(candidate);
		boolean newElite = true; // Will the candidate be a new elite?
		ArrayList<Score<T>> bin;
		if(!archive.containsKey(binLabel)) { // empty bin
			bin = new ArrayList<Score<T>>(mapElitesBinSize+1); // Just big enough to hold one excess individual 
			bin.add(candidate); // now the elite of the bin
		} else { // bin not empty
			bin = archive.get(binLabel);
			Score<T> elite = bin.get(0); // best is always at front
			if(candidate.isBetter(elite)) { // candidate is better than elite
				bin.add(0, candidate); // Add at front as new elite
			} else { // candidate is worse than elite
				bin.add(candidate); // Add at end: not part of standard MAP-Elites
				newElite = false; // Was not a new elite
			}
		}
		// Clear out non-elite individuals
		while(bin.size() > mapElitesBinSize) {
			bin.remove(bin.size() - 1);
		}
		archive.put(binLabel, bin); // put bin back
		if(newElite) {
			System.out.println("Add to " + binLabel + " with score " + candidate.scores[0]);
			if(saveElites) {
				String fileName = "ELITEindividual" + candidate.individual.getId() + ".xml";
				String binPath = archiveDir + File.separator + binLabel;
				new File(binPath).mkdirs(); // make directory if needed
				Easy.save(candidate.individual, binPath + File.separator + fileName);
			}
		}
		return newElite;
	}
	
	/**
	 * Return current elite organism from specified bin
	 * @param binLabel String label identifying bin
	 * @return ArchivedOrganism with best fitness in that bin, or null if bin is empty/not present
	 */
	public Score<T> getElite(String binLabel) {
		ArrayList<Score<T>> bin = archive.get(binLabel);
		return bin == null ? null : bin.get(0); // elite is at front (index 0)
	}
	
	/**
	 * Get the score of the elite for a given bin, or negative infinity
	 * if the bin is empty.
	 * @param binLabel Label for bin
	 * @return Best score
	 */
	public double getBinScore(String binLabel) {
		Score<T> elite = getElite(binLabel);
		return elite == null ? Double.NEGATIVE_INFINITY : elite.scores[0];
	}
	
	/**
	 * Select random label across bins that are occupied
	 * @return String label of a random occupied bin
	 */
	public String randomBinLabel() {
		return RandomNumbers.randomElement(archive.keySet().toArray(new String[0]));
	}
	
	/**
	 * Save the whole archive contents to the specified file path
	 * @param path
	 */
//	public void saveWholeArchive(String path) {
//		String archiveDir = path + File.separator + "archive";
//		new File(archiveDir).mkdirs(); // Create the directory
//		for(String label : archive.keySet()) {
//			String subdir = archiveDir + File.separator + label;
//			new File(subdir).mkdir(); // Make the subdir for the bin
//			Genotype<T> elite = getElite(label).individual;
//			Easy.save(elite, subdir + File.separator + "elite.xml");
//		}
//	}
		
	/**
	 * Return the number of elites in the archive
	 * @return Number of occupied bins
	 */
	public int size() {
		return archive.size();
	}
}
