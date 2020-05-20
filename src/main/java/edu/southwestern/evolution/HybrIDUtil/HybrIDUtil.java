package edu.southwestern.evolution.HybrIDUtil;

import java.util.ArrayList;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
//import edu.southwestern.networks.hyperneat.HyperNEATVisualizationUtil;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.PopulationUtil;

public class HybrIDUtil {
	/**
	 * Resets the archetype because the evolved CPPN genes are no longer relevant.
	 * @param exemplarNN member of new population that will define the genotype
	 */
	private static void resetArchetype(TWEANNGenotype exemplarNN) {
		// Reset next innovation based on the maximum in the exemplar genotype
		long maxInnovation = 0;
		for(LinkGene lg : exemplarNN.links) {
			maxInnovation = Math.max(maxInnovation, lg.innovation);
		}
		EvolutionaryHistory.setInnovation(maxInnovation+1);
		EvolutionaryHistory.initArchetype(0, null, exemplarNN);
	}

	/**
	 * Turns off all HyperNEAT parameters that do not apply to NEAT. Mainly turns off CPPN.
	 */
	private static void deactivateHyperNEAT() {
		// Turn HyperNEAT off
		CommonConstants.hyperNEAT = false;
		Parameters.parameters.setBoolean("hyperNEAT", false);
		// HyperNEAT disables monitorInputs, but if the parameter was true, then hybrID can turn it back on
		CommonConstants.monitorInputs = Parameters.parameters.booleanParameter("monitorInputs");
		// Turn off HyperNEAT visualizations
		//HyperNEATVisualizationUtil.clearHyperNEATVisualizations();
		// Need small genes because there are so many of them
		TWEANNGenotype.smallerGenotypes = true; // Since whole population is larger substrate networks, need them small
		// Switch from CPPNs to plain TWEANNs
		Parameters.parameters.setClass("genotype", TWEANNGenotype.class);
		// Substrate networks cannot have different activation functions
		CommonConstants.netChangeActivationRate = 0;
		Parameters.parameters.setDouble("netChangeActivationRate", 0);
		// Only CPPNs have multiple activation functions, but standard NNs do not
		CommonConstants.allowMultipleFunctions = false;
		Parameters.parameters.setBoolean("allowMultipleFunctions", false);
	}

	/**
	 * Switches the given population from HyperNEAT to NEAT for use in HybridID
	 * @param population the hyperNEAT population
	 * @return the NEAT population
	 */
	public static <T> ArrayList<Genotype<T>> switchPhenotypeToNEAT(ArrayList<Genotype<T>> population) {
		deactivateHyperNEAT();

		// Get substrate genotypes
		population = PopulationUtil.getSubstrateGenotypesFromCPPNs(HyperNEATUtil.getHyperNEATTask(), population, 0); // 0 is only population

		// Reset archetype because the evolved CPPN genes are no longer relevant.
		// 0 indicates that there is only one population, null will cause the archetype to reset, 
		// and the nodes from the nodes from the first member of the new population will define the genotype	
		resetArchetype((TWEANNGenotype) population.get(0).copy());
		return population;
	}
}
