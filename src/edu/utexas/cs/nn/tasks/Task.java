package edu.utexas.cs.nn.tasks;

/**
 *
 * @author Jacob Schrum
 */
public interface Task {

    public int numObjectives();

    public double[] minScores();

    public double getTimeStamp();
}
