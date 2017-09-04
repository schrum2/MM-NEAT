package edu.southwestern.tasks.vizdoom;

import edu.southwestern.networks.Network;

public class VizDoomHealthGatherSupremeTask<T extends Network> extends VizDoomHealthGatherTask<T> {
	
	public VizDoomHealthGatherSupremeTask() {
		super();
	}

	@Override
	public void taskSpecificInit() {
		game.loadConfig("vizdoom/examples/config/health_gathering.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/health_gathering_supreme.wad");
		game.setDoomMap("map01");
	}
}
