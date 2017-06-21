package edu.utexas.cs.nn.util.datastructures;

import javax.vecmath.Vector3d;

/**
 * Used for 3D object rendering
 * 
 * @author twerasei
 *
 */

@SuppressWarnings("serial")
public class Vertex extends Vector3d {
	public Vertex(double x, double y, double z) {
		super(x,y,z);
	}

	/**
	 * Copy constructor
	 * @param other
	 */
	public Vertex(Vertex other) {
		this(other.x, other.y, other.z);
	}
	
	/**
	 * Adds values of an input vertex to the current vertex
	 * 
	 * @param v Input vertex to be added to current vertex
	 * @return Vertex with the sum of both vertexes at each index
	 */
	public Vertex add(Vertex v) {
		Vertex newV = new Vertex(this);
		newV.add((Vector3d) v);
		return newV;
	}
	
	/**
	 * Calculates distance between current Vertex and input vertex
	 * 
	 * @param vertex Vertex at certain distance away from current vertex
	 * @return distance between two vertexes
	 */
	public double distance(Vertex vertex) {
		Vertex oldV = new Vertex(this);
		double newV = Math.sqrt((Math.pow(oldV.x, 2)-Math.pow(vertex.x, 2)) + ((Math.pow(oldV.y, 2)-Math.pow(vertex.y, 2)) + ((Math.pow(oldV.z, 2)-Math.pow(vertex.z, 2)))));
		return newV;
	}
	
	
}


