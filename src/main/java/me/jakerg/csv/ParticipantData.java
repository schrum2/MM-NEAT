package me.jakerg.csv;

import java.io.File;

import edu.southwestern.parameters.Parameters;

public class ParticipantData {
	@CSVField
	public int participantID = Parameters.parameters.integerParameter("randomSeed");
	
	@CSVField
	public int actionsPerformed = 0;
	
	@CSVField
	public int keysCollected = 0;
	
	@CSVField
	public int enemiesKilled = 0;
	
	@CSVField
	public int damageReceived = 0;
	
	@CSVField
	public int heartsCollected = 0;
	
	@CSVField
	public int bombsCollected = 0;
	
	@CSVField
	public int bombsUsed = 0;
	
	@CSVField
	public int deaths = 0;
	
	@CSVField
	public int distinctRoomsVisited = 0;
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {"randomSeed:1111111"});
		ParticipantData pd = new ParticipantData();
		pd.deaths = 5;
		SimpleCSV<ParticipantData> csv = new SimpleCSV<>(pd);
		try {
			csv.saveToCSV(true, new File("data/test.csv"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
