package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Inserts a new atom on a branch off of the main backbone.
 * This could mean creating a branch where there is none, 
 * or adding to an existing branch (is this second claim accurate?)
 * New atom must be C, N, or O.
 * 
 * Example mutation:
 * C=C-N-O becomes C=C-N(-O)-O
 */
public class SMILESBranchNewAtomMutation extends SMILESMutation{
	public SMILESBranchNewAtomMutation() {
		super(Parameters.parameters.doubleParameter("smilesBranchNewAtomMutationRate"), SMILES_MUTATION_TYPE_BRANCH_NEW_ATOM);
	}
}
