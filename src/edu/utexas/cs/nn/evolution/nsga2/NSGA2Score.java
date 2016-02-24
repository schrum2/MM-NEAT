package edu.utexas.cs.nn.evolution.nsga2;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class NSGA2Score<T> extends MultiObjectiveScore<T> {

    public int numDominators;
    private ArrayList<NSGA2Score<T>> dominatedSet;
    public boolean isAssigned;
    public boolean processed;
    protected double crowdingDistance;
    protected int rank;

    public NSGA2Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats) {
        super(individual, scores, behaviorVector, otherStats);
        reset();
    }

    public NSGA2Score(Score<T> s) {
        this(s.individual, s.scores, s.behaviorVector, s.otherStats);
    }

    @Override
    public String toString() {
        return "c=" + this.crowdingDistance + ":r=" + this.rank + ":" + super.toString();
    }

    public final void reset() {
        isAssigned = false;
        processed = false;
        numDominators = 0;
        dominatedSet = new ArrayList<NSGA2Score<T>>();
        rank = Integer.MAX_VALUE;
    }

    public void addDominatedIndividual(NSGA2Score<T> individual) {
        dominatedSet.add(individual);
    }

    public void increaseNumDominators() {
        numDominators++;
    }

    public void decreaseNumDominators() {
        numDominators--;
    }

    public void assign(int front) {
        rank = front;
        isAssigned = true;
    }

    public void process() {
        for (NSGA2Score<T> score : dominatedSet) {
            score.decreaseNumDominators();
        }
        processed = true;
    }

    public void setCrowdingDistance(double distance) {
        this.crowdingDistance = distance;
    }

    public double getCrowdingDistance() {
        return this.crowdingDistance;
    }

    public int getRank() {
        return this.rank;
    }
    
    public boolean useObjective(int objective) {
        return true;
    }
}
