package edu.southwestern.tasks.molecules;

import java.io.IOException;

import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Runs a Fortran program that performs mutations on SMILES strings.
 */
public class MoleculeMutatorProcess extends MoleculeProcess {

	private static MoleculeMutatorProcess mutatorProcess;
	
	private static synchronized MoleculeMutatorProcess getMoleculeMutatorProcess() {
		if(mutatorProcess == null) {
			mutatorProcess = new MoleculeMutatorProcess();
			mutatorProcess.start();
			
			try {
				// Send two random seeds that are themselves random numbers (change to be parameters?)
				mutatorProcess.commSend(RandomNumbers.randomGenerator.nextInt()+" "+RandomNumbers.randomGenerator.nextInt());
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Mutator process initialization failed to accept random seeds");
				System.exit(1);
			} // Two random seeds
		}
		return mutatorProcess;
	}
	
	public static void terminateMutatorProcess() {
		if(mutatorProcess != null) {
			mutatorProcess.process.destroy();
			mutatorProcess = null;
		}
	}
	
	private MoleculeMutatorProcess() {
		super("SMILESMutate.exe");
	}

	public static final int SMILES_MUTATION_TYPE_CHANGE_BOND_TYPE = 1;
	public static final int SMILES_MUTATION_TYPE_INSERT_NEW_ATOM = 2;
	public static final int SMILES_MUTATION_TYPE_BRANCH_NEW_ATOM = 3;
	public static final int SMILES_MUTATION_TYPE_DELETE_ATOM = 4;
	public static final int SMILES_MUTATION_TYPE_CHANGE_ATOM_TYPE = 5;
	public static final int SMILES_MUTATION_TYPE_DELETE_RING = 6;
	public static final int SMILES_MUTATION_TYPE_ADD_RING = 7;

	public static SMILESStringGenotype smilesMutationChangeBondType(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_CHANGE_BOND_TYPE);
	}
	
	public static SMILESStringGenotype smilesMutationInsertNewAtom(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_INSERT_NEW_ATOM);
	}
	
	public static SMILESStringGenotype smilesMutationBranchNewAtom(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_BRANCH_NEW_ATOM);
	}
	
	public static SMILESStringGenotype smilesMutationDeleteAtom(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_DELETE_ATOM);
	}
	
	public static SMILESStringGenotype smilesMutationChangeAtomType(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_CHANGE_ATOM_TYPE);
	}
	
	public static SMILESStringGenotype smilesMutationDeleteRing(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_DELETE_RING);
	}
	
	public static SMILESStringGenotype smilesMutationAddRing(SMILESStringGenotype smiles) {
		return smilesMutation(smiles,SMILES_MUTATION_TYPE_ADD_RING);
	}
	
	private static SMILESStringGenotype smilesMutation(SMILESStringGenotype smiles, int mutationNumber) {
		MoleculeMutatorProcess temp = getMoleculeMutatorProcess();
		String smilesString = smiles.getPhenotype();
		try {
			temp.commSend(smilesString.length()+" "+mutationNumber);
			temp.commSend(smilesString);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Mutator process failed for type "+mutationNumber+" on string "+smilesString);
			System.exit(1);
		}    // Input string length and mutation type
		String resultString = temp.commRecv();
		return new SMILESStringGenotype(resultString);
	}
	
	public static void main(String[] args) {
		String exampleSMILES = "C1-C-N-C(=C1)-O";
		SMILESStringGenotype smiles = new SMILESStringGenotype(exampleSMILES);
		
		System.out.println(smilesMutationChangeBondType(smiles));
		System.out.println(smilesMutationInsertNewAtom(smiles));
		System.out.println(smilesMutationBranchNewAtom(smiles));
		System.out.println(smilesMutationDeleteAtom(smiles));
		System.out.println(smilesMutationChangeAtomType(smiles));
		System.out.println(smilesMutationDeleteRing(smiles));
		System.out.println(smilesMutationAddRing(smiles));
		
		terminateMutatorProcess();
	}
}
