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
	
	/**
	 * Default constructor - creates new AgentController array w/new players
	 */
	public PredatorPreyEnemy() {
		super(new AgentController[] { new RushingPlayer(), new EscapingPlayer() });
	}
}
