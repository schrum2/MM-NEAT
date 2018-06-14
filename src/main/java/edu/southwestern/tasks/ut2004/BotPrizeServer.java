package edu.southwestern.tasks.ut2004;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.fitness.UT2004FitnessFunction;

public class BotPrizeServer {
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {"runNumber:0", "io:false", "netio:false"});
		
		@SuppressWarnings("unchecked")
		Genotype<TWEANN>[] individuals = new Genotype[0];
		BotController[] controller = new BotController[0];
		int[] nativeBotSkills = new int[0];
		int evalMinutes = 2;
		int desiredSkill = 0;
		ArrayList<UT2004FitnessFunction<TWEANN>> fitness = new ArrayList<>();
		ArrayList<UT2004FitnessFunction<TWEANN>> others = new ArrayList<>();
		//evaluateMultipleGenotypes(Genotype<T>[] individuals, String map, UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel, 
		//UT2004WeaponManager weaponManager, BotController[] opponents, int[] nativeBotSkills, int evalMinutes, int desiredSkill, 
		//ArrayList<UT2004FitnessFunction<T>> fitness, ArrayList<UT2004FitnessFunction<T>> others)
		UT2004Task.evaluateMultipleGenotypes(individuals, "DM-Insidious", null, null, null, controller,	nativeBotSkills, evalMinutes, desiredSkill,	fitness, others);
	}
}
