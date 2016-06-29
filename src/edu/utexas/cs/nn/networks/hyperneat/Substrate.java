package edu.utexas.cs.nn.networks.hyperneat;

import java.util.HashSet;

import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

/**
 * Class that creates a substrate for hyperNEAT
 *
 * @author Lauren Gillespie
 *
 */
public class Substrate {

	public final static int INPUT_SUBSTRATE = 0;
	public final static int PROCCESS_SUBSTRATE = 1;
	public final static int OUTPUT_SUBSTRATE = 2;
	// unique string identifier for substrate
	public final String name;
	// encodes size of rectangular substrate (sticking with 2D for now)
	public final Pair<Integer, Integer> size;
	// encodes type of substrate
	public final int stype;
	// location of substrate in vector space
	public final Triple<Integer, Integer, Integer> subLocation;
	public HashSet<Pair<Integer, Integer>> deadNeurons;
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
	 */
	public Substrate(Pair<Integer, Integer> size, int stype, Triple<Integer, Integer, Integer> subLocation,
			String name) {
		this.size = size;
		this.stype = stype;
		this.name = name;
		// this.connectToSameLayer = connectToSameLayer;
		this.subLocation = subLocation;
		this.deadNeurons = new HashSet<Pair<Integer, Integer>>();
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
	 * Returns the location of substrate in substrate space
	 * @return location of substrate
	 */
	public Triple<Integer, Integer, Integer> getSubLocation() {
		return this.subLocation;
	}

	/**
	 * Overridden toString for substrate
	 */
	public String toString() {
		return "Substrate name: " + this.name + " size: " + this.size.toString() + " stype: " + this.getStype()
				+ " getSubLocation: " + this.getSubLocation().toString();
	}

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
	 * Removes all dead neurons from substrate
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
		return deadNeurons.contains(new Pair<Integer, Integer>(x, y)) ? true : false;
	}
}
