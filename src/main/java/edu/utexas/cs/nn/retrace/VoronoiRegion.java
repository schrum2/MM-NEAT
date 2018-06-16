package edu.utexas.cs.nn.retrace;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VoronoiRegion {

    /**
     * Center of the Voronoi region
     */
    private Location location = null;
    /**
     * Name of the object
     */
    private String name = null;
    /**
     * Id of the object in the database (e.g. navpoint id)
     */
    private int id = -1;

    public int getId() {
        return id;
    }

    public VoronoiRegion(Location location, String name, int id) {
        this.location = location;
        this.name = name;
        this.id = id;
    }

    public double[] getKey() {
        return new double[]{location.x, location.y, location.z};
    }

    public String getName() {
        return name;
    }

    /**
     * List of pose sequences passing through this region
     */
    private List<PoseSequence> paths = new LinkedList<PoseSequence>();

    public int getPathCount() {
        return paths.size();
    }

    public List<PoseSequence> getPaths() {
        return paths;
    }

    /**
     * Count points in the region
     */
    public int getPointCount() {
        int count = 0;
        for (PoseSequence seq : paths) {
            count += seq.getPointsInRegion(id).size();
        }
        return count;
    }
}
