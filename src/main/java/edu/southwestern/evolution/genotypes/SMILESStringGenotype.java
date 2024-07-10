package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mutation.smiles.*;

/**
 * A SMILES string is a representation for molecules:
 * SMILES = Simplified Molecular-Input Line-Entry System
 * All mutations are based on an external Fortran program
 * developed by Steve Alexander.
 */
public class SMILESStringGenotype implements Genotype<String> {

	private static final int NUM_SMILES_MUTATION_TYPES = 7;
	private String smilesString;
	private ArrayList<Long> parents;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	public String toString() {
		return smilesString;
	}
}
