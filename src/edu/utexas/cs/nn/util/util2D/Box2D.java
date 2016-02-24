/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util.util2D;

/**
 *
 * @author Jacob Schrum
 */
public class Box2D {

    private double top;
    private double bottom;
    private double right;
    private double left;

    public Box2D(ILocated2D[] points) {
        top = -Double.MAX_VALUE;
        bottom = Double.MAX_VALUE;
        left = Double.MAX_VALUE;
        right = -Double.MAX_VALUE;
        for (ILocated2D p : points) {
            top = Math.max(top, p.getY());
            bottom = Math.min(bottom, p.getY());
            right = Math.max(right, p.getX());
            left = Math.min(left, p.getX());
        }
    }

    public boolean insideBox(ILocated2D p) {
        return insideBox(p, 0);
    }

    public boolean insideBox(ILocated2D p, double buffer) {
        return p.getX() > (left - buffer) && p.getX() < (right + buffer) && p.getY() > (bottom - buffer) && p.getY() < (top + buffer);
    }
}
