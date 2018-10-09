package edu.southwestern.tasks.ut2004.controller.pathexplorers;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * instructs the bot to go to the nearest health item
 * @author Jacob Schrum
 */
public class HealthItemPathExplorer extends SequentialPathExplorer {

	@Override
	/**
	 * finds the nearest health item for the bot to go to
	 * @return returns nav point with nearest health item
	 */
	public NavPoint getNextNavPoint(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		Item nearestHealth = bot.getItems().getNearestSpawnedItem(ItemType.Category.HEALTH);
		NavPoint np = nearestHealth.getNavPoint();
		return np;
	}

	@Override
	public String getSkin() {
		return "HumanMaleA.EgyptMaleA";
	}
}
