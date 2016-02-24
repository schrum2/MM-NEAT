package edu.utexas.cs.nn.scores;

/**
 *
 * @author Jacob Schrum
 */
public interface Better<T> {

    public T better(T e1, T e2);
}
