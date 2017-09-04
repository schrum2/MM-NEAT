package edu.utexas.cs.nn.networks.hyperneat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

/**
 * Class that creates a substrate for hyperNEAT
 *
 * @author Lauren Gillespie
 *
 */
public class Substrate {

	public static final boolean heterogeneousSubstrateActivations = Parameters.parameters.booleanParameter("heterogeneousSubstrateActivations");
	
	public final static int INPUT_SUBSTRATE = 0;
	public final static int PROCCESS_SUBSTRATE = 1;
	public final static int OUTPUT_SUBSTRATE = 2;
	// unique string identifier for substrate
	private final String name;
	// encodes size of rectangular substrate (sticking with 2D for now)
	private final Pair<Integer, Integer> size;
	// encodes type of substrate: INPUT_SUBSTRATE, PROCCESS_SUBSTRATE, or OUTPUT_SUBSTRATE
	private final int stype;
	// location of substrate in vector space.
	private final Triple<Integer, Integer, Integer> subLocation;
	// Set of neurons in this substrate that cannot process information.
	// Not all neurons within the rectangle may make sense to use.
	private HashSet<Pair<Integer, Integer>> deadNeurons;
	// Ordered list of locations of all neurons within this substrate
	private final List<Pair<Integer,Integer>> neuronCoordinates;
	// The activation function used by these neurons
	private final int ftype;
	// Default activation function is to use whatever the Parameter setting is
	public static final int DEFAULT_ACTIVATION_FUNCTION = -1;
	
	public Substrate(Pair<Integer, Integer> size, int stype, Triple<Integer, Integer, Integer> subLocation,
			String name) {
		this(size,stype,subLocation,name,DEFAULT_ACTIVATION_FUNCTION);
	}
	
	/**
	 * constructor for a substrate
	 *
	 * @param size
	 *            pair representing size of substrate
	 * @param stype
	 *            type of substrate
	 * @param subLocation
	 *            location in vector space of substrate
	 * @param name
	 *            unique string identifier for substrate
	 * @param ftype
	 * 			  type of activation functions used by neurons in this layer
	 */
	public Substrate(Pair<Integer, Integer> size, int stype, Triple<Integer, Integer, Integer> subLocation,
			String name, int ftype) {
		this.size = new Pair<Integer, Integer>(size.t1,size.t2); // copy
		this.stype = stype;
		this.name = name;
		// this.connectToSameLayer = connectToSameLayer;
		this.subLocation = subLocation;
		this.deadNeurons = new HashSet<Pair<Integer, Integer>>();
		this.ftype = ftype;
		
		neuronCoordinates = new ArrayList<Pair<Integer,Integer>>(numberOfNeurons());
		for (int y = 0; y < size.t2; y++) {
			for (int x = 0; x < size.t1; x++) {
				neuronCoordinates.add(new Pair<>(x,y));
			}
		}
	}

	/**
	 * Returns name of substrate
	 * name is a unique identifier
	 * for each substrate
	 * @return name of substrate
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns size of substrate
	 * @return size of substrate
	 */
	public Pair<Integer, Integer> getSize() {
		return this.size;
	}

	/**
	 * Returns the type of substrate
	 * @return type of substrate
	 */
	public int getStype() {
		return this.stype;
	}

	/**
	 * Returns the type of the activation function (see ActivationFunctions)
	 * for neurons in this substrate.
	 * @return function type.
	 */
	public int getFtype() {
		return !heterogeneousSubstrateActivations || ftype == DEFAULT_ACTIVATION_FUNCTION ? CommonConstants.ftype : ftype;
	}
	
	/**
	 * Returns the location of substrate in substrate space.
	 * First coordinate is x location moving left to right inside of one layer (all at same depth).
	 * Second coordinate is y height with input layer at height 0 and each layer above with y coordinate one higher.
	 * Third coordinate z is not currently used, and it always 0, but this could change.
	 * @return location of substrate
	 */
	public Triple<Integer, Integer, Integer> getSubLocation() {
		return this.subLocation;
	}

	/**
	 * Number of neurons in the substrate layer.
	 * Simply the width times the height.
	 * 
	 * @return Number of neurons.
	 */
	public int numberOfNeurons() {
		return size.t1 * size.t2;
	}
	
	/**
	 * Return a list of all neuron coordinates contained in the substrate
	 * (including the dead ones) in the order they should be traversed to
	 * create a substrate network in HyperNEAT (HyperNEATCPPNGenotype uses
	 * a mapping between innovation IDs and substrate coordinates to help
	 * define links between neurons, so order is important).
	 * 
	 * Note: 1D substrates can be represented by simply having one coordinate
	 *       value fixed, but changes will need to be made if you ever move
	 *       to 3D substrates. 
	 * 
	 * @return List of ordered substrate coordinates
	 */
	public List<Pair<Integer,Integer>> coordinateList() {
		return neuronCoordinates;
	}
	
	/**
	 * Provides some basic summary information about the substrate.
	 * @return String summary of substrate contents
	 */
	public String toString() {
		return "Substrate name: " + this.name + " size: " + this.size.toString() + " stype: " + this.getStype()
				+ " getSubLocation: " + this.getSubLocation().toString();
	}

	/**
	 * Indicates that the neuron at the given coordinate can have no input or output links.
	 * The neuron stays dead if it was already dead.
	 * 
	 * @param x x-coordinate of neuron to kill
	 * @param y y-coordinate of neuron to kill
	 */
	public void addDeadNeuron(int x, int y) {
		addDeadNeuron(new Pair<>(x,y));
	}	
	
	/**
	 * Adds a dead neuron to substrate
	 * dead neuron is a neuron that no links to
	 * or from will be created
	 * @param deadNeuron neuron to kill
	 */
	public void addDeadNeuron(Pair<Integer, Integer> deadNeuron) {
		deadNeurons.add(deadNeuron);
	}
	
	/**
	 * Removes all dead neurons from substrate (brings them all back to life)
	 */
	public void removeDeadNeurons() {
		this.deadNeurons = new HashSet<Pair<Integer, Integer>>();
	}
	
	/**
	 * Returns whether or not neuron at given
	 * location is dead
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return if neuron is dead
	 */
	public boolean isNeuronDead(int x, int y) {
		return deadNeurons.contains(new Pair<Integer, Integer>(x, y));
	}
	
	/**
	 * Kills all neurons in substrate
	 */
	public void killAllNeurons() {
		for(int i = 0; i < size.t1; i++) {
			for(int j = 0; j < size.t2; j++) {
				addDeadNeuron(i, j);
			}
		}
	}

	/**
	 * Bring neuron back to life if dead, and return
	 * whether a dead neuron was actually resurrected
	 * (as opposed to not being present in the first place)
	 * @param x x-coord
	 * @param y y-coord
	 * @return whether neuron was dead originally
	 */
	public boolean resurrectNeuron(int x, int y) {
		return deadNeurons.remove(new Pair<>(x, y));
	}
}
