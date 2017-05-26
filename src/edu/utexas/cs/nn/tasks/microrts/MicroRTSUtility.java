package edu.utexas.cs.nn.tasks.microrts;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import micro.rts.PhysicalGameState;

public class MicroRTSUtility {

	public <T> Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method that returns a list of information about the substrate layers
	 * contained in the network.
	 *
	 * @return List of Substrates in order from inputs to hidden to output
	 *         layers
	 */
	public static List<Substrate> getSubstrateInformation(PhysicalGameState pgs) {
		int height = pgs.getHeight();
		int width = pgs.getWidth();
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		
		Substrate inputsBoardState = new Substrate(new Pair<Integer, Integer>(width, height),
				Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Inputs Board State");
		subs.add(inputsBoardState);
		
		if(Parameters.parameters.booleanParameter("")) {
			
		}
			
		Substrate processing = new Substrate(new Pair<Integer, Integer>(width, height), 
				Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.PROCCESS_SUBSTRATE, 0), "Processing");
		subs.add(processing);
		Substrate output = new Substrate(new Pair<Integer, Integer>(1,1),
				Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Output");
		subs.add(output);
		return subs;
	} 
	
	public static List<Pair<String, String>> getSubstrateConnectivity(PhysicalGameState pgs) {
		ArrayList<Pair<String, String>> conn = new ArrayList<Pair<String, String>>();
		
			conn.add(new Pair<String, String>("Inputs Board State", "Processing"));
		} else {
			
		}
		
		conn.add(new Pair<String, String>("Processing","Output"));
		if(Parameters.parameters.booleanParameter("extraHNLinks")) {

			} else {
				conn.add(new Pair<String, String>("Inputs Board State","Output"));
			}
		}
		return conn;
	}
}
