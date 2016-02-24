/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.datastructures.Interval;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class TimeFramesPillScore<T extends Network> extends TimeFramesScore<T> {

    public TimeFramesPillScore(int mode) {
        super(mode);
    }

    @Override
    public List<Integer> eatTimes() {
        return g.getPillEatTimes();
    }

    public static void main(String[] args) {
        LinkedList<Interval<Integer>> usageTimeFrames = new LinkedList<Interval<Integer>>();
        List<Integer> pillEatTimes = new LinkedList<Integer>();

        pillEatTimes.add(3);
        pillEatTimes.add(4);
        pillEatTimes.add(5);    //+
        pillEatTimes.add(6);    //+
        pillEatTimes.add(10);
        pillEatTimes.add(11);   //+
        pillEatTimes.add(12);   //+
        pillEatTimes.add(13);
        pillEatTimes.add(30);   //+
        pillEatTimes.add(31);   //+
        pillEatTimes.add(32);
        pillEatTimes.add(33);

        usageTimeFrames.add(new Interval<Integer>(true, 5, 9, false));
        usageTimeFrames.add(new Interval<Integer>(true, 11, 13, false));
        usageTimeFrames.add(new Interval<Integer>(true, 30, 32, false));

        System.out.println(fitness(usageTimeFrames, pillEatTimes));
    }
}
