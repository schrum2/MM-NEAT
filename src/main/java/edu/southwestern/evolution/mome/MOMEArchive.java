package edu.southwestern.evolution.mome;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import com.sun.org.apache.xalan.internal.xsltc.dom.KeyIndex;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.datastructures.ArrayUtil;

public class MOMEArchive<T> {

	
	ConcurrentHashMap<Vector<Integer>,Vector<Score<T>>> archive;
	//vector of integers represents coordinates for bins of arbitrary dimension
	//vector of scores are the scores of candidates in that bin
	

	//private int occupiedBins; 
	private BinLabels mapping;
	private boolean saveElites;	//would like to know what this is exactly
	private String archiveDir;
	
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

	public void setMapping(BinLabels mapping) {
		this.mapping = mapping;
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
	 * constructor
	 * @param saveElites
	 * @param archiveDirectoryName
	 * @param initNumIndividualsInCells
	 */
	public MOMEArchive(boolean saveElites, String archiveDirectoryName, int initNumIndividualsInCells) {
		this.saveElites = saveElites;
		// Initialize mapping
		try {
			mapping = (BinLabels) ClassCreation.createObject("MOMEBinLabels");
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
			//add the candidate (Score) to the vector of scores for that bin
			archive.get(candidateBinCoordinates).add(candidate);	
			
			//recalculate pareito front, first convert a bunch of things
			ArrayList<Score<T>> arrayListOfScores = new ArrayList<Score<T>>();
			arrayListOfScores.addAll(archive.get(candidateBinCoordinates));
			NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(arrayListOfScores));
			//check if the candidate it there and return if it is
			long candidateID = candidate.individual.getId();
			for (Score<T> score : archive.get(candidateBinCoordinates)) {
				if(score.individual.getId() == candidateID) {
					return true;
				}
			}
			return false;
			//return archive.get(candidateBinCoordinates).contains(candidate);
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
	public Score<T> getRandomIndividaul (){
		//grab a random individual from a random bin
		return RandomNumbers.randomElement(getRandomPopulation());
	}
	/**
	 * get's a random bin from the archive
	 * @return 
	 */
	public Vector<Score<T>> getRandomPopulation (){
		//grab a random bin
		return RandomNumbers.randomElement(archive.values());
	}
	
	//unsure if I even need this but made a stub
	public Vector<Score<T>> getWholeArchiveScores(){
		//needed?
		return null;
	}
	
	/**
	 * returns all the scores for a specific bin
	 * @param keyBinCoordinates	the key which is the coordinates of the bin
	 * @return the scores for that bin
	 */
	public Vector<Score<T>> getScores(Vector<Integer> keyBinCoordinates){
		return archive.get(keyBinCoordinates);
	}
	
	//don't know if I even need the below method
//	public float[] getAllEliteScores( ) {
//		float[] result = new float[archive.size()];
//		//iterate through each key
//		int keyIndexCount = 0; ///to offset placement in float array
//		//TODO: find a way to offset the result float for each key entry
//		//use previous function and += array or whatever
//		archive.forEach( (k,v) -> {
//			float[] temp1 = result;
//			float[] temp2 = turnVectorScoresIntoFloatArray(v);
//			//Vector<Score<T>> scoreVector = getScores(k);
//			//Vector<Score<T>> scoreVector = v;
//			
//			
//
//			//after that temp2 should hold new vector list
//			
//			//add both temps to result
//
//
//		});
//		return result;
//	}
	
	/**
	 * turns a vector of Scores into a float array. Unsure if actually needed
	 * @param scoresList the original scores
	 * @return	a float array containing the score values
	 */
	public float[] turnVectorScoresIntoFloatArray(Vector<Score<T>> scoresList) {
		float[] result = new float[scoresList.size()];
		for(int i = 0; i < result.length; i++) {
			Score<T> score = scoresList.get(i);
			result[i] = score == null ? Float.NEGATIVE_INFINITY : new Double(score.behaviorIndexScore(i)).floatValue();
		}
		return result;
	}

}
