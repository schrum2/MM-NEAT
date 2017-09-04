package edu.southwestern.scores;

/**
 * Interface that provides a constructor for a pair of better objects
 *
 * @author Jacob Schrum
 */
public interface Better<T> {

	public T better(T e1, T e2);
}
