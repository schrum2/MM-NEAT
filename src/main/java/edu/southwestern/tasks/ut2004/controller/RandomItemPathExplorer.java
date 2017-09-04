package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class RandomItemPathExplorer extends SequentialPathExplorer {

	@Override
	public NavPoint getNextNavPoint(UT2004BotModuleController bot) {
		// List of non-taboo navpoints
		List<NavPoint> navs = MyCollections.getFiltered(bot.getWorldView().getAll(NavPoint.class).values(),
				tabooNavPoints);
		// Get a random navpoint now (before list is changed) in case it is
		// needed later
		NavPoint random = null;
		if (!navs.isEmpty()) {
			random = MyCollections.getRandom(navs);
		}
		// Now restrict the list to just items
		Iterator<NavPoint> itr = navs.iterator();
		while (itr.hasNext()) {
			NavPoint np = itr.next();
			if (!np.isInvSpot() || !np.isItemSpawned()) {
				itr.remove();
			}
		}

		if (!navs.isEmpty()) {
			// Random item navpoint
			return MyCollections.getRandom(navs);
		} else if (random != null) {
			// Pick random non-taboo navpoint
			return random;
		}

		// Pick a taboo navpoint
		return MyCollections.getRandom(bot.getWorldView().getAll(NavPoint.class).values());
	}
}
