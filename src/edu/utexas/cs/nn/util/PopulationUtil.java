package edu.utexas.cs.nn.util;

import edu.utexas.cs.nn.data.SaveThread;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.mutation.tweann.CauchyDeltaCodeMutation;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Better;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.file.XMLFilter;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import wox.serial.Easy;

/**
 *
 * @author Jacob Schrum
 */
public class PopulationUtil {

    /**
     * Given a whole population of scores, get the Pareto front and use them
     * as exemplars to create a delta-coded population.
     * @param populationScores population with scores
     * @return new soft restart population
     */
    public static <T> ArrayList<Genotype<T>> getBestAndDeltaCode(ArrayList<Score<T>> populationScores) {
        ArrayList<Genotype<T>> front = NSGA2.staticSelection(populationScores.size(), NSGA2.staticNSGA2Scores(populationScores));
        return deltaCodePopulation(populationScores.size(), front);
    }
    
    /**
     * Take some example genotypes (e.g. a Pareto front) and create a whole 
     * population based off of them by delta coding their network weights.
     * @param size of population to be
     * @param exemplars starting point of new population
     * @return the new population
     */
    public static <T> ArrayList<Genotype<T>> deltaCodePopulation(int size, ArrayList<Genotype<T>> exemplars) {
        CauchyDeltaCodeMutation cauchy = new CauchyDeltaCodeMutation();
        ArrayList<Genotype<T>> newPop = new ArrayList<Genotype<T>>(size);
        assert exemplars.get(0) instanceof TWEANNGenotype : "Cannot delta-code non-TWEANN genotype";
        for(int i = 0; i < size; i++) {
            // Keep cycling through the exemplars
            Genotype<T> exemplar = exemplars.get(i % exemplars.size()).copy();
            cauchy.mutate((Genotype<TWEANN>) exemplar);
            newPop.add(exemplar);
        }
        return newPop;
    } 
    
    /**
     * Given parent scores and a means of comparing individuals, use elitist
     * tournament selection to choose individuals and/or parents for creating an
     * offspring population.
     *
     * @param numChildren children to generate
     * @param parentScores scores of parents, plus genotypes
     * @param judge means of comparing two parents
     * @param mating whether to mate or not
     * @param crossoverRate crossover rate if mating
     * @return population of children
     */
    public static ArrayList<Genotype> childrenFromTournamentSelection(int numChildren, ArrayList<Score> parentScores, Better<Score> judge, boolean mating, double crossoverRate) {
        ArrayList<Genotype> offspring = new ArrayList<Genotype>(numChildren);

        for (int i = 0; i < numChildren; i++) {
            int e1 = RandomNumbers.randomGenerator.nextInt(parentScores.size());
            int e2 = RandomNumbers.randomGenerator.nextInt(parentScores.size());

            Genotype source = judge.better(parentScores.get(e1), parentScores.get(e2)).individual;
            long parentId1 = source.getId();
            long parentId2 = -1;
            Genotype e = source.copy();

            if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
                e1 = RandomNumbers.randomGenerator.nextInt(parentScores.size());
                e2 = RandomNumbers.randomGenerator.nextInt(parentScores.size());

                Genotype otherSource = judge.better(parentScores.get(e1), parentScores.get(e2)).individual;
                parentId2 = otherSource.getId();
                Genotype otherOffspring;

                Genotype other = otherSource.copy();
                otherOffspring = e.crossover(other);
                i++;
                /*
                 * The offspring e will be added no matter what. Because i is
                 * increased and then checked, otherOffspring will NOT always be
                 * added.
                 */
                if (i < numChildren) {
                    //System.out.println(i + ":Mutate Child");
                    otherOffspring.mutate();
                    offspring.add(otherOffspring);
                    EvolutionaryHistory.logLineageData(parentId1 + " X " + parentId2 + " -> " + otherOffspring.getId());
                }
            }

            e.mutate();
            offspring.add(e);
            if (parentId2 == -1) {
                EvolutionaryHistory.logLineageData(parentId1 + " -> " + e.getId());
            } else {
                EvolutionaryHistory.logLineageData(parentId1 + " X " + parentId2 + " -> " + e.getId());
            }
        }
        return offspring;
    }

    /**
     * Modifies population sent in so it contains only members of Pareto front
     *
     * @param <T> Type of phenotype
     * @param population full population to be reduced to Pareto front
     * @param scores loaded scores corresponding to individuals in population
     */
    public static <T> void pruneDownToParetoFront(ArrayList<Genotype<T>> population, NSGA2Score<T>[] scores) {
        pruneDownToTopParetoLayers(population, scores, 1);
    }

    /**
     * Modifies population so it contains only top Pareto layers
     *
     * @param <T> Phenotype
     * @param population genotypes to prune
     * @param scores scores corresponding to genotypes
     * @param layers How many layers to keep
     */
    public static <T> void pruneDownToTopParetoLayers(ArrayList<Genotype<T>> population, NSGA2Score<T>[] scores, int layers) {
        ArrayList<ArrayList<NSGA2Score<T>>> fronts = NSGA2.getParetoLayers(scores);
        // Reduce population to only contain top Pareto layers
        Iterator<Genotype<T>> itr = population.iterator();
        System.out.println("Reducing to top " + layers + " Pareto layers");
        while (itr.hasNext()) {
            Genotype<T> g = itr.next();
            boolean found = false;
            for (int i = 0; !found && i < layers; i++) {
                ArrayList<NSGA2Score<T>> front = fronts.get(i);
                for (NSGA2Score<T> s : front) {
                    if (s.individual.getId() == g.getId()) {
                        found = true;
                        System.out.println(s.individual.getId() + ":" + Arrays.toString(s.scores) + " in layer " + i);
                        break;
                    }
                }
            }
            if (!found) {
                itr.remove();
            }
        }
    }

    /**
     * Load all genotypes that are xml files in the given directory
     *
     * @param <T> Phenotype
     * @param directory directory to load from
     * @return loaded population of genotypes
     */
    public static <T> ArrayList<Genotype<T>> load(String directory) {
        System.out.println("Attempting to load from: " + directory);

        FilenameFilter filter = new XMLFilter();

        ArrayList<Genotype<T>> population = new ArrayList<Genotype<T>>(Parameters.parameters.integerParameter("mu"));

        File dir = new File(directory);
        String[] children = dir.list(filter);
        if (!dir.exists() || children == null) {
            System.err.println("Can't load population, folder '" + directory + "' does not exist");
            System.exit(1);
        } else {
            for (int i = 0; i < children.length; i++) {
                String file = directory + "/" + children[i];
                System.out.print("Load File: \"" + file + "\"");
                Object loaded = Easy.load(file);

                Genotype<T> individual;
                if (loaded instanceof Genotype) {
                    individual = (Genotype<T>) loaded;
                    System.out.println(", ID = " + individual.getId());
                } else {
                    // Fail
                    return null;
                }
                population.add(individual);
            }
        }
        return population;
    }

    /**
     * Loads score information from the score file pertaining to a single
     * generation. Only works for score files saved by single-population
     * experiments.
     *
     * @param <T> phenotype of saved individuals: irrelevant because scores
     * contain anonymous dummy individuals
     * @param generation generation to load scores for
     * @return Array of NSGA2Scores for given generation
     * @throws FileNotFoundException if score file does not exist
     */
    public static <T> NSGA2Score<T>[] loadScores(int generation) throws FileNotFoundException {
        String base = Parameters.parameters.stringParameter("base");
        String saveTo = Parameters.parameters.stringParameter("saveTo");
        int run = Parameters.parameters.integerParameter("runNumber");
        String log = Parameters.parameters.stringParameter("log");
        String filePrefix = base + "/" + saveTo + run + "/" + log + run + "_";
        String infix = "parents_gen";
        String filename = filePrefix + infix + generation + ".txt";
        return loadScores(filename);
    }

    /**
     * Same as above, but for coevolution
     *
     * @param generation generation to load from
     * @param pop subpopulation index
     * @return scores of subpop in designated generation
     * @throws FileNotFoundException
     */
    public static NSGA2Score[] loadSubPopScores(int generation, int pop) throws FileNotFoundException {
        String base = Parameters.parameters.stringParameter("base");
        String saveTo = Parameters.parameters.stringParameter("saveTo");
        int run = Parameters.parameters.integerParameter("runNumber");
        String log = Parameters.parameters.stringParameter("log");
        String filePrefix = base + "/" + saveTo + run + "/" + log + run + "_";
        String infix = "pop" + pop + "parents_gen";
        String filename = filePrefix + infix + generation + ".txt";
        return loadScores(filename);
    }

    /**
     * Loads scores from a specific filename and creates score entries with
     * dummy individuals to return.
     *
     * @param <T> phenotype: irrelevant since anonymous dummy individuals are
     * used
     * @param filename file to load scores from
     * @return array of scores
     * @throws FileNotFoundException if filename does not exist
     */
    public static <T> NSGA2Score<T>[] loadScores(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        NSGA2Score<T>[] populationScores = new NSGA2Score[Parameters.parameters.integerParameter("mu")];
        int i = 0;
        while (s.hasNextLine()) {
            Scanner line = new Scanner(s.nextLine());
            int withinGen = line.nextInt();
            long offspringId = line.nextLong();
            ArrayList<Double> scores = new ArrayList<Double>();
            while (line.hasNext()) {
                double x = line.nextDouble();
                //System.out.print(x + "\t");
                scores.add(x);
            }
            //System.out.println();
            double[] scoreArray = new double[scores.size()];
            for (int j = 0; j < scoreArray.length; j++) {
                scoreArray[j] = scores.get(j);
            }
            Genotype<T> anonymous = anonymousIdIndividual(offspringId);
            populationScores[i++] = new NSGA2Score(anonymous, scoreArray, null, null);
            assert populationScores[i - 1] != null : "Null Score! " + i;
        }
        return populationScores;
    }

    /**
     * Used when creating a score instance where only the score matters. Score
     * instances normally contain a copy of the genotype as well, but when
     * loading scores from a file, the genotype would take an extra effort to
     * load. All that really mattes is the genotype id, so that the score can be
     * associated with the right genotype, even though that genotype is
     * contained within this particular score instance. This method creates an
     * instance of an anonymous Genotype that only stores a genotype id. Any
     * attempt to use any other methods defined by Genotype will lead to
     * exceptions.
     *
     * @param <T> phenotype of genotype: irrelevant in dummy individual.
     * @param offspringId genotype id of individual
     * @return Genotype where only the id is accessible.
     */
    public static <T> Genotype<T> anonymousIdIndividual(final long offspringId) {
        return new Genotype<T>() {

            public Genotype<T> copy() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mutate() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public Genotype<T> crossover(Genotype<T> g) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public T getPhenotype() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public Genotype<T> newInstance() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public long getId() {
                return offspringId;
            }
        };
    }

    public static void saveAllSubPops(String prefix, String saveDirectory, ArrayList<ArrayList<Genotype>> populations, boolean parallel) {
        String fullSaveDir = saveDirectory + "/" + prefix;
        new File(fullSaveDir).mkdir();
        Parameters.parameters.setString("lastSavedDirectory", fullSaveDir);

        for (int i = 0; i < populations.size(); i++) {
            saveSubpop(i, prefix, saveDirectory, addListGenotypeType(populations.get(i)), parallel);
            if (populations.get(i).get(0) instanceof TWEANNGenotype) {
                EvolutionaryHistory.saveArchetype(i);
            }
        }
    }

    public static <T> void saveSubpop(int num, String prefix, String saveDirectory, ArrayList<Genotype<T>> population, boolean parallel) {
        String experimentPrefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber");
        String fullSaveDir = saveDirectory + "/" + prefix + "/" + num;
        prefix = experimentPrefix + "_" + prefix + "_" + num + "_";

        new File(fullSaveDir).mkdir();
        System.out.println("Saving to \"" + fullSaveDir + "\" with prefix \"" + prefix + "\"");

        ExecutorService poolExecutor = null;
        ArrayList<Future<Boolean>> futures = null;
        ArrayList<SaveThread<Genotype<T>>> saves = new ArrayList<SaveThread<Genotype<T>>>(population.size());

        for (int i = 0; i < population.size(); i++) {
            String filename = fullSaveDir;
            if (!filename.equals("")) {
                filename = filename + "/";
            }
            filename += prefix + i + ".xml";
            saves.add(new SaveThread<Genotype<T>>(population.get(i), filename));
        }

        if (parallel) {
            poolExecutor = Executors.newCachedThreadPool();
            futures = new ArrayList<Future<Boolean>>(population.size());
            for (int i = 0; i < population.size(); i++) {
                futures.add(poolExecutor.submit(saves.get(i)));
            }
        }

        for (int i = 0; i < saves.size(); i++) {
            try {
                Boolean result = parallel ? futures.get(i).get() : saves.get(i).call();
                if (!result) {
                    System.out.println("Failure saving " + population.get(i));
                    System.exit(1);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Failure saving " + population.get(i));
                System.exit(1);
            }
        }

        if (parallel) {
            poolExecutor.shutdown();
        }
    }

    /**
     * Loads xml files from a given directory into a vector of vectors such that
     * each sub-vector contains a subpopulation. The "directory" is presumed to
     * itself contain subdirectories named numerically: 0, 1, ... , (numPops -
     * 1). So, numPops designates the end size of the result returned, and the
     * number of populations subdirectories to look for.
     *
     * Each subdir should contain a collection of xml files that can be loaded
     * as Genotypes<T> instances. Also, each subdir should have the same number
     * of xml files to create equal sized subpops.
     *
     * @param directory directory where subdirs containing xml files for subpops
     * are.
     * @param numPops number of subpops to load.
     * @return vector of loaded subpopulations, each stored in a vector of
     * genotypes
     */
    public static ArrayList<ArrayList<Genotype>> loadSubPops(String directory, int numPops) {
        System.out.println("Load multiple populations");
        ArrayList<ArrayList<Genotype>> populations = new ArrayList<ArrayList<Genotype>>(numPops);
        boolean success = true;
        for (int i = 0; i < numPops; i++) {
            ArrayList<Genotype<Object>> pop = PopulationUtil.load(directory + "/" + i);
            success = success && (pop != null);
            /**
             * The only way for Java to compile this is to switch the generic
             * Objects over to being completely unknown types.
             *
             */
            populations.add(removeListGenotypeType(pop));
        }
        return populations;
    }

    /**
     * Given an ArrayList of Genotypes<T> instances, remove the T type and
     * return the resulting list.
     *
     * @param <T>
     * @param genotypes
     * @return
     */
    public static <T> ArrayList<Genotype> removeListGenotypeType(ArrayList<Genotype<T>> genotypes) {
        ArrayList<Genotype> ungenericPop = new ArrayList<Genotype>(genotypes.size());
        for (Genotype g : genotypes) {
            ungenericPop.add(g);
        }
        return ungenericPop;
    }

    /**
     * Puts back type T information for all genotypes in an array list. For this
     * to be valid, all genotypes must be of the same type
     *
     * @param <T>
     * @param genotypes
     * @return
     */
    public static <T> ArrayList<Genotype<T>> addListGenotypeType(ArrayList<Genotype> genotypes) {
        ArrayList<Genotype<T>> genericPop = new ArrayList<Genotype<T>>(genotypes.size());
        for (Genotype g : genotypes) {
            genericPop.add(g);
        }
        return genericPop;
    }

    /**
     * Add generic type T to list of Scores
     *
     * @param <T>
     * @param scores
     * @return
     */
    public static <T> ArrayList<Score<T>> addListScoreType(ArrayList<Score> scores) {
        ArrayList<Score<T>> genericPop = new ArrayList<Score<T>>(scores.size());
        for (Score g : scores) {
            genericPop.add(g);
        }
        return genericPop;
    }

    /**
     * Finds the index in the subpopulation of the genotype with a specified id.
     * Returns -1 if no such genotype is found.
     *
     * @param subpopulation array of genotypes
     * @param id genotype id that might be in subpopulation
     * @return index of genotype with id, or -1
     */
    public static int indexOfGenotypeWithId(ArrayList<Genotype> subpopulation, long id) {
        for (int q = 0; q < subpopulation.size(); q++) {
            if (subpopulation.get(q).getId() == id) {
                return q;
            }
        }
        return -1;
    }

    public static <T> ArrayList<Long> getGenotypeIds(ArrayList<Genotype<T>> genotypes) {
        ArrayList<Long> result = new ArrayList<Long>();
        for (Genotype g : genotypes) {
            result.add(g.getId());
        }
        return result;
    }

    /**
     * Take two populations and find out, by reference to id numbers, which
     * members are in one but not the other.
     *
     * @param lhs pop 1
     * @param rhs pop 2
     * @return The first member of the pair contains individuals in lhs but not
     * in rhs, while the second is members of rhs that are not in lhs
     */
    public static <T> Pair<ArrayList<Genotype<T>>, ArrayList<Genotype<T>>> populationDifferences(ArrayList<Genotype<T>> lhs, ArrayList<Genotype<T>> rhs) {
        ArrayList<Genotype<T>> leftDiffRight = ArrayUtil.setDifference(lhs, rhs);
        ArrayList<Genotype<T>> rightDiffLeft = ArrayUtil.setDifference(rhs, lhs);
        return new Pair<ArrayList<Genotype<T>>, ArrayList<Genotype<T>>>(leftDiffRight, rightDiffLeft);
    }

    /**
     * From an array of scores, return the one whose embedded individual has a
     * designated id
     *
     * @param <T> type of genotypes
     * @param id id of genotype
     * @param staticScores scores to search, each containing a genotype
     * @return the score matching the id
     */
    public static <T> NSGA2Score<T> scoreWithId(long id, NSGA2Score<T>[] staticScores) {
        for (NSGA2Score<T> s : staticScores) {
            if (s.individual.getId() == id) {
                return s;
            }
        }
        return null;
    }
}
