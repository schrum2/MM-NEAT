package edu.southwestern.evolution.crossover;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * This class accomplishes the task of crossing over two different
 * genotypes and a helper method that performs the actual swapping.
 * 
 * @author Jacob Schrum
 */
public class ArrayCrossover<T> extends Crossover<ArrayList<T>> {

	/**
	 * The method crossover is an inherited method from the abstract crossover
	 * class. It performs a standard single-point crossover by taking a random
	 * number, i, seeded with a number based on the phenotype of the toModify
	 * parameter. The values of the objects toModify and toReturn are swapped
	 * after the i value using a for loop and the helper method
	 * newIndexContents.
	 *
	 * @param toModify Reference to genotype that is modified by crossover.
	 * @param toReturn Reference to genotype that is returned unmodified.
	 * @return Returns the second genotype produced by the crossover.
	 */
	@Override
	public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> toModify, Genotype<ArrayList<T>> toReturn) {
		//System.out.println("BEFORE:"+toModify.getPhenotype() + ":"+toReturn.getPhenotype());
		
		// the random seeded number that corresponds to the single-point at
		// which the crossover occurs
		int point = RandomNumbers.randomGenerator.nextInt(toModify.getPhenotype().size());
		// the for loop that swaps the following values.
		for (int i = point; i < toModify.getPhenotype().size(); i++) {
			Pair<T, T> p = newIndexContents(toReturn.getPhenotype().get(i), toModify.getPhenotype().get(i), i);
			toReturn.getPhenotype().set(i, p.t1);
			toModify.getPhenotype().set(i, p.t2);
		}

		//System.out.println("AFTER :"+toModify.getPhenotype() + ":"+toReturn.getPhenotype());
		return toReturn;
	}

	/**
	 * The method newIndexContents works as a helper function that uses the swap
	 * method from the abstract class Crossover.
	 *
	 * @param par1 the object to swap from array 1.
	 * @param par2 the object to swap from toModify.
	 * @param index the index of each array where the par objects are located,
	 *            	used to facilitate the swap.
	 * @return returns a pair containing both values swapped.
	 */
	public Pair<T, T> newIndexContents(T par1, T par2, int index) {
		return swap(par1, par2);
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:100 randomSeed:100 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
