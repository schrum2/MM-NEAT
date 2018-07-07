package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;

/**
 * Tells the bot to move to one target then the other
 * @author Jacob Schrum
 */
public class MoveAlongAction extends Action {
    private final ILocated target1;
    private final ILocated target2;
    private ILocated focus;
    private final boolean jump;

    @Override
    /**
     * allows the bot to print out a description of its actions
     */
    public String toString(){
        return "MoveAlong:" + (jump?"JUMP":"GROUND") + ":" + target1.getLocation().toString() + ":" + target2.getLocation().toString();
    }

    /**
     * Initializes the action with both targets, bot assumes there is no focus, and that it should not jump
     * @param target1 (first target)
     * @param target2 (second target)
     */
    public MoveAlongAction(ILocated target1, ILocated target2) {
        this(target1, target2, null, false);
    }

    /**
     * Initializes the action with both targets, and whether the bot should jump, it assumes there is no focus
     * @param target1 (first target)
     * @param target2 (second target)
     * @param jump (should the bot jump)
     */
    public MoveAlongAction(ILocated target1, ILocated target2, boolean jump) {
        this(target1, target2, null, jump);
    }

    /**
     * Initializes the action with both targets, the focus location, and whether the bot should jump
     * @param target1 (first target)
     * @param target2 (second target)
     * @param focus (location for the bot to focus on)
     * @param jump (should the bot jump)
     */
    public MoveAlongAction(ILocated target1, ILocated target2, ILocated focus, boolean jump) {
        this.target1 = target1;
        this.target2 = target2;
        this.focus = focus;
        this.jump = jump;
    }

    /**
     * gives the location that the bot should look at
     * @param focus (location to focus on)
     */
    public void addFocus(ILocated focus){
        this.focus = focus;
    }

    @Override
    /**
     * tells the bot to execute the action
     */
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