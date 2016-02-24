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
public class MONELog {

    protected PrintStream stream;
    protected String directory;
    protected String prefix;
    public String lastLoadedEntry = null;

    public MONELog(String infix) {
        this(infix, false);
    }

    public MONELog(String infix, boolean batches) {
        this(infix, batches, false);
    }

    public MONELog(String infix, boolean batches, boolean unlimited) {
        if(Parameters.parameters.booleanParameter("logLock")){
            // Don't do any file reading
            return;
        }
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
            int expectedEntries = Parameters.parameters.integerParameter("lastSavedGeneration");
            ArrayList<String> oldData = new ArrayList<String>();
            if (file.exists()) {
                //System.out.println(file.getName() + " exists");
                Scanner oldFile = new Scanner(file);
                if (batches) {
                    int popSize = Parameters.parameters.integerParameter("mu");
                    expectedEntries *= (popSize + 1);
                    for (int i = 0; i < expectedEntries || (unlimited && oldFile.hasNextLine()); i++) {
                        oldData.add(oldFile.nextLine());
                    }
                } else {
                    //System.out.println("Expect " + expectedEntries + " entries");
                    for (int i = 0; i < expectedEntries || (unlimited && oldFile.hasNextLine()); i++) {
                        try {
                            String line = oldFile.nextLine();
                            if(!unlimited){ // Expect generation number to be listed
                                Scanner temp = new Scanner(line);
                                int gen = temp.nextInt();
                                if(i != gen){
                                    System.out.println(file.getAbsolutePath());
                                    System.out.println("Problem copying over log file on resume");
                                    System.out.println("Reading line " + i);
                                    System.out.println("Does not match gen " + gen);
                                    System.out.println("Line: " + line);
                                    System.exit(1);
                                }
                            }
                            oldData.add(line);
                        } catch (NoSuchElementException nse) {
                            System.out.println(file.getAbsolutePath());
                            System.out.println("Failure reading line " + i + " out of an expected " + expectedEntries);
                            System.out.println("Last line successfully read:");
                            System.out.println(oldData.get(oldData.size() - 1));
                            nse.printStackTrace();
                            System.exit(1);
                        }
                    }
                }

                if (oldData.size() > 0) {
                    lastLoadedEntry = oldData.get(oldData.size() - 1);
                }
            }
            stream = new PrintStream(new FileOutputStream(file));
            if (oldData.size() > 1) {
                for (int i = 0; i < oldData.size(); i++) {
                    if (oldData.get(i) != null) {
                        stream.println(oldData.get(i));
                    }
                }
            }
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
