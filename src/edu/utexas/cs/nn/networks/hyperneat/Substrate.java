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

	public final static int INPUT_SUBSTRATE = 0;
	public final static int PROCCESS_SUBSTRATE = 1;
	public final static int OUTPUT_SUBSTRATE = 2;
    public final String name;//unique string identifier for substrate
    public final Pair<Integer, Integer> size;//encodes size of rectangular substrate (sticking with 2D for now)
    public final int stype;//encodes type of substrate
    public final Triple<Integer, Integer, Integer> subLocation;//location of substrate in vector space  
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
    public Substrate(Pair<Integer, Integer> size, int stype, Triple<Integer, Integer, Integer> subLocation, String name) {
        this.size = size;
        this.stype = stype;
        this.name = name;
        //this.connectToSameLayer = connectToSameLayer;
        this.subLocation = subLocation;

    }

    public String getName() {
        return this.name;
    }

    public Pair<Integer, Integer> getSize() {
        return this.size;
    }

    public int getStype() {
        return this.stype;
    }

    public Triple<Integer, Integer, Integer> getSubLocation() {
        return this.subLocation;
    }
}
