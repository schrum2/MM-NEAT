package boardGame;

import java.util.List;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.match.Match;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;

import boardGame.agents.BoardGamePlayer;
import edu.utexas.cs.nn.util.ClassCreation;
import external.JSON.JSONException;

public class GGPBoardGame implements BoardGame {
	
	Game game;
	Match match;
	
	public GGPBoardGame(){
		try {
			game = (Game) ClassCreation.createObject("ggpGame"); // TODO: Create this Parameter
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			match = new Match(null, game, null);
		} catch (JSONException | SymbolFormatException | GdlFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public int getNumPlayers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isGameOver() {
		return match.isCompleted();
	}

	@Override
	public double[] getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getWinners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(BoardGamePlayer bgp) {
		bgp.takeAction(null); // Nothing in GGP really translates to a BoardGameState; nothing to put in here...
	}

	@Override
	public int getCurrentPlayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BoardGameState getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return game.getName();
	}

	@Override
	public void reset() {
		try {
			match = new Match(null, game, null);
		} catch (JSONException | SymbolFormatException | GdlFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
