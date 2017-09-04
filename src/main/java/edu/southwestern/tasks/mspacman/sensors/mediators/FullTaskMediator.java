package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.UnionInputOutputMediator;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class FullTaskMediator extends UnionInputOutputMediator {

	public FullTaskMediator() {
		super(Arrays.asList(new BlockLoadedInputOutputMediator[] { new GhostTaskMediator(), new PillTaskMediator() }));
	}
}
