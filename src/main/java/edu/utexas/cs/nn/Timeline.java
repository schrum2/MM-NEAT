package edu.utexas.cs.nn;

/**
 * A collection of points and events along a timeline
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public interface Timeline {
    public Bounds getBounds();
    public Point getRandom();
    public Point getNext(Point p, int offset);
    public Point getNextByTime(Point p, double dt);
    public Point getPoint(int index);
    public Event getEvent(int event);
    public int getNumPoints();
    public int getNumEvents();
}
