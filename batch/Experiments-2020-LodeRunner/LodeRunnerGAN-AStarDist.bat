

cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:loderunnerlevels log:LodeRunnerLevels-AStarDist saveTo:AStarDist LodeRunnerGANModel:LodeRunnerEpochAllGroundFirstHundred100000_20_7.pth watch:false GANInputSize:20 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true, cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 lodeRunnerAllowsSimpleAStarPath:true lodeRunnerAllowsConnectivity:false}