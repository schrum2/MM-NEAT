package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class BoardGameUtil {
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<Pair<double[], double[]>> playGame(BoardGame bg, BoardGamePlayer[] players){
		bg.reset();
		while(!bg.isGameOver()){
			if(Parameters.parameters.booleanParameter("stepByStep")){
				System.out.print("Press enter to continue");
				System.out.println(bg.toString());
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			bg.move(players[bg.getCurrentPlayer()]);
		}
//		System.out.println("Game over");
//		System.out.println(game);
		
		List<Integer> winners = bg.getWinners();
		ArrayList<Pair<double[], double[]>> scoring = new ArrayList<Pair<double[], double[]>>(bg.getNumPlayers());
		
		for(int i = 0; i < players.length; i++){
		
		double fitness = winners.size() > 1 && winners.contains(i) ? 0 : // multiple winners means tie: fitness is 0 
						(winners.get(0) == i ? 1 // If the one winner is 0, then the neural network won: fitness 1
											 : -2); // Else the network lost: fitness -2
				
		Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] { fitness }, new double[0]);
		scoring.add(evalResults);
		 }
		
		
		return scoring; // Returns the Fitness of the individual's Genotype<T>
	}
	
}
