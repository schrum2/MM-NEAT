package edu.southwestern.tasks.molecules;

import java.io.IOException;

import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
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
			try {
				// Should exit gracefully
				mutatorProcess.commSend("-1 -1");
			} catch (IOException e) {
				// Kill otherwise
				mutatorProcess.process.destroy(); 
			} 
			mutatorProcess = null;
		}
	}
	
	private MoleculeMutatorProcess() {
		super("SMILESMutate.exe");
	}
		
	public static void smilesMutation(SMILESStringGenotype smiles, int mutationNumber) {
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
		if(resultString.trim().contains(" ")) {
			System.out.println("The mutation process returned bad results");
			System.out.println("resultString = "+resultString);
			// Need more error information here
			System.exit(1);
		} else if(!resultString.equals("X")) {
			smiles.updateSMILESString(resultString);
		}
	}
	
//	public static void main(String[] args) {
//		Parameters.initializeParameterCollections(new String[0]);
//		String exampleSMILES = "C-C-N-C(=C)-O";
//		SMILESStringGenotype smiles = new SMILESStringGenotype(exampleSMILES);
//		
//		System.out.println("      Start: "+ exampleSMILES);
//		new SMILESChangeBondTypeMutation().mutate(smiles);
//		System.out.println("Change Bond: "+ smiles);
//		new SMILESInsertNewAtomMutation().mutate(smiles);
//		System.out.println("Insert Atom: "+ smiles);
//		new SMILESBranchNewAtomMutation().mutate(smiles);
//		System.out.println("Branch Atom: "+ smiles);
//		new SMILESDeleteAtomMutation().mutate(smiles);
//		System.out.println("Delete Atom: "+ smiles);
//		new SMILESChangeAtomTypeMutation().mutate(smiles);
//		System.out.println("Change Atom: "+ smiles);
//		new SMILESDeleteRingMutation().mutate(smiles);
//		System.out.println("Delete Ring: "+ smiles);
//		new SMILESAddRingMutation().mutate(smiles);
//		System.out.println("   Add Ring: "+ smiles);
//		
//		terminateMutatorProcess();
//	}
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[0]);
		SMILESStringGenotype smiles = (SMILESStringGenotype) new SMILESStringGenotype("").newInstance();
		System.out.println(smiles);
		for(int i = 0; i < 10; i++) {
			smiles.mutate();
		}
		
		Pair<Double, Double> pair = MoleculeMeltingAndBoilingPointProcess.smilesMeltingAndBoilingPoints(smiles);
		System.out.println(smiles.getPhenotype() + "\nmelting point: "+pair.t1+"\nboiling point: "+pair.t2);
		
		MoleculeMutatorProcess.terminateMutatorProcess();
		MoleculeMeltingAndBoilingPointProcess.terminateMelingBoilingPointProcess();
	}
}
