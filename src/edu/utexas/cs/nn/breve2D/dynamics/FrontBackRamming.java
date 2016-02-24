package edu.utexas.cs.nn.breve2D.dynamics;

import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.agent.Breve2DAction;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 */
public class FrontBackRamming extends MultitaskDynamics implements RammingDynamics {

    public FrontBackRamming() {
        super(new Breve2DDynamics[]{new RammingMonsters(true), new RammingMonsters(false)});
    }

    @Override
    public int numInputSensors() {
        return 31; //41;
    }

    public Tuple2D getRamOffset() {
        return ((RammingDynamics) tasks[task]).getRamOffset();
    }

    public Breve2DAction playerInitialResponseToRam(Agent player, ILocated2D ram, int time) {
        return ((RammingDynamics) tasks[task]).playerInitialResponseToRam(player, ram, time);
    }

    public Breve2DAction playerContinuedResponseToRam(Agent player, ILocated2D ram, int time) {
        return ((RammingDynamics) tasks[task]).playerContinuedResponseToRam(player, ram, time);
    }

    /**
     * Monsters have rams in both tasks
     *
     * @return
     */
    public boolean monstersHaveRams() {
        return true;
    }

    /**
     * Player never has ram
     *
     * @return
     */
    public boolean playerHasRam() {
        return false;
    }

    public Breve2DAction monsterInitialResponseToRam(Agent monster, ILocated2D ram, int time) {
        return null;
    }

    public Breve2DAction monsterContinuedResponseToRam(Agent monster, ILocated2D ram, int time) {
        return null;
    }

    public boolean senseMonstersHaveRams() {
        return true;
    }

    public boolean sensePlayerHasRam() {
        return false;
    }
}
