/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.breve2D.agent;

import edu.southwestern.breve2D.Breve2DGame;

/**
 *
 * @author Jacob Schrum
 */
public class MultitaskPlayer implements AgentController {

	protected final AgentController[] players;
	protected int task;
	
	/**
	 * Default constructor
	 * @param players 
	 */
	public MultitaskPlayer(AgentController[] players) {
		this.players = players;
		this.task = 0;
	}
	
	/**
	 * Returns number of tasks
	 * @return length of AgentController[] players
	 */
	public int numTasks() {
		return players.length;
	}

	/**
	 * task either becomes 0 or 1 based on the number of tasks
	 */
	public void advanceTask() {
		task++; // accounts for modding 0 by an int
		task %= numTasks();
	}
	
	/**
	 * Returns get action method call at specific task index
	 */
	public Breve2DAction getAction(Breve2DGame game) {
		return players[task].getAction(game);
	}
	
	/**
	 * Resets players at specific index of task
	 */
	public void reset() {
		players[task].reset();
	}
}
