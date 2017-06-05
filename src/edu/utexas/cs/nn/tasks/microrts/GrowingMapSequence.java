package edu.utexas.cs.nn.tasks.microrts;

/**
 * @author quintana
 * preliminary sequence of maps for iterative evolution
 */
public class GrowingMapSequence implements MapSequence{
	
	int gensPerMap = 5;
	
	String[] maps = new String[]{
			"8x8/basesWorkers8x8.xml",
			"12x12/basesWorkers12x12.xml",
			"16x16/basesWorkers16x16.xml",
			"24x24/basesWorkers24x24.xml",
			"BWDistantResources32x32.xml",
			"BroodWar/(4)BloodBath.scmA.xml",
	};

	@Override
	public String getAppropriateMap(int generation) {
		return maps[Math.min(generation / gensPerMap, maps.length-1)];
	}
	
	

}
