package edu.southwestern.tasks.molecules;

/**
 * Java code meant to replace the Fortran mutation functions provided by
 * Steve Alexander. This code was initially made by the Claude LLM and
 * then refined from there. It provides a variety of ways of mutating
 * a SMILES string representation of a molecule.
 */
import java.util.*;

import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.evolution.mutation.smiles.SMILESAddRingMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESBranchNewAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESChangeAtomTypeMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESChangeBondTypeMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESDeleteAtomMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESDeleteRingMutation;
import edu.southwestern.evolution.mutation.smiles.SMILESInsertNewAtomMutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.random.RandomNumbers;

public class SmilesMutator {
    private static final Random random = RandomNumbers.randomGenerator;
    private static final char[] ATOMS = {'C', 'O', 'N'};
    private static final char[] BONDS = {'-', '=', '#'};
    private static final char[] RING_NUMBERS = {'1', '2', '3'};
    private static final int MAX_RINGS = 3;
    private static final Map<Character, Integer> MAX_BONDS;
    
    static {
    	MAX_BONDS = new HashMap<>(3);
    	MAX_BONDS.put('C', 4);  // Carbon: up to 4 bonds
    	MAX_BONDS.put('O', 2);  // Oxygen: up to 2 bonds
    	MAX_BONDS.put('N', 3);   // Nitrogen: up to 3 bonds
    }

    public enum MutationType {
        CHANGE_BOND(1),
        ADD_ATOM(2),
        ADD_BRANCH(3),
        DELETE_ATOM(4),
        CHANGE_ATOM(5),
        DELETE_RING(6),
        ADD_RING(7);

        private final int value;
        MutationType(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    /**
     * Represents an atom in the molecule with its connections
     */
    private static class Atom {
        char type;
        List<Bond> bonds = new ArrayList<>();
        
        Atom(char type) {
            this.type = type;
        }
        
        int getTotalBondCount() {
            return bonds.stream().mapToInt(b -> b.type == '#' ? 3 : (b.type == '=' ? 2 : 1)).sum();
        }
    }

    /**
     * Represents a bond between two atoms
     */
    private static class Bond {
        char type;
        Atom from;
        Atom to;
        
        Bond(char type, Atom from, Atom to) {
            this.type = type;
            this.from = from;
            this.to = to;
        }
    }

    /**
     * Main mutation method that delegates to specific mutation types
     */
    public static String mutate(String smiles, MutationType type) {
        if (!isValidMolecule(smiles)) {
            return "X";
        }

        //try {
	        switch (type) {
	        	case CHANGE_BOND: return mutateBond(smiles);
	            case ADD_ATOM: return addAtom(smiles);
	            case ADD_BRANCH: return addBranch(smiles);
	            case DELETE_ATOM: return deleteAtom(smiles);
	            case CHANGE_ATOM: return changeAtom(smiles);
	            case DELETE_RING: return deleteRing(smiles);
	            case ADD_RING: return addRing(smiles);
	        } 
        //} catch (Exception e) {
        //    return "X";
        //}
        return "X";
    }

	/**
     * Changes a random bond type in the molecule
     */
    private static String mutateBond(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> bondPositions = new ArrayList<>();
        
        for (int i = 0; i < chars.length; i++) {
            if (isBond(chars[i])) {
                bondPositions.add(i);
            }
        }
        
        if (bondPositions.isEmpty()) {
            return "X";
        }
        
        int position = bondPositions.get(random.nextInt(bondPositions.size()));
        char currentBond = chars[position];
        char newBond;
        
        do {
            newBond = BONDS[random.nextInt(BONDS.length)];
        } while (newBond == currentBond);
        
        chars[position] = newBond;
        String result = new String(chars);
        
        return isValidMolecule(result) ? result : "X";
    }

    /**
     * Adds a new atom to the existing molecule
     */
    private static String addAtom(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> atomPositions = findValidAtomPositions(chars);
        
        if (atomPositions.isEmpty()) {
            return "X";
        }
        
        int position = atomPositions.get(random.nextInt(atomPositions.size()));
        char newAtom = ATOMS[random.nextInt(ATOMS.length)];
        char newBond = BONDS[random.nextInt(BONDS.length)];
        
        StringBuilder result = new StringBuilder(smiles);
        result.insert(position + 1, newBond).insert(position + 2, newAtom);
        
        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Adds a new branched atom to the molecule
     */
    private static String addBranch(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> atomPositions = findValidAtomPositions(chars);
        
        if (atomPositions.isEmpty()) {
            return "X";
        }
        
        int position = atomPositions.get(random.nextInt(atomPositions.size()));
        char newAtom = ATOMS[random.nextInt(ATOMS.length)];
        char newBond = BONDS[random.nextInt(BONDS.length)];
        
        StringBuilder result = new StringBuilder(smiles);
        result.insert(position + 1, "(" + newBond + newAtom + ")");
        int openBeforeNew = result.substring(0,position).lastIndexOf("(");
        int closeBeforeNew = result.substring(0,position).lastIndexOf(")");
        if(openBeforeNew > -1 && // There is an open ( before the new branch 
           closeBeforeNew < openBeforeNew) { // and there is no close ), or if there is, it is to the left of the open (
        	collapseBranchAt(result,position + 4); // 4 characters were just added
        } else {
        	collapseBranchAtEnd(result); // Never let branches persist at the end
        }
        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Deletes an atom from the molecule
     */
    private static String deleteAtom(String smiles) {
        MoleculeStructure structure = parseMolecule(smiles);
        if (structure.atoms.size() <= 1) {
            return "X";
        }

        List<Integer> deletableAtoms = new ArrayList<>();
        for (int i = 0; i < structure.atoms.size(); i++) {
            Atom atom = structure.atoms.get(i);
            // Don't delete atoms that are part of rings or have multiple branches
            if (atom.bonds.size() == 1 && !isInRing(smiles, i)) {
                deletableAtoms.add(i);
            }
        }

        if (deletableAtoms.isEmpty()) {
            return "X";
        }

        int atomToDelete = deletableAtoms.get(random.nextInt(deletableAtoms.size()));
        StringBuilder result = new StringBuilder(smiles);
        
        // Find and remove the atom and its associated bond
        int atomPos = findNthAtom(smiles, atomToDelete);
        if (atomPos > 0 && isBond(smiles.charAt(atomPos - 1))) {
        	if(atomPos > 1 && smiles.charAt(atomPos - 2) == '(') {
        		// Deletes one and only atom in branch
        		result.delete(atomPos - 2, atomPos + 2);
        	} else {
        		result.delete(atomPos - 1, atomPos + 1);
        	}
        	
        	collapseBranchAtEnd(result);
        } else { // Can/should this ever happen?
            result.deleteCharAt(atomPos);
        }

        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

	/**
	 * If the SMILES string now ends with an unnecessary branch, remove the parentheses
	 * @param result
	 */
	private static void collapseBranchAtEnd(StringBuilder result) {
		collapseBranchAt(result,result.length() - 1);
	}
	/**
	 * The superfluous branch could be inside of another branch, in the middle.
	 * @param result StringBuilder of original SMILES string
	 * @param closingParenPos place where a closing paren might be
	 */
	private static void collapseBranchAt(StringBuilder result, int closingParenPos) {
		// What if the string now ends with a branch? In that case, it's not really a branch
		if(result.charAt(closingParenPos) == ')') {
			int openingParenPos = result.substring(0,closingParenPos).lastIndexOf("(");
			result.deleteCharAt(closingParenPos);     // deletes )
			result.deleteCharAt(openingParenPos);     // deletes (
		}
	}

    /**
     * Indicate if the atom at position i is inside of a ring
     */
    private static boolean isInRing(String smiles, int i) {
    	// Position after the atom is a number indicating the ring it matches with
		return i+1 < smiles.length() && Character.isDigit(smiles.charAt(i+1));
	}

	/**
     * Changes an atom type in the molecule
     */
    private static String changeAtom(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> atomPositions = findValidAtomPositions(chars);
        
        if (atomPositions.isEmpty()) {
            return "X";
        }
        
        int position = atomPositions.get(random.nextInt(atomPositions.size()));
        char currentAtom = chars[position];
        char newAtom;
        
        do {
            newAtom = ATOMS[random.nextInt(ATOMS.length)];
        } while (newAtom == currentAtom);
        
        chars[position] = newAtom;
        String result = new String(chars);
        
        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Deletes a ring from the molecule
     */
    private static String deleteRing(String smiles) {
        // Count rings in molecule
        Map<Character, List<Integer>> ringPositions = findRingPositions(smiles);
        if (ringPositions.isEmpty()) {
            return "X";
        }

        // Select random ring to delete
        char ringNumber = RING_NUMBERS[random.nextInt(ringPositions.size())];
        List<Integer> positions = ringPositions.get(ringNumber);
        
        if (positions == null || positions.size() != 2) {
            return "X";
        }

        StringBuilder result = new StringBuilder(smiles);
        // Delete ring numbers from back to front to maintain correct positions
        result.deleteCharAt(positions.get(1));
        result.deleteCharAt(positions.get(0));

        String finalResult = result.toString();
        return isValidMolecule(finalResult) ? finalResult : "X";
    }

    /**
     * Adds a new ring to the molecule
     */
    private static String addRing(String smiles) {
        // Check existing rings
        Map<Character, List<Integer>> existingRings = findRingPositions(smiles);
        if (existingRings.size() >= MAX_RINGS) {
            return "X";
        }

        // Find next available ring number
        char ringNumber = '1';
        for (char c : RING_NUMBERS) {
            if (!existingRings.containsKey(c)) {
                ringNumber = c;
                break;
            }
        }

        // Find valid positions for ring attachment
        List<Integer> atomPositions = findValidAtomPositions(smiles.toCharArray());
        if (atomPositions.size() < 2) {
            return "X";
        }

        // Select two different positions for ring attachment
        int pos1 = atomPositions.get(random.nextInt(atomPositions.size()));
        int pos2;
        do {
            pos2 = atomPositions.get(random.nextInt(atomPositions.size()));
        } while (pos2 == pos1 || Math.abs(pos2 - pos1) < 2); // Ensure minimum ring size

        StringBuilder result = new StringBuilder(smiles);
        // Add ring numbers from back to front to maintain correct positions
        result.insert(Math.max(pos1, pos2) + 1, ringNumber);
        result.insert(Math.min(pos1, pos2) + 1, ringNumber);

        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Represents the molecular structure for validation
     */
    private static class MoleculeStructure {
        List<Atom> atoms = new ArrayList<>();
        List<Bond> bonds = new ArrayList<>();
        Map<Character, int[]> rings = new HashMap<>();
    }

    /**
     * Parses SMILES string into a molecular structure
     */
    private static MoleculeStructure parseMolecule(String smiles) {
        MoleculeStructure structure = new MoleculeStructure();
        Stack<Atom> atomStack = new Stack<>();
        Atom currentAtom = null;
        
        for (int i = 0; i < smiles.length(); i++) {
            char c = smiles.charAt(i);
            
            if (isAtom(c)) {
                Atom newAtom = new Atom(c);
                structure.atoms.add(newAtom);
                
                if (currentAtom != null) {
                    char bondType = i > 0 && isBond(smiles.charAt(i-1)) ? smiles.charAt(i-1) : '-';
                    Bond bond = new Bond(bondType, currentAtom, newAtom);
                    structure.bonds.add(bond);
                    currentAtom.bonds.add(bond);
                    newAtom.bonds.add(bond);
                }
                
                currentAtom = newAtom;
            } else if (c == '(') {
                atomStack.push(currentAtom);
            } else if (c == ')') {
                currentAtom = atomStack.pop();
            } else if (Character.isDigit(c)) {
            	int[] ringAtoms = structure.rings.get(c);
            	if (ringAtoms == null) {
            	    ringAtoms = new int[2];
            	    structure.rings.put(c, ringAtoms);
            	}
                if (ringAtoms[0] == 0) {
                    ringAtoms[0] = structure.atoms.indexOf(currentAtom);
                } else {
                    ringAtoms[1] = structure.atoms.indexOf(currentAtom);
                }
            }
        }
        
        return structure;
    }

    /**
     * Validates if a given SMILES string represents a valid molecule
     */
    private static boolean isValidMolecule(String smiles) {
        if (smiles == null || smiles.isEmpty() || smiles.equals("X")) {
            return false;
        }

        try {
            // Check first character is an atom
            if (!isAtom(smiles.charAt(0))) {
                return false;
            }

            MoleculeStructure structure = parseMolecule(smiles);

            // Validate bond counts
            for (Atom atom : structure.atoms) {
                int bondCount = atom.getTotalBondCount();
                if (bondCount > MAX_BONDS.get(atom.type)) {
                    return false;
                }
            }

            // Validate parentheses
            int parenthesesCount = 0;
            for (char c : smiles.toCharArray()) {
                if (c == '(') parenthesesCount++;
                if (c == ')') parenthesesCount--;
                if (parenthesesCount < 0) return false;
            }
            if (parenthesesCount != 0) return false;

            // Validate rings
            Map<Character, List<Integer>> ringPositions = findRingPositions(smiles);
            for (List<Integer> positions : ringPositions.values()) {
                if (positions.size() != 2) return false;
            }

            // Validate bond-atom patterns
            for (int i = 0; i < smiles.length() - 1; i++) {
                char current = smiles.charAt(i);
                char next = smiles.charAt(i + 1);
                
                if (isBond(current)) {
                    if (!isAtom(next) && next != '(') return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Helper methods
    private static boolean isAtom(char c) {
        return c == 'C' || c == 'O' || c == 'N';
    }

    private static boolean isBond(char c) {
        return c == '-' || c == '=' || c == '#';
    }

    private static List<Integer> findValidAtomPositions(char[] chars) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            if (isAtom(chars[i])) {
                positions.add(i);
            }
        }
        return positions;
    }

    private static Map<Character, List<Integer>> findRingPositions(String smiles) {
        Map<Character, List<Integer>> ringPositions = new HashMap<>();
        for (int i = 0; i < smiles.length(); i++) {
            char c = smiles.charAt(i);
            if (Character.isDigit(c)) {
            	List<Integer> positions = ringPositions.get(c);
            	if (positions == null) {
            	    positions = new ArrayList<>();
            	    ringPositions.put(c, positions);
            	}
            	positions.add(i);
            }
        }
        return ringPositions;
    }

    private static int findNthAtom(String smiles, int n) {
        int count = 0;
        for (int i = 0; i < smiles.length(); i++) {
            if (isAtom(smiles.charAt(i))) {
                if (count == n) return i;
                count++;
            }
        }
        return -1;
    }
    
	public static void main(String[] args) {

		String exampleSMILES = "C-C-N-C(=C)-O";
		for(int i = 0; i < 1000; i++) {
			System.out.println("Attempt " + i);
			
			String temp = exampleSMILES;
			System.out.println("        Start: "+ exampleSMILES);
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = mutateBond(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "Change Bond: "+ exampleSMILES);
			}
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = addAtom(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "Insert Atom: "+ exampleSMILES);
			}
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = addBranch(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "Branch Atom: "+ exampleSMILES);
			}
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = deleteAtom(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "Delete Atom: "+ exampleSMILES);
			}
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = changeAtom(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "Change Atom: "+ exampleSMILES);
			}
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = deleteRing(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "Delete Ring: "+ exampleSMILES);
			}
			if(RandomNumbers.randomGenerator.nextBoolean()) {
				temp = addRing(exampleSMILES);
				exampleSMILES = temp.equals("X") ? exampleSMILES : temp;
				System.out.println((temp.equals("X") ? "X " : "  ") + "   Add Ring: "+ exampleSMILES);
			}
			MiscUtil.waitForReadStringAndEnterKeyPress();
			
		}
	}
}