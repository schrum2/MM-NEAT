package edu.utexas.cs.nn.retrace;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.Point;
import edu.utexas.cs.nn.db.DbLogger;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * This index is a collection of PoseSequence and VoronoiRegion objects that
 * supports finding paths that pass through the closest region and also the
 * closest path to a particular point.
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class SimpleNavPointIndex extends DbLogger {
    List<PoseSequence> sequences = new LinkedList<PoseSequence>();
    
    KDTree<VoronoiRegion> kdRegions = new KDTree<VoronoiRegion>(3);

    Map<Integer, VoronoiRegion> idRegions = new HashMap<Integer, VoronoiRegion>();

    Map<Integer, NavpointRetraceLink> links = new HashMap<Integer, NavpointRetraceLink>();

    Map<PoseSequence, Integer> timeoutCorner = new HashMap<PoseSequence, Integer>();
    
    int size = 0;

    public SimpleNavPointIndex(String level) {
        level = level.toLowerCase();
        loadNavpoints(level);
        loadPose(level);
    }

    private int getLinkIndex(VoronoiRegion from, VoronoiRegion to) {
        return from.getId() * idRegions.size() + to.getId();
    }

    public NavpointRetraceLink getLink(Location a, Location b) {
        VoronoiRegion from = getNearestRegion(a);
        VoronoiRegion to = getNearestRegion(b);
        int linkIndex = getLinkIndex(from, to);
        if (links.containsKey(linkIndex)) {
            return links.get(linkIndex);
        } else {
            return null;
        }
    }

    private void loadNavpoints(String level) {
        try {
            //Register the JDBC driver for sqlite.
            Class.forName("org.sqlite.JDBC");

            Connection navpointsConnection = DriverManager.getConnection("jdbc:sqlite:DATA/navpoints.db");

            Statement selectNavpoint = navpointsConnection.createStatement();
            selectNavpoint.execute("SELECT id, unreal_id, level, x, y, z FROM navpoint WHERE lower(level) = '" + level + "'");
            ResultSet rs = selectNavpoint.getResultSet();
            while (rs.next()) {
                int id = rs.getInt("id");
                String unreal_id = rs.getString("unreal_id");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                idRegions.put(id, new VoronoiRegion(new Location(x,y,z), unreal_id, id));
            }
            navpointsConnection.close();
        } catch (ClassNotFoundException cnfe) {
            logException("Could not create SQLite DbRecoder: {0}", cnfe);
        } catch (SQLException sqle) {
            logException("Could not create SQLite DbRecorder", sqle);
        }
    }

    private void loadPose(final String level) {
        // reading pose sequences
        File traceHome = new File(Constants.HUMAN_DATA_PATH.get());
        File[] levelDirs = traceHome.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().startsWith(level) && pathname.isDirectory();
            }
        });
        if (levelDirs == null) {
            System.err.println("Could not find any data in " + traceHome);
            getLogger().log(Level.SEVERE, "Could not find any data in {0}", traceHome);
        }
        for (File levelDir : levelDirs) {
            File[] dataFiles = levelDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().startsWith("segment") && pathname.getName().endsWith(".indexed.dat");
                }
            });

            if (dataFiles != null) {
                for (File dataFile : dataFiles) {
                    if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println(dataFile.getName());
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(dataFile));
                        sequences.addAll(PoseSequence.readSequences(reader));
                    } catch (FileNotFoundException ex) {
                        getLogger().log(Level.SEVERE, "Data file not found", ex);
                    } catch (IOException ioe) {
                        getLogger().log(Level.SEVERE, "Error while reading pose data file", ioe);
                    }
                }
            }
            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("done reading sequences: " + sequences.size() + " in dir " + levelDir);
        }

        // add the pose sequences to the regions through which they pass
        // add the size of each sequence to the total point count
        for (PoseSequence seq : sequences) {
            // only add non-trivial traces
            if (seq.navpointIds.size() > 1) {
                size += seq.size();
                for (Integer navpointId : seq.navpointIds.keySet()) {
                    if (idRegions.containsKey(navpointId)) {
                        VoronoiRegion region = idRegions.get(navpointId);
                        region.getPaths().add(seq);
                    }
                }
                if (Constants.HUMAN_TRACE_PATH_CONTROLLER.getBoolean()) {
                    for (int i = 0; i < seq.orderOfNavpoints.size() - 1; i++) {
                        int from = seq.orderOfNavpoints.get(i);
                        int fromEntryPoint = seq.entryPoints.get(i);
                        int fromExitPoint = seq.exitPoints.get(i);
                        int startPoint = (fromEntryPoint + fromExitPoint) / 2;
                        for (int j = i+1; j < seq.orderOfNavpoints.size() - 1; j++) {
                            int to = seq.orderOfNavpoints.get(j);
                            int toEntryPoint = seq.entryPoints.get(j);
                            int toExitPoint = seq.exitPoints.get(j);
                            int endPoint = (toEntryPoint + toExitPoint) / 2;
                            if (idRegions.containsKey(from) && idRegions.containsKey(to)) {
                                VoronoiRegion fromRegion = idRegions.get(from);
                                VoronoiRegion toRegion = idRegions.get(to);
                                NavpointRetraceLink link =
                                        NavpointRetraceLink.CreateLink(fromRegion, toRegion, seq, startPoint, endPoint);
                                int linkIndex = getLinkIndex(fromRegion, toRegion);
                                if (links.containsKey(linkIndex)) {
                                    // path already present, see if it was better
                                    NavpointRetraceLink oldLink = links.get(linkIndex);
                                    if (oldLink.getLength() <= link.getLength()) {
                                        // if it was keep it
                                        link = oldLink;
                                    }
                                }
                                links.put(linkIndex, link);
                            }
                        }
                    }
                    System.err.println("HRC:LINKS_ADDED: " + links.size());
                }
            }
        }

        // form the kd tree that will allow us to find the closest region with
        for (VoronoiRegion region : idRegions.values()) {
            try {
                kdRegions.insert(region.getKey(), region);
            } catch (KeySizeException ex) {
                logException("KeySizeException", ex);
            } catch (KeyDuplicateException ex) {
                logException("KeyDuplicateException", ex);
            }
        }
    }

    public void printVoronoiCounts(PrintStream writer) {
        // form the kd tree that will allow us to find the closest region with
        for (VoronoiRegion region : idRegions.values()) {
            writer.println("Voronoi region around " + region.getName() +
                    " with id " + region.getId() +
                    " intersects " + region.getPathCount() + " paths and " +
                    region.getPointCount() + " points");
        }
    }

    private VoronoiRegion getNearestRegion(Location loc) {
        try {
            return this.kdRegions.nearest(new double[]{loc.x, loc.y, loc.z});
        } catch (KeySizeException ex) {
            return null;
        }
    }

    /**
     * Get all the paths that pass through the region closest to the location
     * @param loc
     * @return
     */
    List<PoseSequence> getNearbyPaths(Location loc) {
        VoronoiRegion region = getNearestRegion(loc);
        if (region != null) {
            return region.getPaths();
        } else {
            return new LinkedList<PoseSequence>();
        }
    }

    private void tickTimeoutCorner() {
        Iterator<Map.Entry<PoseSequence, Integer>> iterator = timeoutCorner.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<PoseSequence, Integer> entry = iterator.next();
            int countdown = entry.getValue() - 1;
            if (countdown < 1) {
                iterator.remove();
            } else {
                entry.setValue(countdown);
            }
        }
    }

    /**
     * Put the sequence seq into timeout so that it is not used for timeout
     * steps.
     * @param seq
     * @param timeout
     */
    public void putInTimeout(PoseSequence seq, int timeout) {
        this.timeoutCorner.put(seq, timeout);
    }

    public void resetTimeout() {
        this.timeoutCorner.clear();
    }

    /**
     * Return the path closest to the specified location, and wind it to the
     * closest point so that calling next or nextByDistance will continue
     * from there.
     * @param loc
     * @return PoseSequence that contains the point closest to loc, ready to iterate from that point
     */
    PoseSequence getClosestPath(Location loc) {
        tickTimeoutCorner();
        try {
            if (this.size() == 0) {
                System.err.println("Empty index!");
                return null;
            }
            if (this.kdRegions.size() == 0) {
                System.err.println("empty kdRegion");
                return null;
            }
            double[] key = new double[] { loc.x, loc.y, loc.z };
            VoronoiRegion region = this.kdRegions.nearest(key);
            PoseSequence bestPath = null;
            Point overallBestPoint = null;
            for (PoseSequence seq : region.getPaths()) {
                if (!this.timeoutCorner.containsKey(seq)) {
                    KDTree<Point> points = seq.getPointsInRegion(region.getId());
                    Point bestPoint = points.nearest(key);
                    if (bestPoint.getIndex() + 1 < size()) {
                        if (overallBestPoint == null) {
                            overallBestPoint = bestPoint;
                            bestPath = seq;
                        } else if (overallBestPoint.getLocation().getDistance(loc) > bestPoint.getLocation().getDistance(loc)) {
                            // better distance? check if path is ending
                            if (bestPoint.getIndex() < seq.size() - 1) {
                                overallBestPoint = bestPoint;
                                bestPath = seq;
                            } else {
                                // TODO: consider reversing path
                            }
                        }
                    }
                } else {
                    System.err.println("timeout: " + seq);
                }
            }
            if (bestPath != null && overallBestPoint != null) {
                if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println("best path through " + overallBestPoint + " of length " + bestPath.size());
                bestPath.jump(overallBestPoint.getIndex()); // wind the path to the best point
                return bestPath;
            } else {
                // TODO: generate a synthetic path to the center of the navpoint
                System.err.println("empty region: " + region.getName() + ", " + region.getPaths().size());
                return null;
            }
        } catch (KeySizeException ex) {
            logException("Could not get closest path!", ex);
            return null;
        }
    }

    PoseSequence recalibratePath(PoseSequence seq, Location loc) {
        try {
            if (this.size() == 0) return seq;
            if (this.kdRegions.size() == 0) return seq;
            double[] key = new double[]{loc.x, loc.y, loc.z};
            VoronoiRegion region = this.kdRegions.nearest(key);
            KDTree<Point> points = seq.getPointsInRegion(region.getId());
            if (points.size() == 0) {
                return getClosestPath(loc);
            }
            Point bestPoint = points.nearest(key);
            if (bestPoint != null) {
                seq.jump(bestPoint.getIndex());
            }
            return seq;
        } catch (KeySizeException ex) {
            logException("Could not get closest path!", ex);
            return null;
        }
    }
    
    public int size() {
        return size;
    }

    public static void main(String[] args) {
        SimpleNavPointIndex index = new SimpleNavPointIndex("DM-Antalus");
        PoseSequence seq = index.sequences.get(Constants.random.nextInt(index.sequences.size()));
        Point p1 = seq.nextByTime(0);
        while (seq.hasNext()) {
            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println(p1);
            Point p2a = seq.peekByDistance(100);
            Point p2b = seq.nextByDistance(100);
            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println(p2a + " :: " + p2b);
            if (p2a != p2b) {
                throw new RuntimeException("peek and next ByDistance are not the same!");
            }
            Point p3a = seq.peekByTime(0.3);
            Point p3b = seq.nextByTime(0.3);
            if(Constants.VIEW_HRTC_MESSAGES.getBoolean()) System.out.println(p3a + " :: " + p3b);
            if (p3a != p3b) {
                throw new RuntimeException("peek and next ByTime are not the same!");
            }
        }
    }
}
