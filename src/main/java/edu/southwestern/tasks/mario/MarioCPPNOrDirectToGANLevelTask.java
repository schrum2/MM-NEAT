package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import ch.idsia.tools.EvaluationInfo;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

@SuppressWarnings("rawtypes")
public class MarioCPPNOrDirectToGANLevelTask extends MarioLevelTask {

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList getMarioLevelListRepresentationFromGenotype(Genotype individual) {
		CPPNOrDirectToGANGenotype m = (CPPNOrDirectToGANGenotype) individual;
		if(m.getFirstForm()) {
			return MarioCPPNtoGANLevelTask.getMarioLevelListRepresentationFromStaticGenotype((Network) m.getPhenotype());
		}else {
			return MarioGANLevelTask.getMarioLevelListRepresentationFromStaticGenotype((ArrayList<Double>) m.getPhenotype());
		}
	}	
	@Override
	public double totalPassableDistance(EvaluationInfo info) {
		// TODO Auto-generated method stub
		return MarioGANLevelTask.BUFFER_LENGTH + Parameters.parameters.integerParameter("marioGANLevelChunks")*MarioGANLevelTask.BASE_LEVEL_LENGTH;
	}
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException{
		
		
		MMNEAT.main("runNumber:0 randomSeed:0 base:mariocppntogan log:MarioCPPNtoGAN-Test saveTo:Test marioGANLevelChunks:6 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 printFitness:true trials:1 mu:10 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNOrDirectToGANLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioLevelMatchFitness:true".split(" "));

	}
}
