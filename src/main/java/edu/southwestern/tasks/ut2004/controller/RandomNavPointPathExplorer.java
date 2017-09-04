/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 *
 * @author Jacob Schrum
 */
public class RandomNavPointPathExplorer extends SequentialPathExplorer {

	@Override
	public NavPoint getNextNavPoint(UT2004BotModuleController bot) {
		// choose one feasible navpoint (== not belonging to tabooNavPoints)
		// randomly
		NavPoint chosen = MyCollections.getRandomFiltered(bot.getWorldView().getAll(NavPoint.class).values(),
				tabooNavPoints);

		if (chosen != null) {
			return chosen;
		}

		// ok, all navpoints have been visited probably, try to pick one at
		// random
		return MyCollections.getRandom(bot.getWorldView().getAll(NavPoint.class).values());
	}
}
