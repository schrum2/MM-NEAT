cd ..
cd ..
java -ea -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:0 randomSeed:0 ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA base:mariogan log:MarioGAN-Test saveTo:Test trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false 