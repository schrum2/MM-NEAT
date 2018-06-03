package edu.southwestern.evolution.nsga2.bd.characterizations;

import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * gets a new array list of past experienced vectors to use for behavioral diversity testing
	 * @param num size of syllabus
	 * @return new syllabus of past experiences
	 */
	public static ArrayList<double[]> newPastExperiencesSyllabus(int num) {
		ArrayList<double[]> syllabus = new ArrayList<double[]>(num);
		List<double[]> observation = ((RemembersObservations) MMNEAT.task).recallObservations();
		for (int i = 0; i < num; i++) {
			syllabus.add(observation.get((int)RandomNumbers.boundedRandom(0, observation.size())));
		}
		return syllabus;
	}

	/**
	 * gives current syllabus object a new set of random values
	 */
	private void newRandomSyllabus() {
		this.syllabus = newRandomSyllabus(syllabusSize);
	}

	/**
	 * gives current syllabus object a set of intelligently determined values
	 */
	private void newPastExperiencesSyllabus() {
		this.syllabus = newPastExperiencesSyllabus(syllabusSize);
	}

	/**
	 * creates a new syllabus which must be prepped to test the networks
	 */
	public void prepare() {
		if(Parameters.parameters.booleanParameter("rememberParameters")) {
			//using intelligent syllabus of past experiences
			newPastExperiencesSyllabus();
		} else {
			newRandomSyllabus();
		}
<<<<<<< HEAD
	}
	
	public ArrayList<double[]> getSyllabus() {
		return syllabus;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<BehaviorVector> getAllBehaviorVectors(ArrayList<Score<T>> population,
			BehaviorCharacterization characterization) {
		// TODO Auto-generated method stub
		return null;
=======
>>>>>>> 6339d0917af041ac8a768a774a5197bb93a98140
	}
}
