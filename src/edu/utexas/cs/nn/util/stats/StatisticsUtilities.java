package edu.utexas.cs.nn.util.stats;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 *
 * @author Jacob Schrum
 */
public class StatisticsUtilities {

    /*
     * @param values array of input values @param p the percentile to compute
     * @param begin the first (0-based) element to include in the computation
     * @return the percentile value @throws IllegalArgumentException if the
     * parameters are not valid or the input array is null
     */
    public static double percentile(final double[] values, final double p) {
        if ((p > 100) || (p <= 0)) {
            throw new IllegalArgumentException("invalid quantile value: " + p);
        }
        double n = (double) values.length;
        if (n == 0) {
            return Double.NaN;
        }
        if (n == 1) {
            return values[0]; // always return single value for n = 1
        }
        double pos = p * (n + 1) / 100;
        double fpos = Math.floor(pos);
        int intPos = (int) fpos;
        double dif = pos - fpos;
        double[] sorted = new double[values.length];
        System.arraycopy(values, 0, sorted, 0, values.length);
        Arrays.sort(sorted);

        if (pos < 1) {
            return sorted[0];
        }
        if (pos >= n) {
            return sorted[values.length - 1];
        }
        double lower = sorted[intPos - 1];
        double upper = sorted[intPos];
        return lower + dif * (upper - lower);
    }

    public static double median(double[] xs) {
        Arrays.sort(xs);
        if (xs.length % 2 == 0) { //even
            return (xs[(xs.length / 2) - 1] + xs[xs.length / 2]) / 2.0;
        } else { //odd
            return xs[xs.length / 2];
        }
    }

    /**
     * The values in xs are checked for the max value, and the array index
     * corresponding to the max is returned. If there is a tie, then the
     * randomArgMaxTieBreak setting determines whether a random max is chosen
     * or the first of the maxes is chosen.
     * @param xs values to check for max
     * @return array index corresponding to max
     */
    public static int argmax(double[] xs) {
        double max = -Double.MAX_VALUE;
        ArrayList<Integer> equalMaxIndexes = new ArrayList<Integer>(xs.length);
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] == max) {
                equalMaxIndexes.add(i);
            } else if (xs[i] > max) {
                max = xs[i];
                equalMaxIndexes.clear();
                equalMaxIndexes.add(i);
            }
        }
        return equalMaxIndexes.get(CommonConstants.randomArgMaxTieBreak ? RandomNumbers.randomGenerator.nextInt(equalMaxIndexes.size()) : 0);
    }

    /**
     * Exactly same as double[] version above, but for int[]
     * @param xs
     * @return 
     */
    public static int argmax(int[] xs) {
        double max = -Double.MAX_VALUE;
        ArrayList<Integer> equalMaxIndexes = new ArrayList<Integer>(xs.length);
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] == max) {
                equalMaxIndexes.add(i);
            } else if (xs[i] > max) {
                max = xs[i];
                equalMaxIndexes.clear();
                equalMaxIndexes.add(i);
            }
        }
        return equalMaxIndexes.get(CommonConstants.randomArgMaxTieBreak ? RandomNumbers.randomGenerator.nextInt(equalMaxIndexes.size()) : 0);
    }

    /**
     * rank can make the result more specific, to get the max (rank = 0),
     * next highest (rank = 1), or next (rank = 2), etc.
     * @param xs
     * @param rank
     * @return 
     */
    public static int argmax(int[] xs, int rank) {
        Pair<Integer, Integer>[] indexValuePairs = new Pair[xs.length];
        for(int i = 0; i < xs.length; i++) {
            indexValuePairs[i] = new Pair<Integer,Integer>(i, xs[i]);
        }
        Arrays.sort(indexValuePairs, new Comparator<Pair<Integer, Integer>>() {
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                // second values are actual usages to be sorted by
                return (int) Math.signum(o2.t2 - o1.t2); // descending order sort
            }
        });
        return indexValuePairs[rank].t1;
    }
    
    /**
     * The values in xs are checked for the min value, and the array index
     * corresponding to the min is returned. If there is a tie, then the
     * randomArgMaxTieBreak setting determines whether a random min is chosen
     * or the first of the mins is chosen.
     * @param xs values to check for min
     * @return array index corresponding to min
     */
    public static int argmin(double[] xs) {
        double min = Double.MAX_VALUE;
        ArrayList<Integer> equalMinIndexes = new ArrayList<Integer>(xs.length);
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] == min) {
                equalMinIndexes.add(i);
            } else if (xs[i] < min) {
                min = xs[i];
                equalMinIndexes.clear();
                equalMinIndexes.add(i);
            }
        }
        return equalMinIndexes.get(CommonConstants.randomArgMaxTieBreak ? RandomNumbers.randomGenerator.nextInt(equalMinIndexes.size()) : 0);
    }

    public static int argmin(int[] xs) {
        double min = Double.MAX_VALUE;
        ArrayList<Integer> equalMinIndexes = new ArrayList<Integer>(xs.length);
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] == min) {
                equalMinIndexes.add(i);
            } else if (xs[i] < min) {
                min = xs[i];
                equalMinIndexes.clear();
                equalMinIndexes.add(i);
            }
        }
        return equalMinIndexes.get(CommonConstants.randomArgMaxTieBreak ? RandomNumbers.randomGenerator.nextInt(equalMinIndexes.size()) : 0);
    }

    public static int softmax(double[] ps, double temperature) {
        double[] posExps = new double[ps.length];
        for (int i = 0; i < posExps.length; i++) {
            posExps[i] = Math.exp(ps[i] / temperature);
        }
        posExps = distribution(posExps);
        return RandomNumbers.probabilisticSelection(posExps);
    }

    public static int probabilistic(double[] ps) {
        for (int i = 0; i < ps.length; i++) {
            ps[i] += 1; // Initial range is [-1,1], so now all are positive
        }
        double[] dist = distribution(ps);
        return RandomNumbers.probabilisticSelection(dist);
    }

    /*
     * Values in raw must be non-negative
     */
    public static double[] distribution(double[] raw) {
        double[] dist = new double[raw.length];
        double sum = 0;
        for (int i = 0; i < raw.length; i++) {
            if (raw[i] < 0) {
                System.out.println("Cannot create distribution from negative values: " + Arrays.toString(raw) + ":" + raw[i]);
                System.exit(1);
            }
            sum += raw[i];
        }
        if (sum == 0) {
            return new double[0];
//            System.out.println("Cannot create distribution across nothing!");
//            throw new IllegalArgumentException("Array only adds to 0: " + Arrays.toString(raw));
        }
        for (int i = 0; i < raw.length; i++) {
            dist[i] = raw[i] / sum;
        }
        return dist;
    }

    public static double[] distribution(int[] raw) {
        assert raw != null : "Can't work on null array";
        double[] ds = new double[raw.length];
        for (int i = 0; i < raw.length; i++) {
            ds[i] = raw[i];
        }
        return distribution(ds);
    }

    /**
     * Statistical mode calculation. Larger values are favored for tie-breaking
     * purposes.
     *
     * @param xs list of numbers
     * @return mode of values in xs
     */
    public static double mode(double[] xs) {
        return mode(xs, 0.001);
    }

    public static double mode(double[] xs, double epsilon) {
        assert xs.length > 0 : "Array empty!";
        //System.out.println(Arrays.toString(xs));
        Arrays.sort(xs);
        //System.out.println(Arrays.toString(xs));
        int maxCount = 0;
        double mode = xs[0];
        int currentCount = 0;
        double currentValue = xs[0];
        for (int i = 0; i < xs.length; i++) {
            if (Math.abs(xs[i] - currentValue) < epsilon) {
                currentCount++;
                //System.out.println("\tCount " + currentValue + " " + currentCount + " times");
            } else {
                if (currentCount >= maxCount) {
                    mode = currentValue;
                    maxCount = currentCount;
                    //System.out.println("\tMode = " + mode + " with " + maxCount + " times");
                }
                currentValue = xs[i];
                currentCount = 1;
            }
        }
        // Final check
        if (currentCount >= maxCount) {
            mode = currentValue;
            //maxCount = currentCount;
            //System.out.println("\tMode = " + mode + " with " + maxCount + " times");
        }

        return mode;
    }

    public static double maximum(double[] xs) {
        double max = xs[0];
        for (int i = 1; i < xs.length; i++) {
            max = Math.max(max, xs[i]);
        }
        return max;
    }

    public static int maximum(Collection<Integer> xs) {
        int max = Integer.MIN_VALUE;
        for (Integer x : xs) {
            max = Math.max(max, x);
        }
        return max;
    }

    public static double minimum(double[] xs) {
        double min = xs[0];
        for (int i = 1; i < xs.length; i++) {
            min = Math.min(min, xs[i]);
        }
        return min;
    }

    public static double average(double[] xs) {
        double avg = 0;
        for (int i = 0; i < xs.length; i++) {
            avg += (xs[i] - avg) / (i + 1);
        }
        return avg;
    }

    /**
     * Add up array of ints
     *
     * @param vals ints to add
     * @return sum of values
     */
    public static int sum(int[] vals) {
        int sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += vals[i];
        }
        return sum;
    }

    public static double sum(double[] vals) {
        double sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += vals[i];
        }
        return sum;
    }

    public static int sum(Collection<Integer> xs) {
        int result = 0;
        for (Integer x : xs) {
            result += x;
        }
        return result;
    }
    
    public static double populationStandardDeviation(double[] xs){
        return Math.sqrt(populationVariance(xs));
    }
    
    public static double sampleStandardDeviation(double[] xs){
        return Math.sqrt(sampleVariance(xs));
    }
    
    public static double populationVariance(double[] xs){
        return sumOfSquares(xs) / xs.length;
    }
    
    public static double sampleVariance(double[] xs){
        return sumOfSquares(xs) / (xs.length - 1.0);
    }
    
    /**
     * Calculate statistical sum of squares
     * @param xs
     * @return 
     */
    public static double sumOfSquares(double[] xs) {
        double average = average(xs);
        double[] squares = new double[xs.length];
        for(int i = 0; i < squares.length; i++) {
            squares[i] = xs[i] - average;
            squares[i] *= squares[i];
        }
        return sum(squares);
    }
    
    /**
     * Compute "instantaneous error energy" as described on page 161 of
     * Neural Networks by Haykin.
     * @param desired what output should be
     * @param output actual output of one neuron
     * @return instantaneous error
     */
    public static double instantaneousErrorEnergy(double desired, double output) {
        double e = desired - output;
        if(CommonConstants.watch){
            System.out.println("Error: " + e);
        }
        //return Math.abs(e);
        return (e*e)/2.0;
    }
    
    /**
     * Compute "TOTAL instantaneous error energy" as described on page 161 of
     * Neural Networks by Haykin.
     * COULD BE SLIGHTLY OPTIMIZED
     * @param pairs each pair is desired/actual values for one output neuron
     * @return total error across whole output layer
     */
    public static double instantaneousTotalErrorEnergy(ArrayList<Pair<Double,Double>> pairs){
        double sum = 0;
        for(Pair<Double,Double> p : pairs) {
            sum += instantaneousErrorEnergy(p.t1, p.t2);
        }
        return sum;
    }
    
    /**
     * Compute "average squared error energy" as described on page 161 of
     * Neural Networks by Haykin.
     * @param samples each member corresponds to a collection of desired/actual outputs for a network
     * @return 
     */
    public static double averageSquaredErrorEnergy(ArrayList<ArrayList<Pair<Double,Double>>> samples){
        double[] totalErrors = new double[samples.size()];
        for(int i = 0; i < totalErrors.length; i++) {
            ArrayList<Pair<Double,Double>> pairs = samples.get(i);
            totalErrors[i] = instantaneousTotalErrorEnergy(pairs);
        }
        return average(totalErrors);
    }

    public static void main(String[] args) {
        System.out.println("--Testing argmax---------------------");
        System.out.println("Plain Max: " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9}));
        System.out.println("0th Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},0));
        System.out.println("1st Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},1));
        System.out.println("2nd Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},2));
        System.out.println("3rd Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},3));
        System.out.println("4th Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},4));
        System.out.println("5th Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},5));
        System.out.println("9th Max  : " + argmax(new int[]{0,1,2,3,4,5,6,7,8,9},9));
        System.out.println("--Testing Mode-----------------------");
        System.out.println(mode(new double[]{1, 1, 1, 2, 2, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8, 8, 9}) + " = 8");
        System.out.println(mode(new double[]{1, 1, 1}) + " = 1");
        System.out.println(mode(new double[]{2, 1, 2, 1, 2, 1}) + " = 2");
        System.out.println(mode(new double[]{2, 3, 1, 3, 2, 3, 1, 3, 2, 3, 1, 3}) + " = 3");
        System.out.println(mode(new double[]{20, 20.2, 1.0, 1.05, 1.05, 2, 4.5}) + " = 1.05");
    }
}
