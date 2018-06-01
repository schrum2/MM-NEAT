package edu.southwestern.evolution.nsga2.bd.characterizations;

import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;

/**
 * General network behavior characterization. Default if no characterization
 * defined for domain.
 * 
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public class GeneralNetworkCharacterization<T extends Network> implements BehaviorCharacterization<T> {

	public int syllabusSize;// size of syllabus
	protected ArrayList<double[]> syllabus;// list of inputs to feed into
											// network to get behavior vector

	/**
	 * Default constructor
	 */
	public GeneralNetworkCharacterization() {
		this(Parameters.parameters.integerParameter("syllabusSize"));
	}

	/**
	 * constructor for generalNetworkCharacterization
	 * 
	 * @param syllabusSize
	 *            size of syllabus
	 */
	public GeneralNetworkCharacterization(int syllabusSize) {
		this.syllabusSize = syllabusSize;
	}

	/**
	 * gets behavior vector from raw score
	 * 
	 * @param score
	 *            raw score
	 */
	public BehaviorVector getBehaviorVector(Score<T> score) {
		// easy because can get phenotype of individual from score
		return new RealBehaviorVector(getBehaviorVector(score.individual.getPhenotype(), this.syllabus));
	}

	/**
	 * gets the behavior vector of a specific network based on syllabus
	 * 
	 * @param net
	 *            network
	 * @param syllabus
	 *            syllabus
	 * @return behavior vector
	 */
	protected ArrayList<Double> getBehaviorVector(T net, ArrayList<double[]> syllabus) {
		net.flush();// clears internal state of network
		ArrayList<Double> behaviorVector = new ArrayList<Double>(syllabus.size() * net.numOutputs());
		for (int x = 0; x < syllabus.size(); x++) {// gets behavior vector by
													// processing syllabus
													// through net
			double[] output = net.process(syllabus.get(x));// uses processed
															// syllabus to
															// compare networks'
															// diversity
			for (int i = 0; i < output.length; i++) {
				behaviorVector.add(output[i]);
			}
		}
		return behaviorVector;
	}

	/**
	 * gets a new array list of random inputs to use for behavioral diversity
	 * testing
	 * 
	 * @param num
	 *            size of syllabus
	 * @return new random syllabus
	 */
	public static ArrayList<double[]> newRandomSyllabus(int num) {
		ArrayList<double[]> syllabus = new ArrayList<double[]>(num);
		for (int i = 0; i < num; i++) {
			double[] example = RandomNumbers.randomBoundedArray(MMNEAT.lowerInputBounds, MMNEAT.upperInputBounds);
			syllabus.add(example);
		}
		return syllabus;
	}
	
	public static ArrayList<double[]> newPastExperiencesSyllabus(int num) {

	/**
	 * gives current syllabus object a new set of values
	 */
	private void newRandomSyllabus() {
		this.syllabus = newRandomSyllabus(syllabusSize);
	}

	/**
	 * creates a new random syllabus which must be prepped to test the networks
	 */
	public void prepare() {
		newRandomSyllabus();
	}
}
