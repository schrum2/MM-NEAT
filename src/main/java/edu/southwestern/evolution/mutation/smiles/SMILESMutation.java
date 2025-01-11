package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.molecules.SmilesMutator;
import edu.southwestern.tasks.molecules.SmilesMutator.MutationType;
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
		//MoleculeMutatorProcess.smilesMutation((SMILESStringGenotype) genotype, mutationType);
		String smilesString = genotype.getPhenotype();
		String modified = SmilesMutator.mutate(smilesString, MutationType.values()[mutationType - 1]);
		if(!modified.equals("X")) {
			((SMILESStringGenotype) genotype).updateSMILESString(modified);
		}
	}
	
	public static void main(String[] args) {
		for(int i = 0; i < 1000; i++) {
			System.out.println("Attempt " + i);
			
			Parameters.initializeParameterCollections(new String[0]);
			String exampleSMILES = "C-C-N-C(=C)-O";
			SMILESStringGenotype smiles = new SMILESStringGenotype(exampleSMILES);
			
			System.out.println("      Start: "+ exampleSMILES);
			new SMILESChangeBondTypeMutation().mutate(smiles);
			System.out.println("Change Bond: "+ smiles);
			new SMILESInsertNewAtomMutation().mutate(smiles);
			System.out.println("Insert Atom: "+ smiles);
			new SMILESBranchNewAtomMutation().mutate(smiles);
			System.out.println("Branch Atom: "+ smiles);
			new SMILESDeleteAtomMutation().mutate(smiles);
			System.out.println("Delete Atom: "+ smiles);
			new SMILESChangeAtomTypeMutation().mutate(smiles);  // Is this the problem?
			System.out.println("Change Atom: "+ smiles);
			new SMILESDeleteRingMutation().mutate(smiles);
			System.out.println("Delete Ring: "+ smiles);
			new SMILESAddRingMutation().mutate(smiles);
			System.out.println("   Add Ring: "+ smiles);
			
		}
	}
}
