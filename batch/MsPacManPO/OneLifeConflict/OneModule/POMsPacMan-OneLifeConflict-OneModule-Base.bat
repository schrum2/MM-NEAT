cd ..
cd ..
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:onelifeconflict maxGens:200 mu:10 io:true netio:true mating:true task:edu.southwestern.tasks.mspacman.MsPacManTask highLevel:true infiniteEdibleTime:false imprisonedWhileEdible:false pacManLevelTimeLimit:8000 pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.po.POCheckEachDirectionMediator trials:1 log:OneLifeConflict-POTest saveTo:POTest fs:false edibleTime:200 trapped:true perLinkMutateRate:0.05 netLinkRate:0.4 netSpliceRate:0.2 crossoverRate:0.5 partiallyObservablePacman:true rawScorePacMan:true printFitness:true ghostPO:true pacmanPO:true
