package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 * Tells the bot to randomly explore the map
 * @author Jacob Schrum
 */
public class RandomNavPointPathExplorer extends SequentialPathExplorer {

	@Override
	/**
	 * choses a random nav point from the ones already explored
	 * @return returns the next nav point for the bot to go to
	 */
	public NavPoint getNextNavPoint(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		// choose one feasible navpoint (== not belonging to tabooNavPoints)
		// randomly
		NavPoint chosen = MyCollections.getRandomFiltered(bot.getWorldView().getAll(NavPoint.class).values(),
				tabooNavPoints);

		if (chosen != null) {//if there is a feasible nav point
			return chosen;
		}

		// ok, all navpoints have been visited probably, try to pick one at
		// random
		return MyCollections.getRandom(bot.getWorldView().getAll(NavPoint.class).values());
	}
}
