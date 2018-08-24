package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Tells the bot to avoid a location and move away from it
 * @author nvh
 */
public class MoveAwayFromLocationAction extends Action {
    private final Location avoid;
    private final AgentMemory memory;

    @Override
    /**
     * allows the bot to print out a description of it's actions
     */
    public String toString(){
        return "MoveAwayFromLocationAction:" + avoid.toString();
    }

    /**
     * Initializes the action with the memory, and location to avoid
     * @param memory (agent memory to use)
     * @param avoid (a location that the bot is told to avoid)
     */
    public MoveAwayFromLocationAction(AgentMemory memory, Location avoid) {
        this.avoid = avoid;
        this.memory = memory;
    }

    @Override
    /**
     * tells the bot to execute the action
     */
    public void execute(AgentBody body) {
        Location agentLocation = body.info.getLocation();
        if(avoid != null && agentLocation != null){
            Location v = avoid.sub(agentLocation).scale(5);
            Location target = agentLocation.sub(v);
            new MoveToLocationAction(memory,target).execute(body);
        }
    }
}
