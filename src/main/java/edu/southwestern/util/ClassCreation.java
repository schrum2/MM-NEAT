package edu.southwestern.util;

import edu.southwestern.parameters.Parameters;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Jacob Schrum
 */
public class ClassCreation {

	/**
	 * Creates an object from a String referring to a Class.
	 * 
	 * @param label
	 * 				name of the class to create object from
	 * @return Object created from a Class according to label
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("rawtypes") // Any type is possible, so it must be raw
	public static Object createObject(String label) throws NoSuchMethodException {
		Class className = Parameters.parameters.classParameter(label);
		if (className == null) {
			System.out.println("Error: Class Name null for label: " + label);
			return null;
		}
		return createObject(className);
	}
	
	/**
	 * Creates an Object from a Class
	 * used by createObject(String)
	 * 
	 * @param className
	 * 				name of the class to create an object from
	 * @return Object of the input Class
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) // Any type is possible, so it must be raw
	public static Object createObject(Class className) throws NoSuchMethodException {
		Constructor classConstructor = className.getConstructor();
		return ClassCreation.createObject(classConstructor);
	}

	/**
	 * creates an Object from a constructor
	 * used by createObject(Class)
	 * 
	 * @param constructor
	 * 				constructor from the class used to create desired objects
	 * @return Object created with the input constructor
	 */
	@SuppressWarnings("rawtypes") // Any type is possible, so it must be raw
	public static Object createObject(Constructor constructor) {
		return createObject(constructor, new Object[0]);
	}

	/**
	 * creates an Object from a constructor and arguments to be passed into it
	 * 
	 * @param constructor
	 * 				constructor from the class used to create desired objects 
	 * @param arguments
	 * 				arguments used to construct the object
	 * @return object made with specified constructor and arguments
	 */
	@SuppressWarnings("rawtypes") // Any type is possible, so it must be raw
	public static Object createObject(Constructor constructor, Object[] arguments) {

		System.out.println("Constructor: " + constructor.toString());
		Object object;

		try {
			object = constructor.newInstance(arguments);
			return object;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
}
