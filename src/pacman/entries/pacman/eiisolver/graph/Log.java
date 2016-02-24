package pacman.entries.pacman.eiisolver.graph;

import java.io.*;

/**
 * Logs to file
 *
 * @author louis
 *
 */
public class Log {

    public static File logFile;
    private static PrintStream out;

    public static void print(String msg) {
        if (logFile == null) {
            return;
        }
        if (out == null) {
            try {
                out = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile)));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logFile = null;
            }
        }
        if (out != null) {
            out.print(msg);
        }
    }

    public static void println(String msg) {
        print(msg);
        if (out != null) {
            out.println();
        }
    }

    public static void println() {
        println("");
    }

    public static void flush() {
        if (out != null) {
            out.flush();
        }
    }
}
