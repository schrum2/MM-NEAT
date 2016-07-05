package edu.utexas.cs.nn.tasks.vizdoom;

public class VizDoomHealthGatherSupremeTask extends VizDoomHealthGatherTask {
	
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
