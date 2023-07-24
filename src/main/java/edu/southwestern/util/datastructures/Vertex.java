package edu.southwestern.util.datastructures;

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
		Vertex copy = new Vertex(this); // copy this vertex
		copy.sub(vertex); // subtract the other vertex from it
		// copy is now a vector whose distance from origin is the distance between original points
		return copy.length(); // distance of result vector
	}
	
	/**
	 * If any component of the vertex is NaN
	 * @return true if any are NaN
	 */
	public boolean anyNaN() {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
	}
}


