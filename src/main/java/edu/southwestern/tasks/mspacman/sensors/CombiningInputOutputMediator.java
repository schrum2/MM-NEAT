package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.GhostEatingNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.PillEatingNetworkBlock;

/**
 *
 * @author Jacob Schrum
 */
public class CombiningInputOutputMediator<T extends Network> extends BlockLoadedInputOutputMediator {

	public CombiningInputOutputMediator() {
		super();
		try {
			blocks.add(new GhostEatingNetworkBlock<T>());
			blocks.add(new PillEatingNetworkBlock<T>());
		} catch (NoSuchMethodException ex) {
			System.out.println("Sub mediator classes not loading properly");
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
