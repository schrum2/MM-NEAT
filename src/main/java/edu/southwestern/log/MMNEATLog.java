package edu.southwestern.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.file.FileUtilities;

/**
 * General logging class. Needs to be generalized more.
 *
 * @author Jacob Schrum
 * @Commented Lauren Gillespie
 */
public class MMNEATLog {

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
	public MMNEATLog(String infix) {
		this(infix, false);
	}

	/**
	 * Constructor for file log
	 * 
	 * @param infix
	 *            name of log file
	 * @param batches
	 *            whether or not log entries come in batches
	 */
	public MMNEATLog(String infix, boolean batches) {
		this(infix, batches, false, false);
	}

	public MMNEATLog(String infix, boolean batches, boolean unlimited) {
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
	public MMNEATLog(String infix, boolean batches, boolean unlimited, boolean restricted) {
		this(infix, batches, unlimited, restricted, false); // Default value of "false" for raw logging
	}
	
	/**
	 * Most general log constructor.
	 * @param infix Part of file name
	 * @param batches Whether logging is in batches
	 * @param unlimited Whether there is no limit on how much is expected to be logged
	 * @param restricted true if there may be an unusually small number of entries per generation
	 * @param raw Overrides other settings: Do not reload old log, just append to it
	 */
	public MMNEATLog(String infix, boolean batches, boolean unlimited, boolean restricted, boolean raw) {
		if(raw) System.out.println(infix + " allows raw logging"); // Just resumes where it left off: no re-load ()
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
			ArrayList<String> oldData = new ArrayList<String>(raw ? 0 : expectedEntries + 1);
			if (file.exists() && !raw) { // Don't read the old file if using raw logging
				Scanner oldFile = new Scanner(file);
				if (batches) {// only occurs if batches of output are in log
					int popSize = Parameters.parameters.integerParameter("mu");
					expectedEntries *= (popSize + 1);
					oldData = new ArrayList<String>(expectedEntries + 1); // Resize accordingly in anticipation
					for (int i = 0; 
							(!restricted || oldFile.hasNextLine()) && // may be fewer log lines than expected
							(i < expectedEntries || // expected number of entries
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
								int gen = temp.nextInt(); // First element of each line is the generation number
								if (i != gen) { // Should match the loop iteration
									System.out.println(file.getAbsolutePath());
									System.out.println("Problem copying over log file on resume");
									System.out.println("Reading line " + i);
									System.out.println("Does not match gen " + gen);
									System.out.println("Line: " + line);
									System.exit(1);
								}
								temp.close();
							}
							oldData.add(line); // Ok to add the line from the old file
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
			// If raw is true, then the stream will append instead of overwriting
			stream = new PrintStream(new FileOutputStream(file, raw));
			if (oldData.size() > 1) { // Why not 0 here?
				for (int i = 0; i < oldData.size(); i++) {
					if (oldData.get(i) != null) {
						stream.println(oldData.get(i));// prints old data
					} else {
						throw new NullPointerException("Why is line " + i + " of the old log file null?\n" + oldData + "\n" + file);
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
