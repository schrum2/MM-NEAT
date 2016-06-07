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
 * @Commented Lauren Gillespie
 */
public class MONELog {

	protected PrintStream stream;
	protected String directory;
	protected String prefix;
	public String lastLoadedEntry = null;

	/**
	 * Default file log constructor
	 * 
	 * @param infix
	 *            name of log file
	 */
	public MONELog(String infix) {
		this(infix, false);
	}

	/**
	 * Constructor for file log
	 * 
	 * @param infix
	 *            name of log file
	 * @param batches
	 *            whether or not there are multiple batches of files
	 */
	public MONELog(String infix, boolean batches) {
		this(infix, batches, false, false);
	}
        
        public MONELog(String infix, boolean batches, boolean unlimited) {
		this(infix, batches, unlimited, false);
	}

	/**
	 * Constructor for file log. Sets up a new file that logs data from task.
	 * Also saves old data if present
	 * 
	 * @param infix
	 *            name of log file
	 * @param batches
	 *            whether or not each generation contains a batch of lines
	 * @param unlimited
	 *            true if there may be an excessive number of entries
         * @param restricted
         *            true if there may be an unusually small number of entries per generation
	 */
	public MONELog(String infix, boolean batches, boolean unlimited, boolean restricted) {
                if(unlimited) System.out.println(infix + " allows unlimited logging");
                if(restricted) System.out.println(infix + " restricted logging");
		if (Parameters.parameters.booleanParameter("logLock")) {
			// Don't do any file reading
			return;
		}
		String experimentPrefix = Parameters.parameters.stringParameter("log")
				+ Parameters.parameters.integerParameter("runNumber");
		this.prefix = experimentPrefix + "_" + infix;// creates file prefix

		String saveTo = Parameters.parameters.stringParameter("saveTo");
		if (saveTo.isEmpty()) {
			System.out.println("Can't maintain logs if no save directory is given");
			System.out.println("infix: " + infix);
			System.exit(1);
		}
		directory = FileUtilities.getSaveDirectory();// retrieves file directory
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();// makes a new directory
		}
		directory += (directory.equals("") ? "" : "/");
		File file = getFile();
		try {
			int expectedEntries = Parameters.parameters.integerParameter("lastSavedGeneration");
			ArrayList<String> oldData = new ArrayList<String>();
			if (file.exists()) {
				Scanner oldFile = new Scanner(file);
				if (batches) {// only occurs if batches of runs want to be kept
					int popSize = Parameters.parameters.integerParameter("mu");
					expectedEntries *= (popSize + 1);
					for (int i = 0; 
                                                (!restricted || oldFile.hasNextLine()) && // may be fewer log lines than expected
                                                (i < expectedEntries || // expectd number of entries
                                                (unlimited && oldFile.hasNextLine())); // more than expected
                                                 i++) {
						oldData.add(oldFile.nextLine());
					}
				} else {
					for (int i = 0; i < expectedEntries || (unlimited && oldFile.hasNextLine()); i++) {
						try {// sticks all the old data in a new file called old data
							String line = oldFile.nextLine();
							if (!unlimited) { // Expect generation number to be listed
								Scanner temp = new Scanner(line);
								int gen = temp.nextInt();
								if (i != gen) {
									System.out.println(file.getAbsolutePath());
									System.out.println("Problem copying over log file on resume");
									System.out.println("Reading line " + i);
									System.out.println("Does not match gen " + gen);
									System.out.println("Line: " + line);
									System.exit(1);
								}
								temp.close();
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
				oldFile.close();
				if (oldData.size() > 0) {
					lastLoadedEntry = oldData.get(oldData.size() - 1);
				}
			}
			stream = new PrintStream(new FileOutputStream(file));
			if (oldData.size() > 1) {
				for (int i = 0; i < oldData.size(); i++) {
					if (oldData.get(i) != null) {
						stream.println(oldData.get(i));// prints old data
					}
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Could not setup log file");
			System.exit(1);
		}
	}

	/**
	 * logs given data to file log
	 * 
	 * @param data
	 */
	public void log(String data) {
		stream.println(data);
	}

	/**
	 * Closes printstream and therefore closes log
	 */
	public void close() {
		stream.close();
	}

	/**
	 * returns the log file
	 * 
	 * @return log file
	 */
	public File getFile() {
		return new File(directory + prefix + "_log.txt");
	}
}
