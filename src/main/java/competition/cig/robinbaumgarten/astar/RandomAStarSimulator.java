package competition.cig.robinbaumgarten.astar;

import java.util.ArrayList;

import edu.southwestern.util.random.RandomNumbers;


public class RandomAStarSimulator extends AStarSimulator
{    
	public double optimalPercent = 0.001; // How often optimal A* action is chosen
	
    protected SearchNode pickBestPos(ArrayList<SearchNode> posPool)
    {
    	if(RandomNumbers.randomGenerator.nextDouble() < optimalPercent) {
    		// Still pick optimal action sometimes to encourage progress
    		return super.pickBestPos(posPool);
    	} else {
    		// Just pick a random position and return it
    		SearchNode bestPos = posPool.get(RandomNumbers.randomGenerator.nextInt(posPool.size()));
    		posPool.remove(bestPos);
    		return bestPos;
    	}
    }
        
}