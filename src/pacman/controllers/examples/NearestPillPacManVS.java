package pacman.controllers.examples;

import java.awt.Color;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.DM;

import static pacman.game.Constants.*;

/*
 * Same as NearestPillPacMan but does some visuals to illustrate what can be done.
 * Please note: the visuals are just to highlight different functionalities and may
 * not make sense from a controller's point of view (i.e., they might not be useful)
 * Comment/un-comment code below as desired (drawing all visuals would probably be too much).
 */
public final class NearestPillPacManVS extends Controller<MOVE> {

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    @SuppressWarnings("deprecation")
    public MOVE getMove(Game game, long timeDue) {
        int currentNodeIndex = game.getPacmanCurrentNodeIndex();
        int[] activePills = game.getActivePillsIndices();
        int[] activePowerPills = game.getActivePowerPillsIndices();
        int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];

        for (int i = 0; i < activePills.length; i++) {
            targetNodeIndices[i] = activePills[i];
        }

        for (int i = 0; i < activePowerPills.length; i++) {
            targetNodeIndices[activePills.length + i] = activePowerPills[i];
        }

        int nearest = game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, targetNodeIndices, DM.PATH);

        //add the path that Ms Pac-Man is following
//		GameView.addPoints(game,Color.GREEN,game.getShortestPath(game.getPacmanCurrentNodeIndex(),nearest));

        //add the path from Ms Pac-Man to the first power pill
//		GameView.addPoints(game,Color.CYAN,game.getShortestPath(game.getPacmanCurrentNodeIndex(),game.getPowerPillIndices()[0]));
//		GameView.addPoints(game,Color.CYAN,game.getAStarPath(game.getPacmanCurrentNodeIndex(),game.getPowerPillIndices()[0]));
//		GameView.addPoints(game,Color.YELLOW,game.getNonReversePath(game.getPacmanCurrentNodeIndex(),game.getPowerPillIndices()[0],game.getPacmanLastMoveMade()));

        //add lines connecting Ms Pac-Man and the power pills
//		for(int i=0;i<activePowerPills.length;i++)
//			GameView.addLines(game,Color.CYAN,game.getPacmanCurrentNodeIndex(),activePowerPills[i]);

        //add lines to the ghosts (if not in lair) - green if edible, red otherwise
//		for(GHOST ghostType : GHOST.values())									
//			if(game.getGhostLairTime(ghostType)==0)
//				if(game.isGhostEdible(ghostType))
//					GameView.addLines(game,Color.GREEN,game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghostType));
//				else
//					GameView.addLines(game,Color.RED,game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghostType));


        //add the path AND ghost path from Ghost 0 to the first power pill (to illustrate the differences)
        if (game.getGhostLairTime(GHOST.BLINKY) == 0 && activePowerPills.length > 0) {
            GameView.addPoints(game, Color.RED, game.getAStarPath(game.getGhostCurrentNodeIndex(GHOST.BLINKY), activePowerPills[0], MOVE.NEUTRAL));
            GameView.addPoints(game, Color.YELLOW, game.getAStarPath(game.getGhostCurrentNodeIndex(GHOST.BLINKY), activePowerPills[0], game.getGhostLastMoveMade(GHOST.BLINKY)));
        }

        //add the path from Ghost 0 to the closest power pill
//		if(game.getGhostLairTime(GHOST.BLINKY)==0 && activePowerPills.length>0)
//			GameView.addPoints(game,Color.WHITE,game.getAStarPath(game.getGhostCurrentNodeIndex(GHOST.BLINKY),
//					game.getClosestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(GHOST.BLINKY),activePowerPills,DM.PATH)));

        //adds the paths the ghost would need to follow to reach Ms Pac-Man
//		Color[] colors={Color.RED,Color.BLUE,Color.MAGENTA,Color.ORANGE};
//		
//		int index=0;
//		
//		for(GHOST ghostType : GHOST.values())
//			if(game.getGhostLairTime(ghostType)==0)
//				GameView.addPoints(game,colors[index++],game.getAStarPath(game.getGhostCurrentNodeIndex(ghostType),currentNodeIndex,game.getGhostLastMoveMade(ghostType)));

        return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), nearest, DM.PATH);
    }
}