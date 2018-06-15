package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.flag.FlagListener;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;
import edu.southwestern.tasks.ut2004.actions.NavigateToLocationAction;

/**
 * Instructs the bot to explore the map, providing it with nav points that are dangerous or a bad idea to go to
 *
 * @author Jacob Schrum
 */
public abstract class SequentialPathExplorer implements BotController {

	/**
	 * Taboo set is working as "black-list", that is you might add some
	 * NavPoints to it for a certain time, marking them as "unavailable".
	 */
	protected TabooSet<NavPoint> tabooNavPoints;
	
	/**
	 * Current navigation point we're navigating to.
	 */
	protected NavPoint targetNavPoint;
	
	/**
	 * Path auto fixer watches for navigation failures and if some navigation
	 * link is found to be unwalkable, it removes it from underlying navigation
	 * graph.
	 *
	 * Note that UT2004 navigation graphs are some times VERY stupid or contains
	 * VERY HARD TO FOLLOW links...
	 */
	protected UT2004PathAutoFixer autoFixer;

        @SuppressWarnings("rawtypes")
		@Override
	public BotAction control(UT2004BotModuleController bot) {
		// => navigate to navpoint
		return handleNavPointNavigation(bot);
	}

	@SuppressWarnings("rawtypes")
	private BotAction handleNavPointNavigation(UT2004BotModuleController bot) {
		if (bot.getNavigation().isNavigating()) {
			// WE'RE NAVIGATING TO SOME NAVPOINT
			return new EmptyAction("[Still going to " + targetNavPoint.getId().getStringId() + "]"
					+ (targetNavPoint.isInvSpot() ? targetNavPoint.getItemClass().getName() : ""));
		}

		// NAVIGATION HAS STOPPED ...
		// => we need to choose another navpoint to navigate to
		// => possibly follow some players ...

		targetNavPoint = getNextNavPoint(bot);
		if (targetNavPoint == null) {
			return new EmptyAction("[No NavPoint target]");
		}

		return new NavigateToLocationAction(targetNavPoint);
	}

        @SuppressWarnings({ "rawtypes" })
		@Override
	public void initialize(UT2004BotModuleController bot) {
		// initialize taboo set where we store temporarily unavailable navpoints
		tabooNavPoints = new TabooSet<NavPoint>(bot.getBot());

		// auto-removes wrong navigation links between navpoints
		autoFixer = new UT2004PathAutoFixer(bot.getBot(), bot.getNavigation().getPathExecutor(),
				bot.getFwMap(), bot.getAStar(), bot.getNavBuilder());

		// IMPORTANT
		// adds a listener to the path executor for its state changes, it will
		// allow you to react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
		bot.getNavigation().getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {
			@Override
			public void flagChanged(IPathExecutorState changedValue) {
				pathExecutorStateChange(changedValue.getState());
			}
		});
	}

	/**
	 * Path executor has changed its state (note that
	 * {@link UT2004BotModuleController#getPathExecutor()} is internally used by
	 * {@link UT2004BotModuleController#getNavigation()} as well!).
	 *
	 * @param state
	 */
	@SuppressWarnings("incomplete-switch")
	protected void pathExecutorStateChange(PathExecutorState state) {
		switch (state) {
		case PATH_COMPUTATION_FAILED:
			// if path computation fails to whatever reason, just try another
			// navpoint taboo bad navpoint for 3 minutes
			tabooNavPoints.add(targetNavPoint, 180);
			break;

		case TARGET_REACHED:
			// taboo reached navpoint for 3 minutes
			tabooNavPoints.add(targetNavPoint, 60);
			break;

		case STUCK:
			// the bot has stuck! ... target nav point is unavailable currently
			tabooNavPoints.add(targetNavPoint, 60);
			break;

		case STOPPED:
			// path execution has stopped
			targetNavPoint = null;
			break;
		}
	}

	/**
	 * Randomly picks some navigation point to head to.
	 *
     * @param bot Bot that is navigating
	 * @return randomly chosen navpoint
	 */
	@SuppressWarnings("rawtypes")
	public abstract NavPoint getNextNavPoint(UT2004BotModuleController bot);

        @SuppressWarnings("rawtypes")
		@Override
	
	public void reset(UT2004BotModuleController bot) {
		bot.getNavigation().stopNavigation();
	}
}
