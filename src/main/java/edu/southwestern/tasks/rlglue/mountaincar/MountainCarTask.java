package edu.southwestern.tasks.rlglue.mountaincar;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.rlglue.RLGlueTask;
import edu.southwestern.tasks.rlglue.mountaincar.MountainCarViewer;

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
