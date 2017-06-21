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

	public Vertex add(Vertex v) {
		Vertex newV = new Vertex(this);
		newV.add((Vector3d) v);
		return newV;
	}
}


