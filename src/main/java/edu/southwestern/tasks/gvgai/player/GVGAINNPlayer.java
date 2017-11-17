package edu.southwestern.tasks.gvgai.player;

import edu.southwestern.networks.Network;
import gvgai.core.player.AbstractPlayer;

/**
 * Universal parent for individual GVG-AI player classes that use a neural network controller
 * @author schrum2
 *
 * @param <T> Type of neural network
 */
public abstract class GVGAINNPlayer<T extends Network> extends AbstractPlayer {

	protected Network network;
	
	public static final double BIAS = 1.0;
	
	/**
	 * Need to be called for each new genotype/network being evaluated,
	 * since it is not designated by a constructor.
	 * @param net
	 */
	public void assignNetwork(Network net) {
		network = net;
	}
}
