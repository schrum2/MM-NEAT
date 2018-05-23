package main;

import java.util.EnumMap;

import entrants.ghosts.spooky.*;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.entries.pacman.MyPacMan;
import pacman.game.Constants.GHOST;
import entrants.pacman.spooky.*;

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

        executor.runGameTimed(new Derpy(), new MASController(controllers));
    }
}
