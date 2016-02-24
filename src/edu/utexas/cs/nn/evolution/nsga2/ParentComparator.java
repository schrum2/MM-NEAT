package edu.utexas.cs.nn.evolution.nsga2;

import edu.utexas.cs.nn.scores.Better;

/**
 *
 * @author Jacob Schrum
 */
public class ParentComparator extends CrowdingDistanceComparator implements Better<NSGA2Score> {

    @Override
    public int compare(NSGA2Score o1, NSGA2Score o2) {
        return (o1.getRank() == o2.getRank()) ? super.compare(o1, o2) : (o2.getRank() - o1.getRank());
    }

    public NSGA2Score better(NSGA2Score o1, NSGA2Score o2) {
        NSGA2Score winner = (compare(o1, o2) < 0) ? o2 : o1;
//        NSGA2Score loser = (winner == o1 ? o2 : o1);
//        System.out.println(winner + " beats " + loser);
        return winner;
    }
}
