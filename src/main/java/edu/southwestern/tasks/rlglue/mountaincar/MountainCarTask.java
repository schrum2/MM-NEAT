package edu.utexas.cs.nn.tasks.rlglue.mountaincar;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;
import edu.utexas.cs.nn.tasks.rlglue.mountaincar.MountainCarViewer;

public class MountainCarTask<T extends Network> extends RLGlueTask<T> {

	public MountainCarTask() {
		super();
		MMNEAT.registerFitnessFunction("RL Return");
		if (CommonConstants.watch) {
			if (MountainCarViewer.current == null) {
				System.out.println("New MountainCarViewer");
				MountainCarViewer.current = new MountainCarViewer();
			} else {
				System.out.println("Same MountainCarViewer");
				MountainCarViewer.current.reset();
			}
		}
	}
	
	@Override
	public String[] outputLabels() {
		return new String[]{"Reverse", "Neutral", "Forward"};
	}
}
