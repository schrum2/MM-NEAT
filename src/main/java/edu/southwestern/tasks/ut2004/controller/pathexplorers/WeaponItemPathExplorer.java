package edu.southwestern.tasks.ut2004.controller.pathexplorers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * instructs the bot to go to the nearest weapon item
 * @author Jacob Schrum
 */
public class WeaponItemPathExplorer extends SequentialPathExplorer {

	/**
	 * finds the nearest weapon item for the bot to go to
	 * @return returns nav point with nearest weapon item
	 */
	@Override
	public NavPoint getNextNavPoint(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		Map<UnrealId, Item> items = bot.getItems().getAllItems(Category.WEAPON);
		Collection<Item> weapons = items.values();
		Iterator<Item> itr = weapons.iterator();

		Item closestWeapon = null;
		double closestDistance = Double.POSITIVE_INFINITY;
		// Get closest weapon
		while (itr.hasNext()) {
			Item weaponItem = itr.next();
			// Only consider the item if bot does not have it
			if (!bot.getWeaponry().hasWeapon(weaponItem.getType())) {
				// Skip item if there are any weird problems with null values
				if(weaponItem.getLocation() == null) continue;
				double distance = bot.getInfo().getLocation().getDistance(weaponItem.getLocation());
				if(distance < closestDistance) {
					closestWeapon = weaponItem;
					closestDistance = distance;
				}
			}
		}
		if(closestWeapon == null) return null; // Important problem to deal with
		// Closest unpossessed weapon
		return closestWeapon.getNavPoint();
	}

	@Override
	public String getSkin() {
		return "HumanMaleA.EgyptMaleA";
	}
}
