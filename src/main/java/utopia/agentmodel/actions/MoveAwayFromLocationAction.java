package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 *
 * @author nvh
 */
public class MoveAwayFromLocationAction extends Action {
    private final Location avoid;
    private final AgentMemory memory;

    @Override
    public String toString(){
        return "MoveAwayFromLocationAction:" + avoid.toString();
    }

    public MoveAwayFromLocationAction(AgentMemory memory, Location avoid) {
        this.avoid = avoid;
        this.memory = memory;
    }

    @Override
    public void execute(AgentBody body) {
        Location agentLocation = body.info.getLocation();
        if(avoid != null && agentLocation != null){
            Location v = avoid.sub(agentLocation).scale(5);
            Location target = agentLocation.sub(v);
            new MoveToLocationAction(memory,target).execute(body);
        }
    }
}
