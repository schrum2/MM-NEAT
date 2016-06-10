package edu.utexas.cs.nn.evolution.crossover.network;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.Crossover;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.Gene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.evolution.mutation.tweann.MeltThenFreezePolicyMutation;
import edu.utexas.cs.nn.evolution.mutation.tweann.MeltThenFreezePreferenceMutation;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class crosses over two TWEANN networks with a command line parameter
 * that controls how much crossover occurs
 *
 * @author Jacob Schrum
 */
public class TWEANNCrossover extends Crossover<TWEANN> {
	// this is the rate at which disjoint/excess nodes are included in children of crossover
	private final double includeExcessRate;
	private boolean includeExcess = false;

	/**
	 * Default constructor for a TWEANN crossover. Calls on another constructor
	 * with a command line parameter that controls when to include
	 * excess/disjoint genes between genotypes.
	 */
	public TWEANNCrossover() {
		this(Parameters.parameters.doubleParameter("crossExcessRate"));
	}

	/**
	 * Constructor for a TWEANN crossover.
	 * 
	 * @param includeExcessRate
	 *            command line parameter that controls when to include
	 *            excess/disjoing genes between genotypes.
	 */
	public TWEANNCrossover(double includeExcessRate) {
		this.includeExcessRate = includeExcessRate;
	}

	/**
	 * Perform crossover between two TWEANN genotypes.
	 *
	 * @param toModify
	 *            = Copy of parent genotype. Is actually modified by the
	 *            crossover, to be one of the offspring. It is modified via
	 *            side-effects because it is not returned.
	 * @param toReturn
	 *            = The other parent. Not actually returned, but but the
	 *            offspring that is returned by this method takes its basic
	 *            structure from this genotype (unless excess crossover occurs)
	 * @return One of the offspring of crossover is returned (the other modified
	 *         via side-effects)
	 */
	public Genotype<TWEANN> crossover(Genotype<TWEANN> toModify, Genotype<TWEANN> toReturn) {
		includeExcess = RandomNumbers.randomGenerator.nextFloat() < includeExcessRate;

		TWEANNGenotype tg = (TWEANNGenotype) toReturn;
		TWEANNGenotype tm = (TWEANNGenotype) toModify;

		// Align and cross nodes. Nodes are aligned to archetype
		ArrayList<ArrayList<NodeGene>> alignedNodes = new ArrayList<ArrayList<NodeGene>>(2);
		try {// makes sure to add and adjust nodes so archetypes of both parents match
			alignedNodes.add(alignNodesToArchetype(tm.nodes, tg.archetypeIndex));
		} catch (IllegalArgumentException e) {
			System.out.println("Outputs: " + tm.numOut);
			System.out.println("Modes: " + tm.numModules);
			System.out.println("Neurons Per Mode: " + tm.neuronsPerModule);
			e.printStackTrace();
			System.exit(1);
		}
		try {// makes sure to check the number of nodes match the archetype of the network
			alignedNodes.add(alignNodesToArchetype(tg.nodes, tg.archetypeIndex));
		} catch (IllegalArgumentException e) {
			System.out.println("Outputs: " + tg.numOut);
			System.out.println("Modes: " + tg.numModules);
			System.out.println("Neurons Per Mode: " + tg.neuronsPerModule);
			e.printStackTrace();
			System.exit(1);
		}// crosses nodes
		ArrayList<ArrayList<NodeGene>> crossedNodes = cross(alignedNodes.get(0), alignedNodes.get(1));
		 // Align and cross links. Links are aligned based on innovation order
		// aligns links to faciliate crossover
		ArrayList<ArrayList<LinkGene>> alignedLinks = alignLinkGenes(((TWEANNGenotype) toModify).links, tg.links);
		ArrayList<ArrayList<LinkGene>> crossedLinks = cross(alignedLinks.get(0), alignedLinks.get(1));// crosses links

		// Assign new lists
		int[] originalAssociations = Arrays.copyOf(tm.moduleAssociations, tm.moduleAssociations.length);
		tm.nodes = crossedNodes.get(0);
		tm.links = crossedLinks.get(0);
		tm.calculateNumModules(); // Needed because excess crossover can result in unknown number of modes
		if (CommonConstants.hierarchicalMultitask) {
			tm.crossModuleAssociations(originalAssociations, tg.moduleAssociations);
		}

		TWEANNGenotype result = new TWEANNGenotype(crossedNodes.get(1), crossedLinks.get(1), tg.neuronsPerModule,
				tg.standardMultitask, tg.hierarchicalMultitask, tg.archetypeIndex);
		// This usage doesn't exactly correspond to the new net, but is close
		result.setModuleUsage(Arrays.copyOf(tg.getModuleUsage(), tg.getModuleUsage().length));
		result.calculateNumModules(); // Needed because excess crossover can result in unknown number of modes
		if (CommonConstants.hierarchicalMultitask) {
			result.crossModuleAssociations(tg.moduleAssociations, originalAssociations);
		}
		// checks command line parameters to see if true and performs said task
		if (CommonConstants.meltAfterCrossover) {
			tm.meltNetwork();
			result.meltNetwork();
		} else {// makes sure offspring are alterable so they too can be mutated
			if (!tm.existsAlterableLink()) {
				if (Parameters.parameters.booleanParameter("prefFreezeUnalterable")) {
					new MeltThenFreezePreferenceMutation().mutate(tm);
				} else if (Parameters.parameters.booleanParameter("policyFreezeUnalterable")) {
					new MeltThenFreezePolicyMutation().mutate(tm);
				} else {
					System.out.println("Crossover allowed an unalterable network to be created");
					System.exit(1);
				}
			}
			// makes sure offspring are alterable so they too can be mutated
			if (!result.existsAlterableLink()) {
				if (Parameters.parameters.booleanParameter("prefFreezeUnalterable")) {
					new MeltThenFreezePreferenceMutation().mutate(result);
				} else if (Parameters.parameters.booleanParameter("policyFreezeUnalterable")) {
					new MeltThenFreezePolicyMutation().mutate(result);
				} else {
					System.out.println("Crossover allowed an unalterable network to be created");
					System.exit(1);
				}
			}
		}

		return result;
	}

	/**
	 * Crosses two lists of Genes, that have been previously aligned (in
	 * whatever manner is appropriate). The alignment may have introduced null
	 * slots into the lists, where the two parents do not align.
	 *
	 * @param <G>
	 *            Will be either a NodeGene or LinkGene
	 * @param left
	 *            One list of parent Genes
	 * @param right
	 *            Other list of parent Genes
	 * @return ArrayList containing both lists of offspring Genes (with no
	 *         nulls)
	 */
	@SuppressWarnings("unchecked")
	public <G extends Gene> ArrayList<ArrayList<G>> cross(ArrayList<G> left, ArrayList<G> right) {
		assert(left.size() == right.size()) : "Can't cross lists of different size!\n" + left.size() + ":" + left + "\n"
				+ right.size() + ":" + right;

		ArrayList<G> crossedLeft = new ArrayList<G>(left.size());
		ArrayList<G> crossedRight = new ArrayList<G>(right.size());

		for (int i = 0; i < left.size(); i++) {
			G leftGene = left.get(i);
			G rightGene = right.get(i);

			if (leftGene != null && rightGene != null) {
				assert(leftGene.innovation == rightGene.innovation) : "Misalignment!\n" + left + "\n" + right;
				crossIndex((G) leftGene.copy(), (G) rightGene.copy(), crossedLeft, crossedRight);
			} else {
				if (leftGene != null) {// crosses left gene
					crossedLeft.add((G) leftGene.copy());
					if (includeExcess) {// either keeps or discards
										// excess/disjoint left genes here
						crossedRight.add((G) leftGene.copy());
					}
				}

				if (rightGene != null) {// crosses right gene
					crossedRight.add((G) rightGene.copy());
					if (includeExcess) {// either keeps or discards
										// excess/disjoint right genes here
						crossedLeft.add((G) rightGene.copy());
					}
				}
			}
		}
		ArrayList<ArrayList<G>> pair = new ArrayList<ArrayList<G>>(2);
		pair.add(crossedLeft);
		pair.add(crossedRight);
		return pair;
	}

	/**
	 * Takes two genes, potentially crosses them, and modifies the genotypes
	 * being constructed accordingly. The offspring lists are modified via
	 * side-effects.
	 *
	 * @param <G>
	 *            LinkGene or NodeGene
	 * @param leftGene
	 *            gene from one parent
	 * @param rightGene
	 *            gene from other parent
	 * @param crossedLeft
	 *            partially finished list of genes for offspring 1
	 * @param crossedRight
	 *            partially finished list of genes for offspring 2
	 */
	public <G extends Gene> void crossIndex(G leftGene, G rightGene, ArrayList<G> crossedLeft,
			ArrayList<G> crossedRight) {
		boolean swap = RandomNumbers.randomGenerator.nextBoolean();
		if (swap) {
			Pair<G, G> p = swap(leftGene, rightGene);
			leftGene = p.t1;
			rightGene = p.t2;
		}
		crossedLeft.add(leftGene);
		crossedRight.add(rightGene);
	}

	/**
	 * Takes a list of NodeGenes and inserts nulls (in a different returned
	 * list) in order to align the list to the archetype list of nodes, which
	 * tracks the universal ordering of nodes across all networks.
	 *
	 * @param list
	 *            = list of node genes to be aligned
	 * @return aligned list with nulls in the slots that don't match with
	 *         archetype.
	 */
	private static ArrayList<NodeGene> alignNodesToArchetype(ArrayList<NodeGene> list, int archetypeIndex) {
		ArrayList<NodeGene> archetype = EvolutionaryHistory.archetypes[archetypeIndex];
		ArrayList<NodeGene> aligned = new ArrayList<NodeGene>(archetype.size());

		// Deal with matching and disjoint genes
		int listPos = 0, archetypePos = 0;
		while (listPos < list.size() && archetypePos < archetype.size()) {
			// System.out.println("l: " + l + ", r: " + r);
			long leftInnovation = list.get(listPos).innovation;
			long rightInnovation = archetype.get(archetypePos).innovation;
			if (leftInnovation == rightInnovation) {
				// System.out.println("Same innovation: " + leftInnovation);
				aligned.add(list.get(listPos++));
				archetypePos++;
			} else {// checks if misaligned
				Integer pos = containsInnovationAt(archetype, leftInnovation);
				if (pos == null) {
					System.out.println("archetypeIndex: " + archetypeIndex);
					System.out.println("How can archetype not have innovation? " + leftInnovation);
					System.out.println("Archetype:" + archetype);
					System.out.println("List:" + list);
					throw new IllegalArgumentException();
				} else if (pos <= archetypePos) {
					System.out.println("Mappings:" + CombiningTWEANNCrossover.oldToNew);
					System.out.println("archetypeIndex: " + archetypeIndex);
					System.out.println("Already passed the innovation! " + leftInnovation);
					System.out.println("Archetype:" + archetype);
					System.out.println("List:" + list);
					printNodeAlignmentColumns(list, archetypeIndex);
					throw new IllegalArgumentException();
				} else {
					// Fill with blanks until gene is reached
					aligned.add(null);
					// Corresponding archetype gene is somewhere ahead
					archetypePos++;
				}
			}
		}

		while (archetypePos < archetype.size()) {// adds nulls to fill out
													// archetype so both matches
			aligned.add(null);
			archetypePos++;
		}
		return aligned;
	}

	/**
	 * Method for printing nodes in columns to visually determine if they are
	 * aligned
	 * 
	 * @param list
	 *            array list of genes to be printed
	 * @param archetypeIndex
	 *            index of the archetype that corresponds with the correct
	 *            generation from the evolutionary history
	 */
	public static void printNodeAlignmentColumns(ArrayList<NodeGene> list, int archetypeIndex) {
		ArrayList<NodeGene> archetype = EvolutionaryHistory.archetypes[archetypeIndex];
		// Deal with matching and disjoint genes
		int listPos = 0, archetypePos = 0;
		while (listPos < list.size() && archetypePos < archetype.size()) {
			// System.out.println("l: " + l + ", r: " + r);
			long leftInnovation = list.get(listPos).innovation;
			long rightInnovation = archetype.get(archetypePos).innovation;
			if (leftInnovation == rightInnovation) {
				System.out.println(archetypePos + ":\t" + leftInnovation + ",\t" + rightInnovation + "\t("
						+ CombiningTWEANNCrossover.oldToNew.get(leftInnovation) + ")\t"
						+ archetype.get(archetypePos).ntype);
				listPos++;
				archetypePos++;
			} else {
				Integer pos = containsInnovationAt(archetype, leftInnovation);
				if (pos == null) {
					System.out.println("ERROR:" + leftInnovation + " not present");
					throw new IllegalArgumentException();
				} else if (pos <= archetypePos) {
					System.out.println("ERROR:" + leftInnovation + " at " + pos + " before " + archetypePos);
					throw new IllegalArgumentException();
				} else {
					System.out.println(archetypePos + ":\tnull,\t" + rightInnovation + "\t("
							+ CombiningTWEANNCrossover.oldToNew.get(rightInnovation) + ")\t"
							+ archetype.get(archetypePos).ntype);
					archetypePos++;
				}
			}
		}

		while (archetypePos < archetype.size()) {
			NodeGene ng = archetype.get(archetypePos);
			System.out.println(archetypePos + ":\tnull,\t" + ng.innovation + "\t" + ng.ntype);
			archetypePos++;
		}
	}

	/**
	 * Index in list of genes where innovation number is found
	 *
	 * @param <G>
	 *            LinkGene or NodeGene
	 * @param genes
	 *            = list of genes
	 * @param innovation
	 *            = innovation number to search for
	 * @return index in "list" where gene with innovation was found
	 */
	private static <G extends Gene> Integer containsInnovationAt(ArrayList<G> genes, long innovation) {
		for (int i = 0; i < genes.size(); i++) {
			if (genes.get(i).innovation == innovation) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Aligns link genes by innovation numbers. Easier to do in the LinkGene
	 * case because no ordering is required for correct network execution.
	 * Therefore, links are simply sorted by their innovation numbers.
	 *
	 * @param left
	 *            = list of parent link genes
	 * @param right
	 *            = list of other parent's link genes
	 * @return ArrayList of two lists: the aligned link genes of each offspring,
	 *         with nulls where genes don't align.
	 */
	private static ArrayList<ArrayList<LinkGene>> alignLinkGenes(ArrayList<LinkGene> left, ArrayList<LinkGene> right) {
		mergeDuplicates(left, right);
		TWEANNGenotype.sortLinkGenes(left);
		TWEANNGenotype.sortLinkGenes(right);

		int maxSize = Math.max(left.size(), right.size());
		ArrayList<LinkGene> alignedLeft = new ArrayList<LinkGene>(maxSize);
		ArrayList<LinkGene> alignedRight = new ArrayList<LinkGene>(maxSize);

		// System.out.println("Align loop start");
		// Deal with matching and disjoint genes
		int leftPos = 0, rightPos = 0;
		while (leftPos < left.size() && rightPos < right.size()) {
			int l = leftPos, r = rightPos;
			// System.out.println("l: " + l + ", r: " + r);
			long leftInnovation = left.get(leftPos).innovation;
			long rightInnovation = right.get(rightPos).innovation;
			if (leftInnovation == rightInnovation) {
				// System.out.println("Same innovation: " + leftInnovation);
				alignedLeft.add(left.get(leftPos++));
				alignedRight.add(right.get(rightPos++));
			} else {
				// System.out.println("Diff innovation: " + leftInnovation + ",
				// " + rightInnovation);
				Integer leftHasRightAt = containsInnovationAt(left, rightInnovation);
				Integer rightHasLeftAt = containsInnovationAt(right, leftInnovation);
				// System.out.println("Innovation: leftHasRight: " +
				// leftHasRightAt + ", rightHasLeft: " + rightHasLeftAt);

				if (leftHasRightAt == null) {
					// System.out.println("leftHasRight is null");
					alignedLeft.add(null);
					alignedRight.add(right.get(rightPos++));
				} else if (rightHasLeftAt == null) {
					// System.out.println("rightHasLeftAt is null");
					alignedLeft.add(left.get(leftPos++));
					alignedRight.add(null);
				}
			}
			if (l == leftPos && r == rightPos) {
				System.out.println("No progress performing crossover: " + l + "," + r);
				System.out.println("Left: " + left);
				System.out.println("Right: " + right);
				System.out.println("alignedLeft: " + alignedLeft);
				System.out.println("alignedRight: " + alignedRight);
				// System.exit(1);
				return null;
			}
		}
		// System.out.println("Align loop done");

		// Deal with excess genes
		while (leftPos < left.size()) {
			alignedLeft.add(left.get(leftPos++));
			alignedRight.add(null);
		}

		while (rightPos < right.size()) {
			alignedLeft.add(null);
			alignedRight.add(right.get(rightPos++));
		}

		ArrayList<ArrayList<LinkGene>> pair = new ArrayList<ArrayList<LinkGene>>(2);
		pair.add(alignedLeft);
		pair.add(alignedRight);
		return pair;
	}

	/**
	 * When crossing excess genes, networks can have multiple links connecting
	 * the same two nodes. This method merges those links into one by changing
	 * innovation numbers
	 *
	 * @param left
	 *            link genes of parent 1
	 * @param right
	 *            link genes of parent 2
	 */
	private static void mergeDuplicates(ArrayList<LinkGene> left, ArrayList<LinkGene> right) {
		for (LinkGene lg : left) {
			for (LinkGene rg : right) {
				if (lg.sourceInnovation == rg.sourceInnovation && lg.targetInnovation == rg.targetInnovation
						&& lg.innovation != rg.innovation) {
					rg.innovation = lg.innovation;
				}
			}
		}
	}
}
