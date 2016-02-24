package edu.utexas.cs.nn.log;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.file.FileUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * General logging class. Needs to be generalized more.
 *
 * @author Jacob Schrum
 */
public class EvalLog {

    protected PrintStream stream;
    protected String directory;
    protected String prefix;

    public EvalLog(String infix) {
        String experimentPrefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber");
        this.prefix = experimentPrefix + "_" + infix;

        String saveTo = Parameters.parameters.stringParameter("saveTo");
        if (saveTo.isEmpty()) {
            System.out.println("Can't maintain logs if no save directory is given");
            System.out.println("infix: " + infix);
            System.exit(1);
        }
        directory = FileUtilities.getSaveDirectory();
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        directory += (directory.equals("") ? "" : "/");
        File file = getFile();
        try {
            stream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not setup log file");
            System.exit(1);
        }
    }

    public void log(String data) {
        stream.println(data);
    }

    public void close() {
        stream.close();
    }

    public File getFile() {
        return new File(directory + prefix + "_log.txt");
    }
}
