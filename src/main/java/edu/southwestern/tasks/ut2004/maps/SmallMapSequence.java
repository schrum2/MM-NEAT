package edu.southwestern.tasks.ut2004.maps;

public class SmallMapSequence implements MapList {

	//define a single example that implements this interface and returns a list of maps that you 
	//find useful to evolve agents on. For example, SmallMapSequence could return a short list
	//(about 3) of several small maps.
	
	@Override
	public String[] getMapList() {
		String[] list = new String[3];
		list[0] = "DM-TrainingDay";
		list[1] = "DM-1on1-Albatross";
		list[2] = "DM-Insidious";
		return list;
	}

}
