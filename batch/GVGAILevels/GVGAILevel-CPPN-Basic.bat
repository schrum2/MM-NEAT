cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:gvgaicppn trials:1 mu:16 maxGens:500 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.interactive.gvgai.LevelBreederTask log:GVGAICPPN-Basic saveTo:Basic allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:50 recurrency:false saveInteractiveSelections:false simplifiedInteractiveInterface:false saveAllChampions:true cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false watch:false
