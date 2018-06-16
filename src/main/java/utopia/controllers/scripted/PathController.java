package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.bots.UT2;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import mockcz.cuni.amis.pogamut.ut2004.agent.navigation.MyUTPathExecutor;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.agentmodel.Controller;

public abstract class PathController extends Controller {

    public static final double TARGET_REACHED_DISTANCE = 30;
    public TabooSet<Item> tabooItems;
    public TabooSet<NavPoint> tabooNavPoints;
    public final MyUTPathExecutor pathExecutor;
    public final PathPlanner pathPlanner;
    protected Item item;
    public boolean retraceFailed = false;

    public PathController(UT2004Bot bot, MyUTPathExecutor pathExecutor, PathPlanner pathPlanner) {
        this.pathExecutor = pathExecutor;
        this.pathPlanner = pathPlanner;
        this.item = null;
        this.tabooItems = new TabooSet<Item>(bot);
        this.tabooNavPoints = new TabooSet<NavPoint>(bot);
    }

    public static boolean wantItem(AgentMemory memory, Item i) {
        if (i.getType().getName().equals("XPickups.AdrenalinePickup") || i.getType().equals(UT2004ItemType.ADRENALINE_PACK) || i.getType().getCategory().equals(UT2004ItemType.Category.ADRENALINE)) {
            return false;
        }

        if (UT2.canJudge()) {
            if (i.getType().equals(UT2004ItemType.LINK_GUN_AMMO) || i.getType().equals(UT2004ItemType.LINK_GUN)) {
                return false;
            }
        }

        Category cat = i.getDescriptor().getItemCategory();
        if (cat.equals(UT2004ItemType.Category.AMMO)) {
            if (UT2.canJudge() && i.getDescriptor().getPickupType().equals(UT2004ItemType.LINK_GUN_AMMO)) {
                return false;
            }

            // Just say bot wants all ammo except Link Gun ammo
            return true;

//            Weapon current = memory.weaponry.getCurrentWeapon();
//            if (current != null && current.getDescriptor().getPriAmmoItemType().equals(i.getDescriptor().getPickupType()) && memory.weaponry.getAmmo(i.getType()) < current.getDescriptor().getPriMaxAmount()) {
//                return true;
//            }
//            return false;
        } else if (cat.equals(UT2004ItemType.Category.WEAPON)) {
            if (UT2.canJudge() && i.getDescriptor().getPickupType().equals(UT2004ItemType.LINK_GUN)) {
                return false;
            }
            if (i.getDescriptor().getPickupType().equals(UT2004ItemType.ONS_GRENADE_LAUNCHER)) {
                return false;
            }
            if (memory.weaponry.getCurrentWeapon() == null) {
                return true;
            }
            boolean canSeeEnemies = memory.players.canSeeEnemies();
            if (!canSeeEnemies) {
                // Weapons are generally desireable, and if threats are not present, might as well get weapon.
                return true;
            }

            ItemType current = memory.weaponry.getCurrentWeapon().getType();
            ItemType other = i.getType();
            double distance = WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE + 1;
            if (canSeeEnemies && memory.getCombatTarget() != null && memory.getCombatTarget().getLocation() != null && memory.info.getLocation() != null) {
                distance = Triple.distanceInSpace(memory.getCombatTarget().getLocation(), memory.info.getLocation());
            }
            if ((!memory.weaponry.hasWeapon(other) && UT2.weaponPreferences.betterWeapon(current, other, distance))) {
                return true;
            }
            return false;
        } else if (cat.equals(UT2004ItemType.Category.SHIELD)) {
            return true;
        } else if (cat.equals(UT2004ItemType.Category.HEALTH)) {
            if (i.getType().equals(UT2004ItemType.SUPER_HEALTH_PACK)) {
                return true;
            }

            ItemType current = memory.weaponry.getCurrentWeapon().getType();
            if (memory.info.getHealth() != null && memory.info.getHealth() >= 90
                    && (current.equals(UT2004ItemType.ASSAULT_RIFLE)
                    || current.equals(UT2004ItemType.SHIELD_GUN)
                    || current.equals(UT2004ItemType.LINK_GUN))) {
                // Good weapon is more important if health is high
                return false;
            }

            // But in general, say yes to health
            return true;
            
//            if (memory.info.getHealth() != null && memory.info.getHealth() < (2 * Constants.MINIMUM_BATTLE_HEALTH.getInt())) {
//                return true;
//            }
//            return false;
        } else if (cat.equals(UT2004ItemType.Category.ARMOR)) {
            if (i.getType().equals(UT2004ItemType.SUPER_SHIELD_PACK)) {
                return true;
            }
            
            ItemType current = memory.weaponry.getCurrentWeapon().getType();
            if (memory.info.getHealth() != null && memory.info.getHealth() >= 90
                    && (current.equals(UT2004ItemType.ASSAULT_RIFLE)
                    || current.equals(UT2004ItemType.SHIELD_GUN)
                    || current.equals(UT2004ItemType.LINK_GUN))) {
                // Good weapon is more important if health is high
                return false;
            }

            // But in general, say yes to armor
            return true;
            //return false;
        } else if (cat.equals(UT2004ItemType.Category.OTHER)) {
            if (i.getType().equals(UT2004ItemType.ADRENALINE_PACK)) {
                return false;
            }
            return true;
        }
        return true;
    }

    public Item getItem() {
        return item;
    }

    public String itemName() {
        return (item == null ? "" : item.getType().getName());
    }

    @Override
    public void reset() {
        pathExecutor.setFocus(null);
        if (item != null) {
            // No need to go to this item for a while
            tabooItems.add(item);
        }
        item = null;
        wasStuck = false;
        retraceFailed = false;
    }

    public void stop() {
        pathExecutor.stop();
        reset();
    }
}
