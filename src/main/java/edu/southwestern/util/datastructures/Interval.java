package edu.southwestern.util.datastructures;

/**
 * A special type of pair that delineates upper and lower boundaries of a range.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Comparable type that defines range bounds
 */
public class Interval<T extends Comparable<T>> extends Pair<T, T> {

	// Whether lower boundary is inclusive
	private final boolean includeFirst;
	// Whether upper boundary is exclusive
	private final boolean includeLast;

	/**
	 * Default constructor that sets up a closed interval
	 * 
	 * @param start
	 *            lower inclusive bound
	 * @param end
	 *            upper inclusive bound
	 */
	public Interval(T start, T end) {
		this(true, start, end, true);
	}

	/**
	 * General interval
	 * 
	 * @param includeFirst
	 *            Whether lower bound is inclusive
	 * @param start
	 *            lower boundary
	 * @param end
	 *            upper boundary
	 * @param includeLast
	 *            Whether upper boundary is inclusive
	 */
	public Interval(boolean includeFirst, T start, T end, boolean includeLast) {
		super(start, end);
		assert start.compareTo(end) < 1 : "Start of interval not before end";
		this.includeFirst = includeFirst;
		this.includeLast = includeLast;
	}

	/**
	 * Return true if the value is contained within the interval, meaning it is
	 * between the boundary values.
	 *
	 * @param value
	 *            see if this is in the interval
	 * @return true if it is, else false
	 */
	public boolean contains(T value) {
		return (includeFirst ? t1.compareTo(value) < 1 : t1.compareTo(value) < 0)
				&& (includeLast ? t2.compareTo(value) > -1 : t2.compareTo(value) > 0);
	}

	/**
	 * True if value is less than start of interval
	 *
	 * @param value
	 * @return
	 */
	public boolean precedes(T value) {
		return includeFirst ? t1.compareTo(value) >= 1 : t1.compareTo(value) >= 0;
	}

	/**
	 * True if value is after end of interval
	 *
	 * @param value
	 * @return
	 */
	public boolean after(T value) {
		return includeLast ? t2.compareTo(value) <= -1 : t2.compareTo(value) <= 0;
	}

	/**
	 * String representation of interval using inward facing brackets for closed
	 * boundaries and outward facing brackets for open intervals.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return (includeFirst ? "[" : "]") + t1 + "," + t2 + (includeLast ? "]" : "[");
	}

}
