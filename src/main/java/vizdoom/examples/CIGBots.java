package vizdoom.examples;

import vizdoom.*;

public class CIGBots {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		SpecifyDLL.specifyDLLPath();

		DoomGame game = new DoomGame();

		System.out.println("\n\nCIG BOTS EXAMPLE\n");

		// Use CIG example config or Your own.
		game.loadConfig("vizdoom/examples/config/cig.cfg");

		// Select game and map You want to use.
		game.setDoomGamePath("vizdoom/scenarios/freedoom2.wad");
		// game.setDoomGamePath("vizdoom/scenarios/doom2.wad"); // Not provided
		// with environment due to licences.

		game.setDoomMap("map01"); // Limited deathmatch.
		// game.setDoomMap("map02"); // Full deathmatch.

		// Start multiplayer game only with Your AI (with options that will be
		// used in the competition, details in CIGHost example).
		game.addGameArgs(
				"-host 1 -deathmatch +timelimit 10.0 +sv_forcerespawn 1 +sv_noautoaim 1 +sv_respawnprotect 1 +sv_spawnfarthest 1");

		// Name Your AI.
		game.addGameArgs("+name AI");

		game.setMode(Mode.ASYNC_PLAYER); // Multiplayer requires the use of
											// asynchronous modes.
		game.init();

		for (int i = 0; i < 7; ++i) {
			game.sendGameCommand("addbot");
		}

		while (!game.isEpisodeFinished()) { // Play until the game (episode) is
											// over.

			if (game.isPlayerDead()) { // Check if player is dead
				game.respawnPlayer(); // Use this to respawn immediately after
										// death, new state will be available.

				// Or observe the game until automatic respawn.
				// game.advanceAction();
				// continue;
			}

			GameState state = game.getState();
			// Analyze the state.

			int[] action = new int[game.getAvailableButtonsSize()];
			// Set your action.

			game.makeAction(action);

			System.out.println(game.getEpisodeTime() + " Frags: " + game.getGameVariable(GameVariable.FRAGCOUNT));
		}

		game.close();
	}
}
