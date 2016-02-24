package edu.utexas.cs.nn.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class PlotLog extends MONELog {

    public PlotLog(String name, ArrayList<String> labels) {
        super(name);
        createPlotFile(labels);
    }

    public final void createPlotFile(ArrayList<String> labels) {
        if (labels != null) {
            File plotFile = new File(directory + prefix + "_log.plot");
            if (!plotFile.exists()) {
                try {
                    PrintStream plotStream = new PrintStream(new FileOutputStream(plotFile));
                    plotStream.println("set style data lines");
                    plotStream.println("set xlabel \"Generation\"");
                    plotStream.println("set yrange [0:]");
                    plotStream.println();

                    for (int i = 0; i < labels.size(); i++) {
                        plotStream.println("set title \"" + prefix + " " + labels.get(i) + "\"");
                        plotStream.println("plot \\");
                        plotStream.println("\"" + prefix + "_log.txt" + "\" u 1:" + (i + 2) + " t \"" + labels.get(i) + "\"");
                        plotStream.println();
                        plotStream.println("pause -1");
                        plotStream.println();
                    }

                    plotStream.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public void log(int gen, ArrayList<Double> values) {
        String log = gen + "\t";
        for (int i = 0; i < values.size(); i++) {
            log += values.get(i) + "\t";
        }
        log(log);
    }
}
