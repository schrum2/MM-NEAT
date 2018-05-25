package com.mycompany.mirrorbot4;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutorHelper;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathNavigator;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutionEstimator;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.BasePathExecutor;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;

public class MyPathExecutor<PATH_ELEMENT extends ILocated> extends UT2004PathExecutor<PATH_ELEMENT> implements IUnrealPathExecutor<PATH_ELEMENT>, IUT2004PathExecutorHelper<PATH_ELEMENT>
{
	public MyPathExecutor(UT2004Bot bot) {
		super(bot);
	}
	
	public MyPathExecutor(UT2004Bot bot, IUT2004PathNavigator<PATH_ELEMENT> navigator) {
		super(bot, navigator);
	}
	
	public MyPathExecutor(UT2004Bot bot, IUT2004PathNavigator<PATH_ELEMENT> navigator, Logger log) {
		super(bot, navigator, log);
	}

	@Override
	protected void stopImpl() {
		//((BasePathExecutor)this).stopImpl();
		//bot.getAct().act(new Stop());
	}
}
