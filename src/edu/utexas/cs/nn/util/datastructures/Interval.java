package edu.utexas.cs.nn.util.datastructures;

/**
 *
 * @author Jacob Schrum
 */
public class Interval<T extends Comparable<T>> extends Pair<T, T> {

    private final boolean includeFirst;
    private final boolean includeLast;

    // default to closed intervals
    public Interval(T start, T end) {
        this(true, start, end, true);
    }

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
     * @param value see if this is in the interval
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

    @Override
    public String toString() {
        return (includeFirst ? "[" : "]") + t1 + "," + t2 + (includeLast ? "]" : "[");
    }

    public static void main(String[] args) {
        Interval<Integer> i = new Interval<Integer>(true, 5, 20, true);
        System.out.println(i);
        System.out.println(-10 + ":" + i.contains(-10));
        System.out.println(5 + ":" + i.contains(5));
        System.out.println(7 + ":" + i.contains(7));
        System.out.println(20 + ":" + i.contains(20));
        System.out.println(21 + ":" + i.contains(21));

        i = new Interval<Integer>(false, 5, 20, true);
        System.out.println(i);
        System.out.println(-10 + ":" + i.contains(-10));
        System.out.println(5 + ":" + i.contains(5));
        System.out.println(7 + ":" + i.contains(7));
        System.out.println(20 + ":" + i.contains(20));
        System.out.println(21 + ":" + i.contains(21));

        i = new Interval<Integer>(false, 5, 20, false);
        System.out.println(i);
        System.out.println(-10 + ":" + i.contains(-10));
        System.out.println(5 + ":" + i.contains(5));
        System.out.println(7 + ":" + i.contains(7));
        System.out.println(20 + ":" + i.contains(20));
        System.out.println(21 + ":" + i.contains(21));

        i = new Interval<Integer>(true, 5, 20, false);
        System.out.println(i);
        System.out.println(-10 + ":" + i.contains(-10));
        System.out.println(5 + ":" + i.contains(5));
        System.out.println(7 + ":" + i.contains(7));
        System.out.println(20 + ":" + i.contains(20));
        System.out.println(21 + ":" + i.contains(21));
    }
}
