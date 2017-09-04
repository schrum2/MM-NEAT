package edu.southwestern.tasks.microrts.iterativeevolution;

/**
 * Map seqeucne that switches very fast (every generation) for testing
 *
 * @author quintana
 *
 */
public class FastMapSequence implements MapSequence{

	int gensPerMap = 1;
	
	String[] maps = new String[]{
			"8x8/basesWorkers8x8.xml",
			"12x12/basesWorkers12x12.xml",
			"16x16/basesWorkers16x16.xml",
			"24x24/basesWorkers24x24.xml",
			"BWDistantResources32x32.xml",
	};
	
	@Override
	public String getAppropriateMap(int generation) {
		return maps[Math.min(generation / gensPerMap, maps.length-1)];
	}

}
