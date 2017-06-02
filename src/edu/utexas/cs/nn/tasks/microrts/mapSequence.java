package edu.utexas.cs.nn.tasks.microrts;

public class mapSequence {

	private static final int gensPerMap = 6;
	private static final String[] maps = new String[]{
			"8x8/8x8BasesWorkers.xml",
			"12x12/12x12BasesWorkers.xml",
			"16x16/16x16BasesWorkers.xml",
			"24x24/24x24BasesWorkers.xml",
			"BWDistantResources32x32.xml",
			//jump from 32x32 to 120x120 seems stark, maybe eventually create intermediate map
			"BroodWar/(4)BloodBath.scmA.xml",
	};

	public static String getAppropriateMap(int generation){
		return maps[Math.min(generation / gensPerMap, maps.length)];

	}
}
