package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.OffsetHybrIDGenotype;
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
	 * TODO
	 * @param hnt
	 * @param population
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Genotype<T>> getSubstrateGenotypesFromCPPNs(ArrayList<Genotype<T>> population, int newLayerWidth, int newSubstratesWidth, int newsubstratesHeight, int connectivityType) {
		ArrayList<Genotype<T>> substrateGenotypes = new ArrayList<>();
		for(int i = 0; i < population.size(); i++) {
			HyperNEATCPPNAndSubstrateArchitectureGenotype genotype = new HyperNEATCPPNAndSubstrateArchitectureGenotype((HyperNEATCPPNGenotype) population.get(i));
			genotype.cascadeExpansion(newLayerWidth, newSubstratesWidth, newsubstratesHeight, connectivityType);
			substrateGenotypes.add((Genotype<T>) genotype);
		}
		return substrateGenotypes;
	}  
}
