package gvgai.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class OptClearJar {
    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
	String dataRuns = "data/gvgai/examples/dataRuns.txt";
	String outputPath = "data/gvgai/outputs/";

	String[] data = new gvgai.tools.IO().readFile(dataRuns);
	PrintWriter writer = new PrintWriter(dataRuns, "UTF-8");
	writer.println(data[0]);
	writer.println("current runs: 0");
	writer.close();

	File[] files = new File(outputPath).listFiles();
	for (int i = 0; i < files.length; i++) {
	    files[i].delete();
	}
    }
}
