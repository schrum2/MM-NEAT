/**
 * NonDominatedSolutionList.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.util;

import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.comparator.*;
import java.util.*;

/**
 * This class implements an unbound list of non-dominated solutions
 */
public class NonDominatedSolutionList extends SolutionSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Stores a <code>Comparator</code> for dominance checking
	 */
	@SuppressWarnings("rawtypes")
	private Comparator dominance_ = new DominanceComparator();
	/**
	 * Stores a <code>Comparator</code> for checking if two solutions are equal
	 */
	@SuppressWarnings("rawtypes")
	private static final Comparator equal_ = new SolutionComparator();

	/**
	 * Constructor. The objects of this class are lists of non-dominated
	 * solutions according to a Pareto dominance comparator.
	 */
	public NonDominatedSolutionList() {
		super();
	} // NonDominatedList

	/**
	 * Constructor. This constructor creates a list of non-dominated individuals
	 * using a comparator object.
	 *
	 * @param dominance
	 *            The comparator for dominance checking.
	 */
	@SuppressWarnings("rawtypes")
	public NonDominatedSolutionList(Comparator dominance) {
		super();
		dominance_ = dominance;
	} // NonDominatedList

	/**
	 * Inserts a solution in the list
	 *
	 * @param solution
	 *            The solution to be inserted.
	 * @return true if the operation success, and false if the solution is
	 *         dominated or if an identical individual exists. The decision
	 *         variables can be null if the solution is read from a file; in
	 *         that case, the domination tests are omitted
	 */
	@SuppressWarnings("unchecked")
	public boolean add(Solution solution) {
		Iterator<Solution> iterator = solutionsList_.iterator();

		if (solution.getDecisionVariables() != null) {
			while (iterator.hasNext()) {
				Solution listIndividual = iterator.next();
				int flag = dominance_.compare(solution, listIndividual);

				if (flag == -1) { // A solution in the list is dominated by the
									// new one
					iterator.remove();
				} else if (flag == 0) { // Non-dominated solutions
					flag = equal_.compare(solution, listIndividual);
					if (flag == 0) {
						return false; // The new solution is in the list
					}
				} else if (flag == 1) { // The new solution is dominated
					return false;
				}
			} // while
		} // if

		// At this point, the solution is inserted into the list
		solutionsList_.add(solution);

		return true;
	} // add
} // NonDominatedList
