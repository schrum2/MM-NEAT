package utopia.agentmodel;

import edu.utexas.cs.nn.Constants;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class ActionLog {

    private PrintWriter log = null;
    private boolean fresh = true;

    public ActionLog(String name) {
        this.name = name;
        actionCounts = new HashMap<String, Integer>();
        lastActionLabel = "";

        if (Constants.LOG_CONTROLLER_EVALUATIONS.getBoolean()) {
            try {
                File f = new File(Constants.CONTROLLER_EVALUATIONS_NAME.get() + "/" + name + ".txt");
                if(f.exists()) {
                    //System.out.println("File " + name + " exists!");
                    fresh = false;
                }
                log = new PrintWriter(new FileWriter(f, true));
            } catch (IOException ex) {
                //System.out.println("COULD NOT WRITE LOG: " + name);
            }            
        }
    }

    private void log_println(String txt){
        if(log != null){
            log.println(txt);
        }
    }

    private void log_print(String txt){
        if(log != null){
            //System.out.println("PRINT VALUES");
            log.print(txt);
        }
    }

    private HashMap<String, Integer> getActionCounts() {
        if (actionCounts == null) {
            actionCounts = new HashMap<String, Integer>();
        }
        return actionCounts;
    }
    private String name;
    private String lastActionLabel = null;
    private HashMap<String, Integer> actionCounts;
    private static final int MAX_LABEL_LENGTH = 20;

    /**
     * At the end of a match, this controller can log the frequency of
     * various types of actions in a uniform way using this method.
     * Controllers that extend this class must use "takeAction()" to
     * maintain a running log of action usage.
     * @param actionLog
     */
    public void logActionChoices(PrintWriter actionLog) {
        actionLog.println(name + " Counts");
        int totalCounts = 0;
        StringBuilder sb = new StringBuilder();
        for (String key : getActionCounts().keySet()) {
            sb.append(key).append(":count\t").append(key).append(":total\t").append(key).append(":percent\t");
            totalCounts += getActionCounts().get(key);
        }
        if(fresh) {
            log_println(sb.substring(0));
            fresh = false;
        }
        actionLog.println("Total " + name + " Actions: " + totalCounts);
        for (String key : getActionCounts().keySet()) {
            actionLog.println(actionLogLine(key + blanks(MAX_LABEL_LENGTH - key.length()), getActionCounts().get(key), totalCounts));
        }
        log_println("");
        actionLog.println();

        if(log != null) {
            log.close();
        }
    }

    private static String blanks(int x) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x; i++) {
            result.append(" ");
        }
        return result.substring(0);
    }

    public String actionLogLine(String label, int count, int total) {
        return actionLogLine(this, label, count, total);
    }
    
    public static String actionLogLine(ActionLog evalLog, String label, int count, int total) {
        if (evalLog != null) {
            //System.out.println("EVAL LOG NOT NULL: " + label);
            evalLog.log_print(count + "\t" + total + "\t" + ((100.0 * count) / total) + "\t");
        }
        return label + ": " + count + "/" + total + ": " + ((100.0 * count) / total) + "%";
    }

    /**
     * Throws NullPointerException if the action is not registered before using
     * @param key
     */
    public void takeAction(String key) {
        lastActionLabel = key;
        Integer x = getActionCounts().get(key);
        getActionCounts().put(key, x + 1);
    }

    public String lastActionLabel() {
        return lastActionLabel;
    }

    public void register(String key) {
        getActionCounts().put(key, 0);
    }
}
