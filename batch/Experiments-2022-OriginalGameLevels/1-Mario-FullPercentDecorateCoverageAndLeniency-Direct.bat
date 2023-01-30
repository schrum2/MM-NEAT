cd ..
java -jar MMNEAT.jar runNumber:%1 randomSeed:%1 base:mariopdsl log:MarioPDSL-FullDirect2GAN saveTo:FullDirect2GAN marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.RealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.binningschemes.MarioMAPElitesPercentDecorateCoverageAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 marioMinDecorationIndex:1 marioMaxDecorationIndex:5 marioMinLeniencyIndex:3 marioMaxLeniencyIndex:5 marioMinSpaceCoverageIndex:0 marioMaxSpaceCoverageIndex:8 discardFromBinOutsideRestrictedRange:false