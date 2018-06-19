package edu.southwestern.tasks.ut2004.sensors;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

public interface AcceptsTeamDistances {
	public void giveTeamDistances(HashMap<String,Location> distances);
}
