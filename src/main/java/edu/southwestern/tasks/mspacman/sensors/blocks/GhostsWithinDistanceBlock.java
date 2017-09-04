package edu.southwestern.tasks.mspacman.sensors.blocks;

import java.util.Arrays;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.util.CombinatoricUtilities;

/**
 *
 * @author Jacob Schrum
 */
public class GhostsWithinDistanceBlock extends MsPacManSensorBlock {

	private final int absence;
	private final boolean[] mask;
	private final boolean threats;
	private final int distance;

	/**
	 * mask corresponds to the ordering of ghosts by proximity ... not their
	 * index
	 *
	 * @param mask
	 * @param threats
	 * @param distance
	 */
	public GhostsWithinDistanceBlock(boolean[] mask, boolean threats, int distance) {
		this.mask = mask;
		this.threats = threats;
		this.distance = distance;
		this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
	}

	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		Integer[] ghostIndices = new Integer[CommonConstants.numActiveGhosts];
		for (int i = 0; i < ghostIndices.length; i++) {
			ghostIndices[i] = i;
		}
		Arrays.sort(ghostIndices, new GhostComparator(gf, !threats, false));
		int current = gf.getPacmanCurrentNodeIndex();
		for (int i = 0; i < ghostIndices.length; i++) {
			if (mask[i]) {
				int ghostLoc = gf.getGhostCurrentNodeIndex(ghostIndices[i]);
				boolean rightType = threats ? gf.isGhostThreat(ghostIndices[i]) : gf.isGhostEdible(ghostIndices[i]);
				double ghostDistance = gf.getPathDistance(current, ghostLoc);
				boolean sense = ghostDistance < distance;
				// System.out.println(ghostLoc + ":" + rightType + ":" +
				// ghostDistance + ":" + sense);
				inputs[in++] = rightType ? (sense ? 1.0 : 0) : absence;
				// System.out.println(ghostLoc + ":" + rightType + ":" + sense +
				// ":" + distance + ":" + ghostDistance);
				if (CommonConstants.watch && rightType && sense) {
					gf.addLines(CombinatoricUtilities.colorFromInt(i), current, ghostLoc);
				}
			}
		}
		return in;
	}

	public int incorporateLabels(String[] labels, int in) {
		String type = threats ? "Threat" : "Edible";
		for (int i = 0; i < mask.length; i++) {
			if (mask[i]) {
				labels[in++] = i + " Distant " + type + " Ghost Within " + distance;
			}
		}
		return in;
	}

	public int numberAdded() {
		int total = 0;
		for (int i = 0; i < mask.length; i++) {
			if (mask[i]) {
				total++;
			}
		}
		return total;
	}

	@Override
	public boolean equals(MsPacManSensorBlock o) {
		if (o != null && o instanceof GhostsWithinDistanceBlock) {
			GhostsWithinDistanceBlock other = (GhostsWithinDistanceBlock) o;
			return other.threats == this.threats && other.distance == this.distance
					&& Arrays.equals(other.mask, this.mask);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + Arrays.hashCode(this.mask);
		hash = 97 * hash + (this.threats ? 1 : 0);
		hash = 97 * hash + this.distance;
		hash = 97 * hash + super.hashCode();
		return hash;
	}
}
