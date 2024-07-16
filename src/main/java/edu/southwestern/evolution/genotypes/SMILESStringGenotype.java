package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.mutation.smiles.SMILESAddRingMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESBranchNewAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESChangeAtomTypeMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESChangeBondTypeMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESDeleteAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESDeleteRingMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESInsertNewAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESMutation;
import edu.southwestern.util.random.RandomNumbers;

/**
 * A SMILES string is a representation for molecules:
 * SMILES = Simplified Molecular-Input Line-Entry System
 * All mutations are based on an external Fortran program
 * developed by Steve Alexander.
 * 
 * Some notes on SMILES string formatting from Alexander's paper:
 * Generating Molecules with Specific Boiling Points and Melting Points: Acyclic Molecules
 * MATCH Commun. Math. Comput. Chem.
 * 
 * Strings can only contain C = carbon, O = oxygen, and N = nitrogen.
 * Hydrogen is also present in the compounds, but are implicit in SMILES strings.
 * "The implicit number of hydrogen atoms attached to other atoms is the
 * difference between the atom’s valence and the number of bonds assigned
 * to the atom."
 * Single, double, and triple bonds are represented with -, =, and # respectively
 * Parentheses are for branches:
 * "These parentheses are placed directly after the symbol for the atom on the 
 * main sequence to which it is connected."
 */
public class SMILESStringGenotype implements Genotype<String> {

	private static final int NUM_SMILES_MUTATION_TYPES = 7;
	private String smilesString;
	private ArrayList<Long> parents;
	private long id = EvolutionaryHistory.nextGenotypeId();
	
	private static transient ArrayList<SMILESMutation> mutationOperators = null;
	
	private static ArrayList<SMILESMutation> getMutationOperators() {
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
	
	public SMILESStringGenotype() {
		this(staticNewSMILESString());
	}
	
	public SMILESStringGenotype(String smiles) {
		smilesString = smiles;
		parents = new ArrayList<Long>();
	}
	
	public void updateSMILESString(String newString) {
		smilesString = newString;
	}
	
	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return parents;
	}

	@Override
	public Genotype<String> copy() {
		return new SMILESStringGenotype(smilesString);
	}

	@Override
	public void mutate() {

		StringBuilder sb = new StringBuilder();
		sb.append(this.getId());
		sb.append(" ");

		for(SMILESMutation mut : getMutationOperators()) {
			mut.go(this, sb);
		}
	}

	@Override
	public Genotype<String> crossover(Genotype<String> g) {
		throw new UnsupportedOperationException("No crossover for SMILES strings yet");
	}

	@Override
	public String getPhenotype() {
		return smilesString;
	}

	// Make these be parameters
	private static final int MIN_STARTING_ATOMS = 2;
	private static final int MAX_STARTING_ATOMS = 7;
	
	@Override
	public Genotype<String> newInstance() {
		return new SMILESStringGenotype(staticNewSMILESString());
	}

	/**
	 * New SMILES strings consist only of a random number of C atoms
	 * with single bonds, so mutations are needed to explore the 
	 * addition of more interesting structures and atoms.
	 */
	private static String staticNewSMILESString() {
		StringBuilder str = new StringBuilder();
		int cCount = RandomNumbers.randomGenerator.nextInt((1 + MAX_STARTING_ATOMS) - MIN_STARTING_ATOMS) + MIN_STARTING_ATOMS;
		str.append("C");
		for(int i = 1; i < cCount; i++) {
			str.append("-C");
		}
		return str.toString();
	}

	@Override
	public long getId() {
		return id;
	}

	public String toString() {
		return getId() + ":" + smilesString;
	}
	
}
