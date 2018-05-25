package edu.utexas.cs.nn.logs;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.GBEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AddInventoryMsg;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;

public class LogEntry {

    private double time;
    private String id;
    private String name;
    private InfoMessage message;

    public LogEntry(double time, String id, String name) {
        this.time = time;
        this.id = id;
        this.name = name;
    }

    public LogEntry(double time, String id, String name, InfoMessage message) {
        this(time, id, name);
        this.message = message;
    }

    public void setMessage(InfoMessage message) {
        this.message = message;
    }

    public double getTime() { return time; }

    public String getId() { return id; }

    public String getName() { return name; }

    public InfoMessage getMessage() { return message; }

    public String getEventType() {
        String header = message.toString().split(" \\| ")[0];
        int p1 = header.indexOf('[');
        int p2 = header.indexOf(']');
        if (p2 > p1 + 1 && p1 > 0) {
            return header.substring(p1 + 1, p2);
        } else {
            return header;
        }
    }

    public String getEventBody() {
        String body = message.toString();
        int i = body.indexOf(" | ");
        if (i > 0) {
            return body.substring(i);
        } else {
            return body;
        }
    }

    private String actor = null;

    /**
     * @return the id of the cause of this event
     */
    public String getActor() {
        if (actor != null) return actor;
        if (message instanceof BotKilled) {
            UnrealId killer = ((BotKilled)message).getKiller();
            if (killer != null) actor = killer.getStringId();
        } else if (message instanceof PlayerKilled) {
            UnrealId killer = ((PlayerKilled)message).getKiller();
            if (killer != null) actor = killer.getStringId();
        } else if (message instanceof BotDamaged) {
            UnrealId instigator = ((BotDamaged)message).getInstigator();
            if (instigator != null) actor = instigator.getStringId();
        }
        return actor;
    }

    private String target = null;

    /**
     * @return the id of the cause of this event
     */
    public String getTarget() {
        if (target != null) return target;
        if (message instanceof PlayerKilled) {
            UnrealId playerKilled = ((PlayerKilled)message).getId();
            if (playerKilled != null) target = playerKilled.getStringId();
        } else if (message instanceof ItemPickedUp) {
            UnrealId itemPickedUp = ((ItemPickedUp)message).getId();
            if (itemPickedUp != null) target = itemPickedUp.getStringId();
        } else if (message instanceof AddInventoryMsg) {
            UnrealId inventoryId = ((AddInventoryMsg)message).getId();
            if (inventoryId != null) target = inventoryId.getStringId();
        } else if (message instanceof PlayerDamaged) {
            UnrealId playerDamaged = ((PlayerDamaged)message).getId();
            if (playerDamaged != null) target = playerDamaged.getStringId();
        }
        return target;
    }

    public String getLevel() {
        return this.id.split("\\.")[0];
    }

    @Override
    public String toString() {
        return "LogEntry | " + time + " | " + id + " | " + name + " | " + getEventType() + " by " + getActor() + " on " + getTarget();
    }
}
