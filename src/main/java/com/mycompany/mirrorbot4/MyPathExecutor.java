package com.mycompany.mirrorbot4;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutorHelper;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;

public class MyPathExecutor<PATH_ELEMENT extends ILocated> extends UT2004PathExecutor<PATH_ELEMENT> implements IUnrealPathExecutor<PATH_ELEMENT>, IUT2004PathExecutorHelper<PATH_ELEMENT>
{
	// J. Schrum : 5/25/18: None of these constructors provide sufficient information for Pogamut 3.7.0
//	public MyPathExecutor(UT2004Bot bot) {
//		super(bot);
//	}
//	
//	public MyPathExecutor(UT2004Bot bot, IUT2004PathNavigator<PATH_ELEMENT> navigator) {
//		super(bot, navigator);
//	}
//	
//	public MyPathExecutor(UT2004Bot bot, IUT2004PathNavigator<PATH_ELEMENT> navigator, Logger log) {
//		super(bot, navigator, log);
//	}

	// Added by J. Schrum 5/25/18
	public MyPathExecutor(UT2004Bot bot, AgentInfo info, AdvancedLocomotion move, IUT2004PathNavigator<PATH_ELEMENT> navigator, Logger log) {
		super(bot, info, move, navigator, log);
	}
	
	@Override
	protected void stopImpl() {
		//((BasePathExecutor)this).stopImpl();
		//bot.getAct().act(new Stop());
	}
}
