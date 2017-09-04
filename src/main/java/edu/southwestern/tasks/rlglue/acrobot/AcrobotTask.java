package edu.southwestern.tasks.rlglue.acrobot;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.rlglue.RLGlueTask;

public class AcrobotTask<T extends Network> extends RLGlueTask<T> {
	
	public AcrobotTask() {
		super();
		MMNEAT.registerFitnessFunction("RL Return");
		if (CommonConstants.watch) {
			if (AcrobotViewer.current == null) {
				System.out.println("New AcrobotViewer");
				AcrobotViewer.current = new AcrobotViewer();
			} else {
				System.out.println("Same AcrobotViewer");
				AcrobotViewer.current.reset(null);
			}
		}
	}
	
	@Override
	public String[] outputLabels() {
		return new String[]{"Negative Torque", "No Torque", "Positive Torque"};
	}
}
