package boardGame.fitnessFunction;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class StaticOtherOpponentFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T> {
	
	BoardGamePlayer<T> opponent;
	BoardGameFitnessFunction<T> selectionFunction;
	
	List<BoardGameFitnessFunction<T>> fitFunctions = new ArrayList<BoardGameFitnessFunction<T>>();

	@SuppressWarnings("unchecked")
	public StaticOtherOpponentFitness(){
		try {
			opponent = (BoardGamePlayer<T>) ClassCreation.createObject("boardGameOpponent"); // The Opponent; TODO: Add OtherStaticOpponent Parameter?
			selectionFunction = new SimpleWinLoseDrawBoardGameFitness<T>();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
		
		fitFunctions.add(selectionFunction);
	}
	
	@Override
	public double getFitness(BoardGamePlayer<T> player) {
		BoardGamePlayer<T>[] players = new BoardGamePlayer[]{player, opponent};
		
		ArrayList<Pair<double[], double[]>> score = BoardGameUtil.playGame(MMNEAT.boardGame, players, fitFunctions);
		
		return score.get(0).t1[0];
	}

	@Override
	public void updateFitness(BoardGameState bgs, int index) {
		// Doesn't update until getFitness
	}

	@Override
	public String getFitnessName() {
		return "Static Opponent Fitness";
	}

	@Override
	public void reset() {
		// Doesn't need to be reset
	}

}
