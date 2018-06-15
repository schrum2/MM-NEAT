package edu.southwestern.tasks.mspacman.sensors.directional;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 * A sensor block defined with respect to a particular direction. The direction
 * can be set in each of the available directions and then checks repeatedly to
 * get different readings for each direction.
 * @author Jacob Schrum
 */
public abstract class VariableDirectionBlock extends MsPacManSensorBlock {

	public int dir = -1;

	/**
	 * A fixed direction can be specified, though it is common to change the direction
	 * before each sensor reading.
	 * @param dir
	 */
	public VariableDirectionBlock(int dir) {
		this.dir = dir;
	}

	/**
	 * Setting the direction immediately before taking a sensor reading
	 * @param dir
	 */
	public final void setDirection(int dir) {
		assert dir >= 0 && dir <= 3 : "Valid directions are from 0 to 3. " + dir + " is not valid!";
		this.dir = dir;
	}

	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		final int current = gf.getPacmanCurrentNodeIndex();
		final int[] neighbors = gf.neighbors(current);
		assert dir >= 0 && dir <= 3 : "Direction must be in range 0-3: " + dir + " is not in this range!";
		inputs[in++] = neighbors[dir] == -1 ? wallValue() : getValue(gf);
		assert!Double.isNaN(inputs[in - 1]) : "Value is NaN: " + this.getLabel() + ":"
				+ this.getClass().getSimpleName();
		return in;
	}

	/**
	 * Reading that this sensor gives if the designated direction currently points
	 * at a wall.
	 * @return Sensor value associated with a wall
	 */
	public abstract double wallValue();

	/**
	 * Specific sensor reading for the current direction
	 * @param gf Game state through a facade
	 * @return Sensor reading when there is no wall
	 */
	public abstract double getValue(GameFacade gf);

	public int incorporateLabels(String[] labels, int startPoint) {
		labels[startPoint++] = getLabel() + " in dir " + dir;
		return startPoint;
	}

	/**
	 * Label of what is being read
	 * @return 
	 */
	public abstract String getLabel();

	/**
	 * There is always only one sensor for the given direction
	 */
	public int numberAdded() {
		return 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && this.getClass() == o.getClass()) {
			VariableDirectionBlock other = (VariableDirectionBlock) o;
			return this.dir == other.dir;
		}
		return false;
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException(
				"Directional sensors should not be used in situations where hash code is needed");
		// int hash = 7;
		// hash = 37 * hash + this.dir;
		// hash = 37 * hash + this.getClass().getName().hashCode();
		// return hash;
	}
}
