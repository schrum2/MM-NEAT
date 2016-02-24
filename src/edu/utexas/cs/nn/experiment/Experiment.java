package edu.utexas.cs.nn.experiment;

/**
 *
 * @author Jacob Schrum
 */
public interface Experiment {

    public void init();

    public void run();

    public boolean shouldStop();
}
