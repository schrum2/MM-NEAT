package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;

/**
 *
 * @author Jacob Schrum
 */
public interface AgentController {

    public Breve2DAction getAction(Breve2DGame game);

    public void reset();
}
