package edu.southwestern.tasks.molecules;

import java.io.IOException;
import java.util.Stack;

import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.evolution.mutation.smiles.SMILESAddRingMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESBranchNewAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESChangeAtomTypeMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESChangeBondTypeMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESDeleteAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESDeleteRingMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESInsertNewAtomMutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Runs a Fortran program that performs mutations on SMILES strings.
 */
public class MoleculeMutatorProcess extends MoleculeProcess {

	private static MoleculeMutatorProcess mutatorProcess;
	private static final boolean DEBUG = true;
	private static final int RANDOM_SEED_CEILING = 256; // Large numbers could cause problems
	
	private static synchronized MoleculeMutatorProcess getMoleculeMutatorProcess() {
		if(mutatorProcess == null) {
			mutatorProcess = new MoleculeMutatorProcess();
			mutatorProcess.start();
			
			try {
				// Send two random seeds that are themselves random numbers (change to be parameters?)
				String seeds = RandomNumbers.randomGenerator.nextInt(RANDOM_SEED_CEILING)+" "+RandomNumbers.randomGenerator.nextInt(RANDOM_SEED_CEILING);
				if(DEBUG) System.out.println("MoleculeMutatorProcess random seeds are: "+seeds);
				mutatorProcess.commSend(seeds);
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
		
	public static synchronized void smilesMutation(SMILESStringGenotype smiles, int mutationNumber) {
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
			String normalized = normalizeSMILESString(resultString);
			smiles.updateSMILESString(normalized);
		}
	}
	
	/**
	 * Remove unnecessary branches that can result in multiple
	 * representations of the same molecule.
	 * 
	 * @param input Valid SMILES String
	 * @return SMILES string with redundancies removed
	 */
	public static String normalizeSMILESString(String input) {
		
        // Stack to keep track of the indices of opening parentheses
        Stack<Integer> stack = new Stack<>();
        
        // StringBuilder to build the resulting string
        StringBuilder result = new StringBuilder(input);
        
        // Iterate over the input string
        for (int i = 0; i < result.length(); i++) {
            char ch = result.charAt(i);
            
            if (ch == '(') {
                // Push the index of the opening parenthesis onto the stack
                stack.push(i);
            } else if (ch == ')') {
                if (!stack.isEmpty()) {
                    // Pop the matching opening parenthesis index
                    int openIndex = stack.pop();
                    
                    // Check if the next character is also a closing parenthesis, or if at end of string
                    if (i + 1 == result.length() || result.charAt(i + 1) == ')') {
                        // Remove the matched pair and the current closing parenthesis
                        result.deleteCharAt(i); // Remove the first closing parenthesis
                        result.deleteCharAt(openIndex); // Remove the matching opening parenthesis
                        i--; // Adjust the index after removal
                    } 
                }
            }
        }
        
        // Convert the StringBuilder back to a string
        return result.toString();
	}

	public static void main(String[] args) {
		for(int i = 0; i < 1000; i++) {
			System.out.println("Attempt " + i);
			
			Parameters.initializeParameterCollections(new String[0]);
			String exampleSMILES = "C-C-N-C(=C)-O";
			SMILESStringGenotype smiles = new SMILESStringGenotype(exampleSMILES);
			
			System.out.println("      Start: "+ exampleSMILES);
			new SMILESChangeBondTypeMutation().mutate(smiles);
			System.out.println("Change Bond: "+ smiles);
			new SMILESInsertNewAtomMutation().mutate(smiles);
			System.out.println("Insert Atom: "+ smiles);
			new SMILESBranchNewAtomMutation().mutate(smiles);
			System.out.println("Branch Atom: "+ smiles);
			new SMILESDeleteAtomMutation().mutate(smiles);
			System.out.println("Delete Atom: "+ smiles);
			new SMILESChangeAtomTypeMutation().mutate(smiles);  // Is this the problem?
			System.out.println("Change Atom: "+ smiles);
			new SMILESDeleteRingMutation().mutate(smiles);
			System.out.println("Delete Ring: "+ smiles);
			new SMILESAddRingMutation().mutate(smiles);
			System.out.println("   Add Ring: "+ smiles);
			
			terminateMutatorProcess();
		}
	}
	
//	public static void main(String[] args) {
//		Parameters.initializeParameterCollections(new String[0]);
//		SMILESStringGenotype smiles = (SMILESStringGenotype) new SMILESStringGenotype("").newInstance();
//		System.out.println(smiles);
//		for(int i = 0; i < 10; i++) {
//			smiles.mutate();
//		}
//		
//		Pair<Double, Double> pair = MoleculeMeltingAndBoilingPointProcess.smilesMeltingAndBoilingPoints(smiles);
//		System.out.println(smiles.getPhenotype() + "\nmelting point: "+pair.t1+"\nboiling point: "+pair.t2);
//		
//		MoleculeMutatorProcess.terminateMutatorProcess();
//		MoleculeMeltingAndBoilingPointProcess.terminateMelingBoilingPointProcess();
//	}
}
