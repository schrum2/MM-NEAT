/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components.NonGhostSensors;
import java.util.ArrayList;
import java.util.List;

/**
 * Each ghost monitor has a redundant copy of sensors pertaining to other
 * objects in the world like pills, and the safety of junctions
 *
 * @author Jacob
 */
public class OneGhostAndPillsMonitorInputOutputMediator extends UnionInputOutputMediator {

	// Should only be used by ClassCreation
	public OneGhostAndPillsMonitorInputOutputMediator() {
		this(0);
		Parameters.parameters.setBoolean("ghostMonitorsSensePills", true);
	}

	public OneGhostAndPillsMonitorInputOutputMediator(int ghostIndex) {
		super(getMediators(ghostIndex));
	}

	public static List<BlockLoadedInputOutputMediator> getMediators(int ghostIndex) {
		ArrayList<BlockLoadedInputOutputMediator> mediators = new ArrayList<BlockLoadedInputOutputMediator>(2);
		mediators.add(new OneGhostMonitorInputOutputMediator(ghostIndex));
		mediators.add(new NonGhostSensors());
		return mediators;
	}
}
