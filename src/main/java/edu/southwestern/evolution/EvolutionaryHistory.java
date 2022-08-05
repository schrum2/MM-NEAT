package edu.southwestern.evolution;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.CombinedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNforDL4JGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.evolution.genotypes.TWEANNPlusParametersGenotype;
import edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.log.TWEANNLog;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;

/**
 * Stores and tracks information about a population of networks. Uses an
 * archetype that stores all genotypes used in population. Also logs history of
 * TWEANN, mutations and lineages of each network
 *
 * @author Jacob Schrum
 */
public class EvolutionaryHistory {

	public static Object innovationMonitor = new Object();
	public static Object genotypeIdMonitor = new Object();
	public static Object archetypeMonitor = new Object();

	public static int maxModes;
	public static int minModes;

	// keeps track of which id to use next
	public static long largestUnusedInnovationNumber = 0;
	public static long largestUnusedGenotypeId = 0;
	// keeps track of archetype of every genotype from each generation of every
	// member of population in an array list
	public static ArrayList<NodeGene>[] archetypes = null;
	public static int[] archetypeOut = null;
	// logs that keep track of history of genotype
	public static TWEANNLog tweannLog = null;
	public static MMNEATLog mutationLog = null;
	public static MMNEATLog lineageLog = null;

	// Once a certain number of generations is surpassed, this is set to true, but the clean may not happen immediately
	private static boolean archetypeCleanPending = false;
	private static int nextCleanGeneration;
	
	/**
	 * Given the current generation, see if it is time to clean the archetype
	 * @param generation Current generation
	 * @return Whether archetype should be cleaned soon
	 */
	public static boolean timeToClean(int generation) {
		// Special case where archetype is never cleaned
		if(Parameters.parameters.integerParameter("cleanFrequency") == -1) {
			return false;
		} else if(generation > nextCleanGeneration) {
			archetypeCleanPending = true;
		}
		return archetypeCleanPending;
	}
	
	/**
	 * Commonly used/shared networks (hierarchical architectures). Raw types are
	 * allowed for greater flexibility (different types of phenotypes may be
	 * generated)
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap<String, Genotype> loadedNetworks = new HashMap<String, Genotype>();

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
	 * Assign new innovation number ID
	 *
	 * @param innovation
	 *            Should be the larger than all previously used innovation
	 *            numbers
	 */
	public static void setInnovation(long innovation) {
		synchronized (innovationMonitor) {
			largestUnusedInnovationNumber = innovation;
		}
	}

	/**
	 * Assign new genotype ID tracker
	 *
	 * @param id
	 *            Should be the larger than all previously used genotype IDs
	 */
	public static void setHighestGenotypeId(long id) {
		synchronized (genotypeIdMonitor) {
			largestUnusedGenotypeId = id;
		}
	}

	/**
	 * Returns the next innovation number and increases the counter
	 *
	 * @return next innovation number
	 */
	public static synchronized long nextInnovation() {
		synchronized (innovationMonitor) {
			long result = largestUnusedInnovationNumber;
			largestUnusedInnovationNumber++;
			Parameters.parameters.setLong("lastInnovation", largestUnusedInnovationNumber);
			return result;
		}
	}

	/**
	 * Returns the next GenotypeID and increases the counter
	 * 
	 * @return next Genotype ID
	 */
	public static long nextGenotypeId() {
		synchronized (genotypeIdMonitor) {
			long result = largestUnusedGenotypeId;
			largestUnusedGenotypeId++;
			Parameters.parameters.setLong("lastGenotypeId", largestUnusedGenotypeId);
			return result;
		}
	}

	/**
	 * Checks for a pre-existing file that is a genotype archetype for all
	 * genotypes in the population. This file assures that crossover aligns
	 * genotypes correctly when the genotypes being crossed have nodes that are
	 * not present in the other parent.
	 *
	 * @param populationIndex
	 *            Unused: Supposed to allow for multiple coevolved populations.
	 *            Corresponds to index of population in question. Since
	 *            coevolution has not been implemented yet, unused.
	 * 
	 * @return true if file exists, false if doesn't
	 */
	public static boolean archetypeFileExists(int populationIndex) {
		String file = FileUtilities.getSaveDirectory() + "/" + "archetype";
		return (new File(file)).exists();
	}

	/**
	 * Public default method that initializes an archetype file that stores the
	 * archetype of the network in question. Includes some checks for whether or
	 * not network exists.
	 * 
	 * @param populationIndex
	 *            Supposed to allow for multiple coevolved populations.
	 *            Corresponds to index of population in question. Since
	 *            coevolution has not been implemented yet, unused.
	 */
	public static void initArchetype(int populationIndex) {
		String base = Parameters.parameters.stringParameter("base");
		String xml = Parameters.parameters.stringParameter("archetype");
		String file = xml + populationIndex;
		if (base.equals("") || !(new File(file).exists())) {
			file = null;
		}
		initArchetype(populationIndex, file);
	}

	/**
	 * Initialize new archetype with default starting genotype
	 * (source depends on whether coevolution is happening)
	 * @param populationIndex
	 * @param loadedArchetype
	 */
	public static void initArchetype(int populationIndex, String loadedArchetype) {
		@SuppressWarnings("rawtypes")
		TWEANNGenotype tg = (TWEANNGenotype) (MMNEAT.genotypeExamples == null ? 
				MMNEAT.genotype instanceof TWEANNPlusParametersGenotype ?
						((TWEANNPlusParametersGenotype) MMNEAT.genotype).getTWEANNGenotype() :
							MMNEAT.genotype instanceof CPPNOrDirectToGANGenotype ?
									((TWEANNGenotype) ((CPPNOrDirectToGANGenotype) MMNEAT.genotype).getCurrentGenotype()) :
										MMNEAT.genotype instanceof CPPNOrBlockVectorGenotype ? 
												((TWEANNGenotype) ((CPPNOrBlockVectorGenotype) MMNEAT.genotype).getCurrentGenotype()) :
													MMNEAT.genotype instanceof CombinedGenotype ? 
															((CombinedGenotype) MMNEAT.genotype).t1 : // Assume firsts of pair is TWEANNGenotype 
																MMNEAT.genotype instanceof HyperNEATCPPNforDL4JGenotype ?
																		((HyperNEATCPPNforDL4JGenotype) MMNEAT.genotype).getCPPN() :
																			MMNEAT.genotype.copy() : // Assume it is a TWEANNGenotype 
																				MMNEAT.genotypeExamples.get(populationIndex).copy()); // First population from coevolution
		initArchetype(populationIndex, loadedArchetype, tg);
	}

	/**
	 * A method that allows the current archetype to be saved if a resume is not
	 * occuring
	 * 
	 * @param populationIndex
	 *            Supposed to allow for multiple coevolved populations.
	 *            Corresponds to index of population in question. Since
	 *            coevolution has not been implemented yet, unused.
	 * 
	 * @param loadedArchetype
	 *            information needed to create the archetype file stored in a
	 *            string
	 *            
	 * @param startingGenotype used to derive initial archetype if it is empty, but can
	 * 						   be null otherwise
	 */
	@SuppressWarnings("unchecked")
	public static void initArchetype(int populationIndex, String loadedArchetype, TWEANNGenotype startingGenotype) {
		int size = MMNEAT.genotypeExamples == null ? 1 : MMNEAT.genotypeExamples.size();
		if (archetypes == null) {// checks to see if an archetype has been created yet for this genotype
			archetypes = new ArrayList[size];
		} // this if statement happens if the current experiment hasn't yet been run or is a resume
		if (loadedArchetype == null || loadedArchetype.equals("") || !(new File(loadedArchetype).exists())) {
			System.out.println("Init archetype from genotype example");
			// ternary operator allows for coevolution to be implemented
			TWEANNGenotype tg = startingGenotype;
			// saves the genotype of the current generation
			archetypes[populationIndex] = tg.nodes;
			saveArchetype(populationIndex);
		} else {
			// this else statement runs in a next run with a new seed
			// The loaded archetype might not simply be from a resume, the seed
			// could be from elsewhere
			System.out.println("Loading archetype: " + loadedArchetype);
			archetypes[populationIndex] = (ArrayList<NodeGene>) Serialization.load(loadedArchetype);
			String combiningCrossoverFile = Parameters.parameters.stringParameter("combiningCrossoverMapping");
			// implement for multimodal behavior. Allows for combining of two separate subpopulations to create a multimodal network
			if (!combiningCrossoverFile.isEmpty()) {
				combiningCrossoverFile += ".txt";
				System.out.println("Loading combining crossover file: " + combiningCrossoverFile);
				CombiningTWEANNCrossover.loadOldToNew(combiningCrossoverFile);
			}
			// finds largest innovation number in network to check largestUnusedInnovationNumber is set right
			long highestInnovation = -1;
			for (NodeGene ng : archetypes[populationIndex]) {
				highestInnovation = Math.max(highestInnovation, ng.innovation);
			}
			// checks to make sure largestUnusedInnovationNumber is set
			if (highestInnovation > largestUnusedInnovationNumber) {
				// to the highest innovation number
				setInnovation(highestInnovation + 1);
			}
			String xml = Parameters.parameters.stringParameter("archetype");
			String file = xml + populationIndex + ".xml";
			// Compare the loaded name with the name to save at. If different,
			// then the load was not a resume, and the archetype needs to be
			// saved
			if (!loadedArchetype.equals(file)) {
				saveArchetype(populationIndex);
			}
		}
		if (archetypeOut == null) {
			archetypeOut = new int[size];
		}
		// saves the archetype for the output neurons
		archetypeOut[populationIndex] = 0;
		for (NodeGene ng : archetypes[populationIndex]) {
			if (ng.ntype == TWEANN.Node.NTYPE_OUTPUT) {
				archetypeOut[populationIndex]++;
			}
		}
		// Can clean archetype after this point
		nextCleanGeneration = Parameters.parameters.integerParameter("lastSavedGeneration") + Parameters.parameters.integerParameter("cleanFrequency");
	}

	/**
	 * A method for saving the archetype of a network to its respective files
	 * 
	 * @param populationIndex
	 *            Supposed to allow for multiple coevolved populations.
	 *            Corresponds to index of population in question. Since
	 *            coevolution has not been implemented yet, unused.
	 */
	public static void saveArchetype(int populationIndex) {
		synchronized (archetypeMonitor) {
			if (archetypes != null && archetypes[populationIndex] != null && CommonConstants.netio) {
				System.out.println("Saving archetype");
				String file = FileUtilities.getSaveDirectory() + "/" + "archetype";
				Parameters.parameters.setString("archetype", file);
				file += populationIndex;
				Serialization.save(archetypes[populationIndex], file);
				System.out.println("Done saving " + file);
				// Could make more explicit using
				// CommonConstants.trackCombiningCrossover
				// Only happens if there is information to save
				CombiningTWEANNCrossover.saveCombiningCrossoverInformation(); 
			}
		}
	}

	/**
	 * Initializes the mutation and lineage logs of an archetype
	 */
	public static void initLineageAndMutationLogs() {
		// Selective breeding allows for an unusual number of log lines per generation
		mutationLog = new MMNEATLog("Mutations", true, false, MMNEAT.ea instanceof SelectiveBreedingEA);
		lineageLog = new MMNEATLog("Lineage", true, false, MMNEAT.ea instanceof SelectiveBreedingEA);
	}

	/**
	 * Initializes the innovation number history and initializes the TWEANN log
	 */
	public static void initTWEANNLog() {
		initInnovationHistory();
		if (tweannLog == null) {
			tweannLog = new TWEANNLog("TWEANNData");
		}
	}

	/**
	 * logs data about the tweann population and generation to tweann log
	 * 
	 * @param population
	 *            array list that contains all the genotypes of the given
	 *            population
	 * 
	 * @param generation
	 *            the number of generations performed so far
	 */
	public static void logTWEANNData(ArrayList<TWEANNGenotype> population, int generation) {
		if (tweannLog != null) {
			tweannLog.log(population, generation);
		}
	}

	/**
	 * logs mutation data to mutationLog
	 * 
	 * @param data
	 *            to be added to mutationLog
	 */
	public static void logMutationData(String data) {
		if (mutationLog != null) {
			mutationLog.log(data);
		}
	}

	/**
	 * Track the parent ID of a cloned child
	 * @param parent Parent genotype id
	 * @param cloneChild Child/clone genotype id
	 */
	public static void logLineageData(long parent, Genotype<?> cloneChild) {
		if(CommonConstants.inheritFitness) {
			cloneChild.addParent(parent);
		}
		logLineageData(parent + " -> " + cloneChild.getId());
	}

	/**
	 * Track the two parent IDs of child offspring
	 * @param parent1
	 * @param parent2
	 * @param childOffspring
	 */
	public static void logLineageData(long parent1, long parent2, Genotype<?> childOffspring) {
		if(CommonConstants.inheritFitness) {
			childOffspring.addParent(parent1);
			childOffspring.addParent(parent2);
		}
		logLineageData(parent1 + " X " + parent2 + " -> " + childOffspring.getId());
	}

	/**
	 * logs lineage data about a network to lineageLog
	 * 
	 * @param data
	 *            to be added to lineage log
	 */
	public static void logLineageData(String data) {
		if (lineageLog != null) {
			lineageLog.log(data);
		}
	}

	/**
	 * 
	 * @param populationIndex
	 *            index of population in question
	 * @param sourceInnovation
	 *            innovation number in question
	 * 
	 * @return the index of the innovation number from the archetypes array
	 */
	public static int indexOfArchetypeInnovation(int populationIndex, long sourceInnovation) {
		if (archetypes[populationIndex] != null) {
			for (int i = 0; i < archetypes[populationIndex].size(); i++) {
				if (archetypes[populationIndex].get(i).innovation == sourceInnovation) {
					return i;
				}
			}
		}
		return -1;// returns if innovation number not found
	}

	/**
	 * Removes from the archetype all nodes that are not part of the given
	 * network in the population
	 *
	 * @param populationIndex
	 *            Archetype index of network to compare for cleaning
	 * @param population
	 *            population of TWEANNGenotypes
	 * @param generation
	 *            number of generations passed, used to tell if an archetype
	 *            cleaning is needed
	 */
	public static void cleanArchetype(int populationIndex, ArrayList<TWEANNGenotype> population, int generation) {
		synchronized (archetypeMonitor) {
			// command line parameter that tells how often archetype needs to be cleaned
			if (archetypes[populationIndex] != null && timeToClean(generation)) {
				System.out.println("Cleaning archetype");
				HashSet<Long> activeNodeInnovations = new HashSet<Long>();
				// Get all node innovation numbers still in use by population
				for (TWEANNGenotype tg : population) {
					for (NodeGene ng : tg.nodes) {
						activeNodeInnovations.add(ng.innovation);
					}
				}
				// Remove from archetype each innovation number no longer active in
				// population
				Iterator<NodeGene> itr = archetypes[populationIndex].iterator();
				archetypeOut[populationIndex] = 0;
				// Could check CommonConstants.trackCombiningCrossover, but knowing
				// that oldToNew is not empty should be enough
				boolean trackCombiningCrossoverInformation = !CombiningTWEANNCrossover.oldToNew.isEmpty();
				while (itr.hasNext()) {
					NodeGene currentGene = itr.next();
					if (!activeNodeInnovations.contains(currentGene.innovation)) {
						if (trackCombiningCrossoverInformation) {
							// If combining crossover information is being tracked,
							// then the cleanup process is more complex
							CombiningTWEANNCrossover.complexArchetypeCleanup(populationIndex, itr, currentGene);
						} else {
							// In the simple case, just remove the inactive node
							itr.remove();
						}
						// if reaches this else if statement, current gene is active
					} else if (currentGene.ntype == TWEANN.Node.NTYPE_OUTPUT) {
						archetypeOut[populationIndex]++;
					}
				}
				int freq = Parameters.parameters.integerParameter("cleanFrequency");
				nextCleanGeneration = generation + freq;
				System.out.println("Next archetype cleaning generation is "+nextCleanGeneration);
				archetypeCleanPending = false;
			}
		}
	}

	/**
	 * Adds a new innovation number to archetype
	 * 
	 * @param populationIndex
	 *            Supposed to allow for multiple coevolved populations.
	 *            Corresponds to index of population in question. Since
	 *            coevolution has not been implemented yet, unused.
	 * @param node
	 *            new genotype to be added to archetype
	 * 
	 * @param origin
	 *            indicates from where in the code the node came from, for
	 *            debugging purposes only
	 */
	public static void archetypeAdd(int populationIndex, NodeGene node, String origin) {
		synchronized (archetypeMonitor) {
			// Make sure that the archetype exists, and does not already contain the
			// innovation number
			if (archetypes != null && archetypes[populationIndex] != null
					&& indexOfArchetypeInnovation(populationIndex, node.innovation) == -1) {
				// adds the new innovation number
				archetypes[populationIndex].add(node);
				if (node.ntype == TWEANN.Node.NTYPE_OUTPUT) {
					archetypeOut[populationIndex]++;
				}
			}
		}
	}

	/**
	 * Adds a new node to archetype. Also checks if the node is a crossover of
	 * other nodes and handles each crossover case specifically
	 * 
	 * @param populationIndex
	 *            index of population
	 * @param pos
	 *            index of where node is to be added in archetype
	 * @param node
	 *            genotype to be added to archetype
	 * @param combineCopy
	 *            boolean value expressing whether or not combining crossover
	 *            information should be tracked for new node
	 * @param origin
	 *            indicates from where in the code the node came from, for
	 *            debugging purposes only
	 */
	public static void archetypeAdd(int populationIndex, int pos, NodeGene node, boolean combineCopy, String origin) {
		synchronized (archetypeMonitor) {
			// If archetype exists, and the node does not currently exist in the archetype
			if (archetypes != null && archetypes[populationIndex] != null && indexOfArchetypeInnovation(populationIndex, node.innovation) == -1) {
				archetypes[populationIndex].add(pos, node);
				if (CommonConstants.trackCombiningCrossover && combineCopy) {
					// this statement only runs if we will be tracking combining crossover information
					CombiningTWEANNCrossover.addToArchetypeForCombiningCrossover(populationIndex, pos, node, combineCopy, origin);
				}
				if (node.ntype == TWEANN.Node.NTYPE_OUTPUT) {
					archetypeOut[populationIndex]++;
				}
			}
			assert orderedArchetype(populationIndex) : "Archetype " + populationIndex + " added at pos " + pos
			+ " " + node
			+ " did not exhibit proper node order after node addition: " + archetypes[populationIndex];
		}
	}

	/**
	 * A helper method for archetypeAdd method. Addresses cases where a node
	 * that is a combination of other nodes is trying to be added
	 * 
	 * @param populationIndex
	 *            index of population in question
	 * @param node
	 *            node to be added
	 * @param pos
	 *            index of where node is to be added in archetype, corresponds
	 *            to innovation number
	 * @param origin
	 *            indicates from where in the code the node came from, for
	 *            debugging purposes only
	 */
	public static void archetypeAddFromCombiningCrossover(int populationIndex, NodeGene node, int pos, String origin) {
		synchronized (archetypeMonitor) {
			// adds a clone of node to add so original node is not affected if add is unsuccessful
			NodeGene newNodeGene = node.clone();
			long oldInnovation = newNodeGene.innovation;
			// Change innovation to prevent weird overlaps
			newNodeGene.innovation = CombiningTWEANNCrossover.getAdjustedInnovationNumber(oldInnovation); 
			// newNodeGene.origin = origin + " copied "+oldInnovation+" (" + (order++) + ")";
			// indicates addition was successful
			newNodeGene.setFromCombiningCrossover();
			archetypes[populationIndex].add(pos, newNodeGene);
		}
	}

	/**
	 * Helper method for archetypeAdd method. Gets the index of first node from
	 * archetype used in combined node
	 * 
	 * @param populationIndex
	 *            index of population in question
	 * @param ntype
	 *            the type of node of the first node from crossover
	 * 
	 * @return index of first node in archetype from combined node to add
	 */
	public static int indexOfFirstArchetypeNodeFromCombiningCrossover(int populationIndex, int ntype) {
		for (int i = 0; i < archetypes[populationIndex].size(); i++) {
			if (archetypes[populationIndex].get(i).fromCombiningCrossover()
					&& archetypes[populationIndex].get(i).ntype == ntype) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Return index of first output node in archetype
	 *
	 * @param archetypeIndex
	 *            index of archetype in question
	 * 
	 * @return index of first output node in archetype
	 */
	public static int firstArchetypeOutputIndex(int archetypeIndex) {
		int result = archetypeSize(archetypeIndex) - archetypeOut[archetypeIndex];
		assert archetypes[archetypeIndex]
				.get(result).ntype == TWEANN.Node.NTYPE_OUTPUT : "First output is not an output! pos " + result + " in "
						+ archetypes[archetypeIndex];
		return result;
		/**
		 * code for testing purposes
		 */
		// Assert below indicated that the above expression is indeed equivalent
		// to searching for the node in
		// question. This would only fail if something else were also causing a
		// problem.
		// for (int i = 0; i < archetypes[archetypeIndex].size(); i++) {
		// if (archetypes[archetypeIndex].get(i).ntype ==
		// TWEANN.Node.NTYPE_OUTPUT) {
		// //System.out.println("archetype output at " + i + " out of " +
		// archetypes[archetypeIndex].size());
		// assert i == archetypeSize(archetypeIndex) -
		// archetypeOut[archetypeIndex]: "First output found at " + i + " not
		// consistent with archetype size " + archetypeSize(archetypeIndex) + "
		// and num outputs " + archetypeOut[archetypeIndex] + ":" +
		// archetypeOut[archetypeIndex];
		// return i;
		// }
		// }
		// return -1;
	}

	/**
	 * Getter method for size of the archetype
	 * 
	 * @param populationIndex
	 *            index of population in question
	 * 
	 * @return size of archetype
	 */
	public static int archetypeSize(int populationIndex) {
		return archetypes[populationIndex] == null ? 0 : archetypes[populationIndex].size();
	}

	/**
	 * This complicated method isn't really used. The point was to allow
	 * evolving network genotypes with preference neurons to alternate between
	 * stages where the policy was evolving and stages where the preference
	 * neuron behavior was evolving.
	 *
	 * The code only runs with TWEANNGenotypes (instanceof), but gets called
	 * with Genotypes of all sorts, which is why the type is raw.
	 *
	 * @param population
	 *            A population of TWEANNGenotypes
	 * @param generation
	 *            index of generation neurons may or may not have been frozen at
	 */
	@SuppressWarnings("rawtypes")
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
	 * 
	 * @return Whether archetype is ordered.
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
