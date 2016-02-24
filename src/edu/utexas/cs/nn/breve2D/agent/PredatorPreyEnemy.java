/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.breve2D.agent;

/**
 *
 * @author Jacob Schrum
 */
public class PredatorPreyEnemy extends MultitaskPlayer {

    public PredatorPreyEnemy() {
        super(new AgentController[]{new RushingPlayer(), new EscapingPlayer()});
    }
}
