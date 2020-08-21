package edu.southwestern.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class used for launching Python programs from Java.
 * 
 * @author jacobschrum
 */
public class PythonUtil {

    public static String PYTHON_EXECUTABLE = ""; // Overwritten by setting the python executable

    /**
     * Specify the path to the Python executable that will be used
     */
    public static void setPythonProgram() {
        try {
            PYTHON_EXECUTABLE = Files.readAllLines(Paths.get("my_python_path.txt")).get(0); // Should only have one line, get first
        } catch (IOException e) {
            System.err.println("Can not find the my_python_path.txt which specifies the python program and should be in the main MM-NEAT directory.");            
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    
    /**
     * Tells whether the user has created a file that contains a Python path.
     * Does not verify that the path is valid, simply that it has been specified.
     * @return Did the user specify a Python path in the appropriate file?
     */
    public static boolean pythonAvailable() {
    	return new File("my_python_path.txt").exists();
    }
}
