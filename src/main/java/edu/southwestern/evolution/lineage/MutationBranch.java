package edu.southwestern.evolution.lineage;

public class MutationBranch {

	Offspring o;
	MutationBranch parent1;
	MutationBranch parent2;

	public MutationBranch(Offspring o, MutationBranch parent1) {
		this(o, parent1, null);
	}

	public MutationBranch(Offspring o, MutationBranch parent1, MutationBranch parent2) {
		this.o = o;
		this.parent1 = parent1;
		this.parent2 = parent2;
	}

	@Override
	public String toString() {
		String result = "";
		if (parent1 != null) {
			result += "(" + parent1 + ") ";
		}
		if (parent2 != null) {
			result += "X (" + parent2 + ") ";
		}
		if (parent1 != null) {
			result += "-> ";
		}
		result += o.offspringId + ":" + o.mutations.toString();
		return result;
	}
}
