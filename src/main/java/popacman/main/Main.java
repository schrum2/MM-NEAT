package popacman.main;


import java.util.EnumMap;

import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import edu.southwestern.tasks.popacman.controllers.MyPacMan;
import pacman.game.Constants.GHOST;
import popacman.entrants.ghosts.spooky.*;
import popacman.entrants.pacman.spooky.*;

/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    public static void main(String[] args) {

        Executor executor = new Executor.Builder()
                .setVisual(true)
                .setTickLimit(4000)
                .build();

        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);

        controllers.put(GHOST.INKY, new George());
        controllers.put(GHOST.BLINKY, new Ringo());
        controllers.put(GHOST.PINKY, new Paul());
        controllers.put(GHOST.SUE, new John());

        executor.runGameTimed(new MyPacMan(), new MASController(controllers));
    }
}
