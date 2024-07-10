package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.tasks.molecules.MoleculeMutatorProcess;
import edu.southwestern.util.random.RandomNumbers;

public class SMILESMutation extends Mutation<String> {

	public static final int SMILES_MUTATION_TYPE_CHANGE_BOND_TYPE = 1;
	public static final int SMILES_MUTATION_TYPE_INSERT_NEW_ATOM = 2;
	public static final int SMILES_MUTATION_TYPE_BRANCH_NEW_ATOM = 3;
	public static final int SMILES_MUTATION_TYPE_DELETE_ATOM = 4;
	public static final int SMILES_MUTATION_TYPE_CHANGE_ATOM_TYPE = 5;
	public static final int SMILES_MUTATION_TYPE_DELETE_RING = 6;
	public static final int SMILES_MUTATION_TYPE_ADD_RING = 7;
	
	private double mutationRate;
	private int mutationType;
	
	public SMILESMutation(double rate, int type) {
		mutationRate = rate;
		mutationType = type;
	}
	
	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < mutationRate);
	}
	
	@Override
	public void mutate(Genotype<String> genotype) {
		MoleculeMutatorProcess.smilesMutation((SMILESStringGenotype) genotype, mutationType);
	}
	
	
}
