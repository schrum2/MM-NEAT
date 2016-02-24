package edu.utexas.cs.nn.breve2D.dynamics;

import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.agent.Breve2DAction;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 */
public class FightOrFlight extends MultitaskDynamics implements RammingDynamics {

    public FightOrFlight() {
        super(new Breve2DDynamics[]{new RammingPlayer(), new PlayerPreyMonsterPredator()});
    }

    @Override
    public int numInputSensors() {
        return 31;
    }

    public Tuple2D getRamOffset() {
        if (tasks[task] instanceof RammingDynamics) {
            return ((RammingDynamics) tasks[task]).getRamOffset();
        }
        return null;
    }

    public boolean monstersHaveRams() {
        return false;
    }

    public Breve2DAction playerInitialResponseToRam(Agent player, ILocated2D ram, int time) {
        return null;
    }

    public Breve2DAction playerContinuedResponseToRam(Agent player, ILocated2D ram, int time) {
        return null;
    }

    public boolean playerHasRam() {
        if (tasks[task] instanceof RammingDynamics) {
            return ((RammingDynamics) tasks[task]).playerHasRam();
        }
        return false;
    }

    public Breve2DAction monsterInitialResponseToRam(Agent monster, ILocated2D ram, int time) {
        if (tasks[task] instanceof RammingDynamics) {
            return ((RammingDynamics) tasks[task]).monsterInitialResponseToRam(monster, ram, time);
        }
        return null;
    }

    public Breve2DAction monsterContinuedResponseToRam(Agent monster, ILocated2D ram, int time) {
        if (tasks[task] instanceof RammingDynamics) {
            return ((RammingDynamics) tasks[task]).monsterContinuedResponseToRam(monster, ram, time);
        }
        return null;
    }

    /**
     * Monsters don't have rams in either task
     *
     * @return
     */
    public boolean senseMonstersHaveRams() {
        return false;
    }

    /**
     * Player has ram in one of the two tasks
     *
     * @return
     */
    public boolean sensePlayerHasRam() {
        return true;
    }
}
