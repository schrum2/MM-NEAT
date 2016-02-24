/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util.datastructures;

/**
 * Tuple of objects
 *
 * @author Jacob Schrum
 */
public class Pair<X, Y> {

    public X t1;
    public Y t2;

    public Pair(X originalReturn, Y originalModify) {
        this.t1 = originalReturn;
        this.t2 = originalModify;
    }

    @Override
    public String toString() {
        return "(" + t1.toString() + "," + t2.toString() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair p = (Pair) other;
            return t1.equals(p.t1) && t2.equals(p.t2);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.t1 != null ? this.t1.hashCode() : 0);
        hash = 41 * hash + (this.t2 != null ? this.t2.hashCode() : 0);
        return hash;
    }
}
