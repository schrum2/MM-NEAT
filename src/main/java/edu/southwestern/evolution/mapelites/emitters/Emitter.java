package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.CMAME;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

/**
 * Basic abstract emitter class which emitters
 * should extend with their own calculateFitness
 * methods. Emitters are described in this paper:
 * https://arxiv.org/pdf/1912.02400.pdf
 * and implemented in python with pyribs:
 * https://docs.pyribs.org/en/stable/api/ribs.emitters.html
 * 
 * @author Maxx Batterton
 */
public abstract class Emitter implements Comparable<Emitter> {
	
	public int solutionCount = 0; // amount of solutions found by this emitter
	int populationCounter; // counter for getting the next solution from the population
	public int additionCounter; // amount of parents currently stored, to be used in distribution update
	int validParents = 0; // amount of non-failure valued parents, for calculating new mu and weights
	double[][] parentPopulation = null; // stored parents
	double[][] sampledPopulation = null; // current population to pull individuals from
	private double[] deltaIFitnesses = null;
	ArrayList<Pair<Double,SOLUTION_TYPE>> fitnessTypePairs = null;
	public String emitterName; // name of the emitter
	CMAEvolutionStrategy CMAESInstance; // internal instance of CMA-ES 
	final int populationSize;
	final int dimension;
	public MMNEATLog individualLog;
	
	/*
	 * This Enum serves as a way to sort solutions and prioritize 
	 * certain solutions over others. With an improvement emitter,
	 * new bins are prioritized over improved bins. With an 
	 * optimizing emitter, SUCCESSFUL_OPTIMIZING has equal priority,
	 * and for all emitters, FAILURE solutions are last
	 */
	public enum SOLUTION_TYPE {NEW_BIN, IMPROVED_BIN, FAILURE, SUCCESSFUL_OPTIMIZING};
	
	/**
	 * Constructor that creates a new emitter
	 * 
	 * @param dimension The Dimension of the internal CMA-ES instance
	 * @param archive The archive being used, to sample from
	 * @param name The display  
	 */
	public Emitter(int dimension, Archive<ArrayList<Double>> archive, int id) {
		this.dimension = dimension;
		this.CMAESInstance = newCMAESInstance(archive);
		this.emitterName = getEmitterPrefix() + " Emitter " + id; 
		this.individualLog = new MMNEATLog(emitterName.replace(" ", ""), false, false, false, true);
		this.populationSize = CMAESInstance.parameters.getPopulationSize();
		parentPopulation = new double[populationSize][CMAESInstance.getDimension()];
		deltaIFitnesses = new double[populationSize];
		fitnessTypePairs = new ArrayList<Pair<Double,SOLUTION_TYPE>>(populationSize);
		
	}

	
	/**
	 * Get the prefix for the emitter, depends on the type;
	 * (Improvement, Optimization, etc)
	 * 
	 * @return Emitter suffix
	 */
	protected abstract String getEmitterPrefix();
	
	
	/**
	 * Gets a new CMA-ES instance, a different starting location 
	 * may be needed to be calculated depending on the type of
	 * emitter
	 * 
	 * @param archive
	 * @return
	 */
	protected CMAEvolutionStrategy newCMAESInstance(Archive<ArrayList<Double>> archive) {
		CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
		optEmitter.setDimension(dimension);
		Genotype<ArrayList<Double>> elite = archive.getElite(archive.randomOccupiedBinIndex()).individual;
		double[] phenod = ArrayUtil.doubleArrayFromList(elite.getPhenotype());
		optEmitter.setInitialX(phenod); // start at random bin
		optEmitter.setInitialStandardDeviation(Parameters.parameters.doubleParameter("CMAMESigma")); // unsure if should be hardcoded or not
		int lambda = Parameters.parameters.integerParameter("lambda"); 
		// Realized that mu = lambda / 2 after extensively reviewing the pyribs code and walking through with Amy Hoover.
		// Even in pyribs, there is apparently an option for mu to be set differently, but it is definitely fixed, and
		// the default is for it to be half of lambda.
		int mu = lambda/2; 
		optEmitter.parameters.setMu(mu);
		optEmitter.parameters.setPopulationSize(lambda);
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
		return optEmitter;
	}
	
	/**
	 * Update the internal CMA-ES instance distribution
	 * 
	 * @param parentPopulation2 Parents to be given to CMA-ES
	 * @param deltaI Fitness values corresponding to the parents
	 */
	protected void updateDistribution(double[][] parentPopulation2, double[] deltaI) {
//		CMAESInstance.parameters.unsafeUnlock(); // unlock parameters
		if ( ((CMAME) MMNEAT.ea).io ) {
			((CMAME) MMNEAT.ea).updateEmitterLog(individualLog, validParents); // log valid parents
		}
		// Old Parameter locking/unlocking testing with mu
//		CMAESInstance.parameters.setMu(validParents);
//		if (CMAME.PRINT_DEBUG) System.out.println("Changed mu to "+validParents+" with an unsafe unlock/lock");
//		CMAESInstance.parameters.setWeights(validParents, CMAESInstance.parameters.getRecombinationType());
//		if (CMAME.PRINT_DEBUG) System.out.println("Set weights with mu:"+validParents+" and Recombination type \""+CMAESInstance.parameters.getRecombinationType()+"\" with an unsafe unlock/lock");
//		
//		CMAESInstance.parameters.unsafeLock(); // relock parameters
		validParents = 0; // reset valid parents
		CMAESInstance.updateDistribution(parentPopulation2, deltaI);	
	}

	
	/**
	 * Add a parent and corresponding fitness to the current set
	 * of parents and fitnesses, and/or update the distribution 
	 * if enough parents have been generated
	 * 
	 * @param parent The parent to be added
	 * @param newScore The fitness of the parent to be added (higher is better)
	 * @param currentScore The fitness of current bin occupant (higher is better)
	 * @param archive The current archive
	 */
	public void addFitness(double[] parent, double newScore, double currentScore, Archive<ArrayList<Double>> archive) {
		fitnessTypePairs.add(calculateFitness(newScore, currentScore));
		//deltaIFitnesses[additionCounter] = calculateFitness(newScore, currentScore);
		parentPopulation[additionCounter] = parent;
		additionCounter++;
		if (additionCounter == populationSize) {
			if (allInvalid()) {
				if ( ((CMAME) MMNEAT.ea).io ) {
					((CMAME) MMNEAT.ea).updateEmitterLog(individualLog, 0); // log a zero
				}
				this.CMAESInstance = newCMAESInstance(archive);
				fitnessTypePairs.clear(); // Empty out for next time.
			} else {
				int i = 0;
				double minEncountered = Double.POSITIVE_INFINITY;
				double maxEncountered = Double.NEGATIVE_INFINITY;
				// Collect the data and determine score range (min/max)
				for(Pair<Double,SOLUTION_TYPE> p : fitnessTypePairs) {
					minEncountered = Math.min(minEncountered, p.t1);
					maxEncountered = Math.max(maxEncountered, p.t1);
					deltaIFitnesses[i++] = p.t1;
				}
				double badRange = maxEncountered - minEncountered; // Will be positive
				badRange++; // Make slightly larger/worse (large values bad for minimizer)
				// Now augment the FAILURE and IMPROVED_BIN scores to rank them worse 
				for(i = 0; i < deltaIFitnesses.length; i++) {
					if(fitnessTypePairs.get(i).t2.equals(SOLUTION_TYPE.IMPROVED_BIN)) {
						deltaIFitnesses[i] += badRange; // Worse than the raw score of any NEW_BIN solution 
					} else if(fitnessTypePairs.get(i).t2.equals(SOLUTION_TYPE.FAILURE)) {
						deltaIFitnesses[i] += 2*badRange; // Worse than adjusted score of any IMPROVED_BIN solution
					}
				}
				fitnessTypePairs.clear(); // Empty out for next time.
				updateDistribution(parentPopulation, deltaIFitnesses); // logging happens inside this function
			}
			additionCounter = 0;
		}
	}
	
	public abstract Pair<Double,SOLUTION_TYPE> calculateFitness(double newScore, double currentScore);
	
	/**
	 * Check if the current fitnesses are invalid, and restart the 
	 * emitter from a new location if so
	 * 
	 * @return True if all fitnesses are the CMA-ME failure value, false otherwise
	 */
	protected boolean allInvalid() {
		return validParents == 0;
	}
	

	/**
	 * Sample the internal CMA-ES instance
	 * 
	 * @return New sampled population
	 */
	private double[][] samplePopulation() {
		return CMAESInstance.samplePopulation();
	}
	

	@Override
	public int compareTo(Emitter other) {
		return this.solutionCount - other.solutionCount;
	}
	
	
	/**
	 * Reset the sampled population and counter
	 */
	public void resetSample() {
		sampledPopulation = this.samplePopulation();
		populationCounter = 0;
	}
	

	/**
	 * Get the next sampled individual from the sample population, 
	 * or generate new ones.
	 * 
	 * @return The next sampled individual
	 */
	public double[] sampleSingle() {
		if (sampledPopulation == null || (sampledPopulation.length-1) < populationCounter) { // at start or when counter is above
			resetSample();
		}
		double[] newIndividual = sampledPopulation[populationCounter];
		// Infeasible solutions are not evaluated, and can thus be replaced without counting toward evaluation cost
		if(Parameters.parameters.booleanParameter("resampleBadCMAMEGenomes")) {
			while(!BoundedRealValuedGenotype.isBounded(newIndividual)) { // Try to replace as long as outside bounds
				newIndividual = CMAESInstance.resampleSingle(populationCounter);
			}
		}
		populationCounter++;
		return newIndividual; 
	}
	
	public double[] getMean() {
		return CMAESInstance.getMeanX();
	}
}
