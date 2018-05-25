package utopia.agentmodel.sensormodel;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;
import mockcz.cuni.pogamut.Client.AgentMemory;

public class BestSensorModel extends BotprizeSensorModel {

    public BestSensorModel(int levelTraces, int airTraces, int groundTraces, double[] sliceLimits, int secondsHistory) {
        super(levelTraces, airTraces, groundTraces, sliceLimits, secondsHistory);
    }

    public BestSensorModel() {
        this(12, 6, 4, new double[]{0, PI / 128, PI / 32, PI / 4, PI / 2, PI}, 3);
    }

    @Override
    public double[] getMiscSensors(AgentMemory memory) {
        double[] sensors = new double[getNumMiscSensors()];
        double[] oldSensors = super.getMiscSensors(memory);
        int numOldMisc = super.getNumMiscSensors();
        System.arraycopy(oldSensors, 0, sensors, 0, numOldMisc);

        int numMisc = 0;
        // Senses
        sensors[numOldMisc + (numMisc++)] = memory.info.isHealthy() ? 1 : 0;
        sensors[numOldMisc + (numMisc++)] = memory.info.isMoving() ? 1 : 0;
        sensors[numOldMisc + (numMisc++)] = memory.info.isShooting() ? 1 : 0;
        sensors[numOldMisc + (numMisc++)] = memory.senses.isBeingDamaged() ? 1 : 0;  // redundant
        sensors[numOldMisc + (numMisc++)] = memory.senses.isBumping() ? 1 : 0;
        sensors[numOldMisc + (numMisc++)] = memory.senses.isCausingDamage() ? 1 : 0;
        sensors[numOldMisc + (numMisc++)] = memory.senses.isFallEdge() ? 1 : 0;

        // Weapon details: these values seem to be very dubious
        Weapon current = memory.weaponry.getCurrentWeapon();
        if (current != null && current.getDescriptor() != null) {
            WeaponDescriptor weapon = current.getDescriptor();
            sensors[numOldMisc + (numMisc++)] = weapon.getPriDamage() / 100.0;
            sensors[numOldMisc + (numMisc++)] = weapon.getSecDamage() / 100.0;
            sensors[numOldMisc + (numMisc++)] = weapon.isSniping() ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = weapon.isPriSplashDamage() || weapon.isSecSplashDamage() ? 1 : 0;
        }

        Player p = memory.getCombatTarget();
        if (p != null) {
            sensors[numOldMisc + (numMisc++)] = p.getFiring();
            // FIXME: isReachable -> isVisible
            sensors[numOldMisc + (numMisc++)] = p.isVisible() ? 1 : 0;
        }

        Item nearest = memory.info.getNearestItem();
        if (nearest != null) {
            // FIXME: isReachable -> isVisible
            sensors[numOldMisc + (numMisc++)] = 0; //nearest.isReachable() ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = nearest.isVisible() ? 1 : 0;

            ItemType type = nearest.getType();
            sensors[numOldMisc + (numMisc++)] = type.getCategory().equals(UT2004ItemType.Category.HEALTH) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = type.getCategory().equals(UT2004ItemType.Category.ARMOR) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = type.getCategory().equals(UT2004ItemType.Category.SHIELD) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = type.getCategory().equals(UT2004ItemType.Category.WEAPON) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = type.equals(UT2004ItemType.U_DAMAGE_PACK) ? 1 : 0;
        }

        if (current != null && current.getDescriptor() != null) {
            WeaponDescriptor weapon = current.getDescriptor();
            sensors[numOldMisc + (numMisc++)] = weapon.getPriFireRate();
            sensors[numOldMisc + (numMisc++)] = weapon.getPriBotRefireRate();
            sensors[numOldMisc + (numMisc++)] = weapon.getSecFireRate();
            sensors[numOldMisc + (numMisc++)] = weapon.getSecBotRefireRate();
        }

        return sensors;
    }

    @Override
    public int getNumMiscSensors() {
        return super.getNumMiscSensors() +
                7 + // senses
                4 + // weapon info
                2 + // enemy info
                7 + // nearest item info
                4; // extra weapon info
    }
}
