package competition.cig.robinbaumgarten;

import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.RandomAStarSimulator;

public class PlayfulAStarAgent extends AStarAgent
{

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        sim = new RandomAStarSimulator();
    }
}
