package edu.southwestern.networks.hyperneat.architecture;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * Defines methods to add hidden layers to HyperNEAT via cascade 
 * @author Devon Fulcher
 */
public class CascadeNetworks {
	/**
	 * Adds a new layer in between the previous last hidden layer and the output layer with
	 * connectivity from the previous last hidden layer to this new last hidden layer as 
	 * connectivityType and connectivity from this new last hidden layer to the output
	 * layer as being fully connected
	 * @param originalHiddenArchitecture hidden architecture that a new layer will be added to
	 * @param originalConnectivity connectivity that new connectivity will be added to
	 * @param outputSubstrateNames unique names of the output substrates
	 * @param newLayerWidth the width(the number of substrates) of the new layer
	 * @param newSubstratesWidth the width of each substrate that is added
	 * @param newsubstratesHeight the height of each substrate that is added
	 * @param connectionType how these two substrates are connected(i.e. full, convolutional,...)
	 * @return a deep copy of the previous architecture and connectivity with the new layer added
	 */
	public static Pair<List<Triple<Integer, Integer, Integer>>, List<SubstrateConnectivity>> cascadeExpansion (
			List<Triple<Integer, Integer, Integer>> originalHiddenArchitecture,
			List<SubstrateConnectivity> originalConnectivity,
			List<String> outputSubstrateNames,
			int newLayerWidth, int newSubstratesWidth, int newsubstratesHeight, int connectivityType) {
		//create new hidden architecture
		List<Triple<Integer, Integer, Integer>> newArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		for(Triple<Integer, Integer, Integer> layer : originalHiddenArchitecture) {
			newArchitecture.add(layer.copy());
		}
		newArchitecture.add(new Triple<Integer, Integer, Integer>(newLayerWidth, newSubstratesWidth, newsubstratesHeight));
		//create new hidden architecture end
		//create new connectivity
		List<SubstrateConnectivity> newConnectivity = new ArrayList<SubstrateConnectivity>();
		for(SubstrateConnectivity connection: originalConnectivity) {
			newConnectivity.add(connection.copy());
		}
		//connect new layer to last hidden layer
		int lastHiddenLayerLocation = originalHiddenArchitecture.size() - 1; //y location of last hidden layer and location of last hidden layer in originalHiddenArchitecture
		for(int i = 0; i < originalHiddenArchitecture.get(lastHiddenLayerLocation).t1; i++) { //originalHiddenArchitecture.get(lastHiddenLayerLocation).t1 = the width of the last hidden layer
			for(int j = 0; j < newLayerWidth; j++) {
				newConnectivity.add(new SubstrateConnectivity(
						"process(" + i + "," + lastHiddenLayerLocation + ")", 
						"process(" + j + "," + (lastHiddenLayerLocation + 1) + ")", connectivityType));
			}
		}
		//connect new layer to last hidden layer end
		//connect new layer to output layer
		for(int i = 0; i < newLayerWidth; i++) {
			for(String outputSubstrateName: outputSubstrateNames) {
				newConnectivity.add(new SubstrateConnectivity(
						"process(" + i + "," + (lastHiddenLayerLocation + 1) + ")",
						outputSubstrateName, SubstrateConnectivity.CTYPE_FULL));
			}
		}
		//connect new layer to output layer end
		//create new connectivity end
		return new Pair<List<Triple<Integer, Integer, Integer>>, List<SubstrateConnectivity>>(newArchitecture, newConnectivity);
	}
	
	/**
	 * Adds a new layer in between the previous last hidden layer and the output layer with
	 * connectivity from the previous last hidden layer to this new last hidden layer as 
	 * fully connected and connectivity from this new last hidden layer to the output
	 * layer as being fully connected. The width and height of each substrate in new last hidden layer
	 * is the same as the previous last hidden layer
	 * @param originalHiddenArchitecture hidden architecture that a new layer will be added to
	 * @param originalConnectivity connectivity that new connectivity will be added to
	 * @param outputSubstrateNames unique names of the output substrates
	 * @param newLayerWidth the width(the number of substrates) of the new layer
	 * @return a deep copy of the previous architecture and connectivity with the new layer added
	 */
	public static Pair<List<Triple<Integer, Integer, Integer>>, List<SubstrateConnectivity>> cascadeExpansion (
			List<Triple<Integer, Integer, Integer>> originalHiddenArchitecture,
			List<SubstrateConnectivity> originalConnectivity,
			List<String> outputSubstrateNames,
			int newLayerWidth) {
		Triple<Integer, Integer, Integer> lastHiddenLayer = originalHiddenArchitecture.get(originalHiddenArchitecture.size() - 1);
		return cascadeExpansion (originalHiddenArchitecture, originalConnectivity, outputSubstrateNames, newLayerWidth,
				lastHiddenLayer.t2,  lastHiddenLayer.t3, SubstrateConnectivity.CTYPE_FULL);
	}

	/**
	 * Causes a cascade expansion on each member of the HyperNEATCPPNAndSubstrateArchitectureGenotype population. This assumes that each member of the population is identical.
	 * The expansion on each network will be a new layer of width 1 with convolutional connectivity defined by the parameters receptiveFieldWidth and receptiveFieldHeight.
	 * The width and height of this new substrate is derived from the receptiveFieldWidth and receptiveFieldHeight.
	 * @param population population of HyperNEATCPPNAndSubstrateArchitectureGenotype instances
	 * @return population of genotypes after expansion or null if another convolutional layer of the appropriate size is not permitted
	 */
	public static <T> ArrayList<Genotype<T>> cascadeExpandAllGenotypes(ArrayList<Genotype<T>> population) {
		assert population.get(0) instanceof HyperNEATCPPNAndSubstrateArchitectureGenotype;
		List<Triple<Integer,Integer,Integer>> exemplarNetworksHiddenArchitecture = ((HyperNEATCPPNAndSubstrateArchitectureGenotype) population.get(0)).hiddenArchitecture;
		Triple<Integer, Integer, Integer> lastLayerInExemplar = exemplarNetworksHiddenArchitecture.get(exemplarNetworksHiddenArchitecture.size() - 1);
		int receptiveFieldHeight = Parameters.parameters.integerParameter("receptiveFieldHeight");
		int receptiveFieldWidth = Parameters.parameters.integerParameter("receptiveFieldWidth");
		int newSubstrateWidth = lastLayerInExemplar.t2 - (2 * (receptiveFieldWidth / 2));
		int newSubstrateHeight = lastLayerInExemplar.t3 - (2 * (receptiveFieldHeight / 2));
		if (newSubstrateWidth > 0 && newSubstrateHeight > 0) {			
			return cascadeExpandAllGenotypes(population, 1, newSubstrateWidth, newSubstrateHeight, SubstrateConnectivity.CTYPE_CONVOLUTION);
		} else {
			return null;
		}
	}
	
	/**
	 * applies cascade expansion to to each genotype in a population
	 * @param hnt hyperNeatTask
	 * @param population collection of genotypes
	 * @return population of genotypes after expansion
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Genotype<T>> cascadeExpandAllGenotypes(ArrayList<Genotype<T>> population, int newLayerWidth, int newSubstratesWidth, int newSubstratesHeight, int connectivityType) {
		ArrayList<Genotype<T>> substrateGenotypes = new ArrayList<>();
		assert population.get(0) instanceof HyperNEATCPPNAndSubstrateArchitectureGenotype;
		for(int i = 0; i < population.size(); i++) {
			//int numOutBefore = ((HyperNEATCPPNAndSubstrateArchitectureGenotype) population.get(i)).numOut;
			HyperNEATCPPNAndSubstrateArchitectureGenotype genotype = (HyperNEATCPPNAndSubstrateArchitectureGenotype) population.get(i).copy();
			//int numOutAfter = genotype.numOut;
			//assert numOutBefore == numOutAfter : "Copy breaks the numOut: " + numOutBefore + " is not " + numOutAfter;
			((HyperNEATTask) MMNEAT.task).flushSubstrateMemory(); // Wipe substrates each time before changing them
			genotype.cascadeExpansion(newLayerWidth, newSubstratesWidth, newSubstratesHeight, connectivityType);
			//int numOutAfterExpansion = genotype.numOut;
			//assert numOutAfterExpansion > numOutAfter : "Cascade expansion did not increase number of outputs: " + numOutAfter + " and " + numOutAfterExpansion;
			substrateGenotypes.add((Genotype<T>) genotype);
		}
		return substrateGenotypes;
	}  
}
