package edu.southwestern.util.datastructures;

/**
 * Tuple of objects
 *
 * @author Jacob Schrum
 * @param <W> Type 1
 * @param <X> Type 2
 * @param <Y> Type 3
 * @param <Z> Type 4
 */
public class Quad<W, X, Y, Z> {

	public W t1;
	public X t2;
	public Y t3;
	public Z t4;

	public Quad(W t1, X t2, Y t3, Z t4) {
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.t4 = t4;
	}

	@Override
	public String toString() {
		return "(" + t1 + "," + t2 + "," + t3 + "," + t4 + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Quad) {
			@SuppressWarnings("unchecked")
			Quad<W, X, Y, Z> p = (Quad<W, X, Y, Z>) other;
			return t1.equals(p.t1) && t2.equals(p.t2) && t3.equals(p.t3) && t4.equals(p.t4);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (this.t1 != null ? this.t1.hashCode() : 0);
		hash = 71 * hash + (this.t2 != null ? this.t2.hashCode() : 0);
		hash = 71 * hash + (this.t3 != null ? this.t3.hashCode() : 0);
		hash = 71 * hash + (this.t4 != null ? this.t4.hashCode() : 0);
		return hash;
	}
}
