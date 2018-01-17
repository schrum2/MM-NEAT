cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariolevel trials:1 mu:50 maxGens:500 io:false netio:false mating:true fs:false task:edu.southwestern.tasks.mario.MarioLevelTask log:MarioLevel-AStar saveTo:AStar allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:false cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 watch:true
