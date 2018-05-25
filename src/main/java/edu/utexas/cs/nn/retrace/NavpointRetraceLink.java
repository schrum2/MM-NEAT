package edu.utexas.cs.nn.retrace;

import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.Point;

/**
 * A path that leads from one navpoint's Voronoi region to another
 */
public class NavpointRetraceLink {

    private VoronoiRegion from;
    private VoronoiRegion to;
    private PoseSequence path;
    private Point startingPoint;
    private Point endPoint;
    private double length;

    private NavpointRetraceLink()
    {

    }

    public static NavpointRetraceLink CreateLink(
            VoronoiRegion from, VoronoiRegion to, PoseSequence path,
            int startPoint, int endPoint) {
        NavpointRetraceLink link = new NavpointRetraceLink();
        link.from = from;
        link.to = to;
        link.path = path;
        link.startingPoint = path.allPoints.get(startPoint);
        link.endPoint = path.allPoints.get(endPoint);
        link.length = link.endPoint.getDistance() - link.startingPoint.getDistance();
        //if(Constants.VIEW_HRTC_MESSAGES.getBoolean())
        //    System.err.println(
        //        "Link from " + from.getName() + ":" + link.startingPoint +
        //        " to " + to.getName() + ":" + link.endPoint + " of length " + link.length);
        return link;
    }

    public double getLength() {
        return length;
    }

    public PoseSequence getPath() {
        path.jump(startingPoint.getIndex());
        return path;
    }

    public Point getEndPoint() {
        return this.endPoint;
    }
}
