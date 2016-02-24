package pacman.game.util;

import java.util.Date;

public class Log {

    private static String fileName;
    private static Log log = null;
    private StringBuilder msg;
    private boolean timeStamp, console;

    private Log() {
        msg = new StringBuilder();

        fileName = "log.txt";
        timeStamp = false;
        console = false;
    }

    public static Log getLog() {
        if (log == null) {
            log = new Log();
        }

        return log;
    }

    public void enableConsolePrinting() {
        console = true;
    }

    public void disableConsolePrinting() {
        console = false;
    }

    public void setFile(String fileName) {
        Log.fileName = fileName;
    }

    public void enableTimeStamp() {
        timeStamp = true;
    }

    public void disableTimeStamp() {
        timeStamp = false;
    }

    public void log(Object context, String message) {
        if (timeStamp) {
            String string = "[" + new Date().toString() + "; " + context.getClass().toString() + "]\t" + message;

            msg.append(string);

            if (console) {
                System.out.println(string);
            }
        } else {
            String string = "[" + context.getClass().toString() + "]\t" + message;

            msg.append(string);

            if (console) {
                System.out.println(string);
            }
        }
    }

    public void clear() {
        msg = new StringBuilder();
    }

    public void saveLog(boolean append) {
        IO.saveFile(fileName, msg.toString(), append);
    }
}