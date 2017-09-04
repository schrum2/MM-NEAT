package edu.southwestern.parameters;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * This class represents a collection of command-line parameters of a particular
 * type. Each lookup key is the parameter name that would actually be used at
 * the command-line. The values of these parameters can easily be looked up in
 * the simulation using the Parameters class, which contains several of these
 * Parameter Collections.
 *
 * @author Jacob Schrum
 * @param <T> Type of parameters
 */
public class ParameterCollection<T> {
        // Contains actual parameter values
	private final HashMap<String, T> options; 
        // A helpful description for each parameter
	private final HashMap<String, String> descriptions; 

	/**
	 * Construct new parameter collection with empty HashMaps.
	 */
	public ParameterCollection() {
		options = new HashMap<String, T>();
		descriptions = new HashMap<String, String>();
	}

	/**
	 * Add a parameter with a specified value and description.
	 * 
	 * @param label
	 *            Lookup value for parameter, both at commandline and in
	 *            simulation
	 * @param value
	 *            Actual value of parameter
	 * @param description
	 *            Short text description of the parameter
	 */
	public void add(String label, T value, String description) {
		options.put(label, value);
		descriptions.put(label, description);
	}

	/**
	 * Change the value of a parameter with a particular lookup label
	 * 
	 * @param label
	 *            Lookup value of parameter
	 * @param value
	 *            New value of parameter
	 */
	public void change(String label, T value) {
		options.put(label, value);
	}

	/**
	 * Retrieve parameter value based on lookup label
	 * 
	 * @param label
	 *            Lookup key for parameter
	 * @return Value associated with given key
	 */
	public T get(String label) {
		return options.get(label);
	}

	/**
	 * Check whether the parameter collection even contains a parameter with the
	 * specified label
	 * 
	 * @param label
	 *            Lookup key to check for
	 * @return true if key exists in collection
	 */
	public boolean hasLabel(String label) {
		return options.containsKey(label);
	}

	/**
	 * Print out actual current values associated with all lookup keys in this
	 * collection
	 */
	public void showUsage() {
		for (String key : options.keySet()) {
			System.out.println(key + " = " + get(key));
		}
	}

	/**
	 * Used for saving contents of the parameter collection to file, or more
	 * generally a PrintStream. Each lookup key is listed alongside its value.
	 * 
	 * @param stream
	 *            Where this output is directed. Typically a parameter file.
	 */
	public void writeLabels(PrintStream stream) {
		for (String label : options.keySet()) {
			T value = get(label);
			if (value != null) {
				if (value instanceof Class) {
					@SuppressWarnings("rawtypes")
					Class c = (Class) value; // Special case: Classes are saved
												// using their class names
					stream.println(label + ":" + c.getName());
				} else {
					stream.println(label + ":" + value);
				}
			}
		}
	}
}
