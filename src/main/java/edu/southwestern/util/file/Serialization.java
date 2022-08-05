package edu.southwestern.util.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.southwestern.parameters.Parameters;
import wox.serial.Easy;

/**
 * This class is meant to replace the Easy class that was part of the wox
 * serialization libraries. Wox is deprecated, and has prevented me from
 * upgrading to newer versions of Java. Also, the xml output format,
 * though very readable, is perhaps too verbose, which causes storage
 * and speed issues.
 * 
 * In contrast, this class uses Java's built-in serialization. It's main
 * downside is that the output format is incomprehensible. However, it is
 * smaller and I think quicker as well (purely as a result of being smaller).
 * It is supposedly very sensitive to class changes (they can break compatibility)
 * but I think that is true of any serialization method.
 * 
 * @author Jacob Schrum
 */
public class Serialization {
	// Prints stack trace on failures
	public static boolean debug = true;
	/**
	 * Use Java's built-in approach to serialization to save to file any
	 * class that implements Serializable. The provided filename should
	 * have no extension, because ".ser" is appended to the end.
	 * 
	 * @param ob An object that implements Serializable
	 * @param filename Filename with no extension
	 */
	public static void save(Object ob, String filename) {
		// During transition, allow use of old wox serialization
		if(Parameters.parameters.booleanParameter("useWoxSerialization")) Easy.save(ob, filename+".xml");
		else {
			FileOutputStream fileOutputStream;
			try {
				// A .ser extension is automatically added to all files being saved
				fileOutputStream = new FileOutputStream(filename + ".ser");
	
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(ob);
				objectOutputStream.flush();
				objectOutputStream.close();
			} catch (Exception e) {
				if(debug) e.printStackTrace();
			}
		}
	}
	
	/**
	 * Loads a file previously saved using the save method of this class.
	 * The loaded class must implement Serializable (otherwise it could not
	 * have been saved) and the name must have ".ser" as its extension.
	 * If the code for the class changed between a file being saved and loaded,
	 * then it could cause compatibility issues.
	 * 
	 * @param filename Filename being loaded, absent the implied ".ser" extension
	 * @return Object that was loaded, or null on failure
	 */
	public static Object load(String filename) {
		// During transition, allow use of old wox serialization
		if(Parameters.parameters.booleanParameter("useWoxSerialization")) {
			if(!filename.endsWith(".xml")) filename = filename + ".xml";
			return Easy.load(filename);
		}
		
		try {
			// A .ser extension is automatically added to all files being loaded, unless it is already there
			if(!filename.endsWith(".ser")) filename = filename + ".ser";
			FileInputStream fileInputStream = new FileInputStream(filename);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			Object result = objectInputStream.readObject();
			objectInputStream.close(); 
			return result;
		}catch(Exception e) {
			if(debug) e.printStackTrace();
			return null;
		}
	}
}
