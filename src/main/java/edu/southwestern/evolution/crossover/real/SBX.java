package edu.southwestern.evolution.crossover.real;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.crossover.MultipointCrossover;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Simulated Binary Crossover of an array of doubles. Based
 * on a research paper by Kalyanmoy Deb. However, my original attempt
 * at implementing had problems, so I borrowed code from here:
 * http://www.java2s.com/example/java-src/pkg/es/udc/gii/common/eaf/algorithm/operator/reproduction/crossover/sbxcrossover-985d5.html
 * 
 * @author Jacob Schrum
 */
public class SBX extends MultipointCrossover<Double> {

	// Other sources refer to this value as ETA
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

	/**
	 * The code in MultipointCrossover will decide whether or not a given index is chosen for
	 * crossover. Once the index has been chosen, the operation is carried out here.
	 * 
	 * @param par1 A value from a given index in one parent
	 * @param par2 A value from the same index in the other parent
	 * @param upper Max allowable value for any gene at this index
	 * @param lower Min allowable value for any gene at this index
	 * @return Pair containing the new gene values for parent1 and parent2 at the index
	 */
	public Pair<Double, Double> newIndexContents(double par1, double par2, double upper, double lower) {
		double yl = lower;
		double yu = upper;
		double y1, y2, betaq;
		if (Math.abs(par1 - par2) > 0.000001) {
			y2 = Math.max(par2, par1);
			y1 = Math.min(par2, par1);

			// Original code (source?)
			/* Find beta value */
			//				double beta;
			//				if ((y1 - lower) > (upper - y2)) {
			//					beta = 1 + (2 * (upper - y2) / (y2 - y1));
			//				} else {
			//					beta = 1 + (2 * (y1 - lower) / (y2 - y1));
			//				}
			//
			//				/* Find alpha */
			//				double expp = DEB_DI + 1.0;
			//
			//				beta = 1.0 / beta;
			//				double alpha = 2.0 - Math.pow(beta, expp);

			// Replacement code: http://www.java2s.com/example/java-src/pkg/es/udc/gii/common/eaf/algorithm/operator/reproduction/crossover/sbxcrossover-985d5.html
			double crossOverIndex = DEB_DI; // Don't understand this name
			double beta = 1.0 + (2.0 * (y1 - yl) / (y2 - y1));
			double alpha = 2.0 - Math.pow(beta, -(crossOverIndex + 1.0));

			// Original code, but I'm not sure why this is an error condition
			if (alpha < 0.0) {
				System.out.println("ERRRROR:1: " + alpha + " " + par1 + " " + par2);
				throw new IllegalStateException("Why is this an error?: ERRRROR:1: " + alpha + " " + par1 + " " + par2);
			}

			double rand = RandomNumbers.randomGenerator.nextDouble();
			// Original code: why ERRRORRR:2?
			//				if (rnd <= 1.0 / alpha) {
			//					alpha = alpha * rnd;
			//					expp = 1.0 / (DEB_DI + 1.0);
			//					betaq = Math.pow(alpha, expp);
			//				} else {
			//					alpha = alpha * rnd;
			//					alpha = 1.0 / (2.0 - alpha);
			//					expp = 1.0 / (DEB_DI + 1.0);
			//					if (alpha < 0.0) {
			//						System.out.printf("ERRRORRR:2: " + alpha + " " + par1 + " " + par2);
			//						System.exit(-1);
			//					}
			//					betaq = Math.pow(alpha, expp);
			//				}

			// Replacement code: http://www.java2s.com/example/java-src/pkg/es/udc/gii/common/eaf/algorithm/operator/reproduction/crossover/sbxcrossover-985d5.html
			if (rand <= (1.0 / alpha)) {
				betaq = Math.pow((rand * alpha), (1.0 / (crossOverIndex + 1.0)));
			} else {
				betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossOverIndex + 1.0)));
			}

			double c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
			beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
			alpha = 2.0 - Math.pow(beta, -(crossOverIndex + 1.0));
			if (rand <= (1.0 / alpha)) {
				betaq = Math.pow((rand * alpha), (1.0 / (crossOverIndex + 1.0)));
			} else {
				betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossOverIndex + 1.0)));
			}

			double c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
			if (c1 < yl) {
				c1 = yl;
			}
			if (c2 < yl) {
				c1 = yl;
			}
			if (c1 > yu) {
				c1 = yu;
			}
			if (c2 > yu) {
				c2 = yu;
			}

			// My code to adapt the source to my framework
			rand = RandomNumbers.randomGenerator.nextDouble();
			// I don't bother bounding the values here because mutation happens after crossover,
			// and the values get bound after mutation.
			if(rand <= 0.5) {
				return new Pair<Double, Double>(c2,c1);                	
			} else {
				return new Pair<Double, Double>(c1,c2);                	
			}
		} 

		// Original code: why an else case?
		//			else {
		//				betaq = 1.0;
		//				y1 = par1;
		//				y2 = par2;
		//			}
		/* Generation two children */
		// Original code (similar calculations above)
		//			return new Pair<Double, Double>(0.5 * ((y1 + y2) - betaq * (y2 - y1)),
		//					0.5 * ((y1 + y2) + betaq * (y2 - y1)));
		
		// Don't change, since values are not sufficiently different in the first place
		return new Pair<Double, Double>(par1, par2);
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:100 randomSeed:100 minecraftXRange:3 arrayCrossover:edu.southwestern.evolution.crossover.real.SBX minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
