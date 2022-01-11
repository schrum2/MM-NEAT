package edu.southwestern.tasks;

/**
 * Solutions to this task (or portions of the solution) are
 * bounded real-valued vectors. Therefore, a way of checking
 * the bounds of the values is needed.
 * 
 * @author Jacob Schrum
 *
 */
public interface BoundedTask {
	/**
	 * An array of upper-bound values corresponding to each position in
	 * a solution vector.
	 * @return upper-bound values
	 */
	public double[] getUpperBounds();
	
	/**
	 * An array of lower-bound values corresponding to each position in
	 * a solution vector.
	 * @return lower-bound values
	 */
	public double[] getLowerBounds();
}
