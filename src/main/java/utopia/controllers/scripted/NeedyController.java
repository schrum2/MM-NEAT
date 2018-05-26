package utopia.controllers.scripted;

import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.Controller;

/**
 * A controller that expects to be notified of the bot state every frame, even
 * when not actually used for control.
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public abstract class NeedyController extends Controller {

    /**
     * Called after the bot has been initialized
     * @param memory
     */
    public abstract void onBotInitialized(AgentMemory memory);

    /**
     * Update the controller (should be run every logic() cycle
     * @param memory agent memory
     */
    public abstract void tick(AgentMemory memory);

    /**
     * Is this controller still interested in being executed?
     * @param memory agent memory
     * @return true iff the controller wants to continue something
     */
    public abstract boolean isStillInterested(AgentMemory memory);

    /**
     * Called before the bot has been shut down
     * @param memory
     */
    public abstract void onBotShutdown(AgentMemory memory);
}
