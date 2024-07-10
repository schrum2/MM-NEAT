package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Adds a ring to a SMILES string by connecting two random atoms.
 * In the string, the shared number 1 indicates the two link points.
 * Molecules can only have at most one ring (is this true?)
 * 
 * Example mutation:
 * C=C-N-O becomes C1=C-N-O1
 */
public class SMILESAddRingMutation extends SMILESMutation{
	public SMILESAddRingMutation() {
		super(Parameters.parameters.doubleParameter("smilesAddRingMutationRate"), SMILES_MUTATION_TYPE_ADD_RING);
	}
}
