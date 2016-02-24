/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util.file;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import static edu.utexas.cs.nn.tasks.LonerTask.NETWORK_WINDOW_OFFSET;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import wox.serial.Easy;

/**
 *
 * @author Jacob Schrum
 */
public class FileUtilities {

    /**
     * Return string holding path to base directory where files for current
     * experiment are saved
     *
     * @return path to directory
     */
    public static String getSaveDirectory() {
        return Parameters.parameters.stringParameter("base") + "/" + Parameters.parameters.stringParameter("saveTo") + Parameters.parameters.integerParameter("runNumber");
    }

    /**
     * Deletes all files in a given directory. Will not work if the directory
     * contains sub-directories
     *
     * @param dir file representing the directory to delete
     */
    public static void deleteDirectoryContents(File dir) {
        assert dir.isDirectory() : "File " + dir + " must be a directory";
        File[] files = dir.listFiles();
        assert files != null : "Directory " + dir + " must contain files to delete";
        for (File file : files) {
            if (!file.delete()) {
                System.out.println("Failed to delete " + file);
                //System.exit(1);
            }
        }
    }

    /**
     * Write a single string to a new file, then close the file
     *
     * @param filename
     * @param contents
     */
    public static void simpleFileWrite(String filename, String contents) {
        PrintStream stream = null;
        try {
            stream = new PrintStream(new FileOutputStream(new File(filename)));
            stream.println(contents);
            stream.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not write '" + contents + "' to '" + filename + "'");
            ex.printStackTrace();
            System.exit(1);
        } finally {
            stream.close();
        }
    }

    /**
     * Read the entire contents of a file into a String and return the String.
     *
     * @param f file to read
     * @return String of file contents
     */
    public static String simpleReadFile(File f) throws FileNotFoundException {
        Scanner s = new Scanner(f);
        s.useDelimiter("\\A"); // Beginning of file, so whole file is returned
        String result = s.next();
        s.close();
        return result;
    }

    /**
     * Draws a given TWEANN genotype directly from xml file
     * @param filename 
     */
    public static void drawTWEANN(String filename) {
        TWEANNGenotype genotype = (TWEANNGenotype) Easy.load(filename);
        DrawingPanel panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolving Network");
        panel.setLocation(NETWORK_WINDOW_OFFSET, 0);
        genotype.getPhenotype().draw(panel);
    }
}
