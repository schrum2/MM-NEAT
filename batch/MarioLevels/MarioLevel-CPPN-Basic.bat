cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariocppn trials:1 mu:50 maxGens:500 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNLevelTask log:MarioCPPN-Basic saveTo:Basic allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:true cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false
