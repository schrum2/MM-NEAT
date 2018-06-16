package edu.utexas.cs.nn.db;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging functions used by some of the DB classes
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class DbLogger {
    public static Logger getLogger() {
        return Logger.getLogger(DbLogger.class.getName());
    }

    public static void logException(String msg, Exception sqle) {
        getLogger().log(Level.SEVERE, msg + ": " + sqle.getMessage(), sqle);
    }

}
