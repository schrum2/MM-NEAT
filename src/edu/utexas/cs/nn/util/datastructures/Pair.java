package edu.utexas.cs.nn.util.datastructures;

/**
 * Tuple of objects
 *
 * @author Jacob Schrum
 */
public class Pair<X, Y> {

	public X t1;
	public Y t2;

	/**
	 * Tuple of items
	 * 
	 * @param pos1
	 *            First item
	 * @param pos2
	 *            Second item
	 */
	public Pair(X pos1, Y pos2) {
		this.t1 = pos1;
		this.t2 = pos2;
	}

	/**
	 * Print tuple in parentheses
	 * 
	 * @return String representation
	 */
	@Override
	public String toString() {
		return "(" + t1.toString() + "," + t2.toString() + ")";
	}

	/**
	 * Pairs are equal if both positions in tuple are equal.
	 * 
	 * @param other
	 *            A different pair
	 * @return Whether pairs are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair p = (Pair) other;
			return t1.equals(p.t1) && t2.equals(p.t2);
		}
		return false;
	}

	/**
	 * Auto-generated hash code for Pairs
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + (this.t1 != null ? this.t1.hashCode() : 0);
		hash = 41 * hash + (this.t2 != null ? this.t2.hashCode() : 0);
		return hash;
	}
}
