package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * A SMILES string has a backbone that can contain branches.
 * This mutation specifically inserts a new atom along
 * the main backbone, between two existing atoms, or possibly
 * a new atom at the start or end, but not along a branch.
 */
public class SMILESInsertNewAtomMutation extends SMILESMutation{
	public SMILESInsertNewAtomMutation() {
		super(Parameters.parameters.doubleParameter("smilesInsertNewAtomMutationRate"), SMILES_MUTATION_TYPE_INSERT_NEW_ATOM);
	}
}
