cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-CPPNThenDirect2GAN saveTo:CPPNThenDirect2GAN marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNOrDirectToGANLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment indirectToDirectTransitionRate:0.1 mapElitesBinLabels:edu.southwestern.tasks.mario.binningschemes.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 includeCosineFunction:true includeIdFunction:true