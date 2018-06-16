package edu.utexas.cs.nn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class Event {
    // time in the sequence
    private double t;
    private int index;
    private int index_before;
    private int index_after;
    private String type;
//    private Type type;
//    public enum Type {
//        AddInventory,
//        AddShieldStrength,
//        Died,
//        GiveHealth,
//        HandlePickup,
//        HitWall,
//        TakeDamage,
//        Attach,
//        Bump,
//        Detach,
//        Landed
//    }
    public String getType() { return type; }
    public double getTime() { return t; }
    public int getIndex() { return index; }
    public int getIndexBefore() { return index_before; }
    public int getIndexAfter() { return index_after; }
    public void setIndexBefore(int index_before) { this.index_before = index_before; }
    public void setIndexAfter(int index_after) { this.index_after = index_after; }
    public Event(String type, int index, double t) {
        this.type = type;
        this.index = index;
        this.t = t;
    }

    @Override
    public String toString() {
        return getType() + "@" + this.t;
    }

    public static Event parseEvent(int i, String eventLine) {
        if (eventLine == null || eventLine.isEmpty()) return null;
        String[] parts = eventLine.split("[ \t]+");
        if (parts.length != 3) {
            System.err.println("Not enough parts in event line");
            return null;
        }
        double t = Double.valueOf(parts[0]);
        String type_string = parts[1].substring(0,parts[1].length() - 1);
        String type = type_string;
        String rest = parts[2];
        return makeEvent(type, i, t);
    }

    public static Event makeEvent(String type, int i, double t) {
        return new Event(type, i, t);
    }

    public static List<Event> readEventsFromFile(String fname) {
        List<Event> result = new LinkedList<Event>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fname));
            String line = null;
            try {
                int i = 0;
                while ( (line = reader.readLine()) != null ) {
                    Event e = Event.parseEvent(i, line);
                    if (e != null) {
                        result.add(e);
                        i++;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        String fname = "c:/botprize/DATA/2008-12-08-14-17-43-Flux2-player1-events.log";
        List<Event> events = Event.readEventsFromFile(fname);
        for (Event e : events) {
            System.out.println(e);
        }
    }
}