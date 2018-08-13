cd..
cd..
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:onelifesplit maxGens:600 mu:100 io:true netio:true mating:true task:edu.southwestern.tasks.mspacman.MsPacManTask highLevel:true infiniteEdibleTime:false imprisonedWhileEdible:false pacManLevelTimeLimit:8000 pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.mediators.po.POCheckEachDirectionMediator trials:1 log:OneLifeSplit-TwoModules saveTo:TwoModules fs:false edibleTime:200 trapped:true specificGhostEdibleThreatSplit:true specificGhostProximityOrder:false specific:false startingModes:2 perLinkMutateRate:0.05 netLinkRate:0.4 netSpliceRate:0.2 crossoverRate:0.5 partiallyObservablePacman:true pacmanPO:true rawScorePacMan:true usePillModel:true useGhostModel:true
