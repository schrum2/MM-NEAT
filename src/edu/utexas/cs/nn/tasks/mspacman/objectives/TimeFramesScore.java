package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.MultinetworkSelectorMsPacManController;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNMsPacMan;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNPacManController;
import edu.utexas.cs.nn.util.datastructures.Interval;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public abstract class TimeFramesScore<T extends Network> extends MsPacManObjective<T> {

    private final int mode;

    public TimeFramesScore(int mode) {
        this.mode = mode;
    }

    public double fitness(Organism<T> individual) {
        NNPacManController nn = ((NNMsPacMan<T>) individual).getController();
        LinkedList<Interval<Integer>> usageTimeFrames = ((MultinetworkSelectorMsPacManController) nn).usageTimeFrames[mode];
        List<Integer> eatTimes = eatTimes();
        return fitness(usageTimeFrames, eatTimes);
    }

    public abstract List<Integer> eatTimes();

    public static double fitness(LinkedList<Interval<Integer>> usageTimeFrames, List<Integer> eatTimes) {
        Iterator<Interval<Integer>> itr = usageTimeFrames.iterator();
        int itemsEatenByMode = 0;
        if (itr.hasNext()) {
            Interval<Integer> currentFrame = itr.next();
            for (Integer time : eatTimes) {
                if (currentFrame.contains(time)) {
                    itemsEatenByMode++;
                } else {
                    while (currentFrame.after(time) && itr.hasNext()) {
                        currentFrame = itr.next();
                    }
                    if (currentFrame.contains(time)) {
                        itemsEatenByMode++;
                    }
                }
            }
        }
        return itemsEatenByMode;
    }

    public static void main(String[] args) {
        LinkedList<Integer> eatTimes = new LinkedList<Integer>();

        eatTimes.add(1); // in
        eatTimes.add(2); // in
        eatTimes.add(3); // in

        eatTimes.add(5); // out

        eatTimes.add(8); // out
        eatTimes.add(10); // in
        eatTimes.add(12); // out

        eatTimes.add(20); // in
        eatTimes.add(21); // in

        LinkedList<Interval<Integer>> usageTimeFrames = new LinkedList<Interval<Integer>>();

        usageTimeFrames.add(new Interval<Integer>(true, 0, 4, false));
        usageTimeFrames.add(new Interval<Integer>(true, 9, 12, false));
        usageTimeFrames.add(new Interval<Integer>(true, 19, 22, false));

        // expected = 6
        System.out.println("Result 1 should be 6: " + fitness(usageTimeFrames, eatTimes));

        eatTimes.add(200); // in

        usageTimeFrames.add(new Interval<Integer>(true, 30, 40, false));
        usageTimeFrames.add(new Interval<Integer>(true, 100, 130, false));
        usageTimeFrames.add(new Interval<Integer>(true, 190, 220, false));

        // expected = 7
        System.out.println("Result 2 should be 7: " + fitness(usageTimeFrames, eatTimes));

    }
}
