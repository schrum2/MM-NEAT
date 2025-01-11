package edu.southwestern.tasks.molecules;

/**
 * Java code meant to replace the Fortran mutation functions provided by
 * Steve Alexander. This code was initially made by the Claude LLM and
 * then refined from there. It provides a variety of ways of mutating
 * a SMILES string representation of a molecule.
 */
import java.util.*;
import java.util.stream.Collectors;

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.random.RandomNumbers;

public class SmilesMutator {
	
	private static class InvalidMoleculeException extends IllegalStateException {
		public InvalidMoleculeException(String smiles) {
			super("Invalid: "+smiles);
		}
	}
	
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
        boolean isBranchPoint = false;
        
        Atom(char type) {
            this.type = type;
        }
        
        int getTotalBondCount() {
            return bonds.stream().mapToInt(b -> b.bondCount()).sum();
        }

		public void setBranchPoint() {
			isBranchPoint = true;
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
        
		public int bondCount() {
			return SmilesMutator.bondCount(type);
		}
    }

    /**
     * Main mutation method that delegates to specific mutation types
     */
    public static String mutate(String smiles, MutationType type) {
        if (!isValidMolecule(smiles)) {
            return "X";
        }

        try {
	        switch (type) {
	        	case CHANGE_BOND: return mutateBond(smiles);
	            case ADD_ATOM: return addAtom(smiles);
	            case ADD_BRANCH: return addBranch(smiles);
	            case DELETE_ATOM: return deleteAtom(smiles);
	            case CHANGE_ATOM: return changeAtom(smiles);
	            case DELETE_RING: return deleteRing(smiles);
	            case ADD_RING: return addRing(smiles);
	        } 
        } catch (Exception e) {
            throw e;
        	//return "X";
        }
        return "X";
    }

	/**
     * Changes a random bond type in the molecule
     */
    static String mutateBond(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> bondPositions = new ArrayList<>();
        List<Integer> maxAllowed = new ArrayList<>();
        
        for (int i = 0; i < chars.length; i++) {
            if (isBond(chars[i])) {
            	int atomPosBefore = i-1;
            	if(chars[atomPosBefore] == ')') {
            		boolean done = chars[atomPosBefore] == '(';
            		int nesting = 0;
            		while(!done) {
            			atomPosBefore--;
            			if(chars[atomPosBefore] == '(' && nesting  == 0) {
            				done = true;
            			} else if(chars[atomPosBefore] == '(') {
            				nesting--;
            			} else if(chars[atomPosBefore] == ')') {
            				nesting++;
            			}
            		}
            		atomPosBefore--;
            	}
            	// There could be two branches off of one atom
            	if(chars[atomPosBefore] == ')') {
            		boolean done = chars[atomPosBefore] == '(';
            		int nesting = 0;
            		while(!done) {
            			atomPosBefore--;
            			if(chars[atomPosBefore] == '(' && nesting  == 0) {
            				done = true;
            			} else if(chars[atomPosBefore] == '(') {
            				nesting--;
            			} else if(chars[atomPosBefore] == ')') {
            				nesting++;
            			}
            		}
            		atomPosBefore--;
            	}
            	// there should not be more than two branches
            	if(chars[atomPosBefore] == '(') atomPosBefore--;
            	if(chars[atomPosBefore] == ')') { // when leaving the second branch, the end of the first may need to be dealt with
            		boolean done = chars[atomPosBefore] == '(';
            		int nesting = 0;
            		while(!done) {
            			atomPosBefore--;
            			if(chars[atomPosBefore] == '(' && nesting  == 0) {
            				done = true;
            			} else if(chars[atomPosBefore] == '(') {
            				nesting--;
            			} else if(chars[atomPosBefore] == ')') {
            				nesting++;
            			}
            		}
            		atomPosBefore--;
            	}            	
            	if(Character.isDigit(chars[atomPosBefore])) atomPosBefore--;
            	assert isAtom(chars[atomPosBefore]) : smiles + " at " + atomPosBefore + " not an atom. Starting from "+i;
            	int atomBondsBefore = getAtomBondCount(smiles, atomPosBefore);

            	int atomPosAfter = i+1;
            	assert isAtom(chars[atomPosAfter]) : smiles + " at " + atomPosAfter + " not an atom. Starting from "+i;
            	int atomBondsAfter = getAtomBondCount(smiles, atomPosAfter);
            	
            	int leftWiggleRoom = MAX_BONDS.get(chars[atomPosBefore]) - atomBondsBefore;  
            	int rightWiggleRoom = MAX_BONDS.get(chars[atomPosAfter]) - atomBondsAfter;
            	int wiggleRoom = Math.min(leftWiggleRoom, rightWiggleRoom);	
            	char bond = chars[i];
            	int current = bondCount(bond);
                int maxBondsAllowedInPosition = wiggleRoom + current;  	            		
                
                if(wiggleRoom != 0 || current != 1) {
                    bondPositions.add(i);
                    maxAllowed.add(maxBondsAllowedInPosition);
                }
            }
        }
        
        if (bondPositions.isEmpty()) {
//        	System.out.println(bondPositions);
//        	System.out.println(maxAllowed);
//        	throw new InvalidMoleculeException(smiles);
        	return "X";
        }
        
        int bondListPos = random.nextInt(bondPositions.size());
        int maxBondAllowed = maxAllowed.get(bondListPos);
        assert maxBondAllowed > 0 : bondPositions.get(bondListPos) +" in " + smiles + ": pos " + bondListPos + " in " + bondPositions + " where max is " + maxAllowed +": maxBondAllowed="+maxBondAllowed + ", currentBond = "+chars[bondPositions.get(bondListPos)];
        int position = bondPositions.get(bondListPos);
        char currentBond = chars[position];
        char newBond;
        
        if(maxBondAllowed == 2) {
        	if(currentBond == '=') newBond = '-';
        	else if(currentBond == '-') newBond = '=';
        	else throw new InvalidMoleculeException(position +" in " + smiles + ": pos " + bondListPos + " in " + bondPositions + " where max is " + maxAllowed +": maxBondAllowed="+maxBondAllowed + ", currentBond = "+currentBond);
        } else if(maxBondAllowed == 3) {
        	if(currentBond == '=') newBond = RandomNumbers.coinFlip() ? '-' : '#';
        	else if(currentBond == '-') newBond = RandomNumbers.coinFlip() ? '=' : '#';
        	else if(currentBond == '#') newBond = RandomNumbers.coinFlip() ? '=' : '-';
        	else throw new InvalidMoleculeException(position +" in " + smiles + ": pos " + bondListPos + " in " + bondPositions + " where max is " + maxAllowed +": maxBondAllowed="+maxBondAllowed + ", currentBond = "+currentBond);
        } else throw new InvalidMoleculeException(position +" in " + smiles + ": pos " + bondListPos + " in " + bondPositions + " where max is " + maxAllowed +": maxBondAllowed="+maxBondAllowed + ", currentBond = "+currentBond);
        
//        int attempts = 0;
//        do {
//        	if(attempts++ > 10) throw new InvalidMoleculeException(position +" in " + smiles + ": pos " + bondListPos + " in " + bondPositions + " where max is " + maxAllowed +": maxBondAllowed="+maxBondAllowed + ", currentBond = "+currentBond);
//        	newBond = BONDS[random.nextInt(BONDS.length)];
//        } while (newBond == currentBond || bondCount(newBond) > maxBondAllowed);

        chars[position] = newBond;
        String result = new String(chars);
        if(!isValidMolecule(result)) {
        	result = "X";
        }

        return result;
    }

	/**
	 * @param bond
	 * @return
	 */
	public static int bondCount(char bond) {
	    switch (bond) {
        case '-': return 1; // Single bond
        case '=': return 2; // Double bond
        case '#': return 3; // Triple bond
        default: throw new IllegalArgumentException("Not a bond character: " + bond);
	    }
	}

    /**
     * Adds a new atom to the existing molecule
     */
    static String addAtom(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> atomPositions = findValidAtomPositions(chars);
        
        // Don't add before a branch
        atomPositions.removeIf(pos -> pos+1 < smiles.length() && smiles.charAt(pos+1) == '(');
        // Also consider branches involved in rings
        atomPositions.removeIf(pos -> pos+2 < smiles.length() && Character.isDigit(smiles.charAt(pos+1)) && smiles.charAt(pos+2) == '(');

        // Cannot add next to oxygen with double bond at the end
        if(atomPositions.get(atomPositions.size() - 1) == smiles.length() - 1 && smiles.substring(smiles.length() - 2).equals("=O")) atomPositions.remove(atomPositions.size() - 1);
        // or a double-bonded oxygen at the end of a branch
        atomPositions.removeIf(pos -> pos > 0 && pos+2 < smiles.length() && smiles.substring(pos-1,pos+2).equals("=O)"));

        
        if (atomPositions.isEmpty()) {
            return "X";
        }
        
        int position = atomPositions.get(random.nextInt(atomPositions.size()));
        if(Character.isDigit(smiles.charAt(position))) { // atom is part of a ring. It's bond is one position over
        	position++;
        }
        char newAtom = ATOMS[random.nextInt(ATOMS.length)];
        
        StringBuilder result = new StringBuilder(smiles);
        
        if(newAtom == 'O') {
        	// There may be restrictions depending on the surrounding bonds
        	if(position+1 < smiles.length() && smiles.charAt(position+1) != ')') {
        		if(smiles.charAt(position+1) == '=') {
        			newAtom = RandomNumbers.coinFlip() ? 'C' : 'N';
        		} else if(smiles.charAt(position+1) == '#') {
        			newAtom = 'C';
        		}
        	} 
        }
        
        if(newAtom == 'N') {
        	// There may be restrictions depending on the surrounding bonds
        	if(position+1 < smiles.length()) {
        		if(smiles.charAt(position+1) == '#') {
        			newAtom = 'C';
        		}
        	}
        }
        
    	if(position+1 == smiles.length() || smiles.charAt(position+1) == ')') {
    		// Is last atom, but what bonds come before?
    		if(smiles.charAt(position) == 'N' && smiles.charAt(position-1) == '#') {
    			// Adding anything to the right of the N would result in too many bonds, so lower the # to =
    			result.setCharAt(position-1,'=');
    		} else if(smiles.charAt(position) == 'O' && smiles.charAt(position-1) == '=') {
    			// Adding anything to the right of the O would result in too many bonds, so lower the = to -
    			result.setCharAt(position-1,'-');
    		}
    	}
        
        // Always add with single bonds to discourage bad SMILES strings. Other mutations can change
        // the bonds later.
        char newBond = '-'; //BONDS[random.nextInt(BONDS.length)];
        
        result.insert(position + 1, newBond).insert(position + 2, newAtom);
        
        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Adds a new branched atom to the molecule
     */
    static String addBranch(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> atomPositions = findValidAtomPositions(chars, new char[] {'O'}); // cannot branch off of Oxygen (only 2 bonds)
        
        // Go through supposedly valid positions and remove those whose atom is already engaged in the max number of bonds
        atomPositions.removeIf(pos -> getAtomBondCount(smiles, pos) >= MAX_BONDS.get(chars[pos]));

        
        if (atomPositions.isEmpty()) {
        	throw new InvalidMoleculeException(smiles);
            //return "X";
        }
        
        int position = atomPositions.get(random.nextInt(atomPositions.size()));
        char newAtom = ATOMS[random.nextInt(ATOMS.length)];
        // Just initialize these to single bonds to be safe. Later mutations can change to higher bond counts
        char newBond = '-'; // BONDS[random.nextInt(BONDS.length)];
        
        // if position is an atom in a ring, then the new branch must come after the ring number
        if(position + 1 < smiles.length()) {
        	if(Character.isDigit(smiles.charAt(position+1))) {
        		position++; // add branch after the ring number
        	}
        }
        
        StringBuilder result = new StringBuilder(smiles);
        if(position == 0) {
        	// A branch off the first atom is actually equivalent to an atom added to the front
        	result.insert(0, "" + newAtom + newBond);
        } else {
        	result.insert(position + 1, "(" + newBond + newAtom + ")");
        }
        int doubleClose = result.indexOf("))");
        
        //int openBeforeNew = result.substring(0,position).lastIndexOf("(");
        //int closeBeforeNew = result.substring(0,position).lastIndexOf(")");
        //if(openBeforeNew > -1 && // There is an open ( before the new branch 
        //   closeBeforeNew < openBeforeNew) { // and there is no close ), or if there is, it is to the left of the open (
        //	collapseBranchAt(result,position + 4); // 4 characters were just added
        if(doubleClose > -1) {
        	System.out.println("doubleClose: "+doubleClose+" : "+result);
        	collapseBranchAt(result,doubleClose);
    	} else {
        	System.out.println("collapse end : "+result);
        	collapseBranchAtEnd(result); // Never let branches persist at the end
        }
        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    static int getAtomBondCount(String smiles, int position) {
    	assert isAtom(smiles.charAt(position));
    	try {
    		int bondCount = 0;
    		if(position > 0) {
    			int precedingBondCount = bondCount(smiles.charAt(position - 1));
    			bondCount += precedingBondCount;
    		}

    		int after = position+1;
    		if(after < smiles.length()) {
    			if(Character.isDigit(smiles.charAt(after))) {
    				bondCount += 1; // Add for ring connection (assuming these are always single bonds)
    				after++;
    			}

    			if(after < smiles.length()) {
    				if(smiles.charAt(after) == '(') { // atom connected to branch
    					int branchBoundCount = bondCount(smiles.charAt(after + 1));
    					bondCount += branchBoundCount;
    					after += 3; // skip after first branch bond and atom
    					// find where branch ends
    					boolean done = smiles.charAt(after) == ')';
    					int nesting = 0;
    					while(!done) {
    						if(smiles.charAt(after) == '(') nesting++;
    						after++;
    						if(smiles.charAt(after) == ')') {
    							if(nesting == 0) done = true;
    							else nesting--;
    						}
    					}
    					after++; // position after branch closes

    					// There could be a second branch here
    					if(smiles.charAt(after) == '(') {
    						int nextBranchBoundCount = bondCount(smiles.charAt(after + 1));
    						bondCount += nextBranchBoundCount;

    						after += 3; // skip after first branch bond and atom
    						// find where branch ends
    						done = smiles.charAt(after) == ')';
        					nesting = 0;
        					while(!done) {
        						if(smiles.charAt(after) == '(') nesting++;
        						after++;
        						if(smiles.charAt(after) == ')') {
        							if(nesting == 0) done = true;
        							else nesting--;
        						}
        					}
    						after++; // position after branch closes
    					}
    				}

    				// Make sure we were not looking at the last atom, and also not the last atom within a branch
    				if(after < smiles.length() && smiles.charAt(after) != ')') {
    					int followingBondCount = bondCount(smiles.charAt(after));
    					bondCount += followingBondCount;
    				}
    			}
    		}
			return bondCount;
    	} catch(Exception e) {
    		System.out.println(smiles + " position " + position);
    		throw e;
    	}
    }    
    
    /**
     * Deletes an atom from the molecule
     */
    static String deleteAtom(String smiles) {
        MoleculeStructure structure = parseMolecule(smiles);
        if (structure.atoms.size() <= 1) {
        	throw new InvalidMoleculeException(smiles);
            //return "X";
        }

        List<Integer> deletableAtoms = new ArrayList<>();
        for (int i = 0; i < structure.atoms.size(); i++) {
            //Atom atom = structure.atoms.get(i);
            // Don't delete atoms that are part of rings or have multiple branches
            //if (atom.bonds.size() == 1 && !isInRing(structure, i)) {
            
        	// Don't delete atoms that are part of rings or have branches
            if (!isInRing(structure, i) && !structure.atoms.get(i).isBranchPoint) {
                deletableAtoms.add(i);
            }
        }

        if (deletableAtoms.isEmpty()) {
        	throw new InvalidMoleculeException(smiles);
            //return "X";
        }

        int atomToDelete = deletableAtoms.get(random.nextInt(deletableAtoms.size()));
        StringBuilder result = new StringBuilder(smiles);
        
        // Find and remove the atom and its associated bond
        int atomPos = findNthAtom(smiles, atomToDelete);
        if (atomPos > 0 && isBond(smiles.charAt(atomPos - 1))) {
        	if(atomPos > 1 && smiles.charAt(atomPos - 2) == '(' && smiles.charAt(atomPos + 1) == ')') {
        		// Deletes one and only atom in branch
        		result.delete(atomPos - 2, atomPos + 2);
        		//System.out.println("            1: "+result + " atomPos = " + atomPos);
        	} else {
        		result.delete(atomPos - 1, atomPos + 1);
        		//System.out.println("            2: "+result + " atomPos = " + atomPos);
        		if(atomPos - 1 < result.length() && (result.charAt(atomPos - 1) == '#' || result.charAt(atomPos - 1) == '=')) { // the bond after what was deleted
        			result.setCharAt(atomPos - 1, '-'); // Just make it a single bond to avoid problems
        		}
        	}
        	
        	collapseBranchAtEnd(result);
        } else if(atomPos == 0) { // deleting first atom, and the bond after it
            result.delete(atomPos, atomPos + 2);
        } else {
        	throw new InvalidMoleculeException(smiles + ": want to delete " + atomPos);
            //return "X";
        }

        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Checks if a given atom index is part of a ring in the molecular structure.
     * 
     * @param structure The parsed molecular structure.
     * @param atomIndex The index of the atom to check.
     * @return true if the atom is part of a ring, false otherwise.
     */
    private static boolean isInRing(MoleculeStructure structure, int atomIndex) {
        for (Map.Entry<Character, int[]> ring : structure.rings.entrySet()) {
            int[] ringAtoms = ring.getValue();
            if (ringAtoms[0] == atomIndex || ringAtoms[1] == atomIndex) {
                return true;
            }
        }
        return false;
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
			int openingParenPos = closingParenPos - 1;
			boolean done = result.charAt(openingParenPos) == '(';
			int nesting = 0;
			while(!done) {
				openingParenPos--;
				if(result.charAt(openingParenPos) == '(' && nesting == 0) {
					done = true;
				} else if(result.charAt(openingParenPos) == '(') {
					nesting--;
				} else if(result.charAt(openingParenPos) == ')') {
					nesting++;
				}
			}
			result.deleteCharAt(closingParenPos);     // deletes )
			result.deleteCharAt(openingParenPos);     // deletes (
		}
	}

	/**
     * Changes an atom type in the molecule
     */
    static String changeAtom(String smiles) {
        char[] chars = smiles.toCharArray();
        List<Integer> atomPositions = findValidAtomPositions(chars);

        // Cannot change a Carbon atom if it is already maxed out on bonds
        atomPositions.removeIf(pos -> smiles.charAt(pos) == 'C' && getAtomBondCount(smiles,pos) == MAX_BONDS.get('C'));
        // Other atom types might have restrictions on what they can change to, but can still change to something
        
        if (atomPositions.isEmpty()) {
        	throw new InvalidMoleculeException(smiles);
            //return "X";
        }
        
        int position = atomPositions.get(random.nextInt(atomPositions.size()));
        char currentAtom = chars[position];
        
        int currentBonds = getAtomBondCount(smiles,position);
        int bondsAllowed = MAX_BONDS.get(currentAtom);
        int diff = bondsAllowed - currentBonds;

        char newAtom;
        
        if(diff == 0) {
        	if(currentAtom == 'N') newAtom = 'C'; // only valid option
        	else if(currentAtom == 'O') newAtom = RandomNumbers.coinFlip() ? 'N' : 'C';
        	else throw new IllegalStateException("smiles = "+smiles+", currentAtom = "+currentAtom+", currentBonds = "+currentBonds+", bondsAllowed = "+bondsAllowed);
        } else if(diff == 1) {
        	if(currentAtom == 'N') newAtom = RandomNumbers.coinFlip() ? 'O' : 'C';
        	else if(currentAtom == 'O') newAtom = RandomNumbers.coinFlip() ? 'N' : 'C';
        	else if(currentAtom == 'C') newAtom = 'N'; // only valid option
        	else throw new IllegalStateException("smiles = "+smiles+", currentAtom = "+currentAtom+", currentBonds = "+currentBonds+", bondsAllowed = "+bondsAllowed);
        } else { // diff >= 2
        	if(currentAtom == 'N') newAtom = RandomNumbers.coinFlip() ? 'O' : 'C';
        	else if(currentAtom == 'O') newAtom = RandomNumbers.coinFlip() ? 'N' : 'C';
        	else if(currentAtom == 'C') newAtom = RandomNumbers.coinFlip() ? 'N' : 'O';
        	else throw new IllegalStateException("smiles = "+smiles+", currentAtom = "+currentAtom+", currentBonds = "+currentBonds+", bondsAllowed = "+bondsAllowed);
        }
                
        chars[position] = newAtom;
        String result = new String(chars);
        
        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Deletes a ring from the molecule
     */
    static String deleteRing(String smiles) {
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
    static String addRing(String smiles) {
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
        
        // Filter positions to avoid parentheses and bond indicators
        atomPositions = atomPositions.stream()
            .filter(pos -> isValidInsertionPoint(smiles, pos))
            .collect(Collectors.toList());

        if (atomPositions.size() <= 2) {
        	return "X";
        }

        // Select the first position for ring attachment randomly
        int atomPos1 = random.nextInt(atomPositions.size());
        int pos1 = atomPositions.get(atomPos1);

        // Filter the atomPositions to exclude invalid options for the second position
        List<Integer> validPositions = new ArrayList<>();
        for (int i = 0; i < atomPositions.size(); i++) {
            if (Math.abs(i - atomPos1) >= 2) {
                validPositions.add(atomPositions.get(i));
            }
        }

        // Check if there are any valid positions left
        if (validPositions.isEmpty()) {
            //throw new InvalidMoleculeException(smiles);
            return "X"; // Fail immediately if no valid positions are found
        }

        // Select the second position for ring attachment randomly from the filtered list
        int pos2 = validPositions.get(random.nextInt(validPositions.size()));

        StringBuilder result = new StringBuilder(smiles);
        // Add ring numbers from back to front to maintain correct positions
        result.insert(Math.max(pos1, pos2) + 1, ringNumber);
        result.insert(Math.min(pos1, pos2) + 1, ringNumber);

        return isValidMolecule(result.toString()) ? result.toString() : "X";
    }

    /**
     * Checks if a given position in the SMILES string is valid for ring number insertion.
     * Valid positions are not inside parentheses or adjacent to bond indicators (e.g., =, #).
     */
    static boolean isValidInsertionPoint(String smiles, int pos) {
        // Ensure position is within bounds
        if (pos < 0 || pos >= smiles.length()) {
            return false;
        }

        // Check the current character
        char current = smiles.charAt(pos);
        if (current == '(' || current == ')' || current == '=' || current == '#') {
            return false;
        }

        // Check the previous character
        if (pos > 0) {
            char prev = smiles.charAt(pos - 1);
            if (prev == '(' || prev == '=' || prev == '#') {
                return false;
            }
        }

        // Check the next character
        if (pos < smiles.length() - 1) {
            char next = smiles.charAt(pos + 1);
            if (next == ')' || next == '=' || next == '#' || Character.isDigit(next)) { // Do not attach a ring where there already is one
                return false;
            }
        }
        
        if(!isAtom(current)) return false;
        
        // Must be an atom
        int bonds = getAtomBondCount(smiles, pos);
        if(bonds >= MAX_BONDS.get(current)) return false; // no room for a ring bond

        return true;
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
            	currentAtom.setBranchPoint();
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
    static boolean isValidMolecule(String smiles) {
        if (smiles == null || smiles.isEmpty() || smiles.equals("X")) {
        	throw new InvalidMoleculeException(smiles);
            //return false;
        }

        try {
            // Check first character is an atom
            if (!isAtom(smiles.charAt(0))) {
            	throw new InvalidMoleculeException(smiles);
                //return false;
            }

            MoleculeStructure structure = parseMolecule(smiles);

            // Validate bond counts
            int pos = 0;
            for (Atom atom : structure.atoms) {
                int bondCount = atom.getTotalBondCount();
                if (bondCount > MAX_BONDS.get(atom.type)) {
                	throw new InvalidMoleculeException(smiles +": invalid bond count of "+bondCount+" for "+atom.type+" at atom pos "+pos);
                    //return false;
                }
                pos++;
            }

            // Validate parentheses
            int parenthesesCount = 0;
            for (char c : smiles.toCharArray()) {
                if (c == '(') parenthesesCount++;
                if (c == ')') parenthesesCount--;
                if (parenthesesCount < 0) {
                	throw new InvalidMoleculeException(smiles);
                	//return false;
                }
            }
            if (parenthesesCount != 0) {
            	throw new InvalidMoleculeException(smiles);
            	//return false;
            }

            // Validate rings
            Map<Character, List<Integer>> ringPositions = findRingPositions(smiles);
            for (List<Integer> positions : ringPositions.values()) {
                if (positions.size() != 2) {
                	throw new InvalidMoleculeException(smiles);
                	//return false;
                }
            }

            // Validate bond-atom patterns
            for (int i = 0; i < smiles.length() - 1; i++) {
                char current = smiles.charAt(i);
                char next = smiles.charAt(i + 1);
                
                if (isBond(current)) {
                    if (!isAtom(next) && next != '(') {
                    	throw new InvalidMoleculeException(smiles);
                    	//return false;
                    }
                }
                
                if(Character.isDigit(next)) {
                	// Ring number
                	if(!isAtom(current)) {
                		throw new InvalidMoleculeException(smiles);
                		//return false; // ring number can only come after atom
                	}
                }
                
                // It actually is allowed to have two branches, as long as there are sufficient bonds
                //if(current == ')' && next == '(') {
                //	throw new InvalidMoleculeException(smiles);
                //	//return false; // don't allow multiple branches like this
                //}
            }

            return true;
        } catch (Exception e) {
        	System.out.println("isValidMolecule:"+smiles);
            throw e;
        	//return false;
        }
    }

    // Helper methods
    private static boolean isAtom(char c) {
        return c == 'C' || c == 'O' || c == 'N';
    }

    private static boolean isBond(char c) {
        return c == '-' || c == '=' || c == '#';
    }

    static List<Integer> findValidAtomPositions(char[] chars) {
    	return findValidAtomPositions(chars, new char[0]);
    }
    static List<Integer> findValidAtomPositions(char[] chars, char[] exclude) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            if (isAtom(chars[i]) && !isExcluded(chars[i], exclude)) {
                positions.add(i);
            }
        }
        return positions;
    }
    
    private static boolean isExcluded(char c, char[] exclude) {
        for (char ex : exclude) {
            if (c == ex) {
                return true;
            }
        }
        return false;
    }

    static Map<Character, List<Integer>> findRingPositions(String smiles) {
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
    
    // Consider unit tests with this nasty SMILES string:
    // O-C(-O-O)(-C)-C(=C(-N(-O)-O)-C(-C))-N=C1-N(-O)-N2-C(-N(-O)-N3-O)-C(-C(-O)(-O)-C-C)(-O-C(-N)(-C3(-C1-C(-N)-C)-C=O)-N(-C)-N-N(-C)-O-C2-C)-O
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
			//MiscUtil.waitForReadStringAndEnterKeyPress();
			
		}
	}
}