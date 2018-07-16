package edu.utexas.cs.nn.retrace;

import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.Point;
import edu.utexas.cs.nn.db.DbLogger;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sequence of pose points that can be iterated by time or distance offsets
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class PoseSequence extends DbLogger implements Iterable<Point> {
    private List<Point> sequence = new ArrayList<Point>();
    private int current = -1;
    private boolean jumpFlag = false;
    public Map<Integer, KDTree<Point>> navpointIds = new HashMap<Integer, KDTree<Point>>();
    public Map<Integer, Point> allPoints = new HashMap<Integer, Point>();
    // TODO: refactor this to make it a single list and encapsulate the entry/exit info into VR
    public List<Integer> orderOfNavpoints = new LinkedList<Integer>();
    public List<Integer> entryPoints = new LinkedList<Integer>();
    public List<Integer> exitPoints = new LinkedList<Integer>();

    /** how long is too long to wait between samples */
    public static final double TIME_DISCONTINUITY = 5.0;

    /** how far is too far between samples */
    public static final double SPACE_DISCONTINUITY = 250; //100.0;

    private PoseSequence(List<Point> points) {
        this.sequence.addAll(points);
        int i = 0;
        for (Point p : points) {
            p.setIndex(i++);
            p.setIndexBefore(i-1);
            p.setIndexAfter(i+1);
            this.allPoints.put(p.getIndex(), p);
            int id = p.getNavpointId();
            if (id >= 0) {
                KDTree<Point> subset;
                if (navpointIds.containsKey(id)) {
                    subset = navpointIds.get(id);
                } else {
                    subset = new KDTree<Point>(3);
                    navpointIds.put(id, subset);
                }
                // add the navpoint id to the order of regions touched
                if (orderOfNavpoints.isEmpty() || orderOfNavpoints.get(orderOfNavpoints.size()-1) != id) {
                    orderOfNavpoints.add(id);
                    exitPoints.add(p.getIndexBefore());
                    entryPoints.add(p.getIndex());
                }
                try {
                    subset.insert(p.asArray(), p);
                } catch (KeySizeException ex) {
                    logException("Wrong key size when adding points to PoseSequence", ex);
                } catch (KeyDuplicateException ex) {
                    // could legitimately happen, ignore
                }
            }
        }
        if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("This path goes through " + navpointIds.size() + " navpoints");
    }

    KDTree<Point> getPointsInRegion(int id) {
        if (navpointIds.containsKey(id)) {
            return navpointIds.get(id);
        } else {
            return new KDTree<Point>(3);
        }
    }

    public static List<PoseSequence> readSequences(BufferedReader reader) throws IOException {
        List<PoseSequence> sequences = new LinkedList<PoseSequence>();
        while (true) {
            List<Point> sequence = new LinkedList<Point>();
            double distance = 0;
            String line;
            Point prev = null;
            while ((line=reader.readLine()) != null) {
                Point pose = Point.parsePose(line);
                if (pose != null) {
                    if (prev != null) {
                        // detect discontinuities
                        if (pose.getT() - prev.getT() > TIME_DISCONTINUITY) {
                            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("Time discontinuity between: " + prev + " and " + pose);
                            break;
                        }
                        prev.setDistance(distance);
                        double d = pose.getLocation().getDistance(prev.getLocation());
                        if (d <= 0)
                            System.err.println("distance step <= 0! " + prev + " and " + pose);
                        assert(d > 0);
                        distance += d;
                        assert(distance > 0);
                        if (d > SPACE_DISCONTINUITY) {
                            if (Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println();
                            if (d <= 0)
                                Logger.getLogger(PoseSequence.class.getName()).log(Level.WARNING, "Space discontinuity between: {0} and {1}", new Object[]{prev, pose});
                            break;
                        }
                    }
                    sequence.add(pose);
                    prev = pose;
                }
            }
            if (sequence.size() > 1) {
                sequences.add(new PoseSequence(sequence));
                sequence.clear();
            } else {
                break;
            }
        }
        return sequences;
    }

    public boolean hasNext() {
        if (current < 0) current = 0;
        return current < sequence.size();
    }

    public void jump(int to) {
        if (to >= 0 && to < sequence.size() - 1) {
            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("jump: " + sequence.get(to));
            current = to;
        }
    }

    public Point nextByTime(double skipTime) {
        if (current < 0) current = 0;
        if (current >= sequence.size()) return null;
        double startTime = sequence.get(current).getT();
        while (current < sequence.size() && sequence.get(current).getT() - startTime < skipTime) {
            current++;
        }
        if (current >= sequence.size()) {
            return null;
        } else {
            return sequence.get(current);
        }
    }

    public Point peekByTime(double skipTime) {
        if (current < 0) current = 0;
        if (current >= sequence.size()) return null;
        int peek_i = current;
        double startTime = sequence.get(peek_i).getT();
        while (peek_i < sequence.size() && sequence.get(peek_i).getT() - startTime < skipTime) {
            peek_i++;
        }
        if (peek_i >= sequence.size()) {
            return null;
        } else {
            return sequence.get(peek_i);
        }
    }

    public boolean getJumpFlag() {
        return jumpFlag;
    }

    public void resetJumpFlag() {
        jumpFlag = false;
    }

    public Point nextByDistance(double distance) {
        if (current == 0) current = 0;
        if (current >= sequence.size()) return null;
        resetJumpFlag();
        Point pose = sequence.get(current);
        double startDistance = pose.getDistance();
        double currentDistance = startDistance;
        while (currentDistance - startDistance < distance && current < sequence.size()) {
            Point next = sequence.get(current++);
            if (next.isStartOfJump()) {
                jumpFlag = true;
            }
            currentDistance = next.getDistance();
            pose = next;
        }
        if (current >= sequence.size()) {
            return null;
        } else {
            return pose;
        }
    }

    public Point peekByDistance(double distance) {
        int peek_i = current;
        if (peek_i == 0) peek_i = 0;
        if (peek_i >= sequence.size()) return null;
        Point pose = sequence.get(peek_i);
        double startDistance = pose.getDistance();
        double currentDistance = startDistance;
        while (currentDistance - startDistance < distance && peek_i < sequence.size()) {
            Point next = sequence.get(peek_i++);
            currentDistance = next.getDistance();
            pose = next;
        }
        if (peek_i >= sequence.size()) {
            return null;
        } else {
            return pose;
        }
    }

    public int size() {
        return sequence.size();
    }

    public Iterator<Point> iterator() {
        return sequence.iterator();
    }

    public Point getCurrent(){
        return sequence.get(current);
    }

    public Point getEnd(){
        return sequence.get(sequence.size() - 1);
    }
}
