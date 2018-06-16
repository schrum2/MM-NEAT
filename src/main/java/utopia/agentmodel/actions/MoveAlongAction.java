package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;

public class MoveAlongAction extends Action {
    private final ILocated target1;
    private final ILocated target2;
    private ILocated focus;
    private final boolean jump;

    @Override
    public String toString(){
        return "MoveAlong:" + (jump?"JUMP":"GROUND") + ":" + target1.getLocation().toString() + ":" + target2.getLocation().toString();
    }

    public MoveAlongAction(ILocated target1, ILocated target2) {
        this(target1, target2, null, false);
    }

    public MoveAlongAction(ILocated target1, ILocated target2, boolean jump) {
        this(target1, target2, null, jump);
    }

    public MoveAlongAction(ILocated target1, ILocated target2, ILocated focus, boolean jump) {
        this.target1 = target1;
        this.target2 = target2;
        this.focus = focus;
        this.jump = jump;
    }

    public void addFocus(ILocated focus){
        this.focus = focus;
    }

    @Override
    public void execute(AgentBody body) {
//        System.out.println("MoveAlongFrom:" + body.info.getLocation());
//        body.body.getLocomotion().moveAlong(target1, target2);
        Move m = new Move();
        m.setFirstLocation(target1.getLocation());
        m.setSecondLocation(target2.getLocation());
        if(focus != null){
            if(focus instanceof Player){
                m.setFocusTarget(((Player)focus).getId());
            } else {
                m.setFocusLocation(focus.getLocation());
            }
        }
        body.act.act(m);
        if(jump) {
            body.jump();
        }
    }
}