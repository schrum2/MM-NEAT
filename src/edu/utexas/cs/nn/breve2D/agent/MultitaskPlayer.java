/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;

/**
 *
 * @author Jacob Schrum
 */
public class MultitaskPlayer implements AgentController {

    protected final AgentController[] players;
    protected int task;

    public MultitaskPlayer(AgentController[] players) {
        this.players = players;
        this.task = 0;
    }

    public int numTasks() {
        return players.length;
    }

    public void advanceTask() {
        task++;
        task %= numTasks();
    }

    public Breve2DAction getAction(Breve2DGame game) {
        return players[task].getAction(game);
    }

    public void reset() {
        players[task].reset();
    }
}
