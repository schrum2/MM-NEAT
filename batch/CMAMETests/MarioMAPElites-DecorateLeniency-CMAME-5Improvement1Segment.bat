cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariolevelsdecorateleniency log:MarioLevelsDecorateLeniency-CMAME5Improvement saveTo:CMAME5Improvement marioGANLevelChunks:1 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:37 lambda:100 maxGens:20000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.binningschemes.MarioMAPElitesDecorAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 numImprovementEmitters:5 numOptimizingEmitters:0 CMAMESigma:0.5 marioStatBasedMEBinIntervals:200