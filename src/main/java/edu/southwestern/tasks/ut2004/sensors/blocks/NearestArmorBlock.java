package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.utils.collections.MyCollections;
import java.util.List;

/**
 *	Locates the nearest armor pickup
 * @author Jacob Schrum
 */
public class NearestArmorBlock extends NearestItemBlock {

	@Override
	public String itemLabel() {
		return "Armor";
	}

	/**
	 * @param bot (bot that will use the data)
	 * @return returns the location of the nearest armor pack 
	 */
	@Override
	protected List<Item> possibleItems(UT2004BotModuleController bot) {
		return MyCollections.getFiltered(bot.getItems().getSpawnedItems(ItemType.Category.ARMOR).values(),
				recentlyVisitedItems);
	}
}
