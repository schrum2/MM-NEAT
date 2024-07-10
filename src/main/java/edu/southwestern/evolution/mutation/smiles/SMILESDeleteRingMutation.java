package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Deletes a ring from a SMILES string, if there is one.
 * This means that the "1" characters indicating the connected
 * atoms is removed from two locations.
 * 
 * Example mutation:
 * C1=C-N-O1 becomes C=C-N-O
 */
public class SMILESDeleteRingMutation extends SMILESMutation{
	public SMILESDeleteRingMutation() {
		super(Parameters.parameters.doubleParameter("smilesDeleteRingMutationRate"), SMILES_MUTATION_TYPE_DELETE_RING);
	}
}
