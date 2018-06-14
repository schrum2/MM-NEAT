package edu.utexas.cs.nn.logs;

import cz.cuni.amis.pogamut.base.communication.connection.WorldReader;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;

/**
 * This is both a ReaderProvider and Reader.
 * As a ReaderProvider, it just provides itself.
 * As a WorldReader, it reads from the sequence of our log lines but ignores
 * everything that's not part of the message.
 */
public class LogReader extends WorldReader {

    /** read lines from this reader */
    private BufferedReader lineReader;
    /** string buffer for storing lines in */
    private StringBuffer stringBuffer = new StringBuffer();
    /** we use a queue of entries in order to store the time,id,name information we encounter during the parse */
    private Queue<LogEntry> logEntryQueue = new LinkedList<LogEntry>();

    public LogReader(BufferedReader lineReader) throws IOException {
        this.lineReader = lineReader;
    }

    @Override
    public void close() throws PogamutIOException {
        try {
            lineReader.close();
        } catch (IOException ex) {
            throw new PogamutIOException(ex, this);
        }
    }

    @Override
    public int read(char[] buffer, int off, int len) throws PogamutIOException, ComponentNotRunningException, ComponentPausedException {
        try {
            while (stringBuffer.length() < len) {
                String line = lineReader.readLine();
                if (line == null) {
                    return -1;
                }
                Matcher matcher = LogProcessor.logLinePattern.matcher(line);
                if (matcher.matches()) {
                    double time = Double.valueOf(matcher.group(1));
                    String id = matcher.group(2).trim();
                    String name = matcher.group(3).trim();
                    logEntryQueue.add(new LogEntry(time,id,name));
                    String message = matcher.group(4).trim();
                    stringBuffer.append(message);
                    stringBuffer.append("\n");
                }
            }
            StringReader stringReader = new StringReader(stringBuffer.toString());
            int read = stringReader.read(buffer, off, len);
            if (read > 0) {
                stringBuffer.delete(0, read);
            }
            return read;
        } catch (IOException ex) {
            throw new PogamutIOException(ex, this);
        }
    }

    public LogEntry getNextEntry() {
        return this.logEntryQueue.poll();
    }
}
