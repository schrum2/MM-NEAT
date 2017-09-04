package edu.southwestern.breve2D.dynamics;

import edu.southwestern.breve2D.agent.Agent;
import edu.southwestern.breve2D.agent.Breve2DAction;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 */
public interface RammingDynamics {

	public Tuple2D getRamOffset();

	public boolean senseMonstersHaveRams();

	public boolean monstersHaveRams();

	public Breve2DAction playerInitialResponseToRam(Agent player, ILocated2D ram, int time);

	public Breve2DAction playerContinuedResponseToRam(Agent player, ILocated2D ram, int time);

	public boolean playerHasRam();

	public boolean sensePlayerHasRam();

	public Breve2DAction monsterInitialResponseToRam(Agent monster, ILocated2D ram, int time);

	public Breve2DAction monsterContinuedResponseToRam(Agent monster, ILocated2D ram, int time);
}
