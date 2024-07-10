package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Deletes a ring from a SMILES string, if there is one
 */
public class SMILESDeleteRingMutation extends SMILESMutation{
	public SMILESDeleteRingMutation() {
		super(Parameters.parameters.doubleParameter("smilesDeleteRingMutationRate"), SMILES_MUTATION_TYPE_DELETE_RING);
	}
}
