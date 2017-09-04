package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.UnionInputOutputMediator;
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
