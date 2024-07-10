package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Adds a ring to a SMILES string
 */
public class SMILESAddRingMutation extends SMILESMutation{
	public SMILESAddRingMutation() {
		super(Parameters.parameters.doubleParameter("smilesAddRingMutationRate"), SMILES_MUTATION_TYPE_ADD_RING);
	}
}
