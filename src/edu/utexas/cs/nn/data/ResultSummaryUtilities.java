package edu.utexas.cs.nn.data;

import edu.utexas.cs.nn.log.PerformanceLog;
import edu.utexas.cs.nn.log.TWEANNLog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.MultiplePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.Statistic;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import jmetal.qualityIndicator.Hypervolume;

/**
 * After several runs of an experiment have been executed, this class can be used
 * to process all the data across runs and combine it into a useful summary form.
 * These methods are launched by executing MM-NEAT with "process" as the first
 * command line parameter.
 *
 * @author Jacob Schrum
 */
public class ResultSummaryUtilities {

    public static void processExperiment(String dirPrefix, String filePrefix, int runs, int generations, String logSuffix, String genFileMiddle, String outputDir) throws FileNotFoundException, NoSuchMethodException {
        hypervolumeProcessing(dirPrefix, runs, filePrefix, genFileMiddle, generations, outputDir);
        // Average objective scores
        System.out.println("Average scores: " + outputDir + "/" + filePrefix + "AVG" + logSuffix);
        int num = averageConditionResults(dirPrefix, filePrefix, logSuffix, runs, outputDir);
        plotAverageFitnessesFile(filePrefix, genFileMiddle + "Scores", logSuffix, num, outputDir);
        // TWEANN Info
        String loadFrom = Parameters.parameters.stringParameter("loadFrom");
        if ((loadFrom == null || loadFrom.equals("")) && !(MMNEAT.task instanceof MultiplePopulationTask)) {
        	if(Parameters.parameters.booleanParameter("logTWEANNData")){
            System.out.println("TWEANN Info: " + outputDir + "/" + filePrefix + "AVG" + logSuffix);
            int inputNum = averageConditionResults(dirPrefix, filePrefix, "_TWEANNData_log.txt", runs, outputDir);
            plotInfoFile(filePrefix, "_TWEANNData_log", inputNum, outputDir, TWEANNLog.getLabels());
        	}
        	if(Parameters.parameters.booleanParameter("logPerformance")) {
            System.out.println("Performance Info: " + outputDir + "/" + filePrefix + "AVG" + logSuffix);
            int inputNum = averageConditionResults(dirPrefix, filePrefix, "_Performance_log.txt", runs, outputDir);
            plotInfoFile(filePrefix, "_Performance_log", inputNum, outputDir, PerformanceLog.getLabels());
        	}
        }
    }
    
    public static void hypervolumeProcessing(String dirPrefix, int runs, String filePrefix, String genFileMiddle, int generations, String outputDir) throws FileNotFoundException {
        System.out.println("Calculate hypervolumes: " + dirPrefix + "X/" + filePrefix + "X" + genFileMiddle + "HV.txt");
        hypervolumesByGenerationForAllRuns(dirPrefix, runs, filePrefix, genFileMiddle, "txt", generations);
        // Average hypervolumes
        System.out.println("Average hypervolumes: " + outputDir + "/" + filePrefix + "AVG" + genFileMiddle + "HV.txt");
        averageConditionResults(dirPrefix, filePrefix, genFileMiddle + "HV.txt", runs, outputDir);
        plotHypervolumesFile(filePrefix, genFileMiddle + "HV", runs, outputDir);
    }

    private static void plotInfoFile(String filePrefix, String middle, int num, String outputDir, ArrayList<String> labels) throws FileNotFoundException, NoSuchMethodException {
        String plotFile = outputDir + "/" + filePrefix + "AVG" + middle + ".plot";
        PrintStream out = new PrintStream(new FileOutputStream(plotFile));
        String condition = Parameters.parameters.stringParameter("saveTo");
        out.println("set style data lines");
        out.println("set key left");

        String file = filePrefix + "AVG" + middle + ".txt";

        for (int i = 1; i < num; i += 4) {
//            if(i > 5){
//                out.println("set yrange [0:1]");
//            }
            out.println("set title \"Average " + labels.get(((i - 1) / 4)) + " for " + condition + " by Generation\"");
            out.println("plot \\");
            out.println("\"" + file + "\" u 1:" + ((2 * i) + 1) + " t \"MIN\", \\");
            out.println("\"" + file + "\" u 1:" + ((2 * i) + 3) + " t \"AVG\", \\");
            out.println("\"" + file + "\" u 1:" + ((2 * i) + 5) + " t \"MAX\"");
            out.println("");
            out.println("pause -1");
            out.println("");
        }
        out.close();
    }

    public static int averageConditionResults(String dirPrefix, String filePrefix, String fileSuffix, int runs, String outputDir) throws FileNotFoundException {
        String[] files = new String[runs];
        for (int i = 0; i < runs; i++) {
            files[i] = dirPrefix + i + "/" + filePrefix + i + fileSuffix;
        }
        int num = averageFileColumns(files, outputDir + "/" + filePrefix + "AVG" + fileSuffix);
        return num;
    }

    /*
     * Return number of entities
     */
    public static int averageFileColumns(String[] files, String output) throws FileNotFoundException {
        int result = 0;
        Scanner[] fs = new Scanner[files.length];
        for (int i = 0; i < fs.length; i++) {
            fs[i] = new Scanner(new File(files[i]));
        }
        PrintStream out = new PrintStream(new FileOutputStream(output));
        while (fs[0].hasNextLine()) {
            Scanner[] lines = new Scanner[fs.length];
            for (int i = 0; i < fs.length; i++) {
                String line = fs[i].nextLine();
                lines[i] = new Scanner(line);
            }

            String lineOut = "";
            result = 0;
            while (lines[0].hasNext()) {
                double average = 0;
                double ss = 0;
                for (int i = 0; i < lines.length; i++) {
                    double x = lines[i].nextDouble();
                    double oldAverage = average;
                    average += ((x - average) / (i + 1.0));
                    ss += ((x - average) * (x - oldAverage));
                }
                lineOut += average + "\t" + (ss / (lines.length - 1.0)) + "\t";
                result++;
            }
            out.println(lineOut);
        }
        out.close();
        return result;
    }

    public static void hypervolumesByGenerationForAllRuns(String dirPrefix, int runs, String filePrefix, String fileMiddle, String fileExtension, int generations) throws FileNotFoundException {
        for (int i = 0; i < runs; i++) {
            hypervolumesByGeneration(dirPrefix, i, filePrefix, fileMiddle, fileExtension, generations, dirPrefix + i + "/" + filePrefix);
        }
    }

    public static void hypervolumesByGeneration(String dirPrefix, int run, String filePrefix, String fileMiddle, String fileExtension, int generations, String outputPrefix) throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream(outputPrefix + run + fileMiddle + "HV.txt"));
        for (int i = 0; i < generations; i++) {
            Pair<Double,Integer> hypervolumeAndFrontSize = hypervolumeForGeneration(dirPrefix, run, filePrefix, fileMiddle, i, fileExtension);
            //out.println(i + "\t" + hypervolume);
            out.println(i + "\t" + hypervolumeAndFrontSize.t1 + "\t" + hypervolumeAndFrontSize.t2);
        }
        out.close();
    }

    /**
     * Actually returns both the hypervolume (as a Double) and the size of the
     * Pareto front (the Integer)
     * @param dirPrefix Dir of the specific method
     * @param run The number of the run
     * @param filePrefix Prefix for all data files
     * @param fileMiddle Unique file name part
     * @param generation Which generation to read
     * @param fileExtension How to end file
     * @return pair with hypervolume and the size of the Pareto front
     */
    public static Pair<Double,Integer> hypervolumeForGeneration(String dirPrefix, int run, String filePrefix, String fileMiddle, int generation, String fileExtension) {
        Hypervolume qualityIndicator = new Hypervolume();
        //Read the front from the files
        String file = dirPrefix + run + "/" + filePrefix + run + fileMiddle + generation + "." + fileExtension;
        double[][] fileData = qualityIndicator.utils_.readFront(file);
        double[][] solutions;
        // First drop solution number
        double[][] step1 = dropColumn(fileData, 0);
        // Then drop id numbers
        solutions = dropColumn(step1, 0);
        Task task = MMNEAT.task;
        int numObjectives = task.numObjectives();
        while (solutions[0].length > numObjectives) {
            // Remove extra meta-heuristic objectives
            solutions = dropColumn(solutions, solutions[0].length - 1);
        }

        // Adjust for possibly negative min scores
        double[] mins = MMNEAT.task.minScores();
        for (int i = 0; i < solutions.length; i++) {
            for (int j = 0; j < solutions[0].length; j++) {
                solutions[i][j] -= mins[j];
            }
        }
        
        int noNondominatedPoints = qualityIndicator.filterNondominatedSet(solutions, solutions.length, solutions[0].length);
//        System.out.println("noNondominatedPoints:"+noNondominatedPoints+":"+solutions.length);
//        for(int i = 0; i <= noNondominatedPoints; i++) {
//            System.out.println("\t" + Arrays.toString(solutions[i]));
//        }
        
        //Obtain hypervolume
        double value = qualityIndicator.calculateHypervolume(solutions, solutions.length, solutions[0].length);
        //return value;
        return new Pair<Double,Integer>(value, noNondominatedPoints);
    }

    public static double[][] dropColumn(double[][] data, int col) {
        double[][] result = new double[data.length][data[0].length - 1];
        for (int i = 0; i < data.length; i++) {
            int k = 0;
            for (int j = 0; j < data[0].length; j++) {
                if (j == col) {
                    continue;
                } else {
                    result[i][k++] = data[i][j];
                }
            }
        }
        return result;
    }

    private static void plotHypervolumesFile(String filePrefix, String fileSuffix, int runs, String outputDir) throws FileNotFoundException {
        String plotFile = outputDir + "/" + filePrefix + "All" + fileSuffix + ".plot";
        PrintStream out = new PrintStream(new FileOutputStream(plotFile));
        String condition = Parameters.parameters.stringParameter("saveTo");
        out.println("set style data lines");
        out.println("set key left");
        out.println("set title \"Hypervolumes for " + condition + " by Generation\"");
        out.println("plot \\");
        for (int i = 0; i < runs; i++) {
            String file = condition + i + "/" + filePrefix + i + fileSuffix + ".txt";
            out.println("\"" + file + "\" u 1:2 t \"" + condition + i + "\"" + (i + 1 < runs ? ", \\" : ""));
        }
        out.println("");
        out.println("pause -1");
        out.println("");
        out.println("set title \"Pareto Front Size for " + condition + " by Generation\"");
        out.println("plot \\");
        for (int i = 0; i < runs; i++) {
            String file = condition + i + "/" + filePrefix + i + fileSuffix + ".txt";
            out.println("\"" + file + "\" u 1:3 t \"" + condition + i + "\"" + (i + 1 < runs ? ", \\" : ""));
        }        
        out.println("");
        out.println("pause -1");
        out.close();

        plotFile = outputDir + "/" + filePrefix + "AVG" + fileSuffix + ".plot";
        out = new PrintStream(new FileOutputStream(plotFile));
        String file = filePrefix + "AVG" + fileSuffix + ".txt";
        out.println("set style data lines");
        out.println("set key left");
        out.println("set title \"Average Hypervolume for " + condition + " by Generation\"");
        out.println("plot \\");
        out.println("\"" + file + "\" u 1:3 t \"" + condition + "\"");
        out.println("");
        out.println("pause -1");

        out.println("set yrange [0:]");
        out.println("set title \"Average Pareto Front Size for " + condition + " by Generation\"");
        out.println("plot \\");
        out.println("\"" + file + "\" u 1:5 t \"" + condition + "\"");
        out.println("");
        out.println("pause -1");
        
        out.close();
    }

    private static void plotAverageFitnessesFile(String filePrefix, String middle, String fileSuffix, int num, String outputDir) throws FileNotFoundException {
        String plotFile = outputDir + "/" + filePrefix + "AVG" + middle + ".plot";
        PrintStream out = new PrintStream(new FileOutputStream(plotFile));
        String condition = Parameters.parameters.stringParameter("saveTo");
        out.println("set style data lines");
        out.println("set key left");

        String file = filePrefix + "AVG" + fileSuffix;
        for (int i = 1; i < num; i += 4) {
            Statistic stat = MMNEAT.aggregationOverrides.get((i - 1) / 4);
            String fitnessFunctionName = MMNEAT.fitnessFunctions.get((i - 1) / 4) + (stat == null ? "" : "[" + stat.getClass().getSimpleName() + "]");
            out.println("set title \"Average '" + fitnessFunctionName + "' for " + condition + " by Generation\"");
            out.println("plot \\");
            out.println("\"" + file + "\" u 1:" + ((2 * i) + 1) + " t \"MIN\", \\");
            out.println("\"" + file + "\" u 1:" + ((2 * i) + 3) + " t \"AVG\", \\");
            out.println("\"" + file + "\" u 1:" + ((2 * i) + 5) + " t \"MAX\"");
            out.println("");
            out.println("pause -1");
            out.println("");
        }
        out.close();
    }
}
