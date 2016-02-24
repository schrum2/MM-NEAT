/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.networks;

/**
 *
 * @author Jacob Schrum
 */
public interface ModeSelector {

    public int mode();

    public int numModes();

    public void reset();
}
