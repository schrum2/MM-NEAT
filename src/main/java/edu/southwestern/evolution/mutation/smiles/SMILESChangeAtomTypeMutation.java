package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Mutates SMILES string by changing a random atom
 * to one of the other available atoms. C, O, N are allowed.
 * 
 * Example mutation:
 * C=C-N-O becomes C=C-N-C
 */
public class SMILESChangeAtomTypeMutation extends SMILESMutation {

	public SMILESChangeAtomTypeMutation() {
		super(Parameters.parameters.doubleParameter("smilesAtomTypeMutationRate"), SMILES_MUTATION_TYPE_CHANGE_ATOM_TYPE);
	}

}
