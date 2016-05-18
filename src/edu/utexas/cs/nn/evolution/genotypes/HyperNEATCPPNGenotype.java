package edu.utexas.cs.nn.evolution.genotypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * genotype for a hyperNEAT CPPN network
 * 
 * @author gillespl
 *
 */
public class HyperNEATCPPNGenotype extends TWEANNGenotype {

	private static final double BIAS = 1.0;//neccessary for most CPPN networks
	int neuronsPerModule = -1; // todo
	int innovationID = 0;//provides unique innovation numbers for links and genes

	/**
	 * Default constructor. Not yet finished
	 */
	HyperNEATCPPNGenotype() {
		super(); 
		// Probably need some extra code here
	}

	/**
	 * Uses another CPPN to create a TWEANN controller for the domain.
	 * This created TWEANN is unique only to the instance in which it is used.
	 * In a sense, it's a one-and-done network, which explains the lax use of innovation numbers
	 */
	public TWEANN getPhenotype() {

		TWEANN cppn = super.getPhenotype();//cppn used to create TWEANN network
		HyperNEATTask hnt = (HyperNEATTask) MMNEAT.task;//creates instance of task in question
		List<Substrate> subs = hnt.getSubstrateInformation();//used to extract substrate information from domian
		List<Pair<String,String>> connections = hnt.getSubstrateConnectivity();//used to extract substrate connectity from domain

		innovationID = 0;//reset here just in case TWEANN reused(should not happen though)
		ArrayList<NodeGene> nodes = createSubstrateNodes(subs);

		// Will map substrate names to index in subs List
		HashMap<String, Integer> substrateIndexMapping = new HashMap<String,Integer>();
		for(int i = 0; i < subs.size(); i++) {
			substrateIndexMapping.put(subs.get(i).getName(), i);
		}
		// loop through connections and add links, based on contents of subs
		ArrayList<LinkGene> links = createNodeLinks(cppn, connections, subs, substrateIndexMapping); // todo
		//the instantiation of the TWEANNgenotype in question 
		//-1 will probably crash ... consider solutions 
		// set up archetype to handle -1 as a spacial value to ignore
		TWEANNGenotype tg = new TWEANNGenotype(nodes, links, neuronsPerModule, false, false, -1);		


		return tg.getPhenotype();
	}

	/**
	 * creates an array list containing all the nodes from all the substrates
	 * 
	 * @param subs list of substrates extracted from domain
	 * @return array llist of NodeGenes from substrates
	 */
	public ArrayList<NodeGene> createSubstrateNodes(List<Substrate> subs) {
		ArrayList<NodeGene> nodes = new ArrayList<NodeGene>(); 
		for(int i = 0; i < subs.size(); i++) {//loops through substrate list
			for(int j = 0; j < subs.get(i).size.t1; j++) {//loops through x values of substrate
				for(int k = 0; k < subs.get(i).size.t2; k++){//loops through y values of substrate
					nodes.add(new NodeGene(CommonConstants.ftype, subs.get(i).getStype(), innovationID++));
				}
			}
		}
		return nodes;
	}

	/**
	 * creates an array list of links between substrates as dictated by connections parameter
	 * 
	 * @param cppn used to evolve link weight
	 * @param connections list of different connections between substrates
	 * @param subs list of substrates in question
	 * @param sIMap hashmap that maps the substrate in question to its index in the substrate list
	 *
	 * @return array list containing all the links between substrates
	 */
	public ArrayList<LinkGene> createNodeLinks(TWEANN cppn, List<Pair<String,String>> connections, List<Substrate> subs, HashMap<String, Integer> sIMap) {
		ArrayList<LinkGene> result = new ArrayList<LinkGene>();
		for(int i = 0; i < connections.size(); i++) {
			int s1Index = sIMap.get(connections.get(i).t1);
			int s2Index = sIMap.get(connections.get(i).t2);
			Substrate s1 = subs.get(s1Index);
			Substrate s2 = subs.get(s2Index);
			result.addAll(loopThroughLinks(cppn, i, s1, s2, s1Index, s2Index, subs));//adds links from between two substrates to whole list of links
		} 
		return result;
	}

	/**
	 * a method for looping through all nodes of two substrates to be linked
	 * 
	 * @param cppn used to evolve link weight
	 * @param outputIndex index from cppn outputs to be used as weight in creating link
	 * @param s1 first substrate to be linked
	 * @param s2 second substrate to be linked
	 * @param s1Index index of first substrate in substrate list
	 * @param s2Index index of second substrate in substrate list
	 * @param subs list of substrates
	 * 
	 * @return array list containing the genes linked between the two substrates
	 */
	public ArrayList<LinkGene> loopThroughLinks(TWEANN cppn, int outputIndex, Substrate s1, Substrate s2, int s1Index, int s2Index, List<Substrate> subs) {
		ArrayList<LinkGene> lg = new ArrayList<LinkGene>();
		for(int X1 = 0; X1 < s1.size.t1; X1++) {//searches through width of first substrate
			for(int Y1 = 0; Y1 < s1.size.t2; Y1++) {//searches through height of first substrate
				for(int X2 = 0; X2 < s2.size.t1; Y1++) {//searches through width of second substrate
					for(int Y2 = 0; Y2 < s2.size.t2; Y2++) {//searches through height of second substrate
						double[] inputs = {(double)X1,(double) X2,(double) Y1,(double) Y2, BIAS};//inputs to CPPN
						lg.add(new LinkGene(getInnovationID(X1, Y1, s1Index, subs),
								getInnovationID(X2, Y2, s2Index, subs), cppn.process(inputs)[outputIndex], innovationID++, false));

					}
				}
			}
		}



		return lg;
	}

	/**
	 * returns the innovation id of the node in question
	 * 
	 * @param x x-coordinate of node
	 * @param y y-coordinate of node
	 * @param sIndex index of substrate in question
	 * @param subs list of substrates available
	 * 
	 * @return innovationID of link in question
	 */
	public long getInnovationID(int x, int y, int sIndex, List<Substrate> subs) {
		long innovationID = 0;
		int i = 0;
		while(i < sIndex) {
			innovationID += subs.get(i).size.t1*subs.get(i).size.t2;
		}
		innovationID += subs.get(sIndex).size.t1*(y-1) + x; 
		return innovationID;
	}
}
