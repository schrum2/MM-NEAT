package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.EvolutionaryHistory;

import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.MLP;
import edu.southwestern.parameters.Parameters;

/**
 * Represents a Multi-Layer Perceptron as two 2D arrays of link weights. MLP has
 * standard layout of a single hidden layer between input and output layers.
 *
 * @author Jacob Schrum
 */
public class MLPGenotype implements Genotype<MLP> {

	private long id = EvolutionaryHistory.nextGenotypeId();
	 // weight from each input to each hidden node
	public double[][] firstConnectionLayer;
	// weight from each hidden node to each output node
	public double[][] secondConnectionLayer; 

	/**
	 * Default constructor
	 */
	public MLPGenotype() {
		this(MMNEAT.networkInputs, Parameters.parameters.integerParameter("hiddenMLPNeurons"), MMNEAT.networkOutputs);
	}

	/**
	 * Constructor
	 * @param numberOfInputs num inputs
	 * @param numberOfHidden num hidden
	 * @param numberOfOutputs num outputs
	 */
	public MLPGenotype(int numberOfInputs, int numberOfHidden, int numberOfOutputs) {
		this(new MLP(numberOfInputs, numberOfHidden, numberOfOutputs));
	}

	/**
	 * Constructor
	 * copies another MLP genotype
	 * @param mlp another mlp genotype
	 */
	public MLPGenotype(MLP mlp) {
		MLP mlpCopy = mlp.copy();
		firstConnectionLayer = mlpCopy.firstConnectionLayer;
		secondConnectionLayer = mlpCopy.secondConnectionLayer;
	}

	/**
	 * copies mlp genotype
	 */
	public Genotype<MLP> copy() {
		return new MLPGenotype(this.getPhenotype().copy());
	}

	/**
	 * Directly from Togelius' code
	 */
	public void mutate() {
		mutate(firstConnectionLayer);
		mutate(secondConnectionLayer);
	}

	/**
	 * mutates connection layer
	 * @param array layer to mutate
	 */
	protected void mutate(double[][] array) {
		for (int i = 0; i < array.length; i++) {
			mutate(array[i]);
		}
	}

	/**
	 * mutates one side of connection layer
	 * @param array layer
	 */
	protected void mutate(double[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] += MMNEAT.weightPerturber.randomOutput()
					* Parameters.parameters.doubleParameter("mlpMutationRate");
		}
	}

	/**
	 * crossover two MLPs
	 * @param g other genotype to cross
	 */
	@SuppressWarnings("unchecked")
	public Genotype<MLP> crossover(Genotype<MLP> g) {
		return MMNEAT.crossoverOperator.crossover(this, g);
	}

	/**
	 * returns MlP from given genotype
	 */
	public MLP getPhenotype() {
		return new MLP(this.firstConnectionLayer, this.secondConnectionLayer);
	}

	/**
	 * creates a new instance of MLPGenotype that is a copy
	 */
	public Genotype<MLP> newInstance() {
		return new MLPGenotype(new MLP(this.firstConnectionLayer.length, this.secondConnectionLayer.length,
				this.secondConnectionLayer[0].length));
	}

	/**
	 * returns id of MLP
	 */
	public long getId() {
		return id;
	}
	
	transient List<Long> parents = new LinkedList<Long>();
	
	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return parents;
	}

}
