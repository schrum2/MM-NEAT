/**
 * Configuration.java
 *
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.util;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This class contain types and constant definitions
 */
public class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Defines the default number of bits used for binary coded variables.
	 */
	// public static final int DEFAULT_PRECISION = 30;
	/**
	 * Defines variable types as an enumerative type. More variable types can be
	 * adding their names to this type, writing the corresponding class in
	 * <code>jmetal.base.variable</code>, and updating the class
	 * <code>jmetal.base.DecisionVariables</code>.
	 */
	// public enum VariableType_ {Binary, BinaryReal, Real, Int, Permutation,
	// ArrayInt, ArrayReal};
	/**
	 * Defines solution types as an enumerative type. Each solution has a type,
	 * which is defined here. The solution types are needed to decide what
	 * operators are available to a given solution. By default, it assumed that
	 * when the name of a solution type is equal to the name of a variable type
	 * (e.g., Real, Binary, etc.), all the variables of the solution have the
	 * same type. New solution types can be defined adding their names to this
	 * enumerate type. The <code>Undefined</code> is the default type of newly
	 * created variables, and it is meant to control error if the type of a
	 * solution has not be properly set.
	 */
	// public enum SolutionType_ {Undefined, Binary, BinaryReal, Real, Int,
	// Permutation, IntReal, ArrayInt, ArrayReal};
	/**
	 * Logger object
	 */
	public static Logger logger_ = Logger.getLogger("jMetal");
} // Configuration
