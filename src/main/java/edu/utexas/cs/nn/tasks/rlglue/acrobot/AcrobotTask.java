package edu.utexas.cs.nn.tasks.rlglue.acrobot;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;

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
