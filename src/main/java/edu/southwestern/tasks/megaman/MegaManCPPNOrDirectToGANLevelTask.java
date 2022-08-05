package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManOneGANGenerator;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManSevenGANGenerator;
import edu.southwestern.util.datastructures.ArrayUtil;

@SuppressWarnings("rawtypes")
public class MegaManCPPNOrDirectToGANLevelTask extends MegaManLevelTask implements BoundedTask{

	public MegaManCPPNOrDirectToGANLevelTask(){
		super();
		MegaManGANLevelTask.resetStaticSettings();
		if(Parameters.parameters.booleanParameter("useMultipleGANsMegaMan")) MegaManGANUtil.setMegaManGANGenerator(new MegaManSevenGANGenerator());
		else MegaManGANUtil.setMegaManGANGenerator(new MegaManOneGANGenerator());
	}

	
	@Override
	public List getMegaManLevelListRepresentationFromGenotype(Genotype individual,
			MegaManTrackSegmentType segmentTypeTracker) {
		CPPNOrDirectToGANGenotype m = (CPPNOrDirectToGANGenotype) individual;

		if(m.getFirstForm()) {
			return MegaManGANUtil.cppnToMegaManLevel(MegaManGANUtil.getMegaManGANGenerator(),(Network) individual.getPhenotype(), Parameters.parameters.integerParameter("megaManGANLevelChunks"), ArrayUtil.doubleOnes(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length), segmentTypeTracker);
			//return MegaManCPPNtoGANLevelTask.getMegaManLevelListRepresentationFromGenotype(individual,segmentTypeTracker);
		}else {
			@SuppressWarnings("unchecked")
			List<Double> latentVector = (List<Double>) individual.getPhenotype();
			return MegaManGANLevelTask.getMegaManLevelListRepresentationFromStaticGenotype(MegaManGANUtil.getMegaManGANGenerator(), latentVector, Parameters.parameters.integerParameter("megaManGANLevelChunks"), segmentTypeTracker);
			//return MegaManGANLevelTask.getMegaManLevelListRepresentationFromGenotype((ArrayList<Double>)m.getPhenotype(),segmentTypeTracker);
		}
	}
	
	@Override
	public double[] getUpperBounds() {
		return MegaManGANLevelTask.getStaticUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return MegaManGANLevelTask.getStaticLowerBounds();
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:1 randomSeed:1 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamansinglegan log:MegaManSingleGAN-MegaManCPPNThenDirect2GAN saveTo:MegaManCPPNThenDirect2GAN megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManCPPNOrDirectToGANLevelTask cleanOldNetworks:false useMultipleGANsMegaMan:false allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype indirectToDirectTransitionRate:0.1".split(" "));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


//Comparisons: 
//MarioCPPNOrDirectToGANLevelTask
//MMNEAT.main("runNumber:0 randomSeed:0 base:mariocppntogan log:MarioCPPNtoGAN-Test saveTo:Test marioGANLevelChunks:6 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 printFitness:true trials:1 mu:10 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNOrDirectToGANLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioLevelMatchFitness:true".split(" "));

//MarioGANLevelTask
//MMNEAT.main("runNumber:0 randomSeed:0 marioGANLevelChunks:6 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 printFitness:true trials:1 mu:10 maxGens:500 io:false netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:false cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioLevelMatchFitness:true".split(" "));
