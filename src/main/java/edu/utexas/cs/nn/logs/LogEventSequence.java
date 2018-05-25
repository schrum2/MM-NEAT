package edu.utexas.cs.nn.logs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class LogEventSequence {
    public class Sequence {
        SortedMap<Double, LogEntry> timeSeq = new TreeMap<Double, LogEntry>();
        HashMap<Integer, LogEntry> indexSeq = new HashMap<Integer, LogEntry>();
        public int size() { return timeSeq.size(); }
        public LogEntry get(int i) { return indexSeq.get(i); }
        public LogEntry getNext(double t) {
            try {
                Double at = timeSeq.tailMap(t).firstKey();
                return timeSeq.get(at);
            } catch (IllegalArgumentException e) {
                System.err.println("No next event for " + t);
                return null;
            }
        }
        public void addEvent(LogEntry e) {
            indexSeq.put(indexSeq.size(), e);
            timeSeq.put(e.getTime(), e);
        }
    }

    private Map<Integer, Sequence> sequences = new HashMap<Integer, Sequence>();

    public void addEvent(int sequence, LogEntry e) {
        Sequence s;
        if (!sequences.containsKey(sequence)) {
            s = new Sequence();
            sequences.put(sequence, s);
        } else {
            s = sequences.get(sequence);
        }
        s.addEvent(e);
    }

    public int size() {
        int s = 0;
        for (Integer i : sequences.keySet()) {
            s = s + sequences.get(i).size();
        }
        return s;
    }

    public static void main(String[] args) {
        String[] filenames = {
            "DM-DG-Colosseum-001.log",
            "DM-GoatswoodPlay-002.log",
            "DM-GoatswoodPlay-001.log",
            "DM-IceHenge-001.log"
        };
        List<LogEntry> events;
        LogEventSequence sequence = new LogEventSequence();
        for (String filename : filenames) {
            try {
                // parse the event logs
                FileReader fileReader = new FileReader(filename);
                BufferedReader lineReader = new BufferedReader(fileReader);
                LogProcessor processor = new LogProcessor();
                events = processor.parseLogEntries(lineReader);
                for (LogEntry event : events) {
                    sequence.addEvent(1, event);
                }
                lineReader.close();
                fileReader.close();
            } catch (IOException ex) {
                Logger.getLogger(LogProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("there are a total of " + sequence.size() + " events");
    }
}
