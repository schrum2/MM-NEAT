package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.CMAME;
import edu.southwestern.log.MMNEATLog;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public abstract class Emitter implements Comparable<Emitter> {
	
	public int solutionCount = 0; // amount of solutions found by this emitter
	int populationCounter; // counter for getting the next solution from the population
	public int additionCounter; // amount of parents currently stored, to be used in distribution update
	int validParents = 0; // amount of non-failure valued parents, for calculating new mu and weights
	double[][] parentPopulation = null; // stored parents
	double[][] sampledPopulation = null; // current population to pull individuals from
	double[] deltaIFitnesses = null;
	public String emitterName; // name of the emitter
	CMAEvolutionStrategy CMAESInstance; // internal instance of CMA-ES 
	final int populationSize;
	final int dimension;
	public MMNEATLog individualLog;
	
	
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
		this.emitterName = getEmitterSuffix() + " Emitter " + id; 
		this.individualLog = new MMNEATLog(emitterName.replace(" ", ""), false, false, false, true);
		this.populationSize = CMAESInstance.parameters.getPopulationSize();
		parentPopulation = new double[populationSize][CMAESInstance.getDimension()];
		deltaIFitnesses = new double[populationSize];
		
	}

	
	/**
	 * Get the suffix for the emitter, depends on the type;
	 * (Improvement, Optimization, etc)
	 * 
	 * @return Emitter suffix
	 */
	protected abstract String getEmitterSuffix();
	
	
	/**
	 * Gets a new CMA-ES instance, a different starting location 
	 * may be needed to be calculated depending on the type of
	 * emitter
	 * 
	 * @param archive
	 * @return
	 */
	protected abstract CMAEvolutionStrategy newCMAESInstance(Archive<ArrayList<Double>> archive); 
	
	
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
		deltaIFitnesses[additionCounter] = calculateFitness(newScore, currentScore);
		parentPopulation[additionCounter] = parent;
		additionCounter++;
		if (additionCounter == populationSize) {
			if (allInvalid()) {
				if ( ((CMAME) MMNEAT.ea).io ) {
					((CMAME) MMNEAT.ea).updateEmitterLog(individualLog, 0); // log a zero
				}
				this.CMAESInstance = newCMAESInstance(archive);
			} else {
				updateDistribution(parentPopulation, deltaIFitnesses); // logging happens inside this function
			}
			additionCounter = 0;
		}
	}
	
	public abstract double calculateFitness(double newScore, double currentScore);
	
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
		populationCounter++;
		return newIndividual; 
	}
}
