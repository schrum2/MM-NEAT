cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariocomparebins log:MarioCompareBins-KLDivergence222and62 saveTo:KLDivergence222and62 marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesKLDivLevel1:data\\VGLC\\SuperMarioBrosNewEncoding\\overworlds\\mario2-2-2.txt mapElitesKLDivLevel2:data\\VGLC\\SuperMarioBrosNewEncoding\\overworlds\\mario-6-2.txt klDivBinDimension:100 klDivMaxValue:0.3