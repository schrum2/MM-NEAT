package edu.southwestern.evolution.mome;

import java.io.File;
import java.util.Vector;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.scores.Score;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.file.FileUtilities;

public class MOMEArchive<T> {

	Vector<Score<T>> archive; // Vector is used because it is thread-safe
	//Vector<Vector<Score<T>> ????
	private int occupiedBins; 
	private BinLabels mapping;
	private boolean saveElites;
	private String archiveDir;
	private int numberOfIndividualsInCells;
	
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
		archive = new Vector<Score<T>>(numBins);
		occupiedBins = 0;
		
		// Archive directory
		String experimentDir = FileUtilities.getSaveDirectory();
		archiveDir = experimentDir + File.separator + archiveDirectoryName;
		if(saveElites) {
			new File(archiveDir).mkdirs(); // make directory
		}
		for(int i = 0; i < numBins; i++) {
			archive.add(null); // Place holder for first individual and future elites
		}
	}
}
