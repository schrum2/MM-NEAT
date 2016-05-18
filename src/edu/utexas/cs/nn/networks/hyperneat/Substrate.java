package edu.utexas.cs.nn.networks.hyperneat;

import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

/**
 * Class that creates a substrate for hyperNEAT
 * 
 * @author gillespl
 *
 */
public class Substrate {
   
    public final String name;//unique string identifier for substrate
    public final Pair<Integer, Integer> size;//encodes size of rectangular substrate (sticking with 2D for now)
    public final int stype;//encodes type of substrate
    public final Triple<Integer,Integer,Integer> subLocation;//location of substrate in vector space  
   // public final boolean connectToSameLayer;
    //public boolean fullyConnected;//don't know alternatives yet
    //public List<Pair<Integer, Integer>> occupied;// come back to later
    
    /**
     * constructor for a substrate 
     * 
     * @param size pair representing size of substrate
     * @param stype type of substrate
     * @param subLocation location in vector space of substrate
     * @param name unique string identifier for substrate
     */
	public Substrate(Pair<Integer, Integer> size, int stype, Triple<Integer,Integer,Integer> subLocation, String name) {
    	this.size = size;
    	this.stype = stype;
    	this.name = name;
    	//this.connectToSameLayer = connectToSameLayer;
    	this.subLocation = subLocation.clone();
    	
    }

	public String getName(){
		return this.name;
	}
	
	public Pair<Integer, Integer> getSize() {
		return this.size;
	}
	
	public int getStype() {
		return this.stype;
	}
	
	public Triple<Integer, Integer,Integer> getSubLocation() {
		return this.subLocation;
	}
	
	
//	public void connectSubstrate(Substrate other) {
//		if(!connectToSameLayer && stype == other.stype) {
//			throw new IllegalArgumentException("Can't connect to same layer substrate!");
//		}
//		if(!size.equals(other.size)){
//			throw new IllegalArgumentException("Substrate sizes do not match!! Sub1: " + size.t1*size.t2 + " Sub2: " + other.size.t1*other.size.t2);
//		}
//		for(int i = 0; i < occupied.size(); i++) {
//			for(int j = 0; j < other.occupied.size(); j++) {
//				Substrate.connect(other.occupied.get(j), occupied.get(i));
//			}
//		}
//		connectedSubs.add(other);
//	}


}
