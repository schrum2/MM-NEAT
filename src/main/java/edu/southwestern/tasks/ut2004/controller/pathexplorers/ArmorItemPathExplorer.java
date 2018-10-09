package edu.southwestern.tasks.ut2004.controller.pathexplorers;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * instructs the bot to go to the nearest armor item
 * @author Jacob Schrum
 */
public class ArmorItemPathExplorer extends SequentialPathExplorer {

	@Override
	/**
	 * finds the nearest armor item for the bot to go to
	 * @return returns nav point with nearest armor item
	 */
	public NavPoint getNextNavPoint(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		Item nearestArmor = bot.getItems().getNearestSpawnedItem(ItemType.Category.ARMOR);
		NavPoint np = nearestArmor.getNavPoint();
		return np;
	}

	@Override
	public String getSkin() {
		return "HumanMaleA.EgyptMaleA";
	}
}
