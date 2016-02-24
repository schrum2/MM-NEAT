package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.graphics.Plot;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.MultinetworkSelectorMsPacManController;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNMsPacMan;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNPacManController;
import edu.utexas.cs.nn.util.datastructures.Interval;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public abstract class EligibilityTimeFramesScore<T extends Network> extends MsPacManObjective<T> {

    private static double lambda;
    private final int mode;

    public EligibilityTimeFramesScore(int mode) {
        this.mode = mode;
        lambda = Parameters.parameters.doubleParameter("eligibilityLambda");
    }

    public double fitness(Organism<T> individual) {
        NNPacManController nn = ((NNMsPacMan<T>) individual).getController();
        LinkedList<Interval<Integer>> usageTimeFrames = ((MultinetworkSelectorMsPacManController) nn).usageTimeFrames[mode];
        List<Integer> eatTimes = eatTimes();
        return fitness(usageTimeFrames, eatTimes);
    }

    public abstract List<Integer> eatTimes();

    public static double fitness(LinkedList<Interval<Integer>> usageTimeFrames, List<Integer> eatTimes) {
        if (eatTimes.isEmpty() || usageTimeFrames.isEmpty()) {
            return 0;
        }
        int lastTime = eatTimes.get(eatTimes.size() - 1);
        // Determine eligibility at all time steps
        double[] eligibility = new double[lastTime + 1];
        double currentEligibility = 0.0;
        Iterator<Interval<Integer>> itr = usageTimeFrames.iterator();
        int lastIntervalEnd = 0;
        while (itr.hasNext()) {
            Interval<Integer> currentFrame = itr.next();
            //System.out.println("Decreasing");
            for (int i = lastIntervalEnd; i < currentFrame.t1 && i < eligibility.length; i++) {
                currentEligibility *= lambda; // decreasing
                eligibility[i] = currentEligibility;
                //System.out.println(eligibility[i]);
            }
            if (currentEligibility == 0.0) {
                currentEligibility = 0.1;
            }
            //System.out.println("Top-Increasing");
            for (int i = currentFrame.t1; i < currentFrame.t2 && i < eligibility.length; i++) {
                currentEligibility = 1.0 - ((1.0 - currentEligibility) * lambda); // increasing
                //currentEligibility = Math.min(1.0, currentEligibility / lambda); // increasing
                eligibility[i] = 1.0; // Full credit within interval
                //System.out.println(eligibility[i]);
            }
            lastIntervalEnd = currentFrame.t2;
            if (lastIntervalEnd >= eligibility.length) {
                break;
            }
        }
        //System.out.println("Decreasing");
        for (int i = lastIntervalEnd; i < eligibility.length; i++) {
            currentEligibility *= lambda; // decreasing
            eligibility[i] = currentEligibility;
            //System.out.println(eligibility[i]);
        }

        // For debugging
//        ArrayList<Double> plotLine = new ArrayList<Double>();
//        for (int i = 0; i < eligibility.length; i++) {
//            plotLine.add(eligibility[i]);
//        }
//        Plot.linePlot(new DrawingPanel(600, 600, "Eligibility"), -0.5, 1.5, plotLine, Color.RED);
//        Executor.waitForEnterKeyPress();

        // Assign fitness basd on eat times
        double fitness = 0;
        for (Integer time : eatTimes) {
            fitness += eligibility[time];
        }
        return fitness;
    }

//    public static double fitness(LinkedList<Interval<Integer>> usageTimeFrames, List<Integer> eatTimes) {
//        if (eatTimes.isEmpty()) {
//            return 0;
//        }
//        Iterator<Interval<Integer>> itr = usageTimeFrames.iterator();
//        double itemsEatenByMode = 0;
//        if (itr.hasNext()) {
//            int firstUse = usageTimeFrames.get(0).t1;
//            int lastTime = eatTimes.get(eatTimes.size() - 1);
//            Interval<Integer> currentFrame = itr.next();
//            int previousTime = 0;
//            int stepsAhead = 0;
//            for (Integer time : eatTimes) {
//                boolean modeEverUsed = time >= firstUse;
//                if (currentFrame.contains(time)) {
//                    itemsEatenByMode++;
//                    stepsAhead = 0;
//                    previousTime = currentFrame.t2 - 1;
//                } else {
//                    stepsAhead = stepsAhead + (time - previousTime);
//                    while (currentFrame.after(time)) {
//                        stepsAhead = time - currentFrame.t2 + 1;
//                        currentFrame = itr.hasNext() ? itr.next() : new Interval<Integer>(true, lastTime + 1, lastTime + 2, false);
//                    }
//                    if (currentFrame.contains(time)) {
//                        itemsEatenByMode++;
//                        stepsAhead = 0;
//                        previousTime = currentFrame.t2 - 1;
//                    } else {
//                        if (modeEverUsed) {
//                            //System.out.println("Time: " + time + ": steps: " + stepsAhead + ": lambda:" + lambda + ": add: " + Math.pow(lambda, stepsAhead));
//                            itemsEatenByMode += Math.pow(lambda, stepsAhead);
//                            previousTime = time;
//                        }
//                    }
//                }
//            }
//        }
//        return itemsEatenByMode;
//    }
    public static void main(String[] args) {
        Parameters.initializeParameterCollections(args);
        lambda = Parameters.parameters.doubleParameter("eligibilityLambda");
        LinkedList<Integer> eatTimes = new LinkedList<Integer>();

        eatTimes.add(1); // 1
        eatTimes.add(2); // 1
        eatTimes.add(3); // 1

        eatTimes.add(5); // 0.9^2 = 0.81

        eatTimes.add(8); // 0.9^5 = 0.59049
        eatTimes.add(10); // 1
        eatTimes.add(12); // 0.9

        eatTimes.add(20); // 1
        eatTimes.add(21); // 1

        LinkedList<Interval<Integer>> usageTimeFrames = new LinkedList<Interval<Integer>>();

        usageTimeFrames.add(new Interval<Integer>(true, 0, 4, false));
        usageTimeFrames.add(new Interval<Integer>(true, 9, 12, false));
        usageTimeFrames.add(new Interval<Integer>(true, 19, 22, false));

        // expected = 8.30049
        System.out.println("Result 1 should be 8.30049: " + fitness(usageTimeFrames, eatTimes));

        eatTimes.add(200); // 1

        usageTimeFrames.add(new Interval<Integer>(true, 30, 40, false));
        usageTimeFrames.add(new Interval<Integer>(true, 100, 130, false));
        usageTimeFrames.add(new Interval<Integer>(true, 190, 220, false));

        // expected = 9.30049
        System.out.println("Result 2 should be 9.30049: " + fitness(usageTimeFrames, eatTimes));

        eatTimes.add(220); // 0.9
        eatTimes.add(221); // 0.81
        eatTimes.add(222); // 0.729
        eatTimes.add(223); // 0.6561
        eatTimes.add(224); // 0.59049
        eatTimes.add(225); // 0.531441

        System.out.println("Result 3 should be 13.517521: " + fitness(usageTimeFrames, eatTimes));

        System.out.println("--------------------------------------");

        LinkedList<Interval<Integer>> timeFrames = new LinkedList<Interval<Integer>>();

        timeFrames.add(new Interval<Integer>(true, 10, 11, false));
        timeFrames.add(new Interval<Integer>(true, 13, 15, false));
        timeFrames.add(new Interval<Integer>(true, 17, 20, false));
        timeFrames.add(new Interval<Integer>(true, 50, 71, false));
        timeFrames.add(new Interval<Integer>(true, 90, 100, false));
        timeFrames.add(new Interval<Integer>(true, 200, 220, false));

        ArrayList<Double> plotLine = new ArrayList<Double>();
        for (int i = 0; i < 300; i++) {
            LinkedList<Integer> point = new LinkedList<Integer>();
            point.add(i);
            double value = fitness(timeFrames, point);
            plotLine.add(value);
            System.out.println(i + ":" + value);
        }

        Plot.linePlot(new DrawingPanel(600, 600, "Eligibility"), -0.5, 1.5, plotLine, Color.RED);
    }
}
