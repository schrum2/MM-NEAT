/**
 * SolutionSet.java
 * 
* @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.base;

import java.io.*;
import java.util.*;

import jmetal.util.Configuration;

/**
 * Class representing a SolutionSet (a set of solutions)
 */
@SuppressWarnings("serial")
public class SolutionSet implements Serializable {

	/**
	 * Stores a list of <code>solution</code> objects.
	 */
	protected List<Solution> solutionsList_;
	/**
	 * Maximum size of the solution set
	 */
	private int capacity_ = 0;

	/**
	 * Constructor. Creates an unbounded solution set.
	 */
	public SolutionSet() {
		solutionsList_ = new ArrayList<Solution>();
	} // SolutionSet

	/**
	 * Creates a empty solutionSet with a maximum capacity.
	 *
	 * @param maximumSize
	 *            Maximum size.
	 */
	public SolutionSet(int maximumSize) {
		solutionsList_ = new ArrayList<Solution>();
		capacity_ = maximumSize;
	} // SolutionSet

	/**
	 * Inserts a new solution into the SolutionSet.
	 *
	 * @param solution
	 *            The <code>Solution</code> to store
	 * @return True If the <code>Solution</code> has been inserted, false
	 *         otherwise.
	 */
	public boolean add(Solution solution) {
		if (solutionsList_.size() == capacity_) {
			Configuration.logger_.severe("The population is full");
			Configuration.logger_.severe("Capacity is : " + capacity_);
			Configuration.logger_.severe("\t Size is: " + this.size());
			return false;
		} // if

		solutionsList_.add(solution);
		return true;
	} // add

	/**
	 * Returns the ith solution in the set.
	 *
	 * @param i
	 *            Position of the solution to obtain.
	 * @return The <code>Solution</code> at the position i.
	 * @throws IndexOutOfBoundsException.
	 */
	public Solution get(int i) {
		if (i >= solutionsList_.size()) {
			throw new IndexOutOfBoundsException("Index out of Bound " + i);
		}
		return solutionsList_.get(i);
	} // get

	/**
	 * Returns the maximum capacity of the solution set
	 *
	 * @return The maximum capacity of the solution set
	 */
	public int getMaxSize() {
		return capacity_;
	} // getMaxSize

	/**
	 * Sorts a SolutionSet using a <code>Comparator</code>.
	 *
	 * @param comparator
	 *            <code>Comparator</code> used to sort.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sort(Comparator comparator) {
		if (comparator == null) {
			Configuration.logger_.severe("No criterium for compare exist");
			return;
		} // if
		Collections.sort(solutionsList_, comparator);
	} // sort

	/**
	 * Returns the number of solutions in the SolutionSet.
	 *
	 * @return The size of the SolutionSet.
	 */
	public int size() {
		return solutionsList_.size();
	} // size

	/**
	 * Writes the objective funcion values of the <code>Solution</code> objects
	 * into the set in a file.
	 *
	 * @param path
	 *            The output file name
	 */
	public void printObjectivesToFile(String path) {
		try {
			/* Open the file */
			FileOutputStream fos = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);

			for (int i = 0; i < solutionsList_.size(); i++) {
				// if (this.vector[i].getFitness()<1.0) {
				bw.write(solutionsList_.get(i).toString());
				bw.newLine();
				// }
			}

			/* Close the file */
			bw.close();
		} catch (IOException e) {
			Configuration.logger_.severe("Error acceding to the file");
			e.printStackTrace();
		}
	} // printObjectivesToFile

	/**
	 * Writes the decision variable values of the <code>Solution</code>
	 * solutions objects into the set in a file.
	 *
	 * @param path
	 *            The output file name
	 */
	public void printVariablesToFile(String path) {
		try {
			/* Open the file */
			FileOutputStream fos = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);

			int numberOfVariables = solutionsList_.get(0).getDecisionVariables().length;
			for (int i = 0; i < solutionsList_.size(); i++) {
				for (int j = 0; j < numberOfVariables; j++) {
					bw.write(solutionsList_.get(i).getDecisionVariables()[j].toString() + " ");
				}
				bw.newLine();
			}

			/* Close the file */
			bw.close();
		} catch (IOException e) {
			Configuration.logger_.severe("Error acceding to the file");
			e.printStackTrace();
		}
	} // printVariablesToFile

	/**
	 * Empties the SolutionSet
	 */
	public void clear() {
		solutionsList_.clear();
	} // clear

	/**
	 * Deletes the <code>Solution</code> at position i in the set.
	 *
	 * @param i
	 *            The position of the solution to remove.
	 */
	public void remove(int i) {
		if (i > solutionsList_.size() - 1) {
			Configuration.logger_.severe("Size is: " + this.size());
		} // if
		solutionsList_.remove(i);
	} // remove

	/**
	 * Returns an <code>Iterator</code> to access to the solution set list.
	 *
	 * @return the <code>Iterator</code>.
	 */
	public Iterator<Solution> iterator() {
		return solutionsList_.iterator();
	} // iterator

	/**
	 * Returns a new <code>SolutionSet</code> which is the result of the union
	 * between the current solution set and the one passed as a parameter.
	 *
	 * @param solutionSet
	 *            SolutionSet to join with the current solutionSet.
	 * @return The result of the union operation.
	 */
	public SolutionSet union(SolutionSet solutionSet) {
		// Check the correct size. In development
		int newSize = this.size() + solutionSet.size();
		if (newSize < capacity_) {
			newSize = capacity_;
		}

		// Create a new population
		SolutionSet union = new SolutionSet(newSize);
		for (int i = 0; i < this.size(); i++) {
			union.add(this.get(i));
		} // for

		for (int i = this.size(); i < (this.size() + solutionSet.size()); i++) {
			union.add(solutionSet.get(i - this.size()));
		} // for

		return union;
	} // union

	/**
	 * Replaces a solution by a new one
	 *
	 * @param position
	 *            The position of the solution to replace
	 * @param solution
	 *            The new solution
	 */
	public void replace(int position, Solution solution) {
		if (position > this.solutionsList_.size()) {
			solutionsList_.add(solution);
		} // if
		solutionsList_.remove(position);
		solutionsList_.add(position, solution);
	} // replace

	/**
	 * Copies the objectives of the solution set to a matrix
	 *
	 * @return A matrix containing the objectives
	 */
	public double[][] writeObjectivesToMatrix() {
		if (this.size() == 0) {
			return null;
		}
		double[][] objectives;
		objectives = new double[size()][get(0).numberOfObjectives()];
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < get(0).numberOfObjectives(); j++) {
				objectives[i][j] = get(i).getObjective(j);
			}
		}
		return objectives;
	} // writeObjectivesMatrix
} // SolutionSet
