package edu.southwestern.tasks.molecules.smiles;

import java.util.function.Predicate;

public class SMILESUtil {

	/**
	 * Counts characters that satisfy some arbitrary predicate
	 * @param input a string, such as a SMILES string
	 * @param predicate returns boolean when given a char as input
	 * @return number of elements of input that satisfy the predicate
	 */
    public static int countCharacters(String input, Predicate<Character> predicate) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (predicate.test(c)) {
                count++;
            }
        }
        return count;
    }
	
	/**
	 * The number of atoms within a SMILES string.
	 * Only recognized C, N and O.
	 * 
	 * @param smiles a SMILES string
	 * @return atom count
	 */
	public static int atomCount(String smiles) {
		return countCharacters(smiles, c -> isAtom(c));
	}
	
	/**
	 * Indicates if a character is an atom that can be in a SMILES string.
	 * For our purposes, only Carbon (C), Nitrogen (N), and Oxygen(O) are
	 * recognized.
	 * 
	 * @param c character from a SMILES string
	 * @return true for C, N, or O
	 */
	public static boolean isAtom(char c) {
		return c == 'C' || c == 'N' || c == 'O';
	}
	
	/**
	 * Number of bond characters in SMILES string.
	 * Recognizes -, =, and #
	 * @param smiles a SMILES string
	 * @return bond count
	 */
	public static int bondCount(String smiles) {
		return countCharacters(smiles, c -> isBond(c));
	}
	
	/**
	 * Indicates if a character represents a bond in a SMILES string.
	 * Single bond is -, double is =, and triple is #
	 * @param c character from a SMILES string
	 * @return true for -, = or #
	 */
	public static boolean isBond(char c) {
		return c == '-' || c == '=' || c == '#';
	}
	
	/**
	 * Number of Carbons in SMILES string
	 * @param smiles SMILES string
	 * @return count number of C characters
	 */
	public static int carbonCount(String smiles) {
		return countCharacters(smiles, c -> c == 'C');
	}
	
	/**
	 * Number of Oxygens in SMILES string
	 * @param smiles SMILES string
	 * @return count number of O characters
	 */
	public static int oxygenCount(String smiles) {
		return countCharacters(smiles, c -> c == 'O');
	}
	
	/**
	 * Number of Nitrogens in SMILES string
	 * @param smiles SMILES string
	 * @return count number of N characters
	 */
	public static int nitrogenCount(String smiles) {
		return countCharacters(smiles, c -> c == 'N');
	}

	/**
	 * Number of single bonds in SMILES string,
	 * ignoring bonds with implied Hydrogens.
	 * @param smiles SMILES string
	 * @return count number of - characters
	 */
	public static int singleBondCount(String smiles) {
		return countCharacters(smiles, c -> c == '-');
	}
	
	/**
	 * Number of double bonds in SMILES string
	 * @param smiles SMILES string
	 * @return count number of = characters
	 */
	public static int doubleBondCount(String smiles) {
		return countCharacters(smiles, c -> c == '=');
	}
	
	/**
	 * Number of triple bonds in SMILES string
	 * @param smiles SMILES string
	 * @return count number of # characters
	 */
	public static int tripleBondCount(String smiles) {
		return countCharacters(smiles, c -> c == '#');
	}
	
	/**
	 * Indicates if SMILES string contains a ring.
	 * 
	 * TODO: I saw a result that verified that a molecule can
	 *       contain multiple rings, for example: C(-N(=C2))(-O)(-N(-N1))-C21-C
	 * 
	 * 
	 * @param smiles SMILES string
	 * @return true if string has a ring 
	 */

}
