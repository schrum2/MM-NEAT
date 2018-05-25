package utopia.agentmodel;


import java.io.PrintWriter;
import utopia.agentmodel.sensormodel.SensorModel;
import utopia.agentmodel.actions.Action;
import utopia.Utils;

import java.io.Serializable;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 12:19:22 PM
 */
public abstract class Controller implements Cloneable, Serializable {

    public boolean wasStuck = false;

    protected SensorModel model;
    //protected ArrayList<StringTuple> arguments;

    public Controller() {
        Utils.controllerCount++;
    }

    public Controller(SensorModel model) {
        this();
        this.model = model;
    }

    public ActionLog getActionLog(){
        if(actionLog == null){
            actionLog = new ActionLog(this.getClass().getSimpleName());
            registerActions();
        }
        return actionLog;
    }

    private transient ActionLog actionLog = null;

    /**
     * At the end of a match, this controller can log the frequency of
     * various types of actions in a uniform way using this method.
     * Controllers that extend this class must use "takeAction()" to
     * maintain a running log of action usage.
     * @param actionLog
     */
    public void logActionChoices(PrintWriter pw) {
        getActionLog().logActionChoices(pw);
    }

    public void takeAction(String key){
        getActionLog().takeAction(key);
    }

    public String lastActionLabel() {
        return getActionLog().lastActionLabel();
    }

    public void register(String key){
        getActionLog().register(key);
    }

    public abstract Action control(AgentMemory memory);

    public abstract void reset();

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public Controller clone() {
        try {
            //we don't want to clone the model here, because it's kind of static and we can always use the same model
            return (Controller) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Object not clonable!");
            e.printStackTrace();
            return null;
        }
    }

    public void prepareSensorModel(AgentBody body) {
        if (model != null) {
            body.removeAllRaysFromAutoTrace();
            model.prepareSensors(body);
        }
    }

    public void registerActions(){
    }

    public SensorModel getSensorModel() {
        return model;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            Utils.removedControllerCount++;
        } finally {
            super.finalize();
        }
    }
}
