

cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 lodeRunnerLevelSequenceAverages:false lodeRunnerLevelSequenceIndividual:false lodeRunnerAllowsSimpleAStarPath:true lodeRunnerAllowsConnectivity:true base:loderunnerlevelsequences log:LodeRunnerLevelSequences-IncreaseDifficulty saveTo:IncreaseDifficulty LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false lodeRunnerNumOfLevelsInSequence:5 lodeRunnerAllowsLinearIncreasingSolutionLength:true lodeRunnerAllowsLinearIncreasingTSPLength:true lodeRunnerAllowsLinearIncreasingEnemyCount:true lodeRunnerAllowsLinearIncreasingTreasureCount:true lodeRunnerAllowsTSPSolutionPath:true