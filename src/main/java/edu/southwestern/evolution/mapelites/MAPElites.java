package edu.southwestern.evolution.mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.loderunner.LodeRunnerLevelTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import wox.serial.Easy;

/**
 * My version of Multi-dimensional Archive of Phenotypic Elites (MAP-Elites), the quality diversity (QD)
 * algorithms that illuminates a search space. This is an unusual implementation, but it gets the job done.
 * 
 * MAP Elites article: https://arxiv.org/abs/1504.04909
 * 
 * @author schrum2
 *
 * @param <T> phenotype
 */
public class MAPElites<T> implements SteadyStateEA<T> {
	private static final int NUM_CODE_EMPTY = -1;
	private static final int NUM_CODE_DIRECT = 2;
	private static final int NUM_CODE_CPPN = 1;
	private boolean io;
	private MMNEATLog archiveLog = null; // Archive elite scores
	private MMNEATLog fillLog = null; // Archive fill amount
	private MMNEATLog cppnThenDirectLog = null;
	private MMNEATLog cppnVsDirectFitnessLog = null;
	private LonerTask<T> task;
	private Archive<T> archive;
	private boolean mating;
	private double crossoverRate;
	private int iterations;
	private int iterationsWithoutElite;
	private int individualsPerGeneration;

	public BinLabels getBinLabelsClass() {
		return archive.getBinLabelsClass();
	}
	
	@SuppressWarnings("unchecked")
	public MAPElites() {
		this.task = (LonerTask<T>) MMNEAT.task;
		this.io = Parameters.parameters.booleanParameter("io"); // write logs
		this.archive = new Archive<>(Parameters.parameters.booleanParameter("netio"));
		if(io) {
			String infix = "MAPElites";
			// Logging in RAW mode so that can append to log file on experiment resume
			archiveLog = new MMNEATLog(infix, false, false, false, true); 
			fillLog = new MMNEATLog("Fill", false, false, false, true);
			// Can't check MMNEAT.genotype since MMNEAT.ea is initialized before MMNEAT.genotype
			if(Parameters.parameters.classParameter("genotype").equals(CPPNOrDirectToGANGenotype.class)) {
				cppnThenDirectLog = new MMNEATLog("cppnToDirect", false, false, false, true);
				cppnVsDirectFitnessLog = new MMNEATLog("cppnVsDirectFitness", false, false, false, true);
			}
			// Create gnuplot file for archive log
			String experimentPrefix = Parameters.parameters.stringParameter("log")
					+ Parameters.parameters.integerParameter("runNumber");
			String prefix = experimentPrefix + "_" + infix;
			String fillPrefix = experimentPrefix + "_" + "Fill";
			String directory = FileUtilities.getSaveDirectory();// retrieves file directory
			directory += (directory.equals("") ? "" : "/");
			String fullName = directory + prefix + "_log.plot";
			String fullFillName = directory + fillPrefix + "_log.plot";
			File plot = new File(fullName); // for archive log plot file
			File fillPlot = new File(fullFillName);
			// Write to file
			try {
				// Archive plot
				this.individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");
				PrintStream ps = new PrintStream(plot);
				ps.println("set term pdf enhanced");
				ps.println("unset key");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set yrange [0:"+ (Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration) +"]");
				ps.println("set xrange [0:"+ archive.getBinMapping().binLabels().size() + "]");
				ps.println("set title \"" + experimentPrefix + " Archive Performance\"");
				ps.println("set output \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".pdf\"");
				// The :1 is for skipping the "generation" number logged in the file
				ps.println("plot \"" + fullName.substring(fullName.lastIndexOf('/')+1, fullName.lastIndexOf('.')) + ".txt\" matrix every ::1 with image");
				ps.close();
				
				// Fill percentage plot
				ps = new PrintStream(fillPlot);
				ps.println("set term pdf enhanced");
				//ps.println("unset key");
				ps.println("set key bottom right");
				// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
				ps.println("set xrange [0:"+ (Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration) +"]");
				ps.println("set title \"" + experimentPrefix + " Archive Filled Bins\"");
				ps.println("set output \"" + fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.')) + ".pdf\"");
				String name = fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.'));
				ps.println("plot \"" + name + ".txt\" u 1:2 w linespoints t \"Total\"" + (cppnThenDirectLog != null ? ", \\" : ""));
				if(cppnThenDirectLog != null) { // Print CPPN and direct counts on same plot
					ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:2 w linespoints t \"CPPNs\", \\");
					ps.println("     \"" + name.replace("Fill", "cppnToDirect") + ".txt\" u 1:3 w linespoints t \"Vectors\"");
				}
				ps.close();
				
			} catch (FileNotFoundException e) {
				System.out.println("Could not create plot file: " + plot.getName());
				e.printStackTrace();
				System.exit(1);
			}
		}
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.iterations = Parameters.parameters.integerParameter("lastSavedGeneration");
		this.iterationsWithoutElite = 0; // Not accurate on resume		
	}
	
	/**
	 * Get the archive
	 * @return
	 */
	public Archive<T> getArchive() {
		return archive;
	}
	
	/**
	 * Fill the archive with a set number of random initial genotypes,
	 * according to where they best fit.
	 * @param example Starting genotype used to derive new instances
	 */
	@Override
	public void initialize(Genotype<T> example) {		
		if(iterations > 0) {
			int numLabels = archive.getBinMapping().binLabels().size();
			// Loading from saved archive
			String archiveDir = archive.getArchiveDirectory();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Load each elite from xml file into archive
			for(int i = 0; i < binLabels.size(); i++) {
				String binDir = archiveDir + "/" + binLabels.get(i) + "/";
				@SuppressWarnings("unchecked")
				Genotype<T> elite = (Genotype<T>) Easy.load(binDir + "elite.xml"); // Load genotype
				// Load behavior scores
				ArrayList<Double> scores = new ArrayList<Double>(numLabels); 
				try {
					Scanner scoresFile = new Scanner(new File(binDir + "scores.txt"));
					while(scoresFile.hasNextDouble()) {
						scores.add(scoresFile.nextDouble());
					}
					scoresFile.close();
				} catch (FileNotFoundException e) {
					System.out.println("Could not read " + binDir + "scores.txt");
					e.printStackTrace();
					System.exit(1);
				}
				// Package in a score
				Score<T> score = new Score<T>(elite, new double[0], scores);
				archive.archive.set(i, score); // Directly set the bin contents
			}
		} else {
			// Start from scratch
			int startSize = Parameters.parameters.integerParameter("mu");
			ArrayList<Genotype<T>> startingPopulation = PopulationUtil.initialPopulation(example, startSize);
			for(Genotype<T> g : startingPopulation) {
				Score<T> s = task.evaluate(g);
				archive.add(s); // Fill the archive with random starting individuals
			}	
		}
	}

	/**
	 * Write one line of data to each of the active log files, but only periodically,
	 * when number of iterations divisible by individualsPerGeneration. 
	 */
	private void log() {
		if(io && iterations % individualsPerGeneration == 0) {
			int numCPPN = 0;
			int numDirect = 0;
			// When all iterations were logged, the file got too large
			//log.log(iterations + "\t" + iterationsWithoutElite + "\t" + StringUtils.join(ArrayUtils.toObject(archive.getEliteScores()), "\t"));
			// Just log every "generation" instead
			Float[] elite = ArrayUtils.toObject(archive.getEliteScores());
			final int pseudoGeneration = iterations/individualsPerGeneration;
			archiveLog.log(pseudoGeneration + "\t" + StringUtils.join(elite, "\t"));

			// Exclude negative infinity to find out how many bins are filled
			final int numFilledBins = elite.length - ArrayUtil.countOccurrences(Float.NEGATIVE_INFINITY, elite);
			fillLog.log(pseudoGeneration + "\t" + numFilledBins);
			if(cppnThenDirectLog!=null) {
				Integer[] eliteProper = new Integer[elite.length];
				int i = 0;
				Vector<Score<T>> population = archive.archive;
				for(Score<T> p : population) {
					if(p == null || p.individual == null) eliteProper[i] = NUM_CODE_EMPTY; //if bin is empty
					else if(((CPPNOrDirectToGANGenotype) p.individual).getFirstForm()) {
						numCPPN++;
						eliteProper[i] = NUM_CODE_CPPN; //number for CPPN
					} else { // Assume first form is false
						assert !((CPPNOrDirectToGANGenotype) p.individual).getFirstForm();
						numDirect++;
						eliteProper[i] = NUM_CODE_DIRECT; //number for Direct
					}
					i++;
				}
				//in archive class, archive variable (vector)
				cppnThenDirectLog.log(pseudoGeneration+"\t"+numCPPN+"\t"+numDirect);
				cppnVsDirectFitnessLog.log(pseudoGeneration +"\t"+ StringUtils.join(eliteProper, "\t"));
			}
			
			// Special code for Lode Runner
			if(MMNEAT.task instanceof LodeRunnerLevelTask) {
				int numBeatenLevels = 0;
				for(Float x : elite) {
					// If A* fitness is used, then unbeatable levels have a score of -1 and thus won't be counted here.
					// If A*/Connectivity combo is used, then a connectivity percentage in (0,1) means the level is not beatable.
					// Score will only be greater than 1 if there is an actual A* path.
					if(x >= 1.0) {
						numBeatenLevels++;
					}
				}
				((LodeRunnerLevelTask<?>)MMNEAT.task).beatable.log(pseudoGeneration + "\t" + numBeatenLevels + "\t" + ((1.0*numBeatenLevels)/(1.0*numFilledBins)));
			}
		}
	}

	/**
	 * Create one (maybe two) new individuals by randomly
	 * sampling from the elites in random bins. The reason
	 * that two individuals may be added is if crossover occurs.
	 * In this case, both children can potentially be added 
	 * to the archive, and both trigger logging to file. This
	 * actually counts as 2 iterations.
	 */
	@Override
	public void newIndividual() {
		int index = archive.randomOccupiedBinIndex();
		Genotype<T> parent1 = archive.getElite(index).individual;
		long parentId1 = parent1.getId(); // Parent Id comes from original genome
		long parentId2 = NUM_CODE_EMPTY;
		Genotype<T> child1 = parent1.copy(); // Copy with different Id (will be further modified below)
		
		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			int otherIndex = archive.randomOccupiedBinIndex(); // From a different bin
			Genotype<T> parent2 = archive.getElite(otherIndex).individual;
			parentId2 = parent2.getId(); // Parent Id comes from original genome
			Genotype<T> child2 = parent2.copy(); // Copy with different Id (further modified below)
			
			// Replace child2 with a crossover result, and modify child1 in the process (two new children)
			child2 = child1.crossover(child2);
			child2.mutate(); // Probabilistic mutation of child
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child2);
			// Evaluate and add child to archive
			Score<T> s2 = task.evaluate(child2);
			// Indicate whether elite was added
			boolean child2WasElite = archive.add(s2);
			fileUpdates(child2WasElite); // Log for each individual produced
		}
		
		child1.mutate(); // Was potentially modified by crossover
		if (parentId2 == NUM_CODE_EMPTY) {
			EvolutionaryHistory.logLineageData(parentId1,child1);
		} else {
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child1);
		}
		// Evaluate and add child to archive
		Score<T> s1 = task.evaluate(child1);
		// Indicate whether elite was added
		boolean child1WasElite = archive.add(s1);
		fileUpdates(child1WasElite); // Log for each individual produced
	}
	
	/**
	 * Log data and update other data tracking variables.
	 * @param newEliteProduced Whether the latest individual was good enough to
	 * 							fill/replace a bin.
	 */
	public void fileUpdates(boolean newEliteProduced) {
		// Log to file
		log();
		Parameters.parameters.setInteger("lastSavedGeneration", iterations);
		// Track total iterations
		iterations++;
		// Track how long we have gone without producing a new elite individual
		if(newEliteProduced) {
			iterationsWithoutElite = 0;
		} else {
			iterationsWithoutElite++;
		}
		System.out.println(iterations + "\t" + iterationsWithoutElite + "\t");
		
	}
	
	/**
	 * Number of times new individuals have been 
	 * generated to add to archive.
	 */
	@Override
	public int currentIteration() {
		return iterations;
	}

	@Override
	public void finalCleanup() {
		task.finalCleanup();
	}

	/**
	 * Take members from archive and place them in an ArrayList
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.archive.size());
		for(Score<T> s : archive.archive) {
			if(s != null) { // Not all bins are filled
				result.add(s.individual);
			}
		}
		return result;
	}

	/**
	 * If iterationsWithoutElite is 0, then the last new individual
	 * was inserted into the population.
	 */
	@Override
	public boolean populationChanged() {
		return iterationsWithoutElite == 0;
	}
}
