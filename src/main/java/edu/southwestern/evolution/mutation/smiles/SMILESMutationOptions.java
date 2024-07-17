package edu.southwestern.evolution.mutation.smiles;

import java.util.ArrayList;

public class SMILESMutationOptions {

	private static final int NUM_SMILES_MUTATION_TYPES = 7;

	private static transient ArrayList<SMILESMutation> mutationOperators = null;
	
	public static ArrayList<SMILESMutation> getMutationOperators() {
		if(mutationOperators == null) {
			mutationOperators = new ArrayList<>(NUM_SMILES_MUTATION_TYPES);
			mutationOperators.add(new SMILESChangeBondTypeMutation());
			mutationOperators.add(new SMILESInsertNewAtomMutation());
			mutationOperators.add(new SMILESBranchNewAtomMutation());
			mutationOperators.add(new SMILESDeleteAtomMutation());
			mutationOperators.add(new SMILESChangeAtomTypeMutation());
			mutationOperators.add(new SMILESDeleteRingMutation());
			mutationOperators.add(new SMILESAddRingMutation());
		} 
		return mutationOperators;
	}
}
