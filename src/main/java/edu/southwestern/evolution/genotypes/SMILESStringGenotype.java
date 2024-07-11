package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.mutation.smiles.*;

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
	
	private static ArrayList<SMILESMutation> mutationOperators;
	
	static {
		mutationOperators = new ArrayList<>(NUM_SMILES_MUTATION_TYPES);
		mutationOperators.add(new SMILESChangeBondTypeMutation());
		mutationOperators.add(new SMILESInsertNewAtomMutation());
		mutationOperators.add(new SMILESBranchNewAtomMutation());
		mutationOperators.add(new SMILESDeleteAtomMutation());
		mutationOperators.add(new SMILESChangeAtomTypeMutation());
		mutationOperators.add(new SMILESDeleteRingMutation());
		mutationOperators.add(new SMILESAddRingMutation());
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

		for(SMILESMutation mut : mutationOperators) {
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

	@Override
	public Genotype<String> newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getId() {
		return id;
	}

	public String toString() {
		return smilesString;
	}
}
