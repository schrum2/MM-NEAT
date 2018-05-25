/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.utils.collections.MyCollections;
import edu.southwestern.tasks.ut2004.Util;
import java.util.List;

/**
 *locates the closest item
 * @author Jacob Schrum
 */
public class NearestItemBlock implements UT2004SensorBlock {

	public static final int MAX_ITEM_DISTANCE = 5000;
	public static final int TABOO_TIME = 60;
	protected TabooSet<ILocated> recentlyVisitedItems;

	/**
	 * creates the sensor block array for the items
	 * @param bot (bot that is looking for the items)
	 */
	public void prepareBlock(UT2004BotModuleController bot) {
		this.recentlyVisitedItems = new TabooSet<ILocated>(bot.getBot());
		bot.getWorld().addEventListener(ItemPickedUp.class, new IWorldEventListener<ItemPickedUp>() {
			public void notify(ItemPickedUp add) {
				// Don't keep going to the same item (mainly an issue with
				// bWeaponStay)
				recentlyVisitedItems.add(add.getLocation(), TABOO_TIME);
			}
		});

	}

	/**
	 * Collects data on the bot's status and puts it into an array
	 * 
	 * @param bot (bot which will use the sensor data)
	 * @param in (address to start at in array)
	 * @param inputs (an array that collects the values from the statuses)
	 * @return returns next address for sensor allocation
	 */
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Item item = getItemOfInterest(bot);

		Location botLocation = bot.getInfo().getLocation();
		Location itemLocation = item == null ? null : item.getLocation();
		double distance = (botLocation == null || itemLocation == null) ? MAX_ITEM_DISTANCE
				: botLocation.getDistance(itemLocation);
		double distance2D = (botLocation == null || itemLocation == null) ? MAX_ITEM_DISTANCE
				: botLocation.getDistance2D(itemLocation);
		distance = Math.min(distance, MAX_ITEM_DISTANCE);
		distance2D = Math.min(distance2D, MAX_ITEM_DISTANCE);

		inputs[in++] = Util.scale(distance, MAX_ITEM_DISTANCE);
		inputs[in++] = Util.scale(distance2D, MAX_ITEM_DISTANCE);

		return in;
	}

	/**
	 * populates the labels array so statuses can be identified
	 * 
	 * @param in (address in the array to be labeled)
	 * @param labels (an empty array that will be populated)
	 * @return returns the next address to be labeled
	 */
	public int incorporateLabels(int in, String[] labels) {
		labels[in++] = "Nearest " + itemLabel() + " Item Proximity 3D";
		labels[in++] = "Nearest " + itemLabel() + " Item Proximity 2D";
		return in;
	}

	public String itemLabel() {
		return "Spawned";
	}

	/**
	 * @return returns the number of sensors
	 */
	public int numberOfSensors() {
		return 2;
	}

	/**
	 * @param bot (the bot that will pick up the items
	 * @return returns a list of the nearby items
	 */
	protected List<Item> possibleItems(UT2004BotModuleController bot) {
		return MyCollections.getFiltered(bot.getItems().getSpawnedItems().values(), recentlyVisitedItems);
	}

	/**
	 * tells the bot to retireve the nearest item of interest
	 * @param bot (bot that will pick up the item)
	 * @return returns null
	 */
	protected Item getItemOfInterest(UT2004BotModuleController bot) {
		List<Item> items = possibleItems(bot);
		ILocated botLocation = bot.getInfo().getLocation();
		if (botLocation != null && !items.isEmpty()) {
			Item nearest = DistanceUtils.getNearest(items, botLocation, MAX_ITEM_DISTANCE);
			return nearest;
		}
		return null;
	}
}
