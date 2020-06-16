package megaManMaker;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.Pair;

public abstract class MegaManLevelTask<T> extends NoisyLonerTask<T> {
	private static int numFitnessFunctions = 0; 
	private static final int numOtherScores = 8;

	MegaManLevelTask(){
		this(true);
	}
	protected MegaManLevelTask(boolean register) {
		if(register) {
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
				MMNEAT.registerFitnessFunction("simpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}

			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("numOfPositionsVisited",false); //connectivity
			MMNEAT.registerFitnessFunction("percentLadders", false);
			MMNEAT.registerFitnessFunction("percentGround", false);
			MMNEAT.registerFitnessFunction("percentRope", false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
			MMNEAT.registerFitnessFunction("numTreasures", false);
			MMNEAT.registerFitnessFunction("numEnemies", false);
		}
	}
	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTimeStamp() {
		return 0; //not used
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	public abstract ArrayList<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<ArrayList<Double>> individual);

}
