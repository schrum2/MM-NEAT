package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.UnionInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.mediators.components.NonGhostSensors;
import java.util.ArrayList;
import java.util.List;

/**
 * Components that do not sense ghosts. To be combined with Ghost Monitors
 *
 * @author Jacob Schrum
 */
public class FlattenedGhostMonitorsMediator extends UnionInputOutputMediator {

	public FlattenedGhostMonitorsMediator() {
		super(mediators());
	}

	public static List<BlockLoadedInputOutputMediator> mediators() {
		boolean ghostMonitorsSensePills = Parameters.parameters.booleanParameter("ghostMonitorsSensePills");
		ArrayList<BlockLoadedInputOutputMediator> result = new ArrayList<BlockLoadedInputOutputMediator>(5);
		result.add(new NonGhostSensors());
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			result.add(ghostMonitorsSensePills ? new OneGhostAndPillsMonitorInputOutputMediator(i)
					: new OneGhostMonitorInputOutputMediator(i));
		}
		return result;
	}
}
