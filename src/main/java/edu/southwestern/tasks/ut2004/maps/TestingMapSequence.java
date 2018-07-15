package edu.southwestern.tasks.ut2004.maps;

public class TestingMapSequence implements MapList {

	//define a single example that implements this interface and returns a list of maps that you 
	//find useful to evolve agents on. For example, SmallMapSequence could return a short list
	//(about 3) of several small maps.
	
	@Override
	public String[] getMapList() {
		String[] list = new String[3];
		list[0] = "DM-Phobos2";
		list[1] = "DM-BP2-Calandras";
		list[2] = "DM-Rankin";
		return list;
	}

}
