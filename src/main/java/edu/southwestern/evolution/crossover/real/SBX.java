package edu.southwestern.evolution.crossover.real;

import edu.southwestern.evolution.crossover.ArrayCrossover;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.util.random.RandomNumbers;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.datastructures.Pair;

/**
 * Simulated Binary Crossover of an array of doubles. Based
 * on a research paper by Kalyanmoy Deb.
 * @author Jacob Schrum
 */
public class SBX extends ArrayCrossover<Double> {

	public static final double DEB_DI = 20; // 0.5; // mysterious variable from Deb's code
	private final double[] upperBounds;
	private final double[] lowerBounds;

	public SBX() {
		this(((BoundedTask) MMNEAT.task).getLowerBounds(), ((BoundedTask) MMNEAT.task).getUpperBounds());
	}

	public SBX(double[] upperBounds, double[] lowerBounds) {
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
	}

	@Override
	public Pair<Double, Double> newIndexContents(Double par1, Double par2, int index) {
		return newIndexContents(par1, par2, this.upperBounds[index], this.lowerBounds[index]);
	}

	public Pair<Double, Double> newIndexContents(Double par1, Double par2, Double upper, Double lower) {
		double rnd = RandomNumbers.randomGenerator.nextDouble();
		/* Check whether variable is selected or not */
		if (rnd <= 0.5) {
			double y1, y2, betaq;
			if (Math.abs(par1 - par2) > 0.000001) {
				y2 = Math.max(par2, par1);
				y1 = Math.min(par2, par1);

				/* Find beta value */
				double beta;
				if ((y1 - lower) > (upper - y2)) {
					beta = 1 + (2 * (upper - y2) / (y2 - y1));
				} else {
					beta = 1 + (2 * (y1 - lower) / (y2 - y1));
				}

				/* Find alpha */
				double expp = DEB_DI + 1.0;

				beta = 1.0 / beta;
				double alpha = 2.0 - Math.pow(beta, expp);

				if (alpha < 0.0) {
					System.out.println("ERRRROR:1: " + alpha + " " + par1 + " " + par2);
					System.exit(-1);
				}

				rnd = RandomNumbers.randomGenerator.nextDouble();
				if (rnd <= 1.0 / alpha) {
					alpha = alpha * rnd;
					expp = 1.0 / (DEB_DI + 1.0);
					betaq = Math.pow(alpha, expp);
				} else {
					alpha = alpha * rnd;
					alpha = 1.0 / (2.0 - alpha);
					expp = 1.0 / (DEB_DI + 1.0);
					if (alpha < 0.0) {
						System.out.printf("ERRRORRR:2: " + alpha + " " + par1 + " " + par2);
						System.exit(-1);
					}
					betaq = Math.pow(alpha, expp);
				}
			} else {
				betaq = 1.0;
				y1 = par1;
				y2 = par2;
			}
			/* Generation two children */
			return new Pair<Double, Double>(0.5 * ((y1 + y2) - betaq * (y2 - y1)),
					0.5 * ((y1 + y2) + betaq * (y2 - y1)));
		}
		return new Pair<Double, Double>(par1, par2);
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:100 randomSeed:100 minecraftXRange:3 crossover:edu.southwestern.evolution.crossover.real.SBX minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
