package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.log.MONELog;
import edu.utexas.cs.nn.log.TWEANNLog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.file.FileUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import wox.serial.Easy;

/**
 *
 * @author Jacob Schrum
 */
public class EvolutionaryHistory {

    public static int maxModes;
    public static int minModes;
    
    public static long largestUnusedInnovationNumber = 0;
    public static long largestUnusedGenotypeId = 0;
    public static ArrayList<NodeGene>[] archetypes = null;
    public static int[] archetypeOut = null;

    /**
     * Commonly used/shared networks (hierarchical architectures)
     */
    public static HashMap<String, Genotype> loadedNetworks = new HashMap<String, Genotype>();

    /**
     * Assure that each repeatedly used subnetwork is only loaded once
     *
     * @param xml File path of xml genotype file
     * @return the decoded genotype instance
     */
    @SuppressWarnings("unchecked")
	public static <T extends Network> Genotype<T> getSubnetwork(String xml) {
        if (xml.isEmpty()) {
            // Return a dummy genotype to be ignored later
            return null;
        }
        if (!loadedNetworks.containsKey(xml)) {
            System.out.println("Added to subnetworks: " + xml);
            loadedNetworks.put(xml, (Genotype<T>) Easy.load(xml));
        }
        return loadedNetworks.get(xml).copy();
    }

    /*
     * Remains high even when modes are deleted
     */
    // Doesn't account for multiple archetypes, and doesn't update after archetype cleaning
//    public static void netHasModes(int modes) {
//        maxModesOfAnyNetwork = Math.max(maxModesOfAnyNetwork, modes);
//    }

    /**
     * Sets up tracker for previously used innovation numbers.
     */
    public static void initInnovationHistory() {
        setInnovation(Parameters.parameters.longParameter("lastInnovation"));
    }

    /**
     * Sets up tracker for previously used genotype IDs.
     */
    public static void initGenotypeIds() {
        setHighestGenotypeId(Parameters.parameters.longParameter("lastGenotypeId") - 1);
    }

    /**
     * Assign new innovation number tracker
     * @param innovation Should be the larger than all previously used innovation numbers
     */
    public static void setInnovation(long innovation) {
        largestUnusedInnovationNumber = innovation;
    }

    /**
     * Assign new genotype ID tracker
     * @param id Should be the larger than all previously used genotype IDs
     */
    public static void setHighestGenotypeId(long id) {
        largestUnusedGenotypeId = id;
    }

    /**
     * Returns the next innovation number and increases the counter
     *
     * @return next innovation number
     */
    public static long nextInnovation() {
        long result = largestUnusedInnovationNumber;
        largestUnusedInnovationNumber++;
        Parameters.parameters.setLong("lastInnovation", largestUnusedInnovationNumber);
        return result;
    }

    public static long nextGenotypeId() {
        long result = largestUnusedGenotypeId;
        largestUnusedGenotypeId++;
        Parameters.parameters.setLong("lastGenotypeId", largestUnusedGenotypeId);
        return result;
    }
    public static TWEANNLog tweannLog = null;
    public static MONELog mutationLog = null;
    public static MONELog lineageLog = null;

    /**
     * Checks for a pre-existing file that is a genotype archetype for all genotypes
     * in the population. This file assures that crossover aligns genotypes correctly
     * when the genotypes being crossed have nodes that are not present in the other parent.
     * 
     * @param populationIndex Unused: Supposed to allow for multiple coevolved populations.
     * @return
     */
    public static boolean archetypeFileExists(int populationIndex) {
        String file = FileUtilities.getSaveDirectory() + "/" + "archetype";
        return (new File(file)).exists();
    }

    public static void initArchetype(int populationIndex) {
        String base = Parameters.parameters.stringParameter("base");
        String xml = Parameters.parameters.stringParameter("archetype");
        String file = xml + populationIndex + ".xml";
        if (base.equals("") ||
            !(new File(file).exists())) {
            file = null;
        }
        initArchetype(populationIndex, file);
    }

    @SuppressWarnings("unchecked")
	public static void initArchetype(int populationIndex, String loadedArchetype) {
        int size = MMNEAT.genotypeExamples == null ? 1 : MMNEAT.genotypeExamples.size();
        if (archetypes == null) {
            archetypes = new ArrayList[size];
        }
        if (loadedArchetype == null || loadedArchetype.equals("") || !(new File(loadedArchetype).exists())) {
            TWEANNGenotype tg = (TWEANNGenotype) (MMNEAT.genotypeExamples == null ? MMNEAT.genotype.copy() : MMNEAT.genotypeExamples.get(populationIndex).copy());
            archetypes[populationIndex] = tg.nodes;
            //System.out.println("Archetype " + populationIndex + ":" + archetypes[populationIndex]);
            saveArchetype(populationIndex);
        } else {
            // The loaded archetype might not simply be from a resume, the seed could be from elsewhere
            System.out.println("Loading archetype: " + loadedArchetype);
            archetypes[populationIndex] = (ArrayList<NodeGene>) Easy.load(loadedArchetype);
            String combiningCrossoverFile = Parameters.parameters.stringParameter("combiningCrossoverMapping");
            if (!combiningCrossoverFile.isEmpty()) {
                combiningCrossoverFile += ".txt";
                System.out.println("Loading combining crossover file: " + combiningCrossoverFile);
                CombiningTWEANNCrossover.loadOldToNew(combiningCrossoverFile);
            }
            long highestInnovation = -1;
            for (NodeGene ng : archetypes[populationIndex]) {
                highestInnovation = Math.max(highestInnovation, ng.innovation);
            }
            if (highestInnovation > largestUnusedInnovationNumber) {
                setInnovation(highestInnovation + 1);
            }
            String xml = Parameters.parameters.stringParameter("archetype");
            String file = xml + populationIndex + ".xml";
            // Compare the loaded name with the name to save at. If different, 
            // then the load was not a resume, and the archetype needs to be saved
            if (!loadedArchetype.equals(file)) {
                saveArchetype(populationIndex);
            }
        }
        if (archetypeOut == null) {
            archetypeOut = new int[size];
        }
        archetypeOut[populationIndex] = 0;
        for (NodeGene ng : archetypes[populationIndex]) {
            if (ng.ntype == TWEANN.Node.NTYPE_OUTPUT) {
                archetypeOut[populationIndex]++;
            }
        }
    }

    public static void saveArchetype(int populationIndex) {
        //new NullPointerException().printStackTrace();
        if (archetypes != null && archetypes[populationIndex] != null && CommonConstants.netio) {
            System.out.println("Saving archetype");
            String file = FileUtilities.getSaveDirectory() + "/" + "archetype";
            Parameters.parameters.setString("archetype", file);
            file += populationIndex + ".xml";
            Easy.save(archetypes[populationIndex], file);
            System.out.println("Done saving " + file);
            if (!CombiningTWEANNCrossover.oldToNew.isEmpty()) {
                System.out.println("Saving Combining Crossover Mapping");
                file = FileUtilities.getSaveDirectory() + "/" + "combiningCrossoverMapping";
                Parameters.parameters.setString("combiningCrossoverMapping", file);
                file += ".txt";
                CombiningTWEANNCrossover.saveOldToNew(file);
            }
        }
    }

    public static void initLineageAndMutationLogs() {
        mutationLog = new MONELog("Mutations", true);
        lineageLog = new MONELog("Lineage", true);
    }

    public static void initTWEANNLog() {
        initInnovationHistory();
        if (tweannLog == null) {
            tweannLog = new TWEANNLog("TWEANNData");
        }
    }

    public static void logTWEANNData(ArrayList<TWEANNGenotype> population, int generation) {
        if (tweannLog != null) {
            tweannLog.log(population, generation);
        }
    }

    public static void logMutationData(String data) {
        if (mutationLog != null) {
            mutationLog.log(data);
        }
    }

    public static void logLineageData(String data) {
        if (lineageLog != null) {
            lineageLog.log(data);
        }
    }

    public static int indexOfArchetypeInnovation(int populationIndex, long sourceInnovation) {
        if (archetypes[populationIndex] != null) {
            for (int i = 0; i < archetypes[populationIndex].size(); i++) {
                if (archetypes[populationIndex].get(i).innovation == sourceInnovation) {
                    return i;
                }
            }
//            System.out.println("Could not find innovation number: " + sourceInnovation);
//            System.out.println("In archetype " + populationIndex + ": " + archetypes[populationIndex]);
//            System.exit(1);
        }
        return -1;
    }

    /**
     * Removes from the archetype nodes all nodes that are not part of some
     * network in the population
     *
     * @param population population of TWEANNGenotypes
     * @param generation generation, used to tell if an archetype cleaning is
     * needed
     */
    public static void cleanArchetype(int populationIndex, ArrayList<TWEANNGenotype> population, int generation) {
        int freq = Parameters.parameters.integerParameter("cleanFrequency");
        if (archetypes[populationIndex] != null && generation % freq == 0) {
            System.out.println("Cleaning archetype");
            HashSet<Long> activeNodeInnovations = new HashSet<Long>();
            // Get all node innovation numbers still in use
            for (TWEANNGenotype tg : population) {
                for (NodeGene ng : tg.nodes) {
                    activeNodeInnovations.add(ng.innovation);
                }
            }
            // Remove from archetype each innovation the set does not have
            Iterator<NodeGene> itr = archetypes[populationIndex].iterator();
            archetypeOut[populationIndex] = 0;
            while (itr.hasNext()) {
                NodeGene ng = itr.next();
                if (!activeNodeInnovations.contains(ng.innovation)) {
                    // If combining crossover maps to this innovation, it can only be removed
                    // if the node that maps to it is also absent.
                    if (CombiningTWEANNCrossover.oldToNew.containsValue(ng.innovation)) {
                        Iterator<Long> onItr = CombiningTWEANNCrossover.oldToNew.keySet().iterator();
                        long source = -1;
                        while (onItr.hasNext()) {
                            long next = onItr.next();
                            if (CombiningTWEANNCrossover.oldToNew.get(next) == ng.innovation) {
                                source = next;
                                break; // should only be one such entry
                            }
                        }
                        assert (source != -1) :
                                "How can source == -1 if the mapping contains the value?\n"
                                + ng.innovation + "\n"
                                + CombiningTWEANNCrossover.oldToNew;
                        // source is the old innovation that maps to the combined innovation.
                        // if source is still present, then what it maps to cannot be removed.
                        int indexOfSource = indexOfArchetypeInnovation(populationIndex, source);
                        // But if indexOfSource is -1, then ng should be removed
                        if (indexOfSource == -1) {
                            itr.remove();
                            // Remove the mapping too
                            CombiningTWEANNCrossover.oldToNew.remove(source);
                        }
                    } else if (CombiningTWEANNCrossover.oldToNew.containsKey(ng.innovation)) {
                        CombiningTWEANNCrossover.oldToNew.remove(ng.innovation);
                        itr.remove();
                    } else {
                        // In the simple case, just remove the inactive node
                        itr.remove();
                    }
                } else if (ng.ntype == TWEANN.Node.NTYPE_OUTPUT) {
                    archetypeOut[populationIndex]++;
                }
            }
        }
    }
    //private static int order = 1;

    public static void archetypeAdd(int populationIndex, NodeGene node, String origin) {
        // Make sure that the archetype exists, and does not already contain the innovation number
        if (archetypes != null && archetypes[populationIndex] != null && indexOfArchetypeInnovation(populationIndex, node.innovation) == -1) {
            //node.origin = origin + " (" + (order++) + ")";
            //System.out.println("Archetype " + populationIndex + " Add End: " + node.innovation);
            archetypes[populationIndex].add(node);
            if(node.ntype == TWEANN.Node.NTYPE_OUTPUT) {
                archetypeOut[populationIndex]++;
            }
        }
    }

    public static void archetypeAdd(int populationIndex, int pos, NodeGene node, boolean combineCopy, String origin) {
        if (archetypes != null && archetypes[populationIndex] != null) {
            //node.origin = origin + " (" + (order++) + ")";
            //System.out.println("Archetype " + populationIndex + " Add "+pos+": " + node.innovation + ":" + node);
            archetypes[populationIndex].add(pos, node);

            if (combineCopy) {
                NodeGene previous = archetypes[populationIndex].get(pos - 1);
                if (previous.ntype == TWEANN.Node.NTYPE_INPUT) {
                    int firstCombined = indexOfFirstArchetypeNodeFromCombiningCrossover(populationIndex, TWEANN.Node.NTYPE_HIDDEN);
                    if (firstCombined == -1) { // no combining crossover yet
                        archetypeAddFromCombiningCrossover(populationIndex, node, archetypes[populationIndex].size() - archetypeOut[populationIndex], "combine splice (FIRST)");
                    } else {
                        archetypeAddFromCombiningCrossover(populationIndex, node, firstCombined, "combine splice (before others)");
                    }
                } else if (previous.ntype == TWEANN.Node.NTYPE_HIDDEN) {
                    long previousInnovation = previous.innovation;
                    if (CombiningTWEANNCrossover.oldToNew.containsKey(previousInnovation)) {
                        long newPreviousInnovation = CombiningTWEANNCrossover.oldToNew.get(previousInnovation);
                        int indexNewPrevious = indexOfArchetypeInnovation(populationIndex, newPreviousInnovation);
                        // Add new node directly after, in anticipation of future combining crossover
                        archetypeAddFromCombiningCrossover(populationIndex, node, indexNewPrevious + 1, "combine splice (hidden)");
                    }
                    // otherwise do nothing, since node is only being spliced in multitask networks
                } else {
                    System.out.println("Error! " + previous + "," + pos + "," + archetypes[populationIndex]);
                    System.exit(1);
                }
            }
        }
        assert orderedArchetype(populationIndex) : "Archetype " + populationIndex + " did not exhibit proper node order after node addition: " + archetypes[populationIndex];
    }

    public static void archetypeAddFromCombiningCrossover(int populationIndex, NodeGene node, int pos, String origin) {
        NodeGene newNodeGene = node.clone();
        long oldInnovation = newNodeGene.innovation;
        newNodeGene.innovation = CombiningTWEANNCrossover.getAdjustedInnovationNumber(oldInnovation); // Change innovation to prevent weird overlaps
        //newNodeGene.origin = origin + " copied "+oldInnovation+" (" + (order++) + ")";
        newNodeGene.fromCombiningCrossover = true;
        archetypes[populationIndex].add(pos, newNodeGene);
    }

    public static int indexOfFirstArchetypeNodeFromCombiningCrossover(int populationIndex, int ntype) {
        for (int i = 0; i < archetypes[populationIndex].size(); i++) {
            if (archetypes[populationIndex].get(i).fromCombiningCrossover && archetypes[populationIndex].get(i).ntype == ntype) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return index of first output node in archetype
     *
     * @param archetypeIndex
     * @return
     */
    public static int firstArchetypeOutputIndex(int archetypeIndex) {
        int result = archetypeSize(archetypeIndex) - archetypeOut[archetypeIndex];
        assert archetypes[archetypeIndex].get(result).ntype == TWEANN.Node.NTYPE_OUTPUT : "First output is not an output! pos " + result + " in " + archetypes[archetypeIndex];
        return result;
        // Assert below indicated that the above expression is indeed equivalent to searching for the node in
        // question. This would only fail if something else were also causing a problem.
//        for (int i = 0; i < archetypes[archetypeIndex].size(); i++) {
//            if (archetypes[archetypeIndex].get(i).ntype == TWEANN.Node.NTYPE_OUTPUT) {
//                //System.out.println("archetype output at " + i + " out of " + archetypes[archetypeIndex].size());
//                assert i == archetypeSize(archetypeIndex) - archetypeOut[archetypeIndex]: "First output found at " + i + " not consistent with archetype size " + archetypeSize(archetypeIndex) + " and num outputs " + archetypeOut[archetypeIndex] + ":" + archetypeOut[archetypeIndex];
//                return i;
//            }
//        }
//        return -1;
    }

    public static int archetypeSize(int populationIndex) {
        return archetypes[populationIndex] == null ? 0 : archetypes[populationIndex].size();
    }

    public static void frozenPreferenceVsPolicyStatusUpdate(ArrayList<? extends Genotype> population, int generation) {
        if ((population.get(0) instanceof TWEANNGenotype)
                && Parameters.parameters.booleanParameter("alternatePreferenceAndPolicy")
                && (generation % Parameters.parameters.integerParameter("freezeMeltAlternateFrequency")) == 0) {
            boolean result = false;
            for (Genotype tg : population) {
                result = ((TWEANNGenotype) tg).alternateFrozenPreferencePolicy();
            }
            System.out.println((result ? "Policy" : "Preference") + " neurons were frozen at gen " + generation);
        }
    }

    /**
     * Diagnostic method used in assertion: Makes sure nodes are properly
     * ordered in archetype, i.e. inputs, then hidden, then output
     *
     * @param populationIndex
     * @return
     */
    private static boolean orderedArchetype(int populationIndex) {
        int sectionType = TWEANN.Node.NTYPE_INPUT;
        for (int i = 0; i < archetypes[populationIndex].size(); i++) {
            NodeGene node = archetypes[populationIndex].get(i);
            if (node.ntype != sectionType) {
                switch (sectionType) {
                    case TWEANN.Node.NTYPE_INPUT:
                        if (node.ntype == TWEANN.Node.NTYPE_HIDDEN || node.ntype == TWEANN.Node.NTYPE_OUTPUT) {
                            // Expected progress
                            sectionType = node.ntype;
                        } else {
                            System.out.println("How does " + node.ntype + " follow " + sectionType + "?");
                            return false;
                        }
                        break;
                    case TWEANN.Node.NTYPE_HIDDEN:
                        if (node.ntype == TWEANN.Node.NTYPE_OUTPUT) {
                            // Expected progress
                            sectionType = node.ntype;
                        } else {
                            System.out.println("How does " + node.ntype + " follow " + sectionType + "?");
                            return false;
                        }
                        break;
                    case TWEANN.Node.NTYPE_OUTPUT:
                        System.out.println("How does " + node.ntype + " follow " + sectionType + "?");
                        return false;
                }
            }
        }
        return true;
    }
}
