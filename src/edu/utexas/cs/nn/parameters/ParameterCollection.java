package edu.utexas.cs.nn.parameters;

import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author Jacob Schrum
 */
public class ParameterCollection<T> {

    private HashMap<String, T> options;
    private HashMap<String, String> descriptions;

    public ParameterCollection() {
        options = new HashMap<String, T>();
        descriptions = new HashMap<String, String>();
    }

    public void add(String label, T value, String description) {
        options.put(label, value);
        descriptions.put(label, description);
    }

    public void change(String label, T value) {
        options.put(label, value);
    }

    public T get(String label) {
        return options.get(label);
    }

    public boolean hasLabel(String label) {
        return options.containsKey(label);
    }

    public void showUsage() {
        for (String key : options.keySet()) {
            System.out.println(key + " = " + get(key));
        }
    }

    public void writeLabels(PrintStream stream) {
        for (String label : options.keySet()) {
            T value = get(label);
            if (value != null) {
                if (value instanceof Class) {
                    Class c = (Class) value;
                    stream.println(label + ":" + c.getName());
                } else {
                    stream.println(label + ":" + value);
                }
            }
        }
    }
}
