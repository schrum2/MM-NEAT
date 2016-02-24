/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.utils.collections.MyCollections;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class NearestArmorBlock extends NearestItemBlock {

    @Override
    public String itemLabel() {
        return "Armor";
    }

    @Override
    protected List<Item> possibleItems(UT2004BotModuleController bot) {
        return MyCollections.getFiltered(bot.getItems().getSpawnedItems(ItemType.Category.ARMOR).values(), recentlyVisitedItems);
    }
}
