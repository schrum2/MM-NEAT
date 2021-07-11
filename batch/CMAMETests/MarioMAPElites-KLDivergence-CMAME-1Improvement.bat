cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariolevelskldivergence log:MarioLevelsKLDivergence-CMAME1Improvement saveTo:CMAME1Improvement marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 lambda:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesKLDivLevel1:data\\VGLC\\SuperMarioBrosNewEncoding\\overworld\\mario-8-1.txt mapElitesKLDivLevel2:data\\VGLC\\SuperMarioBrosNewEncoding\\overworld\\mario-3-1.txt klDivBinDimension:100 klDivMaxValue:0.4 numImprovementEmitters:1 numOptimizingEmitters:0