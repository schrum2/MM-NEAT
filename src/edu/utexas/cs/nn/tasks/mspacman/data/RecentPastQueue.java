/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.data;

import java.util.LinkedList;
import java.util.Queue;
import pacman.game.Game;

/**
 * The purpose of this class is to track a collection of pacman game states of a
 * certain length. The main intended use is to be aware what ultimately led to a
 * state where pacman died.
 *
 * @author Jacob Schrum
 */
public class RecentPastQueue {

    private Queue<Game> recentStates;
    private int maxQueueLength;

    public RecentPastQueue(int memLength) {
        maxQueueLength = memLength;
        recentStates = new LinkedList<Game>();
    }

    /**
     * Tell the queue which Game state was just experienced
     *
     * @param g
     */
    public void giveCurrentState(Game g) {
        if (recentStates.size() > maxQueueLength) {
            recentStates.poll();
        }
        recentStates.offer(g.copy());
    }
    
    public boolean memoryFull(){
        return recentStates.size() >= maxQueueLength;
    }

    /**
     * Return the Game stored at the beginning of the memory.
     *
     * @return
     */
    public Game beginningOfMemory() {
        return recentStates.peek();
    }

    /**
     * Empty out the queue to start a new Game
     */
    public void empty() {
        recentStates.clear();
    }
}
