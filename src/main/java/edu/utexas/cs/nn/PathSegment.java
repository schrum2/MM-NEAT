package edu.utexas.cs.nn;

public class PathSegment {

    public Point start;
    public Point end;

    public PathSegment(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public PathSegment join(PathSegment other) {
        if (this.end.getIndex() + 1 == other.start.getIndex()) {
            return new PathSegment(this.start, other.end);
        } else if (this.start.getIndex() - 1 == other.end.getIndex()) {
            return new PathSegment(other.start, this.end);
        } else {
            throw new IllegalArgumentException("Cannot join segments " + this + " and " + other);
        }
    }

    public void prepend(Point p) {
        if (p.getIndex() + 1 == start.getIndex()) {
            start = p;
        } else {
            throw new IllegalArgumentException("Cannot prepend point " + p);
        }
    }

    public void append(Point p) {
        if (p.getIndex() - 1 == end.getIndex()) {
            end = p;
        } else {
            throw new IllegalArgumentException("Cannot append point " + p);
        }
    }
}
