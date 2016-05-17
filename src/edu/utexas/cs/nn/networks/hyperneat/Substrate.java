package edu.utexas.cs.nn.networks.hyperneat;

import java.util.List;

import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * Class that creates a substrate for hyperNEAT
 * 
 * @author gillespl
 *
 */
public class Substrate {
	
	public static final int STYPE_INPUT = 0;
    public static final int STYPE_HIDDEN = 1;
    public static final int STYPE_OUTPUT = 2;
    public static final int SPACE_DIMENSIONS = 3;
    public static final int DEFAULT_SIZE = 10;
    
    public final Pair<Integer, Integer> size;
    public final int stype;
    public final int[] subLocation = new int[SPACE_DIMENSIONS];//not sure if array is best choice for this 
    
    public boolean connectToSameLayer;
    public boolean fullyConnected;
    public List<Substrate> connectedSubs;
    public List<Pair<Integer, Integer>> occupied;
    
    public Substrate() {
    	this(new Pair<Integer, Integer>(DEFAULT_SIZE, DEFAULT_SIZE), STYPE_INPUT, new int[]{0, 0, 0}, false, true);
    }

	public Substrate(Pair<Integer, Integer> size, int stype, int[] subLocation, boolean connectToSameLayer, boolean fullyConnected) {
    	this.size = size;
    	this.stype = stype;
    	this.connectToSameLayer = connectToSameLayer;
    	this.fullyConnected = fullyConnected;
    	assert(subLocation.length == SPACE_DIMENSIONS);
    	for(int i = 0; i < subLocation.length; i++) {
    		this.subLocation[i] = subLocation[i];
    	}
    }

	public void connectSubstrate(Substrate other) {
		if(!connectToSameLayer && stype == other.stype) {
			throw new IllegalArgumentException("Can't connect to same layer substrate!");
		}
		if(!size.equals(other.size)){
			throw new IllegalArgumentException("Substrate sizes do not match!! Sub1: " + size.t1*size.t2 + " Sub2: " + other.size.t1*other.size.t2);
		}
		for(int i = 0; i < occupied.size(); i++) {
			for(int j = 0; j < other.occupied.size(); j++) {
				Substrate.connect(other.occupied.get(j), occupied.get(i));
			}
		}
		connectedSubs.add(other);
	}

	private static void connect(Pair pair, Pair pair2) {
		// not sure how to make this method
		
	}
	
	public void addOccupied(Pair<Integer, Integer> toAdd){
		occupied.add(toAdd);
	}
}
