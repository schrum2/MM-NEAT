package edu.utexas.cs.nn.util;

import edu.utexas.cs.nn.parameters.Parameters;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Jacob Schrum
 */
public class ClassCreation {

	@SuppressWarnings("rawtypes") // Any type is possible, so it must be raw
	public static Object createObject(String label) throws NoSuchMethodException {
		Class className = Parameters.parameters.classParameter(label);
		if (className == null) {
			return null;
		}
		return createObject(className);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" }) // Any type is possible, so it must be raw
	public static Object createObject(Class className) throws NoSuchMethodException {
		Constructor classConstructor = className.getConstructor();
		return ClassCreation.createObject(classConstructor);
	}

	@SuppressWarnings("rawtypes") // Any type is possible, so it must be raw
	public static Object createObject(Constructor constructor) {
		return createObject(constructor, new Object[0]);
	}

	@SuppressWarnings("rawtypes") // Any type is possible, so it must be raw
	public static Object createObject(Constructor constructor, Object[] arguments) {

		System.out.println("Constructor: " + constructor.toString());
		Object object;

		try {
			object = constructor.newInstance(arguments);
			// System.out.println("Object: " + object.toString());
			return object;
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
}
