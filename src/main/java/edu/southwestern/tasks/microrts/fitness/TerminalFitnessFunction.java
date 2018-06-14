package edu.southwestern.tasks.microrts.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.units.Unit;

public class TerminalFitnessFunction extends RTSFitnessFunction{
	
	public TerminalFitnessFunction(){}

	/**
	 * scores performance in a game
	 * @param terminalGameState
	 * @return pair of double[], the first of which has {victory, time, unitDifference}
	 */
	public ArrayList<Pair<double[], double[]>> getFitness(GameState terminalGameState) {
		
		Pair<double[], double[]> score = new Pair<>(new double[3], new double[2]); 
		//first[]:{victory, time, unitDifference, } on a scale from -1 to 1, except unit difference, which starts at 0 and can go up or down
		
		pgs = terminalGameState.getPhysicalGameState();
		int gameEndTime = terminalGameState.getTime();
		List<Unit> unitsLeft = terminalGameState.getUnits();

		//If agent won, else if agent lost, else if agent tied
		if(terminalGameState.winner() == 0){
			//victory organism being tested! 
			//record the win
			score.t1[0] = 1;
			
			//record the length of the game
			score.t1[1] = (double) (maxCycles - gameEndTime) / maxCycles * RESULTRANGE - 1; //lower time is better
			
			//count the remaining units
			for(Unit u : unitsLeft){
				if(u.getType().name != "Resource") score.t1[2] += u.getType().cost;
			}
		} else if(terminalGameState.winner() == 1){
			//defeat for organism being tested
			
			//record the loss
			score.t1[0] = -1;
			
			//record the length of the game
			score.t1[1] = (double) (maxCycles - gameEndTime) / maxCycles * -1 * RESULTRANGE + 1; //holding out for longer is better
			
			//record the number of units left
			for(Unit u : unitsLeft){
				if(u.getType().name != "Resource") score.t1[2] -= u.getType().cost;
			}
		} else if(terminalGameState.winner() == -1){ //tie, ran out of time
			
			//record the tie
			score.t1[0] = 0;
			
			//record the units left
			for(Unit u : unitsLeft){
				if(u.getPlayer()==0) score.t1[2] += u.getType().cost;
				else if(u.getPlayer()==1) score.t1[2] -= u.getType().cost;
			}
			
			//if winning hard but didn't close out => very bad, if basically even => okay, if losing but held out => great
			score.t1[1] = -1 * score.t1[2] / (pgs.getHeight()*pgs.getWidth());  
		}
		
		
		int winner = terminalGameState.winner(); //0:win 1:loss -1:tie
		
		//record the other scores
		double[]other = new double[]{
				winner + 1 % 2, //1:win 0:tie -1:loss (from ai1's perspective)
				terminalGameState.getTime()
		};
		
		score.t2 = other;
		//System.out.println("result?: "+score.t1[0] + " unit-difference: "+ score.t1[2] + " time: " +score.t1[1]);
		ArrayList<Pair<double[], double[]>> result = new ArrayList<>();
		result.add(score);
		return result;
	} //END fitnessFunction

	@Override
	//Getter function for the current fitness functions
	public String[] getFunctions() {
		return new String[]{"win/loss","time","unit-difference"};
	}

	//not used
	@Override
	public String[] getOtherScores() { 
		return new String[0];
	}

}
