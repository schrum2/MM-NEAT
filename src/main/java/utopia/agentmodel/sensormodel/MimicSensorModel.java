package utopia.agentmodel.sensormodel;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;
import mockcz.cuni.pogamut.Client.AgentMemory;
import java.util.Map;

/**
 * Sensor model that adds support for interpreting whether the opponent is performing
 * a basic movement action that the bot can perform.
 *
 * @author Jacob Schrum
 */
@SuppressWarnings("serial")
public class MimicSensorModel extends BestSensorModel {

    public MimicSensorModel(int levelTraces, int airTraces, int groundTraces, double[] sliceLimits, int secondsHistory) {
        super(levelTraces, airTraces, groundTraces, sliceLimits, secondsHistory);
    }

    public MimicSensorModel() {
        this(12, 6, 4, new double[]{0, PI / 128, PI / 32, PI / 4, PI / 2, PI}, 3);
    }

    @Override
    public double[] getMiscSensors(AgentMemory memory) {
        double[] sensors = new double[getNumMiscSensors()];
        double[] oldSensors = super.getMiscSensors(memory);
        int numOldMisc = super.getNumMiscSensors();
        System.arraycopy(oldSensors, 0, sensors, 0, numOldMisc);

        int numMisc = 0;

        sensors[numOldMisc + (numMisc++)] = memory.inWater() ? 1 : 0;

        // Mimicry sensors
        Player p = memory.getCombatTarget();
        if (p != null) {
            sensors[numOldMisc + (numMisc++)] = memory.isAdvancing(p) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = memory.isRetreating(p) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = memory.isStrafing(p, true) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = memory.isStrafing(p, false) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = AgentMemory.isStill(p) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = AgentMemory.isJumping(p) ? 1 : 0;
            sensors[numOldMisc + (numMisc++)] = p.getLocation() != null && memory.isAboveMe(p.getLocation()) ? 1 : 0;

            // Opponent Weapon Info
            String weapon = p.getWeapon() + "Pickup";
            ItemType weaponType = UT2004ItemType.getItemType(weapon);
            //System.out.println("           WeaponType: " +weaponType);
            if (weaponType != null) {
                Map<UnrealId, Item> examples = memory.items.getAllItems(weaponType);
                WeaponDescriptor descriptor = null;
                if (!examples.isEmpty()) {
                    //System.out.println("               Examples:"+examples);
                    Item[] items = new Item[0];
                    items = examples.values().toArray(items);
                    Item specificWeapon = items[0];
                    descriptor = (WeaponDescriptor) specificWeapon.getDescriptor();
                } else {
                    //System.out.println("               Have Weapon? " + memory.weaponry.getWeapons());
                    descriptor = memory.weaponry.getWeaponDescriptor(weaponType);
                }
                if (descriptor != null) {
                    //System.out.println("           WeaponDesc: " +descriptor);
                    sensors[numOldMisc + (numMisc++)] = descriptor.getPriDamage() / 100.0;
                    sensors[numOldMisc + (numMisc++)] = descriptor.getSecDamage() / 100.0;
                    sensors[numOldMisc + (numMisc++)] = descriptor.isSniping() ? 1 : 0;
                    sensors[numOldMisc + (numMisc++)] = descriptor.isPriSplashDamage() || descriptor.isSecSplashDamage() ? 1 : 0;
                    sensors[numOldMisc + (numMisc++)] = descriptor.getPriFireRate();
                    sensors[numOldMisc + (numMisc++)] = descriptor.getPriBotRefireRate();
                    sensors[numOldMisc + (numMisc++)] = descriptor.getSecFireRate();
                    sensors[numOldMisc + (numMisc++)] = descriptor.getSecBotRefireRate();
                }

            }
        }

        return sensors;
    }

    @Override
    public int getNumMiscSensors() {
        return super.getNumMiscSensors()
                + 1  // In water
                + 7  // Mimicry sensors
                + 8; // Enemy weapon sensors
    }
}
