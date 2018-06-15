package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

public class AimData {
	
	@SuppressWarnings("rawtypes")
	private UT2004BotModuleController ctrl;
	
	private Location aim;
	
	/**
	 * gives the bot the default location that it will aim
	 * @param c (bot controller)
	 */
	public AimData(@SuppressWarnings("rawtypes") UT2004BotModuleController c){
		ctrl = c;
		aim = new Location(200, 0.0, 0.0);
	}
	
	/**
	 * sets what the bot will focus on
	 * @param l (the opponent/location that the bot will go after)
	 */
	//TODO: check e-mail asking for clarification
	public void setFocus(Location l){
		Location newAim = l.getNormalized();
		if (newAim.getLength() > 0.0) aim = newAim;
	}
	
	/**
	 * @return returns data on the focus relative to the bot
	 */
	public Location getFocus(){ //relative
		return (aim.scale(ctrl.getInfo().getBaseSpeed()*10));
	}
	
	/**
	 * @return returns the data on the focus location
	 */
	public Location getFocusLocation(){ //absolute
		return (ctrl.getInfo().getLocation().add(aim.scale(ctrl.getInfo().getBaseSpeed()*10)));
	}
}
