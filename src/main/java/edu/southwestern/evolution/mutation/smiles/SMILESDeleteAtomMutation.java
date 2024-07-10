package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Delete an atom from a SMILES string, and its connecting bond.
 * 
 * Example mutation:
 * C=C-N-O becomes C=C-O
 */
public class SMILESDeleteAtomMutation extends SMILESMutation{
	public SMILESDeleteAtomMutation() {
		super(Parameters.parameters.doubleParameter("smilesDeleteAtomMutationRate"), SMILES_MUTATION_TYPE_DELETE_ATOM);
	}
}
