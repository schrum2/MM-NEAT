/*
 * Method for determining what other members of a population are in the same niche
 * for local competition
 */
package edu.utexas.cs.nn.evolution.nsga2.bd.localcompetition;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.scores.Score;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class TWEANNModesNicheDefinition extends NicheDefinition<TWEANN> {

    /**
     * If the best score in some objective is possessed by a network with
     * "modes" or more number of modes, then mode mutation on a network with
     * "modes" number of modes is justified.
     *
     * @param modes number modes of candidate network
     * @return true if mode mutation should occur
     */
//    public static boolean nichePerformanceJustifiesModeMutation(int modes) {
//        if(bestOverallScores == null){
//            //System.out.println("MM with " + modes + " because of no record");
//            return true; // mode mutation ok in first generation (PROBLEM ON RESUME!)
//        } else {
//            int nicheModes = modes;
//            // Look through niches with more modes as well (PROBLEM WITH GAPS! ie 3 modes but not 2 modes)
//            while(bestScoresInNiche.containsKey(nicheModes)){
//                ArrayList<Double> nicheScores = bestScoresInNiche.get(nicheModes);
//                for(int i = 0; i < nicheScores.size(); i++){
//                    // If niche has best score in objective
//                    if(Math.abs(nicheScores.get(i) - bestOverallScores.get(i)) < Double.MIN_VALUE){
//                        // Then more modes are justified
//                        //System.out.println("MM with " + modes + " modes because of niche " + nicheModes);
//                        return true;
//                    }
//                }
//                nicheModes++;
//            }
//        }
//        //System.out.println("No MM with " +modes + " modes");
//        return false;
//    }
    public static int bestHighModeNiche() {
        if (bestScoresInNiche == null) {
            return 1;
        }
        Set<Integer> modeKeys = bestScoresInNiche.keySet();
        int bestHighNiche = 1;
        for (Integer modes : modeKeys) {
            ArrayList<Double> nicheScores = bestScoresInNiche.get(modes);
            for (int i = 0; i < nicheScores.size(); i++) {
                // Then niche has a best score
                if (Math.abs(nicheScores.get(i) - bestOverallScores.get(i)) < Double.MIN_VALUE) {
                    bestHighNiche = Math.max(bestHighNiche, modes);
                }
            }
        }
        return bestHighNiche;
    }
    // Used for storing discovered niches, since they are globally calculated
    private HashMap<Integer, ArrayList<Score<TWEANN>>> niches;
    // HashMap keys are mode numbers, vector indices are objective indices, doubles are best scores
    private static HashMap<Integer, ArrayList<Double>> bestScoresInNiche = null;
    private static ArrayList<Double> bestOverallScores = null;

    @Override
    public void loadPopulation(ArrayList<Score<TWEANN>> originalPopulation) {
        super.loadPopulation(originalPopulation);
        // minus 1 because of diversity objective
        int numObjectives = originalPopulation.get(0).numObjectives() - 1;
        niches = new HashMap<Integer, ArrayList<Score<TWEANN>>>();
        bestScoresInNiche = new HashMap<Integer, ArrayList<Double>>();
        bestOverallScores = new ArrayList<Double>(numObjectives);
        for (int i = 0; i < numObjectives; i++) {
            // Initialize with lowest possible values
            bestOverallScores.add(-Double.MAX_VALUE);
        }
    }

    @Override
    public ArrayList<Score<TWEANN>> getNiche(Score<TWEANN> individual) {
        int numModes = ((TWEANNGenotype) individual.individual).numModes;
        ArrayList<Score<TWEANN>> niche;
        if (!niches.containsKey(numModes)) {
            niche = new ArrayList<Score<TWEANN>>(originalPopulation.size());
            for (Score<TWEANN> s : originalPopulation) {
                if (numModes == ((TWEANNGenotype) s.individual).numModes) {
                    niche.add(s);
                }
            }
            niches.put(numModes, niche);
        }
        // Update max scores of niche
        updateBestNicheScores(numModes, individual);
        return niches.get(numModes);
    }

    private void updateBestNicheScores(int numModes, Score<TWEANN> individual) {
        int numScores = individual.numObjectives() - 1; // Ignore diversity objective
        if (!bestScoresInNiche.containsKey(numModes)) {
            // Initialize
            ArrayList<Double> start = new ArrayList<Double>(numScores);
            for (int i = 0; i < numScores; i++) {
                double x = individual.scores[i];
                start.add(x);
                bestOverallScores.set(i, Math.max(bestOverallScores.get(i), individual.scores[i]));
            }
            bestScoresInNiche.put(numModes, start);
        } else {
            ArrayList<Double> bestScores = bestScoresInNiche.get(numModes);
            for (int i = 0; i < numScores; i++) {
                double original = bestScores.get(i);
                bestScores.set(i, Math.max(original, individual.scores[i]));
                bestOverallScores.set(i, Math.max(bestOverallScores.get(i), individual.scores[i]));
            }
        }
    }
}
