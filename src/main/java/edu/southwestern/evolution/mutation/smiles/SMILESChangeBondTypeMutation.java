package edu.southwestern.evolution.mutation.smiles;

import edu.southwestern.parameters.Parameters;

/**
 * Mutates SMILES string by changing a random bond.
 * Could replace a double bond with a single bond
 * or a single bond with a double bond, in locations
 * where such a swap makes sense. Triple bonds are
 * also possible:
 * Single uses '-'
 * Double uses '='
 * Triple uses '#'
 * 
 * Example mutation:
 * C=C-N-O becomes C-C-N-O
 */
public class SMILESChangeBondTypeMutation extends SMILESMutation {

	public SMILESChangeBondTypeMutation() {
		super(Parameters.parameters.doubleParameter("smilesBondTypeMutationRate"), SMILES_MUTATION_TYPE_CHANGE_BOND_TYPE);
	}

}
