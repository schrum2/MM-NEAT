package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class GeneralNetworkCharacterization<T extends Network> implements BehaviorCharacterization<T> {

    public int syllabusSize;
    protected ArrayList<double[]> syllabus;

    public GeneralNetworkCharacterization() {
        this(Parameters.parameters.integerParameter("syllabusSize"));
    }

    public GeneralNetworkCharacterization(int syllabusSize) {
        this.syllabusSize = syllabusSize;
    }

    public BehaviorVector getBehaviorVector(Score<T> score) {
        return new RealBehaviorVector(getBehaviorVector(score.individual.getPhenotype(), this.syllabus));
    }

    protected ArrayList<Double> getBehaviorVector(T net, ArrayList<double[]> syllabus) {
        net.flush();
        ArrayList<Double> behaviorVector = new ArrayList<Double>(syllabus.size() * net.numOutputs());
        for (int x = 0; x < syllabus.size(); x++) {
            double[] output = net.process(syllabus.get(x));
            for (int i = 0; i < output.length; i++) {
                behaviorVector.add(output[i]);
            }
        }
        return behaviorVector;
    }

    public static ArrayList<double[]> newRandomSyllabus(int num) {
        ArrayList<double[]> syllabus = new ArrayList<double[]>(num);
        for (int i = 0; i < num; i++) {
            double[] example = RandomNumbers.randomBoundedArray(MMNEAT.lowerInputBounds, MMNEAT.upperInputBounds);
            syllabus.add(example);
        }
        return syllabus;
    }

    private void newRandomSyllabus() {
        this.syllabus = newRandomSyllabus(syllabusSize);
    }

    public void prepare() {
        newRandomSyllabus();
    }
}
