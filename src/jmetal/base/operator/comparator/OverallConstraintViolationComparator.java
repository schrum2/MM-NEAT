/**
 * OverallConstraintViolationComparator.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.base.operator.comparator;

import jmetal.base.Solution;
import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on the overall constraint violation of
 * the solutions, as in NSGA-II.
 */
@SuppressWarnings("rawtypes")
public class OverallConstraintViolationComparator implements Comparator {

	/**
	 * Compares two solutions.
	 *
	 * @param o1
	 *            Object representing the first <code>Solution</code>.
	 * @param o2
	 *            Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
	 *         respectively.
	 */
        @Override
	public int compare(Object o1, Object o2) {
		double overall1, overall2;
		overall1 = ((Solution) o1).getOverallConstraintViolation();
		overall2 = ((Solution) o2).getOverallConstraintViolation();

		if ((overall1 < 0) && (overall2 < 0)) {
			if (overall1 > overall2) {
				return -1;
			} else if (overall2 > overall1) {
				return 1;
			} else {
				return 0;
			}
		} else if ((overall1 == 0) && (overall2 < 0)) {
			return -1;
		} else if ((overall1 < 0) && (overall2 == 0)) {
			return 1;
		} else {
			return 0;
		}
	} // compare
} // ConstraintComparator
