package edu.southwestern.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.file.FileUtilities;

/**
 * General logging class. Needs to be generalized more.
 *
 * @author Jacob Schrum
 */
public class EvalLog {

	protected PrintWriter stream;
	protected String directory;
	protected String prefix;

	public EvalLog(String infix) {
		String experimentPrefix = Parameters.parameters.stringParameter("log")
				+ Parameters.parameters.integerParameter("runNumber");
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
		FileWriter file = getFileWriter();
		stream = new PrintWriter(new BufferedWriter(file));
	}

	public void log(String data) {
		stream.println(data);
	}

	public void close() {
		stream.close();
	}

	public String getFilePath() {
		return directory + prefix + "_log.txt";
	}
	
	public FileWriter getFileWriter() {
		try {
			// The true allows for appending to the existing file
			return new FileWriter(getFilePath(),true);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not setup log file");
			System.exit(1);
			return null;
		}
	}
}
