cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:imprisonconflict maxGens:200 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.mspacman.MsPacManTask highLevel:true infiniteEdibleTime:false imprisonedWhileEdible:true pacManLevelTimeLimit:8000 pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator trials:10 log:ImprisonConflict-TwoModules saveTo:TwoModules fs:false edibleTime:200 startingModes:2 trapped:true perLinkMutateRate:0.05 netLinkRate:0.4 netSpliceRate:0.2 crossoverRate:0.5
