package com.mycompany.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

public class AimData
{
	private UT2004BotModuleController ctrl;
	
	private Location aim;
	
	public AimData(UT2004BotModuleController c)
	{
		ctrl = c;
		aim = new Location(200, 0.0, 0.0);
	}
	
	public void setFocus(Location l)
	{
		Location newAim = l.getNormalized();
		if (newAim.getLength() > 0.0) aim = newAim;
	}
	
	public Location getFocus() //relative
	{
		return (aim.scale(ctrl.getInfo().getBaseSpeed()*10));
	}
	
	public Location getFocusLocation() //absolute
	{
		return (ctrl.getInfo().getLocation().add(aim.scale(ctrl.getInfo().getBaseSpeed()*10)));
	}
}
