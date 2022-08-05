package edu.southwestern.util.datastructures;

/**
 * 3-Tuple of objects
 *
 * @author Jacob Schrum
 * @param <X> Type 1
 * @param <Y> Type 2
 * @param <Z> Type 3
 */
public class Triple<X, Y, Z> {

	public X t1;
	public Y t2;
	public Z t3;

	public Triple(X pos1, Y pos2, Z pos3) {
		this.t1 = pos1;
		this.t2 = pos2;
		this.t3 = pos3;
	}

	@Override
	public String toString() {
		return "(" + t1.toString() + "," + t2.toString() + "," + t3.toString() + ")";
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof Triple) {
			@SuppressWarnings("unchecked")
			Triple<X,Y,Z> p = (Triple<X,Y,Z>) other;
			result = t1.equals(p.t1) && t2.equals(p.t2) && t3.equals(p.t3);
		}
		//System.out.println(result + ":" +this + " equals " + other);
		return result;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (this.t1 != null ? this.t1.hashCode() : 0);
		hash = 71 * hash + (this.t2 != null ? this.t2.hashCode() : 0);
		hash = 71 * hash + (this.t3 != null ? this.t3.hashCode() : 0);
		return hash;
	}
	
	public Triple<X,Y,Z> copy() {
		return new Triple<>(t1,t2,t3);
	}
}
