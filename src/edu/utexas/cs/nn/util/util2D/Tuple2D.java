package edu.utexas.cs.nn.util.util2D;

import java.awt.geom.Point2D;

/**
 *
 * @author Jacob Schrum
 */
public class Tuple2D extends Point2D.Double implements ILocated2D {

    public Tuple2D(double x, double y) {
        super(x, y);
    }

    public boolean isZero() {
        return (x == 0 && y == 0);
    }

    public double length() {
        return Math.sqrt((x * x) + (y * y));
    }

    public Tuple2D normalize() {
        double len = length();
        return new Tuple2D(x / len, y / len);
    }

    public double angle() {
        double angle = x == 0 ? Math.PI / 2 : Math.acos(Math.abs(this.normalize().x));
        if (x < 0) {
            if (y < 0) {
                angle = Math.PI + angle;
            } else if (y > 0) {
                angle = Math.PI - angle;
            } else if (y == 0) {
                angle = Math.PI;
            }
        } else if (x > 0) {
            if (y < 0) {
                angle = (2 * Math.PI) - angle;
            }
        } else {
            if (y < 0) {
                angle = (3 * Math.PI) / 2;
            } else if (y > 0) {
                angle = Math.PI / 2;
            } else {
                angle = 0;
            }
        }
        return angle;
    }

    public Tuple2D midpoint(Tuple2D position) {
        return add(position).div(2);
    }

    public Tuple2D add(Tuple2D position) {
        return new Tuple2D(x + position.x, y + position.y);
    }

    public Tuple2D sub(ILocated2D rhs) {
        return new Tuple2D(this.x - rhs.getX(), this.y - rhs.getY());
    }

    public Tuple2D mult(double i) {
        return new Tuple2D(x * i, y * i);
    }

    public Tuple2D div(double i) {
        return new Tuple2D(x / i, y / i);
    }

    public Tuple2D getPosition() {
        return this;
    }

    public Tuple2D rotate(double radians) {
        return new Tuple2D(
                (x * Math.cos(radians)) - (y * Math.sin(radians)),
                (x * Math.sin(radians)) + (y * Math.cos(radians)));
    }

    public double distance(ILocated2D other) {
        return distance(new Point2D.Double(other.getX(), other.getY()));
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public double angleBetweenTargets(ILocated2D a, ILocated2D b) {
        double angle1 = a.getPosition().sub(this).angle();
        double angle2 = b.getPosition().sub(this).angle();
        return Math.abs(angle1 - angle2);
    }
}
