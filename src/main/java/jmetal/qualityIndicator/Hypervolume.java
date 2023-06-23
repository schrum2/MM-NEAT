/**
 * Hypervolume.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.qualityIndicator;


/**
 * This class implements the hypervolume metric. The code is the a Java version
 * of the orginal metric implementation by Eckart Zitzler. It can be used also
 * as a command line program just by typing $java Hypervolume
 * <solutionFrontFile> <trueFrontFile> <numberOfOjbectives> Reference: E.
 * Zitzler and L. Thiele Multiobjective Evolutionary Algorithms: A Comparative
 * Case Study and the Strength Pareto Approach, IEEE Transactions on
 * Evolutionary Computation, vol. 3, no. 4, pp. 257-271, 1999.
 */
public class Hypervolume {

	public jmetal.qualityIndicator.util.MetricsUtil utils_;

	/**
	 * Constructor Creates a new instance of MultiDelta
	 */
	public Hypervolume() {
		utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
	} // Hypervolume

	/**
	 * This checks if point1 (an array of scores) is better in any given score to point2
	 * If point2 has any objective score that is greater than point1 it returns false
	 * only returns true if all of point1's scores are greater than or equal to point2's scores
	 * 
	 * returns true if 'point1' dominates 'points2' with respect to the 
	 * first 'numberOfObjectives' objectives
	 * @param point1 an array of scores belonging to one individual
	 * @param point2 an array of scores belonging to one individual
	 * @param numberOfObjectives the number of objectives/scores being compared
	 * @return true if point1 scores higher than point2 in at least one objective
	 */
	boolean dominates(double point1[], double point2[], int numberOfObjectives) {
		int i;
		int betterInAnyObjective;

		betterInAnyObjective = 0;
		for (i = 0; i < numberOfObjectives && point1[i] >= point2[i]; i++) {
			//breaks loop early if point2[i] is greater than point1[i]
			if (point1[i] > point2[i]) {
				betterInAnyObjective = 1;
			}
		}

		return ((i >= numberOfObjectives) && (betterInAnyObjective > 0));
	} // Dominates

	/**
	 * Swaps the double[] of front index i with front index j
	 * front[i][] <--> front[j][]
	 * @param front the collection of scores
	 * @param i	an index in the front
	 * @param j an index in the front
	 */
	void swap(double[][] front, int i, int j) {
		double[] temp;

		temp = front[i];
		front[i] = front[j];
		front[j] = temp;
	} // Swap

	/**
	 * 
	 * 
	 * all nondominated points regarding the first 'numberOfObjectives' dimensions are
	 * collected; the points referenced by 'front[0..numberOfPoints-1]' are
	 * considered; 'front' is resorted, such that 'front[0..n-1]' contains the
	 * nondominated points; n is returned
	 * @param front the collection of scores being considered front[0..numberOfPoints-1][0..numberOfObjectives-1]
	 * @param numberOfPoints the number of score sets or individuals in the front ie front[numberOfPoints][]
	 * @param numberOfObjectives the number of objectives being considered front[][numberOfObjectives]
	 * @return the new number of points in the front (n) such that n is front[0...n-1] TODO: fix this sentence
	 */
	public int filterNondominatedSet(double[][] front, int numberOfPoints, int numberOfObjectives) {
		int i, j;
		int n;

		n = numberOfPoints;	//controls the looping of front comparisons front[0..n-1]
		i = 0;
		while (i < n) {	//outer loop
			j = i + 1;
			while (j < n) {	//inner loop
				if (dominates(front[i], front[j], numberOfObjectives)) {
					/* remove point 'j' */
					n--;	//decrement the total number of front[nPoints], decrements before swap
					swap(front, j, n);	//so that the last element in the front is moved to front[j]
				} else if (dominates(front[j], front[i], numberOfObjectives)) {
					/*
					 * remove point 'i'; ensure that the point copied to index
					 * 'i' is considered in the next outer loop (thus, decrement
					 * i)
					 */
					n--;	//decrement the number of points so that it swaps the last point into spot i
					swap(front, i, n);
					i--;	//so that it reevaluates the element that was swapped in
					break;	//breaks to reevaluate all j elements to the newly swapped in i element
				} else {
					j++;
				}
			}
			i++;
		}
		return n;
	} // FilterNondominatedSet

	/*
	 * calculate next value regarding dimension 'objective'; consider points
	 * referenced in 'front[0..noPoints-1]'
	 */
	double surfaceUnchangedTo(double[][] front, int noPoints, int objective) {
		int i;
		double minValue, value;

		if (noPoints < 1) {
			System.err.println("run-time error");
		}

		minValue = front[0][objective];
		for (i = 1; i < noPoints; i++) {
			value = front[i][objective];
			if (value < minValue) {
				minValue = value;
			}
		}
		return minValue;
	} // SurfaceUnchangedTo

	/*
	 * remove all points which have a value <= 'threshold' regarding the
	 * dimension 'objective'; the points referenced by 'front[0..noPoints-1]'
	 * are considered; 'front' is resorted, such that 'front[0..n-1]' contains
	 * the remaining points; 'n' is returned
	 */
	/**
	 * 
	 * @param front
	 * @param noPoints
	 * @param objective
	 * @param threshold
	 * @return
	 */
	int reduceNondominatedSet(double[][] front, int noPoints, int objective, double threshold) {
		int n;
		int i;

		n = noPoints;
		for (i = 0; i < n; i++) {
			if (front[i][objective] <= threshold) {
				n--;
				swap(front, i, n);
			}
		}

		return n;
	} // ReduceNondominatedSet

	/**
	 * 
	 * @param front
	 * @param numberOfPoints
	 * @param numberOfObjectives
	 * @return
	 */
	public double calculateHypervolume(double[][] front, int numberOfPoints, int numberOfObjectives) {
		int n;
		double volume, distance;

		volume = 0;
		distance = 0;
		n = numberOfPoints;
		while (n > 0) {
			int noNondominatedPoints;
			double tempVolume, tempDistance;

			noNondominatedPoints = filterNondominatedSet(front, n, numberOfObjectives - 1);
			tempVolume = 0;
			if (numberOfObjectives < 3) {
				if (noNondominatedPoints < 1) {
					System.err.println("run-time error");
				}

				tempVolume = front[0][0];
			} else {
				tempVolume = calculateHypervolume(front, noNondominatedPoints, numberOfObjectives - 1);
			}

			tempDistance = surfaceUnchangedTo(front, n, numberOfObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			n = reduceNondominatedSet(front, n, numberOfObjectives - 1, distance);
		}
		return volume;
	} // CalculateHypervolume

	/* merge two fronts */
	/**
	 * 
	 * @param front1
	 * @param sizeFront1
	 * @param front2
	 * @param sizeFront2
	 * @param numberOfObjectives
	 * @return
	 */
	double[][] mergeFronts(double[][] front1, int sizeFront1, double[][] front2, int sizeFront2, int numberOfObjectives) {
		int i, j;
		int numberOfPoints;
		double[][] frontPtr;

		/* allocate memory */
		numberOfPoints = sizeFront1 + sizeFront2;
		frontPtr = new double[numberOfPoints][numberOfObjectives];
		/* copy points */
		numberOfPoints = 0;
		for (i = 0; i < sizeFront1; i++) {
			for (j = 0; j < numberOfObjectives; j++) {
				frontPtr[numberOfPoints][j] = front1[i][j];
			}
			numberOfPoints++;
		}
		for (i = 0; i < sizeFront2; i++) {
			for (j = 0; j < numberOfObjectives; j++) {
				frontPtr[numberOfPoints][j] = front2[i][j];
			}
			numberOfPoints++;
		}

		return frontPtr;
	} // MergeFronts

	/**
	 * Returns the hypevolume value of the paretoFront. This method call to the
	 * calculate hypervolume one
	 *
	 * @param paretoFront The pareto front
	 * @param paretoTrueFront The true pareto front
	 * @param numberOfObjectives Number of objectives of the pareto front
	 */
	public double hypervolume(double[][] paretoFront, double[][] paretoTrueFront, int numberOfObjectives) {

		/**
		 * Stores the maximum values of true pareto front.
		 */
		double[] maximumValues;

		/**
		 * Stores the minimum values of the true pareto front.
		 */
		double[] minimumValues;

		/**
		 * Stores the normalized front.
		 */
		double[][] normalizedFront;

		/**
		 * Stores the inverted front. Needed for minimization problems
		 */
		double[][] invertedFront;

		// STEP 1. Obtain the maximum and minimum values of the Pareto front
		maximumValues = utils_.getMaximumValues(paretoTrueFront, numberOfObjectives);
		minimumValues = utils_.getMinimumValues(paretoTrueFront, numberOfObjectives);

		// STEP 2. Get the normalized front
		normalizedFront = utils_.getNormalizedFront(paretoFront, maximumValues, minimumValues);

		// STEP 3. Inverse the pareto front. This is needed because of the
		// original
		// metric by Zitzler is for maximization problems
		invertedFront = utils_.invertedFront(normalizedFront);

		// STEP4. The hypervolume (control is passed to java version of Zitzler
		// code)
		return this.calculateHypervolume(invertedFront, invertedFront.length, numberOfObjectives);
	}// hypervolume

	/**
	 * This class can be invoqued from the command line. Three params are
	 * required: 1) the name of the file containing the front, 2) the name of
	 * the file containing the true Pareto front 3) the number of objectives
	 */
	public static void main(String args[]) {
		if (args.length < 2) {
			System.err.println("Error using delta. Type: \n java hypervolume " + "<SolutionFrontFile>"
					+ "<TrueFrontFile> + <numberOfObjectives>");
			System.exit(1);
		}

		// Create a new instance of the metric
		Hypervolume qualityIndicator = new Hypervolume();
		// Read the front from the files
		double[][] solutionFront = qualityIndicator.utils_.readFront(args[0]);
		double[][] trueFront = qualityIndicator.utils_.readFront(args[1]);

		// Obtain delta value
		double value = qualityIndicator.hypervolume(solutionFront, trueFront, (new Integer(args[2])).intValue());

		System.out.println(value);
	} // main
} // Hypervolume
