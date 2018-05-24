package edu.utexas.cs.nn.weapons;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;

public class WeaponPreferenceTable {

    public class WeaponTableEntry {

        // Values picked using educated guesses
        public static final int MAX_MELEE_RANGE = 300;
        public static final int MAX_RANGED_RANGE = 2500;
        public static final int RANGE_CLOSE = 0;
        public static final int RANGE_MEDIUM = 1;
        public static final int RANGE_FAR = 2;
        public double range;
        public ArrayList<ItemType> weapons;

        public WeaponTableEntry(double range, ArrayList<ItemType> weapons) {
            this.range = range;
            this.weapons = weapons;
        }

        public ItemType getBestAvailable(Collection<ItemType> available) {
            for (int i = 0; i < weapons.size(); i++) {
                // Return first (highest priority) weapon in list that bot has
                if (available.contains(weapons.get(i))) {
                    return weapons.get(i);
                }
            }
            return null;
        }

        public int getPriority(ItemType weapon) {
            return weapons.indexOf(weapon);
        }

        @Override
        public String toString() {
            return "{" + range + ":" + weapons + "}";
        }
    }

    public int mapRange(double distance) {
        if (distance <= WeaponTableEntry.MAX_MELEE_RANGE) {
            return WeaponTableEntry.RANGE_CLOSE;
        } else if (distance <= WeaponTableEntry.MAX_RANGED_RANGE) {
            return WeaponTableEntry.RANGE_MEDIUM;
        } else {
            return WeaponTableEntry.RANGE_FAR;
        }
    }
    public static Hashtable<Integer, WeaponTableEntry> weaponPriorities = null;

    public WeaponPreferenceTable() {
        if (weaponPriorities == null) {
            weaponPriorities = new Hashtable<Integer, WeaponTableEntry>();

            ArrayList<ItemType> closeWeapons = new ArrayList<ItemType>();
            closeWeapons.add(ItemType.FLAK_CANNON);
            closeWeapons.add(ItemType.MINIGUN);
            closeWeapons.add(ItemType.SHOCK_RIFLE);
            closeWeapons.add(ItemType.BIO_RIFLE);
            closeWeapons.add(ItemType.ROCKET_LAUNCHER);
            closeWeapons.add(ItemType.ASSAULT_RIFLE);
            closeWeapons.add(ItemType.SHIELD_GUN);
            closeWeapons.add(ItemType.SNIPER_RIFLE);
            closeWeapons.add(ItemType.LIGHTNING_GUN);
            weaponPriorities.put(WeaponTableEntry.RANGE_CLOSE, new WeaponTableEntry(WeaponTableEntry.RANGE_CLOSE, closeWeapons));
            
            ArrayList<ItemType> mediumWeapons = new ArrayList<ItemType>();
            mediumWeapons.add(ItemType.ROCKET_LAUNCHER);
            mediumWeapons.add(ItemType.FLAK_CANNON);
            mediumWeapons.add(ItemType.SHOCK_RIFLE);
            mediumWeapons.add(ItemType.MINIGUN);
            mediumWeapons.add(ItemType.SNIPER_RIFLE);
            mediumWeapons.add(ItemType.LIGHTNING_GUN);
            mediumWeapons.add(ItemType.BIO_RIFLE);
            mediumWeapons.add(ItemType.ASSAULT_RIFLE);
            mediumWeapons.add(ItemType.SHIELD_GUN);
            weaponPriorities.put(WeaponTableEntry.RANGE_MEDIUM, new WeaponTableEntry(WeaponTableEntry.RANGE_MEDIUM, mediumWeapons));

            ArrayList<ItemType> farWeapons = new ArrayList<ItemType>();
            farWeapons.add(ItemType.SNIPER_RIFLE);
            farWeapons.add(ItemType.LIGHTNING_GUN);
            farWeapons.add(ItemType.ROCKET_LAUNCHER);
            farWeapons.add(ItemType.SHOCK_RIFLE);
            farWeapons.add(ItemType.FLAK_CANNON);
            farWeapons.add(ItemType.MINIGUN);
            farWeapons.add(ItemType.ASSAULT_RIFLE);
            farWeapons.add(ItemType.BIO_RIFLE);
            farWeapons.add(ItemType.SHIELD_GUN);
            weaponPriorities.put(WeaponTableEntry.RANGE_FAR, new WeaponTableEntry(WeaponTableEntry.RANGE_FAR, farWeapons));
        }
    }

    @Override
    public String toString() {
        return weaponPriorities.toString();
    }

    /*
     * Return true if other is a better weapon than current at given distance
     */
    public boolean betterWeapon(ItemType current, ItemType other, double distance) {
        if (current == null) {
            return true;
        }
        int range = mapRange(distance);
        WeaponTableEntry list = weaponPriorities.get(range);
        int currentIndex = list.getPriority(current);
        int otherIndex = list.getPriority(other);
        // Lower index has higher priority
        boolean result = otherIndex < currentIndex;
        return result;
    }

    public boolean hasGoodWeapon(Map<ItemType, Weapon> loadedWeapons, Players players, AgentMemory memory) {
        double distance = WeaponTableEntry.MAX_RANGED_RANGE - 1;
        if (players.canSeeEnemies() && memory.getCombatTarget() != null && memory.getCombatTarget().getLocation() != null && memory.info.getLocation() != null) {
            distance = Triple.distanceInSpace(memory.getCombatTarget().getLocation(), memory.info.getLocation());
        }

        return hasGoodWeapon(loadedWeapons, distance);
    }
    public Weapon savedRec = null;

    public boolean hasGoodWeapon(Map<ItemType, Weapon> availableWeapons, double distance) {
        savedRec = recommend(availableWeapons, distance);
        for (Weapon w : availableWeapons.values()) {
            if (//!w.getType().equals(ItemType.ASSAULT_RIFLE) && 
                    !w.getType().equals(ItemType.BIO_RIFLE)
                    && !w.getType().equals(ItemType.LINK_GUN)
                    && !w.getType().equals(ItemType.SHIELD_GUN)) {
                return true;
            }
        }
        return false; //(savedRec == null ? false : true);
    }

    public Weapon recommend(Map<ItemType, Weapon> availableWeapons, double distance) {
        int range = this.mapRange(distance);
        ItemType best = weaponPriorities.get(range).getBestAvailable(availableWeapons.keySet());
        return availableWeapons.get(best);
    }
}
