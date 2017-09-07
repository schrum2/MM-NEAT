package vizdoom.examples;

import vizdoom.*;

import java.util.*;

public class Shaping {

	public static void main(String[] args) {
		SpecifyDLL.specifyDLLPath();

		System.out.println("\n\nSHAPING EXAMPLE\n");

		// Create DoomGame instance. It will run the game and communicate with
		// you.
		DoomGame game = new DoomGame();

		// Health gathering scenario has scripted shaping reward.
		game.loadConfig("vizdoom/examples/config/health_gathering.cfg");
		game.setViZDoomPath("vizdoom/bin/vizdoom");

		// Sets path to doom2 iwad resource file which contains the actual doom
		// game-> Default is "./doom2.wad".
		game.setDoomGamePath("vizdoom/scenarios/freedoom2.wad");
		// game.setDoomGamePath("vizdoom/scenarios/doom2.wad"); // Not provided
		// with environment due to licences.

		// Sets resolution. Default is 320X240
		game.setScreenResolution(ScreenResolution.RES_320X240);

		// Schrum: why was this missing?
		game.setDoomScenarioPath("vizdoom/scenarios/health_gathering.wad");
		// Also missing
		game.addAvailableButton(Button.TURN_LEFT);
		game.addAvailableButton(Button.TURN_RIGHT);
		game.addAvailableButton(Button.MOVE_FORWARD);
		// Also missing
		game.setDoomMap("map01");
		game.setSeed(1234);

		// Initialize the game. Further configuration won't take any effect from
		// now on.
		game.init();

		List<int[]> actions = new ArrayList<int[]>();
		actions.add(new int[] { 1, 0, 1 });
		actions.add(new int[] { 0, 1, 1 });
		actions.add(new int[] { 0, 0, 1 });

		Random ran = new Random();

		// Run this many episodes
		int episodes = 10;

		// Use this to remember last shaping reward value.
		double lastTotalShapingReward = 0;
		for (int i = 0; i < episodes; ++i) {

			System.out.println("Episode #" + (i + 1));

			// Starts a new episode. It is not needed right after init() but it
			// doesn't cost much and the loop is nicer.
			game.newEpisode();

			while (!game.isEpisodeFinished()) {
                // Get the state
                GameState state = game.getState();

                // Make random action and get reward
                double reward = game.makeAction(actions.get(ran.nextInt(3)));

                // Retrieve the shaping reward
                double _ssr = game.getGameVariable(GameVariable.USER1); // Get value of scripted variable
                
                // Schrum: This command is absent
                double ssr = game.doomFixedToDouble(_ssr);              // If value is in DoomFixed format project it to double
                double sr = ssr - lastTotalShapingReward;
                lastTotalShapingReward = ssr;

                System.out.println("State #" + state.number);
                System.out.println("Health: " + Arrays.toString(state.gameVariables));
                System.out.println("Action reward: " + reward);
                System.out.println("Action shaping reward: " + sr);
                System.out.println("=====================");

			}

			System.out.println("Episode finished.");
			System.out.println("Total reward: " + game.getTotalReward());
			System.out.println("************************");

		}

		// It will be done automatically in destructor but after close You can
		// init it again with different settings.
		game.close();
	}
}
